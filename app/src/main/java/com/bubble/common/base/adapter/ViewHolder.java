package com.bubble.common.base.adapter;

import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.DrawableRes;
import androidx.annotation.IdRes;
import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

/**
 * @author Bubble
 * @date 2020/7/7
 * @email jiaxiang6595@foxmail.com
 * @Desc
 */
public class ViewHolder extends RecyclerView.ViewHolder {
    private SparseArray<View> mViews = new SparseArray<>();

    public ViewHolder(@NonNull ViewGroup parent, @LayoutRes int layoutId) {
        super(LayoutInflater.from(parent.getContext()).inflate(layoutId, parent, false));
    }


    public void setText(@IdRes int id, String text) {
        if (checkViewNull(id)) {
            getView(TextView.class, id).setText(text);
        }
    }

    public void setImageRes(@IdRes int id, @DrawableRes int resId) {
        if (checkViewNull(id)) {
            getView(ImageView.class, id).setImageResource(resId);
        }
    }

    public void setVisible(@IdRes int id, boolean visible) {
        if (checkViewNull(id)) {
            getView(id).setVisibility(visible ? View.VISIBLE : View.GONE);
        }
    }

    public boolean checkViewNull(@IdRes int id) {
        return mViews.get(id) != null;
    }

    public <T extends View> T getView(@IdRes int id) {
        if (checkViewNull(id)) {
            mViews.put(id, itemView.findViewById(id));
        }
        return (T) mViews.get(id);
    }


    public <T extends View> T getView(Class<T> clazz, @IdRes int id) {
        return getView(id);
    }

    public <T extends View.OnClickListener> void setOnClickListener(T listener, @IdRes int... ids) {
        for (int id : ids) {
            if (getView(id) != null) {
                getView(id).setOnClickListener(listener);
            }
        }
    }
}
