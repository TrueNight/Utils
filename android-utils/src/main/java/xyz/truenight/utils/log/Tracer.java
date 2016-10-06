package xyz.truenight.utils.log;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

import xyz.truenight.utils.ConcurrentMap;

public class Tracer {

    private Tracer() {
    }

    private static AtomicBoolean isPrintToLog = new AtomicBoolean(true);
    public static final String TRACER = "Tracer";
    private static final int CLIENT_CODE_STACK_INDEX;
    private static final ConcurrentMap S_CONCURRENT_MAP = new ConcurrentMap();
    private static final AtomicReference<Log.LogImplementation> logImpl = new AtomicReference<>(Log.get());

    static {
        int i = 0;
        try {
            for (StackTraceElement ste : Thread.currentThread().getStackTrace()) {
                i++;
                if (ste.getClassName().equals(Tracer.class.getName())) {
                    break;
                }
            }
        } catch (Throwable t) {

        }
        CLIENT_CODE_STACK_INDEX = ++i;
    }

    private static void setLog(Log.LogImplementation logImpl) {
        Tracer.logImpl.set(logImpl);
    }

    private static String getSteInfo(int depth, int depthOffset) {
        final StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
        String result = null;
        if (depth == 1) {
            result = stackTrace[CLIENT_CODE_STACK_INDEX + depthOffset].toString();
        } else if (depth > 1) {
            final int lastNeedfulIndex = CLIENT_CODE_STACK_INDEX + depthOffset + depth;
            final char nextLine = '\n';
            final StringBuilder sb = new StringBuilder();
            for (int i = CLIENT_CODE_STACK_INDEX + depthOffset; i < stackTrace.length
                    && i < lastNeedfulIndex; i++) {
                sb.append(stackTrace[i]).append(nextLine);
            }
            result = sb.toString();
        }
        return result;
    }

    private static String getFileName(int depthOffset) {
        final StackTraceElement element =
                Thread.currentThread().getStackTrace()[CLIENT_CODE_STACK_INDEX + depthOffset];
        String result = element.getFileName();
        int position = result.indexOf(".java");
        return position > -1 ? result.substring(0, position) : result;
    }

    private static String getMethodName(int depthOffset) {
        final StackTraceElement element =
                Thread.currentThread().getStackTrace()[CLIENT_CODE_STACK_INDEX + depthOffset];
        return element.getMethodName();
    }

    public static void setEnable(boolean isPrintToLog) {
        Tracer.isPrintToLog.set(isPrintToLog);
    }

    public static boolean isEnable() {
        return isPrintToLog.get();
    }

    public static void print() {
        if (!isPrintToLog.get()) return;
        int depth = 1;
        String steInfo = Tracer.getSteInfo(depth, 0);
        print(Log.LogLevel.D, steInfo, false, false, null);
    }

    public static void print(Object... msg) {
        if (!isPrintToLog.get()) return;
        int depth = 1;
        String steInfo = Tracer.getSteInfo(depth, 0);
        print(Log.LogLevel.D, steInfo, false, true, msg);
    }

    public static void print(Object msg) {
        if (!isPrintToLog.get()) return;
        int depth = 1;
        String steInfo = Tracer.getSteInfo(depth, 0);
        print(Log.LogLevel.D, steInfo, false, false, msg);
    }

    public static void e(String msg) {
        Tracer.config().depthOffset(1).logLevel(Log.LogLevel.E).print(msg);
    }

    public static void e(Throwable tr) {
        Tracer.config().depthOffset(1).logLevel(Log.LogLevel.E).print('\n' + Dumper.dump(tr));
    }

    public static void e(String msg, Throwable tr) {
        Tracer.config().depthOffset(1).logLevel(Log.LogLevel.E).print(msg + '\n' + Dumper.dump(tr));
    }

    public static void w(String msg) {
        Tracer.config().depthOffset(1).logLevel(Log.LogLevel.W).print(msg);
    }

