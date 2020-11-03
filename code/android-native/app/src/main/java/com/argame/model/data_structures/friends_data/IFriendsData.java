package com.argame.model.data_structures.friends_data;

import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleOwner;

import com.argame.model.data_structures.user_data.IUser;

import java.util.List;

public interface IFriendsData {

    void removeUpdateListener(ListenerFriendsUpdate listener);

    void addOnUpdateListenerLifecycle(LifecycleOwner owner, Lifecycle.Event event, ListenerFriendsUpdate listener);

    void addOnUpdateListener(ListenerFriendsUpdate listener);

    List<IUser> getFriendsList();

    int getFriendOrderedNumber(IUser friend);

    IUser getFriend(String friendID);
}
