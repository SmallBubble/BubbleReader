package com.bubble.reader.page.listener;

/**
 * packger：com.bubble.reader.page.event
 * author：Bubble
 * date：2020/6/27
 * email：1337986595@qq.com
 * Desc：
 */
public abstract class PageListener {
    public final static int TYPE_ERROR = 0x1;
    public final static int TYPE_SUCCESS = 0x2;
    public final static int TYPE_PAGE_LOAD_FINISHED = 0x3;
    public final static int TYPE_BOOK_FINISHED = 0x4;
    public final static int TYPE_BOOK_START = 0x5;

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
     * 加载结束
     */
    public void onPageLoadFinished() {

    }

    /**
     * 书籍结束 后面没有内容了
     */
    public void onBookFinished() {

    }

    /**
     * 书籍开始 前面没有内容了
     */
    public void onBookStart() {

    }

}
