package com.zweigbergk.speedswede.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.zweigbergk.speedswede.R;
import com.zweigbergk.speedswede.core.Chat;
import com.zweigbergk.speedswede.core.Message;
import com.zweigbergk.speedswede.database.DataChange;
import com.zweigbergk.speedswede.database.DatabaseEvent;
import com.zweigbergk.speedswede.util.Lists;

import java.util.ArrayList;
import java.util.List;

import static android.media.CamcorderProfile.get;

public class ChatListAdapter extends BaseAdapter {

    TextView description;

    TextView header;

    ArrayList<Chat> mChats;

    public ChatListAdapter() {

        mChats = new ArrayList<>();
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

        header = (TextView) convertView.findViewById(R.id.header_textView);

        header.setText(mChats.get(position).getId()); //Or the name of the other user?

        description = (TextView) convertView.findViewById(R.id.description_textView);

        List<Message> lastConversation = mChats.get(position).getConversation();

        description.setText(lastConversation.get(lastConversation.size()).getText());


        return convertView;
    }

    public void onListChanged(DataChange<Chat> change) {
        Chat chat = change.getItem();
        DatabaseEvent event = change.getEvent();

        switch (event) {
            case ADDED:
                addChat(chat);
                break;
            case CHANGED:
                // TODO (Is it needed? Probably not)
                break;
            case REMOVED:
                removeChat(chat);
                break;
            case INTERRUPED:
                // TODO
                //Handle failure to respond to a change in the database by creating a listener
                // for connection and call onListChanged() once connection is reestablished
                break;
        }
    }

    public void addChat(Chat chat) {
        mChats.add(chat);
        notifyDataSetChanged();
    }

    public void addChats(List<Chat> chats) {
        Lists.forEach(chats, mChats::add);
        notifyDataSetChanged();
    }

    public void removeChat(Chat chat) {
        mChats.remove(chat);
        notifyDataSetChanged();
    }

}
