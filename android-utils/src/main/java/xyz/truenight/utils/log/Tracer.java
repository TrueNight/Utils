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

package xyz.truenight.utils.log;

import java.lang.reflect.InvocationTargetException;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

import xyz.truenight.utils.ConcurrentHashMap;

public class Tracer {

    private Tracer() {
    }

    private static AtomicReference<Class<?>> dumpClass = new AtomicReference<Class<?>>(Dumper.class);
    private static AtomicBoolean isPrintToLog = new AtomicBoolean(true);
    public static final String TRACER = "Tracer";
    private static final int CLIENT_CODE_STACK_INDEX;
    private static final ConcurrentHashMap S_CONCURRENT_MAP = new ConcurrentHashMap();
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

    /**
     * Class which have static method {@code dump(Object)}
     *
     * @param dumperClass dumper
     */
    public static void setDumper(Class<?> dumperClass) {
        Tracer.dumpClass.set(dumperClass);
    }

    public static boolean isEnable() {
        return isPrintToLog.get();
    }

    public static void print() {
        if (!isPrintToLog.get()) return;
        int depth = 1;
        String steInfo = Tracer.getSteInfo(depth, 0);
        print(Log.LogLevel.D, steInfo, false, null, null);
    }

    public static void print(Object... msg) {
        if (!isPrintToLog.get()) return;
        int depth = 1;
        String steInfo = Tracer.getSteInfo(depth, 0);
        print(Log.LogLevel.D, steInfo, false, dumpClass.get(), msg);
    }

    public static void print(Object msg) {
        if (!isPrintToLog.get()) return;
        int depth = 1;
        String steInfo = Tracer.getSteInfo(depth, 0);
        print(Log.LogLevel.D, steInfo, false, dumpClass.get(), msg);
    }

    public static String printToString() {
        if (!isPrintToLog.get()) return "";
        int depth = 1;
        String steInfo = Tracer.getSteInfo(depth, 0);
        return printToString(steInfo, false, null, null);
    }

    public static String printToString(Object... msg) {
        if (!isPrintToLog.get()) return "";
        int depth = 1;
        String steInfo = Tracer.getSteInfo(depth, 0);
        return printToString(steInfo, false, dumpClass.get(), msg);
    }

    public static String printToString(Object msg) {
        if (!isPrintToLog.get()) return "";
        int depth = 1;
        String steInfo = Tracer.getSteInfo(depth, 0);
        return printToString(steInfo, false, dumpClass.get(), msg);
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

    private static String printToString(Builder builder) {
        if (!isPrintToLog.get()) return "";

        String steInfo = Tracer.getSteInfo(builder.depth, 2 + builder.depthOffset);

        if (builder.printCallerChanges) {
            String key = firstLine(steInfo);
            synchronized (S_CONCURRENT_MAP) {
                if (S_CONCURRENT_MAP.compare(key, steInfo)) return "";
                S_CONCURRENT_MAP.put(key, steInfo);
            }
        }

        return printToString(steInfo, builder.threadInfo, builder.dump, builder.msg);
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
        print(Log.LogLevel.D, steInfo, false, null, msg);
    }

    private static void print(Log.LogLevel logLevel, String steInfo, boolean printThreadInfo, Class<?> dump, Object msg) {
        StringBuilder sb = new StringBuilder();

        sb.append(steInfo);

        if (printThreadInfo) {
            final Thread currentThread = Thread.currentThread();
            sb.append(" tid = ").append(currentThread.getId()).append(" tname = ");
            sb.append(currentThread.getName());
        }

        if (msg != null) {
            sb.append(" ").append(dump != null ? dump(dump, msg) : msg.toString());
        }

        logImpl.get().log(logLevel, TRACER, sb.toString());
    }

    private static String printToString(String steInfo, boolean printThreadInfo, Class<?> dump, Object msg) {
        StringBuilder sb = new StringBuilder();

        sb.append(steInfo);

        if (printThreadInfo) {
            final Thread currentThread = Thread.currentThread();
            sb.append(" tid = ").append(currentThread.getId()).append(" tname = ");
            sb.append(currentThread.getName());
        }

        if (msg != null) {
            sb.append(" ").append(dump != null ? dump(dump, msg) : msg.toString());
        }

        return sb.toString();
    }

    private static String dump(Class<?> dump, Object obj) {
        try {
            return (String) dump.getMethod("dump", Object.class).invoke(null, obj);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
        return "Dump error";
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
        private Class<?> dump = Dumper.class;
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

        /**
         * Class which have static method {@code dump(Object)}
         *
         * @param dumperClass dumper
         */
        public Builder dump(Class<?> dumperClass) {
            this.dump = dumperClass;
            return this;
        }

        public void print() {
            Tracer.print(this);
        }

        public void print(Object msg) {
            this.msg = msg;
            Tracer.print(this);
        }

        public void printToString() {
            Tracer.printToString(this);
        }

        public void printToString(Object msg) {
            this.msg = msg;
            Tracer.printToString(this);
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