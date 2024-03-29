package com.mcreater.amcl.util;

import com.mcreater.amcl.natives.OSInfo;
import com.mcreater.amcl.natives.PosixHandler;
import com.mcreater.amcl.patcher.ClassPathInjector;
import com.mcreater.amcl.util.builders.ThreadBuilder;
import jnr.posix.POSIX;
import jnr.posix.POSIXFactory;

import java.awt.image.BufferedImage;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.math.BigInteger;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Vector;
import java.util.concurrent.CountDownLatch;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;

public class FileUtils {
    public static class PathUtil {
        public static String toPlatformPath(String path) {
            return path.replace("/", File.separator).replace("\\", File.separator);
        }

        public static String buildPath(String... args) {
            return String.join(File.separator, Arrays.asList(args));
        }
    }

    public static class OperateUtil {
        public static void deleteFile(String path) {
            try {
                Path path2 = Paths.get(path);
                Files.deleteIfExists(path2);
            } catch (Exception ignored) {

            }
        }

        public static void deleteDirectory(File f, String orgin) {
            if (!f.exists()) {
                return;
            }
            if (f.isFile()) {
                f.delete();
                return;
            } else {
                for (File f1 : f.listFiles()) {
                    deleteDirectory(f1, orgin);
                }
            }
            if (!f.getPath().equals(orgin)) {
                f.delete();
            }
        }

        public static void deleteDirectory(File f) {
            if (!f.exists()) {
                return;
            }
            if (f.isFile()) {
                f.delete();
                return;
            } else {
                for (File f1 : f.listFiles()) {
                    deleteDirectory(f1);
                }
            }
            f.delete();
        }

        public static void createDirectory(String path) {
            new File(path).getAbsoluteFile().getParentFile().mkdirs();
        }

        public static void createDirectoryDirect(String path) {
            new File(path).mkdirs();
        }
    }

    public static String getJavaExecutable() {
        String env = getJavaExecutableInEnv();
        if (env != null) return env;
        List<File> path = getJavaTotal();
        if (path.size() > 0) {
            return path.get(0).getAbsolutePath();
        }
        return getCurrentJavaExecutable();
    }

    public static List<File> getJavaTotal() {
        Vector<File> java = getJavaExecutableInPath();
        return getJavaInSystemPath().stream()
                .filter(file2 -> java.stream()
                        .noneMatch(file -> file.getAbsolutePath().equals(file2.getAbsolutePath())))
                .collect(Collectors.toList());
    }

    private static Vector<File> getJavaInSystemPath() {
        Vector<File> basePaths = new Vector<>();
        if (OSInfo.isWin()) basePaths.add(new File("C:\\Program Files\\Java"));
        if (OSInfo.isMac()) basePaths.addAll(J8Utils.createList(
                new File("/Library/Java/JavaVirtualMachines"),
                new File("/System/Library/Java/JavaVirtualMachines")
        ));
        if (OSInfo.isLinux()) basePaths.add(new File("/usr/lib/jvm"));

        CountDownLatch latch = new CountDownLatch(basePaths.size());
        Vector<File> paths = new Vector<>();
        basePaths.stream()
                .map(file -> ThreadBuilder.createBuilder().runTarget(() -> {
                    try {
                        paths.addAll(getJavaInSelPath(file));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    latch.countDown();
                }).build())
                .forEach(Thread::start);
        try {
            latch.await();
        } catch (Exception ignored) {
        }

        return paths;
    }

    private static Vector<File> getJavaInSelPath(File f) throws IOException {
        Vector<File> files = new Vector<>();
        Files.walkFileTree(f.toPath(), new SimpleFileVisitor<Path>() {
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                File f = file.toFile();
                if (f.getName().endsWith(OSInfo.isWin() ? "java.exe" : "java")) {
                    files.add(f);
                }
                return FileVisitResult.CONTINUE;
            }

            public FileVisitResult postVisitDirectory(Path dir, IOException exc) {
                return FileVisitResult.CONTINUE;
            }
        });
        return files;
    }

