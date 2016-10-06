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