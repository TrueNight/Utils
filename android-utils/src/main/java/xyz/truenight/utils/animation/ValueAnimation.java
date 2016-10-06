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

package xyz.truenight.utils.animation;

import android.view.animation.Animation;
import android.view.animation.Transformation;

public class ValueAnimation extends Animation {

    private static final String TAG = ValueAnimation.class.getSimpleName();
    private float mEnd = 0;
    private float mStart = 0;
    private OnTransformListener mOnTransformListener;

    /**
     * @param durationShow
     * @param startValue
     * @param endValue
     */
    public ValueAnimation(long durationShow, float startValue, float endValue) {
        setDuration(durationShow);
        mStart = startValue;
        mEnd = endValue;
    }

    @Override
    protected void applyTransformation(float interpolatedTime, Transformation t) {
        super.applyTransformation(interpolatedTime, t);

        float delta = Math.abs(mEnd - mStart);
        float currentValue;
        if (mEnd > mStart) {
            currentValue = mStart + delta * interpolatedTime;
        } else {
            currentValue = mStart - delta * interpolatedTime;
        }


        if (mOnTransformListener != null) {
//            Log.d(TAG, "interpolatedTime: " + interpolatedTime + "; currentValue: " + currentValue);
            mOnTransformListener.onTransform(interpolatedTime, currentValue);
        }
    }

    public ValueAnimation transform(OnTransformListener onTransformListener) {
        mOnTransformListener = onTransformListener;
        return this;
    }

    public interface OnTransformListener {
        void onTransform(float interpolatedTime, float currentValue);
    }
}