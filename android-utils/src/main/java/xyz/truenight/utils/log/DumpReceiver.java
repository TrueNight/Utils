package xyz.truenight.utils.log;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

import xyz.truenight.utils.BaseApplication;

public class DumpReceiver extends BroadcastReceiver {
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
                Tracer.print(Dumper.DumpUtils.dumpActivity(context.topActivity()));
                break;
            case ACTION_DUMP_GUI_DETAILED:
                Tracer.print(Dumper.DumpUtils.dumpViewHierarchy(context.topActivity()));
                break;
            case ACTION_DUMP_THREADS:
                Tracer.print(Dumper.DumpUtils.dumpThreads());
                break;
            case ACTION_DUMP_FILES:
                Tracer.print("\n" + Dumper.DumpUtils.Dir.listFiles(context.getCacheDir().getParent()));
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