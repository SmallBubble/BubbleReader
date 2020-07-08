package com.bubble.common.listener;

import java.util.List;

/**
 * @author Bubble
 * @date 2020/7/7
 * @email jiaxiang6595@foxmail.com
 * @Desc 列表Item点击事件监听
 */
public interface OnItemClickListener<T> {
    void onItemClicked(List<T> data, int position);
}
