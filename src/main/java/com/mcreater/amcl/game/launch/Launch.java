package com.mcreater.amcl.game.launch;

import com.google.gson.Gson;
import com.google.gson.internal.LinkedTreeMap;
import com.mcreater.amcl.Launcher;
import com.mcreater.amcl.api.auth.YggdrasilServer;
import com.mcreater.amcl.api.auth.users.AbstractUser;
import com.mcreater.amcl.nativeInterface.ResourceGetter;
import com.mcreater.amcl.pages.dialogs.FastInfomation;
import com.mcreater.amcl.tasks.taskmanager.TaskManager;
import com.mcreater.amcl.tasks.DownloadTask;
import com.mcreater.amcl.exceptions.*;
import com.mcreater.amcl.game.MavenPathConverter;
import com.mcreater.amcl.game.versionTypeGetter;
import com.mcreater.amcl.model.LibModel;
import com.mcreater.amcl.model.VersionJsonModel;
import com.mcreater.amcl.nativeInterface.EnumWindow;
import com.mcreater.amcl.pages.MainPage;
import com.mcreater.amcl.audio.BGMManager;
import com.mcreater.amcl.pages.dialogs.ProcessDialog;
import com.mcreater.amcl.util.*;
import com.mcreater.amcl.util.FileUtils;
import com.mcreater.amcl.util.FileUtils.*;
import com.mcreater.amcl.util.net.FasterUrls;
import javafx.application.Platform;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.*;
import java.nio.channels.FileChannel;
import java.nio.file.Files;
import java.util.*;
import java.util.concurrent.CountDownLatch;

