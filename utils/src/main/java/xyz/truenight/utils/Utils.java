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

import java.lang.ref.Reference;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

import xyz.truenight.utils.interfaces.Filter;

import static java.lang.Math.abs;

/**
 * Created by true
 * date: 22/03/16
 * time: 18:17
 */
public class Utils {

    private static final String EMPTY = "";

    private Utils() {

    }

    public static boolean check(int value, int mask) {
        return (value & mask) != 0;
    }

    public static Integer safe(Integer value) {
        return safe(value, 0);
    }

    public static Integer safe(Integer value, int defValue) {
        if (value != null) {
            return value;
        } else {
            return defValue;
        }
    }

    public static Long safe(Long value) {
        return safe(value, 0L);
    }

    public static Long safe(Long value, long defValue) {
        if (value != null) {
            return value;
        } else {
            return defValue;
        }
    }

    public static Double safe(Double value) {
        return safe(value, 0D);
    }

    public static Double safe(Double value, double defValue) {
        if (value != null) {
            return value;
        } else {
            return defValue;
        }
    }

    public static Float safe(Float value) {
        return safe(value, 0F);
    }

    public static Float safe(Float value, float defValue) {
        if (value != null) {
            return value;
        } else {
            return defValue;
        }
    }

    public static String safe(String value) {
        return string(value, EMPTY);
    }

    public static Short safe(Short value) {
        return safe(value, (short) 0);
    }

    public static Short safe(Short value, short defValue) {
        if (value != null) {
            return value;
        } else {
            return defValue;
        }
    }

    public static Boolean safe(Boolean value) {
        return value != null && value;
    }

    public static <T> T safe(T value, T defValue) {
        if (value != null) {
            return value;
        } else {
            return defValue;
        }
    }

    public static String ifOnly(boolean condition, String result) {
        if (condition) {
            return result;
        }
        return EMPTY;
    }

    public static boolean get(boolean[] array, int position) {
        return !(position < 0 || position >= Utils.sizeOf(array)) && array[position];
    }

    public static byte get(byte[] array, int position) {
        return position < 0 || position >= Utils.sizeOf(array) ? 0 : array[position];
    }

    public static char get(char[] array, int position) {
        return position < 0 || position >= Utils.sizeOf(array) ? 0 : array[position];
    }

    public static double get(double[] array, int position) {
        return position < 0 || position >= Utils.sizeOf(array) ? 0 : array[position];
    }

    public static float get(float[] array, int position) {
        return position < 0 || position >= Utils.sizeOf(array) ? 0 : array[position];
    }

    public static int get(int[] array, int position) {
        return position < 0 || position >= Utils.sizeOf(array) ? 0 : array[position];
    }

    public static long get(long[] array, int position) {
        return position < 0 || position >= Utils.sizeOf(array) ? 0 : array[position];
    }

    public static short get(short[] array, int position) {
        return position < 0 || position >= Utils.sizeOf(array) ? 0 : array[position];
    }

    public static <T> T get(T[] array, int position) {
        return position < 0 || position >= Utils.sizeOf(array) ? null : array[position];
    }

    public static <T> T get(List<T> data, int position) {
        return position < 0 || position >= Utils.sizeOf(data) ? null : data.get(position);
    }

    public static <T> T get(Map<?, T> data, Object key) {
        return data == null ? null : data.get(key);
    }

    public static boolean contains(Collection<?> data, Object what) {
        return data != null && data.contains(what);
    }

    public static boolean containsAll(Collection<?> data, Collection<?> what) {
        return data != null && data.containsAll(what);
    }

    public static boolean containsKey(Map<?, ?> data, Object what) {
        return data != null && data.containsKey(what);
    }

    public static boolean containsValue(Map<?, ?> data, Object what) {
        return data != null && data.containsValue(what);
    }

    public static boolean first(boolean[] list) {
        return !isEmpty(list) && list[0];
    }

    public static byte first(byte[] list) {
        return isEmpty(list) ? 0 : list[0];
    }

    public static char first(char[] list) {
        return isEmpty(list) ? 0 : list[0];
    }

    public static double first(double[] list) {
        return isEmpty(list) ? 0 : list[0];
    }

    public static float first(float[] list) {
        return isEmpty(list) ? 0 : list[0];
    }

    public static int first(int[] list) {
        return isEmpty(list) ? 0 : list[0];
    }

