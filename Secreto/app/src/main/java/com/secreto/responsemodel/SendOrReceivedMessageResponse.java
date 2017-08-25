package com.secreto.responsemodel;

import com.google.gson.annotations.SerializedName;
import com.secreto.model.Message;

import java.util.ArrayList;

/**
 * Created by Aashish Tomar on 8/25/2017.
 */

public class SendOrReceivedMessageResponse extends BaseResponse {
    @SerializedName("list")
    private ArrayList<Message> messageArrayList;

    public ArrayList<Message> getMessageArrayList() {
        return messageArrayList;
    }
}
