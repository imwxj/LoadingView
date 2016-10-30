package com.wxj.loadingview;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Interpolator;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathMeasure;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.animation.AccelerateInterpolator;

import java.util.Random;

import static android.content.ContentValues.TAG;

/**
 * Created by wxj on 2016/10/29.
 */

public class ECGView extends View {

    private Path path_ECG;
    private int width, height;
    private float length, left, dst;//心电图长度,起点，间隔
    private Paint whitePaint;
    private ValueAnimator mValueAnimator;

    //监听器
    private ValueAnimator.AnimatorUpdateListener mUpdateListener;
    private ValueAnimator.AnimatorListener mAnimatorListener;
    private PathMeasure mPathMeasure;
    // 用于控制动画状态转换
    private Handler mAnimatorHandler;

    // 默认的动效周期 2s
    private int defaultDuration = 1000;
    // 动画数值
    private float mAnimatorValue = 0;
    private Interpolator interpolator;

    public ECGView(Context context) {
        super(context);
        initPaint();
        initListener();
        initAnimator();
        initHandler();
    }

    private void initHandler() {
        mAnimatorHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                initPath();//重新生成path
                mValueAnimator.start();//重新开始动画
            }
        };
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        Log.e(TAG, "onSizeChanged: w:" + w + "\n h:" + h);
        width = w;
        height = h;
        left = width * 0.8f / 2;
        initPath();
        mValueAnimator.start();
    }

    private void initPaint() {
        whitePaint = new Paint();
        whitePaint.setColor(Color.WHITE);
        whitePaint.setStrokeWidth(10);
        whitePaint.setAntiAlias(true);
        whitePaint.setStyle(Paint.Style.STROKE);
        whitePaint.setStrokeCap(Paint.Cap.ROUND);//画笔笔刷类型，影响始末端轮廓
    }

    private void initPath() {
        mPathMeasure = new PathMeasure();
        float x = 0, y;
        dst = width * 0.8f / 2 / 7;
        path_ECG = new Path();
        path_ECG.moveTo(-left, 0);//起点
        path_ECG.lineTo(0, 0);
        Random mRandom = new Random();
        Log.e(TAG, "initPath: height: " + (long) (height * 0.5 / 2));
        for (int i = 0; i < 5; i++) {//画5个点
            y = (i % 2 == 0 ? 1f : -1f) * mRandom.nextInt((int) (height * 0.5 / 2));//y随机生成，一正一负
            x = x + dst;
            path_ECG.lineTo(x, y);
            Log.e(TAG, "initPath: \n x:" + x + "\n y:" + y);
        }
        x = x + dst;
        path_ECG.lineTo(x, 0);//回到直线
        x = x + dst;
        path_ECG.lineTo(x, 0);//最后一小段直线

        mPathMeasure.setPath(path_ECG, false);
        length = mPathMeasure.getLength();//得到总长度
    }

    private void initListener() {
        mUpdateListener = new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                mAnimatorValue = (float) valueAnimator.getAnimatedValue();
                invalidate();
            }
        };
        mAnimatorListener = new ValueAnimator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {

            }

            @Override
            public void onAnimationEnd(Animator animator) {
                mAnimatorHandler.sendEmptyMessageDelayed(0, 200);//延迟两秒开始新动画
            }

            @Override
            public void onAnimationCancel(Animator animator) {

            }

            @Override
            public void onAnimationRepeat(Animator animator) {

            }
        };

    }

    private void initAnimator() {
        mValueAnimator = ValueAnimator.ofFloat(0, 1).setDuration(defaultDuration);
        mValueAnimator.setInterpolator(new AccelerateInterpolator());
        mValueAnimator.addListener(mAnimatorListener);
        mValueAnimator.addUpdateListener(mUpdateListener);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
//        Log.e(TAG, "onDraw: " + length);
        canvas.drawColor(Color.parseColor("#0082D7"));
        canvas.translate(width / 2, height / 2);
        drawTail(canvas);
    }

    private void drawTail(Canvas canvas) {
        Path path = new Path();
        //圆圈终点为动画进度
        float stop = length * mAnimatorValue;
        // 小尾巴长度 tail = stop - start
        // start = stop - tail = mAnimatorValue * (length - left) + dst
        float start = stop - (mAnimatorValue * (length - left) + dst);
        //Log.e(TAG, "left：" + left + "\nstop:" + stop + "\nlength:" + (stop - left));
        mPathMeasure.getSegment(start, stop, path, true);
        canvas.drawPath(path, whitePaint);
    }
}
