package com.bubble.reader.widget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.collection.ArrayMap;

import com.bubble.common.log.BubbleLog;
import com.bubble.reader.creator.PageCreator;
import com.bubble.reader.page.PageBitmap;
import com.bubble.reader.page.listener.PageListener;
import com.bubble.reader.widget.draw.DrawHelper;
import com.bubble.reader.widget.draw.HorizontalScrollDrawHelper;
import com.bubble.reader.widget.draw.SimulationDrawHelper;
import com.bubble.reader.widget.listener.OnContentListener;

import java.lang.ref.WeakReference;

/**
 * packger：com.bubble.reader.reader
 * auther：Bubble
 * date：2020/6/20
 * email：1337986595@qq.com
 * Desc： 阅读视图
 */
public class PageView extends View {
    private static final String TAG = PageView.class.getSimpleName();

    /**
     * 绘制帮助类
     */
    private DrawHelper mDrawHelper;
    /**
     * 绘制帮助类集合 缓存起来 不用每次切换都重新创建一个
     */
    private ArrayMap<String, DrawHelper> mDrawHelpers = new ArrayMap<>();
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
    private LoadHandler mLoadHandler = new LoadHandler(this);

    private TurnPageMode mTurnPageMode = TurnPageMode.HORIZONTAL_SCROLL;

    enum TurnPageMode {
        /**
         * 水平移动
         */
        HORIZONTAL_MOVE,
        /**
         * 水平滑动
         */
        HORIZONTAL_SCROLL,
        /**
         * 上下滑动
         */
        VERTICAL_SCROLL,
        /**
         * 仿真翻页
         */
        SIMULATION
    }

    static class LoadHandler extends Handler {
        private WeakReference<PageView> mReference;

        public LoadHandler(PageView view) {
            mReference = new WeakReference<>(view);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            PageView pageView = mReference.get();
            if (pageView == null) {
                removeCallbacksAndMessages(null);
                return;
            }
            switch (msg.what) {
                case 1:
                    if (!pageView.mLoading) {
                        removeCallbacksAndMessages(null);
                    }
                    pageView.invalidate();
                    break;

            }
        }
    }

    private PageListener mPageListener = new PageListener() {
        @Override
        public void onNextPage(boolean hasNext) {
            mLoading = false;
        }

        @Override
        public void onPrePage(boolean hasPre) {
            mLoading = false;
        }
    };
    private OnContentListener mOnContentListener = new OnContentListener() {
        @Override
        public boolean onNextPage(int scroll) {
            if (mPageCreator != null) {
                boolean hasNext = mPageCreator.onNextPage(scroll);
                mLoading = false;
                return hasNext;
            }
            return false;
        }

        @Override
        public boolean onPrePage(int scroll) {
            if (mPageCreator != null) {
                return mPageCreator.onPrePage(scroll);
            }
            return false;
        }

        @Override
        public void onCancel() {
            if (mPageCreator != null) {
                mPageCreator.onCancel();
            }
        }
    };
    private boolean mLoading;
    private Paint mPaint;

    public PageView(Context context) {
        this(context, null);
    }

    public PageView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public PageView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
        post(mDelayedInit);
    }

    private void init() {
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setColor(Color.RED);
        mPaint.setTextSize(32);
    }

    private Runnable mDelayedInit = () -> delayedInit();

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        removeCallbacks(mDelayedInit);
        mLoadHandler.removeCallbacksAndMessages(null);
//        mDrawHelper.recycle();
//        recycle();
    }

    private void recycle() {
        mCurrentPage.getBitmap().recycle();
        mNextPage.getBitmap().recycle();
        mCurrentPage = null;
        mNextPage = null;
    }

    private void delayedInit() {
        mWidth = getMeasuredWidth();
        mHeight = getMeasuredHeight();
        mDownPoint = new Point();
        mTouchPoint = new Point();
        mAttach = true;
        initBitmap();
        initData();
    }

    private void initBitmap() {
        BubbleLog.e(TAG, "initBitmap");
        mCurrentPage = new PageBitmap(Bitmap.createBitmap(mWidth, mHeight, Bitmap.Config.ARGB_8888));
        mCurrentPage.setType(1);
        mNextPage = new PageBitmap(Bitmap.createBitmap(mWidth, mHeight, Bitmap.Config.ARGB_8888));
        mNextPage.setType(2);
    }

    /**
     * 切换翻页模式
     *
     * @param mode
     */
    public void switchMode(TurnPageMode mode) {
        mTurnPageMode = mode;
        switch (mTurnPageMode) {
            case SIMULATION:
                if (mDrawHelpers.get("SIMULATION") == null) {
                    mDrawHelper = new SimulationDrawHelper(this);
                    initData();
                } else {
                    mDrawHelper = mDrawHelpers.get("SIMULATION");
                }
                break;
            case HORIZONTAL_MOVE:
                if (mDrawHelpers.get("HORIZONTAL_MOVE") == null) {
                    mDrawHelper = new SimulationDrawHelper(this);
                    initData();
                } else {
                    mDrawHelper = mDrawHelpers.get("HORIZONTAL_MOVE");
                }
                break;
            case HORIZONTAL_SCROLL:
                if (mDrawHelpers.get("HORIZONTAL_SCROLL") == null) {
                    mDrawHelper = new SimulationDrawHelper(this);
                    initData();
                } else {
                    mDrawHelper = mDrawHelpers.get("HORIZONTAL_SCROLL");
                }
                break;
            case VERTICAL_SCROLL:
                if (mDrawHelpers.get("VERTICAL_SCROLL") == null) {
                    mDrawHelper = new SimulationDrawHelper(this);
                    initData();
                } else {
                    mDrawHelper = mDrawHelpers.get("VERTICAL_SCROLL");
                }
                break;
        }
    }

    /**
     * 初始化绘制帮助类和页面创建者
     */
    private void initData() {
        if (!mAttach) {
            return;
        }
        if (mDrawHelper == null) {
            mDrawHelper = new HorizontalScrollDrawHelper(this);
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
        mDrawHelper.setOnContentListener(mOnContentListener);
        mPageCreator.addPageListener(mPageListener);
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
        BubbleLog.e(TAG, "onDraw");
        if (mLoading) {// 绘制加载中
            canvas.drawText("加载中", mWidth / 2, mHeight / 2, mPaint);
        } else {
            if (mInitialized && checkPageInit()) {
                if (mDrawHelper.isRunning()) {
                    BubbleLog.e(TAG, "onDrawPage");
                    mDrawHelper.onDrawPage(canvas);
                } else {
                    BubbleLog.e(TAG, "onDrawStatic");
                    mDrawHelper.onDrawStatic(canvas);
                }
            }
        }
    }

    /**
     * 页面不为空
     *
     * @return
     */
    private boolean checkPageInit() {
        return mCurrentPage.getBitmap() != null && mNextPage != null;
    }

    @Override
    public void computeScroll() {
        super.computeScroll();
        if (mDrawHelper != null) {
            mDrawHelper.computeScroll();
        }
    }

    public OnContentListener getOnContentListener() {
        return mOnContentListener;
    }

    public void setOnContentListener(OnContentListener onContentListener) {
        mOnContentListener = onContentListener;
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
