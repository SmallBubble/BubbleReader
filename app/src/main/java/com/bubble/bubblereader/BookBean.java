package com.bubble.bubblereader;

import android.os.Parcel;

import com.bubble.common.base.bean.SelectMultipleTypeBean;

/**
 * @author Bubble
 * @date 2020/7/8
 * @email 1337986595@qq.com
 * @GitHub https://github.com/SmallBubble
 * @Gitte https://gitee.com/SmallCatBubble
 * @Desc
 */
public class BookBean extends SelectMultipleTypeBean {
    private String mBookName;

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeString(this.mBookName);
    }

    public BookBean() {
    }

    protected BookBean(Parcel in) {
        super(in);
        this.mBookName = in.readString();
    }

    public static final Creator<BookBean> CREATOR = new Creator<BookBean>() {
        @Override
        public BookBean createFromParcel(Parcel source) {
            return new BookBean(source);
        }

        @Override
        public BookBean[] newArray(int size) {
            return new BookBean[size];
        }
    };
}