public class Launch {
    String java;
    String jvm;
    String mem;
    String mainClass;
    String arguments;
    String forge_jvm;
    String forgevm;
    public Process p;
    ProcessDialog d;
    Logger logger = LogManager.getLogger(this.getClass());
    public void launch(String java_path, String dir, String version_name, boolean ie, int m, AbstractUser user) throws Exception {
        if (user == null){
            throw new Exception("user is null");
        }
        TaskManager.bind(MainPage.d, 2);
        MainPage.d.Create();
        MainPage.d.setV(0, 5, Launcher.languageManager.get("ui.launch._01"));
        java = java_path;
        MainPage.d.setV(0, 10, Launcher.languageManager.get("ui.launch._02"));
        MainPage.d.setV(2, 0, Launcher.languageManager.get("ui.fix._01"));
        try {
            MinecraftFixer.fix(Launcher.configReader.configModel.fastDownload, Launcher.configReader.configModel.downloadChunkSize, dir, version_name);
        }
        catch (IOException e){
            Platform.runLater(() -> {
                MainPage.d.close();
                FastInfomation.create(Launcher.languageManager.get("ui.mainpage.launch.launchFailed.name"), Launcher.languageManager.get("ui.mainpage.launch.launchFailed.Headcontent"), e.toString());
            });
        }
        Platform.runLater(() -> MainPage.d.setV(2, 100));

        if (!new File(dir).exists()){
            throw new BadMinecraftDirException();
        }
        File f = new File(LinkPath.link(dir, "versions\\" + version_name));
        if (!f.exists()){
            throw new BadVersionDirException();
        }
        File json_file = new File(LinkPath.link(f.getPath(),version_name + ".json"));
        File jar_file = new File(LinkPath.link(f.getPath(),version_name + ".jar"));
        if (!json_file.exists() || !jar_file.exists()){
            throw new BadMainFilesException();
        }
        MainPage.d.setV(0, 75, Launcher.languageManager.get("ui.launch._03"));
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
        for (LibModel l : r.libraries) {
            if (l.name != null) {
                if (l.downloads != null) {
                    if (l.downloads.classifiers != null) {
                        if (l.downloads.classifiers.get("natives-windows") != null) {
                            if (new File(LinkPath.link(libf.getPath(), l.downloads.classifiers.get("natives-windows").path)).exists()) {
                                natives.add(LinkPath.link(libf.getPath(), l.downloads.classifiers.get("natives-windows").path));
                                libs.add(LinkPath.link(libf.getPath(), l.downloads.classifiers.get("natives-windows").path));
                            }
                        }
                    }
                }
                if (l.name.contains("natives-windows")) {
                    if (new File(LinkPath.link(libf.getPath(), l.downloads.artifact.get("path"))).exists()) {
                        natives.add(LinkPath.link(libf.getPath(), l.downloads.artifact.get("path")));
                        libs.add(LinkPath.link(libf.getPath(), l.downloads.artifact.get("path")));
                    }
                }
                if (l.name.contains("net.minecraftforge:minecraftforge")){
                    String p = LinkPath.link(libf.getPath(), MavenPathConverter.get(l.name));
                    System.out.println(new File(p).getParentFile());
                    for (File f1 : new File(p).getParentFile().listFiles()){
                        if (f1.getPath().endsWith(".jar")){
                            libs.add(f1.getPath());
                        }
                    }
                }
                if (new File(LinkPath.link(libf.getPath(), MavenPathConverter.get(l.name))).exists()) {
                    if (!libs.contains(LinkPath.link(libf.getPath(), MavenPathConverter.get(l.name)))) {
                        if (l.name.contains("org.apache.logging.log4j:log4j-api:2.8.1") || l.name.contains("org.apache.logging.log4j:log4j-core:2.8.1")){
                            if (versionTypeGetter.get(dir, version_name).contains("forge")) {
                                String local = LinkPath.link(libf.getPath(), MavenPathConverter.get(l.name)).replace("2.8.1", "2.15.0");
                                new File(StringUtils.GetFileBaseDir.get(local)).mkdirs();
                                String server = FasterUrls.fast(l.downloads.artifact.get("url"), FasterUrls.Servers.valueOf(Launcher.configReader.configModel.downloadServer)).replace("2.8.1", "2.15.0");
                                if (!Objects.equals(l.downloads.artifact.get("sha1"), FileUtils.HashHelper.getFileSHA1(new File(local)))) {
                                    new DownloadTask(server, local, 1024).setHash(l.downloads.artifact.get("sha1")).execute();
                                }
                                libs.add(local);
                            }
                            else{
                                libs.add(LinkPath.link(libf.getPath(), MavenPathConverter.get(l.name)));
                            }
                        }
                        else {
                            libs.add(LinkPath.link(libf.getPath(), MavenPathConverter.get(l.name)));
                        }
                    }
                }
            }
            s0 += 1;
            MainPage.d.setV(0, 75 + 5 * s0 / r.libraries.size(), String.format(Launcher.languageManager.get("ui.launch._04"), l.name));
            MainPage.d.setV(1, (int) ((double) s0 / r.libraries.size() * 100));
        }
        MainPage.d.setV(1, 100);
        MainPage.d.setV(0, 80, String.format(Launcher.languageManager.get("ui.launch._05"), r.libraries.size()));
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
            MainPage.d.setV(0, 85, Launcher.languageManager.get("ui.launch._06"));
        }
        StringBuilder classpath = new StringBuilder("-cp \"");
        for (String s : libs){
            classpath.append(s).append(File.pathSeparator);
        }
        classpath.append(jar_file).append("\"");

