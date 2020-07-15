package com.bubble.reader.widget.draw.impl;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PointF;
import android.os.Build;
import android.view.MotionEvent;

import com.bubble.reader.widget.PageView;
import com.bubble.reader.widget.draw.base.PageDrawHelper;

/**
 * @author Bubble
 * @date 2020/6/21
 * @email 1337986595@qq.com
 * @GitHub https://github.com/SmallBubble
 * @Gitte https://gitee.com/SmallCatBubble
 * @Desc 仿真翻页
 */
public class SimulationDrawHelper extends PageDrawHelper {

    private PointF mPointA = new PointF();
    private PointF mPointB = new PointF();
    private PointF mPointC = new PointF();
    private PointF mPointD = new PointF();
    private PointF mPointE = new PointF();
    private PointF mPointF = new PointF();
    private PointF mPointG = new PointF();
    private PointF mPointH = new PointF();
    private PointF mPointI = new PointF();
    private PointF mPointJ = new PointF();
    private PointF mPointK = new PointF();

    private Path mPathFront;
    private Path mPathBack;
    private Path mPathNext;
    private Paint mPaint;

    public SimulationDrawHelper(PageView pageView) {
        super(pageView);
    }

    @Override
    protected void initData() {
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setColor(Color.YELLOW);

        mPathFront = new Path();
        mPathBack = new Path();
        mPathNext = new Path();
        mPointA.set(680, 1420);
        mPointF.set(mPageWidth, mPageHeight);
        calcPoints();
    }

    @Override
    public void onTouchEvent(PageView view, MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                if (event.getY() > mPageHeight / 2f) {
                    //右下角
                    mPointF.set(mPageWidth, mPageHeight);
                } else {
                    // 右上角
                    mPointF.set(mPageWidth, 0);
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
        mPageView.invalidate();
    }

    @Override
    public void onDrawPage(Canvas canvas) {
        getFrontAreaPath();
        getBackAreaPath();
        getNextAreaPath();

        canvas.drawPath(mPathFront, mPaint);

        mPaint.setColor(Color.GREEN);
        canvas.drawPath(mPathBack, mPaint);

        mPaint.setColor(Color.BLUE);
        canvas.drawPath(mPathNext, mPaint);
    }

    /**
     * 获取正面的显示区域
     */
    private void getFrontAreaPath() {
        mPathFront.reset();
        if (mPointF.y == 0) {
            mPathFront.moveTo(0, 0);
            mPathFront.lineTo(0, mPageHeight);
            mPathFront.lineTo(mPageWidth, mPageHeight);
            mPathFront.lineTo(mPointJ.x, mPointJ.y);
            mPathFront.quadTo(mPointH.x, mPointH.y, mPointK.x, mPointK.y);
            mPathFront.lineTo(mPointA.x, mPointA.y);
            mPathFront.lineTo(mPointB.x, mPointB.y);
            mPathFront.quadTo(mPointE.x, mPointE.y, mPointC.x, mPointC.y);
            mPathFront.close();
        } else {
            mPathFront.moveTo(0, 0);
            mPathFront.lineTo(0, mPageHeight);
            mPathFront.lineTo(mPointC.x, mPointC.y);
            mPathFront.quadTo(mPointE.x, mPointE.y, mPointB.x, mPointB.y);
            mPathFront.lineTo(mPointA.x, mPointA.y);
            mPathFront.lineTo(mPointK.x, mPointK.y);
            mPathFront.quadTo(mPointH.x, mPointH.y, mPointJ.x, mPointJ.y);
            mPathFront.lineTo(mPageWidth, 0);
        }
        mPathFront.close();
    }

    /**
     * 获取背面的显示区域
     */
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
        int width2 = mPointF.y > 0 ? (int) (mPageWidth - mPointC.x) : (int) (width1 - mPointC.x);
        int height2 = mPointF.y > 0 ? (int) (mPageHeight - mPointA.y) : (int) mPointA.y;
        int height1 = width1 * height2 / width2;
        mPointA.set(mPageHeight - height1, mPageHeight - height1);
        calcPoints();
    }

    /**
     * 获取下一页的显示区域
     */
    private void getNextAreaPath() {
        mPathNext.reset();
        mPathNext.moveTo(mPointC.x, mPointC.y);
        mPathNext.lineTo(mPointF.x, mPointF.y);
        mPathNext.lineTo(mPointJ.x, mPointJ.y);
        mPathNext.close();
        Path tempPath = new Path();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            tempPath.op(mPathFront, Path.Op.UNION);
            tempPath.op(mPathBack, Path.Op.UNION);
            mPathNext.op(tempPath, Path.Op.DIFFERENCE);
        }
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
     * @param lineOnePointOne 线段1 的点1
     * @param lineOnePointTwo 线段1 的点2
     * @param lineTwoPointOne 线段2 的点1
     * @param lineTwoPointTwo 线段2 的点2
     * @return 返回该点
     */
    private PointF getIntersectionPoint(PointF lineOnePointOne, PointF lineOnePointTwo, PointF lineTwoPointOne, PointF lineTwoPointTwo) {
        float x1, y1, x2, y2, x3, y3, x4, y4;
        x1 = lineOnePointOne.x;
        y1 = lineOnePointOne.y;
        x2 = lineOnePointTwo.x;
        y2 = lineOnePointTwo.y;
        x3 = lineTwoPointOne.x;
        y3 = lineTwoPointOne.y;
        x4 = lineTwoPointTwo.x;
        y4 = lineTwoPointTwo.y;
        float pointX = ((x1 - x2) * (x3 * y4 - x4 * y3) - (x3 - x4) * (x1 * y2 - x2 * y1))
                / ((x3 - x4) * (y1 - y2) - (x1 - x2) * (y3 - y4));
        float pointY = ((y1 - y2) * (x3 * y4 - x4 * y3) - (x1 * y2 - x2 * y1) * (y3 - y4))
                / ((y1 - y2) * (x3 - x4) - (x1 - x2) * (y3 - y4));
        return new PointF(pointX, pointY);
    }
}
