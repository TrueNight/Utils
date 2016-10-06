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

import android.content.Context;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.AttributeSet;
import android.view.MotionEvent;

import xyz.truenight.utils.helper.ViewHelper;
import xyz.truenight.utils.log.Log;

/**
 * Created by true
 * date: 27/10/15
 * time: 11:00
 */
public class SwipeRefreshTopLayout extends SwipeRefreshLayout {

    private static final String TAG = SwipeRefreshTopLayout.class.getSimpleName();

    private static final boolean DEBUG = true;

    public static final float TWENTY_PERCENT_OF_VIEW = 0.20f;
    public static final float TWELVE_AND_HALF_PERCENT_OF_VIEW = 0.125f;
    private boolean mEnabled = isEnabled();

    public void setPercentOfView(float rate) {
        mPercentOfView = rate;
    }

    @Override
    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);
        mEnabled = enabled;
    }

    private float mPercentOfView = TWENTY_PERCENT_OF_VIEW;

    public SwipeRefreshTopLayout(Context context) {
        super(context);
    }

    public SwipeRefreshTopLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        try {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                boolean use = !ViewHelper.getViewRectWithoutTop(getChildAt(0), mPercentOfView).contains((int) event.getX(), (int) event.getY());
                super.setEnabled(use && mEnabled);
                if (DEBUG) {
                    Log.d(TAG, "onTouchEvent; use " + use + "; Percent: " + mPercentOfView);
                }
            }

        } catch (Exception e) {
            if (DEBUG) {
                Log.d(TAG, "onTouchEvent;", e);
            }
        }
        return super.onTouchEvent(event);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent event) {
        try {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                boolean use = !ViewHelper.getViewRectWithoutTop(getChildAt(0), mPercentOfView).contains((int) event.getX(), (int) event.getY());
                super.setEnabled(use && mEnabled);
                if (DEBUG) {
                    Log.d(TAG, "onInterceptTouchEvent; use " + use + "; Percent: " + mPercentOfView);
                }
            }
        } catch (Exception e) {
            if (DEBUG) {
                Log.d(TAG, "onInterceptTouchEvent;", e);
            }
        }
        return super.onInterceptTouchEvent(event);
    }
}
