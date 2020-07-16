package com.bubble.reader.widget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Point;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.collection.ArrayMap;

import com.bubble.common.log.BubbleLog;
import com.bubble.reader.bean.PageBitmap;
import com.bubble.reader.bean.PageResult;
import com.bubble.reader.page.PageCreator;
import com.bubble.reader.page.listener.PageListener;
import com.bubble.reader.widget.draw.base.PageDrawHelper;
import com.bubble.reader.widget.draw.impl.HorizontalMoveDrawHelper;
import com.bubble.reader.widget.draw.impl.HorizontalScrollDrawHelper;
import com.bubble.reader.widget.draw.impl.LoadingDrawHelper;
import com.bubble.reader.widget.draw.impl.SimulationDrawHelper;
import com.bubble.reader.widget.draw.impl.VerticalScrollDrawHelperV2;
import com.bubble.reader.widget.listener.OnContentListener;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Bubble
 * @date 2020/6/20
 * @email 1337986595@qq.com
 * @GitHub https://github.com/SmallBubble
 * @Gitte https://gitee.com/SmallCatBubble
 * @Desc 阅读视图
 */
public class PageView extends View {
    private static final String TAG = PageView.class.getSimpleName();

    /**
     * 绘制帮助类
     */
    private PageDrawHelper mDrawHelper;
    private LoadingDrawHelper mLoadingDrawHelper;
    /**
     * 绘制帮助类集合 缓存起来 不用每次切换都重新创建一个
     */
    private ArrayMap<String, PageDrawHelper> mDrawHelpers = new ArrayMap<>();
    /**
     * 获取内容
     */
    private PageCreator mPageCreator;
    /**
     * 绘制内容的bitmap 当前页、 下一页
     */
//    protected PageBitmap mCurrentPage;
//    protected PageBitmap mNextPage;
    protected List<PageBitmap> mPageBitmaps = new ArrayList<>();
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

    private PageSettings mSettings = new PageSettings(new PageSettings.OnSettingListener() {
        @Override
        public void onChanged() {
            mPageCreator.refresh();
        }
    });

    public PageSettings getSettings() {
        return mSettings;
    }

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

    private PageListener mPageListener = new PageListener() {
        @Override
        public void onPageLoadFinished() {
            super.onPageLoadFinished();
            // 页面加载结束 绘制内容
            mLoadingDrawHelper.stopLoading();
            postInvalidate();
        }
    };
    private OnContentListener mOnContentListener = new OnContentListener() {
        @Override
        public PageResult onNextPage() {
            if (mPageCreator != null) {
                exchangePage(true);
                PageResult result = mPageCreator.onNextPage();
                if (result.isLoading()) {
                    mLoadingDrawHelper.startLoading();
                }
                return result;
            }
            return new PageResult();
        }

        @Override
        public PageResult onPrePage() {
            if (mPageCreator != null) {
                exchangePage(false);
                PageResult result = mPageCreator.onPrePage();
                if (result.isLoading()) {
                    mLoadingDrawHelper.startLoading();
                }
                return result;
            }
            return new PageResult();
        }

        @Override
        public void onCancel() {
            if (mPageCreator != null) {
                mPageCreator.onCancel();
            }
        }
    };

    /**
     * 改变bitmap 位置
     *
     * @param next
     */
    private void exchangePage(boolean next) {
        PageBitmap bitmap = mPageBitmaps.get(0);
        mPageBitmaps.remove(0);
        mPageBitmaps.add(bitmap);
        mPageBitmaps.get(0).setType(1);
        mPageBitmaps.get(1).setType(2);
    }


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
        mLoadingDrawHelper = new LoadingDrawHelper(this);
        mLoadingDrawHelper.init();
    }

    private Runnable mDelayedInit = () -> delayedInit();

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        removeCallbacks(mDelayedInit);
        mDrawHelper.recycle();
        mLoadingDrawHelper.recycle();
        recycle();
    }

    private void recycle() {
        for (PageBitmap bitmap : mPageBitmaps) {
            bitmap.getBitmap().recycle();
            bitmap.setBitmap(null);
        }
        mPageBitmaps.clear();
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
        mPageBitmaps.add(new PageBitmap(Bitmap.createBitmap(mWidth, mHeight, Bitmap.Config.ARGB_8888)));
        mPageBitmaps.get(0).setType(1);
        mPageBitmaps.add(new PageBitmap(Bitmap.createBitmap(mWidth, mHeight, Bitmap.Config.ARGB_8888)));
        mPageBitmaps.get(1).setType(2);
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
                    mDrawHelper = new HorizontalMoveDrawHelper(this);
                    initData();
                } else {
                    mDrawHelper = mDrawHelpers.get("HORIZONTAL_MOVE");
                }
                break;
            case HORIZONTAL_SCROLL:
                if (mDrawHelpers.get("HORIZONTAL_SCROLL") == null) {
                    mDrawHelper = new HorizontalScrollDrawHelper(this);
                    initData();
                } else {
                    mDrawHelper = mDrawHelpers.get("HORIZONTAL_SCROLL");
                }
                break;
            case VERTICAL_SCROLL:
                if (mDrawHelpers.get("VERTICAL_SCROLL") == null) {
                    mDrawHelper = new VerticalScrollDrawHelperV2(this);
                    initData();
                } else {
                    mDrawHelper = mDrawHelpers.get("VERTICAL_SCROLL");
                }
                break;
            default:
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
    public void setDrawHelper(PageDrawHelper drawHelper) {
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
        return mPageBitmaps.get(0);
    }

    public PageBitmap getNextPage() {
        return mPageBitmaps.get(1);
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        // 让DrawHelper 接管事件 所有的翻页效果都通过helper类来实现
        mDrawHelper.onTouchEvent(this, event);
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                // 设置起始点
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
            default:
        }
        return true;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        BubbleLog.e(TAG, "onDraw");
        // 绘制加载中
        mLoadingDrawHelper.draw(canvas);
        if (mInitialized && checkPageInit()) {
            mDrawHelper.draw(canvas);
        }

    }

    /**
     * 页面不为空
     *
     * @return
     */
    private boolean checkPageInit() {
        return mPageBitmaps != null && mPageBitmaps.size() >= 2;
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
