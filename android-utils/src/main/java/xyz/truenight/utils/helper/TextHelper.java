package xyz.truenight.utils.helper;

import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import xyz.truenight.utils.log.Tracer;

public class TextHelper {

    public static String makeNotNull(String s) {
        return s != null ? s : "";
    }

    public static String curl(URL url, String requestMethod, HashMap<String, List<String>> requestHeaders, String requestBody) {
        //curl 'http://tobox.com/api/beta/adv' -i -X POST -H 'header1' -H 'header2' -d 'body'
        StringBuilder sb = new StringBuilder();
        sb.append("curl --compressed '").append(url).append("' -i -X ").append(requestMethod);
        for (String key : requestHeaders.keySet()) {
            List<String> headerList = requestHeaders.get(key);
            String headerVal;
            if (headerList != null && headerList.size() == 1) {
                headerVal = headerList.get(0);
            } else {
                headerVal = "" + headerList;
            }
            sb.append(" -H '").append(key).append(": ").append(headerVal).append("'");
        }
        sb.append(" -d '").append(requestBody).append("'").toString();
        return sb.toString();
    }

    public static String trim(String s) {
        return makeNotNull(s).trim();
    }

    public static void equalsTest(String s1, String s2) {
        Tracer.print("val 1 = " + s1 + "\nval 2 = " + s2 + "\n equals = " + s1.equals(s2));
    }

    public static ArrayList<String> splitAndClearEmpty(ArrayList<String> strings, String regularExpression) {
        ArrayList<String> result = new ArrayList<>();
        for (String string : strings) {
            result.addAll(splitAndClearEmpty(string, regularExpression));
        }
        return result;
    }

    public static ArrayList<String> splitAndClearEmpty(String string, String regularExpression) {
        ArrayList<String> strings = new ArrayList<>();
        final String[] split = string.split(" ");
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

    public static int getValue(String newText) {
        int value;
        try {
            value = Integer.parseInt(newText);
        } catch (NumberFormatException e) {
            value = 0;
        }
        return value;
    }

    public static String firstLine(String string) {
        if (string == null) return null;
        final int nextLineIndex = string.indexOf('\n');
        if (nextLineIndex > 0) return string.substring(0, nextLineIndex);
        return string;
    }
}
