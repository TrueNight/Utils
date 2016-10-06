package xyz.truenight.utils.animation;

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