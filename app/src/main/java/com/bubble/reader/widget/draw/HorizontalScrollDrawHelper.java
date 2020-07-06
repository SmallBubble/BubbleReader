package com.bubble.reader.widget.draw;

import android.graphics.Canvas;
import android.graphics.drawable.GradientDrawable;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.animation.LinearInterpolator;
import android.widget.Scroller;

import com.bubble.common.log.BubbleLog;
import com.bubble.reader.widget.PageView;

/**
 * packger：com.bubble.reader.widget.draw
 * auther：Bubble
 * date：2020/6/21
 * email：1337986595@qq.com
 * Desc：水平滑动
 */
public class HorizontalScrollDrawHelper extends DrawHelper {

    private static final String TAG = HorizontalScrollDrawHelper.class.getSimpleName();
    /**
     * 阴影
     */
    private GradientDrawable mShadow;
    /**
     * 阴影渐变色
     */
    private int[] mShadowColor = new int[]{0x66666600, 0x00000000};
    /**
     * 滑动处理
     */
    private Scroller mScroller;
    /**
     * 是否发生移动
     */
    private boolean mMove;
    /**
     * 是否  下一页
     */
    private boolean mNext;
    /**
     * 是否还有内容
     */
    private boolean mHasNext;
    /**
     * 是否在滑动
     */
    private boolean mRunning;
    /**
     * 测量速度
     */
    private VelocityTracker mVelocityTracker;

    public HorizontalScrollDrawHelper(PageView pageView) {
        super(pageView);
    }

    @Override
    protected void initData() {
        mShadow = new GradientDrawable(GradientDrawable.Orientation.LEFT_RIGHT, mShadowColor);
        mScroller = new Scroller(mPageView.getContext(), new LinearInterpolator());
    }

