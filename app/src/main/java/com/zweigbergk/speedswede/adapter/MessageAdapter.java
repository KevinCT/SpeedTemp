package com.zweigbergk.speedswede.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

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

    public MessageAdapter() {
        mMessageList = new ArrayList<>();
        //Grab a List<Message> of messages,
        //and call initializeWithConversation() with the grabbed list as argument.
        DatabaseHandler.INSTANCE.fetchConversation(this::initializeWithConversation);
        DatabaseHandler.INSTANCE.registerConversationListener(this::updateConversation);


    }

    private void initializeWithConversation(List<Message> messageList) {
        mMessageList.addAll(messageList);
        notifyDataSetChanged();
    }

    private void updateConversation(Message message) {
        for (Message listMessage : mMessageList) {
            if (listMessage.equals(message)) {
                mMessageList.add(message);
                notifyDataSetChanged();
                return;
            }
        }
        mMessageList.add(message);
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return mMessageList.size();
    }

    @Override
    public Object getItem(int i) {
        return mMessageList.get(i);
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
