package com.bubble.common.net;

import java.util.ArrayList;
import java.util.List;

/**
 * @author WeiJiaxiang
 * Date：2020/7/8
 * Desc：
 */
public class CallBackManager<T> extends RequestCallBack<T> {

    private List<ICallBack<T>> mCallBacks = new ArrayList<>();

    public CallBackManager<T> create() {
        return this;
    }

    public CallBackManager<T> addCallBack(ICallBack<T> callBack) {
        mCallBacks.add(callBack);
        return this;
    }

    @Override
    public void onSuccess(T data) {
        for (ICallBack<T> callBack : mCallBacks) {
            callBack.onSuccess(data);
        }
    }

    @Override
    public void onFailure(int code, String message) {
        for (ICallBack<T> callBack : mCallBacks) {
            callBack.onFailure(code, message);
        }
    }
}
