package com.bubble.common.base.bean;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;

/**
 * @author Bubble
 * @date 2020/7/7
 * @email 1337986595@qq.com
 * @GitHub https://github.com/SmallBubble
 * @Gitte https://gitee.com/SmallCatBubble
 * @Desc 多种
 */
public class MultipleType implements IMultipleType, Serializable, Parcelable {
    protected int mType;

    public MultipleType(int type) {
        mType = type;
    }

    public MultipleType() {
    }

    @Override
    public int getType() {
        return mType;
    }

    @Override
    public void setType(int type) {
        mType = type;
    }

    @Override
    public String toString() {
        return "MultipleType{" +
                "mType=" + mType +
                '}';
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.mType);
    }

    protected MultipleType(Parcel in) {
        this.mType = in.readInt();
    }

    public static final Parcelable.Creator<MultipleType> CREATOR = new Parcelable.Creator<MultipleType>() {
        @Override
        public MultipleType createFromParcel(Parcel source) {
            return new MultipleType(source);
        }

        @Override
        public MultipleType[] newArray(int size) {
            return new MultipleType[size];
        }
    };
}
