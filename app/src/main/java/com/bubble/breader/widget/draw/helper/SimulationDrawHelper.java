package com.bubble.breader.widget.draw.helper;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PointF;
import android.os.Build;
import android.view.MotionEvent;
import android.view.animation.LinearInterpolator;
import android.widget.Scroller;

import com.bubble.common.log.BubbleLog;
import com.bubble.breader.widget.PageView;
import com.bubble.breader.widget.draw.base.PageDrawHelper;

/**
 * @author Bubble
 * @date 2020/6/21
 * @email 1337986595@qq.com
 * @GitHub https://github.com/SmallBubble
 * @Gitte https://gitee.com/SmallCatBubble
 * @Desc 仿真翻页
 */
public class SimulationDrawHelper extends PageDrawHelper {

    private static final String TAG = SimulationDrawHelper.class.getSimpleName();
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

    private Scroller mScroller;
    private boolean mRunning;
    private Path mTempPath;

    private Matrix mMatrix;


    enum Op {
        /**
         * 取消 静止
         */
        CANCEL,
        /**
         * 顶部
         */
        TOP,
        /**
         * 底部
         */
        BOTTOM,
        /**
         * 右边
         */
        RIGHT,
        /**
         * 左边
         */
        LEFT
    }

    private Op mOp = Op.CANCEL;

    public SimulationDrawHelper(PageView pageView) {
        super(pageView);
    }

    @Override
    protected void initData() {
        mScroller = new Scroller(mPageView.getContext(), new LinearInterpolator());

        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setColor(Color.YELLOW);
        mPaint.setTextSize(64);
        mPathFront = new Path();
        mPathBack = new Path();
        mPathNext = new Path();
        mTempPath = new Path();
        calcPoints();
        mMatrix = new Matrix();
    }


