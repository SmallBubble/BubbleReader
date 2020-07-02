package com.bubble.reader.page.event;

/**
 * packger：com.bubble.reader.page.event
 * auther：Bubble
 * date：2020/6/27
 * email：jiaxiang6595@foxmail.com
 * Desc：
 */
public interface PageListener {
    /**
     * 解析错误
     *
     * @param message
     */
    void onError(String message);

    /**
     * 解析成功
     */
    void onSuccess();

}
