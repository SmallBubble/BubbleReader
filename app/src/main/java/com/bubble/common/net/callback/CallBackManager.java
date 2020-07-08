package com.bubble.common.net.callback;

import android.util.SparseArray;

/**
 * @author WeiJiaxiang
 * Date：2020/7/8
 * Desc：
 */
public class CallBackManager<T> extends RequestCallBack<T> {

    private static final int KEY_EMPTY = 1;
    private static final int KEY_NETWORK_ERROR = 2;
    private static final int KEY_DATA = 0;
    private SparseArray<ICallBack<T>> mCallBacks = new SparseArray<>();

    private CallBackManager() {

    }

    public static <T> CallBackManager<T> create() {
        return new CallBackManager<T>();
    }

    private CallBackManager<T> addCallBack(int key, ICallBack<T> callBack) {
        mCallBacks.put(key, callBack);
        return this;
    }

    public CallBackManager<T> addEmptyCallback(EmptyCallBack<T> callBack) {
        return addCallBack(KEY_EMPTY, callBack);
    }

    public CallBackManager<T> addDataCallback(DataCallBack<T> callBack) {
        return addCallBack(KEY_NETWORK_ERROR, callBack);
    }

    public CallBackManager<T> addNetworkErrorCallback(NetworkErrorCallBack<T> callBack) {
        return addCallBack(KEY_DATA, callBack);
    }

    @Override
    public void onSuccess(T data) {
    }

    @Override
    public void onFailure(int code, String message) {
    }
}
