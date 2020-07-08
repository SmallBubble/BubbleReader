package com.bubble.common.base.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bubble.common.listener.OnItemClickListener;
import com.bubble.common.listener.SingleListener;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Bubble
 * @date 2020/7/7
 * @email jiaxiang6595@foxmail.com
 * @Desc
 */
public abstract class BaseAdapter<T> extends RecyclerView.Adapter<ViewHolder> {
    protected OnItemClickListener<T> mItemClickListener;
    protected List<T> mData = new ArrayList<>();
    protected Context mContext;

    public BaseAdapter(Context context) {
        mContext = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return createDataViewHolder(parent, viewType);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position, @NonNull List<Object> payloads) {
        super.onBindViewHolder(holder, position, payloads);
        holder.itemView.setOnClickListener(new SingleListener() {
            @Override
            public void onSingleClick(View view) {
                if (mItemClickListener != null)
                    mItemClickListener.onItemClicked(mData, position);
            }
        });
        bindData(holder, position, payloads);
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }


    /*============================子类重写方法=========================*/

    /***
     * 创建视图
     * @param parent
     * @param viewType
     * @return
     */
    protected abstract ViewHolder createDataViewHolder(ViewGroup parent, int viewType);

    /**
     * 绑定数据
     *
     * @param holder
     * @param position
     * @param payloads
     */
    protected abstract void bindData(ViewHolder holder, int position, List<Object> payloads);


    /*============================设置数据=========================*/

    /**********************刷新数据********************/
    /**
     * 刷新所有数据
     *
     * @param data 数据集
     */
    public void notifyDataSetChanged(List<T> data) {
        notifyDataSetChanged(data, true);
    }

    /**
     * 刷新所有数据
     *
     * @param data  数据集
     * @param clear 是否清空数据
     */
    public void notifyDataSetChanged(List<T> data, boolean clear) {
        if (clear) {
            mData.clear();
        }
        mData.addAll(data);
        notifyDataSetChanged();
    }

    /**
     * 替换某项数据并刷新
     *
     * @param data     数据
     * @param position 刷新的位置
     */
    public void notifyItemChanged(T data, int position) {
        if (position >= mData.size()) return;
        mData.remove(position);
        mData.add(position, data);
        notifyItemChanged(position);
    }

    /**********************获取数据********************/
    public List<T> getData() {
        return mData;
    }

    public T getItem(int position) {
        return mData.size() <= position ? null : mData.get(position);
    }

    /*============================get/set方法=========================*/
    public OnItemClickListener<T> getItemClickListener() {
        return mItemClickListener;
    }

    public void setItemClickListener(OnItemClickListener<T> itemClickListener) {
        mItemClickListener = itemClickListener;
    }
}
