package com.bubble.reader.widget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Point;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.Nullable;

import com.bubble.reader.creator.PageCreator;
import com.bubble.reader.page.PageBitmap;
import com.bubble.reader.widget.draw.DrawHelper;
import com.bubble.reader.widget.draw.HSDrawHelper;
import com.bubble.reader.widget.listener.OnContentListener;

/**
 * packger：com.bubble.reader.reader
 * auther：Bubble
 * date：2020/6/20
 * email：jiaxiang6595@foxmail.com
 * Desc： 阅读视图
 */
public class PageView extends View {
    private static final String TAG = PageView.class.getSimpleName();

    /**
     * 绘制帮助类
     */
    private DrawHelper mDrawHelper;
    /**
     * 获取内容
     */
    private PageCreator mPageCreator;
    /**
     * 绘制内容的bitmap 当前页、 下一页
     */
    protected PageBitmap mCurrentPage;
    protected PageBitmap mNextPage;
    /**
     * 宽度
     */
    private int mWidth;
    private int mHeight;

    private Point mDownPoint;
    private Point mTouchPoint;


    private boolean mMove = false;
    private boolean mAttach;
    private boolean mInitialized;

    private OnContentListener mOnReadListener = new OnContentListener() {
        @Override
        public boolean onNextPage() {
            if (mPageCreator != null) {
                return mPageCreator.onNextPage();
            }
            return false;
        }

        @Override
        public boolean onPrePager() {
            return true;
//            if (mPageCreator != null) {
//                return mPageCreator.onPrePage();
//            }
//            return false;
        }

        @Override
        public void onCancel() {
            if (mPageCreator != null) {
                mPageCreator.onCancel();
            }
        }
    };

    public PageView(Context context) {
        this(context, null);
    }

    public PageView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public PageView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        post(mDelayedInit);
    }

    private Runnable mDelayedInit = () -> init();

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        removeCallbacks(mDelayedInit);
//        mDrawHelper.recycle();
//        recycle();
    }

    private void recycle() {
        mCurrentPage.getBitmap().recycle();
        mNextPage.getBitmap().recycle();
        mCurrentPage = null;
        mNextPage = null;
    }

    private void init() {
        mWidth = getMeasuredWidth();
        mHeight = getMeasuredHeight();
        mDownPoint = new Point();
        mTouchPoint = new Point();
        mAttach = true;
        initBitmap();
        initData();
    }

    private void initBitmap() {
        Log.e(TAG, "initBitmap");
        mCurrentPage = new PageBitmap(Bitmap.createBitmap(mWidth, mHeight, Bitmap.Config.ARGB_8888));
        mNextPage = new PageBitmap(Bitmap.createBitmap(mWidth, mHeight, Bitmap.Config.ARGB_8888));
    }

    /**
     * 初始化绘制帮助类和页面创建者
     */
    private void initData() {
        if (!mAttach) {
            return;
        }
        if (mDrawHelper == null) {
            mDrawHelper = new HSDrawHelper(this);
        }
        mDrawHelper.init();
        if (mPageCreator != null) {
            mPageCreator.init();
        }
        mInitialized = true;
        initListener();
        invalidate();
    }

    private void initListener() {
        mDrawHelper.setOnReadListener(mOnReadListener);
    }

    /**
     * 设置绘制 帮助类
     *
     * @param drawHelper
     */
    public void setDrawHelper(DrawHelper drawHelper) {
        mDrawHelper = drawHelper;
        initData();
    }

    /**
     * 设置页码帮助类
     *
     * @param creator
     */
    public void setPageCreator(PageCreator creator) {
        mPageCreator = creator;
        initData();
    }

    public PageBitmap getCurrentPage() {
        return mCurrentPage;
    }

    public PageBitmap getNextPage() {
        return mNextPage;
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        // 设置当前触摸的点
        mDrawHelper.onTouchEvent(this, event);
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                // 设置起始点
                mDrawHelper.setStartPoint(event.getX(), event.getY());
                mDownPoint.x = (int) event.getX();
                mDownPoint.y = (int) event.getY();
                break;
            case MotionEvent.ACTION_MOVE:
                mMove = true;
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                if (!mMove) {
                    //未发生移动
                    // 点击中部区域
                    if (mDownPoint.y < mHeight / 6 * 5 && mDownPoint.y > mHeight / 6 && mDownPoint.x > mWidth / 4 && mDownPoint.x < mWidth / 4 * 3) {
                        if (mOnPageCenterListener != null) {
                            mOnPageCenterListener.onPageCenter();
                        }
                    }
                    // 底部
                    if (mDownPoint.y > mHeight / 6 * 5 && mDownPoint.x > mWidth / 4 && mDownPoint.x < mWidth / 4 * 3) {
                        if (mOnPageBottomListener != null) {
                            mOnPageBottomListener.onPageBottom();
                        }
                    }
                    // 顶部
                    if (mDownPoint.y < mHeight / 6 && mDownPoint.x > mWidth / 4 && mDownPoint.x < mWidth / 4 * 3) {
                        if (mOnPageTopListener != null) {
                            mOnPageTopListener.onPageTop();
                        }
                    }
                    // 左边
                    if (mDownPoint.x < mWidth / 4) {
                        if (mOnPageCenterListener != null) {
                            mOnPageCenterListener.onPageCenter();
                        }
                    }
                    // 右边
                    if (mDownPoint.x > mWidth / 4 * 3) {
                        if (mOnPageCenterListener != null) {
                            mOnPageCenterListener.onPageCenter();
                        }
                    }
                }
                mMove = false;
                break;
        }
        return true;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        Log.e(TAG, "onDraw");
        if (mInitialized) {
            if (mDrawHelper.isRunning()) {
                Log.e(TAG, "onDrawPage");
                mDrawHelper.onDrawPage(canvas);
            } else {
                Log.e(TAG, "onDrawStatic");
                mDrawHelper.onDrawStatic(canvas);
            }
        }
    }

    @Override
    public void computeScroll() {
        super.computeScroll();
        if (mDrawHelper != null) {
            mDrawHelper.computeScroll();
        }
    }

    public OnContentListener getOnReadListener() {
        return mOnReadListener;
    }

    public void setOnReadListener(OnContentListener onReadListener) {
        mOnReadListener = onReadListener;
    }

    private OnPageCenterListener mOnPageCenterListener;
    private OnPageLeftListener mOnPageLeftListener;
    private OnPageRightListener mOnPageRightListener;
    private OnPageTopListener mOnPageTopListener;
    private OnPageBottomListener mOnPageBottomListener;

    public void setOnPageCenterListener(OnPageCenterListener onPageCenterListener) {
        mOnPageCenterListener = onPageCenterListener;
    }

    public void setOnPageLeftListener(OnPageLeftListener onPageLeftListener) {
        mOnPageLeftListener = onPageLeftListener;
    }

    public void setOnPageRightListener(OnPageRightListener onPageRightListener) {
        mOnPageRightListener = onPageRightListener;
    }

    public void setOnPageTopListener(OnPageTopListener onPageTopListener) {
        mOnPageTopListener = onPageTopListener;
    }

    public void setOnPageBottomListener(OnPageBottomListener onPageBottomListener) {
        mOnPageBottomListener = onPageBottomListener;
    }

    public interface OnPageCenterListener {
        void onPageCenter();
    }

    public interface OnPageLeftListener {
        void onPageLeft();
    }

    public interface OnPageRightListener {
        void onPageRight();
    }

    public interface OnPageTopListener {
        void onPageTop();
    }

    public interface OnPageBottomListener {
        void onPageBottom();
    }
}
