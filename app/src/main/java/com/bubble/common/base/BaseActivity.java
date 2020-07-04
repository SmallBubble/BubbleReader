package com.bubble.common.base;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;

/**
 * packger：com.bubble.common.base
 * auther：Bubble
 * date：2020/6/19
 * email：1337986595@qq.com
 * Desc：
 */
public abstract class BaseActivity extends BaseMvpActivity<BaseView> {
    @Override
    public Class<BaseView> getViewClass() {
        return BaseView.class;
    }

    public abstract int getLayoutId();

    @Override
    protected View getContentView(Bundle savedInstanceState) {
        return LayoutInflater.from(mContext).inflate(getLayoutId(), null, false);
    }
}