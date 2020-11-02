package com.argame.model;

import android.util.Log;

import androidx.annotation.NonNull;

import com.argame.model.data_structures.friends_data.Friends;
import com.argame.model.data_structures.friends_data.IFriends;
import com.argame.model.data_structures.tic_tac_toe_game.TicTacToeGame;
import com.argame.model.data_structures.user_data.User;
import com.argame.model.data_structures.user_data.IUser;
import com.argame.model.data_structures.users_current_game.UsersCurrentGame;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.functions.FirebaseFunctions;
import com.google.firebase.functions.HttpsCallableResult;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class Database {

    static private Database INSTANCE;

    private boolean hasBeenInitialized = false;

    // Realtime database locations
    private static final String USERS_CONNECTION_STATUS = "users_connection_status";

    // Firestore locations
    private static final String COLLECTION_USER_DATA = "users_data";
    private static final String COLLECTION_USER_FRIENDS = "users_friends";
    private static final String COLLECTION_TIC_TAC_TOE_GAMES =  "tic_tac_toe_games";
    private static final String COLLECTION_USERS_CURRENT_GAME = "users_current_game";

    // Application data
    private User userData = new User();
    private Friends userFriends = new Friends();

    // Saved listeners
    private Map<String, ListenerRegistration> friendsUpdateListeners = new HashMap<>();

    private Database(){}

    synchronized static public Database getInstance(){
        if (INSTANCE == null){
            INSTANCE = new Database();
        }

        return INSTANCE;
    }

    synchronized public void retrieveUserData() {

        // Control if database has been already initialized
        if(this.hasBeenInitialized)
            return;

        // Retrieve data only if user is logged
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        if(firebaseUser == null) {
            return;
        }

        // Set up real time database
        FirebaseDatabase.getInstance().setPersistenceEnabled(true);
        DatabaseReference onlineStatusReference = FirebaseDatabase.getInstance().getReference(USERS_CONNECTION_STATUS).child(firebaseUser.getUid());
        onlineStatusReference.setValue(1);
        onlineStatusReference.onDisconnect().setValue(0);

        // Detect when user connection status changed
        DatabaseReference connectedRef = FirebaseDatabase.getInstance().getReference(".info/connected");
        connectedRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                boolean connected = snapshot.getValue(Boolean.class);
                if (connected) {
                    onlineStatusReference.setValue(1);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });

        // Set up firestore
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();

        // Configure persistence
        FirebaseFirestoreSettings settings = new FirebaseFirestoreSettings.Builder()
                .setPersistenceEnabled(true)
                .build();
        firestore.setFirestoreSettings(settings);

        // Get user profile data
        firestore.collection(COLLECTION_USER_DATA).document(firebaseUser.getUid()).addSnapshotListener((snapshotUserData, exceptionUserData) -> {
            if (exceptionUserData != null) {
                Log.w("debugg", "Read data user failed", exceptionUserData);
                return;
            }

            if (snapshotUserData != null && snapshotUserData.exists() && snapshotUserData.getData() != null) {
                this.userData.updateData(snapshotUserData.getData()).notifyListeners();
            } else {
                Log.d("debugg", "Current data: null");
            }
        });

        // Get user friends
        firestore.collection(COLLECTION_USER_FRIENDS).document(firebaseUser.getUid()).addSnapshotListener((snapshotFriends, exception) -> {
            if (exception != null) {
                Log.w("debugg", "Read user's friends failed", exception);
                return;
            }

            if (snapshotFriends != null && snapshotFriends.exists() && snapshotFriends.getData() != null) {
                Set<String> updatedFriendsIDs = new HashSet<>((List<String>) snapshotFriends.getData().get(Friends.FRIENDS_FIELD));
                synchronized (this.userFriends) {

                    // REMOVE DELETED FRIENDS
                    Set<String> deletedFriendsIDs = this.userFriends.getDeletedFriendsIDs(updatedFriendsIDs);

                    // Remove listeners that notify deleted friends changes
                    for(String deletedFriendID: deletedFriendsIDs)
                        this.friendsUpdateListeners.get(deletedFriendID).remove();
                    this.friendsUpdateListeners.keySet().removeAll(deletedFriendsIDs);
                    this.userFriends.removeFriendsUsingIDs(deletedFriendsIDs);

                    // Add new friends
                    Set<String> newFriendsIDs = this.userFriends.getNewFriends(updatedFriendsIDs);
                    for(String newFriendID: newFriendsIDs) {

                        // Create new friend instance
                        User newFriend = new User().setUid(newFriendID);

                        // Create new friend listeners which notifies changes
                        ListenerRegistration listener = firestore.collection(COLLECTION_USER_DATA).document(newFriendID)
                                .addSnapshotListener((snapshotFriend, exceptionFriend) -> {
                                    if (exceptionFriend != null) {
                                        Log.w("debugg", "Read data user failed", exceptionFriend);
                                        return;
                                    }

                                    if (snapshotFriend != null && snapshotFriend.exists() && snapshotFriend.getData() != null) {
                                        newFriend.updateData(snapshotFriend.getData()).notifyListeners();
                                    } else {
                                        Log.d("debugg", "Current data: null");
                                    }
                                });
                        this.friendsUpdateListeners.put(newFriendID, listener);
                        this.userFriends.addNewFriend(newFriendID, newFriend);
                    }
                    // Notify listeners that friends list is changed
                    this.userFriends.notifyListeners();
                }
            } else {
                Log.d("debugg", "Current data: null");
            }
        });

        this.hasBeenInitialized = true;
    }

    public void updateUserData(String name, String surname, String nickname) {
        Map<String, Object> fields = new HashMap<>(3);
        fields.put(User.NAME_FIELD, name);
        fields.put(User.SURNAME_FIELD, surname);
        fields.put(User.NICKNAME_FIELD, nickname);

        FirebaseFirestore.getInstance().collection(COLLECTION_USER_DATA).document(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .update(fields)
                .addOnCompleteListener(task -> {
                })
                .addOnFailureListener(exception -> {
                    Log.w("debugg", "Update user data filed", exception);
                });
    }

    public void createTicTacToeGame(String opponentID) {

        // Prepare game data
        Map<String, Object> gameData = TicTacToeGame.getInitialFieldMap();
        gameData.put(TicTacToeGame.OWNER_ID_FIELD, FirebaseAuth.getInstance().getCurrentUser().getUid());
        gameData.put(TicTacToeGame.OPPONENT_ID_FIELD, opponentID);
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();

        // Create new match
        firestore.collection(COLLECTION_TIC_TAC_TOE_GAMES)
                .add(gameData).addOnSuccessListener(documentReference -> {
                    /*
                    Cloud function not working with the spark plan, use a test channel for agora

                    // Get agora token for video call
                    Map<String, Object> data = new HashMap<>();
                    data.put("uid", FirebaseAuth.getInstance().getCurrentUser().getUid().hashCode());
                    data.put("channel_name", documentReference.getId());
                    FirebaseFunctions.getInstance()
                            .getHttpsCallable("createAgoraToken")
                            .call(data)
                            .continueWith(task -> {
                                // This continuation runs on either success or failure, but if the task
                                // has failed then getResult() will throw an Exception which will be
                                // propagated down.

                                if(!(task.getResult().getData() instanceof HashMap)) {
                                    HashMap<String, Object> result = (HashMap<String, Object>) task.getResult().getData();
                                    Log.d("debugg", "token: " + result.getClass().getName());
                                    return result;
                                }
                                else
                                    return new HashMap<String, Object>();

                            })
                            .addOnSuccessListener(resultMap -> {
                                if(resultMap.keySet().size() == 0)
                                    Log.w("debugg", "Agora token creation failed");
                                else {
                                    Log.d("debugg", "agora token " + resultMap.get("token"));
                                }
                            });
                     */

                    // Update agora channel name and token
                    documentReference.update(TicTacToeGame.AGORA_CHANNEL_FIELD, "test");
                    documentReference.update(TicTacToeGame.AGORA_TOKEN_FIELD, "00629740b29ac4d480e9ff663b48521191bIAC4/+nqf30nZzEUZDQtAKR3j27wx3VCG67bl+SJ9quvzgx+f9gAAAAAEADJ+bHOvmChXwEAAQC+YKFf");

                    // Add match to users
                    Map<String, Object> currentGame = new HashMap<>(2);
                    currentGame.put(UsersCurrentGame.TYPE_FIELD, COLLECTION_TIC_TAC_TOE_GAMES);
                    currentGame.put(UsersCurrentGame.GAME_ID_FIELD, documentReference.getId());
                    firestore.collection(COLLECTION_USERS_CURRENT_GAME).document(FirebaseAuth.getInstance().getCurrentUser().getUid())
                        .set(currentGame)
                        .addOnSuccessListener(aVoid -> {
                            Log.d("debugg", "Current game owner updated");
                        })
                        .addOnFailureListener(e -> {
                            Log.w("debugg", "Current game owner update failed", e);
                        });

                    firestore.collection(COLLECTION_USERS_CURRENT_GAME).document(opponentID)
                            .set(currentGame)
                            .addOnSuccessListener(aVoid -> {
                                Log.d("debugg", "Current game opponent updated");
                            })
                            .addOnFailureListener(e -> {
                                Log.w("debugg", "Current game opponent update failed", e);
                            });
                })
        .addOnFailureListener(e -> Log.w("debugg", "TicTacToeGame creation failure", e));
    }

    public IUser getUserData(){
        return this.userData;
    }

    public IFriends getUserFriends() {
        return this.userFriends;
    }


}
