package com.bubble.reader.page;

import android.graphics.Bitmap;

import java.util.List;

/**
 * packger：com.bubble.reader.page
 * auther：Bubble
 * date：2020/6/20
 * email：1337986595@qq.com
 * Desc：
 */
public class PageBitmap {
    private Bitmap mBitmap;
    private List<String> mContent;

    public PageBitmap(Bitmap bitmap) {
        mBitmap = bitmap;
    }

    public Bitmap getBitmap() {
        return mBitmap;
    }

    public void setBitmap(Bitmap bitmap) {
        mBitmap = bitmap;
    }

    public List<String> getContent() {
        return mContent;
    }

    public void setContent(List<String> content) {
        mContent = content;
    }
}
