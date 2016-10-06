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

import android.os.SystemClock;
import android.support.annotation.NonNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

import xyz.truenight.utils.ConcurrentMap;

public class Profiler {
    private static final AtomicInteger mCounter = new AtomicInteger(0);
    private static volatile long startTime;
    private static final Timing DEFAULT_TIMING = Profiler.getNewInstance();

    public static void setStartTime(long startTime) {
        Profiler.startTime = startTime;
    }

    public static long getTimeFromStart() {
        return SystemClock.elapsedRealtime() - startTime;
    }

    public static String getTimeFromStartStr() {
        return Dumper.DateTimeHelper.getSecondDuration(getTimeFromStart());
    }

    public enum SortType {
        ORDINAL_NUMBER,
        KEY_NAME,
        AVERAGE_TIME
    }

    public static class Timing {
        private final int ordinalNumber = mCounter.incrementAndGet();
        private final ConcurrentMap<Object, Timing> timings = new ConcurrentMap<>();
        private final ConcurrentMap metadata = new ConcurrentMap();

        private volatile long launches;

        private volatile long lastStartThread;
        private volatile long lastStopThread;
        private volatile long totalDurationThread;
        private volatile long lastDurationThread;

        private volatile long lastStart;
        private volatile long lastStop;
        private volatile long totalDuration;
        private volatile long lastDuration;
        private volatile long firstStart;

        public void reset() {
            launches = 0;

            lastStartThread = 0;
            lastStopThread = 0;
            totalDurationThread = 0;
            lastDurationThread = 0;

            lastStart = 0;
            lastStop = 0;
            totalDuration = 0;
            lastDuration = 0;

            timings.clear();
            metadata.clear();
        }

        public void start() {
            if (firstStart == 0) {
                firstStart = SystemClock.elapsedRealtime() - startTime;
            }

            lastStart = SystemClock.elapsedRealtime();
            lastStartThread = SystemClock.currentThreadTimeMillis();
        }

        public void stop() {
            lastStop = SystemClock.elapsedRealtime();
            lastDuration = lastStop - lastStart;
            totalDuration += lastDuration;

            lastStopThread = SystemClock.currentThreadTimeMillis();
            lastDurationThread = lastStopThread - lastStartThread;
            totalDurationThread += lastDurationThread;

            launches++;
        }

        public double averageTime() {
            return ((double) totalDuration) / launches;
        }

        public long getTotalDuration() {
            return totalDuration;
        }

        public long getLastDuration() {
            return lastDuration;
        }

        public long getLastStart() {
            return lastStart;
        }

        public long getLastStop() {
            return lastStop;
        }

        public double averageTimeThread() {
            return ((double) totalDurationThread) / launches;
        }

        public long getTotalDurationThread() {
            return totalDurationThread;
        }

        public long getLastDurationThread() {
            return lastDurationThread;
        }

        public long getLastStartThread() {
            return lastStartThread;
        }

        public long getLastStopThread() {
            return lastStopThread;
        }

        public long getLaunches() {
            return launches;
        }

        @NonNull
        public Timing get(Object key) {
            Timing timing = timings.get(key);
            if (timing == null) {
                timing = new Timing();
                timings.put(key, timing);
            }
            return timing;
        }

        @NonNull
        public ConcurrentMap getMetadata() {
            return metadata;
        }

        public void printReport() {
            Tracer.config().depthOffset(1).print(getReport());
        }

        public String getReport() {
            StringBuilder sb = new StringBuilder();
            sb.append(" averageTime = ")
                    .append(this.averageTime())
                    .append(" threadAverageTime = ")
                    .append(this.averageTimeThread())
                    .append(" launches = ")
                    .append(this.getLaunches())
                    .append(" total duration = ")
                    .append(this.getTotalDuration())
                    .append(" thread total duration = ")
                    .append(this.getTotalDurationThread())
                    .append(" last duration = ")
                    .append(this.getLastDuration())
                    .append(" thread last duration = ")
                    .append(this.getLastDurationThread())
                    .append(" first start = ")
                    .append(Dumper.DateTimeHelper.getSecondDuration(firstStart));
            return sb.toString();
        }

        public String getShortReport() {
            StringBuilder sb = new StringBuilder();
            sb.append(" averageTime = ")
                    .append(this.averageTime())
                    .append(" threadAverageTime = ")
                    .append(this.averageTimeThread())
                    .append(" launches = ")
                    .append(this.getLaunches())
                    .append(" total duration = ")
                    .append(this.getTotalDuration())
                    .append(" thread total duration = ")
                    .append(this.getTotalDurationThread())
                    .append(" first start = ")
                    .append(Dumper.DateTimeHelper.getSecondDuration(firstStart));
            return sb.toString();
        }

