package com.wangjie.seizerecyclerview;

import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

/**
 * Author: wangjie
 * Email: tiantian.china.2@gmail.com
 * Date: 3/27/17.
 */
public abstract class BaseSeizeAdapter implements SeizeAdapter<BaseViewHolder> {
    private static final String TAG = BaseSeizeAdapter.class.getSimpleName();
    protected RecyclerView.Adapter<BaseViewHolder> parentAdapter;
    public static final int TYPE_DEFAULT = 0x8682;
    protected int typeHeaderDefault = -0x8683;
    protected int typeFooterDefault = -0x8684;
    private View headerView;
    private View footerView;

    @Override
    public void setHeader(View view) {
        headerView = view;
        // TODO: 3/29/17 wangjie optim
        typeHeaderDefault = this.hashCode();
    }

    @Override
    public void setFooter(View view) {
        footerView = view;
        // TODO: 3/29/17 wangjie optim
        typeFooterDefault = this.hashCode() - 1;
    }

    @Override
    @Nullable
    public final BaseViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        try {
            return createTypeViewHolderInternal(parent, viewType);
        } catch (Throwable throwable) {
            Log.e(TAG, "onCreateViewHolder", throwable);
        }
        return null;
    }

    @Nullable
    private BaseViewHolder createTypeViewHolderInternal(ViewGroup parent, int viewType) {
        if (viewType == typeHeaderDefault) {
            return new EmptyViewHolder(headerView);
        } else if (viewType == typeFooterDefault) {
            return new EmptyViewHolder(footerView);
        } else {
            return onCreateTypeViewHolder(parent, viewType);
        }
    }

    @Nullable
    public abstract BaseViewHolder onCreateTypeViewHolder(ViewGroup parent, int type);

    @Override
    public void onBindViewHolder(BaseViewHolder holder, SeizePosition seizePosition) {
        try {
            holder.onBindViewHolderInternal(holder, seizePosition);
        } catch (Throwable throwable) {
            Log.e(TAG, "onBindViewHolder", throwable);
        }
    }

    @Override
    public void setParentAdapter(RecyclerView.Adapter<BaseViewHolder> parentAdapter) {
        this.parentAdapter = parentAdapter;
    }

    @Override
    public boolean hasViewType(int viewType) {
        return true;
    }

    @Override
    public final int getItemCount() {
        return getSourceItemCount() + getCount(headerView) + getCount(footerView);
    }

    private int getCount(View view) {
        return null == view ? 0 : 1;
    }

    @Override
    public long getItemId(int subPosition) {
        return subPosition;
    }

    public abstract Object getItem(int subSourcePosition);

    @Override
    public final int getItemViewType(SeizePosition seizePosition) {
        int subPosition = seizePosition.getSubPosition();
        if (isHeader(subPosition)) {
            return typeHeaderDefault;
        } else if (isFooter(subPosition)) {
            return typeFooterDefault;
        } else {
            return getSourceItemViewType(seizePosition.getSubSourcePosition());
        }
    }

    @Override
    public boolean isHeader(int subPosition) {
        int headerCount = getCount(headerView);
        return 0 != headerCount && subPosition <= headerCount - 1;
    }

    @Override
    public boolean isFooter(int subPosition) {
        int footerCount = getCount(footerView);
        return 0 != footerCount && subPosition >= getItemCount() - footerCount;
    }

    @Override
    public int subPositionToSubSourcePosition(int subPosition) {
        return subPosition - getCount(headerView);
    }

    @Override
    public int subSourcePositionToSubPosition(int subSourcePosition) {
        return subSourcePosition + getCount(headerView);
    }

    public abstract int getSourceItemCount();

    public int getSourceItemViewType(int subSourcePosition) {
        return TYPE_DEFAULT;
    }

    @Override
    public void notifyDataSetChanged() {
        if (null != parentAdapter) {
            parentAdapter.notifyDataSetChanged();
        }
    }

}
