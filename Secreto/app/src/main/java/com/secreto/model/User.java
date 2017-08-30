package com.secreto.model;

import com.google.gson.annotations.SerializedName;
import com.secreto.responsemodel.BaseResponse;
import com.secreto.responsemodel.MediaResponse;

import java.io.Serializable;

public class User extends UserMedia implements Serializable {
    @SerializedName("userId")
    private String userId;
    @SerializedName("name")
    private String name;
    @SerializedName("gender")
    private String gender;
    @SerializedName("userName")
    private String userName;
    @SerializedName("email")
    private String email;
    @SerializedName("contact")
    private String contact;
    @SerializedName("password")
    private String password;
    @SerializedName("userStatus")
    private String userStatus;
    @SerializedName("createdDate")
    private String createdDate;

    public String getUserId() {
        return userId;
    }

    public String getName() {
        return name;
    }

    public String getUserName() {
        return userName;
    }

    public String getEmail() {
        return email;
    }

    public String getContact() {
        return contact;
    }

    public String getPassword() {
        return password;
    }

    public String getUserStatus() {
        return userStatus;
    }

    public String getGender() {
        return gender;
    }

    public String getCreatedDate() {
        return createdDate;
    }
}
