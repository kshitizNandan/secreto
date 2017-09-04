package com.secreto.responsemodel;

import com.google.gson.annotations.SerializedName;
import com.secreto.model.Message;
import com.secreto.model.MessageAndUserResponse;

import java.util.ArrayList;

/**
 * Created by Aashish Tomar on 8/25/2017.
 */

public class SendOrReceivedMessageResponse extends BaseResponse {
    @SerializedName("list")
    private ArrayList<MessageAndUserResponse> messageArrayList;
    @SerializedName("offset")
    private int offset;

    public int getOffset() {
        return offset;
    }

    public ArrayList<MessageAndUserResponse> getMessageArrayList() {
        return messageArrayList;
    }
}