    public static long first(long[] list) {
        return isEmpty(list) ? 0 : list[0];
    }

    public static short first(short[] list) {
        return isEmpty(list) ? 0 : list[0];
    }

    public static <T> T first(T[] array) {
        return isEmpty(array) ? null : array[0];
    }

    public static <T> T first(List<T> list) {
        return isEmpty(list) ? null : list.get(0);
    }

    public static <T> T first(Collection<T> list) {
        return isEmpty(list) ? null : list.iterator().next();
    }

    public static boolean last(boolean[] list) {
        return !isEmpty(list) && list[list.length - 1];
    }

    public static byte last(byte[] list) {
        return isEmpty(list) ? 0 : list[list.length - 1];
    }

    public static char last(char[] list) {
        return isEmpty(list) ? 0 : list[list.length - 1];
    }

    public static double last(double[] list) {
        return isEmpty(list) ? 0 : list[list.length - 1];
    }

    public static float last(float[] list) {
        return isEmpty(list) ? 0 : list[list.length - 1];
    }

    public static int last(int[] list) {
        return isEmpty(list) ? 0 : list[list.length - 1];
    }

    public static long last(long[] list) {
        return isEmpty(list) ? 0 : list[list.length - 1];
    }

    public static short last(short[] list) {
        return isEmpty(list) ? 0 : list[list.length - 1];
    }

    public static <T> T last(T[] list) {
        return isEmpty(list) ? null : list[list.length - 1];
    }

    public static <T> T last(List<T> list) {
        return isEmpty(list) ? null : list.get(list.size() - 1);
    }

    public static <T> T[] pullFirst(T[] list) {
        if (isEmpty(list)) {
            return null;
        }
        return Arrays.copyOfRange(list, 1, list.length - 1);
    }

    public static <T> List<T> pullFirst(List<T> list) {
        if (isEmpty(list)) {
            return null;
        }
        List<T> newList = new ArrayList<>();
        for (int i = 1; i < list.size(); i++) {
            newList.add(list.get(i));
        }

        return newList;
    }

    public static <T> T[] pullLast(T[] list) {
        if (isEmpty(list)) {
            return null;
        }
        return Arrays.copyOf(list, list.length - 1);
    }

    public static <T> List<T> pullLast(List<T> list) {
        if (isEmpty(list)) {
            return null;
        }
        List<T> newList = new ArrayList<>();
        for (int i = 0; i < list.size() - 1; i++) {
            newList.add(list.get(i));
        }

        return newList;
    }

    public static boolean isEmpty(boolean[] array) {
        return array == null || array.length == 0;
    }

    public static boolean isEmpty(byte[] array) {
        return array == null || array.length == 0;
    }

    public static boolean isEmpty(char[] array) {
        return array == null || array.length == 0;
    }

    public static boolean isEmpty(double[] array) {
        return array == null || array.length == 0;
    }

    public static boolean isEmpty(float[] array) {
        return array == null || array.length == 0;
    }

    public static boolean isEmpty(int[] array) {
        return array == null || array.length == 0;
    }

    public static boolean isEmpty(long[] array) {
        return array == null || array.length == 0;
    }

    public static boolean isEmpty(short[] array) {
        return array == null || array.length == 0;
    }

    public static boolean isEmpty(Object[] array) {
        return array == null || array.length == 0;
    }

    public static boolean isEmpty(Collection collection) {
        return collection == null || collection.isEmpty();
    }

    public static boolean isEmpty(Map map) {
        return map == null || map.isEmpty();
    }

    public static boolean onlyOne(Collection collection) {
        return collection != null && collection.size() == 1;
    }

    public static boolean onlyOne(Object[] objects) {
        return objects != null && objects.length == 1;
    }

    public static int sizeOf(String string) {
        return string == null ? 0 : string.length();
    }

    public static int sizeOf(boolean[] list) {
        return list == null ? 0 : list.length;
    }

    public static int sizeOf(byte[] list) {
        return list == null ? 0 : list.length;
    }

    public static int sizeOf(char[] list) {
        return list == null ? 0 : list.length;
    }

    public static int sizeOf(double[] list) {
        return list == null ? 0 : list.length;
    }

    public static int sizeOf(float[] list) {
        return list == null ? 0 : list.length;
    }

