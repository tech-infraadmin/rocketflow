package com.rf.taskmodule.ui.buddylisting;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.rf.taskmodule.data.model.response.config.Buddy;
import com.rf.taskmodule.data.model.response.config.BuddySelectionType;
import com.rf.taskmodule.utils.AppConstants;
import com.rf.taskmodule.utils.BuddyStatus;
import com.rf.taskmodule.utils.Log;

import com.rf.taskmodule.data.model.response.config.Buddy;
import com.rf.taskmodule.data.model.response.config.BuddySelectionType;
import com.rf.taskmodule.databinding.ItemBuddyListingEmptyViewSdkBinding;
import com.rf.taskmodule.databinding.ItemBuddyListingViewSdkBinding;
import com.rf.taskmodule.ui.base.BaseSdkViewHolder;
import com.rf.taskmodule.utils.Log;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by rahul on 18/9/18
 */
public class BuddyListingAdapter extends RecyclerView.Adapter<BaseSdkViewHolder> {

    private static final int VIEW_TYPE_EMPTY = 0;
    private static final int VIEW_TYPE_NORMAL = 1;
    private List<Buddy> mBuddyResponseList;
    private List<Buddy> copyList;
    private Intent intent;
    private AdapterEventListener mListener;
    private BuddySelectionType type;

    BuddyListingAdapter(List<Buddy> driverResponseList) {
        this.mBuddyResponseList = driverResponseList;
        copyList = new ArrayList<>();
        copyList.addAll(mBuddyResponseList);
    }
    public void setSelectionType(BuddySelectionType type){
        this.type=type;
    }

    @Override
    public int getItemCount() {
        if (mBuddyResponseList != null && mBuddyResponseList.size() > 0) {
            return mBuddyResponseList.size();
        } else {
            return 1;
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (mBuddyResponseList != null && !mBuddyResponseList.isEmpty()) {
            return VIEW_TYPE_NORMAL;
        } else {
            return VIEW_TYPE_EMPTY;
        }
    }

    @Override
    public void onBindViewHolder(@NonNull BaseSdkViewHolder holder, int position) {
        holder.onBind(position);
    }

    @NonNull
    @Override
    public BaseSdkViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        switch (viewType) {
            case VIEW_TYPE_NORMAL:
                ItemBuddyListingViewSdkBinding buddyListingViewBinding = ItemBuddyListingViewSdkBinding.inflate(LayoutInflater.from(parent.getContext()),
                        parent, false);
                return new BuddyListingViewHolder(buddyListingViewBinding);
            case VIEW_TYPE_EMPTY:
            default:
                ItemBuddyListingEmptyViewSdkBinding emptyViewBinding = ItemBuddyListingEmptyViewSdkBinding.inflate(LayoutInflater.from(parent.getContext()),
                        parent, false);
                return new EmptyViewHolder(emptyViewBinding);
        }
    }

    public void addItems(List<Buddy> stringList) {
        clearItems();
        mBuddyResponseList.addAll(stringList);
        copyList.clear();
        copyList.addAll(mBuddyResponseList);
        notifyDataSetChanged();
    }

    private void clearItems() {
        mBuddyResponseList.clear();
    }

    public void setListener(AdapterEventListener listener, Intent intent) {
        this.mListener = listener;
        this.intent = intent;
    }

    void addFilter(String newText) {
        clearItems();
        if (newText.isEmpty()) {
            mBuddyResponseList.addAll(copyList);
        } else {
            for (Buddy name : copyList) {
                if (name.getName() != null &&
                        name.getName().toLowerCase().contains(newText.toLowerCase())) {
                    mBuddyResponseList.add(name);
                }
            }
        }
        notifyDataSetChanged();
    }

    public List<Buddy> getItems() {
        return mBuddyResponseList;
    }

    public interface AdapterEventListener {
        void onItemClick(Buddy buddyBean);

        void onItemLongClick(Buddy buddy);
    }

