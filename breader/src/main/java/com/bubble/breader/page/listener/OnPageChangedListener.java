package com.bubble.breader.page.listener;

import com.bubble.breader.bean.Page;

/**
 * @author Bubble
 * @date 2020/7/11
 * @email 1337986595@qq.com
 * @GitHub https://github.com/SmallBubble
 * @Gitte https://gitee.com/SmallCatBubble
 * @Desc
 */
public interface OnPageChangedListener {
    /**
     * 阅读页发生改变
     *
     * @param pageBean 当前改变的页面
     */
    void onPageChanged(Page pageBean);
}
