package com.bubble.breader.bean;

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
 * @Desc 默认实现的Page 实体类
 */
public class Page implements IPage, Parcelable, Serializable {
    /**
     * 章节名称
     */
    private String mChapterName;
    /**
     * 章节No
     */
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

    public Page() {
        mContent = new ArrayList<>();
    }

    public void setChapterNo(int chapterNo) {
        mChapterNo = chapterNo;
    }

    public void setChapterName(String chapterName) {
        mChapterName = chapterName;
    }

    public void setContent(List<String> content) {
        mContent = content;
    }

    public void setPageCount(int pageCount) {
        mPageCount = pageCount;
    }

    public void setPageNum(int pageNum) {
        mPageNum = pageNum;
    }

    @Override
    public int getChapterNo() {
        return mChapterNo;
    }

    @Override
    public String getChapterName() {
        return mChapterName;
    }

    @Override
    public List<String> getContent() {
        return mContent;
    }

    @Override
    public int getPageCount() {
        return mPageCount;
    }

    @Override
    public int getPageNum() {
        return mPageNum;
    }


    @Override
    public String toString() {
        return "PageBean{" +
                "mChapterName='" + mChapterName + '\'' +
                ", mChapterNo=" + mChapterNo +
                ", mContent=" + mContent +
                ", mPageCount=" + mPageCount +
                ", mPageNum=" + mPageNum +
                '}';
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

    protected Page(Parcel in) {
        this.mChapterName = in.readString();
        this.mChapterNo = in.readInt();
        this.mContent = in.createStringArrayList();
        this.mPageCount = in.readInt();
        this.mPageNum = in.readInt();
    }

    public static final Creator<Page> CREATOR = new Creator<Page>() {
        @Override
        public Page createFromParcel(Parcel source) {
            return new Page(source);
        }

        @Override
        public Page[] newArray(int size) {
            return new Page[size];
        }
    };
}
