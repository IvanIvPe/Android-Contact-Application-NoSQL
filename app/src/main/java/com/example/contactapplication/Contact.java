package com.example.contactapplication;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;
import io.realm.annotations.RealmClass;

@RealmClass
public class Contact extends RealmObject {

    @PrimaryKey
    private int id;
    private String name;
    private String phone;
    private String profilePictureUri;

    public Contact() {
    }

    public Contact(int id, String name, String phone, String profilePictureUri) {
        this.id = id;
        this.name = name;
        this.phone = phone;
        this.profilePictureUri = profilePictureUri;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getProfilePictureUri() {
        return profilePictureUri;
    }

    public void setProfilePictureUri(String profilePictureUri) {
        this.profilePictureUri = profilePictureUri;
    }
}
