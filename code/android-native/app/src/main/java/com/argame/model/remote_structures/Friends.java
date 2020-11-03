package com.argame.model.remote_structures;

import android.util.Log;

import com.argame.model.data_structures.friends_data.FriendsData;
import com.argame.model.data_structures.friends_data.IFriendsData;
import com.argame.model.data_structures.user_data.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;


import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class Friends {

    private static Friends INSTANCE;

    // Collection name
    public static final String COLLECTION_USER_FRIENDS = "users_friends";

    // Fields' name
    public static final String FRIENDS_FIELD = "friends";

    boolean isInitialized = false;
    private final FriendsData friendsData = new FriendsData();
    private Map<String, ListenerRegistration> friendsUpdateListeners = new HashMap<>();

    private Friends() {}

    synchronized public static Friends getInstance() {
        if(INSTANCE == null)
            INSTANCE = new Friends();
        return INSTANCE;
    }

    synchronized public void initialize() {
        if(FirebaseAuth.getInstance().getCurrentUser() == null || !CurrentUser.getInstance().isInitialized() || this.isInitialized)
            return;

        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
        firestore.collection(COLLECTION_USER_FRIENDS).document(FirebaseAuth.getInstance().getCurrentUser().getUid()).addSnapshotListener((snapshotFriends, exception) -> {
            synchronized (this.friendsData) {
                if (exception != null) {
                    Log.w("debugg", "Read user's friends failed", exception);
                    return;
                }

                if (snapshotFriends != null && snapshotFriends.exists() && snapshotFriends.getData() != null) {
                    Set<String> updatedFriendsIDs = new HashSet<>((List<String>) snapshotFriends.getData().get(Friends.FRIENDS_FIELD));

                    // REMOVE DELETED FRIENDS
                    Set<String> deletedFriendsIDs = this.friendsData.getDeletedFriendsIDs(updatedFriendsIDs);

                    // Remove listeners that notify deleted friends changes
                    for (String deletedFriendID : deletedFriendsIDs)
                        this.friendsUpdateListeners.get(deletedFriendID).remove();
                    this.friendsUpdateListeners.keySet().removeAll(deletedFriendsIDs);
                    this.friendsData.removeFriendsUsingIDs(deletedFriendsIDs);

                    // Add new friends
                    Set<String> newFriendsIDs = this.friendsData.getNewFriends(updatedFriendsIDs);
                    for (String newFriendID : newFriendsIDs) {

                        // Create new friend instance
                        User newFriend = new User().setUid(newFriendID);

                        // Create new friend listeners which notifies changes
                        ListenerRegistration listener = firestore.collection(CurrentUser.COLLECTION_USER_DATA).document(newFriendID)
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
                        this.friendsData.addNewFriend(newFriendID, newFriend);
                    }
                    // Notify listeners that friends list is changed
                    this.friendsData.notifyListeners();
                } else {
                    Log.d("debugg", "Current data: null");
                }
            }
        });
    }

    synchronized public IFriendsData getFriendsData() {
        return this.friendsData;
    }
}
