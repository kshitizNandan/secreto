package com.secreto.fragments;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.secreto.R;


public class SentReceivedMessagesFragment extends Fragment {
    private static final String ARG_PARAM1 = "param1";
    private String mParam1;

    public SentReceivedMessagesFragment() {
    }
    public static SentReceivedMessagesFragment newInstance(String param1) {
        SentReceivedMessagesFragment fragment = new SentReceivedMessagesFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_sent_received_messages, container, false);
    }

}
