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
    @SerializedName("toUserId")
    private String toUserId;
    @SerializedName("message")
    private String message;
    @SerializedName("messageClue")
    private String messageClue;
    @SerializedName("createdDate")
    private String createdDate;
    @SerializedName("canReply")
    private String canReply;

    public String getCanReply() {
        return canReply;
    }

    public String getMessageId() {
        return messageId;
    }

    public String getUserId() {
        return userId;
    }

    public String getToUserId() {
        return toUserId;
    }

    public String getCreatedDate() {
        return createdDate;
    }

    public String getMessage() {
        return message;
    }

    public String getMessageClue() {
        return messageClue;
    }
}
