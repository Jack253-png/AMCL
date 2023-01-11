package com.mcreater.amcl.game.launch;

import com.mcreater.amcl.Launcher;
import com.mcreater.amcl.StableMain;
import com.mcreater.amcl.api.auth.LocalYggdrasilServer;
import com.mcreater.amcl.api.auth.users.AbstractUser;
import com.mcreater.amcl.api.auth.users.OffLineUser;
import com.mcreater.amcl.exceptions.BadLibDirException;
import com.mcreater.amcl.exceptions.BadMainFilesException;
import com.mcreater.amcl.exceptions.BadMinecraftDirException;
import com.mcreater.amcl.exceptions.BadNativeDirException;
import com.mcreater.amcl.exceptions.BadUnzipException;
import com.mcreater.amcl.exceptions.BadUserException;
import com.mcreater.amcl.exceptions.BadVersionDirException;
import com.mcreater.amcl.exceptions.ProcessException;
import com.mcreater.amcl.game.MavenPathConverter;
import com.mcreater.amcl.game.VersionTypeGetter;
import com.mcreater.amcl.model.ArgItemModel;
import com.mcreater.amcl.model.LibModel;
import com.mcreater.amcl.model.VersionJsonModel;
import com.mcreater.amcl.nativeInterface.EnumWindow;
import com.mcreater.amcl.nativeInterface.ResourceGetter;
import com.mcreater.amcl.pages.MainPage;
import com.mcreater.amcl.pages.dialogs.commons.SimpleDialogCreater;
import com.mcreater.amcl.tasks.DownloadTask;
import com.mcreater.amcl.tasks.manager.TaskManager;
import com.mcreater.amcl.util.ConsoleOutputHelper;
import com.mcreater.amcl.util.FileUtils;
import com.mcreater.amcl.util.FileUtils.LinkPath;
import com.mcreater.amcl.util.FileUtils.ZipUtil;
import com.mcreater.amcl.util.J8Utils;
import com.mcreater.amcl.util.JsonUtils;
import com.mcreater.amcl.util.StringUtils;
import com.mcreater.amcl.util.VersionInfo;
import com.mcreater.amcl.util.java.JVMArgs;
import com.mcreater.amcl.util.net.FasterUrls;
import com.mcreater.amcl.util.system.MemoryReader;
import javafx.application.Platform;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintStream;
import java.net.BindException;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Vector;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.BiConsumer;

import static com.mcreater.amcl.download.OriginalDownload.checkAllowState;
import static com.mcreater.amcl.util.FileUtils.OperateUtil.createDirectory;
import static com.mcreater.amcl.util.FileUtils.OperateUtil.createDirectoryDirect;
import static com.mcreater.amcl.util.FileUtils.PathUtil.buildPath;
import static com.mcreater.amcl.util.JsonUtils.GSON_PARSER;

public class LaunchCore {
    public Process process;
    Logger logger = LogManager.getLogger(this.getClass());
    Vector<ConsoleOutputHelper.LogLine> logProperty = new Vector<>();

