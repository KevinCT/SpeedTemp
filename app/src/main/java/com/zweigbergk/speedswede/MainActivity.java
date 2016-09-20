package com.zweigbergk.speedswede;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;

import com.zweigbergk.speedswede.interactor.MainInteractor;
import com.zweigbergk.speedswede.presenter.MainPresenter;
import com.zweigbergk.speedswede.view.MainView;

public class MainActivity extends AppCompatActivity implements MainView {

    public interface ViewListener {
        void onLoginClick();
    }

    private ViewListener mViewListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mViewListener= new MainPresenter(this);

        findViewById(R.id.button).setOnClickListener(view -> mViewListener.onLoginClick());
    }


    @Override
    public void updateButtonText(String text) {
        ((Button) findViewById(R.id.button)).setText(text);
    }
}
