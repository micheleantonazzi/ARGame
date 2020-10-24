package com.argame.model;

import android.util.Log;

import androidx.annotation.NonNull;

import com.argame.model.data_structures.friends_data.Friends;
import com.argame.model.data_structures.friends_data.FriendsInterface;
import com.argame.model.data_structures.user_data.User;
import com.argame.model.data_structures.user_data.UserInterface;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;
import com.google.firebase.firestore.ListenerRegistration;

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
                    for(String deletedFriendID: deletedFriendsIDs)
                        this.friendsUpdateListeners.get(deletedFriendID).remove();
                    this.friendsUpdateListeners.keySet().removeAll(deletedFriendsIDs);
                    this.userFriends.removeFriendsUsingIDs(deletedFriendsIDs);

                    // Add new friends
                    Set<String> newFriendsIDs = this.userFriends.getNewFriends(updatedFriendsIDs);
                    for(String newFriendID: newFriendsIDs) {
                        User newFriend = new User().setUid(newFriendID);

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

    public UserInterface getUserData(){
        return this.userData;
    }

    public FriendsInterface getUserFriends() {
        return this.userFriends;
    }


}
