package com.zweigbergk.speedswede.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.zweigbergk.speedswede.R;
import com.zweigbergk.speedswede.util.methodwrapper.Client;

public class DialogFragment extends android.support.v4.app.DialogFragment {
    private EditText mChatNameTxt;
    private Button mChangeNameBtn;
    private Button mCancelBtn;
    private Client<String> client;

    public DialogFragment(){
        //empty constructor needed
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_dialog, container);
        getDialog().setTitle(R.string.change_chat_name);
        client = (Client) getParentFragment();
        initView(view);
        initListener();

        return view;
    }

    private void initView(View view){
        mChatNameTxt = (EditText) view.findViewById(R.id.chatNameText);
        mChatNameTxt.setText("");
        mChangeNameBtn = (Button) view.findViewById(R.id.changeNameBtn);
        mCancelBtn = (Button) view.findViewById(R.id.cancelBtn);
    }

    private void initListener(){
        mChangeNameBtn.setOnClickListener(view -> {
            if(!mChatNameTxt.getText().toString().equals("")) {
                client.supply(mChatNameTxt.getText().toString());
                dismiss();
            }
            else{
                mChatNameTxt.setHint(R.string.chat_name_error);
            }
        });
        mCancelBtn.setOnClickListener(view -> dismiss());
    }

}