    public static int sizeOf(int[] list) {
        return list == null ? 0 : list.length;
    }

    public static int sizeOf(long[] list) {
        return list == null ? 0 : list.length;
    }

    public static int sizeOf(short[] list) {
        return list == null ? 0 : list.length;
    }

    public static <T> int sizeOf(T[] list) {
        return list == null ? 0 : list.length;
    }

    public static int sizeOf(Collection<?> list) {
        return list == null ? 0 : list.size();
    }

    public static int sizeOf(Map<?, ?> map) {
        return map == null ? 0 : map.size();
    }

    public static <T> List<T> add(T... what) {
        List<T> data = new ArrayList<T>();
        for (T item : what) {
            data.add(item);
        }
        return data;
    }

    public static <T> List<T> add(List<T> to, T what) {
        List<T> data = safe(to, new ArrayList<T>());
        data.add(what);
        return data;
    }

    public static <T> Collection<T> add(Collection<T> to, T what) {
        Collection<T> data = safe(to, new ArrayList<T>());
        data.add(what);
        return data;
    }

    public static <T> List<T> addFirst(List<T> to, T what) {
        List<T> data = safe(to, new ArrayList<T>());
        data.add(0, what);
        return data;
    }

    public static <T> List<T> addAll(List<T> to, List<? extends T> what) {
        List<T> data = safe(to, new ArrayList<T>());
        if (!isEmpty(what)) {
            data.addAll(what);
        }
        return data;
    }

    public static <T> Collection<T> addAll(Collection<T> to, Collection<? extends T> what) {
        Collection<T> data = safe(to, new ArrayList<T>());
        if (!isEmpty(what)) {
            data.addAll(what);
        }
        return data;
    }

    public static <K, V> void put(Map<K, V> to, K key, V value) {
        if (to != null && value != null) {
            to.put(key, value);
        }
    }

    public static <K, V> void putAll(Map<K, V> to, Map<K, V> from) {
        if (to != null && from != null) {
            to.putAll(from);
        }
    }

    @SafeVarargs
    public static <T> List<T> union(List<T>... what) {
        HashSet<T> list = new HashSet<>();
        for (List<T> ts : what) {
            list.addAll(ts);
        }
        return new ArrayList<>(list);
    }

    @SafeVarargs
    public static <T> List<T> concatenate(List<T>... what) {
        List<T> list = new ArrayList<>();
        for (List<T> ts : what) {
            list.addAll(ts);
        }
        return list;
    }

    public static <T> void filter(Collection<T> data, Filter<T> filter) {
        if (!Utils.isEmpty(data)) {
            Iterator<T> iterator = data.iterator();
            while (iterator.hasNext()) {
                T item = iterator.next();
                if (!filter.accept(item)) {
                    iterator.remove();
                }
            }
        }
    }

    /**
     * Compares two {@code byte} values numerically.
     *
     * @param x the first {@code byte} to compare
     * @param y the second {@code byte} to compare
     * @return the value {@code 0} if {@code x == y};
     * a value less than {@code 0} if {@code x < y}; and
     * a value greater than {@code 0} if {@code x > y}
     */
    public static int compare(byte x, byte y) {
        return (x < y) ? -1 : ((x == y) ? 0 : 1);
    }

    /**
     * Compares two {@code int} values numerically.
     * The value returned is identical to what would be returned by:
     * <pre>
     *    Integer.valueOf(x).compareTo(Integer.valueOf(y))
     * </pre>
     *
     * @param x the first {@code int} to compare
     * @param y the second {@code int} to compare
     * @return the value {@code 0} if {@code x == y};
     * a value less than {@code 0} if {@code x < y}; and
     * a value greater than {@code 0} if {@code x > y}
     */
    public static int compare(int x, int y) {
        return (x < y) ? -1 : ((x == y) ? 0 : 1);
    }

    /**
     * Compares two {@code double} values numerically.
     *
     * @param x the first {@code double} to compare
     * @param y the second {@code double} to compare
     * @return the value {@code 0} if {@code x == y};
     * a value less than {@code 0} if {@code x < y}; and
     * a value greater than {@code 0} if {@code x > y}
     */
    public static int compare(double x, double y) {
        return (x < y) ? -1 : ((x == y) ? 0 : 1);
    }

