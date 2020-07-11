package com.bubble.common.base.bean;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;

/**
 * @author Bubble
 * @date 2020/7/8
 * @email 1337986595@qq.com
 * @GitHub https://github.com/SmallBubble
 * @Gitte https://gitee.com/SmallCatBubble
 * @Desc
 */
public class SelectBean implements ISelectBean, Serializable, Parcelable {
    protected boolean mSelect;

    @Override
    public boolean isSelect() {
        return mSelect;
    }

    @Override
    public void setSelect(boolean select) {
        mSelect = select;
    }

    @Override
    public String toString() {
        return "SelectBean{" +
                "mSelect=" + mSelect +
                '}';
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeByte(this.mSelect ? (byte) 1 : (byte) 0);
    }

    public SelectBean() {
    }

    protected SelectBean(Parcel in) {
        this.mSelect = in.readByte() != 0;
    }

    public static final Parcelable.Creator<SelectBean> CREATOR = new Parcelable.Creator<SelectBean>() {
        @Override
        public SelectBean createFromParcel(Parcel source) {
            return new SelectBean(source);
        }

        @Override
        public SelectBean[] newArray(int size) {
            return new SelectBean[size];
        }
    };
}
