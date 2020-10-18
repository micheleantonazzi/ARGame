package com.argame.utilities.data_structures.friends_data;

public interface FriendsInterface {
    void addOnUpdateListener(ListenerFriendsUpdate listener);

    void removeUpdateListener(ListenerFriendsUpdate listener);
}
