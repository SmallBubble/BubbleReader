package com.bubble.reader.widget.draw.impl;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;

import com.bubble.reader.widget.PageView;

/**
 * @author Bubble
 * @date 2020/7/16
 * @email 1337986595@qq.com
 * @GitHub https://github.com/SmallBubble
 * @Gitte https://gitee.com/SmallCatBubble
 * @Desc TODO
 */
public class DefaultLoadingDrawHelper extends LoadingDrawHelper {
    private int mAngle;
    private Paint mBackgroundPaint;
    private Paint mForegroundPaint;
    private float mLoadingWidth;
    private float mSweepAngle;

    public DefaultLoadingDrawHelper(PageView pageView, int speed) {
        super(pageView, speed);
    }

    @Override
    public void init() {
        super.init();
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
    protected void onDraw(Canvas canvas, int pageWidth, int pageHeight) {
        super.onDraw(canvas, pageWidth, pageHeight);
        canvas.drawCircle(mPageWidth / 2f, mPageHeight / 2f, mLoadingWidth, mBackgroundPaint);
        canvas.drawArc(new RectF(mPageWidth / 2f - mLoadingWidth / 2f, 0, 0, 0), mAngle, mSweepAngle, false, mForegroundPaint);
    }
}
