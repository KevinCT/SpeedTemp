package com.zweigbergk.speedswede.adapter;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.zweigbergk.speedswede.R;
import com.zweigbergk.speedswede.core.Message;
import com.zweigbergk.speedswede.service.DatabaseHandler;

import java.util.ArrayList;
import java.util.List;

public class MessageAdapter extends BaseAdapter {
    private final List<Message> mMessageList;
    private View mView;
    private TextView mName;
    private TextView mText;
    private TextView mTimeStamp;

    public MessageAdapter(){

        mMessageList = DatabaseHandler.INSTANCE.fetchInitialData(() -> notifyDataSetChanged());



    }


    @Override
    public int getCount() {
        return mMessageList.size();
    }

    @Override
    public Object getItem(int i) {
        return mMessageList.indexOf(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        //all messages are seen as user now, unique variable needed to separate between user messages
        if(view==null){
            mView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.fragment_message_user,viewGroup,false);
        }

        mText = (TextView) mView.findViewById(R.id.message_textview_user);
        mText.setText(mMessageList.get(i).getText());
        return mView;
    }
}
