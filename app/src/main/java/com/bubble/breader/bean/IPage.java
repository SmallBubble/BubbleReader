package com.bubble.breader.bean;

import java.util.List;

/**
 * @author Bubble
 * @date 2020/7/17
 * @email 1337986595@qq.com
 * @GitHub https://github.com/SmallBubble
 * @Gitte https://gitee.com/SmallCatBubble
 * @Desc 页面接口 具体app中实现该接口
 */
public interface IPage {
    List<String> getContent();

    int getChapterNo();

    String getChapterName();

    int getPageCount();

    int getPageNum();

}
