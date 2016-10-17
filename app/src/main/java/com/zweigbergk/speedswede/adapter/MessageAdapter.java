package com.zweigbergk.speedswede.adapter;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.zweigbergk.speedswede.R;
import com.zweigbergk.speedswede.activity.Language;
import com.zweigbergk.speedswede.core.Message;
import com.zweigbergk.speedswede.core.User;
import com.zweigbergk.speedswede.database.DatabaseEvent;
import com.zweigbergk.speedswede.database.DataChange;
import com.zweigbergk.speedswede.database.DatabaseHandler;
import com.zweigbergk.speedswede.util.Stringify;
import com.zweigbergk.speedswede.util.Translation;
import com.zweigbergk.speedswede.util.methodwrapper.Client;

import com.zweigbergk.speedswede.util.collection.ArrayList;
import com.zweigbergk.speedswede.util.collection.HashMap;
import com.zweigbergk.speedswede.util.collection.List;
import java.util.Locale;
import com.zweigbergk.speedswede.util.collection.Map;

import com.zweigbergk.speedswede.util.Translation.TranslationCache;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.ViewHolder> {

    public static final String TAG = MessageAdapter.class.getSimpleName().toUpperCase();

    private List<Message> mMessages;
    private Map<DatabaseEvent, List<Client<Message>>> eventCallbacks;

    private final Locale mLocale;


    public MessageAdapter(Locale currentLocale) {
        eventCallbacks = new HashMap<>();
        mMessages = new ArrayList<>();
        mLocale = currentLocale;

        for (DatabaseEvent event : DatabaseEvent.values()) {
            eventCallbacks.put(event, new ArrayList<>());
        }
    }

    public void clear() {
        mMessages.clear();
        notifyDataSetChanged();
    }

    public void onListChanged(DataChange<Message> change) {
        Message message = change.getItem();
        DatabaseEvent event = change.getEvent();

        switch (event) {
            case ADDED:
                addMessage(message);
                break;
            case CHANGED:
                updateMessage(message);
                break;
            case REMOVED:
                removeMessage(message);
                break;
            case INTERRUPTED:
                // TODO
                //Handle failure to respond to a change in the database by creating a listener
                // for connection and call onListChanged() once connection is reestablished
                break;
        }
    }

    public void addEventCallback(DatabaseEvent event, Client<Message> callback) {
        eventCallbacks.get(event).add(callback);
    }

    public void removeEventCallback(DatabaseEvent event, Client<Message> callback) {
        eventCallbacks.get(event).remove(callback);
    }

    private void updateMessage(@NonNull Message message) {

        int position = mMessages.indexOf(message);

        for (Message messageInList : mMessages) {
            if (isSameMessage(messageInList, message)) {
                messageInList.copyTextFrom(message);
                notifyItemChanged(position);

                executeCallbacks(DatabaseEvent.CHANGED, message);
                return;
            }
        }
    }

    private void addMessage(Message message) {
        Log.d(TAG, "adMessage() text: " + message.getText());
        if (!mMessages.contains(message)) {
            message.setText(message.getText());

            mMessages.add(message);
            notifyItemInserted(getItemCount() - 1);

            executeCallbacks(DatabaseEvent.ADDED, message);
        }
    }

    private void removeMessage(Message message) {
        int position = mMessages.indexOf(message);

        Log.d(TAG, "In removeMessage");

        mMessages.remove(message);
        notifyItemRemoved(position);

        executeCallbacks(DatabaseEvent.REMOVED, message);
    }

    private boolean isSameMessage(@NonNull Message message1, Message message2) {
        return message1.equals(message2);
    }

    private void executeCallbacks(DatabaseEvent event, Message message) {
        List<Client<Message>> clients = eventCallbacks.get(event);
        for (Client<Message> client : clients) {
            client.supply(message);
        }
    }

    @Override
    public MessageAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;
        if(viewType == 1) {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.message_user, parent, false);
            return new ViewHolder(view);

        }  else {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.message_stranger, parent, false);
            return new ViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Message message = mMessages.get(position);

        Client<String> updateViewText = translation -> {
            String text = Stringify.curlyFormat("{current text}\n\nTranslation:{translation}",
                    message.getText(), translation);
            holder.mTextView.setText(text);
        };


        if (message.hasCache() && message.getTranslationCache().isFromLocale(mLocale)) {
            String cachedTranslation = message.getTranslationCache().getTranslatedText();
            updateViewText.supply(cachedTranslation);
        } else {
            //No up-to-date cache, must translate message
            Translation.translate(message.getText(), Language.SWEDISH, Language.ENGLISH)
                    .then(translatedText -> {
                        //Update cache of the message
                        TranslationCache cache = Translation.TranslationCache.cache(mLocale.getLanguage(), translatedText);
                        message.setTranslationCache(cache);
                        updateViewText.supply(translatedText);
                    });
        }
    }

    @Override
    public int getItemCount() {
        return mMessages.size();
    }

    @Override
    public int getItemViewType(int position){
        User activeUser = DatabaseHandler.getActiveUser();
        if(mMessages.get(position).getId().equals(activeUser.getUid())){
            return 1;
        }
        else {
            return 2;
        }
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        TextView mTextView;
        ViewHolder(View view) {
            super(view);

            mTextView = (TextView) view.findViewById(R.id.message_textview_user);
        }
    }

}