package com.mcreater.amcl.game.launch;

import com.google.gson.Gson;
import com.google.gson.internal.LinkedTreeMap;
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
import com.mcreater.amcl.model.LibModel;
import com.mcreater.amcl.model.VersionJsonModel;
import com.mcreater.amcl.nativeInterface.EnumWindow;
import com.mcreater.amcl.nativeInterface.ResourceGetter;
import com.mcreater.amcl.pages.MainPage;
import com.mcreater.amcl.pages.dialogs.commons.SimpleDialogCreater;
import com.mcreater.amcl.patcher.ClassPathInjector;
import com.mcreater.amcl.tasks.DownloadTask;
import com.mcreater.amcl.tasks.taskmanager.TaskManager;
import com.mcreater.amcl.util.FileUtils;
import com.mcreater.amcl.util.FileUtils.LinkPath;
import com.mcreater.amcl.util.FileUtils.ZipUtil;
import com.mcreater.amcl.util.J8Utils;
import com.mcreater.amcl.util.LogLineDetecter;
import com.mcreater.amcl.util.StringUtils;
import com.mcreater.amcl.util.VersionInfo;
import com.mcreater.amcl.util.java.JVMArgs;
import com.mcreater.amcl.util.net.FasterUrls;
import com.mcreater.amcl.util.system.MemoryReader;
import javafx.application.Platform;
import javafx.beans.property.SimpleObjectProperty;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.BindException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Vector;
import java.util.function.BiConsumer;

