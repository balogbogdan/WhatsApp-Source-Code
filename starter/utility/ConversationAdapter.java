package com.parse.starter.utility;

import android.content.Context;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.widget.ArrayAdapter;

import java.util.ArrayList;
import java.util.List;



public class ConversationAdapter extends ArrayAdapter {
    private ArrayList<String> messages;
    private Context mContext;

    public ConversationAdapter(@NonNull Context context, @LayoutRes int resource, ArrayList<String> messages) {
        super(context, com.parse.starter.R.layout.simple_adapter, messages);
        this.messages = messages;
        this.mContext = context;
    }



}
