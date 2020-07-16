package com.bubble.reader.widget.draw.impl;

import android.graphics.Canvas;
import android.os.Handler;
import android.os.Message;

import com.bubble.reader.widget.PageView;
import com.bubble.reader.widget.draw.base.DrawHelper;

import java.lang.ref.WeakReference;

/**
 * @author Bubble
 * @date 2020/7/14
 * @email 1337986595@qq.com
 * @GitHub https://github.com/SmallBubble
 * @Gitte https://gitee.com/SmallCatBubble
 * @Desc 绘制加载动画
 */
public abstract class LoadingDrawHelper extends DrawHelper {
    private boolean mLoading;
    protected long mSpeed;

    public LoadingDrawHelper(PageView pageView, int speed) {
        super(pageView);
        mSpeed = speed;
    }

    @Override
    public void init() {
    }

    @Override
    public final void draw(Canvas canvas) {
        if (!mLoading) {
            return;
        }
        canvas.save();
        onDraw(canvas, mPageWidth, mPageHeight);
        canvas.restore();
        mHandler.sendEmptyMessageDelayed(0, mSpeed);
    }

    protected void onDraw(Canvas canvas, int pageWidth, int pageHeight) {

    }

    @Override
    public void recycle() {
        mHandler.removeCallbacksAndMessages(null);
    }

    private LoadingHandler mHandler = new LoadingHandler(this);

    static class LoadingHandler extends Handler {
        private WeakReference<LoadingDrawHelper> mReference;

        public LoadingHandler(LoadingDrawHelper helper) {
            mReference = new WeakReference<>(helper);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            LoadingDrawHelper helper = mReference.get();
            if (helper == null) {
                removeCallbacksAndMessages(null);
            }
            sendEmptyMessageDelayed(0, helper.mSpeed);
        }
    }

    public void startLoading() {
        mLoading = true;
        mHandler.sendEmptyMessageDelayed(0, mSpeed);
        mPageView.invalidate();
    }

    public void stopLoading() {
        mLoading = false;
        mHandler.removeCallbacksAndMessages(null);
    }
}