package com.bubble.reader.page.bean;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * packger：com.bubble.reader.page.bean
 * auther：Bubble
 * date：2020/6/21
 * email：1337986595@qq.com
 * Desc：
 */
public class PageBean implements Parcelable, Serializable {
    /**
     * 章节名称
     */
    private String mChapterName;
    /**
     * 是否是书籍开始和结束
     */
    private boolean mBookStart;
    private boolean mBookEnd;
    /**
     * 页开始位置 和结束位置
     */
    private long mPageStart;
    private long mPageEnd;
    /**
     * 章节中的页码
     */
    private int mChapterPage;
    /**
     * 章节内容
     */
    private List<String> mContent;

    private int mPageCount;

    public int getPageCount() {
        return mPageCount;
    }

    public void setPageCount(int pageCount) {
        mPageCount = pageCount;
    }

    public PageBean() {
        mContent = new ArrayList<>();
    }

    public String getChapterName() {
        return mChapterName;
    }

    public void setChapterName(String chapterName) {
        mChapterName = chapterName;
    }

    public boolean isBookStart() {
        return mBookStart;
    }

    public void setBookStart(boolean bookStart) {
        mBookStart = bookStart;
    }

    public boolean isBookEnd() {
        return mBookEnd;
    }

    public void setBookEnd(boolean bookEnd) {
        mBookEnd = bookEnd;
    }

    public long getPageStart() {
        return mPageStart;
    }

    public void setPageStart(long pageStart) {
        mPageStart = pageStart;
    }

    public long getPageEnd() {
        return mPageEnd;
    }

    public void setPageEnd(long pageEnd) {
        mPageEnd = pageEnd;
    }

    public int getChapterPage() {
        return mChapterPage;
    }

    public void setChapterPage(int chapterPage) {
        mChapterPage = chapterPage;
    }

    public List<String> getContent() {
        return mContent;
    }

    public void setContent(List<String> content) {
        mContent = content;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.mChapterName);
        dest.writeByte(this.mBookStart ? (byte) 1 : (byte) 0);
        dest.writeByte(this.mBookEnd ? (byte) 1 : (byte) 0);
        dest.writeLong(this.mPageStart);
        dest.writeLong(this.mPageEnd);
        dest.writeInt(this.mChapterPage);
        dest.writeStringList(this.mContent);
        dest.writeInt(this.mPageCount);
    }

    protected PageBean(Parcel in) {
        this.mChapterName = in.readString();
        this.mBookStart = in.readByte() != 0;
        this.mBookEnd = in.readByte() != 0;
        this.mPageStart = in.readLong();
        this.mPageEnd = in.readLong();
        this.mChapterPage = in.readInt();
        this.mContent = in.createStringArrayList();
        this.mPageCount = in.readInt();
    }

    public static final Creator<PageBean> CREATOR = new Creator<PageBean>() {
        @Override
        public PageBean createFromParcel(Parcel source) {
            return new PageBean(source);
        }

        @Override
        public PageBean[] newArray(int size) {
            return new PageBean[size];
        }
    };
}
