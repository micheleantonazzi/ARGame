package com.argame.model.data_structures.user_data;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleEventObserver;
import androidx.lifecycle.LifecycleOwner;
import androidx.recyclerview.widget.DiffUtil;

import com.argame.model.SubjectUpdate;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class User implements UserInterface, SubjectUpdate {

    // Callback for diffutil
    public static final DiffUtil.ItemCallback<UserInterface> DIFF_CALLBACK = new DiffUtil.ItemCallback<UserInterface>() {
        @Override
        public boolean areItemsTheSame(@NonNull UserInterface oldItem, @NonNull UserInterface newItem) {
            return oldItem.equals(newItem);
        }

        @Override
        public boolean areContentsTheSame(@NonNull UserInterface oldItem, @NonNull UserInterface newItem) {
            return oldItem.getUid().equals(newItem.getUid()) &&
                    oldItem.getName().equals(newItem.getName()) &&
                    oldItem.getSurname().equals(newItem.getSurname()) &&
                    oldItem.getEmail().equals(newItem.getEmail()) &&
                    oldItem.getNickname().equals(newItem.getNickname()) &&
                    oldItem.getProfileImageCount() == newItem.getProfileImageCount();
        }
    };

    // Fields' name
    public static String UID_FIELD = "uid";
    public static String NAME_FIELD = "name";
    public static String SURNAME_FIELD = "surname";
    public static String NICKNAME_FIELD = "nickname";
    public static String EMAIL_FIELD = "email";
    public static String PROFILE_IMAGE_COUNT_FIELD = "profileImageCount";

    private String uid = "";
    private String name = "";
    private String surname = "";
    private String email = "";
    private String nickname = "";
    private int profileImageCount = -1;

    private List<ListenerUserUpdate> listeners = new ArrayList<>(0);

    // Listener pattern implementation
    @Override
    synchronized public void addOnUpdateListener(ListenerUserUpdate listener) {
        if(!this.listeners.contains(listener))
            this.listeners.add(listener);
    }

    @Override
    synchronized public void addOnUpdateListenerLifecycle(LifecycleOwner owner, Lifecycle.Event event, ListenerUserUpdate listener) {
        owner.getLifecycle().addObserver((LifecycleEventObserver) (source, e) -> {
            synchronized (this) {
                if (event == e)
                    this.removeUpdateListener(listener);

            }
        });
        this.listeners.add(listener);
    }

    @Override
    synchronized public void removeUpdateListener(ListenerUserUpdate listener) {
        this.listeners.remove(listener);
    }


    @Override
    synchronized public void notifyListeners() {
        for(ListenerUserUpdate listener: this.listeners)
            listener.update(this);
    }

    // Getters and setters
    synchronized public User updateData(Map<String, Object> data) {
        this.uid = String.valueOf(data.get(UID_FIELD));
        this.name = String.valueOf(data.get(NAME_FIELD));
        this.surname = String.valueOf(data.get(SURNAME_FIELD));
        this.nickname = String.valueOf(data.get(NICKNAME_FIELD));
        this.email = String.valueOf(data.get(EMAIL_FIELD));
        this.profileImageCount = Integer.parseInt(String.valueOf(data.get(PROFILE_IMAGE_COUNT_FIELD)));
        return this;
    }

    @Override
    public String getUid() {
        return uid;
    }

    public User setUid(String uid) {
        this.uid = uid;
        return this;
    }

    @Override
    public String getName() {
        return name;
    }

    public User setName(String name) {
        this.name = name;
        return this;
    }

    @Override
    public String getSurname() {
        return surname;
    }

    public User setSurname(String surname) {
        this.surname = surname;
        return this;
    }

    @Override
    public String getEmail() {
        return email;
    }

    public User setEmail(String email) {
        this.email = email;
        return this;
    }

    @Override
    public String getNickname() {
        return nickname;
    }

    public User setNickname(String nickname) {
        this.nickname = nickname;
        return this;
    }

    @Override
    public int getProfileImageCount() {
        return profileImageCount;
    }

    public User setProfileImageCount(int profileImageCount) {
        this.profileImageCount = profileImageCount;
        return this;
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        if(obj instanceof User) {
            return this.uid.equals(((User) obj).uid);
        }
        return false;
    }
}
