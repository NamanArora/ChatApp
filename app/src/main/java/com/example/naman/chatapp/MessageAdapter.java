package com.example.naman.chatapp;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;

import java.util.List;

/**
 * Created by Naman on 22-01-2017.
 */
public class MessageAdapter extends ArrayAdapter<Message> {

    public MessageAdapter(Context context, List<Message> objects) {
        super(context, 0 , objects);
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        int type = getItemViewType(position);
        if(type == 0)
        {
            if(convertView == null)
            {
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.message_row_2, parent, false);
            }
        }
        else
        {
            if(convertView == null)
            {
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.message_row_1, parent, false);
            }
        }
        ((TextView)convertView.findViewById(R.id.txt)).setText(getItem(position).message);
        ((TextView)convertView.findViewById(R.id.sender)).setText(getItem(position).sender);
        return convertView;
    }

    @Override
    public int getItemViewType(int position) {
        if(getItem(position) != null) {
            if (getItem(position).sender.equals(ChatScreen.user))
                return 0;
            else
                return 1;
        }
        else
        return 0;
    }

    @Override
    public int getViewTypeCount() {
        return 2;
    }
}
