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
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;

import java.util.HashMap;
import java.util.HashSet;

public class PermissionRequest {

    /**
     * Callback Interface
     */
    public interface Response {
        /**
         * Would be called if the permissions are granted
         * {@inheritDoc}
         */
        void permissionGranted();

        /**
         * Would be called if any permission are denied
         * {@inheritDoc}
         */
        void permissionDenied();
    }

    private static class ResponseWrapper {
        final Response response;
        final String[] permission;

        public ResponseWrapper(Response response, String permission) {
            this.response = response;
            this.permission = new String[]{permission};
        }

        ResponseWrapper(Response response, String[] permission) {
            this.response = response;
            this.permission = permission;
        }
    }

    private static final HashMap<Integer, ResponseWrapper> map = new HashMap<>();

    /**
     * Request Android Permissions.
     *
     * @param context     The Context of the Activity or the Fragment.
     * @param permissions The Permission you are need.
     * @param response    Result callback.
     */
    public static void getPermission(@NonNull Activity context, String permissions, @NonNull Response response) {
        getPermission(context, new String[]{permissions}, response);
    }

    /**
     * Request Android Permissions.
     *
     * @param context     The Context of the Activity or the Fragment.
     * @param permissions The Permission you are need.
     * @param response    Result callback.
     */
    public static void getPermission(@NonNull Fragment context, String permissions, @NonNull Response response) {
        getPermission(context, new String[]{permissions}, response);
    }

    /**
     * Check if the user has granted the permissions.
     *
     * @param context     a Android context
     * @param permissions The Permission you are need to check.
     * @return true if all permissions are granted or false if any permission is missing
     */
    public static boolean hasAllPermission(@NonNull Context context, @NonNull String... permissions) {
        if (Build.VERSION.SDK_INT < 23) {
            return true;
        } else {
            for (String permission : permissions) {
                if (ContextCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
            return true;
        }
    }

    /**
     * Check if the user has granted the permissions.
     *
     * @param context     a Android context
     * @param permissions The Permission you are need to check.
     * @return true if all permissions are granted or false if any permission is missing
     */
    public static boolean hasAllPermission(@NonNull Fragment context, @NonNull String... permissions) {
        if (Build.VERSION.SDK_INT < 23) {
            return true;
        } else {
            for (String permission : permissions) {
                if (ContextCompat.checkSelfPermission(context.getContext(), permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
            return true;
        }
    }

    /**
     * Request Android Permissions.
     *
     * @param context     The Context of the Activity or the Fragment.
     * @param permissions The Permissions you are need.
     * @param response    Result callback.
     */
    public static void getPermission(@NonNull Activity context, @NonNull String[] permissions, @NonNull Response response) {
        if (Build.VERSION.SDK_INT < 23) {
            response.permissionGranted();
        } else {

            HashSet<String> permissionSet = new HashSet<>();

            for (String permission : permissions)
                if (ContextCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    permissionSet.add(permission);
                }

            if (permissionSet.size() > 0) {
                int id = 42167;
                while (map.containsKey(id))
                    id = (int) Math.round(Math.random() * Integer.MAX_VALUE);

                map.put(id, new ResponseWrapper(response, permissions));

                context.requestPermissions(permissionSet.toArray(new String[permissionSet.size()]), id);
            } else {
                response.permissionGranted();
            }
        }
    }


    public static void getPermission(@NonNull Fragment context, @NonNull String[] permissions, @NonNull Response response) {
        if (Build.VERSION.SDK_INT < 23) {
            response.permissionGranted();
        } else {

            HashSet<String> permissionSet = new HashSet<>();

            for (String permission : permissions)
                if (ContextCompat.checkSelfPermission(context.getContext(), permission) != PackageManager.PERMISSION_GRANTED) {
                    permissionSet.add(permission);
                }

            if (permissionSet.size() > 0) {
                int id = 42167;
                while (map.containsKey(id))
                    id = (int) Math.round(Math.random() * Integer.MAX_VALUE);

                map.put(id, new ResponseWrapper(response, permissions));

                context.requestPermissions(permissionSet.toArray(new String[permissionSet.size()]), id);
            } else {
                response.permissionGranted();
            }
        }
    }

    /**
     * Must bee called by Activity.onRequestPermissionsResult
     *
     * @param requestCode  The automatically generated request code
     * @param permissions  The requested permissions
     * @param grantResults The result codes of the request
     * @see Activity
     */
    public static void onRequestPermissionsResult(int requestCode, String permissions[], @NonNull int[] grantResults) {
        ResponseWrapper responseWrapper = map.get(requestCode);
        if (responseWrapper == null) return;

        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

            // permission was granted, yay! Do the

            responseWrapper.response.permissionGranted();

        } else {

            // permission denied, boo! Disable the
            // functionality that depends on this permission.

            responseWrapper.response.permissionDenied();
        }
    }
}