        mem = "-Xmn256m -Xmx" + m + "m";
        mainClass = r.mainClass;
        StringBuilder agm = new StringBuilder();
        if (r.minecraftArguments != null) {
            arguments = r.minecraftArguments;
            if (r.arguments != null){
                if (r.arguments.game != null){
                    arguments += " ";
                    for (Object s : r.arguments.game){
                        try{
                            arguments += s + (" ");
                        }
                        catch (ClassCastException ignored){
                        }
                    }
                }
            }
        }
        else{
            for (Object s : r.arguments.game){
                if (s != null) {
                    try {
                        agm.append((String) s).append(" ");
                    } catch (ClassCastException e) {
                        LinkedTreeMap ltm = (LinkedTreeMap) s;
                        try {
                            for (String s1 : (ArrayList<String>) ltm.get("value")) {
                                if (!s1.contains("demo")) {
                                    agm.append(s1).append(" ");
                                }
                            }
                        }
                        catch (ClassCastException e1){
                            if (!((String) ltm.get("value")).contains("demo")) {
                                agm.append((String) ltm.get("value")).append(" ");
                            }
                        }
                    }
                }
            }
            if (Objects.equals(arguments, "")) {
                arguments += " ";
            }
            arguments += String.valueOf(agm);
        }
        arguments = arguments.replace("${assets_root}",LinkPath.link(dir, "assets"));
        if (r.assetIndex != null) {
            if (r.assetIndex.get("id") != null) {
                arguments = arguments.replace("${assets_index_name}", r.assetIndex.get("id"));
            }
        }
        arguments = arguments.replace("${auth_player_name}","\""+user.username+"\"");
        arguments = arguments.replace("${user_type}","mojang");
        arguments = arguments.replace("${version_type}", String.format("\"%s %s\"", VersionInfo.launcher_name, VersionInfo.launcher_version));
        arguments = arguments.replace("${resolution_width}","854");
        arguments = arguments.replace("${resolution_height}","480");
        File gamedir;
        if (!ie) {
            gamedir = new File(dir);
        }
        else{
            gamedir = f;
        }
        arguments = arguments.replace("${game_directory}", String.format("\"%s\"", gamedir.getPath()));
        arguments = arguments.replace("${user_properties}","{}");
        arguments = arguments.replace("${auth_uuid}",user.uuid);
        arguments = arguments.replace("${auth_access_token}",user.accessToken);
        arguments = arguments.replace("${auth_session}","8888888888888");
        arguments = arguments.replace("${game_assets}",LinkPath.link(dir, "assets"));
        arguments = arguments.replace("${version_name}", String.format("\"%s %s\"", VersionInfo.launcher_name, VersionInfo.launcher_version));

        jvm = "-Dfile.encoding=GB18030 -Dminecraft.client.jar=${jar_path} -XX:+UnlockExperimentalVMOptions -XX:+UseG1GC -XX:G1NewSizePercent=20 -XX:G1ReservePercent=20 -XX:MaxGCPauseMillis=50 -XX:G1HeapRegionSize=16m -XX:-UseAdaptiveSizePolicy -XX:-OmitStackTraceInFastThrow -XX:-DontCompileHugeMethods -Dfml.ignoreInvalidMinecraftCertificates=true -Dfml.ignorePatchDiscrepancies=true -Djava.rmi.server.useCodebaseOnly=true -Dcom.sun.jndi.rmi.object.trustURLCodebase=false -Dcom.sun.jndi.cosnaming.object.trustURLCodebase=false -Dlog4j2.formatMsgNoLookups=true -XX:HeapDumpPath=MojangTricksIntelDriversForPerformance_javaw.exe_minecraft.exe.heapdump -Djava.library.path=${native_path} -Dminecraft.launcher.brand=${launcher_brand} -Dminecraft.launcher.version=${launcher_version}";
        jvm = jvm.replace("${jar_path}", String.format("\"%s\"", jar_file.getPath()));
        jvm = jvm.replace("${native_path}",String.format("\"%s\"", nativef.getPath()));
        jvm = jvm.replace("${launcher_brand}", VersionInfo.launcher_name);
        jvm = jvm.replace("${launcher_version}", VersionInfo.launcher_version);

