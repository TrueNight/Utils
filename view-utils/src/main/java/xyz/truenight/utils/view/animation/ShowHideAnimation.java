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
import android.view.animation.LinearInterpolator;
import android.view.animation.Transformation;

public class ShowHideAnimation extends Animation {
    private final float mShowLimit;
    private final float mHideLimit;
    private View mAnimatedView;
    private int mEndHeight = 0;
    private int mStartHeight = 0;
    private int mMin = 0;
    private int mMax = 0;

    /**
     * @param view         The view to animate
     * @param durationShow
     * @param durationHold
     * @param durationHide
     * @param min          animate from this height value by default.
     *                     If height not equals "min" then animation parameters will be
     *                     recalculated(animation always start from current height)
     * @param max
     */
    public ShowHideAnimation(View view, int durationShow, int durationHold, int durationHide, int min, int max) {

        mStartHeight = view.getLayoutParams().height;//current height

        int newDurationShow = durationShow / (max - min) * (max - mStartHeight);

        int duration = newDurationShow + durationHold + durationHide;
        setDuration(duration);

        mShowLimit = ((float) newDurationShow) / duration;
        mHideLimit = ((float) newDurationShow + durationHold) / duration;

        mAnimatedView = view;
        mMin = min;
        mEndHeight = max;
        mMax = max;
        setInterpolator(new LinearInterpolator());
    }

    @Override
    protected void applyTransformation(float interpolatedTime, Transformation t) {
        super.applyTransformation(interpolatedTime, t);

        float upFactor = interpolatedTime >= mShowLimit ? 1 : interpolatedTime / mShowLimit;
        float downFactor = 1 - (interpolatedTime <= mHideLimit ? 0 : (interpolatedTime - mHideLimit) / (1 - mHideLimit));

        if (interpolatedTime == 1) {
            mAnimatedView.getLayoutParams().height = mMin;
        } else if (interpolatedTime > mHideLimit) {
            mAnimatedView.getLayoutParams().height = mMin + (int) ((mEndHeight - mMin) * downFactor);
        } else {
            mAnimatedView.getLayoutParams().height = mStartHeight + (int) ((mEndHeight - mStartHeight) * upFactor);
        }

        mAnimatedView.requestLayout();

//        Tracer.print("time = " + interpolatedTime
//                        + "; upFactor = " + upFactor
//                        + "; downFactor = " + downFactor
//                        + "; height = " + mAnimatedView.getLayoutParams().height
//                        + "; max height = " + mEndHeight
//                        + "; mShowLimit = " + mShowLimit
//                        + "; mHideLimit = " + mHideLimit
//        );
    }
}