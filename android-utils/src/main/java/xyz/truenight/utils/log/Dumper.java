package xyz.truenight.utils.log;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.ComponentCallbacks2;
import android.content.ComponentName;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.content.pm.ApplicationInfo;
import android.content.pm.ComponentInfo;
import android.content.pm.FeatureInfo;
import android.content.pm.InstrumentationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageItemInfo;
import android.content.pm.PermissionGroupInfo;
import android.content.pm.PermissionInfo;
import android.content.pm.ProviderInfo;
import android.content.pm.ResolveInfo;
import android.content.pm.ServiceInfo;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteQueryBuilder;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.CancellationSignal;
import android.os.Debug;
import android.support.v4.app.FragmentActivity;
import android.util.Pair;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.FileFilter;
import java.io.Serializable;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.GenericArrayType;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Proxy;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.lang.reflect.WildcardType;
import java.net.URI;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.zip.ZipEntry;

import xyz.truenight.utils.BaseApplication;
import xyz.truenight.utils.helper.ViewHelper;

public class Dumper {

    private static final List<DumpExtension> sExtensionList;

    static {
        ArrayList<DumpExtension> extensions = new ArrayList<DumpExtension>();
        extensions.add(new AndroidDumpExtension());
        extensions.add(new DefaultDumpExtension());
        sExtensionList = Collections.unmodifiableList(extensions);
    }

    private Dumper() {

    }

    public static abstract class DumpExtension {

        protected final String dump(Object value, ArrayList dumpedObjects) {
            return Dumper.dump(value, dumpedObjects);
        }

        public abstract String dumpImpl(Object value, ArrayList dumpedObjects);

        public abstract boolean canDump(Object value);
    }

    public static String dump(Object... value) {
        return dump(value, new ArrayList());
    }

    public static String dump(Object value) {
        return dump(value, new ArrayList());
    }

    public static String dump(Object value, ArrayList dumpedObjects) {
        if (value == null) {
            return "null";
        }

        for (Object dumpedItem : dumpedObjects) {
            if (dumpedItem == value) {
                return String.format("duplicate %s@%s", value.getClass().getName(), Integer.toHexString(value.hashCode()));
            }
        }

        if (value == dumpedObjects) {
            return String.format("duplicate %s@%s", value.getClass().getName(), Integer.toHexString(value.hashCode()));
        }

        dumpedObjects.add(value);

        String result = null;
        for (DumpExtension extension : sExtensionList) {
            if (extension.canDump(value)) {
                result = extension.dumpImpl(value, dumpedObjects);
                break;
            }
        }

        dumpedObjects.remove(value);

        return result != null ? result :/*obj.getClass().getName() + " " + */value.toString();
    }

    private static class AndroidDumpExtension extends Dumper.DumpExtension {
        @Override
        public String dumpImpl(Object value, ArrayList dumpedObjects) {
            if (!(canDump(value))) {
                throw new IllegalArgumentException(value.getClass().getName()
                        + " unsupported for dump");
            }

            if (value instanceof Uri) {
                return dumpImpl((Uri) value, dumpedObjects);
            } else if (value instanceof URI) {
                return dumpImpl((URI) value, dumpedObjects);
            } else if (value instanceof Activity) {
                return dumpImpl((Activity) value, dumpedObjects);
            } else if (value instanceof Intent) {
                return dumpImpl((Intent) value, dumpedObjects);
            } else if (value instanceof IntentFilter) {
                return dumpImpl((IntentFilter) value, dumpedObjects);
            } else if (value instanceof Bundle) {
                return dumpImpl((Bundle) value, dumpedObjects);
            } else if (value instanceof Pair) {
                return dumpImpl((Pair) value, dumpedObjects);
            } else if (value instanceof ActivityManager.RunningTaskInfo) {
                return dumpImpl((ActivityManager.RunningTaskInfo) value, dumpedObjects);
            } else if (value instanceof Cursor) {
                return dumpImpl((Cursor) value, dumpedObjects);
            } else if (value instanceof SQLiteDatabase) {
                return dumpImpl((SQLiteDatabase) value, dumpedObjects);
            } else if (value instanceof PermissionInfo) {
                return dumpImpl((PermissionInfo) value, dumpedObjects);
            } else if (value instanceof PermissionGroupInfo) {
                return dumpImpl((PermissionGroupInfo) value, dumpedObjects);
            } else if (value instanceof InstrumentationInfo) {
                return dumpImpl((InstrumentationInfo) value, dumpedObjects);
            } else if (value instanceof ActivityInfo) {
                return dumpImpl((ActivityInfo) value, dumpedObjects);
            } else if (value instanceof ServiceInfo) {
                return dumpImpl((ServiceInfo) value, dumpedObjects);
            } else if (value instanceof ProviderInfo) {
                return dumpImpl((ProviderInfo) value, dumpedObjects);
            } else if (value instanceof ComponentInfo) {
                return dumpImpl((ComponentInfo) value, dumpedObjects);
            } else if (value instanceof ApplicationInfo) {
                return dumpImpl((ApplicationInfo) value, dumpedObjects);
            } else if (value instanceof PackageInfo) {
                return dumpImpl((PackageInfo) value, dumpedObjects);
            } else if (value instanceof FeatureInfo) {
                return dumpImpl((FeatureInfo) value, dumpedObjects);
            } else if (value instanceof ResolveInfo) {
                return dumpImpl((ResolveInfo) value, dumpedObjects);
            } else if (value instanceof ComponentName) {
                return dumpImpl((ComponentName) value, dumpedObjects);
            } else if (value instanceof PackageItemInfo) {
                return dumpImpl((PackageItemInfo) value, dumpedObjects);
            } else if (value instanceof ActivityManager.MemoryInfo) {
                return dumpImpl((ActivityManager.MemoryInfo) value, dumpedObjects);
            } else if (value instanceof ActivityManager.RunningAppProcessInfo) {
                return dumpImpl((ActivityManager.RunningAppProcessInfo) value, dumpedObjects);
            } else if (value instanceof Debug.MemoryInfo) {
                return dumpImpl((Debug.MemoryInfo) value, dumpedObjects);
            } else if (value instanceof JSONObject) {
                return dumpImpl((JSONObject) value, dumpedObjects);
            } else if (value instanceof JSONArray) {
                return dumpImpl((JSONArray) value, dumpedObjects);
            } else if (value instanceof Bitmap) {
                return dumpImpl((Bitmap) value, dumpedObjects);
            } else if (value instanceof Menu) {
                return dumpImpl((Menu) value, dumpedObjects);
            } else if (value instanceof Typeface) {
                return dumpImpl((Typeface) value, dumpedObjects);
            } else if (value instanceof View) {
                return dumpImpl((View) value, dumpedObjects);
            }

            throw new IllegalStateException("Failed dump " + value.getClass().getName());
        }

        @Override
        public boolean canDump(Object value) {

            if (value instanceof Uri) {
                return true;
            } else if (value instanceof URI) {
                return true;
            } else if (value instanceof Activity) {
                return true;
            } else if (value instanceof Intent) {
                return true;
            } else if (value instanceof IntentFilter) {
                return true;
            } else if (value instanceof Bundle) {
                return true;
            } else if (value instanceof android.util.Pair) {
                return true;
            } else if (value instanceof ActivityManager.RunningTaskInfo) {
                return true;
            } else if (value instanceof Cursor) {
                return true;
            } else if (value instanceof SQLiteDatabase) {
                return true;
            } else if (value instanceof PermissionInfo) {
                return true;
            } else if (value instanceof PermissionGroupInfo) {
                return true;
            } else if (value instanceof InstrumentationInfo) {
                return true;
            } else if (value instanceof ActivityInfo) {
                return true;
            } else if (value instanceof ServiceInfo) {
                return true;
            } else if (value instanceof ProviderInfo) {
                return true;
            } else if (value instanceof ComponentInfo) {
                return true;
            } else if (value instanceof ApplicationInfo) {
                return true;
            } else if (value instanceof PackageInfo) {
                return true;
            } else if (value instanceof FeatureInfo) {
                return true;
            } else if (value instanceof ResolveInfo) {
                return true;
            } else if (value instanceof ComponentName) {
                return true;
            } else if (value instanceof PackageItemInfo) {
                return true;
            } else if (value instanceof ActivityManager.MemoryInfo) {
                return true;
            } else if (value instanceof ActivityManager.RunningAppProcessInfo) {
                return true;
            } else if (value instanceof Debug.MemoryInfo) {
                return true;
            } else if (value instanceof JSONObject) {
                return true;
            } else if (value instanceof JSONArray) {
                return true;
            } else if (value instanceof Bitmap) {
                return true;
            } else if (value instanceof Menu) {
                return true;
            } else if (value instanceof Typeface) {
                return true;
            } else if (value instanceof View) {
                return true;
            }

            return false;
        }

        @SuppressLint("NewApi")
        private String dumpImpl(Uri uri, ArrayList dumpedObjects) {
            String authority = uri.getAuthority();
            String fragment = uri.getFragment();
            String host = uri.getHost();
            String path = uri.getPath();
            int port = uri.getPort();
            String query = uri.getQuery();

            String encodedAuthority = uri.getEncodedAuthority();
            String encodedFragment = uri.getEncodedFragment();
            String encodedPath = uri.getEncodedPath();
            String encodedQuery = uri.getEncodedQuery();
            String encodedSchemeSpecificPart = uri.getEncodedSchemeSpecificPart();
            String encodedUserInfo = uri.getEncodedUserInfo();

            String scheme = uri.getScheme();
            String schemeSpecificPart = uri.getSchemeSpecificPart();
            String userInfo = uri.getUserInfo();

            String normalizeScheme = null;
            String lastPathSegment = uri.getLastPathSegment();
            List<String> pathSegments = uri.getPathSegments();
            boolean isAbsolute = uri.isAbsolute();
            boolean isOpaque = uri.isOpaque();
            Boolean isHierarchical = uri.isHierarchical();
            Boolean isRelative = uri.isRelative();
            Set<String> queryParameterNames = null;

            String toASCIIString = null;
            String normalize = null;

            if (OsVersionHelper.have(11)) {
                queryParameterNames = uri.getQueryParameterNames();
            }

            if (OsVersionHelper.have(16)) {
                normalizeScheme = uri.normalizeScheme().toString();
            }

            try {
                URI javaUri = URI.create(uri.toString());
                toASCIIString = javaUri.toASCIIString();
                normalize = javaUri.normalize().toString();
            } catch (Exception e) {
                Tracer.e(e);
            }

            final StringBuilder sb = new StringBuilder();
            sb.append(uri);
            sb.append("\n");
            sb.append(" authority = ").append(authority);
            sb.append("\n");
            sb.append(" encodedAuthority = ").append(encodedAuthority);
            sb.append("\n");
            sb.append(" encodedFragment = ").append(encodedFragment);
            sb.append("\n");
            sb.append(" encodedPath = ").append(encodedPath);
            sb.append("\n");
            sb.append(" encodedQuery = ").append(encodedQuery);
            sb.append("\n");
            sb.append(" encodedSchemeSpecificPart = ").append(
                    encodedSchemeSpecificPart);
            sb.append("\n");
            sb.append(" encodedUserInfo = ").append(encodedUserInfo);
            sb.append("\n");
            sb.append(" fragment = ").append(fragment);
            sb.append("\n");
            sb.append(" host = ").append(host);
            sb.append("\n");
            sb.append(" lastPathSegment = ").append(lastPathSegment);
            sb.append("\n");
            sb.append(" path = ").append(path);
            sb.append("\n");
            sb.append(" pathSegments = ").append(pathSegments);
            sb.append("\n");
            sb.append(" port = ").append(port);
            sb.append("\n");
            sb.append(" query = ").append(query);
            sb.append("\n");
            sb.append(" queryParameterNames = ").append(queryParameterNames);
            sb.append("\n");
            sb.append(" scheme = ").append(scheme);
            sb.append("\n");
            sb.append(" schemeSpecificPart = ").append(schemeSpecificPart);
            sb.append("\n");
            sb.append(" userInfo = ").append(userInfo);
            sb.append("\n");

            sb.append(" toASCIIString = ").append(toASCIIString);
            sb.append("\n");
            sb.append(" normalize = ").append(normalize);
            sb.append("\n");
            sb.append(" normalizeScheme = ").append(normalizeScheme);
            sb.append("\n");
            sb.append(" isAbsolute = ").append(isAbsolute);
            sb.append("\n");
            sb.append(" isOpaque = ").append(isOpaque);
            sb.append("\n");
            sb.append(" isHierarchical = ").append(isHierarchical);
            sb.append("\n");
            sb.append(" isRelative = ").append(isRelative);
            sb.append("\n");
            return sb.toString();
        }

