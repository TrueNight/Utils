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

import android.animation.LayoutTransition;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.Dialog;
import android.app.KeyguardManager;
import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.PorterDuff;
import android.graphics.Rect;
import android.graphics.Shader;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.PaintDrawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.RectShape;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.os.Process;
import android.support.annotation.ColorInt;
import android.support.annotation.ColorRes;
import android.support.annotation.DimenRes;
import android.support.annotation.DrawableRes;
import android.support.annotation.IdRes;
import android.support.annotation.LayoutRes;
import android.support.annotation.Nullable;
import android.support.annotation.PluralsRes;
import android.support.annotation.StyleRes;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.Html;
import android.text.InputFilter;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.style.ClickableSpan;
import android.util.Log;
import android.util.Patterns;
import android.util.TypedValue;
import android.view.ContextThemeWrapper;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import xyz.truenight.utils.Utils;
import xyz.truenight.utils.interfaces.Consumer;
import xyz.truenight.utils.interfaces.Filter;
import xyz.truenight.utils.interfaces.Source;
import xyz.truenight.utils.view.animation.ShowHideAnimation;


/**
 * Created by true
 * date: 24/08/15
 * time: 01:50
 */
public final class ViewHelper {

    public static final int FAB_SHOW_DELAY = 200;
    public static final int FAB_SHOW_NEXT_DELAY = 200;
    public static final String HREF_FORMAT = "<a href=\"%1$s\">%2$s</a>";
    public static final int MULTICOLOR = 0x00000001;
    public static final int BLACK_WHITE = 0x00000002;
    private static final String HOLDER = "item#";

    @TargetApi(17)
    public static Resources getLocaleResources(Context context, String language) {
        Configuration conf = new Configuration();
        conf.setLocale(new Locale(language, ""));
        return context.createConfigurationContext(conf).getResources();
    }

    public static Spanned getColoredText(String text, @ColorInt int color) {
        final String htmlString = String.format("<font color='#%06X'>%s</font>", color & 0xFFFFFF, TextUtils.htmlEncode(text));
        return Html.fromHtml(htmlString);
    }

    public static String getLinkString(String uri, String name) {
        return String.format(HREF_FORMAT, uri, name);
    }

    public static Spanned getLink(String uri, String name) {
        String htmlStr = String.format(HREF_FORMAT, uri, name);
        return Html.fromHtml(htmlStr);
    }

    public static Spanned getUnderlineText(String text) {
        return Html.fromHtml("<u>" + text + "</u>");
    }

    public static void setOnClick(SpannableString full, String text, final ClickableSpan onClick) {
        int start = full.toString().indexOf(text);
        full.setSpan(new ClickableSpan() {
            @Override
            public void onClick(View textView) {
                if (onClick != null) {
                    onClick.onClick(textView);
                }
            }

            @Override
            public void updateDrawState(TextPaint ds) {
                super.updateDrawState(ds);
                ds.setUnderlineText(false);
            }
        }, start, start + text.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
    }

    public static void setOnClick(SpannableString full, int start, int end, final boolean underlineText, final ClickableSpan onClick) {
        full.setSpan(new ClickableSpan() {
            @Override
            public void onClick(View textView) {
                if (onClick != null) {
                    onClick.onClick(textView);
                }
            }

            @Override
            public void updateDrawState(TextPaint ds) {
                super.updateDrawState(ds);
                ds.setUnderlineText(underlineText);
            }
        }, start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
    }

    public static void setCursorDrawableColor(EditText editText, int color) {
        try {
            Field fCursorDrawableRes = TextView.class.getDeclaredField("mCursorDrawableRes");
            fCursorDrawableRes.setAccessible(true);
            int mCursorDrawableRes = fCursorDrawableRes.getInt(editText);
            Field fEditor = TextView.class.getDeclaredField("mEditor");
            fEditor.setAccessible(true);
            Object editor = fEditor.get(editText);
            Class<?> clazz = editor.getClass();
            Field fCursorDrawable = clazz.getDeclaredField("mCursorDrawable");
            fCursorDrawable.setAccessible(true);
            Drawable[] drawables = new Drawable[2];
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                drawables[0] = editText.getContext().getDrawable(mCursorDrawableRes).mutate();
                drawables[1] = editText.getContext().getDrawable(mCursorDrawableRes).mutate();
            } else {
                drawables[0] = editText.getContext().getResources().getDrawable(mCursorDrawableRes).mutate();
                drawables[1] = editText.getContext().getResources().getDrawable(mCursorDrawableRes).mutate();
            }
            drawables[0].setColorFilter(color, PorterDuff.Mode.SRC_IN);
            drawables[1].setColorFilter(color, PorterDuff.Mode.SRC_IN);
            fCursorDrawable.set(editor, drawables);
        } catch (Throwable ignored) {
        }
    }

    public static void setCursorDrawable(EditText searchEditText, @DrawableRes int cursor) {
        try {
            Field mCursorDrawableRes = TextView.class.getDeclaredField("mCursorDrawableRes");
            mCursorDrawableRes.setAccessible(true);
            mCursorDrawableRes.set(searchEditText, cursor);
        } catch (Exception ignored) {
        }
    }

    public static Context customStyle(Context context, @StyleRes int style) {
        //view.getContext().getTheme().dump(android.util.Log.DEBUG, "Tracer", " ! ");
        return new ContextThemeWrapper(context, style);
    }

//    public static Context customAttribute(Context context, @StyleableRes int atr) {
//        view.getContext().getTheme().dump(android.util.Log.DEBUG, "Tracer", " ! ");
//        return new ContextThemeWrapper(context, style);
//    }

    public static void setBackgroundColor(int color, final View view) {
        if (color == MULTICOLOR) {
            ShapeDrawable.ShaderFactory sf = new ShapeDrawable.ShaderFactory() {
                @Override
                public Shader resize(int width, int height) {
                    return new LinearGradient(0, 0, 0, height,
                            new int[]{0xFFFF0000, 0xFF0000FF, 0xFF00FF00},
                            new float[]{0.1f, 0.5f, 0.9f}, Shader.TileMode.REPEAT);
                }
            };
            PaintDrawable paintDrawable = new PaintDrawable();
            paintDrawable.setShape(new RectShape());
            paintDrawable.setShaderFactory(sf);
            view.setBackgroundDrawable(paintDrawable);
        } else if (color == BLACK_WHITE) {
            ShapeDrawable.ShaderFactory sf = new ShapeDrawable.ShaderFactory() {
                @Override
                public Shader resize(int width, int height) {
                    return new LinearGradient(0, 0, 0, height,
                            new int[]{0xFFFFFFFF, 0xFF000000},
                            new float[]{0f, 1f}, Shader.TileMode.REPEAT);
                }
            };
            PaintDrawable paintDrawable = new PaintDrawable();
            paintDrawable.setShape(new RectShape());
            paintDrawable.setShaderFactory(sf);
            view.setBackgroundDrawable(paintDrawable);
        } else {
            view.setBackgroundColor(color);
        }
    }

    public static void setDefaultEditTextBackground(EditText editText) {
        // set edit text background
        TypedValue outValue = new TypedValue();
        editText.getContext().getTheme().resolveAttribute(R.attr.editTextBackground,
                outValue, true);
        editText.setBackgroundResource(outValue.resourceId);
    }


    public static void setTextChangedListener(TextView textView, TextWatcher textWatcher) {
        Object tag = textView.getTag();
        if (tag != null) {
            textView.removeTextChangedListener((TextWatcher) tag);
        }
        textView.setTag(textWatcher);
        textView.addTextChangedListener(textWatcher);
    }

    public static void setTextChangedListener(TextView textView, String text, TextWatcher textWatcher) {
        Object tag = textView.getTag(R.id.text_watcher);
        if (tag != null) {
            textView.removeTextChangedListener((TextWatcher) tag);
        }
        textView.setTag(R.id.text_watcher, textWatcher);
        if (text != null) {
            textView.setText(text);
        }
        textView.addTextChangedListener(textWatcher);
    }

    public static void updateTextSilent(TextView textView, String text, TextWatcher textWatcher) {
        InputFilter[] filters = textView.getFilters();
        textView.setFilters(new InputFilter[0]);
        setTextChangedListener(textView, text, textWatcher);
        textView.setFilters(filters);
    }

    public static void updateTextSilent(TextView textView, String text) {
        Object tag = textView.getTag(R.id.text_watcher);
        textView.removeTextChangedListener((TextWatcher) tag);
        InputFilter[] filters = textView.getFilters();
        textView.setFilters(new InputFilter[0]);
        textView.setText(text);
        textView.setFilters(filters);
        textView.addTextChangedListener((TextWatcher) tag);
    }

    public static int getDisplayWidth(Context context) {
        return context.getResources().getDisplayMetrics().widthPixels;
    }

    public static int getDisplayHeight(Context context) {
        return context.getResources().getDisplayMetrics().heightPixels;
    }

    public static Point getDisplaySize(Context context) {
        return new Point(getDisplayWidth(context), getDisplayHeight(context));
    }

    public static int getStatusBarHeight(Context context) {
        int result = 0;
        int resourceId = context.getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = context.getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }

    public static int dpToPx(Context context, float dp) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return Math.round(dp * scale);
    }