    public static void w(Throwable tr) {
        Tracer.config().depthOffset(1).logLevel(Log.LogLevel.W).print('\n' + Dumper.dump(tr));
    }

    public static void w(String msg, Throwable tr) {
        Tracer.config().depthOffset(1).logLevel(Log.LogLevel.W).print(msg + '\n' + Dumper.dump(tr));
    }

    private static void print(Builder builder) {
        if (!isPrintToLog.get()) return;

        String steInfo = Tracer.getSteInfo(builder.depth, 2 + builder.depthOffset);

        if (builder.printCallerChanges) {
            String key = firstLine(steInfo);
            synchronized (S_CONCURRENT_MAP) {
                if (S_CONCURRENT_MAP.compare(key, steInfo)) return;
                S_CONCURRENT_MAP.put(key, steInfo);
            }
        }

        if (builder.wrap) {
            logImpl.get().log(builder.logLevel, TRACER, "===============start===============");
        }

        print(builder.logLevel, steInfo, builder.threadInfo, builder.dump, builder.msg);

        if (builder.wrap) {
            logImpl.get().log(builder.logLevel, TRACER, "================end================");
        }
    }

    public static String firstLine(String string) {
        if (string == null) return null;
        final int nextLineIndex = string.indexOf('\n');
        if (nextLineIndex > 0) return string.substring(0, nextLineIndex);
        return string;
    }

    public static void printChanges(Object key, Object msg) {
        if (!isPrintToLog.get()) return;

        if (key != null) {
            synchronized (S_CONCURRENT_MAP) {
                if (S_CONCURRENT_MAP.compare(key, msg)) return;
                S_CONCURRENT_MAP.put(key, msg);
            }
        }

        int depth = 1;
        String steInfo = Tracer.getSteInfo(depth, 0);
        print(Log.LogLevel.D, steInfo, false, false, msg);
    }

    private static void print(Log.LogLevel logLevel, String steInfo, boolean printThreadInfo, boolean dump, Object msg) {
        StringBuilder sb = new StringBuilder();

        sb.append(steInfo);

        if (printThreadInfo) {
            final Thread currentThread = Thread.currentThread();
            sb.append(" tid = ").append(currentThread.getId()).append(" tname = ");
            sb.append(currentThread.getName());
        }

        if (msg != null) {
            sb.append(" ").append(dump ? Dumper.dump(msg) : msg.toString());
        }

        logImpl.get().log(logLevel, TRACER, sb.toString());
    }

    public static Builder config() {
        return new Builder();
    }

    public static class Builder {
        private int depth = 1;
        private int depthOffset = 0;
        private boolean threadInfo;
        private boolean wrap;
        private Object msg;
        private Log.LogLevel logLevel = Log.LogLevel.D;
        private boolean dump;
        private boolean printCallerChanges;

        public Builder threadInfo() {
            this.threadInfo = true;
            return this;
        }

        public Builder depth(int depth) {
            this.depth = depth;
            if (depth > 1) {
                this.wrap = true;
            }
            return this;
        }

        public Builder logLevel(Log.LogLevel logLevel) {
            this.logLevel = logLevel;
            return this;
        }

        public Builder depthOffset(int depthOffset) {
            this.depthOffset = depthOffset;
            return this;
        }

        public Builder threadInfo(boolean threadInfo) {
            this.threadInfo = threadInfo;
            return this;
        }

        public Builder dump() {
            this.dump = true;
            return this;
        }

        public void print() {
            Tracer.print(this);
        }

        public void print(Object msg) {
            this.msg = msg;
            Tracer.print(this);
        }

        public void printCallerChanges() {
            this.printCallerChanges = true;
            Tracer.print(this);
        }

        public void printCallerChanges(Object msg) {
            this.printCallerChanges = true;
            this.msg = msg;
            Tracer.print(this);
        }
    }
}