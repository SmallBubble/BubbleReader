package com.bubble.reader.widget.listener;

import com.bubble.reader.page.bean.PageResult;

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
     */
    PageResult onNextPage();

    /**
     * 上一页
     */
    PageResult onPrePage();

    /**
     * 取消翻页
     */
    void onCancel();

}