    @Override
    public void onTouchEvent(PageView view, MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mScroller.abortAnimation();
                mRunning = false;
                mStartPoint.set(event.getX(), event.getY());
                mPointA.set(event.getX(), event.getY());
                break;
            case MotionEvent.ACTION_MOVE:
                // 获取横线滑动的距离
                int moveX = (int) (event.getX() - mStartPoint.x);
                if (moveX > 0) {
                    // 右滑 上一页
                    mHasNext = mOnContentListener.onPrePage();
                    mNext = false;
                } else {
                    // 左滑下一页
                    mHasNext = mOnContentListener.onNextPage();
                    mNext = true;
                    // 判断是右上角 中间还是右下角
                    if (event.getY() > mPageHeight / 3f * 2) {
                        mOp = Op.BOTTOM;
                        //右下角
                        mPointF.set(mPageWidth, mPageHeight);
                    } else if (event.getY() < mPageHeight / 3f) {
                        mOp = Op.TOP;
                        // 右上角
                        mPointF.set(mPageWidth, 0);
                    } else {
                        mOp = Op.RIGHT;
                        // 横向翻页
                    }
                }
                // 没有新的一页 没有翻页效果
                if (mHasNext == null || !mHasNext.isHasNext()) {
                    return;
                }
                mRunning = true;
                mPointA.set(event.getX(), event.getY());
                mPageView.invalidate();
                break;
            case MotionEvent.ACTION_UP:
//                startCancel();
                mOp = Op.CANCEL;
                break;
            default:
        }
    }

    @Override
    public void computeScroll() {
        super.computeScroll();
        if (mScroller != null && mScroller.computeScrollOffset()) {
            if (mScroller.getCurrX() == mScroller.getFinalX() && mScroller.getCurrY() == mScroller.getFinalY()) {
                mCancel = true;
                mRunning = false;
                return;
            }
            mPointA.set(mScroller.getCurrX(), mScroller.getCurrY());
            calcPoints();
            if (mPointC.x < 0) {
                checkPointC();
            }
            mPageView.invalidate();
        }

    }

    /**
     * 取消翻页
     */
    private void startCancel() {
        if (mOp == Op.TOP) {
            int dx = (int) (mPointF.x - mPointA.x);
            int dy = -(int) (mPointA.y);
            mScroller.startScroll((int) mPointA.x, (int) mPointA.y, dx, dy, 200);
        } else {
            int dx = (int) (mPointF.x - mPointA.x);
            int dy = mPageHeight - (int) (mPointA.y);
            mScroller.startScroll((int) mPointA.x, (int) mPointA.y, dx, dy, 200);
        }
        mPageView.invalidate();
    }

    @Override
    public void draw(Canvas canvas) {
        // 绘制翻页
        if (isRunning()) {
            BubbleLog.e(TAG, "draw drawPage");
            onDrawPage(canvas);
        } else {
            drawPage(mPageView.getCurrentPage());
            drawPage(mPageView.getNextPage());
            BubbleLog.e(TAG, "draw drawStatic");
            onDrawStatic(canvas);
        }
    }

    @Override
    public void onDrawPage(Canvas canvas) {
        if (mOp == Op.RIGHT) {
            mPointA.y = mPageHeight - 5;
        }
        calcPoints();
        if (mPointC.x < 0) {
            checkPointC();
        }
        getPath();
        drawFront(canvas);
        drawBack(canvas);
        drawNext(canvas);
    }

    private void getPath() {
        getFrontAreaPath();
        getBackAreaPath();
        getNextAreaPath();
    }

    @Override
    public boolean isRunning() {
        return mRunning;
    }

    private void drawFront(Canvas canvas) {
        // 裁剪出正面
        canvas.save();
        mPaint.setColor(Color.YELLOW);
        canvas.drawPath(mPathFront, mPaint);
        canvas.clipPath(mPathFront);
        mPaint.setColor(Color.BLACK);

//        canvas.drawText("AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA", 0, mPageHeight - 100, mPaint);
        canvas.drawBitmap(mPageView.getCurrentPage().getBitmap(), 0, 0, mPaint);
        canvas.restore();
    }

    private void drawBack(Canvas canvas) {
        // 裁剪出正面
        canvas.save();
        mPaint.setColor(Color.GREEN);
        canvas.clipPath(mPathBack);
        canvas.drawPath(mPathBack, mPaint);
        canvas.restore();
    }

    private void drawNext(Canvas canvas) {
        // 裁剪出下一页
        canvas.save();
        mPaint.setColor(Color.BLUE);
        canvas.drawPath(mPathNext, mPaint);
        mTempPath.reset();
        canvas.clipPath(mPathNext);
        mPaint.setColor(Color.BLACK);
        canvas.drawText("BBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBB", 0, mPageHeight - 100, mPaint);
        canvas.drawBitmap(mPageView.getNextPage().getBitmap(), 0, 0, null);
        canvas.restore();
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
     * 获取下一页的显示区域
     */
    private void getNextAreaPath() {
        mPathNext.reset();
        mPathNext.moveTo(mPointC.x, mPointC.y);
        mPathNext.lineTo(mPointF.x, mPointF.y);
        mPathNext.lineTo(mPointJ.x, mPointJ.y);
        mPathNext.close();
        mTempPath.reset();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            mTempPath.op(mPathFront, Path.Op.UNION);
            mTempPath.op(mPathBack, Path.Op.UNION);
            mPathNext.op(mTempPath, Path.Op.DIFFERENCE);
        }
    }

    /**
     * 处理背面区域
     * 旋转 翻转 平移
     */
    private void dealBackContent() {
        mMatrix.setTranslate(0, 0);

        float[] matrixArray = new float[]{0, 0, 0, 0, 0, 0, 0, 0, 1f};
        float sin0 = 2;
        mMatrix.setValues(matrixArray);
    }

    /**
     * 检查c点是否超出范围 炒作重新设置a点并计算各点坐标
     */
    private void checkPointC() {
        if (mPointC.x >= 0) {
            return;
        }

        int c1ToF = (int) (mPageWidth - mPointC.x);
        int c1ToN = (int) (mPointA.x - mPointC.x);
        int c2ToF = mPageWidth;
        //  c1ToN       c2ToM
        // ———————— =  ————————
        //  c1ToF       c2ToF
        int c2ToM = c1ToN * c2ToF / c1ToF;
        //  c1ToN       a2ToM
        // ———————— =  ————————
        //  c2ToM       a1ToN

        int a1ToN = mPointF.y == 0 ? (int) mPointA.y : (int) (mPageHeight - mPointA.y);
        int a2ToM = c2ToM * a1ToN / c1ToN;
        mPointA.set(c2ToM, mPointF.y == 0 ? a2ToM : mPageHeight - a2ToM);
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