package com.zweigbergk.speedswede.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.zweigbergk.speedswede.R;
import com.zweigbergk.speedswede.core.Chat;
import com.zweigbergk.speedswede.core.Message;
import com.zweigbergk.speedswede.core.User;
import com.zweigbergk.speedswede.database.DataChange;
import com.zweigbergk.speedswede.database.DatabaseEvent;
import com.zweigbergk.speedswede.database.DatabaseHandler;
import com.zweigbergk.speedswede.util.Lists;

import java.util.ArrayList;
import java.util.List;

public class ChatListAdapter extends BaseAdapter {
    private ArrayList<Chat> mChats;

    public ChatListAdapter() {

        mChats = new ArrayList<>();
    }

    @Override
    public int getCount() {
        return mChats.size();
    }

    public Chat getChatAt(int index) {
        return mChats.get(index);
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

        header.setText(mChats.get(position).getName()); //Or the name of the other user?

        TextView description = (TextView) convertView.findViewById(R.id.description_textView);

        List<Message> lastConversation = mChats.get(position).getConversation();
        Message latestMessage = Lists.getLast(lastConversation);

        String text =  latestMessage == null ? "N/A" : latestMessage.getText();
        description.setText(text);


        return convertView;
    }

    public void notifyChange(DataChange<Chat> change) {
        Chat chat = change.getItem();
        DatabaseEvent event = change.getEvent();

        switch (event) {
            case ADDED:
                addChat(chat);
                break;
            case CHANGED:
                User activeUser = DatabaseHandler.getActiveUser();
                if (!chat.includesUser(activeUser)) {
                    removeChat(chat);
                }
                break;
            case REMOVED:
                removeChat(chat);
                break;
            default:
                break;
        }
    }

    private void addChat(Chat chat) {
        mChats.add(chat);
        notifyDataSetChanged();
    }

    public void removeChat(Chat chat) {
        mChats.remove(chat);
        notifyDataSetChanged();
    }
}
