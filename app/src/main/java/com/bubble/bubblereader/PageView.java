package com.bubble.bubblereader;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PointF;
import android.os.Build;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.Nullable;

/**
 * @author Bubble
 * @date 2020/7/15
 * @email 1337986595@qq.com
 * @GitHub https://github.com/SmallBubble
 * @Gitte https://gitee.com/SmallCatBubble
 * @Desc TODO
 */
public class PageView extends View {
    PointF mPointA = new PointF();
    PointF mPointB = new PointF();
    PointF mPointC = new PointF();
    PointF mPointD = new PointF();
    PointF mPointE = new PointF();
    PointF mPointF = new PointF();
    PointF mPointG = new PointF();
    PointF mPointH = new PointF();
    PointF mPointI = new PointF();
    PointF mPointJ = new PointF();
    PointF mPointK = new PointF();
    Path mPathFront;
    Path mPathBack;
    Path mPathNext;
    private int mWidth = 1080;
    private int mHeight = 1920;
    private Paint mPaint;

    public PageView(Context context) {
        this(context, null);
    }

    public PageView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public PageView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        getFrontAreaPath();
        getBackAreaPath();
        getNextAreaPath();
        canvas.drawPath(mPathFront, mPaint);
        mPaint.setColor(Color.GREEN);
        canvas.drawPath(mPathBack, mPaint);
        mPaint.setColor(Color.BLUE);
        canvas.drawPath(mPathNext, mPaint);
//        canvas.drawCircle(mPointI.x, mPointI.y, 30, mPaint);
//        canvas.drawCircle(mPointD.x, mPointD.y, 30, mPaint);
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                if (event.getY() > mHeight / 2) {
                    //右下角
                    mPointF.set(mWidth, mHeight);
                } else {
                    // 右上角
                    mPointF.set(mWidth, 0);
                }
                mPointA.set(event.getX(), event.getY());
                break;
            case MotionEvent.ACTION_MOVE:
                mPointA.set(event.getX(), event.getY());
                checkPointC();
                break;
            case MotionEvent.ACTION_UP:
                break;
            default:
        }
        calcPoints();
        invalidate();
        return true;
    }

    private void getFrontAreaPath() {
        mPathFront.reset();
        if (mPointF.y == 0) {
            mPathFront.moveTo(0, 0);
            mPathFront.lineTo(0, mHeight);
            mPathFront.lineTo(mWidth, mHeight);
            mPathFront.lineTo(mPointJ.x, mPointJ.y);
            mPathFront.quadTo(mPointH.x, mPointH.y, mPointK.x, mPointK.y);
            mPathFront.lineTo(mPointA.x, mPointA.y);
            mPathFront.lineTo(mPointB.x, mPointB.y);
            mPathFront.quadTo(mPointE.x, mPointE.y, mPointC.x, mPointC.y);
            mPathFront.close();
        } else {
            mPathFront.moveTo(0, 0);
            mPathFront.lineTo(0, mHeight);
            mPathFront.lineTo(mPointC.x, mPointC.y);
            mPathFront.quadTo(mPointE.x, mPointE.y, mPointB.x, mPointB.y);
            mPathFront.lineTo(mPointA.x, mPointA.y);
            mPathFront.lineTo(mPointK.x, mPointK.y);
            mPathFront.quadTo(mPointH.x, mPointH.y, mPointJ.x, mPointJ.y);
            mPathFront.lineTo(mWidth, 0);
        }
        mPathFront.close();
    }

    private void getBackAreaPath() {
        mPathBack.reset();
        mPathBack.moveTo(mPointD.x, mPointD.y);
        mPathBack.lineTo(mPointI.x, mPointI.y);
        mPathBack.lineTo(mPointK.x, mPointK.y);
        mPathBack.lineTo(mPointA.x, mPointA.y);
        mPathBack.lineTo(mPointB.x, mPointB.y);
        mPathBack.close();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            mPathBack.op(mPathFront, Path.Op.DIFFERENCE);
        }
    }

    /**
     * 检查c点是否超出范围 炒作重新设置a点并计算各点坐标
     */
    private void checkPointC() {
        if (mPointC.x >= 0) {
            return;
        }
        int width1 = mPointF.y > 0 ? (int) (mPointF.x - mPointA.x) : (int) mPointA.x;
        int width2 = mPointF.y > 0 ? (int) (mWidth - mPointC.x) : (int) (width1 - mPointC.x);
        int height2 = mPointF.y > 0 ? (int) (mHeight - mPointA.y) : (int) mPointA.y;
        int height1 = width1 * height2 / width2;
        mPointA.set(mHeight - height1, mHeight - height1);
        calcPoints();
    }

    private void getNextAreaPath() {
        mPathNext.reset();
        mPathNext.moveTo(mPointC.x, mPointC.y);
        mPathNext.lineTo(mPointF.x, mPointF.y);
        mPathNext.lineTo(mPointJ.x, mPointJ.y);
        mPathNext.close();
        Path path = new Path();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            path.op(mPathFront, Path.Op.UNION);
            path.op(mPathBack, Path.Op.UNION);
            mPathNext.op(path, Path.Op.DIFFERENCE);
        }
    }

    private void init() {
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setColor(Color.YELLOW);

        mPathFront = new Path();
        mPathBack = new Path();
        mPathNext = new Path();
        mPointA.set(680, 1420);
        mPointF.set(mWidth, mHeight);
        calcPoints();


    }

    /**
     * 计算各点坐标
     */
    private void calcPoints() {
        mPointG.x = (mPointA.x + mPointF.x) / 2;
        mPointG.y = (mPointA.y + mPointF.y) / 2;

        mPointE.x = mPointG.x - (mPointF.y - mPointG.y) * (mPointF.y - mPointG.y) / (mPointF.x - mPointG.x);
        mPointE.y = mPointF.y;

        mPointH.x = mPointF.x;
        mPointH.y = mPointG.y - (mPointF.x - mPointG.x) * (mPointF.x - mPointG.x) / (mPointF.y - mPointG.y);

        mPointC.x = mPointE.x - (mPointF.x - mPointE.x) / 2;
        mPointC.y = mPointF.y;

        mPointJ.x = mPointF.x;
        mPointJ.y = mPointH.y - (mPointF.y - mPointH.y) / 2;

        mPointB = getIntersectionPoint(mPointA, mPointE, mPointC, mPointJ);
        mPointK = getIntersectionPoint(mPointA, mPointH, mPointC, mPointJ);

        mPointD.x = (mPointC.x + 2 * mPointE.x + mPointB.x) / 4;
        mPointD.y = (2 * mPointE.y + mPointC.y + mPointB.y) / 4;

        mPointI.x = (mPointJ.x + 2 * mPointH.x + mPointK.x) / 4;
        mPointI.y = (2 * mPointH.y + mPointJ.y + mPointK.y) / 4;
    }

    /**
     * 计算两线段相交点坐标
     *
     * @param lineOneMyPointOne
     * @param lineOneMyPointTwo
     * @param lineTwoMyPointOne
     * @param lineTwo_My_pointTwo
     * @return 返回该点
     */
    private PointF getIntersectionPoint(PointF lineOneMyPointOne, PointF lineOneMyPointTwo, PointF lineTwoMyPointOne, PointF lineTwo_My_pointTwo) {
        float x1, y1, x2, y2, x3, y3, x4, y4;
        x1 = lineOneMyPointOne.x;
        y1 = lineOneMyPointOne.y;
        x2 = lineOneMyPointTwo.x;
        y2 = lineOneMyPointTwo.y;
        x3 = lineTwoMyPointOne.x;
        y3 = lineTwoMyPointOne.y;
        x4 = lineTwo_My_pointTwo.x;
        y4 = lineTwo_My_pointTwo.y;
        float pointX = ((x1 - x2) * (x3 * y4 - x4 * y3) - (x3 - x4) * (x1 * y2 - x2 * y1))
                / ((x3 - x4) * (y1 - y2) - (x1 - x2) * (y3 - y4));
        float pointY = ((y1 - y2) * (x3 * y4 - x4 * y3) - (x1 * y2 - x2 * y1) * (y3 - y4))
                / ((y1 - y2) * (x3 - x4) - (x1 - x2) * (y3 - y4));
        return new PointF(pointX, pointY);
    }
}
