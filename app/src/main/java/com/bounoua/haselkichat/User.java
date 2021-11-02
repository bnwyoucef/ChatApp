package com.bounoua.haselkichat;

public class User {
    private String userName;
    private String imagePath;

    public User(String userName, String imagePath) {
        this.userName = userName;
        this.imagePath = imagePath;
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
}
