package com.zweigbergk.speedswede.adapter;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.zweigbergk.speedswede.R;
import com.zweigbergk.speedswede.core.Chat;
import com.zweigbergk.speedswede.core.User;
import com.zweigbergk.speedswede.database.DataChange;
import com.zweigbergk.speedswede.database.DatabaseEvent;
import com.zweigbergk.speedswede.database.DatabaseHandler;
import com.zweigbergk.speedswede.util.Client;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class YetAnotherChatAdapter extends RecyclerView.Adapter<YetAnotherChatAdapter.ViewHolder> {

    public static final String TAG = YetAnotherChatAdapter.class.getSimpleName().toUpperCase();
    private static final int NORMAL_VIEW = 1;

    private List<Chat> mChats;
    private User mUser;
    private Map<DatabaseEvent, List<Client<Chat>>> eventCallbacks;


    public YetAnotherChatAdapter(List<Chat> chats) {
        mUser = DatabaseHandler.getInstance().getActiveUser();
        eventCallbacks = new HashMap<>();
        mChats = chats;

        for (DatabaseEvent event : DatabaseEvent.values()) {
            eventCallbacks.put(event, new ArrayList<>());
        }
    }

    public YetAnotherChatAdapter() {
        this(new ArrayList<>());
    }

    public void clear() {
        mChats.clear();
        notifyDataSetChanged();
    }

    public void notifyChange(DataChange<Chat> change) {
        Chat chat = change.getItem();
        DatabaseEvent event = change.getEvent();

        switch (event) {
            case ADDED:
                addChat(chat);
                break;
            case CHANGED:
                User activeUser = DatabaseHandler.getInstance().getActiveUser();
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

    public void onListChanged(DataChange<Chat> change) {
        Chat chat = change.getItem();
        DatabaseEvent event = change.getEvent();

        switch (event) {
            case ADDED:
                addChat(chat);
                break;
            case CHANGED:
                updateChat(chat);
                break;
            case REMOVED:
                removeChat(chat);
                break;
            case INTERRUPTED:
                // TODO
                //Handle failure to respond to a change in the database by creating a listener
                // for connection and call onListChanged() once connection is reestablished
                break;
        }
    }

    public void addEventCallback(DatabaseEvent event, Client<Chat> callback) {
        eventCallbacks.get(event).add(callback);
    }

    public void removeEventCallback(DatabaseEvent event, Client<Chat> callback) {
        eventCallbacks.get(event).remove(callback);
    }

    private void updateChat(@NonNull Chat chat) {
        int index = mChats.indexOf(chat);
        mChats.set(index, chat);
        notifyItemChanged(index);
    }

    private void addChat(Chat chat) {
        Log.d(TAG, "In addChat");

        if (!mChats.contains(chat)) {
            mChats.add(chat);
            notifyItemInserted(getItemCount() - 1);
        }

        executeCallbacks(DatabaseEvent.ADDED, chat);
    }

    private void removeChat(Chat chat) {
        int position = mChats.indexOf(chat);

        Log.d(TAG, "In removeChat");

        mChats.remove(chat);
        notifyItemRemoved(position);

        executeCallbacks(DatabaseEvent.REMOVED, chat);
    }

    private boolean isSameChat(@NonNull Chat chat1, Chat chat2) {
        return chat1.equals(chat2);
    }

    private void executeCallbacks(DatabaseEvent event, Chat chat) {
        List<Client<Chat>> clients = eventCallbacks.get(event);
        for (Client<Chat> client : clients) {
            client.supply(chat);
        }
    }

    @Override
    public YetAnotherChatAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_chat_list_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.mName.setText(mChats.get(position).getName());
        holder.mLatestMessage.setText(mChats.get(position).getLatestMessage());
        holder.mTimestamp.setText(mChats.get(position).getReadableTime());

    }

    @Override
    public int getItemCount() {
        return mChats.size();
    }

    @Override
    public int getItemViewType(int position) {
            return NORMAL_VIEW;
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        TextView mName;
        TextView mLatestMessage;
        TextView mTimestamp;

        ViewHolder(View view) {
            super(view);

            mName = (TextView) view.findViewById(R.id.row_chat_list_item_name);
            mLatestMessage = (TextView) view.findViewById(R.id.row_chat_list_item_latest_message);
            mTimestamp = (TextView) view.findViewById(R.id.row_chat_list_item_timestamp);
        }
    }

}