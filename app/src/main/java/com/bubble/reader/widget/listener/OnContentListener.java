package com.bubble.reader.widget.listener;

import com.bubble.reader.page.bean.PageResult;

/**
 * packger：com.bubble.reader.widget
 * author：Bubble
 * date：2020/6/21
 * email：1337986595@qq.com
 * Desc：
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
