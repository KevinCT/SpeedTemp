package com.zweigbergk.speedswede.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.zweigbergk.speedswede.R;
import com.zweigbergk.speedswede.core.Message;

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

    public NewMessageAdapter(List<Message> messages) {
        mMessages = messages;
    }

    @Override
    public NewMessageAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                           int viewType) {
        // create a new view
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_message_user, parent, false);
        // set the view's size, margins, paddings and layout parameters

        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        holder.mTextView.setText(mMessages.get(position).getText());

    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mMessages.size();
    }
}