package com.bubble.common.base;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * packger：com.bubble.common.base
 * auther：Bubble
 * date：2020/6/20
 * email：jiaxiang6595@foxmail.com
 * Desc：
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

    protected abstract int getLayoutId();

    protected View getContentView() {
        return mContentView;
    }
}
