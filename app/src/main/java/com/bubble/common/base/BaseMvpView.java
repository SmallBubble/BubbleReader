package com.bubble.common.base;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * @author Bubble
 * @date 2020/6/20
 * @email 1337986595@qq.com
 * @GitHub https://github.com/SmallBubble
 * @Gitte https://gitee.com/SmallCatBubble
 * @Desc
 */
public abstract class BaseMvpView {
    protected Context mContext;
    private View mContentView;

    public View onCreate(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        mContentView = inflater.inflate(getLayoutId(), parent, false);
        return mContentView;
    }

    public Context getContext() {
        return mContext;
    }

    protected void initView() {

    }

    /**
     * 获取布局id
     *
     * @return
     */
    protected abstract int getLayoutId();

    /**
     * 获取布局
     *
     * @return
     */
    protected View getContentView() {
        return mContentView;
    }
}