    @Override
    public void onTouchEvent(PageView view, MotionEvent event) {
        if (mVelocityTracker == null) {
            mVelocityTracker = VelocityTracker.obtain();
        }
        mVelocityTracker.addMovement(event);
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                // 结束滑动
                mScroller.abortAnimation();
                mRunning = false;
                mMove = false;
                mTouchPoint.set(0, 0);
                mStartPoint.set(event.getX(), event.getY());
                break;
            case MotionEvent.ACTION_MOVE:
                if (mOnContentListener == null) {
                    //没设置监听 也就获取不了内容  直接返回
                    return;
                }
                mMove = true;
                if (mTouchPoint.equals(0, 0)) {
                    // 没有滑动过
                    if (event.getX() - mStartPoint.x > 0) {
                        // 往右边滑动，翻上一页
                        mNext = false;
                        mHasNext = mOnContentListener.onPrePage(0);
                    } else {
                        // 往左边滑动，翻下一页
                        mNext = true;
                        mHasNext = mOnContentListener.onNextPage(0);
                    }
                }
                // 没有内容了
                if (!mHasNext) {
                    return;
                }
                // 设置当前位置
                mTouchPoint.set(event.getX(), event.getY());
                mRunning = true;
                // 通知绘制新内容
                view.postInvalidate();
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                // 设置当前位置
                mTouchPoint.x = event.getX();
                mTouchPoint.y = event.getY();

                if (mMove) {
                    int dx;
                    // 计算滑动速度
                    mVelocityTracker.computeCurrentVelocity(1, 10f);
                    float xVelocity = mVelocityTracker.getXVelocity();
                    BubbleLog.e(TAG, "xVelocity ====  " + xVelocity);
                    int moveX = (int) (mTouchPoint.x - mStartPoint.x);
                    if (Math.abs(xVelocity) > 1) {
                        mCancel = false;
                        // 滑动速度大于5 翻页
                        if (mNext) {
                            // 翻到下一页
                            dx = -(mPageWidth - Math.abs(moveX));
                        } else {
                            // 翻到上一页
                            dx = mPageWidth - moveX;
                        }
                    } else {
                        mCancel = false;
                        // 不大于5 根据滑动距离判断是否翻页
                        if (Math.abs(moveX) > mPageWidth / 2) {
                            // 滑动距离超过一半 正常翻页
                            if (mNext) {
                                // 翻到下一页
                                dx = -(mPageWidth - Math.abs(moveX));
                            } else {
                                // 翻到上一页
                                dx = mPageWidth - moveX;
                            }
                        } else {
                            mCancel = true;
                            // 滑动距离未到一半 取消翻页
                            dx = -moveX;
                        }
                        mRunning = true;
                    }
                    if (mOnContentListener != null && mCancel) {
                        mOnContentListener.onCancel();
                    }
                    BubbleLog.e(TAG, "moveX：" + moveX + "   滑动距离" + dx);
                    mScroller.startScroll((int) mTouchPoint.x, 0, dx, 0, 200);
                    // 通知绘制新内容
                    view.postInvalidate();
                }
                BubbleLog.e(TAG, "\n\n\n开始：\n\n\n");
                break;
        }
    }

    @Override
    public void onDrawPage(Canvas canvas) {
        BubbleLog.e(TAG, "onDrawPage" + mTouchPoint.x);
        if (!mMove) {
            return;
        }
        // 滑动的距离

        if (mNext) {
            int moveX = (int) (mTouchPoint.x - mStartPoint.x);
            BubbleLog.e(TAG, "onDrawPage moveX" + moveX);
            if (moveX > 0) {
                return;
            }
            // 往左滑 翻下一页
            moveX = Math.abs(moveX);
            // 画当前页
            mSrcRect.set(moveX, 0, mPageWidth, mPageHeight);
            mDestRect.set(0, 0, mPageWidth - moveX, mPageHeight);
            BubbleLog.e(TAG, "onDrawPage mSrcRect" + mSrcRect.toShortString());
            BubbleLog.e(TAG, "onDrawPage mDestRect" + mDestRect.toShortString());
            BubbleLog.e(TAG, "onDrawPage -----------------------------------------------------------------\n\n");
            BubbleLog.e(TAG, "onDrawPage -----------------------------------------------------------------");

            canvas.drawBitmap(mPageView.getCurrentPage().getBitmap(), mSrcRect, mDestRect, null);
            // 画下一页
            mSrcRect.set(mPageWidth - moveX, 0, mPageWidth, mPageHeight);
            mDestRect.set(mPageWidth - moveX, 0, mPageWidth, mPageHeight);
            canvas.drawBitmap(mPageView.getNextPage().getBitmap(), mSrcRect, mDestRect, null);
            //绘制阴影
            drawShadow(mPageWidth - moveX, canvas);
        } else {
            int moveX = (int) (mTouchPoint.x - mStartPoint.x);
            if (moveX < 0) {
                return;
            }

            // 往右滑 翻上一页
            // 上一页
            mSrcRect.set(mPageWidth - moveX, 0, mPageWidth, mPageHeight);
            mDestRect.set(0, 0, moveX, mPageHeight);
            canvas.drawBitmap(mPageView.getNextPage().getBitmap(), mSrcRect, mDestRect, null);
            //当前页
            mSrcRect.set(moveX, 0, mPageWidth, mPageHeight);
            mDestRect.set(moveX, 0, mPageWidth, mPageHeight);
            canvas.drawBitmap(mPageView.getCurrentPage().getBitmap(), mSrcRect, mDestRect, null);
            //绘制阴影
            drawShadow(moveX, canvas);
        }
    }

    /**
     * 绘制阴影
     *
     * @param x      阴影x坐标
     * @param canvas 画布
     */
    private void drawShadow(int x, Canvas canvas) {
        mShadow.setBounds(x, 0, x + 50, mPageHeight);
        mShadow.draw(canvas);
    }

    @Override
    public void onDrawStatic(Canvas canvas) {
        super.onDrawStatic(canvas);
    }

    @Override
    public void computeScroll() {
        super.computeScroll();
        if (mScroller != null && mScroller.computeScrollOffset()) {
            // 动画未完成
//            Log.e(TAG, "起点：" + mTouchPoint.x + "   滑动距离" + mScroller.getCurrX());
            if (mScroller.getCurrX() == mScroller.getFinalX()) {
                mRunning = false;
                return;
            }
            mTouchPoint.set(mScroller.getCurrX(), mScroller.getCurrY());
            mPageView.postInvalidate();
        }
    }

    @Override
    public boolean isRunning() {
        return mRunning;
    }

    @Override
    public void recycle() {
        super.recycle();
        if (mVelocityTracker != null) {
            mVelocityTracker.recycle();
            mVelocityTracker = null;
        }
    }
}