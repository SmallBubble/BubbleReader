package com.bubble.reader.widget.draw;

import android.graphics.Canvas;
import android.graphics.PointF;
import android.graphics.Rect;
import android.view.MotionEvent;

import com.bubble.common.log.BubbleLog;
import com.bubble.reader.widget.PageView;
import com.bubble.reader.widget.listener.OnContentListener;

/**
 * packger：com.bubble.reader.widget
 * author：Bubble
 * date：2020/6/21
 * email：1337986595@qq.com
 * Desc：绘制内容帮助类  实现不同的滑动效果  继承该类 绘制不同区域
 */
public abstract class DrawHelper {
    private static final String TAG = DrawHelper.class.getSimpleName();
    protected PageView mPageView;
    /**
     * 开始触发事件的点
     */
    protected PointF mStartPoint;
    /**
     * 当前触摸的点
     */
    protected PointF mTouchPoint;

    /**
     * 对图片的裁剪 也就是说你想绘制图片的哪一部分
     */
    protected Rect mSrcRect;
    /**
     * 该图片绘画的位置，就是你想把这张裁剪好的图片放在屏幕的什么位置上
     */
    protected Rect mDestRect;
    /**
     * 页面宽度
     */
    protected int mPageWidth;
    /**
     * 页面高度
     */
    protected int mPageHeight;

    protected OnContentListener mOnContentListener;

    /**
     * 是否取消翻页
     */
    protected boolean mCancel;

    public void setOnContentListener(OnContentListener onContentListener) {
        mOnContentListener = onContentListener;
    }

    public DrawHelper(PageView pageView) {
        mPageView = pageView;
        mStartPoint = new PointF();
        mTouchPoint = new PointF();
        mSrcRect = new Rect();
        mDestRect = new Rect();
    }

    public final void init() {

        mPageWidth = mPageView.getMeasuredWidth();
        mPageHeight = mPageView.getMeasuredHeight();

        initData();
    }

    protected abstract void initData();

    /**
     * 事件监听
     *
     * @param view
     * @param event
     */
    public abstract void onTouchEvent(PageView view, MotionEvent event);

    /**
     * 绘制页面
     *
     * @param canvas
     */
    public abstract void onDrawPage(Canvas canvas);

    /**
     * 绘制静止时的页面
     * <p>
     * <p>
     * 默认情况下 取消状态 绘制当前页  未取消（翻页状态） 绘制新页（上一页/下一页）
     *
     * @param canvas 画布
     */
    public void onDrawStatic(Canvas canvas) {
        BubbleLog.e(TAG, "mCancel ====  " + mCancel);
        if (mCancel) {
            // 取消翻页（绘制原来的页）
            canvas.drawBitmap(mPageView.getCurrentPage().getBitmap(), 0, 0, null);
            mCancel = false;
        } else {
            // 没取消翻页 绘制新的一页（上一页或者下一页 根据滑动方向决定）
            canvas.drawBitmap(mPageView.getNextPage().getBitmap(), 0, 0, null);
        }
//        mCancel = false;
    }

    public PointF getStartPoint() {
        return mStartPoint;
    }

    public void setStartPoint(float x, float y) {
        if (mStartPoint == null) {
            mStartPoint = new PointF();
        }
        mStartPoint.x = x;
        mStartPoint.y = y;
    }

    public PointF getTouchPoint() {
        return mTouchPoint;
    }

    public void setTouchPoint(float x, float y) {
        if (mTouchPoint == null) {
            mTouchPoint = new PointF();
        }
        mTouchPoint.x = x;
        mTouchPoint.y = y;
    }

    public void recycle() {

    }

    /**
     * 滑动
     */
    public void computeScroll() {
    }

    public boolean isRunning() {
        return false;
    }
}
