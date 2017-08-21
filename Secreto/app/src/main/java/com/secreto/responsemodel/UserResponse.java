package com.secreto.responsemodel;

import com.google.gson.annotations.SerializedName;
import com.secreto.model.User;

import java.io.Serializable;

public class UserResponse extends BaseResponse implements Serializable {
    @SerializedName("user")
    private User user;

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    @Override
    public String toString() {
        return "UserResponse{" +
                "user=" + user +
                '}';
    }
}