package com.rf.taskmodule.ui.base;

import android.view.View;

import androidx.recyclerview.widget.RecyclerView;

/**
 * Created by rahul.
 */

public abstract class BaseSdkViewHolder extends RecyclerView.ViewHolder {

    public BaseSdkViewHolder(View itemView) {
        super(itemView);
    }

    public abstract void onBind(int position);
}
