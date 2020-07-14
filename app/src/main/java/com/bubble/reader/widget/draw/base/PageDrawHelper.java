package com.bubble.reader.widget.draw.base;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.PorterDuff;
import android.graphics.Rect;
import android.graphics.RectF;
import android.view.MotionEvent;

import com.bubble.common.log.BubbleLog;
import com.bubble.reader.bean.PageBean;
import com.bubble.reader.bean.PageBitmap;
import com.bubble.reader.widget.PageView;
import com.bubble.reader.widget.listener.OnContentListener;

/**
 * @author Bubble
 * @date 2020/6/21
 * @email 1337986595@qq.com
 * @GitHub https://github.com/SmallBubble
 * @Gitte https://gitee.com/SmallCatBubble
 * @Desc 绘制内容帮助类  实现不同的滑动效果  继承该类 绘制不同区域
 */
public abstract class PageDrawHelper extends DrawHelper {
    private static final String TAG = PageDrawHelper.class.getSimpleName();

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
     * 是否取消翻页
     */
    protected boolean mCancel;

    /**
     * 画笔
     */
    private Paint mPaint;

    /**
     * 顶部画笔
     */
    public Paint mTopPaint;
    /**
     * 底部画笔
     */
    public Paint mBottomPaint;
    /**
     * 电池主体
     */
    public RectF mBatteryRect = new RectF();
    /**
     * 电池头部
     */
    public RectF mBatteryHeadRect = new RectF();

    /**
     * 内容监听器
     */
    protected OnContentListener mOnContentListener;
    /**
     * 绘制内容的基线
     */
    private int mBaseLine;
    /*=======================================初始化=========================================*/

    public PageDrawHelper(PageView pageView) {
        super(pageView);
        mStartPoint = new PointF();
        mTouchPoint = new PointF();
        mSrcRect = new Rect();
        mDestRect = new Rect();
    }

    @Override
    public final void init() {
        super.init();
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setTextSize(mSettings.getFontSize());
        mPaint.setColor(mSettings.getFontColor());
        mTopPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mTopPaint.setTextSize(mSettings.getTopFontSize());
        mTopPaint.setColor(mSettings.getTopFontColor());

        mBottomPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mBottomPaint.setTextSize(mSettings.getBottomFontSize());
        mBottomPaint.setColor(mSettings.getBottomFontColor());
        initData();
    }

    /*=======================================必须重写方法区=========================================*/

    /**
     * 初始化
     */
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

    /*=======================================默认操作/子类可以重写进行自己的实现=========================================*/

    @Override
    public void draw(Canvas canvas) {
        // 绘制内容
        drawPage(mPageView.getCurrentPage());
        drawPage(mPageView.getNextPage());

        // 绘制翻页
        if (isRunning()) {
            BubbleLog.e(TAG, "onDrawPage");
            onDrawPage(canvas);
        } else {
            BubbleLog.e(TAG, "onDrawStatic");
            onDrawStatic(canvas);
        }

    }

    /**
     * 滑动
     */
    public void computeScroll() {
    }

    /**
     * 回收资源 子类有需要回收的资源重写该方法
     */
    @Override
    public void recycle() {

    }

    /*=======================================set/get方法区=========================================*/

    public void setOnContentListener(OnContentListener onContentListener) {
        mOnContentListener = onContentListener;
    }

    /**
     * 是否在进行滑动或者其他操作（不是静止状态下的操作）
     *
     * @return
     */
    public boolean isRunning() {
        return false;
    }

    /*=======================================绘制相关=========================================*/

    /**
     * 绘制静止时的页面
     * <p>
     * <p>
     * 默认情况下 取消状态 绘制当前页  未取消（翻页状态） 绘制新页（上一页/下一页）
     *
     * @param canvas 画布
     */
    public void onDrawStatic(Canvas canvas) {
        BubbleLog.e(TAG, "onDrawStatic mCancel ====  " + mCancel);
        if (mCancel) {
            // 取消翻页（绘制原来的页）
            canvas.drawBitmap(mPageView.getCurrentPage().getBitmap(), 0, 0, null);
            mCancel = false;
        } else {
            // 没取消翻页 绘制新的一页（上一页或者下一页 根据滑动方向决定）
            canvas.drawBitmap(mPageView.getNextPage().getBitmap(), 0, 0, null);
        }
    }

    /**
     * 绘制内容
     *
     * @param bitmap
     */
    protected void drawPage(PageBitmap bitmap) {
        Canvas canvas = new Canvas(bitmap.getBitmap());
        // 清除原来内容
        canvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
        // 页面所对应的内容
        PageBean pageBean = bitmap.getPageBean();
        BubbleLog.e("drawPage" + (pageBean == null ? pageBean : pageBean.toString()));
        if (pageBean == null) {
            return;
        }
        canvas.drawColor(Color.YELLOW);

        // 真正开始绘制内容的顶部是页面高度减掉顶部高度减去顶部内边距
        mBaseLine = mSettings.getTopHeight() - mSettings.getPaddingTop();
        mBaseLine += mSettings.getFontSize() - mPaint.descent();
        BubbleLog.e("drawPage  baseLine" + mBaseLine);
        for (int i = 0; i < pageBean.getContent().size(); i++) {
            String line = pageBean.getContent().get(i);
            if (line.length() > 0) {
                canvas.drawText(line, 0, mBaseLine, mPaint);
                mBaseLine += mSettings.getFontSize() + mSettings.getLineSpace();
            } else {
                mBaseLine += mSettings.getParagraphSpace();
            }
            BubbleLog.e("drawPage  baseLine" + mBaseLine);
        }
        BubbleLog.e("drawPage  baseLine" + mBaseLine);
        drawTop(canvas);
        drawBottom(canvas);
        mBaseLine = 0;
    }

    /**
     * 绘制顶部
     *
     * @param canvas
     */
    private void drawTop(Canvas canvas) {
        mTopPaint.setColor(Color.RED);
        canvas.drawRect(new RectF(0, 0, mPageWidth, mSettings.getTopHeight()), mTopPaint);
        onDrawTop(canvas, 0, 0, mPageWidth, mSettings.getTopHeight());
    }

    /**
     * 绘制顶部
     *
     * @param canvas
     * @param left
     * @param top
     * @param right
     * @param bottom
     */
    protected void onDrawTop(Canvas canvas, int left, int top, int right, int bottom) {

    }

    /**
     * 绘制顶部
     *
     * @param canvas
     * @param left
     * @param top
     * @param right
     * @param bottom
     */
    protected void onDrawBottom(Canvas canvas, int left, int top, int right, int bottom) {
        drawDefaultBottom(canvas, left, top, right, bottom);
    }

    private void drawDefaultBottom(Canvas canvas, int left, int top, int right, int bottom) {

    }

    /**
     * 绘制底部
     *
     * @param canvas
     */
    private void drawBottom(Canvas canvas) {
        canvas.drawRect(new RectF(0, mPageHeight - mSettings.getBottomHeight(), mPageWidth, mPageHeight), mTopPaint);

        onDrawBottom(canvas, 0, mPageHeight - mSettings.getBottomHeight(), mPageWidth, mPageHeight);
    }
}