    public static String getCurrentJavaExecutable() {
        String home = System.getProperty("java.home");
        if (OSInfo.isMac()) {
            home = LinkPath.link(home, "Contents/Home/bin/java");
        } else if (OSInfo.isLinux()) {
            home = LinkPath.link(home, "bin/java");
        } else {
            home = LinkPath.link(home, "bin\\java.exe");
        }
        try {
            if (ClassPathInjector.getJavaVersion(new File(home)) >= 8) {
                return home;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private static Vector<File> getJavaExecutableInPath() {
        String path_env = System.getenv("Path");
        if (path_env == null) return new Vector<>();
        String[] arg = path_env.split(File.pathSeparator);
        CountDownLatch latch = new CountDownLatch(arg.length);
        Vector<File> paths = new Vector<>();
        Arrays.stream(arg)
                .map(s -> ThreadBuilder.createBuilder().runTarget(() -> {
                    try {
                        File[] files = new File(s).listFiles((dir, name) -> name.contains(OSInfo.isWin() ? "java.exe" : "java"));
                        if (files != null) {
                            for (File f : files) {
                                if (ClassPathInjector.getJavaVersion(f) >= 8) {
                                    paths.add(f);
                                }
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    latch.countDown();
                }).build())
                .forEach(Thread::start);
        try {
            latch.await();
        } catch (Exception ignored) {
        }

        return paths;
    }

    private static String getJavaExecutableInEnv() {
        try {
            File envPath = new File(System.getenv("JAVA_HOME"), OSInfo.isWin() ? "java.exe" : "java");
            if (ClassPathInjector.getJavaVersion(envPath) >= 8) return envPath.getPath();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static class ChangeDir {
        public static String dirs;

        public static void saveNowDir() {
            dirs = System.getProperty("user.dir");
        }

        public static void changeToDefault() {
            changeTo(dirs);
        }

        public static void changeTo(String dir) {
            System.setProperty("user.dir", dir);
            PosixHandler handler = new PosixHandler();
            handler.setVerbose(true);
            POSIX posix = POSIXFactory.getPOSIX(handler, true);
            posix.chdir(dir);
        }
    }

    public static class FileStringReader {
        public static String read(String p) {
            File file = new File(PathUtil.toPlatformPath(p));
            BufferedReader reader = null;
            try {
                reader = new BufferedReader(new FileReader(file));
                String result = reader.lines().collect(Collectors.joining("\n"));
                reader.close();
                return result;
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (IOException ignored) {
                    }
                }
            }
            return "";
        }
    }

    public static void del(String p) {
        Path path = Paths.get(p);
        try {
            Files.walkFileTree(path, new SimpleFileVisitor<Path>() {
                public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                    Files.delete(file);
                    return FileVisitResult.CONTINUE;
                }

                public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
                    Files.delete(dir);
                    return FileVisitResult.CONTINUE;
                }
            });
        } catch (IOException ignored) {

        }
    }

    public static class HashHelper {
        private static void putInt(byte[] array, int offset, int x) {
            array[offset] = (byte) (x >> 24 & 0xff);
            array[offset + 1] = (byte) (x >> 16 & 0xff);
            array[offset + 2] = (byte) (x >> 8 & 0xff);
            array[offset + 3] = (byte) (x & 0xff);
        }

        public static String computeTextureHash(BufferedImage img) {
            MessageDigest digest;
            try {
                digest = MessageDigest.getInstance("SHA-256");
            } catch (NoSuchAlgorithmException e) {
                throw new RuntimeException(e);
            }
            int width = img.getWidth();
            int height = img.getHeight();
            byte[] buf = new byte[4096];

            putInt(buf, 0, width);
            putInt(buf, 4, height);
            int pos = 8;
            for (int x = 0; x < width; x++) {
                for (int y = 0; y < height; y++) {
                    putInt(buf, pos, img.getRGB(x, y));
                    if (buf[pos] == 0) {
                        buf[pos + 1] = buf[pos + 2] = buf[pos + 3] = 0;
                    }
                    pos += 4;
                    if (pos == buf.length) {
                        pos = 0;
                        digest.update(buf, 0, buf.length);
                    }
                }
            }
            if (pos > 0) {
                digest.update(buf, 0, pos);
            }

            byte[] sha256 = digest.digest();
            return String.format("%0" + (sha256.length << 1) + "x", new BigInteger(1, sha256));
        }

        public static boolean validateSHA1(File file, String target) {
            if (!file.exists()) {
                return false;
            }
            if (target == null) {
                return true;
            }
            String fileSHA1 = getFileSHA1(file);
            return Objects.equals(fileSHA1, target);
        }

        private static String getFileSHA1(File file) {
            MessageDigest md;
            FileInputStream fis = null;
            StringBuilder sha1Str = new StringBuilder();
            try {
                fis = new FileInputStream(file);
                MappedByteBuffer mbb = fis.getChannel().map(FileChannel.MapMode.READ_ONLY, 0, file.length());
                md = MessageDigest.getInstance("SHA-1");
                md.update(mbb);
                byte[] digest = md.digest();
                String shaHex = "";
                for (byte b : digest) {
                    shaHex = Integer.toHexString(b & 0xFF);
                    if (shaHex.length() < 2) {
                        sha1Str.append(0);
                    }
                    sha1Str.append(shaHex);
                }
            } catch (NoSuchAlgorithmException | IOException e) {
                e.printStackTrace();
            } finally {
                if (fis != null) {
                    try {
                        fis.close();
                    } catch (IOException ignored) {
                    }
                }
            }
            return sha1Str.toString();

        }
    }

    public static class LinkPath {
        public static String link(String p1, String p2) {
            return new File(p1, p2).getPath();
        }
    }

    public static class RemoveFileToTrash {
        public static void remove(String path) {
            File f = new File(path);
            if (f.exists()) {
                com.sun.jna.platform.FileUtils fu = com.sun.jna.platform.FileUtils.getInstance();
                if (fu.hasTrash()) {
                    try {
                        fu.moveToTrash(f);
                    } catch (IOException e) {
                        OperateUtil.deleteDirectory(f);
                    }
                } else {
                    OperateUtil.deleteDirectory(f);
                }
            }
        }
    }

    public static class ZipUtil {
        public static Map<String, String> getNativeHash(String iy) throws IOException {
            Map<String, String> haMap = new HashMap<>();
            ZipFile zp = new ZipFile(iy, Charset.forName("gbk"));
            Enumeration<? extends ZipEntry> entries = zp.entries();
            while (entries.hasMoreElements()) {
                ZipEntry entry = entries.nextElement();
                if (entry.getName().endsWith(".sha1")) {
                    InputStream stream = zp.getInputStream(entry);
                    BufferedReader reader = new BufferedReader(new InputStreamReader(stream));

                    String s = reader.readLine();
                    reader.close();
                    haMap.put(entry.getName().replace(".sha1", ""), s);
                }
            }
            zp.close();
            return haMap;
        }

        public static void unzip(String iy, String o) {
            Map<String, String> haMap = new HashMap<>();
            try {
                haMap = getNativeHash(iy);
            } catch (Exception e) {
            }

            File pathFile = new File(o);
            if (!pathFile.exists()) {
                pathFile.mkdirs();
            }
            ZipFile zp;
            try {
                zp = new ZipFile(iy, Charset.forName("gbk"));
                Enumeration<? extends ZipEntry> entries = zp.entries();
                while (entries.hasMoreElements()) {
                    ZipEntry entry = entries.nextElement();
                    String zipEntryName = entry.getName();
                    InputStream in = zp.getInputStream(entry);
                    String outpath = PathUtil.toPlatformPath(LinkPath.link(o, zipEntryName));
                    File file = new File(outpath.substring(0, outpath.lastIndexOf(File.separator)));
                    if (!file.exists()) {
                        file.mkdirs();
                    }
                    if (new File(outpath).isDirectory())
                        continue;
                    boolean isPassed = HashHelper.validateSHA1(new File(outpath), haMap.get(new File(outpath).getName()));
                    if ((outpath.endsWith(".dll") || outpath.endsWith(".so") || outpath.endsWith(".dylib")) && !isPassed) {
                        OutputStream out = Files.newOutputStream(Paths.get(outpath));
                        byte[] bf = new byte[4096];
                        int len;
                        while ((len = in.read(bf)) > 0) {
                            out.write(bf, 0, len);
                        }
                        in.close();
                        out.close();
                    }
                }
                zp.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        public static void unzipAll(String iy, String o) throws IOException {
            File desDir = new File(o);
            if (!desDir.exists()) {
                boolean mkdirSuccess = desDir.mkdir();
            }
            ZipInputStream zipInputStream = new ZipInputStream(Files.newInputStream(Paths.get(iy)));
            ZipEntry zipEntry = zipInputStream.getNextEntry();
            while (zipEntry != null) {
                if (zipEntry.isDirectory()) {
                    String unzipFilePath = o + File.separator + zipEntry.getName();
                    mkdir(new File(unzipFilePath));
                } else {
                    String unzipFilePath = o + File.separator + zipEntry.getName();
                    File file = new File(unzipFilePath);
                    mkdir(file.getParentFile());
                    BufferedOutputStream bufferedOutputStream =
                            new BufferedOutputStream(Files.newOutputStream(Paths.get(unzipFilePath)));
                    byte[] bytes = new byte[1024];
                    int readLen;
                    while ((readLen = zipInputStream.read(bytes)) != -1) {
                        bufferedOutputStream.write(bytes, 0, readLen);
                    }
                    bufferedOutputStream.close();
                }
                zipInputStream.closeEntry();
                zipEntry = zipInputStream.getNextEntry();
            }
            zipInputStream.close();
        }

        private static void mkdir(File file) {
            if (null == file || file.exists()) {
                return;
            }
            mkdir(file.getParentFile());
            file.mkdir();
        }

        public static String readTextFileInZip(String zipFile, String internalFileName) throws IOException {
            ZipInputStream zipInputStream = new ZipInputStream(Files.newInputStream(Paths.get(zipFile)));
            ZipEntry zipEntry = zipInputStream.getNextEntry();
            String content = "";
            while (zipEntry != null) {
                if (!zipEntry.isDirectory() && zipEntry.getName().equals(internalFileName)) {
                    ByteArrayOutputStream outStream = new ByteArrayOutputStream();
                    byte[] bytes = new byte[64];
                    int length;
                    while ((length = zipInputStream.read(bytes)) != -1) {
                        outStream.write(bytes, 0, length);
                    }

                    content = outStream.toString();
                    zipInputStream.closeEntry();
                    return content;
                }
                zipEntry = zipInputStream.getNextEntry();
            }
            return null;
        }

        public static InputStream readBinaryFileInZip(String zipFile, String internalFileName) throws IOException {
            ZipInputStream zipInputStream = new ZipInputStream(Files.newInputStream(Paths.get(zipFile)));
            ZipEntry zipEntry = zipInputStream.getNextEntry();
            while (zipEntry != null) {
                if (!zipEntry.isDirectory() && zipEntry.getName().equals(internalFileName)) {
                    ByteArrayOutputStream outStream = new ByteArrayOutputStream();
                    byte[] bytes = new byte[64];
                    int length;
                    while ((length = zipInputStream.read(bytes)) != -1) {
                        outStream.write(bytes, 0, length);
                    }
                    return new ByteArrayInputStream(outStream.toByteArray());
                }
                zipEntry = zipInputStream.getNextEntry();
            }
            return new ByteArrayInputStream(new byte[0]);
        }
    }
}
