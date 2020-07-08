package com.bubble.common.net.callback;

/**
 * @author WeiJiaxiang
 * Date：2020/7/8
 * Desc：
 */
public interface ICallBack<T> {
    /**
     * 请求成功
     *
     * @param data
     */
    void onSuccess(T data);

    /**
     * 请求失败
     *
     * @param code    錯誤碼
     * @param message 返回消息
     */
    void onFailure(int code, String message);
}
