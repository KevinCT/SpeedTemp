package com.zweigbergk.speedswede.adapter;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.zweigbergk.speedswede.R;
import com.zweigbergk.speedswede.core.Message;
import com.zweigbergk.speedswede.service.DatabaseHandler.DataChange;
import com.zweigbergk.speedswede.service.DatabaseHandler.Event;

import java.util.ArrayList;
import java.util.List;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.ViewHolder> {
    private List<Message> mMessages;

    public MessageAdapter(List<Message> messages) {
        mMessages = messages;
    }

    public MessageAdapter() {
        this(new ArrayList<>());
    }

    public void onListChanged(DataChange<Message> change) {
        Message message = change.getItem();
        Event event = change.getEvent();

        switch (event) {
            case ADDED:
                addMessage(message);
                break;
            case MODIFIED:
                updateMessage(message);
                break;
            case REMOVED:
                removeMessage(message);
                break;
            case CANCELLED:
                // TODO
                //Handle failure to respond to a change in the database by creating a listener
                // for connection and call onListChanged() once connection is reestablished
                break;
        }
    }

    private void updateMessage(@NonNull Message message) {
        int position = mMessages.indexOf(message);

        for (Message messageInList : mMessages) {
            if (isSameMessage(messageInList, message)) {
                messageInList.copyTextFrom(message);
                notifyItemChanged(position);
                return;
            }
        }
    }

    private void addMessage(Message message) {
        mMessages.add(message);
        notifyItemInserted(getItemCount() - 1);
    }

    private void removeMessage(Message message) {
        int position = mMessages.indexOf(message);

        mMessages.remove(message);
        notifyItemRemoved(position);
    }

    private boolean isSameMessage(@NonNull Message message1, Message message2) {
        return message1.equals(message2);
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