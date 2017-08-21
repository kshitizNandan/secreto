package com.secreto.responsemodel;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by Aashish Tomar on 8/21/2017.
 */

public class MediaResponse extends BaseResponse implements Serializable {
    @SerializedName("media")
    private String media;

    public String getMedia() {
        return media;
    }
}
