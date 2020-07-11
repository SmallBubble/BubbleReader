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
public class SelectMultipleTypeBean implements IMultipleType, ISelectBean , Serializable, Parcelable {
    protected boolean mSelect;
    protected int mType;

    public SelectMultipleTypeBean(boolean select, int type) {
        mSelect = select;
        mType = type;
    }

    public static final Creator<SelectMultipleTypeBean> CREATOR = new Creator<SelectMultipleTypeBean>() {
        @Override
        public SelectMultipleTypeBean createFromParcel(Parcel in) {
            return new SelectMultipleTypeBean(in);
        }

        @Override
        public SelectMultipleTypeBean[] newArray(int size) {
            return new SelectMultipleTypeBean[size];
        }
    };

    @Override
    public int getType() {
        return mType;
    }

    @Override
    public void setType(int type) {
        mType = type;
    }

    @Override
    public boolean isSelect() {
        return mSelect;
    }

    @Override
    public void setSelect(boolean select) {
        mSelect = select;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeByte(this.mSelect ? (byte) 1 : (byte) 0);
        dest.writeInt(this.mType);
    }

    public SelectMultipleTypeBean() {
    }

    protected SelectMultipleTypeBean(Parcel in) {
        this.mSelect = in.readByte() != 0;
        this.mType = in.readInt();
    }

}
