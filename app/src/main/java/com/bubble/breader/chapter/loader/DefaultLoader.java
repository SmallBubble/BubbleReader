package com.bubble.breader.chapter.loader;

import android.app.Activity;

import com.bubble.breader.bean.Chapter;

/**
 * @author Bubble
 * @date 2020/7/16
 * @email 1337986595@qq.com
 * @GitHub https://github.com/SmallBubble
 * @Gitte https://gitee.com/SmallCatBubble
 * @Desc TODO
 */
public class DefaultLoader implements ChaptersLoader<Chapter>, ChapterLoader<Chapter> {
    private Activity mActivity;

    public DefaultLoader(Activity activity) {
        mActivity = activity;
    }

    @Override
    public void loadCache(ChaptersResult<Chapter> result) {

    }

    @Override
    public void loadChapter(boolean isPrepare, int needNo, ChapterResult<Chapter> result) {

    }

    @Override
    public void recycle() {
        // 回收资源
        mActivity = null;
    }
}
