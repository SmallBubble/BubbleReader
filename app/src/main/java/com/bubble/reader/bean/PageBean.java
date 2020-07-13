package com.bubble.reader.bean;

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
    private int mChapterNo;
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

    public int getChapterNo() {
        return mChapterNo;
    }

    public void setChapterNo(int chapterNo) {
        mChapterNo = chapterNo;
    }

    public String getChapterName() {
        return mChapterName;
    }

    public void setChapterName(String chapterName) {
        mChapterName = chapterName;
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
        dest.writeInt(this.mChapterNo);
        dest.writeStringList(this.mContent);
        dest.writeInt(this.mPageCount);
        dest.writeInt(this.mPageNum);
    }

    protected PageBean(Parcel in) {
        this.mChapterName = in.readString();
        this.mChapterNo = in.readInt();
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
