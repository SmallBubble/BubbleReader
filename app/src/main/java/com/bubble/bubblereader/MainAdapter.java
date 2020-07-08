package com.bubble.bubblereader;

import android.content.Context;
import android.view.ViewGroup;

import com.bubble.common.base.adapter.TipsAdapter;
import com.bubble.common.base.adapter.ViewHolder;

import java.util.List;

/**
 * @author Bubble
 * @date 2020/7/7
 * @email 1337986595@qq.com
 * @GitHub https://github.com/SmallBubble
 * @Gitte https://gitee.com/SmallCatBubble
 * @Desc
 */
public class MainAdapter extends TipsAdapter<String> {
    public MainAdapter(Context context) {
        super(context);
    }

    @Override
    protected ViewHolder createDataViewHolder(ViewGroup parent, int viewType) {
        return null;
    }

    @Override
    protected void bindData(ViewHolder holder, int position, List<Object> payloads) {

    }
}
