package com.bubble.reader.widget.draw;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.animation.LinearInterpolator;
import android.widget.OverScroller;

import com.bubble.common.log.BubbleLog;
import com.bubble.reader.widget.PageView;

/**
 * packger：com.bubble.reader.widget.draw
 * auther：Bubble
 * date：2020/6/21
 * email：1337986595@qq.com
 * Desc：上下滑动
 */
public class VerticalScrollDrawHelper extends DrawHelper {
    private static final String TAG = VerticalScrollDrawHelper.class.getSimpleName();
    /**
     * 滑动处理
     */
    private OverScroller mScroller;
    private VelocityTracker mVelocityTracker;
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
     * 上次滑动的距离
     */
    private int mLastY = 0;
    private Paint mPaint;

    public VerticalScrollDrawHelper(PageView pageView) {
        super(pageView);
    }

    enum State {
        FINISHED,
        IDLE
    }

    private State mState = State.IDLE;

    @Override
    protected void initData() {
        mScroller = new OverScroller(mPageView.getContext(), new LinearInterpolator());
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setColor(Color.YELLOW);
        mPaint.setAlpha(100);
//        mScroller.fling(0, 0, 0, 0, 0, 0, 0, 0);
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
                mRunning = false;
                mStartPoint.set(event.getX(), event.getY());
                mTouchPoint.set(0, 0);
                mMove = false;
                break;
            case MotionEvent.ACTION_MOVE:
                if (mOnContentListener == null) {
                    return;
                }
                mMove = true;
                scroll(event.getY() - mStartPoint.y);
                mStartPoint.set(event.getX(), event.getY());
                break;
            case MotionEvent.ACTION_CANCEL:
                BubbleLog.e(TAG, "ACTION_CANCEL===  " + mLastY);

            case MotionEvent.ACTION_UP:
                mTouchPoint.set(event.getX(), event.getY());
                mVelocityTracker.computeCurrentVelocity(1000, 10000);
                float yVelocity = mVelocityTracker.getYVelocity();
                BubbleLog.e(TAG, "速度 yVelocity：  " + yVelocity);
                mScroller.fling(0, (int) mTouchPoint.y, 0, (int) yVelocity, 0, 0, -mPageHeight * 5, 5 * mPageHeight);
//                mVelocityTracker.recycle();
                mPageView.invalidate();
                break;
        }
    }

    private void scroll(float dy) {
        mLastY -= dy;
        BubbleLog.e(TAG, "mLastY  ---- " + mLastY);
        int pageHeight = mCurrentPage.getBitmap().getHeight();
        BubbleLog.e(TAG, "pageHeight  ---- " + pageHeight);
        pageHeight = mCurrentPage.getPageBean().getPageCount() * mPageHeight;
        BubbleLog.e(TAG, "pageHeight  ---- " + pageHeight);
        if (mLastY > 0) {
            // 往上滑 下一页
            if (mState == State.IDLE && pageHeight - mLastY < mPageHeight) {
                BubbleLog.e(TAG, "获取内容  ---- " + mLastY + "   dy   " + dy);
                if (mLastY >= pageHeight) {
                    mLastY -= pageHeight;
                    mLastY += dy + 1;
                }
                mNext = true;
                mHasNext = mOnContentListener.onNextPage(0);
                mState = State.FINISHED;
            }
        } else {
            // 往下滑 上一页
            mNext = false;

        }

        mRunning = true;
        mPageView.invalidate();
    }

    @Override
    public void onDrawPage(Canvas canvas) {
        if (!mMove) {
            //没有产生移动
            return;
        }

        if (mNext) {
            // 当前页高度
            int pageHeight = mCurrentPage.getBitmap().getHeight();
            if (mLastY > pageHeight) {
                //滑动高度 大于当前页高度  只显示下一页内容
                int top = mLastY - pageHeight;
                int height = mNextPage.getBitmap().getHeight();

                mSrcRect.set(0, 0, mPageWidth, height);
                mDestRect.set(0, -top, mPageWidth, height - top);

                canvas.drawBitmap(mCurrentPage.getBitmap(), null, mDestRect, null);
                BubbleLog.e(TAG, mLastY + "mCurrentPage  mDestRect.top  ---- " + mDestRect.top);

                canvas.drawBitmap(mNextPage.getBitmap(), mSrcRect, mDestRect, null);
                BubbleLog.e(TAG, mLastY + "mNextPage  mDestRect.top  ---- " + mDestRect.top);
            } else {
                // 最大显示高度 就是屏幕高度
                int maxShowHeight = Math.min(mPageHeight, pageHeight - mLastY);
                int bottom = mLastY + maxShowHeight;

                mSrcRect.set(0, 0, mPageWidth, pageHeight);
                mDestRect.set(0, -mLastY, mPageWidth, pageHeight - mLastY);
                BubbleLog.e(TAG, mLastY + "   " + maxShowHeight + "   " + bottom);
                canvas.drawBitmap(mCurrentPage.getBitmap(), mSrcRect, mDestRect, null);
                BubbleLog.e(TAG, mLastY + "mCurrentPage  mDestRect.top  ---- " + mDestRect.top);


                int height = mNextPage.getBitmap().getHeight();
                mSrcRect.set(0, 0, mPageWidth, height);
                mDestRect.set(0, mDestRect.bottom, mPageWidth, height + mDestRect.bottom);
                canvas.drawBitmap(mNextPage.getBitmap(), mSrcRect, mDestRect, null);
                BubbleLog.e(TAG, mLastY + "mNextPage  mDestRect.top  ---- " + mDestRect.top);
            }
            if (mLastY >= pageHeight) {
                BubbleLog.e(TAG, mLastY + "  IDLE  ---- " + pageHeight);
                mState = State.IDLE;
            }
        }
    }

    @Override
    public void computeScroll() {
        super.computeScroll();
        if (mScroller != null && mScroller.computeScrollOffset()) {
            if (mScroller.getCurrY() == mScroller.getFinalY()) {
                mRunning = false;
            }
            int dy = (int) (mScroller.getCurrY() - mTouchPoint.y);
            BubbleLog.e(TAG, mTouchPoint + "  computeScroll  ---- " + mScroller.getCurrY() + "    " + dy);
            scroll(dy);
            mTouchPoint.set(mScroller.getCurrX(), mScroller.getCurrY());
        }
    }

    @Override
    public void onDrawStatic(Canvas canvas) {
        super.onDrawStatic(canvas);
    }

    @Override
    public boolean isRunning() {
        return mRunning;
    }
}