        @SuppressLint("NewApi")
        private String dumpImpl(URI uri, ArrayList dumpedObjects) {
            String authority = uri.getAuthority();
            String fragment = uri.getFragment();
            String host = uri.getHost();
            String path = uri.getPath();
            int port = uri.getPort();
            String query = uri.getQuery();

            String encodedAuthority = uri.getRawAuthority();
            String encodedFragment = uri.getRawFragment();
            String encodedPath = uri.getRawPath();
            String encodedQuery = uri.getRawQuery();
            String encodedSchemeSpecificPart = uri.getRawSchemeSpecificPart();
            String encodedUserInfo = uri.getRawUserInfo();

            String scheme = uri.getScheme();
            String schemeSpecificPart = uri.getSchemeSpecificPart();
            String userInfo = uri.getUserInfo();

            String toASCIIString = uri.toASCIIString();
            String normalize = uri.normalize().toString();

            String normalizeScheme = null;
            String lastPathSegment = null;
            List<String> pathSegments = null;
            boolean isAbsolute = uri.isAbsolute();
            boolean isOpaque = uri.isOpaque();
            Boolean isHierarchical = null;
            Boolean isRelative = null;
            Set<String> queryParameterNames = null;

            try {
                Uri androidUri = Uri.parse(uri.toString());
                lastPathSegment = androidUri.getLastPathSegment();
                pathSegments = androidUri.getPathSegments();
                isHierarchical = androidUri.isHierarchical();
                isRelative = androidUri.isRelative();

                if (OsVersionHelper.have(11)) {
                    queryParameterNames = androidUri.getQueryParameterNames();
                }

                if (OsVersionHelper.have(16)) {
                    normalizeScheme = androidUri.normalizeScheme().toString();
                }
            } catch (Exception e) {
                Tracer.e(e);
            }

            final StringBuilder sb = new StringBuilder();
            sb.append(uri);
            sb.append("\n");
            sb.append(" authority = ").append(authority);
            sb.append("\n");
            sb.append(" encodedAuthority = ").append(encodedAuthority);
            sb.append("\n");
            sb.append(" encodedFragment = ").append(encodedFragment);
            sb.append("\n");
            sb.append(" encodedPath = ").append(encodedPath);
            sb.append("\n");
            sb.append(" encodedQuery = ").append(encodedQuery);
            sb.append("\n");
            sb.append(" encodedSchemeSpecificPart = ").append(
                    encodedSchemeSpecificPart);
            sb.append("\n");
            sb.append(" encodedUserInfo = ").append(encodedUserInfo);
            sb.append("\n");
            sb.append(" fragment = ").append(fragment);
            sb.append("\n");
            sb.append(" host = ").append(host);
            sb.append("\n");
            sb.append(" lastPathSegment = ").append(lastPathSegment);
            sb.append("\n");
            sb.append(" path = ").append(path);
            sb.append("\n");
            sb.append(" pathSegments = ").append(pathSegments);
            sb.append("\n");
            sb.append(" port = ").append(port);
            sb.append("\n");
            sb.append(" query = ").append(query);
            sb.append("\n");
            sb.append(" queryParameterNames = ").append(queryParameterNames);
            sb.append("\n");
            sb.append(" scheme = ").append(scheme);
            sb.append("\n");
            sb.append(" schemeSpecificPart = ").append(schemeSpecificPart);
            sb.append("\n");
            sb.append(" userInfo = ").append(userInfo);
            sb.append("\n");

            sb.append(" toASCIIString = ").append(toASCIIString);
            sb.append("\n");
            sb.append(" normalize = ").append(normalize);
            sb.append("\n");
            sb.append(" normalizeScheme = ").append(normalizeScheme);
            sb.append("\n");
            sb.append(" isAbsolute = ").append(isAbsolute);
            sb.append("\n");
            sb.append(" isOpaque = ").append(isOpaque);
            sb.append("\n");
            sb.append(" isHierarchical = ").append(isHierarchical);
            sb.append("\n");
            sb.append(" isRelative = ").append(isRelative);
            sb.append("\n");
            return sb.toString();
        }

        private String dumpImpl(Activity activity, ArrayList dumpedObjects) {
            return activity.getClass().getSimpleName();
        }

        @SuppressLint("NewApi")
        private String dumpImpl(Intent intent, ArrayList dumpedObjects) {
            StringBuilder sb = new StringBuilder("Intent [");
            sb.append("act=").append(intent.getAction());
            sb.append(", cat=").append(dump(intent.getCategories(), dumpedObjects));
            sb.append(", dat=").append(dump(intent.getData(), dumpedObjects));
            sb.append(", typ=").append(intent.getType());
            sb.append(", flg=0x").append(Integer.toHexString(intent.getFlags()));
            sb.append(", pkg=").append(intent.getPackage());
            sb.append(", cmp=").append(intent.getComponent() != null ? intent.getComponent().flattenToShortString() : null);
            sb.append(", bnds=").append(intent.getSourceBounds());
            if (OsVersionHelper.have(16)) {
                sb.append(", clip=").append(dump(intent.getClipData(), dumpedObjects));
            }
            sb.append(", ext=").append(dump(intent.getExtras(), dumpedObjects));
            if (OsVersionHelper.have(15)) {
                sb.append(", sel=").append(dump(intent.getSelector(), dumpedObjects));
            }
            sb.append("]");
            return sb.toString();
        }

        private String dumpImpl(IntentFilter intentFilter, ArrayList dumpedObjects) {
            return "IntentFilter [Actions : " + dump(intentFilter.actionsIterator(), dumpedObjects) + "]";
        }

        private String dumpImpl(Bundle bundle, ArrayList dumpedObjects) {
            return "Bundle [Content : " + dump(DumpUtils.ConvertCollections.toHashMap(bundle), dumpedObjects) + "]";
        }

        private String dumpImpl(android.util.Pair pair, ArrayList dumpedObjects) {
            return String.format("Pair[first = %s, second = %s]", dump(pair.first, dumpedObjects), dump(pair.second, dumpedObjects));
        }

        private String dumpImpl(ActivityManager.RunningTaskInfo info, ArrayList dumpedObjects) {
            StringBuilder sb = new StringBuilder();
            sb.append("RunningTaskInfo [");
            sb.append("topActivity = ");
            if (info.topActivity != null) {
                sb.append(info.topActivity.getPackageName()).append(" / ").append(info.topActivity.getClassName());
            } else {
                sb.append("null");
            }

            sb.append(", description = ");
            if (info.topActivity != null) {
                sb.append(info.description);
            } else {
                sb.append("null");
            }

            sb.append("]");
            return sb.toString();
        }

        private String dumpImpl(Cursor cursor, ArrayList dumpedObjects) {
            StringBuilder sb = new StringBuilder();
            sb.append(">>>>> Dumping cursor " + cursor + "\n");
            if (cursor != null) {
                int startPos = cursor.getPosition();

                cursor.moveToPosition(-1);
                while (cursor.moveToNext()) {
                    String[] cols = cursor.getColumnNames();
                    sb.append("" + cursor.getPosition() + " {\n");
                    int length = cols.length;
                    for (int i = 0; i < length; i++) {
                        String value;
                        try {
                            value = cursor.getString(i);
                        } catch (SQLiteException e) {
                            // assume that if the getString threw this exception then the column is not
                            // representable by a string, e.g. it is a BLOB.
                            value = "<unprintable>";
                        }

                        if (cols[i] != null && cols[i].contains("date")) {
                            try {
                                String date = DateTimeHelper.getDate(Long.parseLong(value));
                                sb.append("   " + cols[i] + '=' + value + "(" + date + ")" + "\n");
                            } catch (Throwable e) {
                                sb.append("   " + cols[i] + '=' + value + "\n");
                            }
                        } else {
                            sb.append("   " + cols[i] + '=' + value + "\n");
                        }
                    }
                    sb.append("}\n");
                }
                cursor.moveToPosition(startPos);
            }
            sb.append("<<<<<\n");
            return sb.toString();
        }

        @SuppressLint("NewApi")
        private String dumpImpl(SQLiteDatabase database, ArrayList dumpedObjects) {
            return DumpUtils.Database.dump(database);
        }

        //region Dump info structures. FIXME not dump constants
        private String dumpImpl(PermissionInfo info, ArrayList dumpedObjects) {
            return DumpUtils.dumpFields(info);
        }

        private String dumpImpl(PermissionGroupInfo info, ArrayList dumpedObjects) {
            return DumpUtils.dumpFields(info);
        }

        private String dumpImpl(InstrumentationInfo info, ArrayList dumpedObjects) {
            return DumpUtils.dumpFields(info);
        }

        private String dumpImpl(ActivityInfo info, ArrayList dumpedObjects) {
            return DumpUtils.dumpFields(info);
        }

        private String dumpImpl(ServiceInfo info, ArrayList dumpedObjects) {
            return DumpUtils.dumpFields(info);
        }

        private String dumpImpl(ProviderInfo info, ArrayList dumpedObjects) {
            return DumpUtils.dumpFields(info);
        }

        private String dumpImpl(ComponentInfo info, ArrayList dumpedObjects) {
            return DumpUtils.dumpFields(info);
        }

        private String dumpImpl(ApplicationInfo info, ArrayList dumpedObjects) {
            return DumpUtils.dumpFields(info);
        }

        private String dumpImpl(PackageInfo info, ArrayList dumpedObjects) {
            return DumpUtils.dumpFields(info);
        }

        private String dumpImpl(FeatureInfo info, ArrayList dumpedObjects) {
            return DumpUtils.dumpFields(info);
        }

        private String dumpImpl(ResolveInfo info, ArrayList dumpedObjects) {
            return DumpUtils.dumpFields(info);
        }

        private String dumpImpl(ComponentName info, ArrayList dumpedObjects) {
            return DumpUtils.dumpFields(info);
        }

        private String dumpImpl(PackageItemInfo info, ArrayList dumpedObjects) {
            return DumpUtils.dumpFields(info);
        }

        private String dumpImpl(ActivityManager.MemoryInfo info, ArrayList dumpedObjects) {
            final StringBuilder sb = new StringBuilder("MemoryInfo{");
            sb.append("\navailMem=").append(TextHelper.humanReadableByteCount(info.availMem));
            if (OsVersionHelper.have(16)) {
                sb.append(",\ntotalMem=").append(TextHelper.humanReadableByteCount(info.totalMem));
            }
            sb.append(",\nthreshold=").append(TextHelper.humanReadableByteCount(info.threshold));
            sb.append(",\nlowMemory=").append(info.lowMemory);
            sb.append('}');
            return sb.toString();
        }

