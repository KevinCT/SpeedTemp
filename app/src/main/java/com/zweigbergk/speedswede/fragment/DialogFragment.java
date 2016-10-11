package com.zweigbergk.speedswede.fragment;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.zweigbergk.speedswede.R;
import com.zweigbergk.speedswede.util.ActivityAttachable;

/**
 * Created by kevin on 10/10/2016.
 */

public class DialogFragment extends android.support.v4.app.DialogFragment {
    private EditText mChatNameTxt;
    private Button mChangeNameBtn;
    private Button mCancelBtn;
    private OnDataPass onDataPass;
    public interface OnDataPass{
        public void onDataPass(String data);
    }
    public DialogFragment(){

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_dialog, container);
        getDialog().setTitle("@string/change_chat_name");
        onDataPass = (OnDataPass) getParentFragment();
        initView(view);
        initListener();

        return view;
    }

    private void initView(View view){
       // getDialog().setTitle("ChatName");
        mChatNameTxt = (EditText) view.findViewById(R.id.chatNameText);
        mChatNameTxt.setText("");
        mChangeNameBtn = (Button) view.findViewById(R.id.changeNameBtn);
        mCancelBtn = (Button) view.findViewById(R.id.cancelBtn);
    }

    private void initListener(){
        mChangeNameBtn.setOnClickListener(view -> {
            onDataPass.onDataPass(mChatNameTxt.getText().toString());
            dismiss();
        });
        mCancelBtn.setOnClickListener(view -> dismiss());
    }


}