public class Launch {
    public Process p;
    Logger logger = LogManager.getLogger(this.getClass());
    SimpleObjectProperty<StringBuilder> logProperty = new SimpleObjectProperty<>(new StringBuilder());

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
    public void launch(String java_path, String dir, String version_name, boolean ie, int memory, AbstractUser user, FasterUrls.Servers dlserver) throws Exception {
        argList.clear();
        if (MemoryReader.getFreeMemory() < (long) memory * 1024 * 1024){
            memory = (int) (MemoryReader.getFreeMemory() / 1024 / 1204);
        }
        if (user == null){
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
            MinecraftFixer.fix(Launcher.configReader.configModel.downloadChunkSize, dir, version_name, dlserver);
        }
        catch (IOException e){
            failedRunnable.run();
            SimpleDialogCreater.exception(e, Launcher.languageManager.get("ui.mainpage.launch.launchFailed.name"));
            return;
        }

        if (!new File(dir).exists()){
            throw new BadMinecraftDirException();
        }
        File f = new File(LinkPath.link(dir, "versions" + File.separator + version_name));
        if (!f.exists()){
            throw new BadVersionDirException();
        }
        File json_file = new File(LinkPath.link(f.getPath(),version_name + ".json"));
        File jar_file = new File(LinkPath.link(f.getPath(),version_name + ".jar"));
        if (!json_file.exists() || !jar_file.exists()){
            throw new BadMainFilesException();
        }
        updater.accept(new ImmutablePair<>(0, 60), Launcher.languageManager.get("ui.launch._03"));
        String json_result = FileUtils.FileStringReader.read(json_file.getPath());
        Gson g = new Gson();
        VersionJsonModel r = g.fromJson(json_result, VersionJsonModel.class);

        File libf = new File(LinkPath.link(dir, "libraries"));
        if (!libf.exists()){
            throw new BadLibDirException();
        }
        Vector<String> libs = new Vector<>();
        Vector<String> natives = new Vector<>();
        int s0 = 0;
        boolean has_322 = false;
        boolean has_321 = false;
        for (LibModel model1 : r.libraries) {
            try {
                if (model1.downloads.artifact != null) {
                    String u = model1.downloads.artifact.get("url");
                    if (!has_321 && u.contains("3.2.1")) {
                        has_321 = true;
                    }
                    if (!has_322 && u.contains("3.2.2")) {
                        has_322 = true;
                    }
                }
            }
            catch (Exception ignored){

            }
        }
        String nativeName = StableMain.getSystem2.run();
        for (LibModel l : r.libraries) {
            if (l.name != null) {
                if (!(l.name.contains("3.2.1") && has_322)) {
                    if (l.downloads != null) {
                        if (l.downloads.classifiers != null) {
                            if (l.downloads.classifiers.containsKey("natives-osx")) nativeName = nativeName.replace("natives-macos", "natives-osx").replace("-arm64", "");
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
                    if (new File(LinkPath.link(libf.getPath(), MavenPathConverter.get(l.name))).exists()) {
                        if (!libs.contains(LinkPath.link(libf.getPath(), MavenPathConverter.get(l.name)))) {
                            if (l.name.contains("org.apache.logging.log4j:log4j-api:2.8.1") || l.name.contains("org.apache.logging.log4j:log4j-core:2.8.1")) {
                                if (VersionTypeGetter.get(dir, version_name) == VersionTypeGetter.VersionType.FORGE) {
                                    String local = LinkPath.link(libf.getPath(), MavenPathConverter.get(l.name)).replace("2.8.1", "2.15.0");
                                    new File(StringUtils.GetFileBaseDir.get(local)).mkdirs();
                                    String server = FasterUrls.fast(l.downloads.artifact.get("url"), dlserver).replace("2.8.1", "2.15.0");
                                    if (!Objects.equals(l.downloads.artifact.get("sha1"), FileUtils.HashHelper.getFileSHA1(new File(local)))) {
                                        new DownloadTask(server, local, 1024).setHash(l.downloads.artifact.get("sha1")).execute();
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
            updater.accept(new ImmutablePair<>(0, 75 + 5 * s0 / r.libraries.size()), String.format(Launcher.languageManager.get("ui.launch._04"), l.name));
        }
        updater.accept(new ImmutablePair<>(0, 80), String.format(Launcher.languageManager.get("ui.launch._05"), r.libraries.size()));
        File nativef = new File(LinkPath.link(f.getPath(),version_name + "-natives"));
        if (!nativef.exists()){
            boolean b = nativef.mkdirs();
            if (!b){
                throw new BadNativeDirException();
            }
        }
        for (String p : natives){
            try {
                if (new File(p).exists()) {
                    ZipUtil.unzip(p, nativef.getPath());
                }
            }
            catch (Exception e){
                e.printStackTrace();
                throw new BadUnzipException();
            }
            updater.accept(new ImmutablePair<>(0, 85), Launcher.languageManager.get("ui.launch._06"));
        }
        StringBuilder classpath = new StringBuilder("");
        for (String s : libs){
            classpath.append(s).append(File.pathSeparator);
        }
        classpath.append(jar_file);

        argList.add("-Xmn256m");
        argList.add("-Xmx" + memory + "m");
        Vector<String> arguList = new Vector<>();

        if (r.minecraftArguments != null) {
            arguList.addAll(Arrays.asList(r.minecraftArguments.split(" ")));
        }
        else {
            if (r.arguments != null){
                if (r.arguments.game != null){
                    for (Object s : r.arguments.game){
                        if (s != null) {
                            try {
                                arguList.add((String) s);
                            } catch (ClassCastException e) {
                                LinkedTreeMap ltm = (LinkedTreeMap) s;
                                try {
                                    for (String s1 : (ArrayList<String>) ltm.get("value")) {
                                        if (!s1.contains("demo")) {
                                            arguList.add(s1);
                                        }
                                    }
                                }
                                catch (ClassCastException e1){
                                    if (!((String) ltm.get("value")).contains("demo")) {
                                        arguList.add((String) ltm.get("value"));
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        File gamedir;
        if (ie) {
            gamedir = f;
        }
        else {
            gamedir = new File(dir);
        }

        Map<String, String> content = new HashMap<>();
        content.put("${assets_root}", LinkPath.link(dir, "assets"));
        if (r.assetIndex != null) {
            if (r.assetIndex.get("id") != null) {
                content.put("${assets_index_name}", r.assetIndex.get("id"));
            }
        }
        content.put("${auth_player_name}", user.username);
        content.put("${user_type}","mojang");
        content.put("${version_type}", String.format("%s %s", VersionInfo.launcher_name, VersionInfo.launcher_version));
        content.put("${game_directory}", gamedir.getAbsolutePath());
        content.put("${user_properties}", "{}");
        content.put("${auth_uuid}", user.uuid);
        content.put("${auth_access_token}", user.accessToken);
        content.put("${auth_session}", user.accessToken);
        content.put("${game_assets}", LinkPath.link(dir, "assets"));
        content.put("${version_name}", String.format("%s %s", VersionInfo.launcher_name, VersionInfo.launcher_version));
        content.put("${resolution_width}",String.valueOf(854));
        content.put("${resolution_height}",String.valueOf(480));

        Vector<String> finalArguList = new Vector<>();

        for (String s : arguList) {
            boolean isRepd = false;
            for (Map.Entry<String, String> entry : content.entrySet()) {
                if (s.contains(entry.getKey())) {
                    finalArguList.add(s.replace(entry.getKey(), entry.getValue()));
                    isRepd = true;
                    break;
                }
            }
            if (!isRepd) finalArguList.add(s);
        }

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
            for (Object o : r.arguments.jvm) {
                if (o instanceof String) {
                    String te = (String) o;
                    te = te.replace("${version_name}", version_name)
                            .replace("${library_directory}", libf.getAbsolutePath().endsWith("/") ? libf.getAbsolutePath().substring(0, libf.getAbsolutePath().length()) : libf.getAbsolutePath())
                            .replace("${classpath_separator}", File.pathSeparator)
                            .replace("${natives_directory}", nativef.getAbsolutePath())
                            .replace("${launcher_brand}", VersionInfo.launcher_name).replace("${launcher_version}", VersionInfo.launcher_version)
                            .replace("${launcher_name}", VersionInfo.launcher_name)
                            .replace("${classpath}", classpath.toString());
                    jvmArguList.add(te);
                }
            }
        }
        else {
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

            String authLibInjectorArg = "";
            int port = 2;
            LocalYggdrasilServer server = null;
            if (user instanceof OffLineUser){
                OffLineUser temp_user = (OffLineUser) user;
                if (temp_user.skinUseable() || temp_user.capeUseable()){
                    File skin = null;
                    File cape = null;
                    if (temp_user.skinUseable()) skin = new File(temp_user.skin);
                    if (temp_user.capeUseable()) cape = new File(temp_user.cape);

                    while (true){
                        try {
                            updater.accept(new ImmutablePair<>(0, 90), String.format(Launcher.languageManager.get("ui.userselectpage.launch.tryOpenServer"), port));
                            server = new LocalYggdrasilServer(port);
                            server.setCurrent_player(new LocalYggdrasilServer.Player(
                                    user.uuid,
                                    user.username,
                                    skin,
                                    cape,
                                    temp_user.is_slim
                            ));
                            server.start();
                            break;
                        }
                        catch (BindException e){
                            port += 1;
                            if (port > 65536){
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

            argList.forEach(s -> rs[0] += s + " ");

            logger.info(String.format("Getted Command Line : %s", rs[0]));
//            Thread lT = new Thread(() -> {
//                while (true) {
//                    if (!MainPage.launchDialog.l.getText().equals(Launcher.languageManager.get("ui.launch._08"))) MainPage.launchDialog.setV(0, 95, Launcher.languageManager.get("ui.launch._08"));
//
//                }
//            });
//            lT.start();
            updater.accept(new ImmutablePair<>(0, 95), Launcher.languageManager.get("ui.launch._08"));
            try {
                p = Runtime.getRuntime().exec(argList.toArray(new String[0]), null, new File(dir));
            }
            catch (IOException e){
                throw new ProcessException();
            }
            new Thread(() -> {
                while (true) {
                    if (!p.isAlive()) break;
                    if (EnumWindow.getTaskPID().contains(J8Utils.getProcessPid(p))) {
                        MainPage.logger.info("Window Showed");
                        failedRunnable.run();
                        break;
                    }
                }
            }).start();
            LocalYggdrasilServer finalServer = server;
            new Thread(() -> {
                while (true){
                    try {
                        if (p != null) {
                            exitCode = (long) p.exitValue();
                        }
                        else {
                            exitCode = 1L;
                        }
                        MainPage.tryToRemoveLaunch(this);
                        if (finalServer != null) finalServer.stop();
                        Platform.runLater(() -> MainPage.check(this));
                        break;
                    }
                    catch (IllegalThreadStateException ignored){}
                }
            }).start();
            readProcessOutput(p);
        } catch (Exception e){
            throw new ProcessException(e);
        }
    }
    public void readProcessOutput(final Process process) {
        if (process != null) {
            new Thread(() -> read(process.getInputStream(), System.out)).start();
            new Thread(() -> read(process.getErrorStream(), System.err)).start();
        }
    }
    public void read(InputStream inputStream, PrintStream out) {
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, Charset.forName("GBK")));
            String line;
            while ((line = reader.readLine()) != null) {
                logProperty.get().append(line);
                LogLineDetecter.printLog(line, out);
                if (line.contains("Backend library: LWJGL version") ||
                line.contains("LWJGL Version") ||
                line.contains("Turning of ImageIO disk-caching") ||
                line.contains("Loading current icons for window from:")){
                    if (ClassPathInjector.version <= 8) failedRunnable.run();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            StringWriter writer = new StringWriter();
            PrintWriter print = new PrintWriter(writer);
            e.printStackTrace(print);
            logProperty.get().append(writer.toString());
            print.close();
        } finally {
            try {
                inputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    public static void loadOut(InputStream stream, PrintStream out){
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(stream, Charset.forName("GBK")));
            String line;
            while ((line = reader.readLine()) != null) {
                LogLineDetecter.printLog(line, out);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                stream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    public static String ret(InputStream inputStream){
        StringBuilder f = new StringBuilder();
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
            String line;
            while ((line = reader.readLine()) != null) {
                f.append(line).append("\n");
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
        return f.toString();
    }
    public void stop_process(){
        if (p != null){
            p.destroy();
        }
    }
}
