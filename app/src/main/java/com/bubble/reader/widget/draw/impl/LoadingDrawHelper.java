package com.bubble.reader.widget.draw.impl;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
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
public class LoadingDrawHelper extends DrawHelper {


    private int mAngle;
    private Paint mBackgroundPaint;
    private Paint mForegroundPaint;
    private float mLoadingWidth;
    private float mSweepAngle;
    private long mTime;

    public LoadingDrawHelper(PageView pageView) {
        super(pageView);
    }

    @Override
    public void init() {


        mBackgroundPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mBackgroundPaint.setStyle(Paint.Style.STROKE);
        mBackgroundPaint.setStrokeWidth(16);
        mBackgroundPaint.setColor(Color.parseColor("#454545"));

        mForegroundPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mBackgroundPaint.setStyle(Paint.Style.STROKE);
        mBackgroundPaint.setStrokeWidth(16);
        mBackgroundPaint.setColor(Color.parseColor("#ff0000"));

    }

    @Override
    public void draw(Canvas canvas) {
        canvas.drawCircle(mPageWidth / 2f, mPageHeight / 2f, mLoadingWidth, mBackgroundPaint);
        canvas.drawArc(new RectF(mPageWidth / 2f - mLoadingWidth / 2f, 0, 0, 0), mAngle, mSweepAngle, false, mForegroundPaint);
        mHandler.sendEmptyMessageDelayed(0, mTime);
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
            sendEmptyMessageDelayed(0, helper.mTime);

        }
    }

    public void startLoading() {
        mHandler.sendEmptyMessageDelayed(0, mTime);
        mPageView.invalidate();
    }

    public void stopLoading() {
        mHandler.removeCallbacksAndMessages(null);
    }
}