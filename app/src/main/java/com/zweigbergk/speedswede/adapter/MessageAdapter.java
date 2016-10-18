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
        Log.d(TAG, "addMessage() text: " + message.getText());
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
            holder.mTextView.setText(message.getText());
            final String newText = holder.mTextView.getText().toString();
            holder.mTextView.setOnClickListener(v -> {
                if (message.isTranslated()) {
                    holder.mTextView.setText(message.getText());
                } else {
                    holder.mTextView.setText(newText + "\n\nTranslation:\n" + translation);
                }
                message.invertIsTranslated();
            });
        };

        if (message.hasCache() && message.getTranslationCache().isFromLocale(mLocale)) {
            String cachedTranslation = message.getTranslationCache().getTranslatedText();
            Log.d(TAG, "Using cached transation. Translated text: " + cachedTranslation);
            updateMessageText(holder, message.getText(), cachedTranslation);
            return;
        }

        //No up-to-date cache, must translate message
        Language currentLanguage = Language.fromString(DatabaseHandler.getActiveUser().getLanguage().getLanguageCode());
        Log.d(TAG, "Current language: " + currentLanguage);

        //We don't need to translate if our language is Swedish
        if (!currentLanguage.equals(Language.SWEDISH)) {
            Translation.translate(message.getText(), Language.SWEDISH, currentLanguage)
                    .then(translatedText -> {
                        //Update cache of the message
                        TranslationCache cache = Translation.TranslationCache.cache(mLocale.getLanguage(), translatedText);
                        message.setTranslationCache(cache);
                        updateMessageText(holder, message.getText(), translatedText);
                    });
        } else {
            updateMessageText(holder, message.getText(), null);
        }
    }

    private void updateMessageText(ViewHolder holder, String text, String translation) {
        String completeText = text;
        if (translation != null) {
            completeText += "\n\nTranslation: " + translation;
        }

        holder.mTextView.setText(completeText);
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