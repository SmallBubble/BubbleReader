package com.bubble.breader.widget.draw.impl;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.animation.LinearInterpolator;
import android.widget.OverScroller;

import com.bubble.common.log.BubbleLog;
import com.bubble.breader.bean.PageResult;
import com.bubble.breader.widget.PageView;
import com.bubble.breader.widget.draw.base.PageDrawHelper;

/**
 * @author Bubble
 * @date 2020/6/21
 * @email 1337986595@qq.com
 * @GitHub https://github.com/SmallBubble
 * @Gitte https://gitee.com/SmallCatBubble
 * @Desc 上下滑动
 */
public class VerticalScrollDrawHelperV2 extends PageDrawHelper {
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
     * 相对正常情况下的偏移量
     */
    private int mOffsetY = 0;
    private Paint mPaint;
    private boolean mInitialized;

    public VerticalScrollDrawHelperV2(PageView pageView) {
        super(pageView);
    }

    enum State {
        /**
         * 获取下一页结束
         */
        NEXT_FINISHED,
        /**
         * 获取上一页结束
         */
        PRE_FINISHED,
        /**
         * 空闲
         */
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

                scroll(moveY, true);
                // 没有内容 不处理了
//                if (mHasNext == null || !mHasNext.isHasNext()) {
//                    return;
//                }
                mRunning = true;
                mMove = true;

                mStartPoint.set(event.getX(), event.getY());
                mPageView.invalidate();
                break;
            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_UP:
                if (mMove) {
                    mTouchPoint.set(event.getX(), event.getY());
                    mVelocityTracker.computeCurrentVelocity(1000, 10000);
                    float yVelocity = mVelocityTracker.getYVelocity();

                    BubbleLog.e(TAG, "==============================yVelocity" + yVelocity);
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

    private void scroll(int moveY, boolean move) {
        mOffsetY += moveY;
        if (moveY == 0) return;
        BubbleLog.e(TAG, "==============================" + mOffsetY + "============================" + move);
        if (moveY > 0) {
            // 往下滑 获取上一页内容
            if (mState == State.NEXT_FINISHED) {
                mOnContentListener.onCancel();
                mState = State.IDLE;
            }

            if (mOffsetY >= mPageHeight) {
                mState = State.IDLE;
            }
            if (mState == State.IDLE) {
                BubbleLog.e(TAG, "==============================获取上一页============================");
                if (mOffsetY >= mPageHeight) {
                    mOffsetY -= mPageHeight;
                }
                mHasNext = mOnContentListener.onPrePage();
                mState = State.PRE_FINISHED;
            }
            if (mNext) {
//                mOnContentListener.onCancel();
            }
            mNext = false;
        } else {
            // 往下滑 获取下一页内容
            if (mState == State.PRE_FINISHED) {
                // 上次的操作是获取上一页
                mOnContentListener.onCancel();
                mState = State.IDLE;
            }
            if (mOffsetY <= -mPageHeight) {
                mState = State.IDLE;
            }

            // 状态为 空闲并且需要获取下一页内容
            if (mState == State.IDLE) {
                BubbleLog.e(TAG, "==============================获取下一页============================");
                if (mOffsetY <= -mPageHeight) {
                    mOffsetY += mPageHeight;
                }
                mState = State.NEXT_FINISHED;
                mHasNext = mOnContentListener.onNextPage();
            }
            if (!mNext) {
//                mOnContentListener.onCancel();
            }
            mNext = true;
        }
    }

    private boolean isNeedGetPrePage() {
        // 移动的距离 大于0 并且小于一页的时候 获取上一页
        return mOffsetY >= 0 && mOffsetY <= mPageHeight;
    }

    private boolean isNeedGetNextPage() {
        // 移动的距离 大于0 并且小于一页的时候 获取上一页
        return Math.abs(mOffsetY) <= mPageHeight;
    }


    @Override
    public void onDrawPage(Canvas canvas) {
        if (!mMove) {
            //没有产生移动
            return;
        }
        drawPage(canvas);

    }

    private void drawPage(Canvas canvas) {
        if (mNext) {
            mSrcRect.set(0, 0, mPageWidth, mPageHeight);
            mDestRect.set(0, mOffsetY, mPageWidth, mOffsetY + mPageHeight);
            canvas.drawBitmap(mPageView.getCurrentPage().getBitmap(), mSrcRect, mDestRect, null);
            if (mOffsetY > 0) {
                // 大于0 说明之前有向下翻页，此时向下翻页的情况摆放（下一页在上，当前页在下）
                int top = mDestRect.top - mPageHeight;
                mSrcRect.set(0, 0, mPageWidth, mPageHeight);
                mDestRect.set(0, top, mPageWidth, mDestRect.top);
            } else {
                // 小于0 按照下一页跟着当前页的位置摆放（当前页在上，下一页在下）
                mSrcRect.set(0, 0, mPageWidth, mPageHeight);
                mDestRect.set(0, mDestRect.bottom, mPageWidth, mDestRect.bottom + mPageHeight);
            }
            canvas.drawBitmap(mPageView.getNextPage().getBitmap(), mSrcRect, mDestRect, null);
        } else {
            mSrcRect.set(0, 0, mPageWidth, mPageHeight);
            mDestRect.set(0, mOffsetY, mPageWidth, mPageHeight + mOffsetY);
            canvas.drawBitmap(mPageView.getCurrentPage().getBitmap(), mSrcRect, mDestRect, null);
            if (mOffsetY < 0) {
                // 小于0 说明之前有往上滑动过 按照下一页跟着当前页的位置摆放（当前页在上，下一页在下）
                mSrcRect.set(0, 0, mPageWidth, mPageHeight);
                mDestRect.set(0, mDestRect.bottom, mPageWidth, mDestRect.bottom + mPageHeight);
            } else {
                // 大于0 按照正常向下翻页的情况摆放（下一页在上，当前页在下）
                int top = mDestRect.top - mPageHeight;
                mSrcRect.set(0, 0, mPageWidth, mPageHeight);
                mDestRect.set(0, top, mPageWidth, mDestRect.top);
            }
            canvas.drawBitmap(mPageView.getNextPage().getBitmap(), mSrcRect, mDestRect, null);
        }
    }

    @Override
    public void computeScroll() {
        super.computeScroll();
        if (mScroller != null && mScroller.computeScrollOffset()) {
            if (mScroller.getCurrY() == mScroller.getFinalY()) {
                mRunning = false;
                mMove = false;
                return;
            }
            int dy = (int) (mScroller.getCurrY() - mTouchPoint.y);
            BubbleLog.e(TAG, mTouchPoint + "  computeScroll  ---- " + mScroller.getCurrY() + "    " + dy);
            scroll(dy, false);
            mTouchPoint.set(mScroller.getCurrX(), mScroller.getCurrY());
            mPageView.invalidate();
        }
    }

    @Override
    public void onDrawStatic(Canvas canvas) {
        if (!mInitialized) {
            BubbleLog.e(TAG, "onDrawStatic 11111");
            mInitialized = true;
            super.onDrawStatic(canvas);
        } else {
            BubbleLog.e(TAG, "onDrawStatic 22222");
            drawPage(canvas);
        }
    }

    @Override
    public boolean isRunning() {
        return mRunning;
    }
}