    public static int dimenToPx(Context context, @DimenRes int id) {
        return context.getResources().getDimensionPixelOffset(id);
    }

    public static int pxToDp(Context context, float px) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return Math.round(px / scale);
    }

    public static Rect pxToDp(Context context, Rect pxRect) {
        Rect rect = new Rect();
        rect.left = pxToDp(context, pxRect.left);
        rect.top = pxToDp(context, pxRect.top);
        rect.right = pxToDp(context, pxRect.right);
        rect.bottom = pxToDp(context, pxRect.bottom);
        return rect;
    }

    public static int getDimension(Context context, int dimensionId) {
        return (int) (context.getResources().getDimension(dimensionId) + 0.5);
    }

    public static boolean isTablet(Context context) {
        return isTablet(context, 600);
    }

    public static boolean isTablet(Context context, int smallestScreenWidthDp) {
        return context.getResources().getConfiguration().smallestScreenWidthDp >= smallestScreenWidthDp;
    }

    public static boolean isLandscape(Context context) {
        return context.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE;
    }

    public static boolean isPortrait(Context context) {
        return context.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT;
    }

    public static float getDensity(Context context) {
        return context.getResources().getDisplayMetrics().density;
    }

    public static int getScreenWidth(Context context) {
        Display display = ((WindowManager) context.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        return size.x;
    }

    public static int getScreenHeight(Context context) {
        Display display = ((WindowManager) context.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        return size.y;
    }

    public static boolean hasJellyBean() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN;
    }

    static boolean hasLollipop() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP;
    }

    public static int hashCode(String hash) {
        if (hash == null || hash.isEmpty()) {
            return -1;
        }

        return Math.abs(hash.hashCode());
    }

    public static void hideBars(Window window) {
        //Tracer.print("isHidden = " + isNavigationHidden(activity));
        // This work only for android 4.4+
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            final int flags = View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;

            window.getDecorView().setSystemUiVisibility(flags);

            // Code below is to handle presses of Volume up or Volume down.
            // Without this, after pressing volume buttons, the navigation bar will
            // show up and won't hide
            final View decorView = window.getDecorView();
            decorView
                    .setOnSystemUiVisibilityChangeListener(new View.OnSystemUiVisibilityChangeListener() {

                        @Override
                        public void onSystemUiVisibilityChange(int visibility) {
                            if ((visibility & View.SYSTEM_UI_FLAG_FULLSCREEN) == 0) {
                                decorView.setSystemUiVisibility(flags);
                            }
                            //Tracer.print("isHidden = " + isNavigationHidden(decorView));
                        }
                    });
        }
        //Tracer.print("isHidden = " + isNavigationHidden(activity));
    }

    public static void showBars(Window window) {
        //Tracer.print("isHidden = " + isNavigationHidden(activity));
        // This work only for android 4.4+
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
//            final int flags = View.SYSTEM_UI_FLAG_LAYOUT_STABLE
//                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
//                    | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
//                    | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
//                    | View.SYSTEM_UI_FLAG_FULLSCREEN
//                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;

            window.getDecorView().setSystemUiVisibility(0);

            // Code below is to handle presses of Volume up or Volume down.
            // Without this, after pressing volume buttons, the navigation bar will
            // show up and won't hide
            final View decorView = window.getDecorView();
            decorView
                    .setOnSystemUiVisibilityChangeListener(new View.OnSystemUiVisibilityChangeListener() {

                        @Override
                        public void onSystemUiVisibilityChange(int visibility) {
                            if ((visibility & View.SYSTEM_UI_FLAG_FULLSCREEN) == 0) {
                                decorView.setSystemUiVisibility(0);
                            }
                            //Tracer.print("isHidden = " + isNavigationHidden(decorView));
                        }
                    });
        }
        //Tracer.print("isHidden = " + isNavigationHidden(activity));
    }

    public static void onWindowFocusChangedHideBars(Activity activity, boolean hasFocus) {
        onWindowFocusChangedHideBars(activity.getWindow(), hasFocus);
    }

    public static void onWindowFocusChangedHideBars(Window window, boolean hasFocus) {
        //Tracer.print("isHidden = " + isNavigationHidden(activity) + " has focus = " + hasFocus);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT && hasFocus) {
            window.getDecorView().setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        }
        //Tracer.print("isHidden = " + isNavigationHidden(activity));
    }

    public static void translucentNavigationBar(Activity activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Window w = activity.getWindow(); // in Activity's onCreate() for instance
            w.setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION, WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
//            w.setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS, WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }
    }

    public static void notTranslucentNavigationBar(Activity activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            final WindowManager.LayoutParams attrs = activity.getWindow()
                    .getAttributes();
            attrs.flags &= (~WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
            attrs.flags &= (~WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            activity.getWindow().setAttributes(attrs);
            activity.getWindow().clearFlags(
                    WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        }
    }

    public static boolean isStatusBarTranslucent(Activity activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            return isFlagExists(activity, WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        } else {
            return false;
        }
    }

    public static boolean isFlagExists(Activity activity, int flag) {
        return isFlagExists(activity.getWindow(), flag);
    }

    public static boolean isFlagExists(Window window, int flag) {
        return (window.getAttributes().flags & flag) != 0;
    }

    public static void hideNavigationBar(Activity activity) {
        hideNavigationBar(activity.getWindow());
    }

    public static void hideNavigationBar(Dialog dialog) {
        hideNavigationBar(dialog.getWindow());
    }

    public static void hideNavigationBar(Window window) {
        //Tracer.print("isHidden = " + isNavigationHidden(activity));
        // This work only for android 4.4+
//        enableTransitionAnimation((ViewGroup) ((ViewGroup) activity
//                .findViewById(android.R.id.content)).getChildAt(0));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            final int flags = //View.SYSTEM_UI_FLAG_LAYOUT_STABLE
////                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
////                    | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN |
                    View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
////                    | View.SYSTEM_UI_FLAG_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
//
            window.getDecorView().setSystemUiVisibility(flags);

            // Code below is to handle presses of Volume up or Volume down.
            // Without this, after pressing volume buttons, the navigation bar will
            // show up and won't hide
            final View decorView = window.getDecorView();
            decorView
                    .setOnSystemUiVisibilityChangeListener(new View.OnSystemUiVisibilityChangeListener() {

                        @Override
                        public void onSystemUiVisibilityChange(int visibility) {
                            if ((visibility & View.SYSTEM_UI_FLAG_FULLSCREEN) == 0) {
                                decorView.setSystemUiVisibility(flags);
                            }
                            //Tracer.print("isHidden = " + isNavigationHidden(decorView));
                        }
                    });
        }
        //Tracer.print("isHidden = " + isNavigationHidden(activity));
    }

    private static boolean isNavigationHidden(Activity activity) {
        View decorView = activity.getWindow().getDecorView();
        return isNavigationHidden(decorView);
    }

    private static boolean isNavigationHidden(Dialog dialog) {
        View decorView = dialog.getWindow().getDecorView();
        return isNavigationHidden(decorView);
    }

    private static boolean isNavigationHidden(View decorView) {
        return (decorView.getSystemUiVisibility() & View.SYSTEM_UI_FLAG_HIDE_NAVIGATION) != 0 &&
                (decorView.getSystemUiVisibility() & View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY) != 0;
    }

    public static void hideKeyboardAtLaunch(View layout) {
        //add flags to layout
        //android:focusable="true"
        //android:focusableInTouchMode="true"
        layout.setFocusable(true);
        layout.setFocusableInTouchMode(true);
    }

    public static void onWindowFocusChanged(Activity activity, boolean hasFocus) {
        onWindowFocusChanged(activity.getWindow(), hasFocus);
    }

    public static void onWindowFocusChanged(Window window, boolean hasFocus) {
        //Tracer.print("isHidden = " + isNavigationHidden(activity) + " hasFocus = " + hasFocus);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT && hasFocus) {
            window.getDecorView().setSystemUiVisibility(
//                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE
//                            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
//                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN |
                    View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
//                            | View.SYSTEM_UI_FLAG_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
            );
        }
        //Tracer.print("isHidden = " + isNavigationHidden(activity));
    }

    public static boolean isViewContains(View view, MotionEvent event) {
        return isViewContains(view, (int) event.getRawX(), (int) event.getRawY());
    }

    public static boolean isViewContains(View view, int rx, int ry) {
        Rect rect = getViewRectOnScreen(view);
        return rect.contains(rx, ry);
    }

    public static Rect getViewRectOnScreen(View view) {
        int[] l = new int[2];
        view.getLocationOnScreen(l);
        return new Rect(l[0], l[1], l[0] + view.getMeasuredWidth(), l[1] + view.getMeasuredHeight());
    }

    public static Rect getViewRectWithoutTop(View view, float rate) {
        int offset = Math.round(rate * view.getMeasuredHeight());
        return new Rect(0, offset, view.getMeasuredWidth(), view.getMeasuredHeight());
    }

    public static Rect getViewRect(View view) {
        return new Rect(0, 0, view.getMeasuredWidth(), view.getMeasuredHeight());
    }

    public static Drawable getViewDrawable(View v) {
        final Bitmap bitmap = ViewHelper.getViewBitmap(v);
        return bitmap == null ? null : new BitmapDrawable(v.getResources(), bitmap);
    }

    public static Bitmap getViewBitmap(View v) {
        fixViewSize(v);

        if (Math.min(v.getWidth(), v.getHeight()) == 0) {
            return null;
        }

        Bitmap bitmap = Bitmap.createBitmap(v.getWidth(), v.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        v.draw(canvas);
        return bitmap;
    }

    public static void fixViewSize(View v) {
        //region Case for invisible view(just inflated). Fix view size
        if (v.getWidth() == 0 || v.getHeight() == 0) {
            v.measure(View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
                    View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
            v.layout(0, 0, v.getMeasuredWidth(), v.getMeasuredHeight());
        }
        //endregion

        //region fix for checkForRelayout
        if (v.getLayoutParams() == null) {
            v.setLayoutParams(
                    new ViewGroup.LayoutParams(
                            ViewGroup.LayoutParams.WRAP_CONTENT,
                            ViewGroup.LayoutParams.WRAP_CONTENT
                    )
            );
        }
        //endregion
    }

    public static Drawable getDrawable(Context context, @DrawableRes int id) {
        return context.getResources().getDrawable(id);
    }

    @ColorInt
    public static int getColor(Context context, @ColorRes int id) {
        return context.getResources().getColor(id);
    }

    @SuppressWarnings("all")
    public static ColorStateList getColorStateList(Context context, @DrawableRes int id) {
        return context.getResources().getColorStateList(id);
    }

    public static void setBackground(View view, Drawable background) {
        if (Build.VERSION.SDK_INT >= 16) {
            view.setBackground(background);
        } else {
            view.setBackgroundDrawable(background);
        }
    }

    public static void setDrawableColor(Drawable drawable, @ColorInt int color) {
        drawable.mutate().setColorFilter(color, PorterDuff.Mode.SRC_IN);
    }

    public static void setDrawableColor(ImageView imageView, @ColorInt int color) {
        imageView.getDrawable().mutate().setColorFilter(color, PorterDuff.Mode.SRC_IN);
    }

    public static void setBackgroundDrawableColor(View view, @ColorInt int color) {
        view.getBackground().mutate().setColorFilter(color, PorterDuff.Mode.SRC_IN);
    }

    public static Bitmap getBitmap(Drawable resource, Rect bounds) {
        if (bounds != null) {
            resource.setBounds(bounds);
        }
        if (resource instanceof BitmapDrawable) {
            return ((BitmapDrawable) resource).getBitmap();
        }

        Bitmap bitmap = Bitmap.createBitmap(resource.getIntrinsicWidth(),
                resource.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        resource.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        resource.draw(canvas);

        return bitmap;
    }

    public static Bitmap getBitmap(Drawable resource) {
        return getBitmap(resource, null);
    }

    public static void enableTransitionAnimation(ViewGroup viewGroup) {
        viewGroup.setLayoutTransition(new LayoutTransition());
    }

    public static void enableDeepTransitionAnimation(ViewGroup viewGroup) {
//        viewGroup.setLayoutTransition(new LayoutTransition());
        LayoutTransition layoutTransition = viewGroup.getLayoutTransition();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            layoutTransition.enableTransitionType(LayoutTransition.CHANGING);
            layoutTransition.enableTransitionType(LayoutTransition.APPEARING);
            layoutTransition.enableTransitionType(LayoutTransition.DISAPPEARING);
            layoutTransition.enableTransitionType(LayoutTransition.CHANGE_APPEARING);
            layoutTransition.enableTransitionType(LayoutTransition.CHANGE_DISAPPEARING);
        }
    }

    public static void setClearTextListener(final TextView textView, final Filter<String> filter) {
        ViewHelper.setTextChangedListener(textView, new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (filter.accept(s != null ? s.toString() : "")) {
                    textView.removeTextChangedListener(this);
                    textView.setText("");
                    textView.addTextChangedListener(this);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        textView.setText(textView.getText());
    }

    public static void setClearZeroStartedTextListener(final TextView textView) {
        ViewHelper.setClearTextListener(textView, new Filter<String>() {
            @Override
            public boolean accept(String source) {
                return source.startsWith("0");
            }
        });
    }

    public static void setFilterTextListener(final TextView textView, final Filter<String> filter) {
        ViewHelper.setTextChangedListener(textView, new TextWatcher() {
            private String beforeText;
            private int selectionStart;
            private int selectionEnd;

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                beforeText = s.toString();
                selectionStart = textView.getSelectionStart();
                selectionEnd = textView.getSelectionEnd();
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (filter.accept(s != null ? s.toString() : "")) {
                    textView.removeTextChangedListener(this);
                    textView.setText(beforeText);
                    if (textView instanceof EditText) {
                        ((EditText) textView).setSelection(selectionStart, selectionEnd);
                    }
                    textView.addTextChangedListener(this);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        textView.setText(textView.getText());
    }

    public static void setClearCharsListener(final TextView textView, final Filter<Character> filter) {
        ViewHelper.setTextChangedListener(textView, new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!TextUtils.isEmpty(s)) {
                    String originString = s.toString();
                    StringBuilder sb = new StringBuilder();
                    for (char c : originString.toCharArray()) {
                        if (!filter.accept(c)) {
                            sb.append(c);
                        }
                    }

                    String modifiedString = sb.toString();
                    if (!modifiedString.equals(originString)) {
                        textView.removeTextChangedListener(this);
                        textView.setText(modifiedString);
                        textView.addTextChangedListener(this);
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        textView.setText(textView.getText());
    }

    public static void setClearNonPasswordCharsListener(final TextView textView) {
        ViewHelper.setClearCharsListener(textView, new Filter<Character>() {

            @Override
            public boolean accept(Character source) {
                return source < 33 || source > 126;
            }
        });
    }

    public static void animateAlpha(View view, float endAlpha) {
        float alpha = view.getAlpha();
        view.animate().cancel();
        if (alpha != endAlpha) {
            view.animate().alpha(endAlpha).setDuration(300L).start();
        }
    }

    public static void setOnRightDrawableClick(final TextView view, final View.OnClickListener onClickListener) {
        if (onClickListener != null) {
            view.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    final int DRAWABLE_LEFT = 0;
                    final int DRAWABLE_TOP = 1;
                    final int DRAWABLE_RIGHT = 2;
                    final int DRAWABLE_BOTTOM = 3;


                    if (view.getCompoundDrawables()[DRAWABLE_RIGHT] != null) {
                        if (event.getRawX() >= (view.getRight() - view.getCompoundDrawables()[DRAWABLE_RIGHT].getBounds().width())) {
                            if (event.getAction() == MotionEvent.ACTION_UP) {
                                // your action here
                                onClickListener.onClick(view);
                            }
                            return true;
                        }
                    }
                    return false;
                }
            });
        } else {
            view.setOnTouchListener(null);
        }
    }

    public static void setHeight(View view, int value) {
        view.getLayoutParams().height = value;
        view.requestLayout();
    }

    public static void setWidth(View view, int value) {
        view.getLayoutParams().width = value;
        view.requestLayout();
    }

    public static int getViewId(Resources resources, String id) {
        return resources.getIdentifier(id, null, null);
    }

    public static View findViewById(View from, String id) {
        return from.findViewById(getViewId(from.getResources(), id));
    }

//    public static void setUpSearchView(SearchView searchView, @StringRes int hintRes) {
//        ImageView closeButtonImage = (ImageView) ViewHelper.findViewById(searchView, "android:id/search_close_btn");
//        closeButtonImage.setImageResource(R.drawable.close_search);
//        closeButtonImage.setAdjustViewBounds(true);
//
//
//        ImageView hintIcon = (ImageView) ViewHelper.findViewById(searchView, "android:id/search_button");
//        hintIcon.setImageResource(R.drawable.search_middle);
//
//        //fix setAdjustViewBounds for lower OS versions
//        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.JELLY_BEAN_MR1) {
//            closeButtonImage.setScaleType(ImageView.ScaleType.CENTER);
//            ViewGroup.LayoutParams lp = closeButtonImage.getLayoutParams();
//            lp.width = App.getAppResources().getDimensionPixelOffset(R.dimen.close_search_button_size);
//            lp.height = App.getAppResources().getDimensionPixelOffset(R.dimen.close_search_button_size);
//            closeButtonImage.setLayoutParams(lp);
//        }
//        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {
//            closeButtonImage.setBackgroundResource(R.drawable.background_selector_rounded);
//        }
//
//        View searchPlate = ViewHelper.findViewById(searchView, "android:id/search_plate");
//        searchPlate.setBackgroundResource(R.drawable.background_search_view);
//
//        ImageView searchImageView = (ImageView) ViewHelper.findViewById(searchView, "android:id/search_button");
//        searchImageView.setBackgroundResource(R.drawable.searchview_ripple_selector);
//
//        EditText searchEditText = (EditText) ViewHelper.findViewById(searchView, "android:id/search_src_text");
//        searchEditText.setHintTextColor(ViewHelper.getColor(R.color.gray_light));
//        searchEditText.setCompoundDrawablesWithIntrinsicBounds(R.drawable.search_middle, 0, 0, 0);
//        searchEditText.setCompoundDrawablePadding(ViewHelper.dpToPx(8));
//        ViewHelper.setCursorDrawableColor(searchEditText, ViewHelper.getColor(R.color.text_color));
//
//        try {
//            Class<?> clazz = Class.forName("android.widget.SearchView$SearchAutoComplete");
//
//            SpannableStringBuilder stopHint = new SpannableStringBuilder(getString(hintRes));
//
//            // Set the new hint text
//            Method setHintMethod = clazz.getMethod("setHint", CharSequence.class);
//            setHintMethod.invoke(searchEditText, stopHint);
//        } catch (ClassNotFoundException e) {
//            e.printStackTrace();
//        } catch (NoSuchMethodException e) {
//            e.printStackTrace();
//        } catch (IllegalAccessException e) {
//            e.printStackTrace();
//        } catch (InvocationTargetException e) {
//            e.printStackTrace();
//        }
//    }

    public static String pluralString(Resources resources, @PluralsRes int resId, int count) {
        return resources.getQuantityString(resId, count, count);
    }

    public static class DecimalInputFilter implements InputFilter {
        private final EditText mEditText;
        private final int mDigitsBeforeZero;
        private final int mDigitsAfterZero;

        public DecimalInputFilter(EditText editText, int digitsBeforeZero, int digitsAfterZero) {
            mEditText = editText;
            mDigitsBeforeZero = digitsBeforeZero;
            mDigitsAfterZero = digitsAfterZero;
        }

        @Override
        public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
            final String text = mEditText.getText().toString();

//            Tracer.print(text + " source = " + source + " source length = " + (source == null ? "null" : "" + source.length())
//                    + " start " + start + " end " + end + " dest " + dest + " dstart " + dstart + " dend " + dend);

            boolean clearChar = false;
            if (source == null || source.length() > 1) {
                clearChar = false;
                return clearChar ? "" : null;
            }

            boolean point = source.toString().equals(".");

            if (text.contains(".")) {
                final int pointPosition = text.indexOf(".");
                int beforePointPlaces = text.substring(0, pointPosition).length();
                int afterPointPlaces = text.substring(pointPosition + 1).length();
                if (point) {
                    clearChar = true;
                } else {
                    boolean beforePoint = dstart <= pointPosition;
                    if (beforePoint) {
                        clearChar = beforePointPlaces + 1 > mDigitsBeforeZero;
                    } else {
                        clearChar = afterPointPlaces + 1 > mDigitsAfterZero;
                    }
                }
            } else {
                if (point) {
                    String newText = new StringBuffer(text).insert(dstart, ".").toString();
                    final int pointPosition = newText.indexOf(".");
                    int beforePointPlaces = newText.substring(0, pointPosition).length();
                    int afterPointPlaces = newText.substring(pointPosition + 1).length();
                    clearChar = beforePointPlaces > mDigitsBeforeZero || afterPointPlaces > mDigitsAfterZero;
                } else {
                    int beforePointPlaces = text.length();
                    clearChar = beforePointPlaces + 1 > mDigitsBeforeZero;
                }
            }

            return clearChar ? "" : null;
        }
    }

    public static class MaxValInputFilter implements InputFilter {
        private final EditText mEditText;
        private final Source<Integer> mMax;

        public MaxValInputFilter(EditText editText, final int max) {
            mEditText = editText;
            mMax = new Source<Integer>() {
                @Override
                public Integer get() {
                    return max;
                }
            };
        }

        public MaxValInputFilter(EditText editText, Source<Integer> max) {
            mEditText = editText;
            mMax = max;
        }

        @Override
        public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
            String text = mEditText.getText().toString();
            String newText = new StringBuffer(text).insert(dstart, source.toString()).toString();

//            Tracer.print("text = " + text + " newText = " + newText + " source = " + source + " source length " + (source == null ? "null" : "" + source.length())
//                    + " start " + start + " end " + end + " dest = " + dest + " dstart " + dstart + " dend " + dend);

            int value = Utils.getIntValue(newText);
            int max = mMax.get();
            boolean clearChar = value > max || newText.length() > String.valueOf(max).length();

//            Tracer.print("\nclearChar = " + clearChar
//                    + "\nvalue = " + value
//                    + "\nmax = " + max
//            );

            return clearChar ? "" : null;
        }
    }

    //TODO fix text deletion
    public static class MinValInputFilter implements InputFilter {
        private final EditText mEditText;
        private final Source<Integer> mMin;

        public MinValInputFilter(EditText editText, final int min) {
            mEditText = editText;
            mMin = new Source<Integer>() {
                @Override
                public Integer get() {
                    return min;
                }
            };
        }

        public MinValInputFilter(EditText editText, Source<Integer> min) {
            mEditText = editText;
            mMin = min;
        }

        @Override
        public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
            String text = mEditText.getText().toString();
            String newText = new StringBuffer(text).insert(dstart, source.toString()).toString();

//            Tracer.print("text = " + text + " newText = " + newText + " source = " + source + " source length " + (source == null ? "null" : "" + source.length())
//                    + " start " + start + " end " + end + " dest = " + dest + " dstart " + dstart + " dend " + dend);

            int value = Utils.getIntValue(newText);
            int min = mMin.get();
            boolean clearChar = value < min;

//            Tracer.print("\nclearChar = " + clearChar
//                    + "\nvalue = " + value
//                    + "\nmin = " + min
//            );

            return clearChar ? "" : null;
        }
    }

    public static void setDecimalInputFilter(final EditText editText, int digitsBeforeZero, int digitsAfterZero) {
        editText.setFilters(new InputFilter[]{new DecimalInputFilter(editText, digitsBeforeZero, digitsAfterZero)});
    }

    public static void setMaxValInputFilter(final EditText editText, int max) {
        editText.setFilters(new InputFilter[]{new MaxValInputFilter(editText, max)});
    }

    public static void setMaxValInputFilter(final EditText editText, Source<Integer> max) {
        editText.setFilters(new InputFilter[]{new MaxValInputFilter(editText, max)});
    }

    public static void bindClickListener(View view, View.OnClickListener listener, @IdRes int... ids) {
        for (int id : ids) {
            View viewForClick = view.findViewById(id);
            if (viewForClick != null) {
                viewForClick.setOnClickListener(listener);
            }
        }
    }

    public static void bindClickListener(Activity activity, View.OnClickListener listener, @IdRes int... ids) {
        for (int id : ids) {
            View viewForClick = activity.findViewById(id);
            if (viewForClick != null) {
                viewForClick.setOnClickListener(listener);
            }
        }
    }

    public static void setDrawableLeft(TextView textView, View view) {
        setDrawableLeft(textView, getViewDrawable(view));
    }

    public static void setDrawableLeft(TextView textView, @DrawableRes int imageRes) {
        final Drawable drawable = imageRes == 0 ? null : textView.getResources().getDrawable(imageRes);
        setDrawableLeft(textView, drawable);
    }

    public static void setDrawableLeft(TextView textView, Drawable drawable) {
        Drawable[] drawables = textView.getCompoundDrawables();
        textView.setCompoundDrawablesWithIntrinsicBounds(
                drawable,
                drawables[1],
                drawables[2],
                drawables[3]
        );
    }

    public static void setDrawableTop(TextView textView, View view) {
        setDrawableTop(textView, getViewDrawable(view));
    }

    public static void setDrawableTop(TextView textView, @DrawableRes int imageRes) {
        final Drawable drawable = imageRes == 0 ? null : textView.getResources().getDrawable(imageRes);
        setDrawableTop(textView, drawable);
    }

    public static void setDrawableTop(TextView textView, Drawable drawable) {
        Drawable[] drawables = textView.getCompoundDrawables();
        textView.setCompoundDrawablesWithIntrinsicBounds(
                drawables[0],
                drawable,
                drawables[2],
                drawables[3]
        );
    }

    public static void setDrawableRight(TextView textView, View right) {
        setDrawableRight(textView, getViewDrawable(right));
    }

    public static void setDrawableRight(TextView textView, @DrawableRes int imageRes) {
        final Drawable right = imageRes == 0 ? null : textView.getResources().getDrawable(imageRes);
        setDrawableRight(textView, right);
    }

    public static void setDrawableRight(TextView textView, Drawable right) {
        Drawable[] drawables = textView.getCompoundDrawables();
        textView.setCompoundDrawablesWithIntrinsicBounds(
                drawables[0],
                drawables[1],
                right,
                drawables[3]
        );
    }

    public static void setDrawableBottom(TextView textView, View bottom) {
        setDrawableBottom(textView, getViewDrawable(bottom));
    }

    public static void setDrawableBottom(TextView textView, @DrawableRes int imageRes) {
        final Drawable bottom = imageRes == 0 ? null : textView.getResources().getDrawable(imageRes);
        setDrawableBottom(textView, bottom);
    }

    public static void setDrawableBottom(TextView textView, Drawable bottom) {
        Drawable[] drawables = textView.getCompoundDrawables();
        textView.setCompoundDrawablesWithIntrinsicBounds(
                drawables[0],
                drawables[1],
                drawables[2],
                bottom
        );
    }

    public static Drawable getDrawableLeft(TextView textView) {
        Drawable[] drawables = textView.getCompoundDrawables();
        return drawables[0];
    }

    public static Drawable getDrawableTop(TextView textView) {
        Drawable[] drawables = textView.getCompoundDrawables();
        return drawables[1];
    }

    public static Drawable getDrawableRight(TextView textView) {
        Drawable[] drawables = textView.getCompoundDrawables();
        return drawables[2];
    }

    public static Drawable getDrawableBottom(TextView textView) {
        Drawable[] drawables = textView.getCompoundDrawables();
        return drawables[3];
    }

    public static void crossOutTextView(TextView textView) {
        textView.setPaintFlags(textView.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
    }

    @SuppressWarnings("unchecked")
    private static <T> T getTag(View view, @IdRes int id, T defaultValue, Class<T> type) {
        final Object tag = view.getTag(id);
        if (tag != null) {
            return (T) tag;
        }
        return defaultValue;
    }

    private static <T> T getTag(View view, @IdRes int id, Class<T> type) {
        return getTag(view, id, null, type);
    }

    public static void setVisible(View view, boolean visible) {
        view.setVisibility(visible ? View.VISIBLE : View.GONE);
    }

    public static void setVisible(@Visibility int visibility, View... views) {
        for (View v : views) {
            v.setVisibility(visibility);
        }
    }

    public static void setEnabled(boolean enable, View... views) {
        for (View v : views) {
            v.setEnabled(enable);
        }
    }

    public static void setAlpha(float alpha, View... views) {
        for (View v : views) {
            v.setAlpha(alpha);
        }
    }

    public static View getRootView(Activity activity) {
        return activity.findViewById(android.R.id.content).getRootView();
    }

    public static void animateHide(Activity activity, @IdRes int id) {
        activity.findViewById(id).animate().alpha(0).setDuration(500).start();
    }

    public static void animateShow(Activity activity, @IdRes int id) {
        activity.findViewById(id).animate().alpha(1).setDuration(500).start();
    }

    public static void animatePopup(Activity activity, @IdRes int id) {
        final View view = activity.findViewById(id);

        int maxHeight = ViewHelper.dpToPx(activity, 94);
        ShowHideAnimation animation = new ShowHideAnimation(view, 300, 4000, 300, 0, maxHeight);
        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                view.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                view.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        view.startAnimation(animation);
    }

    public static class OnTouchRevealListener implements View.OnTouchListener {

        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
                animateReveal(view, (int) motionEvent.getX(), (int) motionEvent.getY());
            }
            return false;
        }
    }

    public static class OnTouchDisabledListener implements View.OnTouchListener {

        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            return true;
        }
    }

    public static void animateReveal(View view, int startX, int startY) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP && !view.isSelected()) {
            float radius;
            int width = view.getWidth();
            int height = view.getHeight();
            radius = (float) Math.sqrt(width * width + height * height);
            ViewAnimationUtils.createCircularReveal(view,
                    startX,
                    startY,
                    0,
                    radius * 1.15f).start();
        }
    }

    public static void setCustomTabView(TabLayout tabLayout, @LayoutRes int layoutRes) {
        // Iterate over all tabs and set the custom view
        for (int i = 0; i < tabLayout.getTabCount(); i++) {
            TabLayout.Tab tab = tabLayout.getTabAt(i);
            if (tab != null) {
                tab.setCustomView(layoutRes);
            }
        }
    }

    public static void enableSelectionAnimation(TabLayout tabLayout) {
        LinearLayout tabStrip = ((LinearLayout) tabLayout.getChildAt(0));
        for (int i = 0; i < tabStrip.getChildCount(); i++) {
            View tabView = tabStrip.getChildAt(i);
            enableSelectionAnimation(tabView);
            if (i == 0) {
                tabView.setSelected(false);
                tabView.setSelected(true);
            }
        }
    }

    public static void enableSelectionAnimation(View view) {
        enableSelectionAnimation(view, R.drawable.ripple_selector);
    }

    public static void enableSelectionAnimation(View view, @DrawableRes int resId) {
        view.setBackgroundResource(resId);
        view.setOnTouchListener(new ViewHelper.OnTouchRevealListener());
        ViewHelper.enableTransitionAnimation((ViewGroup) view);
    }

    public static void clearFocus(TabLayout tabLayout) {
        LinearLayout tabStrip = ((LinearLayout) tabLayout.getChildAt(0));
        for (int i = 0; i < tabStrip.getChildCount(); i++) {
            View tabView = tabStrip.getChildAt(i);
            tabView.setSelected(false);
        }
    }

    public static void enableTransitionAnimation(TabLayout tabLayout) {
        LinearLayout tabStrip = ((LinearLayout) tabLayout.getChildAt(0));
        for (int i = 0; i < tabStrip.getChildCount(); i++) {
            View tabView = tabStrip.getChildAt(i);
            ViewHelper.enableTransitionAnimation((ViewGroup) tabView);
        }
    }

    public static void setToolbar(final Activity activity, Toolbar toolbar) {
        setToolbar(toolbar, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //What to do on back clicked
                try {
                    KeyboardHelper.hideKeyboard(ViewHelper.getRootView(activity));
                    activity.onBackPressed();
                } catch (Exception e) {
                    //already finishing
                }
            }
        });
    }

    public static void setToolbar(Toolbar toolbar, View.OnClickListener onBackClickListener) {
        if (toolbar != null) {
            // inflate your menu
            toolbar.setNavigationIcon(R.drawable.abc_ic_ab_back_mtrl_am_alpha);//abc_ic_ab_back_mtrl_am_alpha
            toolbar.setNavigationOnClickListener(onBackClickListener);
        }
    }

    public static void showToolbarBackButton(Toolbar toolbar, boolean show) {
        if (toolbar != null) {
            // inflate your menu
            if (show) {
                toolbar.setNavigationIcon(R.drawable.abc_ic_ab_back_mtrl_am_alpha);//abc_ic_ab_back_material
            } else {
                toolbar.setNavigationIcon(null);
            }
        }
    }

    public static void runInUiThread(Runnable runnable) {
        if (runnable == null) {
            return;
        }

        if (isUiThread()) {
            runnable.run();
        } else {
            new Handler(Looper.getMainLooper()).post(runnable);
        }
    }

    public static boolean isUiThread() {
        return Looper.myLooper() == Looper.getMainLooper();
    }

    public static void sleep(AtomicBoolean lock) {
        sleep(lock, 1000);
    }

    public static void sleep(AtomicBoolean lock, long stepTime) {
        while (lock.get()) {
            try {
                Thread.sleep(stepTime);
            } catch (InterruptedException e) {
                Log.e("ViewHelper", "e", e);
            }
        }
    }

    public static void sleep(long time) {
        try {
            Thread.sleep(time);
        } catch (InterruptedException e) {
            Log.e("ViewHelper", "e", e);
        }
    }

    /**
     * @param color color
     * @return is black
     */
    public static boolean getContrastYIQ(int color) {
        int b = (color) & 0xFF;
        int g = (color >> 8) & 0xFF;
        int r = (color >> 16) & 0xFF;
        int a = (color >> 24) & 0xFF;
        int yiq = ((r * 299) + (g * 587) + (b * 114)) / 1000;
        return (yiq >= 128);
    }

    public static void recyclerViewCount(RecyclerView recyclerView, OnCountCompletedListener onCountCompletedListener) {
        int firstVisibleItem;
        int lastVisibleItem;
        int visibleItemCount;
        int totalItemCount;
        RecyclerView.LayoutManager layoutManager = recyclerView.getLayoutManager();
        if (layoutManager instanceof LinearLayoutManager) {
            LinearLayoutManager linearLayoutManager = (LinearLayoutManager) layoutManager;
            firstVisibleItem = linearLayoutManager.findFirstVisibleItemPosition();
            lastVisibleItem = linearLayoutManager.findLastVisibleItemPosition();
            totalItemCount = linearLayoutManager.getItemCount();
        } else if (layoutManager instanceof GridLayoutManager) {
            GridLayoutManager gridLayoutManager = (GridLayoutManager) layoutManager;
            firstVisibleItem = gridLayoutManager.findFirstVisibleItemPosition();
            lastVisibleItem = gridLayoutManager.findLastVisibleItemPosition();
            totalItemCount = gridLayoutManager.getItemCount();
        } else {
            throw new RuntimeException("NOT implemented!");
        }
        visibleItemCount = lastVisibleItem - firstVisibleItem + 1;

        onCountCompletedListener.onCountCompleted(firstVisibleItem, lastVisibleItem, visibleItemCount, totalItemCount);
    }

    public interface OnCountCompletedListener {
        void onCountCompleted(int firstVisibleItem, int lastVisibleItem, int visibleItemCount, int totalItemCount);
    }

    public static class TextLink {
        public String name;
        public String uri;
        public int start;
        public int end;
        public String type;

        public TextLink(String name, String uri, int start, int end, String type) {
            this.name = name;
            this.uri = uri;
            this.start = start;
            this.end = end;
            this.type = type;
        }

        public TextLink() {
        }

        public boolean isValid() {
            return start >= 0 && end >= 0 && start < end && !Utils.isEmpty(name) && !Utils.isEmpty(uri);
        }

        public boolean collision(TextLink link) {
            final boolean ok = (link.start < start && link.end < start) || (link.start > end && link.end > end);
            return !ok;
        }

        @Override
        public String toString() {
            final StringBuilder sb = new StringBuilder("TextLink{");
            sb.append("name='").append(name).append('\'');
            sb.append(", uri='").append(uri).append('\'');
            sb.append(", start=").append(start);
            sb.append(", end=").append(end);
            sb.append(", type='").append(type).append('\'');
            sb.append('}');
            return sb.toString();
        }
    }

    public static ArrayList<TextLink> findTextLinks(String originText) {
        ArrayList<TextLink> links = new ArrayList<>();
        if (!Utils.isEmpty(originText)) {
            Pattern pattern = Patterns.WEB_URL;//compile("\\bhttps?://([^.,;!?\\s]|[.,;!?](?=[^.,;!?\\s]))+");
            Matcher matcher = pattern.matcher(originText);
            while (matcher.find()) {
                String match = matcher.group();
                int start = matcher.start();
                int end = matcher.end();
                if (match.startsWith("Http://") || match.startsWith("Https://") || match.startsWith("Rtsp://")) {
                    char c[] = match.toCharArray();
                    c[0] = Character.toLowerCase(c[0]);
                    match = new String(c);
                }

                String name = match;

                if (!match.startsWith("https://") && !match.startsWith("http://") && !match.startsWith("rtsp://")) {
                    match = "http://" + match;
                }

                links.add(new TextLink(name, match, start, end, "text"));
            }
        }
        return links;
    }

    public static ArrayList<TextLink> findMarkdownLinks(String text) {
        ArrayList<TextLink> linkList = new ArrayList<>();

        if (Utils.isEmpty(text)) {
            return linkList;
        }

        final String divider = "](";
        final int dividerLength = divider.length();
        final int minLinkNameLength = 1;

        int start = 0;
        while (start <= text.length()) {
            TextLink link = new TextLink();
            link.type = "markdown";
            link.start = text.indexOf("[", start);
            if (link.start < 0) {
                break;
            }

            link.end = link.start + 1;
            while (link.end <= text.length()) {
                link.end = text.indexOf(")", link.end);
                if (link.end < 0) {
                    break;
                }
                link.end++;

                if (text.substring(link.start, link.end).lastIndexOf(divider) >= 0) {
                    int dividerIndex = link.start + text.substring(link.start, link.end).lastIndexOf(divider);

                    //find shortest link name:)
                    for (int i = dividerIndex - 1 - minLinkNameLength; i >= link.start; i--) {
                        if (text.charAt(i) == '[') {
                            link.start = i;
                            break;
                        }
                    }

                    link.name = text.substring(link.start + 1, dividerIndex);
                    String url = text.substring(dividerIndex + dividerLength, link.end - 1);

                    if (Utils.isEmpty(url)) {
                        url = null;
                    }

                    if (url != null) {
                        int matches = 0;
                        Pattern pattern = Patterns.WEB_URL;//compile("\\bhttps?://([^.,;!?\\s]|[.,;!?](?=[^.,;!?\\s]))+");
                        Matcher matcher = pattern.matcher(url);
                        while (matcher.find()) {
                            String match = matcher.group();
                            if (!url.equals(match)) {
                                url = null;
                                break;
                            }
                            matches++;
                        }

                        if (matches != 1) {
                            url = null;
                        }
                    }

                    if (!Utils.isEmpty(url) && !url.startsWith("https://") && !url.startsWith("http://") && !url.startsWith("rtsp://")) {
                        url = "http://" + url;
                    }

                    link.uri = url;
                }

                if (link.isValid()) {
                    break;
                } else {
                    link.end = link.end + 1;//find end of link starting this index on next loop
                }
            }

            if (link.isValid()) {
                linkList.add(link);
                start = link.end + 1;
            } else {
                break;
            }
        }

        return linkList;
    }

    public static ArrayList<TextLink> findLinks(String text) {
//        Tracer.print(text);
        ArrayList<TextLink> markdownLinks = findMarkdownLinks(text);
//        Tracer.print(Dumper.dump(markdownLinks));
        ArrayList<TextLink> textLinks = findTextLinks(text);
//        Tracer.print(Dumper.dump(textLinks));

        Iterator<TextLink> iterator = textLinks.iterator();
        while (iterator.hasNext()) {
            TextLink textLink = iterator.next();
            for (TextLink markdownLink : markdownLinks) {
                if (markdownLink.collision(textLink)) {
                    iterator.remove();
                    break;
                }
            }
        }

        ArrayList<TextLink> result = new ArrayList<>();
        result.addAll(markdownLinks);
        result.addAll(textLinks);

        Collections.sort(result, new Comparator<TextLink>() {
            @Override
            public int compare(TextLink lhs, TextLink rhs) {
                if (lhs.start < rhs.start && lhs.end < rhs.start) {
                    return -1;
                } else if (lhs.start > rhs.end && lhs.end > rhs.end) {
                    return 1;
                } else {
                    throw new IllegalStateException("Links collision");
                }
            }
        });

//        Tracer.print(Dumper.dump(result));
        return result;
    }

    public static CharSequence getSpannedText(String text) {
        return getSpannedText(text, null);
    }

    @Nullable
    public static CharSequence getSpannedText(String text, @Nullable final Consumer<String> linkCallback) {
        if (!Utils.isEmpty(text)) {
            ArrayList<TextLink> links = ViewHelper.findLinks(text);

//            Tracer.print("text = " + text + "\nlinks = " + Dumper.dump(links));

            int delta = 0;
            for (TextLink link : links) {
                link.start += delta;
                link.end += delta;

                text = text.substring(0, link.start) + link.name + text.substring(link.end, text.length());

                int newEnd = link.start + link.name.length();
                delta = delta + (newEnd - link.end);
                link.end = newEnd;
            }

//            Tracer.print("new text = " + text);

            SpannableString spannableString = new SpannableString(text);

            for (final ViewHelper.TextLink link : links) {
//                URLSpan span = new URLSpan(link.uri);
//                spannableString.setSpan(span, link.start, link.end, 0);

                ViewHelper.setOnClick(spannableString, link.start, link.end, true, new ClickableSpan() {
                    @Override
                    public void onClick(View widget) {
                        if (linkCallback != null) {
                            linkCallback.consume(link.uri);
                        }
                    }
                });
            }
            return spannableString;
        }
        return text;
    }

    public static boolean isReuseView(View holder, int hashCode) {
        if (holder.getTag(R.id.source_tag) != null) {
            if (holder.getTag(R.id.source_tag).equals(HOLDER + hashCode)) {
                return true;
            }
        }
        holder.setTag(R.id.source_tag, HOLDER + hashCode);
        return false;
    }

    public static List<Fragment> getAllFragments(FragmentActivity activity) {
        List<Fragment> allFragments = activity.getSupportFragmentManager().getFragments();
        if (allFragments == null || allFragments.isEmpty()) {
            return Collections.emptyList();
        }
        return allFragments;
    }

    public static List<Fragment> getVisibleFragments(FragmentActivity activity) {
        List<Fragment> allFragments = getAllFragments(activity);

        List<Fragment> visibleFragments = new ArrayList<Fragment>();
        for (Fragment fragment : allFragments) {
            if (fragment.isVisible()) {
                visibleFragments.add(fragment);
            }
        }
        return visibleFragments;
    }

    public static boolean isApplicationForeground(Context context) {
        KeyguardManager keyguardManager = (KeyguardManager) context.getSystemService(Context.KEYGUARD_SERVICE);
        if (keyguardManager.inKeyguardRestrictedInputMode()) {
            return false;
        } else {
            int myPid = Process.myPid();
            ActivityManager var3 = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
            List runningApps = var3.getRunningAppProcesses();
            if (runningApps != null) {
                Iterator iterator = runningApps.iterator();

                while (iterator.hasNext()) {
                    ActivityManager.RunningAppProcessInfo processInfo = (ActivityManager.RunningAppProcessInfo) iterator.next();
                    if (processInfo.pid == myPid) {
                        return processInfo.importance == 100;
                    }
                }
            }

            return false;
        }
    }
}
