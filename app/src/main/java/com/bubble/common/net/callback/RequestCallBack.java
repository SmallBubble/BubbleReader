package com.bubble.common.net.callback;

import com.bubble.common.net.JsonResult;

import io.reactivex.observers.DisposableObserver;
import retrofit2.HttpException;

/**
 * @author WeiJiaxiang
 * @date 2020/7/8
 * @Desc
 */
public abstract class RequestCallBack<T> extends DisposableObserver<JsonResult<T>> {


    @Override
    public void onNext(JsonResult<T> result) {
        if (isDisposed()) {
            return;
        }
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

    protected abstract void onSuccess(T data);

    protected abstract void onFailure(int code, String message);

    @Override
    public void onComplete() {

    }

}
