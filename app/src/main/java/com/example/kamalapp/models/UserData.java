package com.example.kamalapp.models;

public class UserData {
    private String email;

    public UserData() {
        // Default constructor required for calls to DataSnapshot.getValue(UserData.class)
    }

    public UserData(String email) {
        this.email = email;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}