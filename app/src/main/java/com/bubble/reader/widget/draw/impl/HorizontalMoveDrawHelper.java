package com.bubble.reader.widget.draw.impl;

import android.graphics.Canvas;
import android.graphics.drawable.GradientDrawable;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.animation.LinearInterpolator;
import android.widget.Scroller;

import com.bubble.common.log.BubbleLog;
import com.bubble.reader.bean.PageResult;
import com.bubble.reader.widget.PageView;
import com.bubble.reader.widget.draw.base.PageDrawHelper;

/**
 * @author Bubble
 * @date 2020/6/21
 * @email 1337986595@qq.com
 * @GitHub https://github.com/SmallBubble
 * @Gitte https://gitee.com/SmallCatBubble
 * @Desc 水平平移
 */
public class HorizontalMoveDrawHelper extends PageDrawHelper {
    private final static String TAG = HorizontalMoveDrawHelper.class.getSimpleName();
    /**
     * 阴影
     */
    private GradientDrawable mShadow;
    /**
     * 阴影渐变色
     */
    private int[] mShadowColor = new int[]{0xffff0000, 0xffff0000};
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
    private PageResult mHasNext;
    /**
     * 是否在滑动
     */
    private boolean mRunning;
    /**
     * 测量速度
     */
    private VelocityTracker mVelocityTracker;

    public HorizontalMoveDrawHelper(PageView pageView) {
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
                mScroller.abortAnimation();
                mCancel = false;
                mMove = false;
                mRunning = false;
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
                        mHasNext = mOnContentListener.onPrePage();
                    } else {
                        // 往左边滑动，翻下一页
                        mNext = true;
                        mHasNext = mOnContentListener.onNextPage();
                    }
                }
                if (mHasNext == null || !mHasNext.isHasNext()) {
                    return;
                }
                mRunning = true;
                //设置当前坐标
                mTouchPoint.set(event.getX(), event.getY());
                // 通知绘制新内容
                mPageView.postInvalidate();
                break;
            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_UP:
                if (mMove) {
                    int dx;
                    mVelocityTracker.computeCurrentVelocity(1, 10f);
                    float xVelocity = mVelocityTracker.getXVelocity();
                    BubbleLog.e(TAG, "xVelocity ====  " + xVelocity);
                    int moveX = (int) (mTouchPoint.x - mStartPoint.x);
                    if (Math.abs(xVelocity) > 1f) {
                        mCancel = false;
                        //速度大于1 翻页
                        if (mNext) {
                            dx = -(mPageWidth - Math.abs(moveX));
                        } else {
                            dx = mPageWidth - Math.abs(moveX);
                        }

                    } else {
                        // 不大于一 根据滑动距离判断是否翻页
                        if (Math.abs(moveX) > mPageWidth / 2) {
                            mCancel = false;
                            if (mNext) {
                                dx = -(mPageWidth - Math.abs(moveX));
                            } else {
                                dx = mPageWidth - Math.abs(moveX);
                            }
                        } else {
                            mCancel = true;
                            dx = -moveX;
                        }
                    }
                    if (mOnContentListener != null && mCancel) {
                        mOnContentListener.onCancel();
                    }
                    mScroller.startScroll((int) mTouchPoint.x, 0, dx, mPageHeight, 300);
                    mPageView.postInvalidate();
                }
                break;
            default:
        }
    }

    @Override
    public void onDrawPage(Canvas canvas) {
        if (!mMove) {
            return;
        }
        int moveX = Math.abs((int) (mTouchPoint.x - mStartPoint.x));
        if (mNext) {
            // 翻下一页
            mSrcRect.set(moveX, 0, mPageWidth, mPageHeight);
            mDestRect.set(0, 0, mPageWidth - moveX, mPageHeight);
            canvas.drawBitmap(mPageView.getCurrentPage().getBitmap(), mSrcRect, mDestRect, null);

            mSrcRect.set(0, 0, moveX, mPageHeight);
            mDestRect.set(mPageWidth - moveX, 0, mPageWidth, mPageHeight);
            canvas.drawBitmap(mPageView.getNextPage().getBitmap(), mSrcRect, mDestRect, null);
            drawLine(canvas, mPageWidth - moveX);
        } else {
            // 翻上一页
            mSrcRect.set(0, 0, mPageWidth - moveX, mPageHeight);
            mDestRect.set(moveX, 0, mPageWidth, mPageHeight);
            canvas.drawBitmap(mPageView.getCurrentPage().getBitmap(), mSrcRect, mDestRect, null);


            mSrcRect.set(mPageWidth - moveX, 0, mPageWidth, mPageHeight);
            mDestRect.set(0, 0, moveX, mPageHeight);
            canvas.drawBitmap(mPageView.getNextPage().getBitmap(), mSrcRect, mDestRect, null);
            drawLine(canvas, moveX);
        }
    }

    private void drawLine(Canvas canvas, int moveX) {
        mShadow.setBounds(moveX - 1, 0, moveX + 1, mPageHeight);
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
}
