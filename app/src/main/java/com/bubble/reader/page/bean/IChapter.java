package com.bubble.reader.page.bean;

/**
 * @author Bubble
 * @date 2020/7/12
 * @email 1337986595@qq.com
 * @GitHub https://github.com/SmallBubble
 * @Gitte https://gitee.com/SmallCatBubble
 * @Desc 具体项目的章节结构自行设计 实现这个接口 返回需要的信息即可
 */
public interface IChapter {
    String getChapterName();

    int getChapterNo();

    int getChapterCount();

    boolean isBookStart();

    boolean isBookEnd();

    String getContent();

}
