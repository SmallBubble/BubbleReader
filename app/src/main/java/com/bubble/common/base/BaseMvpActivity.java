package com.bubble.common.base;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;

import androidx.annotation.Nullable;

import com.bubble.common.net.callback.RequestCallBack;
import com.trello.rxlifecycle3.RxLifecycle;
import com.trello.rxlifecycle3.android.ActivityEvent;
import com.trello.rxlifecycle3.components.support.RxAppCompatActivity;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.ObservableTransformer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

/**
 * @author Bubble
 * @date 2020/6/20
 * @email 1337986595@qq.com
 * @GitHub https://github.com/SmallBubble
 * @Gitte https://gitee.com/SmallCatBubble
 * @Desc
 */
public abstract class BaseMvpActivity<V extends BaseMvpView> extends RxAppCompatActivity {
    protected V mView;
    protected Context mContext;
    private Activity mActivity;

    private List<RequestCallBack> mCallBacks = new ArrayList<>();

    /**
     * 这里封装 一些创建流程的基本操作 非必要情況 建议不要重写这个方法
     *
     * @param savedInstanceState
     */
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

    /*======================必须重写===========================*/

    /**
     * 初始化 无关视图的初始化操作 比如
     */
    protected abstract void init();

    /**
     * 初始化视图
     */
    protected abstract void initView();

    /*======================选择重写===========================*/

    /**
     * 初始化事件监听
     */
    protected void initListener() {
    }

    /**
     * 获取数据 DB Net ...
     */
    protected void initData() {

    }


    /*======================私有方法/不建议重写===========================*/

    /**
     * 創建view 层
     */
    private void createView() {
        try {
            mView = getViewClass().newInstance();
        } catch (Exception e) {
            throw new RuntimeException("View 层创建失败");
        }
    }

    /**
     * 获取布局 View  继承这个类的子类统一通过这个方法
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

    /*======================通用方法===========================*/

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
