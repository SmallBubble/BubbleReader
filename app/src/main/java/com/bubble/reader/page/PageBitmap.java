package com.bubble.reader.page;

import android.graphics.Bitmap;
import android.os.Parcel;
import android.os.Parcelable;

import com.bubble.reader.page.bean.PageBean;

import java.io.Serializable;

/**
 * packger：com.bubble.reader.page
 * auther：Bubble
 * date：2020/6/20
 * email：1337986595@qq.com
 * Desc：
 */
public class PageBitmap implements Parcelable, Serializable {
    private Bitmap mBitmap;
    private PageBean mPageBean;
    private int mTranslationY;
    private int type;

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getTranslationY() {
        return mTranslationY;
    }

    public void setTranslationY(int translationY) {
        mTranslationY = translationY;
    }

    public PageBean getPageBean() {
        return mPageBean;
    }

    public void setPageBean(PageBean pageBean) {
        mPageBean = pageBean;
    }

    public PageBitmap(Bitmap bitmap) {
        mBitmap = bitmap;
    }

    public Bitmap getBitmap() {
        return mBitmap;
    }

    public void setBitmap(Bitmap bitmap) {
        mBitmap = bitmap;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(this.mBitmap, flags);
        dest.writeParcelable(this.mPageBean, flags);
    }

    protected PageBitmap(Parcel in) {
        this.mBitmap = in.readParcelable(Bitmap.class.getClassLoader());
        this.mPageBean = in.readParcelable(PageBean.class.getClassLoader());
    }

    public static final Parcelable.Creator<PageBitmap> CREATOR = new Parcelable.Creator<PageBitmap>() {
        @Override
        public PageBitmap createFromParcel(Parcel source) {
            return new PageBitmap(source);
        }

        @Override
        public PageBitmap[] newArray(int size) {
            return new PageBitmap[size];
        }
    };
}
