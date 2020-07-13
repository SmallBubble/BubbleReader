package com.bubble.reader.page.bean;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Bubble
 * @date 2020/6/21
 * @email 1337986595@qq.com
 * @GitHub https://github.com/SmallBubble
 * @Gitte https://gitee.com/SmallCatBubble
 * @Desc
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
    private int mPageStart;
    private int mPageEnd;
    /**
     * 该页所在章节开始位置 和结束位置
     */
    private int mChapterStart;
    private int mChapterEnd;
    /**
     * 章节内容
     */
    private List<String> mContent;
    /**
     * 当前章节的阅读页数量
     */
    private int mPageCount;
    /**
     * 章节中的页码
     */
    private int mPageNum;


    public PageBean() {
        mContent = new ArrayList<>();
    }


    public void copyField(String chapterName, int chapterStart, int chapterEnd, int pageCount, int pageNum) {
        mChapterName = chapterName;
        mChapterStart = chapterStart;
        mChapterEnd = chapterEnd;
        mPageCount = pageCount;
        mPageNum = pageNum;
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

    public int getPageStart() {
        return mPageStart;
    }

    public void setPageStart(int pageStart) {
        mPageStart = pageStart;
    }

    public int getPageEnd() {
        return mPageEnd;
    }

    public void setPageEnd(int pageEnd) {
        mPageEnd = pageEnd;
    }

    public int getChapterStart() {
        return mChapterStart;
    }

    public void setChapterStart(int chapterStart) {
        mChapterStart = chapterStart;
    }

    public int getChapterEnd() {
        return mChapterEnd;
    }

    public void setChapterEnd(int chapterEnd) {
        mChapterEnd = chapterEnd;
    }

    public List<String> getContent() {
        return mContent;
    }

    public void setContent(List<String> content) {
        mContent = content;
    }

    public int getPageCount() {
        return mPageCount;
    }

    public void setPageCount(int pageCount) {
        mPageCount = pageCount;
    }

    public int getPageNum() {
        return mPageNum;
    }

    public void setPageNum(int pageNum) {
        mPageNum = pageNum;
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
        dest.writeInt(this.mPageStart);
        dest.writeInt(this.mPageEnd);
        dest.writeStringList(this.mContent);
        dest.writeInt(this.mPageCount);
        dest.writeInt(this.mPageNum);
    }

    protected PageBean(Parcel in) {
        this.mChapterName = in.readString();
        this.mBookStart = in.readByte() != 0;
        this.mBookEnd = in.readByte() != 0;
        this.mPageStart = in.readInt();
        this.mPageEnd = in.readInt();
        this.mContent = in.createStringArrayList();
        this.mPageCount = in.readInt();
        this.mPageNum = in.readInt();
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
