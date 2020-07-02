package com.bubble.common.base;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;

import androidx.annotation.Nullable;

import com.trello.rxlifecycle3.RxLifecycle;
import com.trello.rxlifecycle3.android.ActivityEvent;
import com.trello.rxlifecycle3.components.support.RxAppCompatActivity;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.ObservableTransformer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

/**
 * packger：com.bubble.common.base
 * auther：Bubble
 * date：2020/6/20
 * email：jiaxiang6595@foxmail.com
 * Desc：
 */
public abstract class BaseMvpActivity<V extends BaseMvpView> extends RxAppCompatActivity {
    protected V mView;
    protected Context mContext;
    private Activity mActivity;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActivity = this;
        mContext = this;
        createView();
        init();
        if (mView != null) {
            setContentView(getContentView(savedInstanceState));
        }
        initView();
        initListener();
        initData();
    }

    /**
     * 获取view层类
     *
     * @return
     */
    public abstract Class<V> getViewClass();


    /**
     * 初始化 无关视图的初始化操作
     */
    protected abstract void init();

    /**
     * 初始化视图
     */
    protected abstract void initView();

    /**
     * 初始化事件监听
     */
    protected void initListener() {
    }

    /**
     * 获取数据
     */
    protected void initData() {
    }

    /**
     * 創建view 层
     */
    protected void createView() {
        try {
            mView = getViewClass().newInstance();
        } catch (Exception e) {
            throw new RuntimeException("View 层创建失败");
        }
    }

    /**
     * 获取布局 View
     *
     * @param savedInstanceState
     * @return
     */
    protected View getContentView(Bundle savedInstanceState) {
        // 设置布局
        if (mView != null) {
            return mView.onCreate(LayoutInflater.from(mContext), null, savedInstanceState);
        }
        return null;
    }

    /**
     * 线程调度 绑定生命周期
     *
     * @param <T>
     * @return
     */
    protected <T> ObservableTransformer<T, T> apply() {
        return new ObservableTransformer<T, T>() {
            @Override
            public ObservableSource<T> apply(Observable<T> upstream) {
                return upstream
                        .compose(RxLifecycle.<T, ActivityEvent>bindUntilEvent(lifecycle(), ActivityEvent.DESTROY))
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread());
            }
        };
    }
}
