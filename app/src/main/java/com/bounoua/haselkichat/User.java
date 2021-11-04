package com.bounoua.haselkichat;

public class User {
    private String userName;
    private String imagePath;
    private String userID;

    public User(String userName, String imagePath, String userID) {
        this.userName = userName;
        this.imagePath = imagePath;
        this.userID = userID;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }
}
