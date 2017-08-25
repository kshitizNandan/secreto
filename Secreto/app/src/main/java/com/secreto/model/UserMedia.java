package com.secreto.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by Aashish Tomar on 8/21/2017.
 */

public class UserMedia implements Serializable {
    @SerializedName("mediaId")
    private String media_id;
    @SerializedName("media")
    private String profile_pic;
    @SerializedName("type")
    private String type;

    public String getMedia_id() {
        return media_id;
    }

    public String getProfile_pic() {
        return profile_pic;
    }

    public String getType() {
        return type;
    }

    public void setProfile_pic(String profile_pic) {
        this.profile_pic = profile_pic;
    }
}
