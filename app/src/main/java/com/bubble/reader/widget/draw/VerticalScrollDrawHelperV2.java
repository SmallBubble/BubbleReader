package com.bubble.reader.widget.draw;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.animation.LinearInterpolator;
import android.widget.OverScroller;

import com.bubble.common.log.BubbleLog;
import com.bubble.reader.page.bean.PageResult;
import com.bubble.reader.widget.PageView;

/**
 * packger：com.bubble.reader.widget.draw
 * author：Bubble
 * date：2020/6/21
 * email：1337986595@qq.com
 * Desc：上下滑动
 */
public class VerticalScrollDrawHelperV2 extends DrawHelper {
    private static final String TAG = VerticalScrollDrawHelperV2.class.getSimpleName();
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
    private PageResult mHasNext;
    /**
     * 是否在滑动
     */
    private boolean mRunning;
    /**
     * 上次滑动的距离
     */
    private int mLastY = 0;
    private Paint mPaint;
    private boolean mInited;

    public VerticalScrollDrawHelperV2(PageView pageView) {
        super(pageView);
    }

    enum State {
        UP_FINISHED,
        DOWN_FINISHED,
        IDLE
    }

    private State mState = State.IDLE;

    @Override
    protected void initData() {
        mScroller = new OverScroller(mPageView.getContext(), new LinearInterpolator());
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setColor(Color.YELLOW);
        mPaint.setAlpha(100);
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
                mStartPoint.set(event.getX(), event.getY());
                mTouchPoint.set(event.getX(), event.getY());
                mRunning = false;
                mMove = false;
                break;
            case MotionEvent.ACTION_MOVE:
                if (mOnContentListener == null) {
                    return;
                }
                int moveY = (int) (event.getY() - mStartPoint.y);
                mLastY += moveY;
                if (moveY > 0) {
                    // 往下滑 获取上一页内容
                    if (mLastY >= 0) {
                        mState = State.IDLE;
                    }
                    if (mState == State.IDLE) {
                        BubbleLog.e(TAG, "==============================获取上一页============================");
                        if (mLastY >= mPageHeight) {
                            mLastY -= mPageHeight;
                        }
                        mHasNext = mOnContentListener.onPrePage();
                        mState = State.DOWN_FINISHED;
                    }
                    mNext = false;
                } else {
                    // 往下滑 获取下一页内容
                    if (mLastY <= -mPageHeight) {
                        mState = State.IDLE;
                    }
                    // 状态为 空闲并且需要获取下一页内容
                    if (mState == State.IDLE) {
                        BubbleLog.e(TAG, "==============================获取下一页============================");
                        if (mLastY <= -mPageHeight) {
                            mLastY += mPageHeight;
                        }
                        mState = State.UP_FINISHED;
                        mHasNext = mOnContentListener.onNextPage();
                    }
                    mNext = true;

                }
                // 没有内容 不处理了
                if (mHasNext == null || !mHasNext.isHasNext()) {
                    return;
                }
                mRunning = true;
                mMove = true;
                BubbleLog.e(TAG, "==============================" + mLastY + "============================");
                mStartPoint.set(event.getX(), event.getY());
                mPageView.invalidate();
                break;
            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_UP:
//                if (mMove) {
//                    mTouchPoint.set(event.getX(), event.getY());
//                    mVelocityTracker.computeCurrentVelocity(1000, 10000);
//                    float yVelocity = mVelocityTracker.getYVelocity();
//                    mScroller.fling(0, (int) mTouchPoint.y, 0, (int) yVelocity, 0, 0, -mPageHeight * 10, 10 * mPageHeight);
//                    mPageView.invalidate();
//                    if (mVelocityTracker != null)
//                        mVelocityTracker.clear();
//                }
                break;
        }
    }

    private boolean isNeedGetPrePage() {
        // 移动的距离 大于0 并且小于一页的时候 获取上一页
        return mLastY >= 0 && mLastY <= mPageHeight;
    }

    private boolean isNeedGetNextPage() {
        // 移动的距离 大于0 并且小于一页的时候 获取上一页
        return Math.abs(mLastY) <= mPageHeight;
    }


    @Override
    public void onDrawPage(Canvas canvas) {
        if (!mMove) {
            //没有产生移动
            return;
        }

        if (mNext) {
            int top = mLastY;
            mSrcRect.set(0, 0, mPageWidth, mPageHeight);
            mDestRect.set(0, top, mPageWidth, top + mPageHeight);
            canvas.drawBitmap(mPageView.getCurrentPage().getBitmap(), mSrcRect, mDestRect, null);

            mSrcRect.set(0, 0, mPageWidth, mPageHeight);
            mDestRect.set(0, mDestRect.bottom, mPageWidth, mDestRect.bottom + mPageHeight);
            canvas.drawBitmap(mPageView.getNextPage().getBitmap(), mSrcRect, mDestRect, null);
        } else {
            mSrcRect.set(0, 0, mPageWidth, mPageHeight);
            mDestRect.set(0, mLastY, mPageWidth, mPageHeight + mLastY);
            canvas.drawBitmap(mPageView.getCurrentPage().getBitmap(), mSrcRect, mDestRect, null);

            mSrcRect.set(0, 0, mPageWidth, mPageHeight);
            mDestRect.set(0, mDestRect.bottom, mPageWidth, mDestRect.bottom + mPageHeight);
            canvas.drawBitmap(mPageView.getNextPage().getBitmap(), mSrcRect, mDestRect, null);
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
//            BubbleLog.e(TAG, mTouchPoint + "  computeScroll  ---- " + mScroller.getCurrY() + "    " + dy);
//            scroll(dy, false);
            mTouchPoint.set(mScroller.getCurrX(), mScroller.getCurrY());
        }
    }

    @Override
    public void onDrawStatic(Canvas canvas) {
        if (!mInited) {
            mInited = true;
            super.onDrawStatic(canvas);
        } else {
            int top = mPageView.getCurrentPage().getTranslationY();
            int pageHeight = mPageView.getCurrentPage().getBitmap().getHeight();
            mSrcRect.set(0, 0, mPageWidth, pageHeight);
            mDestRect.set(0, top, mPageWidth, pageHeight - top);
            canvas.drawBitmap(mPageView.getCurrentPage().getBitmap(), mSrcRect, mDestRect, null);
            mPageView.getCurrentPage().setTranslationY(mDestRect.top);

            mSrcRect.set(0, 0, mPageWidth, pageHeight);
            mDestRect.set(0, mDestRect.bottom, mPageWidth, pageHeight + mDestRect.bottom);
            canvas.drawBitmap(mPageView.getNextPage().getBitmap(), mSrcRect, mDestRect, null);
            mPageView.getNextPage().setTranslationY(mDestRect.top);
        }

    }

    @Override
    public boolean isRunning() {
        return mRunning;
    }
}
