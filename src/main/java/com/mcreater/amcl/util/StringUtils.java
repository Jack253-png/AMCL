package com.mcreater.amcl.util;

import org.nlpcn.commons.lang.index.MemoryIndex;
import org.nlpcn.commons.lang.pinyin.Pinyin;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Vector;
import java.util.function.Consumer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static com.mcreater.amcl.util.FileUtils.PathUtil.buildPath;

public class StringUtils {
    private static final DateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
    private static final DateFormat DATE_FORMAT2 = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
    private static final DateFormat DATE_FORMAT3 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private static final DateFormat DATE_FORMAT4 = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");

    public static class ForgeMapplings {
        public static String get(String raw) {
            raw = raw.replace("[", "").replace("]", "");
            Vector<String> f = new Vector<>(J8Utils.createList(raw.split(":")));
            String s1 = f.get(0);
            String s2 = f.get(1);
            String s3 = J8Utils.createList(f.get(2).split("@")).get(0);
            String p = J8Utils.createList(f.get(2).split("@")).get(1);
            return String.format(buildPath("%s", "%s", "%s", "%s-%s.%s"), s1.replace(".", File.separator), s2, s3, s2, s3, p);
        }

        public static String getLong(String raw) {
            raw = raw.replace("[", "").replace("]", "");
            Vector<String> f = new Vector<>(J8Utils.createList(raw.split(":")));
            String s1 = f.get(0).replace(".", File.separator);
            String s2 = f.get(1);
            String s3 = f.get(2);
            String s4 = f.get(3);
            if (s4.contains("@")) {
                s4 = s4.replace("@", ".");
            } else {
                s4 += ".jar";
            }
            return String.format(buildPath("%s", "%s", "%s", "%s-%s-%s"), s1, s2, s3, s2, s3, s4);
        }

        public static boolean checkIsForgePath(String s) {
            return s.contains("[") && s.contains("]");
        }

        public static boolean checkIsMapKey(String s) {
            return s.contains("{") && s.contains("}");
        }
    }

    public static class GetFileBaseDir {
        public static String get(String s) {
            return new File(s).getAbsoluteFile().getParent();
        }

        public static String forgeGet(String s) {
            List<String> st = J8Utils.createList(s.split(":"));
            try {
                return String.format(buildPath("%s", "%s", "%s", "%s-%s-%s.jar"), st.get(0).replace(".", File.separator), st.get(1), st.get(2), st.get(1), st.get(2), st.get(3));
            } catch (Exception e) {
                return String.format(buildPath("%s", "%s", "%s", "%s-%s.jar"), st.get(0).replace(".", File.separator), st.get(1), st.get(2), st.get(1), st.get(2));
            }
        }
    }

    public static class ArgReplace {
        public static String replace(String arg, Map<String, String> content) {
            for (Map.Entry<String, String> entry : content.entrySet()) {
                if (arg.contains(entry.getKey())) return arg.replace(entry.getKey(), entry.getValue());
            }
            return arg;
        }
    }

    public static String readFromStream(InputStream stream) {
        BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
        return reader.lines().collect(Collectors.joining("\n"));
    }

    public static Matcher returnCreatedMatcher(String s, Pattern pattern) {
        Matcher matcher = pattern.matcher(s);
        return matcher.find() ? matcher : null;
    }

    public static Date parseDate(String src) throws ParseException {
        return toTimeZoneDate(DATE_FORMAT.parse(src));
    }

    public static Date parseDate2(String src) throws ParseException {
        return toTimeZoneDate(DATE_FORMAT2.parse(src.split("\\+")[0]));
    }

    public static Date parseDate3(String src) throws ParseException {
        return toTimeZoneDate(DATE_FORMAT3.parse(src));
    }

    public static Date parseDate4(String src) throws ParseException {
        return toTimeZoneDate(DATE_FORMAT4.parse(src.split("\\.")[0]));
    }

    public static String toStringDate(Date date) {
        return DATE_FORMAT3.format(date);
    }

    public static Date toTimeZoneDate(Date date) {
        return Date.from(date.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime().toInstant(ZoneOffset.UTC));
    }

    public static long findEn(String src, String target) {
        return (src.length() - target.length()) + src.indexOf(target);
    }
}
