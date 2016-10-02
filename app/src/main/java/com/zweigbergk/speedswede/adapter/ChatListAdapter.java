package com.zweigbergk.speedswede.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.zweigbergk.speedswede.R;
import com.zweigbergk.speedswede.core.Chat;
import com.zweigbergk.speedswede.core.Message;

import java.util.ArrayList;
import java.util.List;

import static android.media.CamcorderProfile.get;

public class ChatListAdapter extends BaseAdapter {

    ArrayList<Chat> mChats;

    public ChatListAdapter(ArrayList<Chat> chats) {

        mChats = chats;
    }

    @Override
    public int getCount() {
        return mChats.size();
    }

    @Override
    public Object getItem(int position) {
        return mChats.get(position);
    }

    @Override
    public long getItemId(int position) {
        Chat currentChat = mChats.get(position);
        return currentChat.getIdAsLong();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if (convertView == null) {
            convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_chat_list_item, parent, false);
        }

        TextView header = (TextView) convertView.findViewById(R.id.header_textView);

        header.setText(mChats.get(position).getId()); //Or the name of the other user?

        TextView description = (TextView) convertView.findViewById(R.id.description_textView);

        List<Message> lastConversation = mChats.get(position).getConversation();

        description.setText(lastConversation.get(lastConversation.size()).getText());


        return convertView;
    }

    public void addChat(Chat chat) {
        mChats.add(chat);
        notifyDataSetChanged();
    }

    public void removeChat(Chat chat) {
        mChats.remove(chat);
        notifyDataSetChanged();
    }

}