        private String dumpImpl(ActivityManager.RunningAppProcessInfo info, ArrayList dumpedObjects) {
            final StringBuilder sb = new StringBuilder("RunningAppProcessInfo{");
            sb.append("\nprocessName='").append(info.processName).append('\'');
            sb.append(",\npid=").append(info.pid);
            sb.append(",\nuid=").append(info.uid);
            sb.append(",\npkgList=").append(Arrays.toString(info.pkgList));
            sb.append(",\nlastTrimLevel=").append(DumpUtils.dumpTrimMemory(info.lastTrimLevel));
            sb.append(",\nimportance=").append(info.importance);
            sb.append(",\nlru=").append(info.lru);
            sb.append(",\nimportanceReasonCode=").append(info.importanceReasonCode);
            sb.append(",\nimportanceReasonPid=").append(info.importanceReasonPid);
            sb.append(",\nimportanceReasonComponent=").append(Dumper.dump(info.importanceReasonComponent, dumpedObjects));
            sb.append('}');
            return sb.toString();
        }

        private String dumpImpl(Debug.MemoryInfo info, ArrayList dumpedObjects) {
            final StringBuilder sb = new StringBuilder("MemoryInfo{");
            sb.append("\ndalvikPss=").append(TextHelper.humanReadableByteCount(info.dalvikPss));
            sb.append(",\ndalvikPrivateDirty=").append(TextHelper.humanReadableByteCount(info.dalvikPrivateDirty));
            sb.append(",\ndalvikSharedDirty=").append(TextHelper.humanReadableByteCount(info.dalvikSharedDirty));
            sb.append(",\nnativePss=").append(TextHelper.humanReadableByteCount(info.nativePss));
            sb.append(",\nnativePrivateDirty=").append(TextHelper.humanReadableByteCount(info.nativePrivateDirty));
            sb.append(",\nnativeSharedDirty=").append(TextHelper.humanReadableByteCount(info.nativeSharedDirty));
            sb.append(",\notherPss=").append(TextHelper.humanReadableByteCount(info.otherPss));
            sb.append(",\notherSharedDirty=").append(TextHelper.humanReadableByteCount(info.otherSharedDirty));
//        if (OsVersionHelper.have(23)) {
//            sb.append(",\ngetMemoryStats=").append(Dumper.dump(info.getMemoryStats(), dumpedObjects));
//        }
            if (OsVersionHelper.have(19)) {
                sb.append(",\ngetTotalPrivateClean=").append(TextHelper.humanReadableByteCount(info.getTotalPrivateClean()));
            }
            sb.append(",\ngetTotalPrivateDirty=").append(TextHelper.humanReadableByteCount(info.getTotalPrivateDirty()));
            sb.append(",\ngetTotalPss=").append(TextHelper.humanReadableByteCount(info.getTotalPss()));
            if (OsVersionHelper.have(19)) {
                sb.append(",\ngetTotalSharedClean=").append(TextHelper.humanReadableByteCount(info.getTotalSharedClean()));
            }
            sb.append(",\ngetTotalSharedDirty=").append(TextHelper.humanReadableByteCount(info.getTotalSharedDirty()));
            if (OsVersionHelper.have(19)) {
                sb.append(",\ngetTotalSwappablePss=").append(TextHelper.humanReadableByteCount(info.getTotalSwappablePss()));
            }
            sb.append('}');
            return sb.toString();
        }
        //endregion

        private String dumpImpl(JSONObject object, ArrayList dumpedObjects) {
            return object.toString();
        }

        private String dumpImpl(JSONArray array, ArrayList dumpedObjects) {
            return array.toString();
        }

        private String dumpImpl(Bitmap bitmap, ArrayList dumpedObjects) {
            final StringBuilder sb = new StringBuilder();
            sb.append("Bitmap {\n")
                    .append(bitmap.getWidth())
                    .append(";")
                    .append(bitmap.getHeight())
                    .append(", \nmin size = ")
                    .append(TextHelper.humanReadableByteCount(bitmap.getByteCount()));

            if (OsVersionHelper.have(19)) {
                sb.append(", \nsize = ")
                        .append(TextHelper.humanReadableByteCount(bitmap.getAllocationByteCount()));
            }
            sb.append(", \ndensity = ")
                    .append(bitmap.getDensity());

            sb.append(", \nisRecycled = ")
                    .append(bitmap.isRecycled());
            sb.append(", \nisMutable = ")
                    .append(bitmap.isMutable());

            if (OsVersionHelper.have(17)) {
                sb.append(", \nisPremultiplied = ")
                        .append(bitmap.isPremultiplied());
            }
            sb.append(", \nhasAlpha = ")
                    .append(bitmap.hasAlpha());

            if (OsVersionHelper.have(17)) {
                sb.append(", \nhasMipMap = ")
                        .append(bitmap.hasMipMap());
            }

            sb.append(", \ngenerationId = ")
                    .append(bitmap.getGenerationId());

            sb.append("}");
            return sb.toString();
        }

        private String dumpImpl(Menu menu, ArrayList dumpedObjects) {
            final StringBuilder sb = new StringBuilder();
            sb.append("Menu {\n");
            sb.append("item count = ").append(menu.size()).append("\n");
            for (int i = 0; i < menu.size(); i++) {
                final MenuItem item = menu.getItem(i);
                sb.append("item").append(i)
                        .append("{id=").append(item.getItemId())
                        .append(" visible = ").append(item.isVisible())
                        .append(" title = ").append(item.getTitle())
                        .append("}\n");
            }
            sb.append("}");

            return sb.toString();
        }

        private String dumpImpl(Typeface typeface, ArrayList dumpedObjects) {
            final StringBuilder sb = new StringBuilder();
            sb.append("Typeface {\n");
            sb.append("isBold = ").append(typeface.isBold()).append("\n");
            sb.append("isItalic = ").append(typeface.isItalic()).append("\n");
            sb.append("}");
            return sb.toString();
        }

        private String dumpImpl(View object, ArrayList dumpedObjects) {
            return DumpUtils.dumpViewState(object);
        }
    }

    private static class DefaultDumpExtension extends Dumper.DumpExtension {

        @Override
        public String dumpImpl(Object value, ArrayList dumpedObjects) {
            if (!(canDump(value))) {
                throw new IllegalArgumentException(value.getClass().getName()
                        + " unsupported for dump");
            }

            if (value instanceof byte[]) {
                return dumpImpl((byte[]) value, dumpedObjects);
            } else if (value instanceof short[]) {
                return dumpImpl((short[]) value, dumpedObjects);
            } else if (value instanceof int[]) {
                return dumpImpl((int[]) value, dumpedObjects);
            } else if (value instanceof long[]) {
                return dumpImpl((long[]) value, dumpedObjects);
            } else if (value instanceof float[]) {
                return dumpImpl((float[]) value, dumpedObjects);
            } else if (value instanceof double[]) {
                return dumpImpl((double[]) value, dumpedObjects);
            } else if (value instanceof char[]) {
                return dumpImpl((char[]) value, dumpedObjects);
            } else if (value instanceof boolean[]) {
                return dumpImpl((boolean[]) value, dumpedObjects);
            } else if (value instanceof Object[]) {
                return dumpImpl((Object[]) value, dumpedObjects);
            } else if (value instanceof Properties) {
                return dumpImpl((Properties) value, dumpedObjects);
            } else if (value instanceof Collection<?>) {
                return dumpImpl((Collection<?>) value, dumpedObjects);
            } else if (value instanceof Map) {
                return dumpImpl((Map) value, dumpedObjects);
            } else if (value instanceof Map.Entry) {
                return dumpImpl((Map.Entry) value, dumpedObjects);
            } else if (value instanceof Iterator) {
                return dumpImpl((Iterator) value, dumpedObjects);
            } else if (value instanceof Throwable) {
                return dumpImpl((Throwable) value, dumpedObjects);
            } else if (value instanceof URI) {
                return dumpImpl((URI) value, dumpedObjects);
            } else if (value instanceof ZipEntry) {
                return dumpImpl((ZipEntry) value, dumpedObjects);
            } else if (value instanceof Thread) {
                return dumpImpl((Thread) value, dumpedObjects);
            } else if (value instanceof ThreadGroup) {
                return dumpImpl((ThreadGroup) value, dumpedObjects);
            }

            throw new IllegalStateException("Failed dump " + value.getClass().getName());
        }

        @Override
        public boolean canDump(Object value) {
            if (value instanceof byte[]) {
                return true;
            } else if (value instanceof short[]) {
                return true;
            } else if (value instanceof int[]) {
                return true;
            } else if (value instanceof long[]) {
                return true;
            } else if (value instanceof float[]) {
                return true;
            } else if (value instanceof double[]) {
                return true;
            } else if (value instanceof char[]) {
                return true;
            } else if (value instanceof boolean[]) {
                return true;
            } else if (value instanceof Object[]) {
                return true;
            } else if (value instanceof Properties) {//extends Map
                return true;
            } else if (value instanceof Collection<?>) {
                return true;
            } else if (value instanceof Map) {
                return true;
            } else if (value instanceof Map.Entry) {
                return true;
            } else if (value instanceof Iterator) {
                return true;
            } else if (value instanceof Throwable) {
                return true;
            } else if (value instanceof URI) {
                return true;
            } else if (value instanceof ZipEntry) {
                return true;
            } else if (value instanceof Thread) {
                return true;
            } else if (value instanceof ThreadGroup) {
                return true;
            }
            return false;
        }

        private static String dumpImpl(byte[] array, ArrayList dumpedObjects) {
            return Arrays.toString(array);
        }

        private static String dumpImpl(short[] array, ArrayList dumpedObjects) {
            return Arrays.toString(array);
        }

        private static String dumpImpl(int[] array, ArrayList dumpedObjects) {
            return Arrays.toString(array);
        }

        private static String dumpImpl(long[] array, ArrayList dumpedObjects) {
            return Arrays.toString(array);
        }

        private static String dumpImpl(float[] array, ArrayList dumpedObjects) {
            return Arrays.toString(array);
        }

        private static String dumpImpl(double[] array, ArrayList dumpedObjects) {
            return Arrays.toString(array);
        }

        private static String dumpImpl(char[] array, ArrayList dumpedObjects) {
            return Arrays.toString(array);
        }

        private static String dumpImpl(boolean[] array, ArrayList dumpedObjects) {
            return Arrays.toString(array);
        }

        private String dumpImpl(Object[] array, ArrayList dumpedObjects) {
            return dump(Arrays.asList(array), dumpedObjects);
        }

        private String dumpImpl(Collection<?> collection, ArrayList dumpedObjects) {
            return dump(collection.iterator(), dumpedObjects);
        }

        private String dumpImpl(Map<?, ?> map, ArrayList dumpedObjects) {
            return map.getClass().getSimpleName() + " " + dump(map.entrySet().iterator(), dumpedObjects);
        }

        private String dumpImpl(Map.Entry<?, ?> entry, ArrayList dumpedObjects) {
            return dump(entry.getKey(), dumpedObjects) + "=" + dump(entry.getValue(), dumpedObjects);
        }

        private String dumpImpl(Iterator iterator, ArrayList dumpedObjects) {
            StringBuilder sb = new StringBuilder();
            sb.append('[');

            if (iterator.hasNext()) {
                sb.append(dump(iterator.next(), dumpedObjects));

                while (iterator.hasNext()) {
                    sb.append(", ").append(dump(iterator.next(), dumpedObjects));
                }
            }

            sb.append(']');
            return sb.toString();
        }

