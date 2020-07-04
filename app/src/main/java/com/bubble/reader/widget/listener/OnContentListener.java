package com.bubble.reader.widget.listener;

/**
 * packger：com.bubble.reader.widget
 * auther：Bubble
 * date：2020/6/21
 * email：jiaxiang6595@foxmail.com
 * Desc：
 */
public interface OnContentListener {


    /**
     * 下一页
     */
    boolean onNextPage();

    /**
     * 上一页
     */
    boolean onPrePage();

    /**
     * 取消翻页
     */
    void onCancel();

}
