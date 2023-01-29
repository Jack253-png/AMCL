package com.mcreater.amcl.util.java;

import com.mcreater.amcl.natives.OSInfo;
import com.mcreater.amcl.util.J8Utils;
import com.mcreater.amcl.util.StringUtils;
import org.apache.commons.lang3.tuple.ImmutablePair;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Map;
import java.util.Vector;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static com.mcreater.amcl.util.ConsoleOutputHelper.readStreamContent;

public class JavaInfoGetter {
    private static final Pattern PROPERTY_PATTERN = Pattern.compile("    (?<key>.*) = (?<value>.*)");

    public static Vector<String> getCore(File f) {
        try {
            // "-XshowSettings:properties"
            String p = f.getPath();
            Process proc = new ProcessBuilder(p, "-XshowSettings:properties", "-version").start();
            String resu = readStreamContent(proc.getErrorStream(), OSInfo.NATIVE_CHARSET);
            String[] compled = resu.split("\n");
            String[] version_info = compled[0].split(" ");
            String version = "1.0.0";
            for (String s : version_info) {
                if (s.contains(".")) {
                    version = s;
                    break;
                }
            }
            String bits = "32";
            version = version.replace("\"", "");
            version = version.replace("_", " update ");
            if (compled[2].contains("64")) {
                bits = "64";
            }
            Vector<String> r = new Vector<>();
            r.add(version);
            r.add(bits);
            return r;
        } catch (IOException ignored) {
        }
        Vector<String> r = new Vector<>();
        for (int i = 0; i < 4; i++) {
            r.add("null");
        }
        return r;
    }

    public static JavaVersionInfo get(File f) {
        try {
            Process proc = new ProcessBuilder(f.getPath(), "-XshowSettings:properties", "-version").start();
            do {
            }
            while (!proc.isAlive());
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(proc.getErrorStream(), OSInfo.NATIVE_CHARSET))) {
                Map<String, String> properties = J8Utils.createMap(reader.lines()
                        .filter(s -> PROPERTY_PATTERN.matcher(s).find())
                        .map(s -> StringUtils.returnCreatedMatcher(s, PROPERTY_PATTERN))
                        .map((Function<Matcher, Map.Entry<String, String>>) matcher -> new ImmutablePair<>(matcher.group("key"), matcher.group("value")))
                        .collect(Collectors.toList())
                );

                JavaVersionInfo info = new JavaVersionInfo();
                info.javaClassVersion = properties.get("java.class.version");
                info.javaRuntimeName = properties.get("java.runtime.name");
                info.javaRuntimeVersion = properties.get("java.runtime.version");
                info.javaSpecificationName = properties.get("java.specification.name");
                info.javaSpecificationVendor = properties.get("java.specification.vendor");
                info.javaSpecificationVersion = properties.get("java.specification.version");
                info.javaVersion = properties.get("java.version");
                info.javaVersionDate = properties.get("java.version.date");
                info.javaVmInfo = properties.get("java.vm.info");
                info.javaVmName = properties.get("java.vm.name");
                return info;
            }
        } catch (Exception ignored) {

        }
        return new JavaVersionInfo();
    }

    public static class JavaVersionInfo {
        public String javaClassVersion;
        public String javaRuntimeName;
        public String javaRuntimeVersion;
        public String javaSpecificationName;
        public String javaSpecificationVendor;
        public String javaSpecificationVersion;
        public String javaVersion;
        public String javaVersionDate;
        public String javaVmInfo;
        public String javaVmName;
    }
}
