package com.bubble.common.listener;

import android.view.View;

/**
 * @author Bubble
 * @date 2020/7/7
 * @email jiaxiang6595@foxmail.com
 * @Desc 单次点击事件  在一定时间内只响应一次点击事件
 */
public abstract class SingleListener implements View.OnClickListener {
    private final static int DURATION_500 = 500;
    private long mLastTime;
    private int mDuration = DURATION_500;

    public SingleListener() {
    }

    public SingleListener(int duration) {
        mDuration = duration;
    }

    @Override
    public void onClick(View v) {
        long nowTime = System.currentTimeMillis();
        //当前时间 和上次点击超过间隔 就响应点击事件 否则不做响应
        if (nowTime - mLastTime > mDuration) {
            onSingleClick(v);
        }
        mLastTime = nowTime;
    }

    public abstract void onSingleClick(View view);
}