        private String dumpImpl(Throwable ex, ArrayList dumpedObjects) {
            StringBuilder sb = new StringBuilder();
            String info = null;
            try {
                info = ex.toString();
            } catch (Throwable e) {

            }

            sb.append(info).append('\n');

            StackTraceElement[] stackTrace = null;
            try {
                stackTrace = ex.getStackTrace();
            } catch (Throwable e) {

            }

            if (stackTrace == null) {
                sb.append("null").append('\n');
            } else {
                for (StackTraceElement stackTraceElement : stackTrace) {
                    sb.append(stackTraceElement.toString()).append('\n');
                }
            }

            Throwable cause = null;
            try {
                cause = ex.getCause();
            } catch (Throwable e) {

            }

            if (cause != null && cause != ex) {
                sb.append("Caused by: ").append('\n').append(dump(cause, dumpedObjects));
            }
            return sb.toString();
        }

        private static String dumpImpl(URI uri, ArrayList dumpedObjects) {
            String authority = uri.getAuthority();
            String fragment = uri.getFragment();
            String host = uri.getHost();
            String path = uri.getPath();
            int port = uri.getPort();
            String query = uri.getQuery();

            String encodedAuthority = uri.getRawAuthority();
            String encodedFragment = uri.getRawFragment();
            String encodedPath = uri.getRawPath();
            String encodedQuery = uri.getRawQuery();
            String encodedSchemeSpecificPart = uri.getRawSchemeSpecificPart();
            String encodedUserInfo = uri.getRawUserInfo();

            String scheme = uri.getScheme();
            String schemeSpecificPart = uri.getSchemeSpecificPart();
            String userInfo = uri.getUserInfo();

            String toASCIIString = uri.toASCIIString();
            String normalize = uri.normalize().toString();

            String normalizeScheme = null;
            String lastPathSegment = null;
            List<String> pathSegments = null;
            boolean isAbsolute = uri.isAbsolute();
            boolean isOpaque = uri.isOpaque();
            Boolean isHierarchical = null;
            Boolean isRelative = null;
            Set<String> queryParameterNames = null;

            final StringBuilder sb = new StringBuilder();
            sb.append(uri);
            sb.append("\n");
            sb.append(" authority = ").append(authority);
            sb.append("\n");
            sb.append(" encodedAuthority = ").append(encodedAuthority);
            sb.append("\n");
            sb.append(" encodedFragment = ").append(encodedFragment);
            sb.append("\n");
            sb.append(" encodedPath = ").append(encodedPath);
            sb.append("\n");
            sb.append(" encodedQuery = ").append(encodedQuery);
            sb.append("\n");
            sb.append(" encodedSchemeSpecificPart = ").append(
                    encodedSchemeSpecificPart);
            sb.append("\n");
            sb.append(" encodedUserInfo = ").append(encodedUserInfo);
            sb.append("\n");
            sb.append(" fragment = ").append(fragment);
            sb.append("\n");
            sb.append(" host = ").append(host);
            sb.append("\n");
            sb.append(" lastPathSegment = ").append(lastPathSegment);
            sb.append("\n");
            sb.append(" path = ").append(path);
            sb.append("\n");
            sb.append(" pathSegments = ").append(pathSegments);
            sb.append("\n");
            sb.append(" port = ").append(port);
            sb.append("\n");
            sb.append(" query = ").append(query);
            sb.append("\n");
            sb.append(" queryParameterNames = ").append(queryParameterNames);
            sb.append("\n");
            sb.append(" scheme = ").append(scheme);
            sb.append("\n");
            sb.append(" schemeSpecificPart = ").append(schemeSpecificPart);
            sb.append("\n");
            sb.append(" userInfo = ").append(userInfo);
            sb.append("\n");

            sb.append(" toASCIIString = ").append(toASCIIString);
            sb.append("\n");
            sb.append(" normalize = ").append(normalize);
            sb.append("\n");
            sb.append(" normalizeScheme = ").append(normalizeScheme);
            sb.append("\n");
            sb.append(" isAbsolute = ").append(isAbsolute);
            sb.append("\n");
            sb.append(" isOpaque = ").append(isOpaque);
            sb.append("\n");
            sb.append(" isHierarchical = ").append(isHierarchical);
            sb.append("\n");
            sb.append(" isRelative = ").append(isRelative);
            sb.append("\n");
            return sb.toString();
        }

        private String dumpImpl(ZipEntry zipEntry, ArrayList dumpedObjects) {
            StringBuilder sb = new StringBuilder();
            sb.append("ZipEntry [");
            sb.append("\n").append("class = " + zipEntry.getClass());
            sb.append("\n").append("name = " + zipEntry.getName());
            sb.append("\n").append("isDirectory = " + zipEntry.isDirectory());
            sb.append("\n").append("comment = " + zipEntry.getComment());
            sb.append("\n").append("compressedSize = " + zipEntry.getCompressedSize());
            sb.append("\n").append("crc = " + zipEntry.getCrc());
//        sb.append("\n").append("creationTime = " + zipEntry.getCreationTime());
            sb.append("\n").append("extra = " + zipEntry.getExtra());
//        sb.append("\n").append("lastAccessTime = " + zipEntry.getLastAccessTime());
//        sb.append("\n").append("lastModifiedTime = " + zipEntry.getLastModifiedTime());
            sb.append("\n").append("method = " + zipEntry.getMethod());
            sb.append("\n").append("size = " + zipEntry.getSize());
            sb.append("\n").append("time = " + zipEntry.getTime());
            sb.append("\n").append("]");
            return sb.toString();
        }

        private String dumpImpl(Properties properties, ArrayList dumpedObjects) {
            Set<String> keys = properties.stringPropertyNames();
            HashMap<String, String> hm = new HashMap<String, String>();
            for (String key : keys) {
                hm.put(key, properties.getProperty(key));
            }
            return String.format("Properties [%s]", dump(hm, dumpedObjects));
        }

        private String dumpImpl(Thread object, ArrayList dumpedObjects) {
            if (object == null) {
                return null;
            }
            return new StringBuilder()
                    .append(object.getClass().getName())
                    .append("[").append("@").append(Integer.toHexString(object.hashCode()))
                    .append(", id=").append(object.getId())
                    .append(", name=").append(object.getName())
                    .append(", getPriority = ").append(object.getPriority())
                    .append(", getState = ").append(object.getState())
                    .append(", isDaemon = ").append(object.isDaemon())
                    .append(", isAlive = ").append(object.isAlive())
                    .append(", isInterrupted = ").append(object.isInterrupted())
//                .append(", getContextClassLoader = ").append("" + object.getContextClassLoader())
                    .append(", getThreadGroup = ").append(dump(object.getThreadGroup(), dumpedObjects))
                    .append("]").toString() + object.getId();
        }

        private String dumpImpl(ThreadGroup object, ArrayList dumpedObjects) {
            return new StringBuilder()
                    .append(object.getClass().getName())
                    .append("[").append("@").append(Integer.toHexString(object.hashCode()))
                    .append(", name=").append(object.getName())
                    .append(", maxPriority=").append(object.getMaxPriority())
                    .append(", isDaemon = ").append(object.isDaemon())
                    .append("]").toString();
        }
    }

    private static class OsVersionHelper {

        public static boolean have(int versionCode) {
            return Build.VERSION.SDK_INT >= versionCode;
        }

        public static int osVersion() {
            return Build.VERSION.SDK_INT;
        }
    }

    public static class TextHelper {

        public static String ellipsize(String s, int maxLength) {
            return s != null && s.length() > maxLength ? s.substring(0, maxLength) + "..." : s;
        }

        public static String removeInvisibleChars(String string) {
            return string.replaceAll("[\\p{Cs}\\p{Cc}\\p{Cf}\\p{Co}\\p{Cn}]", "");
        }

        public static String join(CharSequence delimiter, Iterable tokens) {
            StringBuilder sb = new StringBuilder();
            boolean firstTime = true;
            for (Object token : tokens) {
                if (firstTime) {
                    firstTime = false;
                } else {
                    sb.append(delimiter);
                }
                sb.append(token);
            }
            return sb.toString();
        }

        private String removeFromEnd(String source, String endStr) {
            StringBuilder sb = new StringBuilder(source);
            int lastIndexOf;
            while ((lastIndexOf = sb.lastIndexOf(endStr)) > -1 && lastIndexOf + endStr.length() == sb.length()) {
                sb.setLength(lastIndexOf);
            }
            return sb.toString();
        }

        public static String makeNotNull(String s) {
            return s != null ? s : "";
        }

        public static boolean isEmpty(String s) {
            return s == null || s.length() == 0;
        }

        public static boolean isNotEmpty(String s) {
            return !isEmpty(s);
        }

        public static boolean isAllEmpty(String... strArr) {
            for (String s : strArr) {
                if (!isEmpty(s)) {
                    return false;
                }
            }
            return true;
        }

        public static boolean isAllNotEmpty(String... strArr) {
            for (String s : strArr) {
                if (isEmpty(s)) {
                    return false;
                }
            }
            return true;
        }

        public static int getAfterSpaceIndex(String line, int startIndex) {
            int afterSpaceIndex;
            int spaceIndex = line.indexOf(' ', startIndex);
            if (spaceIndex >= 0) {
                try {
                    afterSpaceIndex = spaceIndex + 1;
                    while (line.charAt(afterSpaceIndex) == ' ') {
                        afterSpaceIndex++;
                    }
                    return afterSpaceIndex;
                } catch (Exception e) {
                    //after space not found
                    return -1;
                }
            } else {
                //space not found
                return -1;
            }
        }

        public static ArrayList<String> wordWrap(String line, int lineLength) {
            if (TextHelper.isEmpty(line)) {
                throw new IllegalArgumentException("Empty line unsupported");
            }

            if (line.contains("\n")) {
                throw new IllegalArgumentException("Line with next line chars unsupported");
            }

            ArrayList<String> lines = new ArrayList<String>();

            int startIndex = 0;
            while (true) {
                int afterSpaceIndex = TextHelper.getAfterSpaceIndex(line, startIndex);
                if (afterSpaceIndex == -1) {
                    //add rest of the line
                    lines.add(line.substring(startIndex));
                    break;
                } else if (afterSpaceIndex - startIndex >= lineLength) {
                    //first after space occurrence >= lineLength. add part of the line
                    lines.add(line.substring(startIndex, afterSpaceIndex));
                    startIndex = afterSpaceIndex;
                } else {
                    //find most appropriate after space occurrence
                    int afterSpaceIndexNext;
                    while ((afterSpaceIndexNext = TextHelper.getAfterSpaceIndex(line, afterSpaceIndex)) - startIndex < lineLength && afterSpaceIndexNext != -1) {
                        afterSpaceIndex = afterSpaceIndexNext;
                    }

                    lines.add(line.substring(startIndex, afterSpaceIndex));
                    startIndex = afterSpaceIndex;
                }
            }
            return lines;
        }

//        public static List<String> splitLines(String string) {
//            List<String> origTextLines = null;
//            try {
//                origTextLines = StreamReader.streamToStringList(new StringInputStream(string));
//            } catch (IOException e) {
//                Tracer.e(e);
//            }
//            return origTextLines;
//        }

        public static String concat(List<String> text, boolean newLine) {
            StringBuilder sb = new StringBuilder();
            for (String string : text) {
                sb.append(string);
                if (newLine) {
                    sb.append("\n");
                }
            }
            return sb.toString();
        }

        public static String humanReadableByteCount(long bytes) {
            return humanReadableByteCount(bytes, false);
        }

        /**
         * @param bytes number of bytes
         * @param si    is SI metric system. if <code>false</code> binary format will be used
         * @return
         */
        public static String humanReadableByteCount(long bytes, boolean si) {
            int unit = si ? 1000 : 1024;
            if (bytes < unit) return bytes + " B";
            int exp = (int) (Math.log(bytes) / Math.log(unit));
            String pre = "" + (si ? "kMGTPE" : "KMGTPE").charAt(exp - 1);
            return String.format("%.1f %sB", bytes / Math.pow(unit, exp), pre);
        }
    }

