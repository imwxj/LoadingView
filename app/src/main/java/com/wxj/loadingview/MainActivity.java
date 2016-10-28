package com.wxj.loadingview;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;

public class MainActivity extends AppCompatActivity {

    private Button btn_start, btn_stop;
    private LoadingView loadingView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        btn_start = (Button) findViewById(R.id.btn_start);
        btn_stop = (Button) findViewById(R.id.btn_stop);
        btn_stop.setEnabled(false);
        btn_start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btn_start.setEnabled(false);
                btn_stop.setEnabled(true);
                showLoading();
            }
        });
        btn_stop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btn_start.setEnabled(true);
                btn_stop.setEnabled(false);
                hideLoading();
            }
        });
    }

    public void showLoading() {
        if (loadingView == null) {
            loadingView = createLoadingView(this);
        }
        loadingView.setIsLoading(true);
    }

    public void hideLoading() {
        if (loadingView != null) {
            loadingView.setIsLoading(false);
        }
    }

    /**
     * 创建一个LoadingView
     * @param context
     * @return LoadingView
     */
    private LoadingView createLoadingView(Context context) {
        LoadingView loadingView = new LoadingView(context);
        FrameLayout frameLayout = (FrameLayout) findViewById(android.R.id.content);
        FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, Gravity.CENTER);
        loadingView.setLayoutParams(layoutParams);
        frameLayout.addView(loadingView);
        return loadingView;
    }
}
