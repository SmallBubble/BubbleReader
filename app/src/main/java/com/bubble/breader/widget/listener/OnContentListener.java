package com.bubble.breader.widget.listener;

import com.bubble.breader.bean.PageResult;

/**
 * @author Bubble
 * @date 2020/6/21
 * @email 1337986595@qq.com
 * @GitHub https://github.com/SmallBubble
 * @Gitte https://gitee.com/SmallCatBubble
 * @Desc 内容监听
 */
public interface OnContentListener {
    /**
     * 下一页
     *
     * @return
     */
    PageResult onNextPage();

    /**
     * 上一页
     *
     * @return
     */
    PageResult onPrePage();

    /**
     * 取消翻页
     */
    void onCancel();

}
