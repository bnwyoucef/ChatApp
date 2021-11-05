package com.bounoua.haselkichat;

public class MessageModel {
    private String message;
    private String from;
    private String keyUnique;

    public MessageModel() {
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getKeyUnique() {
        return keyUnique;
    }

    public void setKeyUnique(String keyUnique) {
        this.keyUnique = keyUnique;
    }
}
