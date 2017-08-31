package com.secreto.responsemodel;

import com.google.gson.annotations.SerializedName;
import com.secreto.model.User;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Stack;

/**
 * Created by Kshitiz Nandan on 8/31/2017.
 */

public class AllUserResponse extends BaseResponse implements Serializable {
    @SerializedName("list")
    private ArrayList<User> users;

    public ArrayList<User> getUsers() {
        return users;
    }

    public void setUsers(ArrayList<User> users) {
        this.users = users;
    }

    @Override
    public String toString() {
        return "AllUserResponse{" +
                "users=" + users +
                '}';
    }
}
