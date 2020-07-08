package com.bubble.common.base;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;

/**
 * @author Bubble
 * @date 2020/6/19
 * @email 1337986595@qq.com
 * @GitHub https://github.com/SmallBubble
 * @Gitte https://gitee.com/SmallCatBubble
 * <p>
 * Desc：Activity基类 继承自{@link BaseMvpActivity} 创建一个默认的View层
 */
public abstract class BaseActivity extends BaseMvpActivity<BaseView> {
    @Override
    public Class<BaseView> getViewClass() {
        return BaseView.class;
    }

    /**
     * 获取布局id
     *
     * @return
     */
    public abstract int getLayoutId();

    /**
     * 获取ContentView
     * <p>
     * 重写自 {@link BaseMvpActivity#getContentView(Bundle)} 方法
     * <p>
     * 这个基类 中不需要通过view层获取布局 直接通过 {@link BaseActivity#getLayoutId }设置布局
     *
     * @param savedInstanceState
     * @return
     */
    @Override
    protected final View getContentView(Bundle savedInstanceState) {
        return LayoutInflater.from(mContext).inflate(getLayoutId(), null, false);
    }
}