package com.secreto.model;

import java.io.Serializable;

public class UserResponse extends BaseResponse implements Serializable {

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