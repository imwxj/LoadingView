package com.wxj.loadingview;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathMeasure;
import android.graphics.RectF;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;

import static com.wxj.loadingview.TickLoadingView.State.ENDING;
import static com.wxj.loadingview.TickLoadingView.State.LOADING;
import static com.wxj.loadingview.TickLoadingView.State.NONE;
import static com.wxj.loadingview.TickLoadingView.State.STARTING;

/**
 * Created by wxj on 2016/10/21.
 * 自定义loading进度条
 */

public class TickLoadingView extends View {
    private static String TAG = "LoadingView";
    private int width, height;
    private Paint mPaint;
    private Path path_circle, path_tick;

    // 测量Path 并截取部分的工具
    private PathMeasure mMeasure;

    // 默认的动效周期 2s
    private int defaultDuration = 2000;
    // 开始和结束动画为1s
    private int startAndEndDuration = 1000;
    private ValueAnimator mStartingAnimator;
    private ValueAnimator mLoadingAnimator;
    private ValueAnimator mEndingAnimator;

    // 动画数值(用于控制动画状态,因为同一时间内只允许有一种状态出现,具体数值处理取决于当前状态)
    private float mAnimatorValue = 0;

    // 动效过程监听器
    private ValueAnimator.AnimatorUpdateListener mUpdateListener;
    private Animator.AnimatorListener mAnimatorListener;

    // 状态
    public enum State {
        NONE,
        STARTING,
        LOADING,
        ENDING
    }

    // 当前的状态
    private TickLoadingView.State mCurrentState = NONE;

    // 用于控制动画状态转换
    private Handler mAnimatorHandler;

    // 判断是否loading
    private boolean isLoading = false;

    public TickLoadingView(Context context) {
        super(context);
        Log.e(TAG, "LoadingView: ");
        initListener();
        initHandler();
        initAnimator();
        initPaint();
        initPath();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        Log.e(TAG, "onMeasure: ");
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        Log.e(TAG, "onSizeChanged: ");
        width = w;
        height = h;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.translate(width / 2, height / 2);//画布移到中间
        //  Log.e(TAG, "onDraw");
        switch (mCurrentState) {
            case NONE:
                break;
            case STARTING:
                mMeasure.setPath(path_tick, false);
                Path dst = new Path();
                //Log.e(TAG, "mMeasure.getLength() ：" + mMeasure.getLength());
                //Log.e(TAG, "mAnimatorValue" + mAnimatorValue);
                //钩子的起点从左边往右边缩减，终点不变
                mMeasure.getSegment(mMeasure.getLength() * mAnimatorValue, mMeasure.getLength(), dst, true);
                canvas.drawPath(dst, mPaint);
                break;
            case LOADING:
                //Log.e(TAG, "LOADING");
                mMeasure.setPath(path_circle, false);
                Path dst2 = new Path();
                //圆圈终点为动画进度
                float stop = mMeasure.getLength() * mAnimatorValue;
                // 小尾巴长度 length = stop - start = (0.5 - Math.abs(mAnimatorValue - 0.5)) * 200f
                // (0.5 - Math.abs(mAnimatorValue - 0.5)) 在中点的时候为0.5，起点和终点为0
                // 所以小尾巴长度在起点和终点为0，最大在中点，为100f
                float start = (float) (stop - ((0.5 - Math.abs(mAnimatorValue - 0.5)) * 200f));
                //Log.e(TAG, "start：" + start + "\nstop:" + stop + "\nlength:" + (stop - start));
                mMeasure.getSegment(start, stop, dst2, true);
                canvas.drawPath(dst2, mPaint);
                break;
            case ENDING:
                //Log.e(TAG, "ENDING");
                mMeasure.setPath(path_tick, false);
                Path dst3 = new Path();
                mMeasure.getSegment(mMeasure.getLength() * mAnimatorValue, mMeasure.getLength(), dst3, true);
                canvas.drawPath(dst3, mPaint);
                mMeasure.setPath(path_circle, false);
                Path dst4 = new Path();
                mMeasure.getSegment(0, mMeasure.getLength() * (1 - mAnimatorValue), dst4, true);
                canvas.drawPath(dst4, mPaint);
                break;
        }
    }

    private void initPaint() {
        mPaint = new Paint();
        mPaint.setAntiAlias(false);
        mPaint.setColor(Color.parseColor("#0082D7"));
        mPaint.setStrokeWidth(15);
        mPaint.setStrokeCap(Paint.Cap.ROUND);//画笔笔刷类型，影响始末端轮廓
        mPaint.setStyle(Paint.Style.STROKE);
    }

    private void initPath() {
        mMeasure = new PathMeasure();

        path_circle = new Path();//外部圆环
        path_tick = new Path();//钩子

        float radius = 100f;//半径

        RectF rectF = new RectF(-radius, -radius, radius, radius);
        path_circle.addArc(rectF, 330, 359.9f);

        mMeasure.setPath(path_circle, false);

        float[] pos = new float[2];
        mMeasure.getPosTan(0, pos, null);//得到圆形起点的坐标

        path_tick.moveTo(-radius / 2 * 1.1f, 0);//钩子左边
        path_tick.lineTo(0, radius / 2 * 1.03f);
        path_tick.lineTo(pos[0], pos[1]);//钩子右边

//        Log.e(TAG, "pos=" + pos[0] + ":" + pos[1]);
//        Log.e(TAG, "radius=" + radius);
    }

    private void initListener() {
        mUpdateListener = new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                mAnimatorValue = (float) animation.getAnimatedValue();
                invalidate(); //得到当前进度，重绘
            }
        };

        mAnimatorListener = new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                mAnimatorHandler.sendEmptyMessage(0);//动画播放结束
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        };
    }

    private void initHandler() {
        mAnimatorHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                switch (mCurrentState) {
                    case STARTING:
                        mCurrentState = LOADING;
                        mLoadingAnimator.start(); // 从开始动画转换到搜索动画
                        break;
                    case LOADING:
                        if (isLoading) {//如果还在loading，继续播放动画
                            mLoadingAnimator.start();
                            // Log.e(TAG, "Restart");
                        } else {
                            mCurrentState = ENDING;
                            mEndingAnimator.start();
                        }
                        break;
                    case ENDING:
                        mCurrentState = NONE;
                        setVisibility(GONE);
                        break;
                }
            }
        };
    }

    private void initAnimator() {
        mStartingAnimator = ValueAnimator.ofFloat(0, 1).setDuration(startAndEndDuration);
        mLoadingAnimator = ValueAnimator.ofFloat(0, 1).setDuration(defaultDuration);
        mEndingAnimator = ValueAnimator.ofFloat(1, 0).setDuration(startAndEndDuration);

        mStartingAnimator.addUpdateListener(mUpdateListener);
        mLoadingAnimator.addUpdateListener(mUpdateListener);
        mEndingAnimator.addUpdateListener(mUpdateListener);

        mStartingAnimator.addListener(mAnimatorListener);
        mLoadingAnimator.addListener(mAnimatorListener);
        mEndingAnimator.addListener(mAnimatorListener);
    }

    public void setIsLoading(boolean isLoading) {
        Log.e(TAG, "setIsLoading: " + isLoading);
        this.isLoading = isLoading;
        if (isLoading) {
            setVisibility(VISIBLE);
            mCurrentState = STARTING;
            mStartingAnimator.start();
        }
    }
}