    Vector<String> argList = new Vector<>();
    private BiConsumer<ImmutablePair<Integer, Integer>, String> updater = (integerIntegerImmutablePair, s) -> {};
    private Runnable failedRunnable = () -> {};
    public void setUpdater(@NotNull BiConsumer<ImmutablePair<Integer, Integer>, String> updater) {
        this.updater = updater;
    }
    public void setFailedRunnable(@NotNull Runnable r) {
        this.failedRunnable = r;
    }
    public Long exitCode;
    public void launch(String java_path, String dir, String version_name, boolean ie, int memory, AbstractUser user, FasterUrls.Servers dlserver, int chunkSize) throws Exception {
        argList.clear();
        if (MemoryReader.getFreeMemory() < (long) memory * 1024 * 1024) {
            memory = (int) (MemoryReader.getFreeMemory() / 1024 / 1204);
        }
        if (user == null) {
            throw new BadUserException();
        }
        updater.accept(new ImmutablePair<>(0, 5), Launcher.languageManager.get("ui.launch._01"));

        argList.add(java_path);

        updater.accept(new ImmutablePair<>(0, 15), Launcher.languageManager.get("ui.launch._02"));
        updater.accept(new ImmutablePair<>(0, 30), Launcher.languageManager.get("ui.fix._01"));

        TaskManager.setUpdater((integer, s) -> updater.accept(new ImmutablePair<>(1, integer), s));
        TaskManager.setFinishRunnable(() -> {
            updater.accept(new ImmutablePair<>(1, 100), "");
            TaskManager.setFinishRunnable(() -> {});
        });
        try {
            MinecraftFixer.fix(chunkSize, dir, version_name, dlserver);
        } catch (IOException e) {
            failedRunnable.run();
            SimpleDialogCreater.exception(e, Launcher.languageManager.get("ui.mainpage.launch.launchFailed.name"));
            return;
        }

        if (!new File(dir).exists()) {
            throw new BadMinecraftDirException();
        }
        File f = new File(LinkPath.link(dir, "versions" + File.separator + version_name));
        if (!f.exists()) {
            throw new BadVersionDirException();
        }
        File json_file = new File(LinkPath.link(f.getPath(), version_name + ".json"));
        File jar_file = new File(LinkPath.link(f.getPath(), version_name + ".jar"));
        if (!json_file.exists() || !jar_file.exists()) {
            throw new BadMainFilesException();
        }
        updater.accept(new ImmutablePair<>(0, 60), Launcher.languageManager.get("ui.launch._03"));
        String json_result = FileUtils.FileStringReader.read(json_file.getPath());
        VersionJsonModel r = GSON_PARSER.fromJson(json_result, VersionJsonModel.class);

        System.out.println(r.javaVersion);

        File libf = new File(LinkPath.link(dir, "libraries"));
        if (!libf.exists()) {
            throw new BadLibDirException();
        }
        Vector<String> libs = new Vector<>();
        Vector<String> natives = new Vector<>();

        int s0 = 0;
        String nativeName = StableMain.getSystem2.run();
        for (LibModel l : r.libraries) {
            if (checkAllowState(l)) {
                if (l.name != null) {
                    if (l.downloads != null) {
                        if (l.downloads.classifiers != null) {
                            if (l.downloads.classifiers.containsKey("natives-osx"))
                                nativeName = nativeName.replace("natives-macos", "natives-osx").replace("-arm64", "");
                            if (l.downloads.classifiers.get(nativeName) != null) {
                                if (new File(LinkPath.link(libf.getPath(), l.downloads.classifiers.get(nativeName).path)).exists()) {
                                    natives.add(LinkPath.link(libf.getPath(), l.downloads.classifiers.get(nativeName).path));
                                    libs.add(LinkPath.link(libf.getPath(), l.downloads.classifiers.get(nativeName).path));
                                }
                            }
                        }
                    }
                    if (l.name.contains(nativeName)) {
                        if (new File(LinkPath.link(libf.getPath(), l.downloads.artifact.get("path"))).exists()) {
                            natives.add(LinkPath.link(libf.getPath(), l.downloads.artifact.get("path")));
                            libs.add(LinkPath.link(libf.getPath(), l.downloads.artifact.get("path")));
                        }
                    }
                    if (l.name.contains("net.minecraftforge:minecraftforge")) {
                        String p = LinkPath.link(libf.getPath(), MavenPathConverter.get(l.name));
                        for (File f1 : new File(p).getParentFile().listFiles()) {
                            if (f1.getPath().endsWith(".jar")) {
                                libs.add(f1.getPath());
                            }
                        }
                    }
                    File st = new File(LinkPath.link(libf.getPath(), MavenPathConverter.get(l.name)));

                    if (st.exists()) {
                        if (!libs.contains(LinkPath.link(libf.getPath(), MavenPathConverter.get(l.name)))) {
                            if (l.name.contains("org.apache.logging.log4j:log4j-api:2.8.1") || l.name.contains("org.apache.logging.log4j:log4j-core:2.8.1")) {
                                if (VersionTypeGetter.get(dir, version_name) == VersionTypeGetter.VersionType.FORGE) {
                                    String local = LinkPath.link(libf.getPath(), MavenPathConverter.get(l.name)).replace("2.8.1", "2.15.0");
                                    createDirectory(local);
                                    String server = FasterUrls.fast(l.downloads.artifact.get("url"), dlserver).replace("2.8.1", "2.15.0");
                                    if (!FileUtils.HashHelper.validateSHA1(new File(local), l.downloads.artifact.get("sha1"))) {
                                        new DownloadTask(server, local, chunkSize).setHash(l.downloads.artifact.get("sha1")).execute();
                                    }
                                    libs.add(local);
                                } else {
                                    libs.add(LinkPath.link(libf.getPath(), MavenPathConverter.get(l.name)));
                                }
                            } else {
                                libs.add(LinkPath.link(libf.getPath(), MavenPathConverter.get(l.name)));
                            }
                        }
                    }
                }
            }
            s0 += 1;
            updater.accept(new ImmutablePair<>(0, 75 + 5 * s0 / r.libraries.size()), Launcher.languageManager.get("ui.launch._04", l.name));
        }
        updater.accept(new ImmutablePair<>(0, 80), Launcher.languageManager.get("ui.launch._05", r.libraries.size()));
        File nativef = new File(LinkPath.link(f.getPath(), version_name + "-natives"));
        if (!nativef.exists()) {
            boolean b = nativef.mkdirs();
            if (!b) {
                throw new BadNativeDirException();
            }
        }
        for (String p : natives) {
            try {
                if (new File(p).exists()) {
                    ZipUtil.unzip(p, nativef.getPath());
                }
            } catch (Exception e) {
                e.printStackTrace();
                throw new BadUnzipException();
            }
            updater.accept(new ImmutablePair<>(0, 85), Launcher.languageManager.get("ui.launch._06"));
        }
        StringBuilder classpath = new StringBuilder();
        for (String s : libs) {
            classpath.append(s).append(File.pathSeparator);
        }
        classpath.append(jar_file);

        argList.add("-Xmn256m");
        argList.add(String.format("-Xmx%dm", memory));
        Vector<String> arguList = new Vector<>();

        if (r.minecraftArguments != null) {
            arguList.addAll(Arrays.asList(r.minecraftArguments.split(" ")));
        } else {
            if (r.arguments != null) {
                if (r.arguments.game != null) {
                    for (Object s : r.arguments.game) {
                        if (s != null) {
                            if (s instanceof String) {
                                arguList.add((String) s);
                            }
                            else {
                                Map<String, Boolean> result = J8Utils.createMap(
                                        String.class, Boolean.class,
                                        "is_demo_user", false,
                                        "has_custom_resolution", true
                                );

                                ArgItemModel model = GSON_PARSER.fromJson(GSON_PARSER.toJson(s), ArgItemModel.class);

                                AtomicBoolean b = new AtomicBoolean(true);
                                model.rules.forEach(rulesModel -> {
                                    int part = 0;
                                    for (Map.Entry<String, Boolean> entry : rulesModel.features.entrySet()) {
                                        if (JsonUtils.JsonProcessors.parseBoolean(result.get(entry.getKey())) == JsonUtils.JsonProcessors.parseBoolean(entry.getValue())) {
                                            part++;
                                        }
                                    }

                                    if (rulesModel.features.size() == part) b.set(false);
                                });

                                if (!b.get()) {
                                    if (model.value instanceof String) {
                                        arguList.add((String) model.value);
                                    }
                                    else if (model.value instanceof List<?>) {
                                        ((List<?>) model.value).forEach(o -> arguList.add(o.toString()));
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        File gamedir = ie ? f : new File(dir);
        String id = "pre-1.6";
        if (r.assetIndex != null) {
            if (r.assetIndex.get("id") != null) {
                id = r.assetIndex.get("id");
            }
        }

        Map<String, String> content = new HashMap<>();
        String old_assets = LinkPath.link(gamedir.getAbsolutePath(), "resources");

        if (Objects.equals(id, "pre-1.6")) {
            try {
                createDirectoryDirect(old_assets);
                String path = LinkPath.link(dir, String.format(buildPath("assets", "indexes", "%s.json"), id));

                String json = FileUtils.FileStringReader.read(path);
                JSONObject object = new JSONObject(json);

                JSONObject objects = object.getJSONObject("objects");
                objects.keySet().forEach(s -> {
                    try {
                        String hash2 = objects.getJSONObject(s).getString("hash");
                        String pathAsset = LinkPath.link(old_assets, s);
                        String hashedAsset = LinkPath.link(dir, String.format(buildPath("assets", "objects", "%s", "%s"), hash2.substring(0, 2), hash2));

                        File target = new File(pathAsset);
                        File origin = new File(hashedAsset);

                        createDirectory(pathAsset);

                        if (!FileUtils.HashHelper.validateSHA1(target, hash2)) {
                            try (FileChannel in = new FileInputStream(origin).getChannel();
                                 FileChannel out = new FileOutputStream(target).getChannel()) {
                                out.transferFrom(in, 0, in.size());
                            }
                        }
                    } catch (Exception e) {
//                        logger.warn("Failed to copy assets", e);
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        content.put("${assets_root}", LinkPath.link(dir, "assets"));
        if (id != null) {
            content.put("${assets_index_name}", id);
        }
        content.put("${auth_player_name}", user.username);
        content.put("${user_type}", "mojang");
        content.put("${version_type}", String.format("%s %s", VersionInfo.launcher_name, VersionInfo.launcher_version));
        content.put("${game_directory}", gamedir.getAbsolutePath());
        content.put("${user_properties}", "{}");
        content.put("${auth_uuid}", user.uuid);
        content.put("${auth_access_token}", user.accessToken);
        content.put("${auth_session}", user.accessToken);
        content.put("${game_assets}", old_assets);
        content.put("${version_name}", String.format("%s %s", VersionInfo.launcher_name, VersionInfo.launcher_version));
        content.put("${resolution_width}", String.valueOf(854));
        content.put("${resolution_height}", String.valueOf(480));

        Vector<String> finalArguList = new Vector<>();

        arguList.forEach(s -> finalArguList.add(StringUtils.ArgReplace.replace(s, content)));

        Vector<String> jvmArguList = new Vector<>();

        if (r.arguments != null && r.arguments.jvm != null) {
            jvmArguList.addAll(J8Utils.createList(
                    JVMArgs.FILE_ENCODING,
                    JVMArgs.MINECRAFT_CLIENT_JAR.replace("${jar_path}", jar_file.getPath()),
                    JVMArgs.UNLOCK_EXPERIMENTAL_OPTIONS,

                    JVMArgs.USE_G1GC,
                    JVMArgs.YOUNG_SIZE_PERCENT,
                    JVMArgs.RESERVE_SIZE_PERCENT,
                    JVMArgs.MAX_GC_PAUSE,
                    JVMArgs.HEAP_REGION_SIZE,
                    JVMArgs.ADAPTIVE_SIZE_POLICY,
                    JVMArgs.STACK_TRACE_FAST_THROW,
                    JVMArgs.DONT_COMPILE_HUGE_METHODS,

                    JVMArgs.IGNORE_INVAILD_CERTIFICATES,
                    JVMArgs.IGNORE_PATCH_DISCREPANCIES,

                    JVMArgs.USE_CODEBASE_ONLY,
                    JVMArgs.TRUST_URL_CODE_BASE,

                    JVMArgs.DISABLE_MSG_LOOPUPS,
                    JVMArgs.INTEL_PERFORMANCE,
                    JVMArgs.STDOUT_ENCODING,
                    JVMArgs.STDERR_ENCODING
            ));
            Map<String, String> jvmContent = J8Utils.createMap(
                    String.class, String.class,
                    "${version_name}", version_name,
                    "${library_directory}", libf.getAbsolutePath(),
                    "${classpath_separator}", File.pathSeparator,
                    "${natives_directory}", nativef.getAbsolutePath(),
                    "${launcher_brand}", VersionInfo.launcher_name,
                    "${launcher_version}", VersionInfo.launcher_version,
                    "${launcher_name}", VersionInfo.launcher_name,
                    "${classpath}", classpath.toString()
            );
            for (Object o : r.arguments.jvm) {
                if (o instanceof String) {
                    jvmArguList.add(StringUtils.ArgReplace.replace(o.toString(), jvmContent));
                }
            }
        } else {
            jvmArguList.addAll(J8Utils.createList(
                    JVMArgs.FILE_ENCODING,
                    JVMArgs.MINECRAFT_CLIENT_JAR.replace("${jar_path}", jar_file.getPath()),
                    JVMArgs.UNLOCK_EXPERIMENTAL_OPTIONS,

                    JVMArgs.USE_G1GC,
                    JVMArgs.YOUNG_SIZE_PERCENT,
                    JVMArgs.RESERVE_SIZE_PERCENT,
                    JVMArgs.MAX_GC_PAUSE,
                    JVMArgs.HEAP_REGION_SIZE,
                    JVMArgs.ADAPTIVE_SIZE_POLICY,
                    JVMArgs.STACK_TRACE_FAST_THROW,
                    JVMArgs.DONT_COMPILE_HUGE_METHODS,

                    JVMArgs.IGNORE_INVAILD_CERTIFICATES,
                    JVMArgs.IGNORE_PATCH_DISCREPANCIES,

                    JVMArgs.USE_CODEBASE_ONLY,
                    JVMArgs.TRUST_URL_CODE_BASE,

                    JVMArgs.DISABLE_MSG_LOOPUPS,
                    JVMArgs.INTEL_PERFORMANCE,
                    JVMArgs.JAVA_LIBRARY_PATH.replace("${native_path}", nativef.getPath()),
                    JVMArgs.MINECRAFT_LAUNCHER_BRAND.replace("${launcher_brand}", VersionInfo.launcher_name),
                    JVMArgs.MINECRAFT_LAUNCHER_VERSION.replace("${launcher_version}", VersionInfo.launcher_version),
                    JVMArgs.STDOUT_ENCODING,
                    JVMArgs.STDERR_ENCODING,
                    "-cp",
                    classpath.toString()
            ));
        }

        argList.addAll(jvmArguList);

        try {
            InputStream stream = ResourceGetter.get("authlib-injector.jar");
            File target = new File("authlib-injector.jar");

            OutputStream output = Files.newOutputStream(target.toPath());
            byte[] buf = new byte[1024];
            int bytesRead;
            while ((bytesRead = stream.read(buf)) > 0) {
                output.write(buf, 0, bytesRead);
            }
            stream.close();
            output.close();

            int port = 2;
            LocalYggdrasilServer server = null;
            if (user instanceof OffLineUser) {
                OffLineUser temp_user = (OffLineUser) user;
                if (temp_user.hasCustomSkin()) {
                    while (true) {
                        try {
                            updater.accept(new ImmutablePair<>(0, 90), Launcher.languageManager.get("ui.userselectpage.launch.tryOpenServer", port));
                            server = new LocalYggdrasilServer(port);
                            server.getPlayers().add(temp_user.toYggdrasilPlayer());
                            server.start();
                            break;
                        } catch (BindException e) {
                            port += 1;
                            if (port > 65536) {
                                throw new IOException("server cannot open local server");
                            }
                        }
                    }
                    argList.add("-javaagent:" + target.getAbsolutePath() + "=http://localhost:" + port);
                    argList.add("-Dauthlibinjector.side=client");
                }
            }

            argList.add(r.mainClass);
            argList.addAll(finalArguList);

            updater.accept(new ImmutablePair<>(0, 90), Launcher.languageManager.get("ui.launch._07"));

            final String[] rs = {""};
            argList.forEach(s -> rs[0] += "\"" + s + "\" ");
            logger.info(String.format("Getted Command Line : %s", rs[0]));

            updater.accept(new ImmutablePair<>(0, 95), Launcher.languageManager.get("ui.launch._08"));
            try {
                process = Runtime.getRuntime().exec(argList.toArray(new String[0]), null, new File(dir));
            } catch (IOException e) {
                throw new ProcessException(e);
            }
            new Thread(() -> {
                while (EnumWindow.enumWindowEnabled() && process.isAlive()) {
                    if (EnumWindow.getTaskPidContains(J8Utils.getProcessPid(process))) {
                        logger.info("Window Showed");
                        failedRunnable.run();
                        break;
                    }
                }
            }).start();
            LocalYggdrasilServer finalServer = server;
            new Thread(() -> {
                while (true) {
                    try {
                        if (process != null) {
                            exitCode = (long) process.exitValue();
                        } else {
                            exitCode = 1L;
                        }
                        MainPage.tryToRemoveLaunch(this);
                        if (finalServer != null) finalServer.stop();
                        Platform.runLater(() -> MainPage.check(this));
                        break;
                    } catch (IllegalThreadStateException ignored) {
                    }
                }
            }).start();
            readProcessOutput(process);
        } catch (Exception e) {
            throw new ProcessException(e);
        }
    }
    private void readProcessOutput(final Process process) {
        if (process != null) {
            new Thread(() -> read(process.getInputStream(), System.out)).start();
            new Thread(() -> read(process.getErrorStream(), System.err)).start();
        }
    }
    private void read(InputStream inputStream, PrintStream out) {
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, Charset.forName("GBK")));
            String line;
            while ((line = reader.readLine()) != null) {
                logProperty.add(ConsoleOutputHelper.toLogLine(line, out));
                ConsoleOutputHelper.printLog(line, out);
                if (line.contains("Backend library: LWJGL version") ||
                        line.contains("LWJGL Version") ||
                        line.contains("Turning of ImageIO disk-caching") ||
                        line.contains("Loading current icons for window from:")) {
                    if (!EnumWindow.enumWindowEnabled()) failedRunnable.run();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                inputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    public void stop_process() {
        if (process != null){
            process.destroy();
        }
    }
}
