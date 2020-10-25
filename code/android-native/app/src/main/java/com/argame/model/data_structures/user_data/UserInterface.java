package com.argame.model.data_structures.user_data;

import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleOwner;
import androidx.recyclerview.widget.DiffUtil;

public interface UserInterface {

    DiffUtil.ItemCallback<UserInterface> DIFF_CALLBACK = null;

    void addOnUpdateListener(ListenerUserUpdate listener);

    void addOnUpdateListenerLifecycle(LifecycleOwner owner, Lifecycle.Event event, ListenerUserUpdate listener);

    void removeUpdateListener(ListenerUserUpdate listener);

    String getUid();

    String getName();

    String getSurname();

    String getEmail();

    String getNickname();

    int getOnlineStatus();

    int getProfileImageCount();
}
