package com.secreto.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class MessageAndUserResponse implements Serializable {
    @SerializedName("message")
    private Message message;
    @SerializedName("user")
    private User user;

    public Message getMessage() {
        return message;
    }

    public User getUser() {
        return user;
    }
}

