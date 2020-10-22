package com.argame.model.data_structures.friends_data;

import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleEventObserver;
import androidx.lifecycle.LifecycleOwner;

import com.argame.model.SubjectUpdate;

import com.argame.model.data_structures.user_data.User;
import com.argame.model.data_structures.user_data.UserInterface;


import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class Friends implements FriendsInterface, SubjectUpdate {

    // Fields' name
    public static final String FRIENDS_FIELD = "friends";

    private List<ListenerFriendsUpdate> listeners = new ArrayList<>();
    private Map<String, User> friends = new HashMap<>();
    private List<UserInterface> orderedUsers = new ArrayList<>();

    synchronized public Set<String> getDeletedFriendsIDs(Set<String> updatedFriendsIDs) {
        Set<String> deletedFriendsIDs = new HashSet<>(this.friends.keySet());
        deletedFriendsIDs.removeAll(updatedFriendsIDs);
        return deletedFriendsIDs;
    }

    synchronized public Set<String> getNewFriends(Set<String> updatedFriendsIDs) {
        Set<String> newFriends = new HashSet<>(updatedFriendsIDs);
        newFriends.removeAll(this.friends.keySet());
        return newFriends;
    }

    synchronized public void addNewFriend(String friendID, User newFriend) {
        this.friends.put(friendID, newFriend);
        this.sortUser();
    }

    synchronized public void removeFriendsUsingIDs(Set<String> deletedFriendsIDs) {
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
    synchronized public List<UserInterface> getFriendsList() {
        return this.orderedUsers;

    }

    @Override
    public int getFriendOrderedNumber(UserInterface friend) {
        return this.orderedUsers.indexOf(friend);
    }
}
