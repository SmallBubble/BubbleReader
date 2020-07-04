package com.bubble.reader.widget.draw;

import android.graphics.Canvas;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.animation.LinearInterpolator;
import android.widget.Scroller;

import com.bubble.reader.widget.PageView;

/**
 * packger：com.bubble.reader.widget.draw
 *
 * @auther Bubble
 * date：2020/6/21
 * email：jiaxiang6595@foxmail.com
 * @Desc 水平平移
 */
public class HorizontalMoveDrawHelper extends DrawHelper {
    private final static String TAG = HorizontalMoveDrawHelper.class.getSimpleName();
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

    public HorizontalMoveDrawHelper(PageView pageView) {
        super(pageView);
    }

    @Override
    protected void initData() {
        mScroller = new Scroller(mPageView.getContext(), new LinearInterpolator());
    }

    @Override
    public void onTouchEvent(PageView view, MotionEvent event) {
        mVelocityTracker.addMovement(event);
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mStartPoint.set(event.getX(), event.getY());
                mMove = false;

                break;
            case MotionEvent.ACTION_MOVE:
                mMove = true;

                break;
            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_UP:
                if (mMove) {

                }


                break;
        }
    }

    @Override
    public void onDrawPage(Canvas canvas) {

    }


    @Override
    public void onDrawStatic(Canvas canvas) {

    }
}
