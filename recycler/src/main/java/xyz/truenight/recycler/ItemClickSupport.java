package xyz.truenight.recycler;

import android.support.v7.widget.RecyclerView;
import android.view.View;

public class ItemClickSupport {

    private final RecyclerView mRecyclerView;
    private boolean mOnlyClickable;
    private OnItemClickListener mOnItemClickListener;
    private OnItemLongClickListener mOnItemLongClickListener;
    private View.OnClickListener mOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (mOnItemClickListener != null) {
                RecyclerView.ViewHolder holder = mRecyclerView.getChildViewHolder(v);
                mOnItemClickListener.onItemClicked(mRecyclerView, v, holder.getAdapterPosition());
            }
        }
    };
    private View.OnLongClickListener mOnLongClickListener = new View.OnLongClickListener() {
        @Override
        public boolean onLongClick(View v) {
            if (mOnItemLongClickListener != null) {
                RecyclerView.ViewHolder holder = mRecyclerView.getChildViewHolder(v);
                return mOnItemLongClickListener.onItemLongClicked(mRecyclerView, v, holder.getAdapterPosition());
            }
            return false;
        }
    };
    private RecyclerView.OnChildAttachStateChangeListener mAttachListener
            = new RecyclerView.OnChildAttachStateChangeListener() {
        @Override
        public void onChildViewAttachedToWindow(View view) {
            if (mOnItemClickListenerSet) {
                if (mOnItemClickListener != null && (!mOnlyClickable || view.isClickable())) {
                    view.setOnClickListener(mOnClickListener);
                } else {
                    view.setOnClickListener(null);
                }
            }
            if (mOnItemLongClickListenerSet) {
                if (mOnItemLongClickListener != null && (!mOnlyClickable || view.isClickable())) {
                    view.setOnLongClickListener(mOnLongClickListener);
                } else {
                    view.setOnLongClickListener(null);
                }
            }
        }

        @Override
        public void onChildViewDetachedFromWindow(View view) {

        }
    };
    private boolean mOnItemClickListenerSet;
    private boolean mOnItemLongClickListenerSet;

    private ItemClickSupport(RecyclerView recyclerView) {
        mRecyclerView = recyclerView;
        mRecyclerView.setTag(R.id.item_click_support, this);
        mRecyclerView.addOnChildAttachStateChangeListener(mAttachListener);
    }

    public static ItemClickSupport addTo(RecyclerView view) {
        ItemClickSupport support = (ItemClickSupport) view.getTag(R.id.item_click_support);
        if (support == null) {
            support = new ItemClickSupport(view);
        }
        return support;
    }

    public static ItemClickSupport removeFrom(RecyclerView view) {
        ItemClickSupport support = (ItemClickSupport) view.getTag(R.id.item_click_support);
        if (support != null) {
            support.detach(view);
        }
        return support;
    }

    public ItemClickSupport setOnlyClickable(boolean onlyClickable) {
        if (mOnlyClickable != onlyClickable) {
            this.mOnlyClickable = onlyClickable;
            refreshAllChildren();
        }
        return this;
    }

    public ItemClickSupport setOnItemClickListener(OnItemClickListener listener) {
        if (mOnItemClickListener == null && listener != null) {
            mOnItemClickListener = listener;
            mOnItemClickListenerSet = true;
            refreshAllChildren();
        } else if (mOnItemClickListener != null && listener == null) {
            mOnItemClickListener = null;
            refreshAllChildren();
            mOnItemLongClickListenerSet = false;
        } else {
            mOnItemClickListener = listener;
        }
        return this;
    }

    public ItemClickSupport setOnItemLongClickListener(OnItemLongClickListener listener) {
        if (mOnItemLongClickListener == null && listener != null) {
            mOnItemLongClickListener = listener;
            mOnItemLongClickListenerSet = true;
            refreshAllChildren();
        } else if (mOnItemLongClickListener != null && listener == null) {
            mOnItemLongClickListener = null;
            refreshAllChildren();
            mOnItemLongClickListenerSet = false;
        } else {
            mOnItemLongClickListener = listener;
        }
        return this;
    }

    private void refreshAllChildren() {
        for (int i = 0; i < mRecyclerView.getChildCount(); i++) {
            View view = mRecyclerView.getChildAt(i);
            mAttachListener.onChildViewAttachedToWindow(view);
        }
    }

    private void detach(RecyclerView view) {
        view.removeOnChildAttachStateChangeListener(mAttachListener);
        view.setTag(R.id.item_click_support, null);
    }

    public interface OnItemClickListener {
        void onItemClicked(RecyclerView recyclerView, View itemView, int position);
    }

    public interface OnItemLongClickListener {
        boolean onItemLongClicked(RecyclerView recyclerView, View itemView, int position);
    }
}