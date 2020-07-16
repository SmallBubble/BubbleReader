package com.bubble.reader.bean;

/**
 * @author Bubble
 * @date 2020/7/12
 * @email 1337986595@qq.com
 * @GitHub https://github.com/SmallBubble
 * @Gitte https://gitee.com/SmallCatBubble
 * @Desc 具体项目的章节结构自行设计 实现这个接口 返回需要的信息即可
 */
public interface IChapter {
    /**
     * 获取章节名称
     *
     * @return
     */
    String getChapterName();

    /**
     * 获取章节号
     *
     * @return
     */
    int getChapterNo();

    /**
     * 获取章节数量
     *
     * @return
     */
    int getChapterCount();

    /**
     * 是否书籍开始
     *
     * @return
     */
    boolean isBookStart();

    /**
     * 是否书籍结束
     *
     * @return
     */
    boolean isBookEnd();

    /**
     * 获取章节内容
     *
     * @return
     */
    String getContent();

    boolean isLoaded();

    void setLoaded(boolean loaded);

}
