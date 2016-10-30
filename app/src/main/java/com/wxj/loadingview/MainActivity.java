package com.wxj.loadingview;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;

public class MainActivity extends AppCompatActivity {

    private Button btn_start, btn_stop;
    private TickLoadingView tickLoadingView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(new ECGView(this));
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
        if (tickLoadingView == null) {
            tickLoadingView = createLoadingView(this);
        }
        tickLoadingView.setIsLoading(true);
    }

    public void hideLoading() {
        if (tickLoadingView != null) {
            tickLoadingView.setIsLoading(false);
        }
    }

    /**
     * 创建一个LoadingView
     * @param context
     * @return LoadingView
     */
    private TickLoadingView createLoadingView(Context context) {
        TickLoadingView tickLoadingView = new TickLoadingView(context);
        FrameLayout frameLayout = (FrameLayout) findViewById(android.R.id.content);
        FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, Gravity.CENTER);
        tickLoadingView.setLayoutParams(layoutParams);
        frameLayout.addView(tickLoadingView);
        return tickLoadingView;
    }
}
