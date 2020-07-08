package com.bubble.common.base.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.DrawableRes;
import androidx.annotation.IdRes;
import androidx.annotation.NonNull;

import com.bubble.bubblereader.R;
import com.bubble.common.listener.OnAdapterTipsClickListener;
import com.bubble.common.listener.SingleListener;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Bubble
 * @date 2020/7/7
 * @email 1337986595@qq.com
 * @GitHub https://github.com/SmallBubble
 * @Gitte https://gitee.com/SmallCatBubble
 * @Desc 带有tips 的通用Adapter
 */
public abstract class TipsAdapter<T> extends BaseAdapter<MultipleType> {
    public final static int TYPE_EMPTY = 0x10000000;
    public final static int TYPE_NETWORK_ERROR = 0x20000000;
    private OnAdapterTipsClickListener mTipsClickListener;
    private int[] mTipIds;
    private int mTipsIcon;
    private String mTips;

    public TipsAdapter(Context context) {
        super(context);
    }

    public void addAll(List<T> data) {
        List<MultipleType<T>> types = new ArrayList<>();
        for (T t : data) {
            types.add(new MultipleType<>());
        }
    }

    @NonNull
    @Override
    public final ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        switch (viewType) {
            case TYPE_EMPTY:
                return new ViewHolder(parent, getEmptyLayoutId());
            case TYPE_NETWORK_ERROR:
                return new ViewHolder(parent, getNetworkErrorLayoutId());
            default:
                return createDataViewHolder(parent, viewType);
        }
    }

    @Override
    public final void onBindViewHolder(@NonNull ViewHolder holder, int position, @NonNull List<Object> payloads) {
        super.onBindViewHolder(holder, position, payloads);
        switch (holder.getItemViewType()) {
            case TYPE_EMPTY:
            case TYPE_NETWORK_ERROR:
                bindTips(holder, position, payloads);
            default:
                bindData(holder, position, payloads);
                holder.itemView.setOnClickListener(new SingleListener() {
                    @Override
                    public void onSingleClick(View view) {
                        if (mItemClickListener != null)
                            mItemClickListener.onItemClicked(mData, position);
                    }
                });
        }
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

    }

    protected int getNetworkErrorLayoutId() {
        return R.layout.item_network_error;
    }

    protected int getEmptyLayoutId() {
        return R.layout.item_empty;
    }

    /*============================子类重写方法=========================*/

    /**
     * 绑定tips信息 如果设置自定义tips的情况下
     *
     * @param holder   holder
     * @param position 位置
     * @param payloads
     */
    protected void bindTips(ViewHolder holder, int position, List<Object> payloads) {
        if (getEmptyLayoutId() <= 0) {
            holder.setText(R.id.tvContent, mTips);
            holder.setImageRes(R.id.tvContent, mTipsIcon);
        }
    }

    /*============================tips显示区=========================*/

    public void showEmptyData() {
        showEmptyData("暂无数据");
    }

    public void showEmptyData(String tips) {
        showEmptyData(R.drawable.ic_launcher_background, tips);
    }

    public void showEmptyData(@DrawableRes int resId, String tips) {
        mTips = tips;
        mTipsIcon = resId;
        mData.add(new MultipleType<>(TYPE_EMPTY, null));
    }

    public void showNetworkError() {
        showNetworkError("加载失败了哦");
    }

    public void showNetworkError(String tips) {
        mTips = tips;
        mData.add(new MultipleType<>(TYPE_NETWORK_ERROR, null));
    }


    /*============================set/get方法区=========================*/
    public OnAdapterTipsClickListener getTipsClickListener() {
        return mTipsClickListener;
    }

    public void setTipsClickListener(OnAdapterTipsClickListener tipsClickListener, @IdRes int... ids) {
        mTipIds = ids;
        mTipsClickListener = tipsClickListener;
    }
}
