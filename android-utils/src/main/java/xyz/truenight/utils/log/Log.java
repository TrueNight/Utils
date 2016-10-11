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

import android.content.Context;
import android.os.Environment;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

public class Log {

    private Log() {

    }

    private static final AtomicReference<LogImplementation> logImpl = new AtomicReference<LogImplementation>(new AndroidLog());

    public enum LogLevel {
        V,
        D,
        I,
        W,
        E
    }

    public static LogImplementation get() {
        return logImpl.get();
    }

    public static void set(Log.LogImplementation logImpl) {
        Log.logImpl.set(logImpl);
    }

    public static void v(String tag, String msg) {
        get().v(tag, msg);
    }

    public static void v(String tag, Throwable tr) {
        get().v(tag, tr);
    }

    public static void v(String tag, String msg, Throwable tr) {
        get().v(tag, msg, tr);
    }

    public static void d(String tag, String msg) {
        get().d(tag, msg);
    }

    public static void d(String tag, Throwable tr) {
        get().d(tag, tr);
    }

    public static void d(String tag, String msg, Throwable tr) {
        get().d(tag, msg, tr);
    }

    public static void i(String tag, String msg) {
        get().i(tag, msg);
    }

    public static void i(String tag, Throwable tr) {
        get().i(tag, tr);
    }

    public static void i(String tag, String msg, Throwable tr) {
        get().i(tag, msg, tr);
    }

    public static void w(String tag, String msg) {
        get().w(tag, msg);
    }

    public static void w(String tag, Throwable tr) {
        get().w(tag, tr);
    }

    public static void w(String tag, String msg, Throwable tr) {
        get().w(tag, msg, tr);
    }

    public static void e(String tag, String msg) {
        get().e(tag, msg);
    }

    public static void e(String tag, Throwable th) {
        get().e(tag, th);
    }

    public static void e(String tag, String msg, Throwable tr) {
        get().e(tag, msg, tr);
    }

    public static void setEnable(boolean enabled) {
        get().setEnable(enabled);
    }

    public static abstract class LogImplementation {

        private AtomicBoolean mEnabled = new AtomicBoolean(true);

        public void setEnable(boolean enabled) {
            mEnabled.set(enabled);
        }

        public boolean isEnabled() {
            return mEnabled.get();
        }

        public void v(String tag, String msg) {
            log(Log.LogLevel.V, tag, msg);
        }

        public void v(String tag, Throwable tr) {
            log(Log.LogLevel.V, tag, Dumper.dump(tr));
        }

        public void v(String tag, String msg, Throwable tr) {
            log(Log.LogLevel.V, tag, msg + '\n' + Dumper.dump(tr));
        }

        public void d(String tag, String msg) {
            log(Log.LogLevel.D, tag, msg);
        }

        public void d(String tag, Throwable tr) {
            log(Log.LogLevel.D, tag, Dumper.dump(tr));
        }

        public void d(String tag, String msg, Throwable tr) {
            log(Log.LogLevel.D, tag, msg + '\n' + Dumper.dump(tr));
        }

        public void i(String tag, String msg) {
            log(Log.LogLevel.I, tag, msg);
        }

        public void i(String tag, Throwable tr) {
            log(Log.LogLevel.I, tag, Dumper.dump(tr));
        }

        public void i(String tag, String msg, Throwable tr) {
            log(Log.LogLevel.I, tag, msg + '\n' + Dumper.dump(tr));
        }

        public void w(String tag, String msg) {
            log(Log.LogLevel.W, tag, msg);
        }

        public void w(String tag, Throwable tr) {
            log(Log.LogLevel.W, tag, Dumper.dump(tr));
        }

        public void w(String tag, String msg, Throwable tr) {
            log(Log.LogLevel.W, tag, msg + '\n' + Dumper.dump(tr));
        }

        public void e(String tag, String msg) {
            log(Log.LogLevel.E, tag, msg);
        }

        public void e(String tag, Throwable tr) {
            log(Log.LogLevel.E, tag, Dumper.dump(tr));
        }

        public void e(String tag, String msg, Throwable tr) {
            log(Log.LogLevel.E, tag, msg + '\n' + Dumper.dump(tr));
        }

        public abstract void log(LogLevel level, String tag, String msg);

    }

    public static class AndroidLog extends LogImplementation {

        private static final int LOG_CHUNK_SIZE = 4000;

        @Override
        public void log(Log.LogLevel level, String tag, String msg) {
            if (isEnabled()) {
                int msgLines = msg.length() / LOG_CHUNK_SIZE + (msg.length() % LOG_CHUNK_SIZE == 0 ? 0 : 1);
                if (msgLines > 1) {
                    logInternal(level, tag, new StringBuilder().append("=====start=====(size = ")
                            .append(msg.length())
                            .append(" lines = ")
                            .append(msgLines)
                            .append(")\n").toString());
                }

                for (int i = 0, len = msg.length(); i < len; i += LOG_CHUNK_SIZE) {
                    int end = Math.min(len, i + LOG_CHUNK_SIZE);
                    logInternal(level, tag, msg.substring(i, end));
                }

                if (msgLines > 1) {
                    logInternal(level, tag, "=====end=====");
                }
            }
        }

        private void logInternal(Log.LogLevel level, String tag, String msg) {
            switch (level) {
                case V:
                    android.util.Log.v(tag, msg);
                    break;
                case D:
                    android.util.Log.d(tag, msg);
                    break;
                case I:
                    android.util.Log.i(tag, msg);
                    break;
                case W:
                    android.util.Log.w(tag, msg);
                    break;
                case E:
                    android.util.Log.e(tag, msg);
                    break;
            }
        }
    }

