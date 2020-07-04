package com.bubble.reader.widget.listener;

/**
 * packger：com.bubble.reader.widget
 * auther：Bubble
 * date：2020/6/21
 * email：1337986595@qq.com
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
