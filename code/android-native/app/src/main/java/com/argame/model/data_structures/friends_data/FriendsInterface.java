package com.argame.model.data_structures.friends_data;

import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleOwner;

import com.argame.model.data_structures.user_data.UserInterface;

import java.util.List;

public interface FriendsInterface {
    void addOnUpdateListener(ListenerFriendsUpdate listener);

    void addOnUpdateListenerLifecycle(LifecycleOwner owner, Lifecycle.Event event, ListenerFriendsUpdate listener);

    void removeUpdateListener(ListenerFriendsUpdate listener);

    List<UserInterface> getFriendsList();

    int getFriendOrderedNumber(UserInterface friend);
}