        if (r.arguments != null){
            if (r.arguments.jvm != null){
                for (Object o : r.arguments.jvm) {
                    try {
                        String gt = (String) o;
                        if (!(gt.contains("java.library.path") ||
                              gt.contains("minecraft.launcher.brand") ||
                              gt.contains("minecraft.launcher.version") ||
                              gt.contains("cp") ||
                              gt.contains("classpath")) ||
                                gt.contains("java.base/java.util.jar") ||
                                 gt.contains("java.base/sun.security.util") ||
                                 gt.contains("jdk.naming.dns/com.sun.jndi.dns"))
                        {
                            forge_jvm += gt + " ";

                        }
                    } catch (Exception ignored) {
                    }
                }
                if (forge_jvm != null) {
                    forgevm = forge_jvm;
                    forgevm = forgevm.replace("${version_name}", version_name);
                    forgevm = forgevm.replace("${primary_jar_name}", version_name + ".jar");
                    forgevm = forgevm.replace("${library_directory}", libf.getPath());
                    StringBuilder forge_libs = new StringBuilder();
                    for (LibModel l : r.libraries) {
                        if ((l.name.contains("cpw.mods") || l.name.contains("org.ow2")) && !l.name.contains("modlauncher")) {
                            forge_libs.append(LinkPath.link(libf.getPath(), MavenPathConverter.get(l.name).replace("\\", "/"))).append(";");
                        }
                    }
                    forge_libs = new StringBuilder(forge_libs.substring(0, forge_libs.length() - 1));
                    forge_libs.append("\"");
                    forgevm = forgevm.replace("-p ", "-p " + "\"" + forge_libs);
                    forgevm = forgevm.replace("--add-modules ALL-MODULE-PATH", " --add-modules ALL-MODULE-PATH");
                    if (forgevm.contains("-add-opens --add-exports")){
                        forgevm += "--add-opens java.base/java.lang.invoke=cpw.mods.securejarhandler ";
                    }
                    forgevm = forgevm.replace("--add-opens --add-exports", "--add-exports");
                }
                else{
                    forge_jvm = "";
                }
            }
        }

        try {
            InputStream stream = new ResourceGetter().get("authlib-injector.jar");
            File target = new File("authlib-injector.jar");

            OutputStream output = Files.newOutputStream(target.toPath());
            byte[] buf = new byte[1024];
            int bytesRead;
            while ((bytesRead = stream.read(buf)) > 0) {
                output.write(buf, 0, bytesRead);
            }
            stream.close();
            output.close();

            String authLibInjectorArg = "-javaagent:" + target.getAbsolutePath() + "=http://localhost:" + YggdrasilServer.DEFAULT_PORT + " -Dauthlibinjector.side=client";
            authLibInjectorArg = "";

            String command = StringUtils.LinkCommands.link(java, jvm, authLibInjectorArg,String.valueOf(classpath), mem, forgevm,mainClass.replace(" ",""), arguments);
            MainPage.d.setV(0, 90, Launcher.languageManager.get("ui.launch._07"));
            command = command.replace("null","");
            logger.info(String.format("Getted Command Line : %s", command));
            MainPage.exit_code = null;
            MainPage.cleanLog();
            MainPage.d.setV(0, 95, Launcher.languageManager.get("ui.launch._08"));
            try {
                p = Runtime.getRuntime().exec(command, null, new File(dir));
            }
            catch (IOException e){
                MainPage.d.close();
            }
            new Thread(() -> {
                while (true) {
                    try {
                        if (EnumWindow.getTaskPID().contains(J8Utils.getProcessPid(p))) {
                            BGMManager.stop();
                            MainPage.logger.info("Window Showed");
                            MainPage.d.close();
                            break;
                        }
                    }
                    catch (Exception ignored){

                    }
                }
            }).start();
            MainPage.minecraft_running = true;
            MainPage.stop.setDisable(false);
            new Thread(() -> {
                while (true){
                    try {
                        if (p != null) {
                            MainPage.exit_code = (long) p.exitValue();
                        }
                        else {
                            MainPage.exit_code = 1L;
                        }
                        MainPage.minecraft_running = false;
                        Platform.runLater(MainPage::check);
                        MainPage.launchButton.setDisable(false);
                        ChangeDir.changeToDefault();
                        break;
                    }
                    catch (IllegalThreadStateException e){
                        if (!MainPage.minecraft_running) {
                            MainPage.minecraft_running = true;
                        }
                    }
                }
            }).start();
            readProcessOutput(p);
        } catch (Exception e){
            e.printStackTrace();
        }
    }
    public static void readProcessOutput(final Process process) {
        if (process != null) {
            read(process.getInputStream(), System.out);
            read(process.getErrorStream(), System.err);
        }
    }
    public static void read(InputStream inputStream, PrintStream out) {
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
            String line;
            while ((line = reader.readLine()) != null) {
                MainPage.addLog(line);
                out.println(line);
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
    public static String ret(InputStream inputStream){
        StringBuilder f = new StringBuilder();
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
            String line;
            while ((line = reader.readLine()) != null) {
                f.append(line).append("\n");
                System.out.println(line);
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