    public static class FileLog extends Log.LogImplementation {
        private LogSaveHelper mLogSaveHelper = new LogSaveHelper();

        private ThreadLocal<SimpleDateFormat> df = new ThreadLocal<SimpleDateFormat>() {
            @Override
            protected SimpleDateFormat initialValue() {
                return new SimpleDateFormat("MM-dd HH:mm:ss.SSS", Locale.US);
            }
        };

        public FileLog(File dir) {
            this(dir, 10);
        }

        public FileLog(File dir, long mbSize) {
            this(dir, mbSize, "log.txt", "old_log.txt");
        }

        public FileLog(File dir, long mbSize, String logFileName, String oldLogFileName) {
            mLogSaveHelper.setSettings(dir, logFileName, oldLogFileName, LogSaveHelper.ONE_MB * mbSize);
        }

        public void resetFile() {
            mLogSaveHelper.getLogFileStream(true);
        }

        @Override
        public void log(Log.LogLevel level, String tag, String s) {
            if (isEnabled()) {
                final String time = df.get().format(new Date(System.currentTimeMillis()));
                final long tid = Thread.currentThread().getId();
                write(String.format(Locale.US, "%s    %d/ %s %s, %s\n", time, tid, "" + level, tag, s));
            }
        }

        private void write(String s) {
            try {
                mLogSaveHelper.write(s);
            } catch (IOException e) {

            }
        }
    }

    public static class LogSet extends Log.LogImplementation {

        private final Log.LogImplementation[] logImplArr;

        public LogSet(Log.LogImplementation... logImplArr) {
            this.logImplArr = logImplArr;
        }

        @Override
        public void log(Log.LogLevel level, String tag, String s) {
            if (isEnabled()) {
                for (Log.LogImplementation log : logImplArr) {
                    log.log(level, tag, s);
                }
            }
        }

        public static Log.LogImplementation getFullLogSet(Context context) {
            return getFullLogSet(context, 10, "log.txt", "old_log.txt");
        }

        public static Log.LogImplementation getFullLogSet(Context context, String logFileName) {
            return getFullLogSet(context, 10, logFileName + ".txt", logFileName + "_old.txt");
        }

        public static Log.LogImplementation getFullLogSet(Context context, long mbSize, String logFileName, String oldLogFileName) {
            return new Log.LogSet(
                    new Log.FileLog(context.getCacheDir(), mbSize, logFileName, oldLogFileName),
                    new Log.FileLog(Environment.getExternalStorageDirectory(), mbSize, logFileName, oldLogFileName),
                    new Log.AndroidLog()
            );
        }
    }

    public static class LogSaveHelper {
        public static final int ONE_K = 1024;
        public static final int TEN_K = 10 * ONE_K;
        public static final long ONE_MB = 1024 * ONE_K;
        public static final long TEN_MB = 10 * ONE_MB;

        private volatile long maxLogFileSize = TEN_MB;

        private volatile File logFileDir;
        private volatile String logFileName;
        private volatile String oldLogFileName;

        private volatile File logFile;
        private volatile FileOutputStream logFileStream;

        public void setSettings(File logFileDir, String logFileName, String oldLogFileName, long maxFileSize) {
            maxLogFileSize = maxFileSize;
            this.logFileName = logFileName;
            this.oldLogFileName = oldLogFileName;
            this.logFileDir = logFileDir;
        }

        public boolean write(byte[] buffer, int byteOffset, int byteCount) throws IOException {
            FileOutputStream logFileStream;
            if ((logFileStream = getLogFileStream(false)) != null) {
                logFileStream.write(buffer, byteOffset, byteCount);
                logFileStream.flush();
            }
            return true;
        }

        public void write(String s) throws IOException {
            byte[] bytes = s.getBytes(Charset.forName("UTF-8"));
            write(bytes, 0, bytes.length);
        }

        public FileOutputStream getLogFileStream(boolean prepare) {
            if (prepare || (logFile != null && logFile.length() > maxLogFileSize)) {
                closeLogFileStream();
                prepareLogFile();
                initLogWriter();
            }

            if (logFileStream == null || logFile == null) {
                initLogWriter();
            }
            return logFileStream;
        }

        public void closeLogFileStream() {
            if (logFileStream != null) {
                try {
                    logFileStream.close();
                    logFileStream = null;
                    logFile = null;
                } catch (IOException e) {
                    logFileStream = null;
                    logFile = null;
                    Tracer.e("error closing output stream");
                }
            }
        }

        private File prepareLogFile() {
            File f = null;
            try {
                f = new File(logFileDir, logFileName);
                if (f.exists()) {
                    File from = new File(logFileDir, logFileName);
                    File to = new File(logFileDir, oldLogFileName);
                    if (to.exists()) {
                        to.delete();
                    }
                    from.renameTo(to);
                }
                f = new File(logFileDir, logFileName);
                f.createNewFile();
            } catch (IOException e) {
//                Tracer.e(e);
            }
            return f;
        }

        private File getLogFile() {
            File f = null;
            try {
                f = new File(logFileDir, logFileName);
                if (!f.exists()) {
                    f.createNewFile();
                }
            } catch (IOException e) {
//                Tracer.e(e);
            }
            return f;
        }

        private void initLogWriter() {
            try {
                logFile = getLogFile();
                logFileStream = new FileOutputStream(logFile, true);
            } catch (IOException e) {
//                Tracer.e(e);
            }
        }
    }
}