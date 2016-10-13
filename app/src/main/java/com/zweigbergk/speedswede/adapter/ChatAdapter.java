package com.zweigbergk.speedswede.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.zweigbergk.speedswede.R;
import com.zweigbergk.speedswede.core.Chat;
import com.zweigbergk.speedswede.core.Message;
import com.zweigbergk.speedswede.core.User;
import com.zweigbergk.speedswede.database.DataChange;
import com.zweigbergk.speedswede.database.DatabaseEvent;
import com.zweigbergk.speedswede.database.DatabaseHandler;
import com.zweigbergk.speedswede.database.LocalStorage;
import com.zweigbergk.speedswede.fragment.ChatListFragment;
import com.zweigbergk.speedswede.util.methodwrapper.Client;
import com.zweigbergk.speedswede.util.Time;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.ViewHolder> {

    public enum Event { CHAT_VIEW_CLICKED, CHAT_REMOVED, CHAT_ADDED }


    public static final String TAG = ChatAdapter.class.getSimpleName().toUpperCase();

    private List<Chat> mChats;
    private Map<Event, List<Client<Chat>>> eventClients;
    private Context mContext;

    public ChatAdapter(List<Chat> chats) {
        eventClients = new HashMap<>();
        mChats = chats;

        for (Event event : Event.values()) {
            eventClients.put(event, new ArrayList<>());
        }
    }

    public ChatAdapter() {
        this(new ArrayList<>());
    }

    public void clear() {
        mChats.clear();
        notifyDataSetChanged();
    }

    public final void notifyChange(DataChange<Chat> change) {
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
                } else {
                    updateChat(chat);
                }
                break;
            case REMOVED:
                removeChat(chat);
                break;
            default:
                break;
        }
    }

    public void addEventClient(Event event, Client<Chat> client) {
        eventClients.get(event).add(client);
    }

    public void removeEventClient(Event event, Client<Chat> client) {
        eventClients.get(event).remove(client);
    }

    private void updateChat(@NonNull Chat chat) {
        int index = mChats.indexOf(chat);
        mChats.set(index, chat);
        notifyItemChanged(index);
    }

    private void addChat(Chat chat) {
        Log.d(TAG, "In addChat");

        if (!mChats.contains(chat)) {
            Log.d(TAG, "Did not contain our chat.");
            mChats.add(chat);
            notifyItemInserted(getItemCount() - 1);
            broadcastEvent(Event.CHAT_ADDED, chat);
        } else {
            Log.d(TAG, "Contained our chat.");
            mChats.set(mChats.indexOf(chat), chat);
            notifyItemChanged(getItemCount() - 1);
        }

        Log.d(TAG, mChats.size() + "");
    }

    private void removeChat(Chat chat) {
        int position = mChats.indexOf(chat);

        Log.d(TAG, "In removeChat, position: " + position);

        mChats.remove(chat);
        notifyItemRemoved(position);

        broadcastEvent(Event.CHAT_REMOVED, chat);
    }

    private void broadcastEvent(Event event, Chat chat) {
        List<Client<Chat>> clients = eventClients.get(event);
        for (Client<Chat> client : clients) {
            client.supply(chat);
        }
    }

    @Override
    public ChatAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        mContext =parent.getContext();
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_chat_list_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Chat chat = mChats.get(position);
        chat.setName(LocalStorage.INSTANCE.getString(mContext,chat.getId(),chat.getName()));

        Message latestMessage = chat.getLatestMessage();

        String messageText = latestMessage != null ? latestMessage.getText() : "";
        String formattedTime = latestMessage != null ? Time.formatMessageDate(latestMessage) : "";

        holder.mName.setText(chat.getName());
        holder.mLatestMessage.setText(messageText);
        holder.mTimestamp.setText(formattedTime);

        holder.mView.setOnClickListener(chatView -> broadcastEvent(Event.CHAT_VIEW_CLICKED, chat));
    }

    @Override
    public int getItemCount() {
        return mChats.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        TextView mName;
        TextView mLatestMessage;
        TextView mTimestamp;
        View mView;

        ViewHolder(View view) {
            super(view);

            mView = view;
            mName = (TextView) view.findViewById(R.id.row_chat_list_item_name);
            mLatestMessage = (TextView) view.findViewById(R.id.row_chat_list_item_latest_message);
            mTimestamp = (TextView) view.findViewById(R.id.row_chat_list_item_timestamp);
        }
    }

    public List<Chat> getChats() {
        return mChats;
    }
}