package com.bubble.reader.widget.draw;

import android.graphics.Canvas;
import android.graphics.drawable.GradientDrawable;
import android.util.Log;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.animation.LinearInterpolator;
import android.widget.Scroller;

import com.bubble.reader.widget.PageView;

/**
 * packger：com.bubble.reader.widget.draw
 * auther：Bubble
 * date：2020/6/21
 * email：jiaxiang6595@foxmail.com
 * Desc：水平滑动
 */
public class HSDrawHelper extends DrawHelper {

    private static final String TAG = HSDrawHelper.class.getSimpleName();
    private GradientDrawable mShadow;
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

    public HSDrawHelper(PageView pageView) {
        super(pageView);
    }

    @Override
    protected void initData() {
        mShadow = new GradientDrawable(GradientDrawable.Orientation.LEFT_RIGHT, mShadowColor);
        mScroller = new Scroller(mPageView.getContext(), new LinearInterpolator() {
            @Override
            public float getInterpolation(float input) {
                Log.e(TAG, "inpput" + input);
                return super.getInterpolation(input);
            }
        });

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
                mStartPoint.x = event.getX();
                mStartPoint.y = event.getY();
                break;
            case MotionEvent.ACTION_MOVE:
                if (mOnReadListener == null) {
                    //没设置监听 也就获取不了内容  直接返回
                    return;
                }
                mMove = true;
                if (mTouchPoint.equals(0, 0)) {
                    // 没有滑动过
                    if (event.getX() - mStartPoint.x > 0) {
                        // 往右边滑动，翻上一页
                        mNext = false;
                        mHasNext = mOnReadListener.onPrePager();
                    } else {
                        // 往左边滑动，翻下一页
                        mNext = true;
                        mHasNext = mOnReadListener.onNextPage();
                    }
                }
                // 没有内容了
                if (!mHasNext) {
                    return;
                }
                // 设置当前位置
                mTouchPoint.x = event.getX();
                mTouchPoint.y = event.getY();
                mRunning = true;
                // 通知绘制新内容
                view.postInvalidate();
                break;
            case MotionEvent.ACTION_UP:
                // 设置当前位置
                mTouchPoint.x = event.getX();
                mTouchPoint.y = event.getY();

                if (mMove) {
                    int dx = 0;
                    // 计算滑动速度
                    mVelocityTracker.computeCurrentVelocity(1, 10f);
                    float xVelocity = mVelocityTracker.getXVelocity();
                    Log.e(TAG, "xVelocity ====  " + xVelocity);
                    int moveX = (int) (mTouchPoint.x - mStartPoint.x);
                    if (Math.abs(xVelocity) > 2) {
                        // 滑动速度大于5 翻页
                        if (mNext) {
                            // 翻到下一页
                            dx = -(mPageWidth - Math.abs(moveX));
                        } else {
                            // 翻到上一页
                            dx = mPageWidth - moveX;
                        }
                    } else {
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
                            // 滑动距离未到一半 取消翻页
                            dx = -moveX;
                        }
                        mRunning = true;
                    }
                    Log.e(TAG, "moveX：" + moveX + "   滑动距离" + dx);
                    mScroller.startScroll((int) mTouchPoint.x, 0, dx, 0, 200);
                    // 通知绘制新内容
                    view.postInvalidate();
                }
                Log.e(TAG, "\n\n\n开始：\n\n\n");
                break;
        }
    }

    @Override
    public void onDrawPage(Canvas canvas) {
        Log.e(TAG, "onDrawPage" + mTouchPoint.x);
        if (!mMove) {
            return;
        }
        // 滑动的距离

        if (mNext) {
            int moveX = (int) (mTouchPoint.x - mStartPoint.x);
            Log.e(TAG, "onDrawPage moveX" + moveX);
            if (moveX > 0) {
                return;
            }
            // 往左滑 翻下一页
            moveX = Math.abs(moveX);
            // 画当前页
            mSrcRect.set(moveX, 0, mPageWidth, mPageHeight);
            mDestRect.set(0, 0, mPageWidth - moveX, mPageHeight);
            Log.e(TAG, "onDrawPage mSrcRect" + mSrcRect.toShortString());
            Log.e(TAG, "onDrawPage mDestRect" + mDestRect.toShortString());
            Log.e(TAG, "onDrawPage -----------------------------------------------------------------\n\n");
            Log.e(TAG, "onDrawPage -----------------------------------------------------------------");

            canvas.drawBitmap(mCurrentPage.getBitmap(), mSrcRect, mDestRect, null);
            // 画下一页
            mSrcRect.set(mPageWidth - moveX, 0, mPageWidth, mPageHeight);
            mDestRect.set(mPageWidth - moveX, 0, mPageWidth, mPageHeight);
            canvas.drawBitmap(mNextPage.getBitmap(), mSrcRect, mDestRect, null);
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
            canvas.drawBitmap(mNextPage.getBitmap(), mSrcRect, mDestRect, null);
            //当前页
            mSrcRect.set(moveX, 0, mPageWidth, mPageHeight);
            mDestRect.set(moveX, 0, mPageWidth, mPageHeight);
            canvas.drawBitmap(mCurrentPage.getBitmap(), mSrcRect, mDestRect, null);
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
        if (mCancel) {
            // 取消翻页（绘制原来的页）
            canvas.drawBitmap(mCurrentPage.getBitmap(), 0, 0, null);
        } else {
            // 没取消翻页 绘制新的一页（上一页或者下一页 根据滑动方向决定）
            canvas.drawBitmap(mNextPage.getBitmap(), 0, 0, null);
        }
    }

    @Override
    public void computeScroll() {
        super.computeScroll();
        if (mScroller != null && mScroller.computeScrollOffset()) {
            // 动画未完成
//            Log.e(TAG, "起点：" + mTouchPoint.x + "   滑动距离" + mScroller.getCurrX());
            if (mScroller.getCurrX() == mScroller.getFinalX()) {
                mRunning = false;
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