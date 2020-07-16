package com.bubble.reader.widget.draw.impl;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;

import com.bubble.common.utils.Dp2PxUtil;
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
    private final static int ANGLE_360 = 360;
    private int mAngle = 0;
    private Paint mBackgroundPaint;
    private Paint mForegroundPaint;
    private float mLoadingWidth = Dp2PxUtil.dip2px(24);
    private float mSweepAngle = 0;
    private RectF mRectF;

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

        mRectF = new RectF(mPageWidth / 2f - mLoadingWidth / 2f, 0, 0, 0);
    }

    @Override
    protected void onDraw(Canvas canvas, int pageWidth, int pageHeight) {
        super.onDraw(canvas, pageWidth, pageHeight);
        canvas.drawArc(mRectF, mAngle, mSweepAngle, false, mForegroundPaint);
    }

    @Override
    protected void updateValue() {
        super.updateValue();
        mAngle += 5;
        mSweepAngle += 5;
        if (mSweepAngle > ANGLE_360) {
            mSweepAngle = mSweepAngle - ANGLE_360;
        }
    }
}