        public String getFullReport() {
            return getFullReport(SortType.KEY_NAME);
        }

        public String getFullReport(SortType sortType) {
            StringBuilder sb = new StringBuilder();
            ArrayList<Object> keys = new ArrayList<>();
            keys.add(null);
            keys.addAll(Dumper.ReflectionHelper.getField(timings, "MAP", ConcurrentHashMap.class).keySet());

            switch (sortType) {
                case ORDINAL_NUMBER:
                    Collections.sort(keys, new Comparator<Object>() {
                        @Override
                        public int compare(Object lhsObj, Object rhsObj) {
                            int lhs = get(lhsObj).ordinalNumber;
                            int rhs = get(rhsObj).ordinalNumber;
                            return lhs < rhs ? -1 : (lhs == rhs ? 0 : 1);
                        }
                    });

                    break;
                case KEY_NAME:
                    Collections.sort(keys, new Comparator<Object>() {
                        @Override
                        public int compare(Object lhs, Object rhs) {
                            String lhsStr = lhs == null ? "default" : String.valueOf(lhs);
                            String rhsStr = rhs == null ? "default" : String.valueOf(rhs);
                            return lhsStr.compareTo(rhsStr);
                        }
                    });

                    break;
                case AVERAGE_TIME:
                    Collections.sort(keys, new Comparator<Object>() {
                        @Override
                        public int compare(Object lhsObj, Object rhsObj) {
                            double lhs = get(rhsObj).averageTime();
                            double rhs = get(lhsObj).averageTime();
                            return lhs < rhs ? -1 : (lhs == rhs ? 0 : 1);
                        }
                    });

                    break;
            }

            for (Object key : keys) {
                String keyStr = key == null ? "default" : String.valueOf(key);

                Timing item = key == null ? this : get(key);

                sb.append(keyStr).append(": ")
                        .append(item.getReport())
                        .append("\n");
            }

            sb.append("total").append(": ")
                    .append(getTotalTiming().getShortReport())
                    .append("\n");

            return sb.toString();
        }

        public Timing getTotalTiming() {
            ArrayList<Object> keys = new ArrayList<>();
            keys.add(null);
            keys.addAll(Dumper.ReflectionHelper.getField(timings, "MAP", ConcurrentHashMap.class).keySet());

            Timing totalTiming = new Timing();

            for (Object key : keys) {

                Timing item = key == null ? this : get(key);

                totalTiming.launches += item.getLaunches();
                totalTiming.totalDuration += item.getTotalDuration();
                totalTiming.totalDurationThread += item.getLastDurationThread();
            }

            return totalTiming;
        }
    }

    public static Timing getNewInstance() {
        return new Timing();
    }

    public static void reset() {
        DEFAULT_TIMING.reset();
    }

    public static void start() {
        DEFAULT_TIMING.start();
    }

    public static void stop() {
        DEFAULT_TIMING.stop();
    }

    public static double averageTime() {
        return DEFAULT_TIMING.averageTime();
    }

    public static double averageTimeThread() {
        return DEFAULT_TIMING.averageTimeThread();
    }

    public static long getLaunches() {
        return DEFAULT_TIMING.getLaunches();
    }

    public static long getTotalDuration() {
        return DEFAULT_TIMING.getTotalDuration();
    }

    public static long getTotalDurationThread() {
        return DEFAULT_TIMING.getTotalDurationThread();
    }

    public static long getLastDuration() {
        return DEFAULT_TIMING.getLastDuration();
    }

    public static long getLastStart() {
        return DEFAULT_TIMING.getLastStart();
    }

    public static long getLastStop() {
        return DEFAULT_TIMING.getLastStop();
    }

    @NonNull
    public static Timing get(Object key) {
        return DEFAULT_TIMING.get(key);
    }

    @NonNull
    public ConcurrentMap getMetadata() {
        return DEFAULT_TIMING.getMetadata();
    }

    public static void printReport() {
        Tracer.config().depthOffset(1).print(getReport());
    }

    public static String getReport() {
        return DEFAULT_TIMING.getReport();
    }

    public static String getShortReport() {
        return DEFAULT_TIMING.getShortReport();
    }

    public static String getFullReport() {
        return DEFAULT_TIMING.getFullReport();
    }

    public static String getFullReport(SortType sortType) {
        return DEFAULT_TIMING.getFullReport(sortType);
    }

}