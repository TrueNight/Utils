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

package xyz.truenight.utils;

import android.app.Activity;
import android.content.Context;
import android.graphics.Rect;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.inputmethod.InputMethodManager;

import xyz.truenight.utils.helper.ViewHelper;
import xyz.truenight.utils.log.Tracer;

public class KeyboardHelper {
    private final KeyboardStorage mKeyboardStorage;
    private final ViewCompressionDetector mViewCompressionDetector;
    private OnKeyboardListener mExternalKeyboardListener;
    private boolean mIsKbVisible;
    private Context mContext;

    public interface OnKeyboardListener {
        void onKeyboardStateChanged(boolean visible, int height);
    }

    //region internal utils
    private static class ViewCompressionDetector {
        private static final boolean PRINT_LOG = false;
        private double mMaxCompressionPercent = 0.2;
        private final View mView;
        private OnLayoutCompressionListener mExternalLayoutListener;
        private int mPrevHeightDiff = 0;

        private final ViewTreeObserver.OnGlobalLayoutListener mLayoutListener = new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                Rect visibleRect = new Rect();
                mView.getWindowVisibleDisplayFrame(visibleRect);
                final int visibleHeight = visibleRect.bottom - visibleRect.top;
                final int heightDiff = mView.getRootView().getHeight() - visibleHeight;
                boolean isHighCompression = heightDiff > mView.getRootView().getHeight() * mMaxCompressionPercent;
                final int compressionStateDelta = heightDiff - mPrevHeightDiff;
                if (PRINT_LOG) {
                    Tracer.print("kb open = " + isHighCompression + " top = " + visibleRect.top
                            + " bottom = " + visibleRect.bottom + " heightDiff = " + heightDiff
                            + " compressionStateDelta = " + compressionStateDelta);
                }

                if (mExternalLayoutListener != null) {
                    mExternalLayoutListener
                            .onViewCompressionStateChanged(isHighCompression, compressionStateDelta, heightDiff);
                }

                mPrevHeightDiff = heightDiff;
            }
        };

        public ViewCompressionDetector(View view) {
            mView = view;
            mView.getViewTreeObserver().addOnGlobalLayoutListener(mLayoutListener);
        }

        public void setListener(OnLayoutCompressionListener listener) {
            mExternalLayoutListener = listener;
        }

        public void setMaxCompressionPercent(double maxCompressionPercent) {
            mMaxCompressionPercent = maxCompressionPercent;
        }

        public interface OnLayoutCompressionListener {
            void onViewCompressionStateChanged(boolean highCompression, int compressionStateDelta, int compression);
        }
    }

    private static class KeyboardStorage {

        private int keyboardHeightPortrait;
        private int keyboardHeightLandscape;

        public KeyboardStorage(Context context) {
            keyboardHeightPortrait = ViewHelper.dpToPx(context, 240);
            keyboardHeightLandscape = ViewHelper.getScreenHeight(context) / 2;
        }

        public void setKeyboardHeightPortrait(int keyboardHeightPortrait) {
            this.keyboardHeightPortrait = keyboardHeightPortrait;
        }

        public void setKeyboardHeightLandscape(int keyboardHeightLandscape) {
            this.keyboardHeightLandscape = keyboardHeightLandscape;
        }

        public int getKeyboardHeightPortrait() {
            return keyboardHeightPortrait;
        }

        public int getKeyboardHeightLandscape() {
            return keyboardHeightLandscape;
        }
    }
    //endregion

    public KeyboardHelper(Activity activity) {
        this(activity.findViewById(android.R.id.content).getRootView());
    }

    public KeyboardHelper(View view) {
        mContext = view.getContext();
        mViewCompressionDetector = new ViewCompressionDetector(view);
        mKeyboardStorage = new KeyboardStorage(view.getContext());
    }

    public Context getContext() {
        return mContext;
    }

    public void setListener(OnKeyboardListener listener) {
        mExternalKeyboardListener = listener;
        mViewCompressionDetector.setListener(new ViewCompressionDetector.OnLayoutCompressionListener() {
            @Override
            public void onViewCompressionStateChanged(boolean highCompression,
                                                      int compressionStateDelta, int height) {
                boolean prevIsHighCompression = mIsKbVisible;
                mIsKbVisible = highCompression;

                if (ViewHelper.isPortrait(getContext())) {
                    mKeyboardStorage.setKeyboardHeightPortrait(height);
                } else {
                    mKeyboardStorage.setKeyboardHeightLandscape(height);
                }

                if (mExternalKeyboardListener != null && prevIsHighCompression != mIsKbVisible) {
                    mExternalKeyboardListener.onKeyboardStateChanged(mIsKbVisible, height);
                }
            }
        });
    }

    public KeyboardHelper setKeyboardHeightThreshold(double percent) {
        mViewCompressionDetector.setMaxCompressionPercent(percent);
        return this;
    }

    public boolean isKeyboardVisible() {
        return mIsKbVisible;
    }

    public int getKeyboardHeight() {
        if (ViewHelper.isPortrait(getContext())) {
            return mKeyboardStorage.getKeyboardHeightPortrait();
        } else {
            return mKeyboardStorage.getKeyboardHeightLandscape();
        }
    }

    public int getKeyboardHeightPortrait() {
        return mKeyboardStorage.getKeyboardHeightPortrait();
    }

    public int getKeyboardHeightLandscape() {
        return mKeyboardStorage.getKeyboardHeightLandscape();
    }

    public static void hideKeyboard(Activity activity) {
        if (activity != null) {
            InputMethodManager inputManager = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
            if (activity.getCurrentFocus() != null && inputManager != null) {
                inputManager.hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(), 0);
                inputManager.hideSoftInputFromInputMethod(activity.getCurrentFocus().getWindowToken(), 0);//was showSoftInputFromInputMethod in sample. test it
            }
        }
    }

    public static void hideKeyboard(View view) {
        if (view != null) {
            InputMethodManager inputManager = (InputMethodManager) view.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
            if (inputManager != null) {
                inputManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
            }
        }
    }

    public static void showKeyboard(View view) {
        if (view != null) {
            InputMethodManager inputManager = (InputMethodManager) view.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
            if (inputManager != null) {
                view.requestFocus();
                inputManager.showSoftInput(view, InputMethodManager.SHOW_IMPLICIT);
            }
        }
    }
}