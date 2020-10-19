package com.argame.utilities.data_structures.friends_data;

import com.argame.utilities.SubjectUpdate;

import com.argame.utilities.data_structures.user_data.User;


import java.util.ArrayList;
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
    }

    synchronized public void removeFriendsUsingIDs(Set<String> deletedFriendsIDs) {
        this.friends.keySet().removeAll(deletedFriendsIDs);
    }

    // Listener pattern implementation
    @Override
    synchronized public void addOnUpdateListener(ListenerFriendsUpdate listener) {
        if(!this.listeners.contains(listener))
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
}
