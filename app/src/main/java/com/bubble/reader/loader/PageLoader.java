package com.bubble.reader.loader;

import com.bubble.reader.page.bean.PageBean;

/**
 * @author Bubble
 * @date 2020/7/4
 * @email 1337986595@qq.com
 * @Desc 页面加载类
 */
public interface PageLoader {
    /**
     * 加载上一页
     *
     * @param currentPage 当前页
     */
    void loadPrePage(PageBean currentPage);

    /**
     * 加载下一页
     *
     * @param currentPage 当前页
     */
    void loadNextPage(PageBean currentPage);
}
