package com.bubble.reader.page.listener;

/**
 * packger：com.bubble.reader.page.event
 * auther：Bubble
 * date：2020/6/27
 * email：1337986595@qq.com
 * Desc：
 */
public abstract class PageListener {
    /**
     * 解析错误
     *
     * @param message
     */
    public void onError(String message) {

    }

    /**
     * 解析成功
     */
    public void onSuccess() {

    }

    /**
     * 下一页回调
     *
     * @param hasNext 有下一页 true ： 有/false：没有下一页
     */
    public void onNextPage(boolean hasNext) {

    }

    /**
     * 上一页回调
     *
     * @param hasPre 有上一页 true ： 有/false：没有
     */
    public void onPrePage(boolean hasPre) {

    }
}