    public static class DumpUtils {

        public static class Database {

            private enum ColumnType {
                UNKNOWN, FIELD_TYPE_NULL, FIELD_TYPE_INTEGER, FIELD_TYPE_FLOAT, FIELD_TYPE_STRING, FIELD_TYPE_BLOB;


                public static ColumnType getTypeById(int id) {
                    ColumnType type = UNKNOWN;

                    switch (id) {
                        case Cursor.FIELD_TYPE_NULL:
                            type = FIELD_TYPE_NULL;
                            break;
                        case Cursor.FIELD_TYPE_INTEGER:
                            type = FIELD_TYPE_INTEGER;
                            break;
                        case Cursor.FIELD_TYPE_FLOAT:
                            type = FIELD_TYPE_FLOAT;
                            break;
                        case Cursor.FIELD_TYPE_STRING:
                            type = FIELD_TYPE_STRING;
                            break;
                        case Cursor.FIELD_TYPE_BLOB:
                            type = FIELD_TYPE_BLOB;
                            break;
                    }
                    return type;
                }

                public static String getColumnTypeString(int id) {
                    ColumnType type = getTypeById(id);
                    return "" + (type == UNKNOWN ? UNKNOWN + "(" + id + ")" : type);
                }

                @SuppressLint("NewApi")
                private static String getColumnType(Cursor cursor, int index) {
                    if (OsVersionHelper.have(11)) {
                        return ColumnType.getColumnTypeString(cursor.getType(index));
                    } else {
                        return "Unknown(type detect available since api 11)";
                    }
                }
            }

            @SuppressLint("NewApi")
            public static String dump(SQLiteDatabase database) {
                StringBuilder sb = new StringBuilder();
                sb.append("Database:\npath = ").append(database.getPath()).append("\n");
                sb.append("version = ").append(database.getVersion()).append("\n");
                sb.append("pageSize = ").append(database.getPageSize()).append("\n");
                sb.append("maximumSize = ").append(database.getMaximumSize()).append("\n");

                if (OsVersionHelper.have(11)) {
                    sb.append("attachedDbs = ").append(database.getAttachedDbs()).append("\n");
                }

                sb.append("isOpen = ").append(database.isOpen()).append("\n");
                sb.append("isReadOnly = ").append(database.isReadOnly()).append("\n");

                if (OsVersionHelper.have(16)) {
                    sb.append("isWriteAheadLoggingEnabled = ").append(database.isWriteAheadLoggingEnabled()).append("\n");
                }

                sb.append("inTransaction =").append(database.inTransaction()).append("\n");
                sb.append("isDbLockedByCurrentThread = ").append(database.isDbLockedByCurrentThread()).append("\n");

                if (OsVersionHelper.have(11)) {
                    sb.append("isDatabaseIntegrityOk = ").append(database.isDatabaseIntegrityOk()).append("\n");
                }

                Cursor dbMetadataCursor = null;
                try {
                    dbMetadataCursor = database.rawQuery("SELECT * FROM sqlite_master", null);
                    int columnIndex = dbMetadataCursor.getColumnIndex("name");

                    sb.append("\n\nDB metadata:\n\n");
                    dumpCursorMetadata("    ", sb, dbMetadataCursor);

                    sb.append("\n\nTables:\n\n");

                    dbMetadataCursor.moveToPosition(-1);
                    while (dbMetadataCursor.moveToNext()) {
                        String table = dbMetadataCursor.getString(columnIndex);
                        sb.append("\n\ntable name = ").append(table).append("\n");
                        sb.append("columns:\n");
                        Cursor curTableCursor = null;
                        try {
                            curTableCursor = database.rawQuery("SELECT * FROM " + table, null);
                            dumpCursorMetadata("    ", sb, curTableCursor);
                        } catch (Exception e) {
                            Tracer.e(e);
                            sb.append("Failed dump table metadata").append("\n");
                        } finally {
                            try {
                                curTableCursor.close();
                            } catch (Exception e) {

                            }
                        }
                    }

                } catch (Exception e) {
                    Tracer.e(e);
                    sb.append("Failed dump DB metadata").append("\n");
                } finally {
                    try {
                        dbMetadataCursor.close();
                    } catch (Exception e) {

                    }
                }
                return sb.toString();
            }

            private static void dumpCursorMetadata(String linesPrefix, StringBuilder sb, Cursor cursor) {
                String[] columns = cursor.getColumnNames();

                for (int i = 0; i < columns.length; i++) {
                    String column = columns[i];
                    sb.append(linesPrefix).append(column).append("\n");
                }
            }

            public static String dumpCursor(Context context, Uri contentUri) {
                return dumpCursor(context, contentUri, null, null, null, null);
            }

            public static String dumpCursor(Context context, Uri contentUri, String[] columns, String selection, String[] selectionArgs, String orderBy) {
                final StringBuilder sb = new StringBuilder();
                sb.append(Sql.query(contentUri, columns, selection, selectionArgs, orderBy)).append("\n");

                sb.append("table name = ").append(contentUri).append("\n");
                sb.append("columns:\n");

                Cursor cursor = null;
                try {
                    cursor = context.getContentResolver().query(contentUri, columns, selection, selectionArgs, orderBy);
                    dumpCursorMetadata("    ", sb, cursor);
                    sb.append("data:\n");
                    sb.append(Dumper.dump(cursor));
                } catch (Exception e) {
                    Tracer.e(e);
                    sb.append("Failed dump cursor").append("\n");
                } finally {
                    try {
                        cursor.close();
                    } catch (Exception e) {

                    }
                }

                return sb.toString();
            }
        }

        public static class Sql {

            public static String query(Uri uri, String[] columns, String selection,
                                       String[] selectionArgs, String orderBy) {
                String table = uri.toString();
                return query(table, columns, selection, selectionArgs, null, null, orderBy, null, null);
            }

            public static String query(String table, String[] columns,
                                       String selection, String[] selectionArgs, String groupBy,
                                       String having, String orderBy) {
                return query(table, columns, selection, selectionArgs, groupBy, having, orderBy, null, null);
            }

            public static String query(String table, String[] columns,
                                       String selection, String[] selectionArgs, String groupBy,
                                       String having, String orderBy, String limit) {
                return query(table, columns, selection, selectionArgs, groupBy, having, orderBy, limit, null);
            }

            public static String query(String table, String[] columns,
                                       String selection, String[] selectionArgs, String groupBy,
                                       String having, String orderBy, String limit,
                                       CancellationSignal cancellationSignal) {

                String result = null;
                try {
                    final String sql = SQLiteQueryBuilder.buildQueryString(false,
                            table, columns, selection, groupBy, having, orderBy, limit);
                    result = "query : " + sql + "\nargs : " + Arrays.toString(selectionArgs);
                } catch (Exception e) {
                    result = Dumper.dump(e);
                }

                return result;
            }

            public static String delete(Uri uri, String selection, String[] selectionArgs) {
                String table = uri.toString();
                return delete(table, selection, selectionArgs);
            }

            public static String delete(String table, String whereClause, String[] whereArgs) {
                final String sql = "DELETE FROM "
                        + table
                        + (!android.text.TextUtils.isEmpty(whereClause) ? " WHERE " + whereClause
                        : "");
                return "query : " + sql + "\nargs:" + Arrays.toString(whereArgs);
            }

            public static String update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
                String table = uri.toString();
                return update(table, values, selection, selectionArgs);
            }

            public static String update(String table, ContentValues values, String whereClause, String[] whereArgs) {
                StringBuilder sql = new StringBuilder(120);
                sql.append("UPDATE ");
                sql.append(table);
                sql.append(" SET ");
                int setValuesSize = values.size();
                int bindArgsSize = (whereArgs == null) ? setValuesSize
                        : (setValuesSize + whereArgs.length);
                Object[] bindArgs = new Object[bindArgsSize];
                int i = 0;

                for (Map.Entry<String, Object> entry : values.valueSet()) {
                    sql.append((i > 0) ? "," : "");
                    sql.append(entry.getKey());
                    bindArgs[i++] = entry.getValue();
                    sql.append("=?");
                }

                if (whereArgs != null) {
                    for (i = setValuesSize; i < bindArgsSize; i++) {
                        bindArgs[i] = whereArgs[i - setValuesSize];
                    }
                }

                if (!android.text.TextUtils.isEmpty(whereClause)) {
                    sql.append(" WHERE ");
                    sql.append(whereClause);
                }

                return "query : " + sql + "\nargs : " + Arrays.toString(bindArgs);
            }

            public static String insert(Uri uri, ContentValues values) {
                String table = uri.toString();
                return insert(table, null, values);
            }

            public static String insert(String table, String nullColumnHack, ContentValues initialValues) {
                StringBuilder sql = new StringBuilder();
                sql.append("INSERT");
                sql.append(" INTO ");
                sql.append(table);
                sql.append('(');

                Object[] bindArgs = null;
                int size = (initialValues != null && initialValues.size() > 0) ? initialValues
                        .size() : 0;
                if (size > 0) {
                    bindArgs = new Object[size];
                    int i = 0;
                    for (Map.Entry<String, Object> entry : initialValues.valueSet()) {
                        sql.append((i > 0) ? "," : "");
                        sql.append(entry.getKey());
                        bindArgs[i++] = entry.getValue();
                    }
                    sql.append(')');
                    sql.append(" VALUES (");
                    for (i = 0; i < size; i++) {
                        sql.append((i > 0) ? ",?" : "?");
                    }
                } else {
                    sql.append(nullColumnHack + ") VALUES (NULL");
                }
                sql.append(')');

                return "query : " + sql + "\nargs : " + Arrays.toString(bindArgs);
            }

            public static String execSQL(String sql, Object[] bindArgs) {
                return "query : " + sql + "\nargs : " + Arrays.toString(bindArgs);
            }

            public static String rawQuery(String sql, Object[] bindArgs) {
                return "query : " + sql + "\nargs : " + Arrays.toString(bindArgs);
            }
        }

        public static class ConvertCollections {

            public static HashMap<String, Object> toHashMap(Bundle bundle) {
                if (bundle == null) {
                    return null;
                }

                Set<String> keys = bundle.keySet();
                HashMap<String, Object> hm = new HashMap<String, Object>();

                for (String key : keys) {
                    hm.put(key, bundle.get(key));
                }
                return hm;
            }
        }

        public static class Dir {
            public static final char GO_TO_NEXT_LINE = '\n';
            public static final String F_PREFIX = "[F] : ";
            public static final String D_PREFIX = "[D] : ";
            private static final String FILE_LISTING = " file listing...";
            private static final String EITHER_DIRECTORY_DOES_NOT_EXIST_OR_IS_NOT_A_DIRECTORY = "Either directory does not exist or is not a directory";
            private static final String ERROR_WITH = "Error with ";
            private static final String BYTES = " bytes";
            private static final String SPACE = " ";
            private static final String LINE_SEPARATOR = "----------------------";
            private static final char D_CHAR = 'D';
            private static final StringBuilder sb = new StringBuilder();

            private static void doFileFilterListing(String dirName, String ff) {
                if (dirName == null) {
                    return;
                }
                println(LINE_SEPARATOR);
                print(dirName);
                println(FILE_LISTING);
                println(LINE_SEPARATOR);

                final String fileFilter = ff;

                File dir = new File(dirName);
                FileFilter filter = null;

                if (fileFilter != null) {
                    filter = new FileFilter() {
                        public boolean accept(File file) {
                            String name = file.getName();
                            return name.startsWith(fileFilter);
                        }
                    };
                }

                String[] children = FileHelper.list(dir, filter);
                printFiles(children, dirName);
                File[] listFiles = dir.listFiles();
                if (listFiles != null) {
                    for (File f : listFiles) {
                        if (f != null && f.exists() && f.isDirectory()) {
                            doFileFilterListing(f.getAbsolutePath(), ff);
                        }
                    }
                }
            }

