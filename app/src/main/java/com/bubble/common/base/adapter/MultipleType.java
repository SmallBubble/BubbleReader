package com.bubble.common.base.adapter;

/**
 * @author Bubble
 * @date 2020/7/7
 * @email 1337986595@qq.com
 * @GitHub https://github.com/SmallBubble
 * @Gitte https://gitee.com/SmallCatBubble
 * @Desc 多种
 */
public class MultipleType<T> {
    private int mType;
    private T mData;

    public MultipleType(int type, T data) {
        mType = type;
        mData = data;
    }

    public MultipleType() {
    }

    public int getType() {
        return mType;
    }

    public void setType(int type) {
        mType = type;
    }

    public T getData() {
        return mData;
    }

    public void setData(T data) {
        mData = data;
    }

    @Override
    public String toString() {
        return "MultipleType{" +
                "mType=" + mType +
                ", mData=" + mData +
                '}';
    }
}
