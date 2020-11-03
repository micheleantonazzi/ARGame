package com.argame.model.data_structures.friends_data;

import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleEventObserver;
import androidx.lifecycle.LifecycleOwner;

import com.argame.model.SubjectUpdate;
import com.argame.model.data_structures.user_data.IUser;
import com.argame.model.data_structures.user_data.User;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class FriendsData implements IFriendsData, SubjectUpdate {

    private List<ListenerFriendsUpdate> listeners = new ArrayList<>(0);
    private Map<String, User> friends = new HashMap<>();
    private List<IUser> orderedUsers = new ArrayList<>();

    private void sortUser() {
        this.orderedUsers =  new ArrayList<>(this.friends.values());
        Collections.sort(this.orderedUsers, (o1, o2) -> o1.getNickname().compareTo(o2.getNickname()));
    }

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

    synchronized public void removeFriendsUsingIDs(Set<String> deletedFriendsIDs) {
        this.friends.keySet().removeAll(deletedFriendsIDs);
        this.sortUser();
    }

    synchronized public void addNewFriend(String friendID, User newFriend) {
        this.friends.put(friendID, newFriend);
        this.sortUser();
    }

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