            private static void printFiles(String[] children, String dirName) {

                if (children == null) {
                    print(ERROR_WITH);
                    println(dirName);
                    println(EITHER_DIRECTORY_DOES_NOT_EXIST_OR_IS_NOT_A_DIRECTORY);
                } else {
                    for (int i = 0; i < children.length; i++) {
                        String filename = children[i];
                        if (filename == null) {
                            continue;
                        }
                        File file = new File(dirName, filename);
                        if (file.isDirectory()) {
                            print(D_PREFIX);
                            print(dirName);
                            print(File.separator);
                            println(filename);
                        } else {
                            print(F_PREFIX);
                            print(dirName);
                            print(File.separator);
                            print(filename);
                            print(SPACE);
                            print(file.length());
                            println(BYTES);
                        }
                    }
                }
            }

            private static void println(String string) {
                sb.append(string).append(GO_TO_NEXT_LINE);
            }

            private static void print(String string) {
                sb.append(string);
            }

            private static void print(long l) {
                sb.append(l);
            }

            public static String listFiles(File dir) {
                String result = "";
                if (dir != null) {
                    sb.setLength(0);
                    doFileFilterListing(dir.getAbsolutePath(), null);
                    result = sb.toString();
                    sb.setLength(0);
                }
                return result;
            }

            public static String listFiles(String dirName) {
                String result = "";
                if (dirName != null) {
                    sb.setLength(0);
                    doFileFilterListing(dirName, null);
                    result = sb.toString();
                    sb.setLength(0);
                }
                return result;
            }

            public static String listFiles(String[] dirNames) {
                String result = "";
                if (dirNames != null) {
                    sb.setLength(0);
                    for (int i = 0; i < dirNames.length; i++) {
                        sb.append(D_CHAR).append(i).append(GO_TO_NEXT_LINE);
                        doFileFilterListing(dirNames[i], null);
                    }
                    result = sb.toString();
                    sb.setLength(0);
                }
                return result;
            }
        }

        public static String toString(Object obj, int maxLength) {
            return TextHelper.ellipsize(String.valueOf(obj), maxLength);
        }

        public static String dumpFields(Object obj) {
            return ObjectDumper.dumpFields(obj);
        }

        public static String dumpProperties() {
            return Dumper.dump(System.getProperties());
        }

        public static String dumpSize(Collection collection) {
            return collection == null ? "null" : "" + collection.size();
        }

        public static String dumpChars(String s) {
            if (s == null) {
                return "null";
            }
            final char[] chars = s.toCharArray();
            StringBuilder sb = new StringBuilder();
            sb.append("string chars:\n");
            sb.append("string = " + s).append("\n");
            sb.append("count = " + s.length()).append("\n");
            sb.append("string chars:\n");
            sb.append("chars = " + Dumper.dump(chars)).append("\n");

            ArrayList<String> list = new ArrayList<String>();
            for (char curChar : chars) {
                list.add(String.format("%04x", (int) curChar));
            }
            sb.append("chars(hex) = " + list).append("\n");

            list.clear();
            for (char curChar : chars) {
                list.add(String.valueOf((int) curChar));
            }
            sb.append("chars(decimal) = " + list).append("\n");
            return sb.toString();
        }

        public static String dumpViewState(View... views) {
            StringBuilder sb = new StringBuilder();
            sb.append("view info: ");
            for (View view : views) {
                if (view == null) {
                    sb.append("null\n\n");
                } else {
                    sb.append("\n").append(view.getClass().getName())
                            .append(" @+id/").append(getViewName(view))
                            .append(" ").append(getViewVisibility(view)).append("\n")
                            .append("alpha = ").append(view.getAlpha()).append("\n")
                            .append("size = ").append("(" + view.getWidth() + ";" + view.getHeight() + ")").append("\n")
                            .append("location = ").append(getViewRect(view)).append("\n");

                    if (view instanceof TextView) {
                        final TextView textView = (TextView) view;
                        final String text = textView.getText().toString();
                        final float px = textView.getTextSize();
                        final int dp = ViewHelper.pxToDp(textView.getContext(), px);
                        sb
                                .append("text = ").append(ellipsize(text, 10)).append("\n")
                                .append("text size(dp) = ").append(dp).append("\n")
                                .append("text size(px) = ").append(px).append("\n");
                    }
                }
            }
            return sb.toString().trim();
        }

        public static String getResName(Context ctx, int resId) {
            try {
                return ctx.getResources().getResourceEntryName(resId); // return ctx.getResources().getResourceName(resId);
            } catch (Resources.NotFoundException e) {
                return "Resource not found";
            }
        }

        public static Rect getViewRect(View view) {
            int[] l = new int[2];
            view.getLocationOnScreen(l);
            return new Rect(l[0], l[1], l[0] + view.getWidth(), l[1] + view.getHeight());
        }

        public static String getViewName(View view) {
            int viewId = view.getId();

            if (viewId == View.NO_ID) {
                return "No id";
            } else {
                return getResName(view.getContext(), viewId);
            }
        }

        public static String getViewVisibility(View view) {
            String visibilityStr;

            int visibility = view.getVisibility();
            switch (visibility) {
                case View.VISIBLE:
                    visibilityStr = "VISIBLE";
                    break;
                case View.INVISIBLE:
                    visibilityStr = "INVISIBLE";
                    break;
                case View.GONE:
                    visibilityStr = "GONE";
                    break;
                default:
                    visibilityStr = "unknown(" + visibility + ")";
                    break;
            }
            return visibilityStr;
        }

        public static String dumpActivity(Activity activity) {
            final StringBuilder sb = new StringBuilder();
            sb.append("\nactivity: ").append(activity.getClass());
            if (activity instanceof FragmentActivity) {
                sb.append("\nfragments: ").append(Dumper.dump(ViewHelper.getAllFragments((FragmentActivity) activity)));
            }
            return sb.toString();
        }

        public static String dumpViewHierarchy(Activity activity) {
            return dumpActivity(activity) + dumpViewHierarchy(activity.findViewById(android.R.id.content).getRootView());
        }

        public static String dumpViewHierarchy(View view) {
            StringBuilder sb = new StringBuilder();
            dumpViewHierarchy(sb, view, "", -1);
            return sb.toString();
        }

        private static void dumpViewHierarchy(StringBuilder sb, View view, String indent, int position) {
            sb.append('\n');
            int startPosition = sb.length();
            sb.append(indent).append("* ");
            indent = sb.substring(startPosition);

            if (position >= 0) {
                sb.append(position).append(':');
            }

            if (view != null) {
                Resources resources = view.getContext().getResources();
                sb.append(view.getClass().getName());
                int id = view.getId();
                if (id != View.NO_ID) {
                    if (resources != null) {
                        sb.append(" @+id/").append(resources.getResourceEntryName(id));
                    }
                } else {
                    sb.append(" NO_ID");
                }
            } else {
                sb.append("null");
            }

            if (view instanceof TextView) {
                sb.append(" ").append(ellipsize(((TextView) view).getText().toString(), 10));
            }

            if (true) {
                sb.append(" ").append(getViewVisibility(view)).append(" ")
                        .append("alpha = ").append(view.getAlpha()).append(" ")
                        .append("size = ").append("(" + view.getWidth() + ";" + view.getHeight() + ")").append(" ")
                        .append("location = ").append(getViewRect(view));
            }

            if (view instanceof ViewGroup) {
                ViewGroup group = (ViewGroup) view;
                int childCount = group.getChildCount();
                sb.append('\n').append(indent).append("\\/==subviews(").append(childCount).append(")==\\/")
                        .append("\n").append(indent).append("{");
                for (int i = 0; i < childCount; i++) {
                    dumpViewHierarchy(sb, group.getChildAt(i), indent, i);
                }
                sb.append('\n').append(indent).append("}").append('\n').append(indent)
                        .append("/\\==subviews(").append(childCount).append(")==/\\");
            }
        }

        private static String ellipsize(String s, int maxLength) {
            return s != null && s.length() > maxLength ? s.substring(0, maxLength) + "..." : s;
        }

        public static String dumpThreads() {
            return dumpThreads(true);
        }

        public static String dumpThreads(boolean full) {
            final Map<Thread, StackTraceElement[]> allStackTraces = Thread.getAllStackTraces();
            final Set<Thread> threadSet = allStackTraces.keySet();
            Set<Map.Entry<Thread, StackTraceElement[]>> set = allStackTraces.entrySet();
            StringBuilder sb = new StringBuilder();
            sb.append("Threads state. count: " + threadSet.size()).append('\n');
            for (Map.Entry<Thread, StackTraceElement[]> entry : set) {
                final Thread thread = entry.getKey();
                sb.append(Dumper.dump(thread));

                if (full) {
                    StackTraceElement[] stackTrace = entry.getValue();
                    if (stackTrace == null) {
                        sb.append("\nin\n(unknown)");
                    } else {
                        sb.append("\nin\n");
                        for (StackTraceElement stackTraceElement : stackTrace) {
                            sb.append(stackTraceElement.toString()).append('\n');
                        }
                        sb.append('\n').append('=');
                    }
                    sb.append('\n').append('\n');
                }
            }

            return sb.toString();
        }

        public static String dumpThread(boolean full) {
            return dumpThread(Thread.currentThread(), full);
        }

        public static String dumpThread(Thread thread, boolean full) {
            StringBuilder sb = new StringBuilder();
            sb.append(Dumper.dump(thread));

            if (full) {
                StackTraceElement[] stackTrace = thread.getStackTrace();
                if (stackTrace == null) {
                    sb.append("\nin\n(unknown)");
                } else {
                    sb.append("\nin\n");
                    for (StackTraceElement stackTraceElement : stackTrace) {
                        sb.append(stackTraceElement.toString()).append('\n');
                    }
                    sb.append('\n').append('=');
                }
                sb.append('\n').append('\n');
            }

            return sb.toString();
        }