    public class BuddyListingViewHolder extends BaseSdkViewHolder implements
            BuddyListingItemViewModel.BuddyListingItemViewModelListener {

        private ItemBuddyListingViewSdkBinding mBinding;

        private BuddyListingItemViewModel mBuddyListingItemViewModel;

        public BuddyListingViewHolder(ItemBuddyListingViewSdkBinding binding) {
            super(binding.getRoot());
            this.mBinding = binding;
        }

        @Override
        public void onBind(int position) {
            final Buddy buddyList = mBuddyResponseList.get(position);
            if (buddyList.getStatus() != null) {
                switch (buddyList.getStatus()) {
                    case ONLINE:
                        mBinding.ivDriverStatus.setSelected(true);
                        break;
                    case PENDING:
                        mBinding.ivDriverStatus.setEnabled(true);
                        break;
                    case OFFLINE:
                    default:
                        mBinding.ivDriverStatus.setEnabled(false);
                        mBinding.ivDriverStatus.setSelected(false);
                        break;
                }
            }

            mBuddyListingItemViewModel = new BuddyListingItemViewModel(buddyList, this);
            mBinding.setViewModel(mBuddyListingItemViewModel);

            // Immediate Binding
            // When a variable or observable changes, the binding will be scheduled to change before
            // the next frame. There are times, however, when binding must be executed immediately.
            // To force execution, use the executePendingBindings() method.
            mBinding.executePendingBindings();
        }

        @Override
        public void onItemClick(Buddy buddyBean) {
            //if intent is from main activity then call listener's method.
            if (intent.hasExtra(AppConstants.Extra.EXTRA_BUDDY_LIST_CALLING_FROM_NAVIGATION_MENU)) {
                mListener.onItemClick(buddyBean);
            } else if (intent.hasExtra(AppConstants.Extra.EXTRA_BUDDY_LIST_CALLING_FROM_DASHBOARD_MENU)) {
                for (int i = 0; i < mBuddyResponseList.size(); i++) {
                    Log.e("BUDDYLIAting", buddyBean.getBuddyId() + " <---> "
                            + mBuddyResponseList.get(i).getBuddyId());
                    if (mBuddyResponseList.get(i).getBuddyId().equals(buddyBean.getBuddyId())) {
                        if ( buddyBean.isSelected()) {
                            buddyBean.setSelected(false);
                            mBuddyResponseList.get(i).setSelected(false);
                        } else {
                            buddyBean.setSelected(true);
                            mBuddyResponseList.get(i).setSelected(true);
                        }
                    } else {
                        if (mBuddyResponseList.get(i).getShowSelected()) {
                            mBuddyResponseList.get(i).setSelected(false);
                        }
                    }
                }
                notifyDataSetChanged();
            } else {
                if(type!=null&&type==BuddySelectionType.SINGLE){
                    for (int i = 0; i < mBuddyResponseList.size(); i++) {
                        Log.e("BUDDYLIAting", buddyBean.getBuddyId() + " <---> "
                                + mBuddyResponseList.get(i).getBuddyId());
                        if (buddyBean.getBuddyId() != null &&
                                buddyBean.getBuddyId().equals(mBuddyResponseList.get(i).getBuddyId())) {
                            buddyBean.setSelected(true);
                            mBuddyResponseList.get(i).setSelected(true);
                        }else{
                            mBuddyResponseList.get(i).setSelected(false);
                        }
                    }

                }else{
                    for (int i = 0; i < mBuddyResponseList.size(); i++) {
                        Log.e("BUDDYLIAting", buddyBean.getBuddyId() + " <---> "
                                + mBuddyResponseList.get(i).getBuddyId());
                        if (buddyBean.getBuddyId() != null &&
                                buddyBean.getBuddyId().equals(mBuddyResponseList.get(i).getBuddyId())) {
                            if (buddyBean.isSelected()) {
                                buddyBean.setSelected(false);
                                mBuddyResponseList.get(i).setSelected(false);
                            } else {
                                buddyBean.setSelected(true);
                                mBuddyResponseList.get(i).setSelected(true);
                            }
                            break;
                        }
                    }
                }

                notifyDataSetChanged();
            }
        }

        @Override
        public void onItemLongClick(Buddy buddyBean) {
            if (intent.hasExtra(AppConstants.Extra.EXTRA_BUDDY_LIST_CALLING_FROM_NAVIGATION_MENU)) {
                mListener.onItemLongClick(buddyBean);
            }
        }
    }

    public class EmptyViewHolder extends BaseSdkViewHolder implements
            BuddyListingEmptyItemViewModel.BuddyListingEmptyItemViewHolderListener {

        private ItemBuddyListingEmptyViewSdkBinding mBinding;

        public EmptyViewHolder(ItemBuddyListingEmptyViewSdkBinding binding) {
            super(binding.getRoot());
            this.mBinding = binding;
        }

        @Override
        public void onBind(int position) {
            BuddyListingEmptyItemViewModel emptyItemViewModel = new BuddyListingEmptyItemViewModel(this);
            mBinding.setViewModel(emptyItemViewModel);
        }

        @Override
        public void onRetryClick() {
//            mListener.onRetryClick();
        }
    }
}