package com.bubble.breader.widget;

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

import com.bubble.breader.BubbleReader;
import com.bubble.breader.bean.PageBitmap;
import com.bubble.breader.bean.PageResult;
import com.bubble.breader.page.PageCreator;
import com.bubble.breader.page.listener.PageListener;
import com.bubble.breader.widget.draw.base.PageDrawHelper;
import com.bubble.breader.widget.draw.impl.DefaultLoadingDrawHelper;
import com.bubble.breader.widget.draw.impl.HorizontalMoveDrawHelper;
import com.bubble.breader.widget.draw.impl.HorizontalScrollDrawHelper;
import com.bubble.breader.widget.draw.impl.LoadingDrawHelper;
import com.bubble.breader.widget.draw.impl.SimulationDrawHelper;
import com.bubble.breader.widget.draw.impl.VerticalScrollDrawHelperV2;
import com.bubble.breader.widget.listener.OnContentListener;
import com.bubble.breader.widget.listener.OnPageBottomListener;
import com.bubble.breader.widget.listener.OnPageCenterListener;
import com.bubble.breader.widget.listener.OnPageLeftListener;
import com.bubble.breader.widget.listener.OnPageRightListener;
import com.bubble.breader.widget.listener.OnPageTopListener;
import com.bubble.common.log.BubbleLog;

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
    protected List<PageBitmap> mPageBitmaps = new ArrayList<>();
    /**
     * 宽度
     */
    private int mWidth;
    /**
     * 高度
     */
    private int mHeight;
    /**
     * 按下的点
     */
    private Point mDownPoint;
    /**
     * 是否产生移动
     */
    private boolean mMove = false;
    /**
     * 是否绑定
     */
    private boolean mAttach;
    /**
     * 是否初始化完成
     */
    private boolean mInitialized;


    /**
     * 翻页模式
     */
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



    /*===================================监听=====================================*/
    /**
     * 页面设置
     */
    private PageSettings mSettings = new PageSettings(new PageSettings.OnSettingListener() {
        @Override
        public void onChanged() {
            mPageCreator.refreshPage();
        }
    });

    /**
     * 页面监听
     */
    private PageListener mPageListener = new PageListener() {
        @Override
        public void onPageLoadFinished() {
            super.onPageLoadFinished();
            // 页面加载结束 绘制内容
            if (mLoadingDrawHelper != null) {
                mLoadingDrawHelper.stopLoading();
            }
            postInvalidate();
        }
    };
    /**
     * 翻页监听
     */
    private OnContentListener mOnContentListener = new OnContentListener() {
        @Override
        public PageResult onNextPage() {
            if (mPageCreator != null) {
                exchangePage(true);
                PageResult result = mPageCreator.onNextPage();
                if (result.isHasNext() && !result.isLoading() && mLoadingDrawHelper != null) {
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
                if (result.isHasNext() && !result.isLoading() && mLoadingDrawHelper != null) {
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
     * 延迟加载 测量布局完后加载
     */
    private Runnable mDelayedInit = () -> delayedInit();
    /*===================================初始化=====================================*/

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

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        removeCallbacks(mDelayedInit);
        mDrawHelper.recycle();
        mLoadingDrawHelper.recycle();
        recycle();
    }

    /**
     * 回收资源
     */
    private void recycle() {
        for (PageBitmap bitmap : mPageBitmaps) {
            bitmap.getBitmap().recycle();
            bitmap.setBitmap(null);
        }
        mPageBitmaps.clear();
    }

    /**
     * 初始化
     */
    private void init() {
        if (!BubbleReader.getInstance().isInit()) {
            throw new RuntimeException("未初始化阅读器");
        }
    }

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

    /**
     * 延迟加载
     */
    private void delayedInit() {
        mWidth = getMeasuredWidth();
        mHeight = getMeasuredHeight();
        mDownPoint = new Point();
        mAttach = true;
        initBitmap();
        initData();
    }

    /**
     * 初始化页面 bitmap
     */
    private void initBitmap() {
        BubbleLog.e(TAG, "initBitmap");
        mPageBitmaps.add(new PageBitmap(Bitmap.createBitmap(mWidth, mHeight, Bitmap.Config.ARGB_8888)));
        mPageBitmaps.get(0).setType(1);
        mPageBitmaps.add(new PageBitmap(Bitmap.createBitmap(mWidth, mHeight, Bitmap.Config.ARGB_8888)));
        mPageBitmaps.get(1).setType(2);
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
        if (mLoadingDrawHelper == null) {
            mLoadingDrawHelper = new DefaultLoadingDrawHelper(this, 200);
        }
        mLoadingDrawHelper.init();

        mLoadingDrawHelper.init();
        if (mPageCreator != null) {
            mPageCreator.init();
        }
        mInitialized = true;
        initListener();
        invalidate();
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
                if (mDrawHelpers.get(TurnPageMode.SIMULATION) == null) {
                    mDrawHelper = new SimulationDrawHelper(this);
                    initData();
                } else {
                    mDrawHelper = mDrawHelpers.get(TurnPageMode.SIMULATION);
                }
                break;
            case HORIZONTAL_MOVE:
                if (mDrawHelpers.get(TurnPageMode.HORIZONTAL_MOVE) == null) {
                    mDrawHelper = new HorizontalMoveDrawHelper(this);
                    initData();
                } else {
                    mDrawHelper = mDrawHelpers.get(TurnPageMode.HORIZONTAL_MOVE);
                }
                break;
            case HORIZONTAL_SCROLL:
                if (mDrawHelpers.get(TurnPageMode.HORIZONTAL_SCROLL) == null) {
                    mDrawHelper = new HorizontalScrollDrawHelper(this);
                    initData();
                } else {
                    mDrawHelper = mDrawHelpers.get(TurnPageMode.HORIZONTAL_SCROLL);
                }
                break;
            case VERTICAL_SCROLL:
                if (mDrawHelpers.get(TurnPageMode.VERTICAL_SCROLL) == null) {
                    mDrawHelper = new VerticalScrollDrawHelperV2(this);
                    initData();
                } else {
                    mDrawHelper = mDrawHelpers.get(TurnPageMode.VERTICAL_SCROLL);
                }
                break;
            default:
        }
    }

    /**
     * 获取设置
     *
     * @return
     */
    public PageSettings getSettings() {
        return mSettings;
    }


    private void initListener() {
        mDrawHelper.setOnContentListener(mOnContentListener);
        mPageCreator.addPageListener(mPageListener);
    }

    /**
     * 下一页
     */
    public void nextPage() {
        mOnContentListener.onNextPage();
    }

    /**
     * 上一页
     */
    public void prePage() {
        mOnContentListener.onPrePage();
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
     * 设置加载帮助类
     *
     * @param loadingDrawHelper
     */
    public void setLoadingDrawHelper(LoadingDrawHelper loadingDrawHelper) {
        mLoadingDrawHelper = loadingDrawHelper;
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

    /**
     * 获取当前页
     *
     * @return
     */
    public PageBitmap getCurrentPage() {
        return mPageBitmaps.get(0);
    }

    /**
     * 获取下一页
     *
     * @return
     */
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
        if (mLoadingDrawHelper != null && mLoadingDrawHelper.isLoading()) {
            mLoadingDrawHelper.draw(canvas);
        } else {
            if (mInitialized && checkPageInit()) {
                mDrawHelper.draw(canvas);
            }
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
}