    /**
     * Compares two {@code long} values numerically.
     *
     * @param x the first {@code long} to compare
     * @param y the second {@code long} to compare
     * @return the value {@code 0} if {@code x == y};
     * a value less than {@code 0} if {@code x < y}; and
     * a value greater than {@code 0} if {@code x > y}
     */
    public static int compare(long x, long y) {
        return (x < y) ? -1 : ((x == y) ? 0 : 1);
    }

    /**
     * Returns 0 if {@code a == b}, or {@code c.compare(a, b)} otherwise.
     * That is, this makes {@code c} null-safe.
     */
    public static <T> int compare(T a, T b, Comparator<? super T> c) {
        if (a == b) {
            return 0;
        }
        return c.compare(a, b);
    }

    /**
     * Returns true if both arguments are null,
     * the result of {@link Arrays#equals} if both arguments are primitive arrays,
     * the result of {@link Arrays#deepEquals} if both arguments are arrays of reference types,
     * and the result of {@link #equals} otherwise.
     */
    public static boolean deepEquals(Object a, Object b) {
        if (a == null || b == null) {
            return a == b;
        } else if (a instanceof Object[] && b instanceof Object[]) {
            return Arrays.deepEquals((Object[]) a, (Object[]) b);
        } else if (a instanceof boolean[] && b instanceof boolean[]) {
            return Arrays.equals((boolean[]) a, (boolean[]) b);
        } else if (a instanceof byte[] && b instanceof byte[]) {
            return Arrays.equals((byte[]) a, (byte[]) b);
        } else if (a instanceof char[] && b instanceof char[]) {
            return Arrays.equals((char[]) a, (char[]) b);
        } else if (a instanceof double[] && b instanceof double[]) {
            return Arrays.equals((double[]) a, (double[]) b);
        } else if (a instanceof float[] && b instanceof float[]) {
            return Arrays.equals((float[]) a, (float[]) b);
        } else if (a instanceof int[] && b instanceof int[]) {
            return Arrays.equals((int[]) a, (int[]) b);
        } else if (a instanceof long[] && b instanceof long[]) {
            return Arrays.equals((long[]) a, (long[]) b);
        } else if (a instanceof short[] && b instanceof short[]) {
            return Arrays.equals((short[]) a, (short[]) b);
        }
        return a.equals(b);
    }

    public static <T> T unwrap(Reference<T> reference) {
        return reference == null ? null : reference.get();
    }

    public static <T> T unwrap(AtomicReference<T> reference) {
        return reference == null ? null : reference.get();
    }


    /**
     * Null-safe equivalent of {@code a.equals(b)}.
     */
    public static boolean equal(Object a, Object b) {
        return (a == null) ? (b == null) : a.equals(b);
    }

    /**
     * Convenience wrapper for {@link Arrays#hashCode}, adding varargs.
     * This can be used to compute a hash code for an object's fields as follows:
     * {@code Objects.hash(a, b, c)}.
     */
    public static int hash(Object... values) {
        return Arrays.hashCode(values);
    }

    /**
     * Convenience wrapper for {@link Arrays#hashCode}, adding varargs.
     * This can be used to compute a hash code for an object's fields as follows:
     * {@code Objects.hash(a, b, c)}.
     */
    public static int absHash(Object... values) {
        return abs(Arrays.hashCode(values));
    }

    /**
     * Returns 0 for null or {@code o.hashCode()}.
     */
    public static int hashCode(Object o) {
        return (o == null) ? 0 : o.hashCode();
    }

    /**
     * Returns {@code o} if non-null, or throws {@code NullPointerException}.
     */
    public static <T> T requireNonNull(T o) {
        if (o == null) {
            throw new NullPointerException();
        }
        return o;
    }

    /**
     * Returns {@code o} if non-null, or throws {@code NullPointerException}
     * with the given detail message.
     */
    public static <T> T requireNonNull(T o, String message) {
        if (o == null) {
            throw new NullPointerException(message);
        }
        return o;
    }

    /**
     * Returns true if the string is null or 0-length.
     *
     * @param str the string to be examined
     * @return true if str is null or zero length
     */
    public static boolean isEmpty(CharSequence str) {
        if (str == null || str.length() == 0)
            return true;
        else
            return false;
    }

    /**
     * Returns NULL for null or {@code o.toString()}.
     */
    public static String toString(Object o) {
        return (o == null) ? null : o.toString();
    }

