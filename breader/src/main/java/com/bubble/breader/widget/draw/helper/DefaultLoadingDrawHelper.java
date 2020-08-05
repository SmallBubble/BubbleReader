package com.bubble.breader.widget.draw.helper;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;

import com.bubble.breader.utils.Dp2PxUtil;
import com.bubble.breader.widget.PageView;

import java.util.Random;

/**
 * @author Bubble
 * @date 2020/7/16
 * @email 1337986595@qq.com
 * @GitHub https://github.com/SmallBubble
 * @Gitte https://gitee.com/SmallCatBubble
 * @Desc 默认的加载
 */
public class DefaultLoadingDrawHelper extends LoadingDrawHelper {
    private final static int ANGLE_360 = 360;
    private int mAngle = 0;
    private Paint mBackgroundPaint;
    private Paint mForegroundPaint;
    private float mLoadingWidth = Dp2PxUtil.dip2px(72);
    private float mSweepAngle = 90;
    private RectF mRectF;
    private int[] colors = new int[]{Color.YELLOW, Color.GREEN, Color.RED, Color.BLUE};

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
        mForegroundPaint.setStyle(Paint.Style.STROKE);
        mForegroundPaint.setStrokeWidth(12);
        mForegroundPaint.setStrokeCap(Paint.Cap.ROUND);
        mForegroundPaint.setColor(Color.parseColor("#ff0000"));
        mRandom = new Random();
        mRectF = new RectF(mPageWidth / 2f - mLoadingWidth / 2f, mPageHeight / 2f - mLoadingWidth / 2f, mPageWidth / 2f + mLoadingWidth / 2f, mPageHeight / 2f + mLoadingWidth / 2f);
    }

    private Random mRandom;

    @Override
    protected void onDraw(Canvas canvas, int pageWidth, int pageHeight) {
        super.onDraw(canvas, pageWidth, pageHeight);

        canvas.drawArc(mRectF, mAngle, mSweepAngle, false, mForegroundPaint);
    }

    @Override
    protected void updateValue() {
        super.updateValue();

        mAngle += 5;
        mSweepAngle += 2;
        if (mSweepAngle > ANGLE_360) {
            mForegroundPaint.setColor(colors[mRandom.nextInt(colors.length)]);
            mSweepAngle = 0;
        }
    }
}