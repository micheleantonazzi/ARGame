package com.argame.utilities.data_structures.friends_data;

import com.argame.utilities.SubjectUpdate;
import com.argame.utilities.data_structures.user_data.ListenerUserUpdate;
import com.argame.utilities.data_structures.user_data.UserInterface;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Friends implements FriendsInterface, SubjectUpdate {

    private List<ListenerFriendsUpdate> listeners = new ArrayList<>();
    private Set<UserInterface> friends = new HashSet<>();

    public Set<String> findDeletedFriends(Set<String> newFriends) {
        Set<String> currentFriends = new HashSet<>();
        for(UserInterface friend: this.friends)
            currentFriends.add(friend.getUid());
        return currentFriends;
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
