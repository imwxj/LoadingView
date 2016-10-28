# LoadingView
一个仿支付宝的loading 效果
## 预览图
![预览图](https://github.com/OLieWaGundam/LoadingView/blob/master/img/LoadingView.gif)
## 使用
LoadingView中公开了一个方法，设置是否需要loading，所以实际使用中只需把view添加到布局，并给它发开始和结束的信号就行了。  
LoadingView中：
```
public void setIsLoading(boolean isLoading) {
        Log.e(TAG, "setIsLoading: " + isLoading);
        this.isLoading = isLoading;
        if (isLoading) {
            setVisibility(VISIBLE);
            mCurrentState = STARTING;
            mStartingAnimator.start();
        }
    }
```
Activity中：
```
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
            ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, 
            Gravity.CENTER);
    loadingView.setLayoutParams(layoutParams);
    frameLayout.addView(loadingView);
    return loadingView;
}
```
其中LoadingView的大小我写死了，如果需要根据view的大小确定圆圈和钩子的大小的话，一些相关的方法就得写在onSizeChanged()之后。  
此时请记得设置一个flag判断是否第一次进行，因为第一次进行的时候，setIsLoading()方法会比onSizeChange()快，出现一些错误。
