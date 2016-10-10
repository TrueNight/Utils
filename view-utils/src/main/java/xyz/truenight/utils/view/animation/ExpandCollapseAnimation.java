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

package xyz.truenight.utils.view.animation;

import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Transformation;

public class ExpandCollapseAnimation extends Animation {
    private View mAnimatedView;
    private int mEndHeight = 0;
    private int mStartHeight = 0;

    /**
     * @param view     The view to animate
     * @param duration
     * @param from
     * @param to
     */
    public ExpandCollapseAnimation(View view, int duration, int from, int to) {
        mAnimatedView = view;
        mStartHeight = from;
//        mStartHeight = view.getLayoutParams().height;//current height
        mEndHeight = to;

//        duration = duration / (to - from) * (to - mStartHeight);
//        if (duration < 0) {
//            duration = 0;
//        }
        setDuration(duration);
    }

    @Override
    protected void applyTransformation(float interpolatedTime, Transformation t) {
        super.applyTransformation(interpolatedTime, t);
        if (interpolatedTime < 1.0f) {
            mAnimatedView.getLayoutParams().height = mStartHeight + (int) ((mEndHeight - mStartHeight) * interpolatedTime);
            mAnimatedView.requestLayout();
        } else {
            mAnimatedView.getLayoutParams().height = mEndHeight;
            mAnimatedView.requestLayout();
        }
    }
}