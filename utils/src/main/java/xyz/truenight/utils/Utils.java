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
        return position < 0 || position >= Utils.sizeOf(array) ? false : array[position];
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

    public static <T> T first(T[] list) {
        return list != null && list.length > 0 ? list[0] : null;
    }

    public static <T> T first(List<T> list) {
        return list != null && !list.isEmpty() ? list.get(0) : null;
    }

    public static <T> T first(Collection<T> list) {
        return list != null && !list.isEmpty() ? list.iterator().next() : null;
    }

    public static <T> T last(T[] list) {
        return list != null && list.length > 0 ? list[list.length - 1] : null;
    }

    public static <T> T last(List<T> list) {
        return list != null && !list.isEmpty() ? list.get(list.size() - 1) : null;
    }

    public static <T> List<T> pullFirst(List<T> list) {
        if (list == null || list.isEmpty()) {
            return null;
        }
        List<T> newList = new ArrayList<>();
        for (int i = 1; i < list.size(); i++) {
            newList.add(list.get(i));
        }

        return newList;
    }

    public static <T> T[] pullFirst(T[] list) {
        if (list == null || list.length == 0) {
            return null;
        }
        return Arrays.copyOfRange(list, 1, list.length - 1);
    }

    public static <T> List<T> pullLast(List<T> list) {
        if (list == null || list.isEmpty()) {
            return null;
        }
        List<T> newList = new ArrayList<>();
        for (int i = 0; i < list.size() - 1; i++) {
            newList.add(list.get(i));
        }

        return newList;
    }

    public static <T> T[] pullLast(T[] list) {
        if (list == null || list.length == 0) {
            return null;
        }
        return Arrays.copyOf(list, list.length - 1);
    }

    public static boolean isEmpty(Collection collection) {
        return collection == null || collection.isEmpty();
    }

    public static boolean isEmpty(Object[] objects) {
        return objects == null || objects.length == 0;
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

    public static <K, V> void putAll(Map<K, V> to, Map<K, V> from) {
        if (to != null && from != null) {
            to.putAll(from);
        }
    }

    public static <T> T unwrap(Reference<T> reference) {
        return reference == null ? null : reference.get();
    }

    public static <T> T unwrap(AtomicReference<T> reference) {
        return reference == null ? null : reference.get();
    }

    public static int sizeOf(String string) {
        return string == null ? 0 : string.length();
    }

    public static <T> int sizeOf(boolean[] list) {
        return list == null ? 0 : list.length;
    }

    public static <T> int sizeOf(byte[] list) {
        return list == null ? 0 : list.length;
    }

    public static <T> int sizeOf(char[] list) {
        return list == null ? 0 : list.length;
    }

    public static <T> int sizeOf(double[] list) {
        return list == null ? 0 : list.length;
    }

    public static <T> int sizeOf(float[] list) {
        return list == null ? 0 : list.length;
    }

    public static <T> int sizeOf(int[] list) {
        return list == null ? 0 : list.length;
    }

    public static <T> int sizeOf(long[] list) {
        return list == null ? 0 : list.length;
    }

    public static <T> int sizeOf(short[] list) {
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

    public static int getValue(String intNumber) {
        int value;
        try {
            value = Integer.parseInt(intNumber);
        } catch (NumberFormatException e) {
            value = 0;
        }
        return value;
    }
}
