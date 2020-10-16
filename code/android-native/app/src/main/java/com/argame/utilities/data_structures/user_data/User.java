package com.argame.utilities.data_structures.user_data;

import android.util.Log;

import com.argame.utilities.SubjectUpdate;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class User implements UserInterface, SubjectUpdate {

    // Fields' name
    public static String NAME_FIELD = "name";
    public static String SURNAME_FIELD = "surname";
    public static String NICKNAME_FIELD = "nickname";
    public static String EMAIL_FIELD = "email";
    public static String PROFILE_IMAGE_COUNT_FIELD = "profileImageCount";

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
    synchronized public void removeUpdateListener(ListenerUserUpdate listener) {
        this.listeners.remove(listener);
    }


    @Override
    synchronized public void notifyListeners() {
        for(ListenerUserUpdate listener: this.listeners)
            listener.update(this);
    }

    // Getters and setters
    synchronized public void updateData(Map<String, Object> data) {
        this.name = String.valueOf(data.get(NAME_FIELD));
        this.surname = String.valueOf(data.get(SURNAME_FIELD));
        this.nickname = String.valueOf(data.get(NICKNAME_FIELD));
        this.email = String.valueOf(data.get(EMAIL_FIELD));
        this.profileImageCount = Integer.valueOf(String.valueOf(data.get(PROFILE_IMAGE_COUNT_FIELD)));
        this.notifyListeners();
    }

    @Override
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    @Override
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @Override
    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    @Override
    public int getProfileImageCount() {
        return profileImageCount;
    }

    public void setProfileImageCount(int profileImageCount) {
        this.profileImageCount = profileImageCount;
    }
}
