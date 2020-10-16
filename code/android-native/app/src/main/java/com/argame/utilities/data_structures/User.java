package com.argame.utilities.data_structures;

public class User implements UserInterface{

    private String name = "";
    private String surname = "";
    private String email = "";
    private String nickname = "";
    private int profileImageCount = -1;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public int getProfileImageCount() {
        return profileImageCount;
    }

    public void setProfileImageCount(int profileImageCount) {
        this.profileImageCount = profileImageCount;
    }
}
