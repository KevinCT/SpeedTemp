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

import java.util.List;

public class NewMessageAdapter extends RecyclerView.Adapter<NewMessageAdapter.ViewHolder> {
    private List<Message> mMessages;

    static class ViewHolder extends RecyclerView.ViewHolder {

        TextView mTextView;
        ViewHolder(View view) {
            super(view);

            mTextView = (TextView) view.findViewById(R.id.message_textview_user);
        }
    }

    public void onListChanged(DataChange<Message> change) {
        Message message = change.getItem();
        Event event = change.getEvent();
        int position = mMessages.indexOf(message);

        switch (event) {
            case ADDED:
                mMessages.add(message);
                notifyItemInserted(getItemCount() - 1);
                break;
            case MODIFIED:
                updateMessage(message);
                notifyItemChanged(position);
                break;
            case REMOVED:
                mMessages.remove(message);
                notifyItemRemoved(position);
            case CANCELLED:
                // TODO
                //Handle failure to respond to a change in the database, maybe by issuing
                //another onListChanged() after a certain time using onListChanged(change);
                break;
        }
    }

    private void updateMessage(@NonNull Message message) {
        for (Message messageInList : mMessages) {
            if (isSameMessage(messageInList, message)) {
                messageInList.copyTextFrom(message);
                notifyDataSetChanged();
                return;
            }
        }
    }

    private boolean isSameMessage(@NonNull Message message1, @NonNull Message message2) {
        return message1.equals(message2);
    }

    public NewMessageAdapter(List<Message> messages) {
        mMessages = messages;
    }

    @Override
    public NewMessageAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
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
}