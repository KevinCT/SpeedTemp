package com.zweigbergk.speedswede.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.zweigbergk.speedswede.core.Message;

import java.util.ArrayList;
import java.util.List;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.ViewHolder> {
    private List<Message> messageList;

    public MessageAdapter(){
        messageList = new ArrayList<>();
    }
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        //waiting for layouts.
        //View messageView = LayoutInflater.from(parent.getContext()).inflate(R.,parent,false);
        //ViewHolder viewHolder = new ViewHolder(messageView);
        return null;

    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Message message = messageList.get(position);
        holder.nameView.setText(message.getName());
        holder.textView.setText(message.getText());
        holder.timeStampView.setText("" + message.getTimeStamp());

    }

    @Override
    public int getItemCount() {
        return messageList.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView nameView;
        private TextView textView;
        private TextView timeStampView;
        public ViewHolder(View itemView) {
            super(itemView);
            /*message layout not available yet
            nameView =(TextView) itemView.findViewById(R.id.cast_notification_id);
            textView =(TextView) itemView.findViewById(R.id.cast_notification_id);
            timeStampView =(TextView) itemView.findViewById(R.id.cast_notification_id);
            */


        }
    }
}
