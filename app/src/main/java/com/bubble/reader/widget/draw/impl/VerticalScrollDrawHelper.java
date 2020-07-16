package com.bubble.reader.widget.draw.impl;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.animation.LinearInterpolator;
import android.widget.OverScroller;

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
 * @Desc 上下滑动
 */
public class VerticalScrollDrawHelper extends PageDrawHelper {
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
                mMove = true;
                int dy = (int) (event.getY() - mStartPoint.y);
                scroll(dy, true);
                mStartPoint.set(event.getX(), event.getY());
                break;
            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_UP:
                if (mMove) {
                    mTouchPoint.set(event.getX(), event.getY());
                    mVelocityTracker.computeCurrentVelocity(1000, 10000);
                    float yVelocity = mVelocityTracker.getYVelocity();
                    mScroller.fling(0, (int) mTouchPoint.y, 0, (int) yVelocity, 0, 0, -mPageHeight * 10, 10 * mPageHeight);
                    mPageView.invalidate();
                    if (mVelocityTracker != null) {
                        mVelocityTracker.clear();
                    }
                }
                break;
            default:
        }
    }

    private void scroll(float dy, boolean move) {
        mLastY -= dy;
        int pageHeight = mPageView.getCurrentPage().getBitmap().getHeight();
        if (mLastY > 0) {
            // 往上滑 下一页
            boolean needPre = mState == State.IDLE && pageHeight - mLastY < mPageHeight;
            if (needPre) {
                if (mLastY >= pageHeight) {
                    mLastY -= pageHeight;
                    mLastY += dy + 1;
                }
                mNext = true;
                mHasNext = mOnContentListener.onNextPage();
                mState = State.FINISHED;
            }
        } else {
            // 往下滑 上一页
            boolean needNext = mState == State.IDLE && (Math.abs(mLastY) > mPageHeight || mLastY < 0);
            if (needNext) {
                if (Math.abs(mLastY) >= pageHeight) {
                    mLastY += pageHeight;
                    mLastY -= dy + 1;
                }
                mNext = false;
                mHasNext = mOnContentListener.onPrePage();
                mState = State.FINISHED;
            }
        }
        if (mHasNext == null || !mHasNext.isHasNext()) {
            return;
        }
        BubbleLog.e(TAG, "mLastY -----  " + mLastY);
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
            int pageHeight = mPageView.getCurrentPage().getBitmap().getHeight();
            if (mLastY > pageHeight) {
                //滑动高度 大于当前页高度  只显示下一页内容
                int top = mLastY - pageHeight;
                int height = mPageView.getCurrentPage().getBitmap().getHeight();
                mSrcRect.set(0, 0, mPageWidth, height);
                mDestRect.set(0, -top, mPageWidth, height - top);
                canvas.drawBitmap(mPageView.getCurrentPage().getBitmap(), mSrcRect, mDestRect, null);
                canvas.drawBitmap(mPageView.getNextPage().getBitmap(), mSrcRect, mDestRect, null);

                mPageView.getCurrentPage().setTranslationY(mDestRect.top);
                mPageView.getNextPage().setTranslationY(mDestRect.top);
            } else {
                // 最大显示高度 就是屏幕高度
                mSrcRect.set(0, 0, mPageWidth, pageHeight);
                mDestRect.set(0, -mLastY, mPageWidth, pageHeight - mLastY);
                canvas.drawBitmap(mPageView.getCurrentPage().getBitmap(), mSrcRect, mDestRect, null);
                mPageView.getCurrentPage().setTranslationY(mDestRect.top);
                int height = mPageView.getNextPage().getBitmap().getHeight();
                mSrcRect.set(0, 0, mPageWidth, height);
                mDestRect.set(0, mDestRect.bottom, mPageWidth, height + mDestRect.bottom);
                canvas.drawBitmap(mPageView.getNextPage().getBitmap(), mSrcRect, mDestRect, null);
                mPageView.getNextPage().setTranslationY(mDestRect.top);
            }
            if (mLastY >= pageHeight) {
                // 当滑动超过一页的时候 状态 改为空闲 这时候 可以去获取新的内容
                mState = State.IDLE;
            }
        } else {
            // 上一页  的滑动
            int pageHeight = mPageView.getCurrentPage().getBitmap().getHeight();

            mLastY = Math.abs(mLastY);
            if (Math.abs(mLastY) > pageHeight) {
                // 当滑动距离超过一个页面大小时 获取减去页面高度的剩余高度
                int top = mLastY - pageHeight;
                mSrcRect.set(0, 0, mPageWidth, pageHeight);
                mDestRect.set(0, top, mPageWidth, top + pageHeight);
                mPageView.getCurrentPage().setTranslationY(mDestRect.top);
                mPageView.getNextPage().setTranslationY(mDestRect.top);
                canvas.drawBitmap(mPageView.getCurrentPage().getBitmap(), mSrcRect, mDestRect, null);
                canvas.drawBitmap(mPageView.getNextPage().getBitmap(), mSrcRect, mDestRect, null);
            } else {
                //往下滑了多少 当前页的顶部就是多少
                int top = mLastY;
                mSrcRect.set(0, 0, mPageWidth, pageHeight);
                mDestRect.set(0, top, mPageWidth, mLastY + pageHeight);
                mPageView.getCurrentPage().setTranslationY(mDestRect.top);
                canvas.drawBitmap(mPageView.getCurrentPage().getBitmap(), mSrcRect, mDestRect, null);
                BubbleLog.e(TAG, "getCurrentPage mDestRect" + mDestRect.toShortString());

                mSrcRect.set(0, 0, mPageWidth, pageHeight);
                mDestRect.set(0, top - pageHeight, mPageWidth, mDestRect.top);
                mPageView.getNextPage().setTranslationY(mDestRect.top);
                canvas.drawBitmap(mPageView.getNextPage().getBitmap(), mSrcRect, mDestRect, null);
                BubbleLog.e(TAG, "getNextPage mDestRect" + mDestRect.toShortString());
            }
            if (Math.abs(mLastY) >= pageHeight) {
                // 当滑动超过一页的时候 状态 改为空闲 这时候 可以去获取新的内容
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
//            BubbleLog.e(TAG, mTouchPoint + "  computeScroll  ---- " + mScroller.getCurrY() + "    " + dy);
            scroll(dy, false);
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
