package com.secreto.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by Aashish Tomar on 8/25/2017.
 */

public class Message implements Serializable {
    @SerializedName("messageId")
    private String messageId;
    @SerializedName("userId")
    private String userId;
    @SerializedName("fromUserId")
    private String fromUserId;
    @SerializedName("message")
    private String message;
    @SerializedName("messageClue")
    private String messageClue;

    public String getMessageId() {
        return messageId;
    }

    public String getUserId() {
        return userId;
    }

    public String getFromUserId() {
        return fromUserId;
    }

    public String getMessage() {
        return message;
    }

    public String getMessageClue() {
        return messageClue;
    }
}
