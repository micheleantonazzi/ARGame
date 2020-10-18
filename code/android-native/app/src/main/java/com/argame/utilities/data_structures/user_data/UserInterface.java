package com.argame.utilities.data_structures.user_data;

public interface UserInterface {

    void addOnUpdateListener(ListenerUserUpdate listener);

    void removeUpdateListener(ListenerUserUpdate listener);

    String getUid();

    String getName();

    String getSurname();

    String getEmail();

    String getNickname();

    int getProfileImageCount();
}
