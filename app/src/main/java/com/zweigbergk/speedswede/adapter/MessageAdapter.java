package com.zweigbergk.speedswede.adapter;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.zweigbergk.speedswede.R;
import com.zweigbergk.speedswede.core.Message;
import com.zweigbergk.speedswede.service.ConversationEvent;
import com.zweigbergk.speedswede.service.DatabaseHandler.DataChange;
import com.zweigbergk.speedswede.util.Client;
import com.zweigbergk.speedswede.util.Executable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.ViewHolder> {
    private List<Message> mMessages;

    private Map<ConversationEvent, List<Client<Message>>> eventCallbacks;


    public MessageAdapter(List<Message> messages) {
        eventCallbacks = new HashMap<>();
        mMessages = messages;

        for (ConversationEvent event : ConversationEvent.values()) {
            eventCallbacks.put(event, new ArrayList<>());
        }
    }

    public MessageAdapter() {
        this(new ArrayList<>());
    }

    public void onListChanged(DataChange<Message> change) {
        Message message = change.getItem();
        ConversationEvent event = change.getEvent();

        switch (event) {
            case MESSAGE_ADDED:
                addMessage(message);
                break;
            case MESSAGE_MODIFIED:
                updateMessage(message);
                break;
            case MESSAGE_REMOVED:
                removeMessage(message);
                break;
            case INTERRUPED:
                // TODO
                //Handle failure to respond to a change in the database by creating a listener
                // for connection and call onListChanged() once connection is reestablished
                break;
        }
    }

    public void addEventCallback(ConversationEvent event, Client<Message> callback) {
        eventCallbacks.get(event).add(callback);
    }

    public void removeEventCallback(ConversationEvent event, Client<Message> callback) {
        eventCallbacks.get(event).remove(callback);
    }

    private void updateMessage(@NonNull Message message) {
        int position = mMessages.indexOf(message);

        for (Message messageInList : mMessages) {
            if (isSameMessage(messageInList, message)) {
                messageInList.copyTextFrom(message);
                notifyItemChanged(position);

                executeCallbacks(ConversationEvent.MESSAGE_MODIFIED, message);
                return;
            }
        }
    }

    private void addMessage(Message message) {
        mMessages.add(message);
        notifyItemInserted(getItemCount() - 1);

        executeCallbacks(ConversationEvent.MESSAGE_ADDED, message);
    }

    private void removeMessage(Message message) {
        int position = mMessages.indexOf(message);

        mMessages.remove(message);
        notifyItemRemoved(position);

        executeCallbacks(ConversationEvent.MESSAGE_REMOVED, message);
    }

    private boolean isSameMessage(@NonNull Message message1, Message message2) {
        return message1.equals(message2);
    }

    private void executeCallbacks(ConversationEvent event, Message message) {
        List<Client<Message>> clients = eventCallbacks.get(event);
        for (Client<Message> client : clients) {
            client.supply(message);
        }
    }

    @Override
    public MessageAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_message_user, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.mTextView.setText(mMessages.get(position).getText());

    }

    @Override
    public int getItemCount() {
        return mMessages.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        TextView mTextView;
        ViewHolder(View view) {
            super(view);

            mTextView = (TextView) view.findViewById(R.id.message_textview_user);
        }
    }
}