        public static String dumpTrimMemory(int level) {
            switch (level) {
                case ComponentCallbacks2.TRIM_MEMORY_BACKGROUND:
                    return "TRIM_MEMORY_BACKGROUND";
                case ComponentCallbacks2.TRIM_MEMORY_COMPLETE:
                    return "TRIM_MEMORY_COMPLETE";
                case ComponentCallbacks2.TRIM_MEMORY_MODERATE:
                    return "TRIM_MEMORY_MODERATE";
                case ComponentCallbacks2.TRIM_MEMORY_RUNNING_CRITICAL:
                    return "TRIM_MEMORY_RUNNING_CRITICAL";
                case ComponentCallbacks2.TRIM_MEMORY_RUNNING_LOW:
                    return "TRIM_MEMORY_RUNNING_LOW";
                case ComponentCallbacks2.TRIM_MEMORY_RUNNING_MODERATE:
                    return "TRIM_MEMORY_RUNNING_MODERATE";
                case ComponentCallbacks2.TRIM_MEMORY_UI_HIDDEN:
                    return "TRIM_MEMORY_UI_HIDDEN";
                default:
                    return "unknown";
            }
        }
    }

    private static class FileHelper {

        public static String[] list(File dir, FileFilter filter) {
            ArrayList<String> list = new ArrayList<String>();
            File[] fileArr = dir.listFiles(filter);
            if (fileArr != null) {
                for (File f : fileArr) {
                    list.add(f.getName());
                }
            }
            return list.toArray(new String[list.size()]);
        }
    }

    public static class DateTimeHelper {
        public static final long SECOND = 1000;
        public static final long MINUTE = SECOND * 60;
        public static final long HOUR = MINUTE * 60;
        public static final long DAY = HOUR * 24;
        public static final long DAYS_30 = DAY * 30;

        public static final long LAST_MS_IN_SEC = 999;

        public static String getMsDuration(double time) {
            String prefix = "";
            if (time < 0) {
                prefix = "-";
                time = -time;
            }

            int percents = (int) ((time * 100) % 100);

            return prefix + (long) time + "." + (percents < 10 ? "0" + percents : percents);
        }

        public static String getSecondDuration(long time) {
            String prefix = "";
            if (time < 0) {
                prefix = "-";
                time = -time;
            }

            long ms = time % 1000;
            long sec = time / 1000;

            String seconds = "" + (sec < 10 ? "0" + sec : sec);

            return prefix + seconds + "." + (ms < 10 ? "00" + ms : (ms < 100 ? "0" + ms : ms));
        }

        public static String getDuration(long time) {
            return getDuration(time, true);
        }

        public static String getDuration(long time, boolean showMs) {
            String prefix = "";
            if (time < 0) {
                prefix = "-";
                time = -time;
            }

            long ms = time % 1000;
            long sec = time / 1000;
            long hours = sec / 3600;
            sec = sec % 3600;
            long min = sec / 60;
            sec = sec % 60;

            String withoutMs = ""
                    + (hours < 10 ? "0" + hours : hours) + ":"
                    + (min < 10 ? "0" + min : min) + ":"
                    + (sec < 10 ? "0" + sec : sec);

            if (showMs) {
                return prefix + withoutMs + "." + (ms < 10 ? "00" + ms : (ms < 100 ? "0" + ms : ms));
            } else {
                return prefix + withoutMs;
            }
        }

        public static String getDurationMinutes(long time) {
            return getDurationMinutes(time, true);
        }

        public static String getDurationMinutes(long time, boolean showMs) {
            long ms = time % 1000;
            long sec = time / 1000;
            long min = sec / 60;
            sec = sec % 60;

            String withoutMs = ""
                    + (min < 10 ? "0" + min : min) + ":"
                    + (sec < 10 ? "0" + sec : sec);

            if (showMs) {
                return withoutMs + ":" + (ms < 10 ? "00" + ms : (ms < 100 ? "0" + ms : ms));
            } else {
                return withoutMs;
            }
        }

        public static String getDate() {
            return getDate(System.currentTimeMillis());
        }

        public static String getDate(long time) {
            return new SimpleDateFormat("yyyy MM dd HH:mm:ss:SSS", Locale.getDefault()).format(time);
        }

        public static String getFileDate() {
            return getFileDate(System.currentTimeMillis());
        }

        private static String getFileDate(long time) {
            return new SimpleDateFormat("yyyy_MM_dd_HH_mm_ss_SSS", Locale.getDefault()).format(time);
        }

        public static String getTime() {
            return getTime(System.currentTimeMillis(), true);
        }

        public static String getTime(long time) {
            return getTime(time, true);
        }

        public static String getTime(boolean showMs) {
            return getTime(System.currentTimeMillis(), showMs);
        }

        private static String getTime(long time, boolean showMs) {
            if (showMs) {
                return new SimpleDateFormat("HH:mm:ss:SSS", Locale.getDefault()).format(time);
            } else {
                return new SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(time);
            }
        }

        public static String getDateTimeInPreferredFormat(Context context, long date) {
            StringBuilder sb = new StringBuilder()
                    .append(getDateInPreferredFormat(context, date)).append(' ')
                    .append(getTimeInPreferredFormat(context, date));
            return sb.toString();
        }

        public static String getDateInPreferredFormat(Context context, long date) {
            return android.text.format.DateFormat.getDateFormat(context).format(date);
        }

        public static String getTimeInPreferredFormat(Context context, long date) {
            return android.text.format.DateFormat.getTimeFormat(context).format(date);
        }

        public static long getYesterdayTime() {
            Calendar date = getMidnight();
            date.add(Calendar.DAY_OF_MONTH, -1);
            return date.getTimeInMillis();
        }

        public static long getNewYearTime() {
            Calendar date = getMidnight();
            // set 1 January
            date.set(Calendar.MONTH, Calendar.JANUARY);
            date.set(Calendar.DAY_OF_MONTH, 1);
            return date.getTimeInMillis();
        }

        public static long getMidnightTime() {
            return getMidnight().getTimeInMillis();
        }

        private static Calendar getMidnight() {
            // today
            Calendar date = new GregorianCalendar();
            // reset hour, minutes, seconds, milliseconds
            date.set(Calendar.HOUR_OF_DAY, 0);
            date.set(Calendar.MINUTE, 0);
            date.set(Calendar.SECOND, 0);
            date.set(Calendar.MILLISECOND, 0);
            return date;
        }

        // TODO check performance
        public static String getDateNoYearFromPreferredFormat(Context context, long date) {
            String dateStr = getDateInPreferredFormat(context, date);
            try {
                char[] order = android.text.format.DateFormat.getDateFormatOrder(context);
                if (order[0] == 'Y' || order[0] == 'y') {
                    // without first 4 chars
                    dateStr = dateStr.substring(4);
                    while (true) {
                        char firstChar = dateStr.charAt(0);
                        if (firstChar == ' ' || firstChar == '/'
                                || firstChar == '\\' || firstChar == '.'
                                || firstChar == ',') {
                            dateStr = dateStr.substring(1);
                        } else {
                            break;
                        }
                    }
                } else {
                    // without last 4 chars
                    dateStr = dateStr.substring(0, dateStr.length() - 5);
                    while (true) {
                        char lastChar = dateStr.charAt(dateStr.length() - 1);
                        if (lastChar == ' ' || lastChar == '/' || lastChar == '\\'
                                || lastChar == '.' || lastChar == ',') {
                            dateStr = dateStr.substring(0, dateStr.length() - 2);
                        } else {
                            break;
                        }
                    }
                }
            } catch (Exception e) {
                Tracer.e(e);
            }
            return dateStr == null ? "" : dateStr;
        }

        public static String getAppFormattedDateTime(long time, Context context, int resId) {
            String date = "";
            if (time > DateTimeHelper.getMidnightTime()) {
                date = DateTimeHelper.getTimeInPreferredFormat(context, time);
            } else if (time > DateTimeHelper.getYesterdayTime()) {
                String timeString = DateTimeHelper.getTimeInPreferredFormat(context, time);
                date = context.getString(resId, timeString);
            } else if (time > DateTimeHelper.getNewYearTime()) {
                date = DateTimeHelper.getDateNoYearFromPreferredFormat(context, time) + " "
                        + DateTimeHelper.getTimeInPreferredFormat(context, time);
            } else {
                date = DateTimeHelper.getDateTimeInPreferredFormat(context, time);
            }
            return date;
        }

        public static String getDateTimeInPreferredFormat(Context context) {
            long currentTimeMillis = System.currentTimeMillis();
            return getDateTimeInPreferredFormat(context, currentTimeMillis);
        }
    }

    private static class ObjectDumper {

        public static String dumpFields(Object object) {
            StringBuilder sb = new StringBuilder();
            dumpFields(sb, object, new ArrayList());
            return sb.toString();
        }

        public static void dumpFields(StringBuilder sb, Object object, ArrayList dumpedObjects) {
            if (object == null) {
                sb.append("null");
                return;
            }

            for (Object dumpedItem : dumpedObjects) {
                if (dumpedItem == object) {
                    sb.append("duplicate " + object.getClass().getName()
                            + "@" + Integer.toHexString(object.hashCode()));
                    return;
                }
            }

            if (object == dumpedObjects) {
                sb.append("duplicate " + object.getClass().getName()
                        + "@" + Integer.toHexString(object.hashCode()));
                return;
            }

            dumpedObjects.add(object);

            Class<?> oClass = object != null ? object.getClass() : null;
            if (object == null) {
                sb.append("null");
            } else if (oClass.isArray()) {
                sb.append("Array: ");
                sb.append("[");
                for (int i = 0; i < Array.getLength(object); i++) {
                    Object value = Array.get(object, i);
                    if (value != null) {
                        if (value.getClass().isPrimitive()
                                || value.getClass() == Long.class
                                || value.getClass() == Integer.class
                                || value.getClass() == Boolean.class
                                || value.getClass() == Double.class
                                || value.getClass() == Float.class
                                || value.getClass() == Short.class
                                || value.getClass() == Character.class
                                || value.getClass() == Byte.class) {
                            sb.append(value);
                            if (i != (Array.getLength(object) - 1)) {
                                sb.append(",");
                            }
                        } else if (value.getClass() == String.class) {
                            String str = (String) value;
                            sb.append(str != null && str.length() > 1000 ? str.substring(0, 1000) : str);
                            if (i != (Array.getLength(object) - 1)) {
                                sb.append(",");
                            }
                        } else {
                            dumpFields(sb, value, dumpedObjects);
                        }
                    } else {
                        sb.append("null");
                    }
                }
                sb.append("]\n");
            } else {
                sb.append("Class: " + oClass.getName());
                sb.append("{\n");
                while (oClass != null) {
                    Field[] fields = oClass.getDeclaredFields();
                    for (int i = 0; i < fields.length; i++) {
                        fields[i].setAccessible(true);
                        sb.append(fields[i].getName());
                        sb.append("=");
                        try {
                            Object value = fields[i].get(object);
                            if (value != null) {
                                if (value.getClass().isPrimitive()
                                        || value.getClass() == Long.class
                                        || value.getClass() == Integer.class
                                        || value.getClass() == Boolean.class
                                        || value.getClass() == Double.class
                                        || value.getClass() == Float.class
                                        || value.getClass() == Short.class
                                        || value.getClass() == Character.class
                                        || value.getClass() == Byte.class) {
                                    sb.append(value);
                                } else if (value.getClass() == String.class) {
                                    String str = (String) value;
                                    sb.append(str != null && str.length() > 1000 ? str.substring(0, 1000) : str);
                                } else {
                                    sb.append(Dumper.dump(value, dumpedObjects));
                                }
                            } else {
                                sb.append("null");
                            }
                        } catch (IllegalAccessException e) {
                            sb.append(e.getMessage());
                        }
                        sb.append("\n");
                    }
                    oClass = oClass.getSuperclass();
                }
                sb.append("}\n");
            }

            dumpedObjects.remove(object);
        }

        public static String dumpMethods(Object object) {
            StringBuilder sb = new StringBuilder();
            dumpMethods(sb, object);
            return sb.toString();
        }

        public static String dumpMethods(Class<?> clazz) {
            StringBuilder sb = new StringBuilder();
            dumpMethods(sb, clazz);
            return sb.toString();
        }

        public static void dumpMethods(StringBuilder sb, Object object) {
            Class<?> clazz = object != null ? object.getClass() : null;
            dumpMethods(sb, clazz);
        }

        public static void dumpMethods(StringBuilder sb, Class<?> clazz) {
            if (clazz == null) {
                sb.append("null");
            } else {
                for (Class<?> c = clazz; c != null; c = c.getSuperclass()) {
                    sb.append('\n').append('\n').append(clazz.getName()).append(" methods:").append('\n');
                    for (Method method : c.getDeclaredMethods()) {
                        sb.append(method);
                        sb.append('\n');
                    }
                }
                sb.append('\n');
            }
        }
    }

    public static class ReflectionHelper {

        private static final AtomicBoolean sPrintLog = new AtomicBoolean(false);

        public static void setPrintLog(boolean print) {
            sPrintLog.set(print);
        }

        public static <T> T getInstance(String className) {
            T result = null;
            try {
                Class<?> c = Class.forName(className);
                result = (T) c.newInstance();
            } catch (ClassNotFoundException e) {
                Tracer.w("Filed instantiate " + className, e);
            } catch (InstantiationException e) {
                Tracer.w("Filed instantiate " + className, e);
            } catch (IllegalAccessException e) {
                Tracer.w("Filed instantiate " + className, e);
            }

            return result;
        }

        public static <T> T getInstanceUnsafe(String className) throws Exception {
            Class<?> c = Class.forName(className);
            T result = (T) c.newInstance();
            return result;
        }

        public static Object callMethod(Object object, String methodName, Object... args) {
            return callMethod(object.getClass(), object, methodName, args);
        }

        /**
         * For static calls
         */
        public static Object callMethod(Class<?> c, String methodName, Object... args) {
            return callMethod(c, null, methodName, args);
        }

        public static Object callMethod(Class<?> c, Object obj, String methodName, Object[] args) {
            Object result = null;

            Class<?>[] argTypes = new Class[args.length];

            for (int i = 0; i < args.length; i++) {
                argTypes[i] = args[i].getClass();
            }

            result = callMethodTyped(c, obj, methodName, argTypes, args);

            return result;
        }

        public static Object callMethodVarArgs(Class<?> c, Object obj, String methodName, Object[] args, Object varArgs) {
            Object result = null;

            Class<?>[] argTypes = new Class[args.length + 1];
            Object[] allArgs = new Object[args.length + 1];

            for (int i = 0; i < args.length; i++) {
                argTypes[i] = args[i].getClass();
                allArgs[i] = args[i];
            }

            argTypes[args.length] = varArgs.getClass();
            allArgs[args.length] = varArgs;

            result = callMethodTyped(c, obj, methodName, argTypes, allArgs);

            return result;
        }

        public static Object callMethodTyped(Class<?> c, Object obj, String methodName, Class<?>[] argTypes, Object[] args) {
            if (obj != null && obj.getClass() != c) {
                throw new IllegalStateException();
            }

            Object result = null;
            try {
                if (sPrintLog.get()) {
                    Tracer.print(getMethodStr(methodName, argTypes));
                }
                Method method = c.getDeclaredMethod(methodName, argTypes);
                method.setAccessible(true);
                result = method.invoke(obj, args);
            } catch (InvocationTargetException e) {
                Tracer.e(e);
            } catch (NoSuchMethodException e) {
                Tracer.e(e);
            } catch (IllegalAccessException e) {
                Tracer.e(e);
            }

            return result;
        }

        public static String getMethodStr(String methodName, Class<?>[] argTypes) {
            return String.format("%s(%s)", methodName, getTypesStr(argTypes));
        }

        public static String getTypesStr(Class<?>[] types) {
            if (types.length == 0) {
                return "";
            }
            StringBuilder result = new StringBuilder();
            appendTypeName(result, types[0]);
            for (int i = 1; i < types.length; i++) {
                result.append(',');
                appendTypeName(result, types[i]);
            }
            return result.toString();
        }

        public static void appendTypeName(StringBuilder out, Class<?> c) {
            int dimensions = 0;
            while (c.isArray()) {
                c = c.getComponentType();
                dimensions++;
            }
            out.append(c.getName());
            for (int d = 0; d < dimensions; d++) {
                out.append("[]");
            }
        }

        public static <T> T getField(Object object, String fieldName, Class<T> tClass) {
            return (T) getField(object, fieldName);
        }

        public static Object getField(Object object, String fieldName) {
            try {
                Class<?> c = object.getClass();
                Field field = c.getDeclaredField(fieldName);
                field.setAccessible(true);
                return field.get(object);
            } catch (IllegalAccessException e) {
                Tracer.e(e);
            } catch (NoSuchFieldException e) {
                Tracer.e(e);
            }
            return null;
        }

        public static <T> T getField(Class<?> с, String fieldName, Class<T> tClass) {
            return (T) getField(с, fieldName);
        }

        public static Object getField(Class<?> c, String fieldName) {
            try {
                Field field = c.getDeclaredField(fieldName);
                field.setAccessible(true);
                return field.get(null);
            } catch (IllegalAccessException e) {
                Tracer.e(e);
            } catch (NoSuchFieldException e) {
                Tracer.e(e);
            }
            return null;
        }

        public static <T> T proxy(T originObject) {
//        verifyCanProxyClass(originObject);
            Class<?>[] interfaces = new Class<?>[]{Serializable.class};
            @SuppressWarnings("unchecked")
            T proxy = (T) Proxy.newProxyInstance(originObject.getClass().getClassLoader(), interfaces, new DefaultHandler(originObject));
            return proxy;
        }

        private static class DefaultHandler implements InvocationHandler {

            private final Object mOriginObject;

            public <T> DefaultHandler(T originObject) {
                mOriginObject = originObject;
            }

            @Override
            public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                Tracer.print("proxy = " + proxy + " method = " + method + " args = " + Dumper.dump(args));
                return method.invoke(mOriginObject, args);
            }
        }

        /**
         * Get the underlying class for a type, or null if the type is a variable type.
         *
         * @param type the type
         * @return the underlying class
         */
        public static Class<?> getClass(Type type) {
            if (type instanceof Class) {
                return (Class) type;
            } else if (type instanceof ParameterizedType) {
                return getClass(((ParameterizedType) type).getRawType());
            } else if (type instanceof GenericArrayType) {
                Type componentType = ((GenericArrayType) type).getGenericComponentType();
                Class<?> componentClass = getClass(componentType);
                if (componentClass != null) {
                    return Array.newInstance(componentClass, 0).getClass();
                } else {
                    return null;
                }
            } else {
                return null;
            }
        }

        /**
         * Get the actual type arguments a child class has used to extend a generic base class.
         *
         * @param baseClass  the base class
         * @param childClass the child class
         * @return a list of the raw classes for the actual type arguments.
         */
        public static <T> List<Class<?>> getTypeArguments(
                Class<T> baseClass, Class<? extends T> childClass) {
            Map<Type, Type> resolvedTypes = new HashMap<Type, Type>();
            Type type = childClass;
            // start walking up the inheritance hierarchy until we hit baseClass
            while (!getClass(type).equals(baseClass)) {
                if (type instanceof Class) {
                    // there is no useful information for us in raw types, so just keep going.
                    type = ((Class) type).getGenericSuperclass();
                } else {
                    ParameterizedType parameterizedType = (ParameterizedType) type;
                    Class<?> rawType = (Class) parameterizedType.getRawType();

                    Type[] actualTypeArguments = parameterizedType.getActualTypeArguments();
                    TypeVariable<?>[] typeParameters = rawType.getTypeParameters();
                    for (int i = 0; i < actualTypeArguments.length; i++) {
                        resolvedTypes.put(typeParameters[i], actualTypeArguments[i]);
                    }

                    if (!rawType.equals(baseClass)) {
                        type = rawType.getGenericSuperclass();
                    }
                }
            }

            // finally, for each actual type argument provided to baseClass, determine (if possible)
            // the raw class for that type argument.
            Type[] actualTypeArguments;
            if (type instanceof Class) {
                actualTypeArguments = ((Class) type).getTypeParameters();
            } else {
                actualTypeArguments = ((ParameterizedType) type).getActualTypeArguments();
            }
            List<Class<?>> typeArgumentsAsClasses = new ArrayList<Class<?>>();
            // resolve types by chasing down type variables.
            for (Type baseType : actualTypeArguments) {
                while (resolvedTypes.containsKey(baseType)) {
                    baseType = resolvedTypes.get(baseType);
                }
                typeArgumentsAsClasses.add(getClass(baseType));
            }
            return typeArgumentsAsClasses;
        }

        public static Class<?> getRawType(Type type) {
            if (type instanceof Class<?>) {
                // type is a normal class.
                return (Class<?>) type;

            } else if (type instanceof ParameterizedType) {
                ParameterizedType parameterizedType = (ParameterizedType) type;

                // I'm not exactly sure why getRawType() returns Type instead of Class.
                // Neal isn't either but suspects some pathological case related
                // to nested classes exists.
                Type rawType = parameterizedType.getRawType();
                checkArgument(rawType instanceof Class);
                return (Class<?>) rawType;

            } else if (type instanceof GenericArrayType) {
                Type componentType = ((GenericArrayType) type).getGenericComponentType();
                return Array.newInstance(getRawType(componentType), 0).getClass();

            } else if (type instanceof TypeVariable) {
                // we could use the variable's bounds, but that won't work if there are multiple.
                // having a raw type that's more general than necessary is okay
                return Object.class;

            } else if (type instanceof WildcardType) {
                return getRawType(((WildcardType) type).getUpperBounds()[0]);

            } else {
                String className = type == null ? "null" : type.getClass().getName();
                throw new IllegalArgumentException("Expected a Class, ParameterizedType, or "
                        + "GenericArrayType, but <" + type + "> is of type " + className);
            }
        }

        private static void checkArgument(boolean condition) {
            if (!condition) {
                throw new IllegalArgumentException();
            }
        }

        public static boolean equals(Type type, Class<?> clazz) {
            return getRawType(type) == clazz;
        }
    }

    public static class DumpReceiver extends BroadcastReceiver {
        private static volatile DumpReceiver INSTANCE;
        private final BaseApplication context;

        private DumpReceiver(BaseApplication context) {
            this.context = context;
        }

        public static final String ACTION_DUMP_GUI = "DUMP_GUI";
        public static final String ACTION_DUMP_GUI_DETAILED = "DUMP_GUI_DETAILED";
        public static final String ACTION_DUMP_THREADS = "DUMP_THREADS";
        public static final String ACTION_DUMP_FILES = "DUMP_FILES";

        public IntentFilter getIntentFilter() {
            IntentFilter filter = new IntentFilter();
            filter.addAction(ACTION_DUMP_GUI);
            filter.addAction(ACTION_DUMP_GUI_DETAILED);
            filter.addAction(ACTION_DUMP_THREADS);
            filter.addAction(ACTION_DUMP_FILES);

            return filter;
        }

        @Override
        public void onReceive(Context context, Intent intent) {
            try {
                dumpByAction(intent);
            } catch (Exception e) {
                //do nothing
            }
        }

        private void dumpByAction(Intent intent) {
            Tracer.print(Dumper.dump(intent));

            //adb shell am broadcast -a DUMP_GUI
            //adb shell am broadcast -a DUMP_GUI_DETAILED
            //adb shell am broadcast -a DUMP_THREADS
            //adb shell am broadcast -a DUMP_FILES
            switch (intent.getAction()) {
                case ACTION_DUMP_GUI:
                    Tracer.print(DumpUtils.dumpActivity(context.topActivity()));
                    break;
                case ACTION_DUMP_GUI_DETAILED:
                    Tracer.print(DumpUtils.dumpViewHierarchy(context.topActivity()));
                    break;
                case ACTION_DUMP_THREADS:
                    Tracer.print(DumpUtils.dumpThreads());
                    break;
                case ACTION_DUMP_FILES:
                    Tracer.print("\n" + DumpUtils.Dir.listFiles(context.getCacheDir().getParent()));
                    break;
            }
        }

        public void register() {
            getContext().registerReceiver(this, getIntentFilter());
        }

        public void unregister() {
            try {
                getContext().unregisterReceiver(this);
            } catch (Exception e) {

            }
        }

        public static DumpReceiver get(BaseApplication context) {
            synchronized (DumpReceiver.class) {
                if (INSTANCE == null) {
                    INSTANCE = new DumpReceiver(context);
                }
                return INSTANCE;
            }
        }

        private Context getContext() {
            return context;
        }
    }
}