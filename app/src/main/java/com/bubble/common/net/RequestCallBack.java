package com.bubble.common.net;

import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import retrofit2.HttpException;

/**
 * @author WeiJiaxiang
 * Date：2020/7/8
 * Desc：
 */
public abstract class RequestCallBack<T> implements Observer<JsonResult<T>> {

    @Override
    public void onSubscribe(Disposable d) {

    }

    @Override
    public void onNext(JsonResult<T> result) {
//        if (isDisposed()) {
//            return;
//        }
        if (result == null) {
            onFailure(-1, "网络请求失败!");
        } else {
            if (result.getCode() == 200) {
                onSuccess(result.getData());
            } else {
                onFailure(result.getCode(), result.getMessage());
            }
        }
    }

    @Override
    public void onError(Throwable e) {
        if (e instanceof HttpException) {
            onFailure(-1, "网络请求失败!");
        } else if (e instanceof NumberFormatException) {
            onFailure(-2, "數字格式失败!");
        } else {
            onFailure(-2, "數字格式失败!");
        }
    }

    @Override
    public void onComplete() {

    }

    /**
     * 请求成功
     *
     * @param data
     */
    public abstract void onSuccess(T data);

    /**
     * 请求失败
     *
     * @param code    錯誤碼
     * @param message 返回消息
     */
    public abstract void onFailure(int code, String message);
}
