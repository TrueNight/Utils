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
import android.app.Application;

import java.lang.ref.WeakReference;

/**
 * Created by true
 * date: 30/07/15
 * time: 18:50
 */
//@Profiler.ProfileMethod
public class BaseApplication extends Application {

    private static BaseApplication msApplication = BuildConfig.DEBUG ? new BaseApplication() : null; // to fix layout preview
    private WeakReference<Activity> mCurrentActivity;

    public static BaseApplication getInstance() {
        return msApplication;
    }

    public Activity topActivity() {
        return Utils.unwrap(mCurrentActivity);
    }

    @Override
    public void onCreate() {
        msApplication = this;

        mCurrentActivity = null;
        registerActivityLifecycleCallbacks(new SimpleActivityLifecycleCallbacks() {
            @Override
            public void onActivityResumed(Activity activity) {
                mCurrentActivity = new WeakReference<>(activity);
            }

            @Override
            public void onActivityDestroyed(Activity activity) {
                if (Utils.unwrap(mCurrentActivity) == activity) {
                    mCurrentActivity = null;
                }
            }
        });
        super.onCreate();
    }
}