    /**
     * Returns NULL for null or {@code o.toString()}.
     */
    public static List<String> toString(Object... o) {
        List<String> strings = new ArrayList<>();
        for (Object item : o) {
            strings.add(toString(item));
        }
        return strings;
    }

    /**
     * Returns NULL for null or {@code o.toString()}.
     */
    public static List<String> toString(Collection o) {
        List<String> strings = new ArrayList<>();
        for (Object item : o) {
            strings.add(toString(item));
        }
        return strings;
    }

    /**
     * Returns "null" for null or {@code o.toString()}.
     */
    public static String string(Object o) {
        return (o == null) ? "null" : o.toString();
    }

    /**
     * Returns "null" for null or {@code o.toString()}.
     */
    public static List<String> string(Object... o) {
        List<String> strings = new ArrayList<>();
        for (Object item : o) {
            strings.add(string(item));
        }
        return strings;
    }

    /**
     * Returns "null" for null or {@code o.toString()}.
     */
    public static List<String> string(Collection o) {
        List<String> strings = new ArrayList<>();
        for (Object item : o) {
            strings.add(toString(item));
        }
        return strings;
    }

    /**
     * Returns {@code nullString} for null or {@code o.toString()}.
     */
    public static String string(Object o, String nullString) {
        return (o == null) ? nullString : o.toString();
    }

    public static boolean startsWith(String string, String prefix) {
        return isEmpty(string) && isEmpty(prefix) || string != null && string.startsWith(prefix);
    }

    public static CharSequence join(Iterable list) {
        return join("", list);
    }

    /**
     * Returns a string containing the tokens joined by delimiters.
     *
     * @param tokens an array objects to be joined. Strings will be formed from
     *               the objects by calling object.toString().
     */
    public static String join(CharSequence delimiter, Object... tokens) {
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

    /**
     * Returns a string containing the tokens joined by delimiters.
     *
     * @param tokens an array objects to be joined. Strings will be formed from
     *               the objects by calling object.toString().
     */
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

    public static String firstLine(String string) {
        if (string == null) return null;
        final int nextLineIndex = string.indexOf('\n');
        if (nextLineIndex > 0) return string.substring(0, nextLineIndex);
        return string;
    }

    /**
     * Splits this strings using the supplied {@code regularExpression}.
     */
    public static List<String> splitAndClearEmpty(ArrayList<String> strings, String regularExpression) {
        ArrayList<String> result = new ArrayList<>();
        for (String string : strings) {
            result.addAll(splitAndClearEmpty(string, regularExpression));
        }
        return result;
    }

    /**
     * Splits this string using the supplied {@code regularExpression}.
     */
    public static List<String> splitAndClearEmpty(String string, String regularExpression) {
        ArrayList<String> strings = new ArrayList<>();
        final String[] split = string.split(regularExpression);
        if (split == null || split.length == 0) {
            strings.add(string);
        } else {
            strings.addAll(Arrays.asList(split));
        }

        Iterator<String> iterator = strings.iterator();
        while (iterator.hasNext()) {
            String item = iterator.next();
            if (item == null || item.trim().length() == 0) {
                iterator.remove();
            }
        }

        return strings;
    }

    /**
     * Splits this strings using the supplied {@code regularExpression}.
     */
    public static String[] splitAndClear(String[] strings, String regularExpression) {
        ArrayList<String> result = new ArrayList<>();
        for (String string : strings) {
            result.addAll(splitAndClearEmpty(string, regularExpression));
        }
        return result.toArray(new String[result.size()]);
    }

    /**
     * Splits this string using the supplied {@code regularExpression}.
     */
    public static String[] splitAndClear(String string, String regularExpression) {
        ArrayList<String> strings = new ArrayList<>();
        final String[] split = string.split(regularExpression);
        if (split == null || split.length == 0) {
            strings.add(string);
        } else {
            strings.addAll(Arrays.asList(split));
        }

        Iterator<String> iterator = strings.iterator();
        while (iterator.hasNext()) {
            String item = iterator.next();
            if (item == null || item.trim().length() == 0) {
                iterator.remove();
            }
        }

        return strings.toArray(new String[strings.size()]);
    }

    public static int getIntValue(String intNumber) {
        try {
            return Integer.parseInt(intNumber);
        } catch (NumberFormatException e) {
            return 0;
        }
    }
}
