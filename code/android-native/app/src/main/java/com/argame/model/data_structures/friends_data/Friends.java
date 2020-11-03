package com.argame.model.data_structures.friends_data;

import android.util.Log;

import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleEventObserver;
import androidx.lifecycle.LifecycleOwner;

import com.argame.model.SubjectUpdate;

import com.argame.model.data_structures.current_user.CurrentUser;
import com.argame.model.data_structures.user_data.User;
import com.argame.model.data_structures.user_data.IUser;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;


import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class Friends implements IFriends, SubjectUpdate {

    private static Friends INSTANCE;

    // Collection name
    public static final String COLLECTION_USER_FRIENDS = "users_friends";

    // Fields' name
    public static final String FRIENDS_FIELD = "friends";

    boolean isInitialize = false;
    private List<ListenerFriendsUpdate> listeners = new ArrayList<>();
    private Map<String, User> friends = new HashMap<>();
    private Map<String, ListenerRegistration> friendsUpdateListeners = new HashMap<>();
    private List<IUser> orderedUsers = new ArrayList<>();

    private Friends() {}

    synchronized public static Friends getInstance() {
        if(INSTANCE == null)
            INSTANCE = new Friends();
        return INSTANCE;
    }

    synchronized public void initialize() {
        if(FirebaseAuth.getInstance().getCurrentUser() == null || !CurrentUser.getInstance().isInitialized() || this.isInitialize)
            return;

        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
        firestore.collection(COLLECTION_USER_FRIENDS).document(FirebaseAuth.getInstance().getCurrentUser().getUid()).addSnapshotListener((snapshotFriends, exception) -> {
            synchronized (Friends.getInstance()) {
                if (exception != null) {
                    Log.w("debugg", "Read user's friends failed", exception);
                    return;
                }

                if (snapshotFriends != null && snapshotFriends.exists() && snapshotFriends.getData() != null) {
                    Set<String> updatedFriendsIDs = new HashSet<>((List<String>) snapshotFriends.getData().get(Friends.FRIENDS_FIELD));

                    // REMOVE DELETED FRIENDS
                    Set<String> deletedFriendsIDs = this.getDeletedFriendsIDs(updatedFriendsIDs);

                    // Remove listeners that notify deleted friends changes
                    for (String deletedFriendID : deletedFriendsIDs)
                        this.friendsUpdateListeners.get(deletedFriendID).remove();
                    this.friendsUpdateListeners.keySet().removeAll(deletedFriendsIDs);
                    this.removeFriendsUsingIDs(deletedFriendsIDs);

                    // Add new friends
                    Set<String> newFriendsIDs = this.getNewFriends(updatedFriendsIDs);
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
                        this.addNewFriend(newFriendID, newFriend);
                    }
                    // Notify listeners that friends list is changed
                    this.notifyListeners();
                } else {
                    Log.d("debugg", "Current data: null");
                }
            }
        });
    }

    private Set<String> getDeletedFriendsIDs(Set<String> updatedFriendsIDs) {
        Set<String> deletedFriendsIDs = new HashSet<>(this.friends.keySet());
        deletedFriendsIDs.removeAll(updatedFriendsIDs);
        return deletedFriendsIDs;
    }

    private Set<String> getNewFriends(Set<String> updatedFriendsIDs) {
        Set<String> newFriends = new HashSet<>(updatedFriendsIDs);
        newFriends.removeAll(this.friends.keySet());
        return newFriends;
    }

    private void addNewFriend(String friendID, User newFriend) {
        this.friends.put(friendID, newFriend);
        this.sortUser();
    }

    private void removeFriendsUsingIDs(Set<String> deletedFriendsIDs) {
        this.friends.keySet().removeAll(deletedFriendsIDs);
        this.sortUser();
    }

    // Listener pattern implementation
    @Override
    synchronized public void addOnUpdateListener(ListenerFriendsUpdate listener) {
        if(!this.listeners.contains(listener))
            this.listeners.add(listener);
    }

    @Override
    synchronized public void addOnUpdateListenerLifecycle(LifecycleOwner owner, Lifecycle.Event event, ListenerFriendsUpdate listener) {
        owner.getLifecycle().addObserver((LifecycleEventObserver) (source, e) -> {
            synchronized (this) {
                if (event == e)
                    this.removeUpdateListener(listener);

            }
        });
        this.listeners.add(listener);
    }

    @Override
    synchronized public void removeUpdateListener(ListenerFriendsUpdate listener) {
        this.listeners.remove(listener);
    }


    @Override
    synchronized public void notifyListeners() {
        for(ListenerFriendsUpdate listener: this.listeners)
            listener.update(this);
    }

    private void sortUser() {
        this.orderedUsers =  new ArrayList<>(this.friends.values());
        Collections.sort(this.orderedUsers, (o1, o2) -> o1.getNickname().compareTo(o2.getNickname()));
    }

    @Override
    synchronized public List<IUser> getFriendsList() {
        return this.orderedUsers;

    }

    @Override
    synchronized public int getFriendOrderedNumber(IUser friend) {
        return this.orderedUsers.indexOf(friend);
    }

    @Override
    synchronized public IUser getFriend(String friendID) {
        return this.friends.get(friendID);
    }
}
