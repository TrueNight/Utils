/**
 * Copyright (C) 2016 Mikhail Frolov
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package xyz.truenight.utils.view;

/**
 * Created by true
 * date: 31/07/15
 * time: 10:41
 */

import android.support.v7.widget.RecyclerView;

import xyz.truenight.utils.Utils;
import xyz.truenight.utils.helper.ViewHelper;


public class EndlessScrollListener extends RecyclerView.OnScrollListener {

    private static final String TAG = EndlessScrollListener.class.getSimpleName();
    private static final int COMPLETED = 0x100;
    private static final int NOT_COMPLETED = 0xFFFFFEFF;
    private static final int MANUAL = 0x1000;
    private int mVisibleThreshold = 0;
    private int mCurrentPage = 0;
    private int mPreviousTotal = 0;
    private boolean mLoading = true;

    private int mCombined = COMPLETED;

    private OnUpdateTask mUpdateTask;

    public void setCompleted(boolean completed) {
        if (!isManual()) {
            mCombined |= MANUAL;
        }
        if (completed) {
            mCombined |= COMPLETED;
        } else {
            mCombined &= NOT_COMPLETED;
        }
    }

    public EndlessScrollListener(int visibleThreshold, OnUpdateTask loadTask) {
        mVisibleThreshold = visibleThreshold;
        mUpdateTask = loadTask;
    }

    public void onScroll(int firstVisibleItem, int visibleItemCount, int totalItemCount) {
        if (mLoading) {
            if (totalItemCount != mPreviousTotal && isCompleted()) {
                mLoading = false;
                notCompleted();
                mPreviousTotal = totalItemCount;
                mCurrentPage++;
            }
        }
        if (!mLoading && (totalItemCount - visibleItemCount) <= (firstVisibleItem + mVisibleThreshold)) {
            // send loading next page
            if (mUpdateTask != null) {
                mUpdateTask.onUpdate();
            }
            mLoading = true;
        }
    }

    public void refresh() {
        mLoading = true;
        mPreviousTotal = 0;
        mCurrentPage = 0;
        notCompleted();
    }

    private void notCompleted() {
        if (isManual()) {
            mCombined &= NOT_COMPLETED;
        }
    }

    public boolean isLoading() {
        return !isCompleted() && mLoading;
    }

    public void setLoading(boolean loading) {
        mLoading = loading;
    }

    @Override
    public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
        super.onScrolled(recyclerView, dx, dy);

        ViewHelper.recyclerViewCount(recyclerView, new ViewHelper.OnCountCompletedListener() {
            @Override
            public void onCountCompleted(int firstVisibleItem, int lastVisibleItem, int visibleItemCount, int totalItemCount) {
                onScroll(firstVisibleItem, visibleItemCount, totalItemCount);
            }
        });
    }

    public boolean isManual() {
        return Utils.check(mCombined, MANUAL);
    }

    private boolean isCompleted() {
        return Utils.check(mCombined, COMPLETED);
    }
}
