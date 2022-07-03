package com.mcreater.amcl.game.launch;

import com.google.gson.Gson;
import com.google.gson.internal.LinkedTreeMap;
import com.mcreater.amcl.Application;
import com.mcreater.amcl.exceptions.*;
import com.mcreater.amcl.game.getPath;
import com.mcreater.amcl.model.LibModel;
import com.mcreater.amcl.model.VersionJsonModel;
import com.mcreater.amcl.nativeInterface.EnumWindow;
import com.mcreater.amcl.pages.MainPage;
import com.mcreater.amcl.pages.dialogs.ProcessDialog;
import com.mcreater.amcl.util.*;
import javafx.application.Platform;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.*;
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
    public void launch(String java_path, String dir, String version_name, boolean ie, int m) throws IllegalStateException, InterruptedException, LaunchException {
        MainPage.d.Create();
        MainPage.d.setV(0, 5, Application.languageManager.get("ui.launch._01"));
        java = java_path;
        MainPage.d.setV(0, 10, Application.languageManager.get("ui.launch._02"));

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
        MainPage.d.setV(0, 75, Application.languageManager.get("ui.launch._03"));
        String json_result = FileStringReader.read(json_file.getPath());
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
                if (new File(LinkPath.link(libf.getPath(), getPath.get(l.name))).exists()) {
                    if (!libs.contains(LinkPath.link(libf.getPath(), getPath.get(l.name)))) {
                        libs.add(LinkPath.link(libf.getPath(), getPath.get(l.name)));
                    }
                }
            }
            s0 += 1;
            MainPage.d.setV(0, 75 + 5 * s0 / r.libraries.size(), String.format(Application.languageManager.get("ui.launch._04"), l.name));
            MainPage.d.setV(1, (int) ((double) s0 / r.libraries.size() * 100));
        }
        MainPage.d.setV(1, 100);
        MainPage.d.setV(0, 80, String.format(Application.languageManager.get("ui.launch._05"), r.libraries.size()));
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
            MainPage.d.setV(0, 85, Application.languageManager.get("ui.launch._06"));
        }
        StringBuilder classpath = new StringBuilder("-cp \"");
        for (String s : libs){
            classpath.append(s).append(";");
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
        arguments = arguments.replace("${auth_player_name}","123");
        arguments = arguments.replace("${user_type}","mojang");
        arguments = arguments.replace("${version_type}", String.format("\"%s %s\"", Vars.launcher_name, Vars.launcher_version));
        arguments = arguments.replace("${resolution_width}","854");
        arguments = arguments.replace("${resolution_height}","480");
        File gamedir;
        if (!ie) {
            gamedir = new File(dir);
        }
        else{
            gamedir = f;
        }
        arguments = arguments.replace("${game_directory}", gamedir.getPath());
        arguments = arguments.replace("${user_properties}","{}");
        arguments = arguments.replace("${auth_uuid}","8".repeat(18));
        arguments = arguments.replace("${auth_access_token}","8".repeat(18));
        arguments = arguments.replace("${auth_session}","8".repeat(18));
        arguments = arguments.replace("${game_assets}",LinkPath.link(dir, "assets"));

        jvm = "-Dfile.encoding=GB18030 -Dminecraft.client.jar=${jar_path} -XX:+UnlockExperimentalVMOptions -XX:+UseG1GC -XX:G1NewSizePercent=20 -XX:G1ReservePercent=20 -XX:MaxGCPauseMillis=50 -XX:G1HeapRegionSize=16m -XX:-UseAdaptiveSizePolicy -XX:-OmitStackTraceInFastThrow -XX:-DontCompileHugeMethods -Dfml.ignoreInvalidMinecraftCertificates=true -Dfml.ignorePatchDiscrepancies=true -Djava.rmi.server.useCodebaseOnly=true -Dcom.sun.jndi.rmi.object.trustURLCodebase=false -Dcom.sun.jndi.cosnaming.object.trustURLCodebase=false -Dlog4j2.formatMsgNoLookups=true -XX:HeapDumpPath=MojangTricksIntelDriversForPerformance_javaw.exe_minecraft.exe.heapdump -Djava.library.path=${native_path} -Dminecraft.launcher.brand=${launcher_brand} -Dminecraft.launcher.version=${launcher_version}";
        jvm = jvm.replace("${jar_path}",jar_file.getPath());
        jvm = jvm.replace("${native_path}",nativef.getPath());
        jvm = jvm.replace("${launcher_brand}",Vars.launcher_name);
        jvm = jvm.replace("${launcher_version}",Vars.launcher_version);

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
                                (gt.contains("java.base/java.util.jar") ||
                                 gt.contains("java.base/sun.security.util") ||
                                 gt.contains("jdk.naming.dns/com.sun.jndi.dns")
                                )
                        ) {
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
                            forge_libs.append(LinkPath.link(libf.getPath(), getPath.get(l.name).replace("\\", "/"))).append(";");
                        }
                    }
                    forge_libs = new StringBuilder(forge_libs.substring(0, forge_libs.length() - 1));
                    forge_libs.append("\"");
                    forgevm = forgevm.replace("-p ", "-p " + "\"" + forge_libs);
                    forgevm = forgevm.replace("--add-modules ALL-MODULE-PATH", " --add-modules ALL-MODULE-PATH");
                }
                else{
                    forge_jvm = "";
                }
            }
        }

        try {
            String command = LinkCommands.link(java, jvm, String.valueOf(classpath), mem, forgevm,mainClass.replace(" ",""), arguments);
            MainPage.d.setV(0, 90, Application.languageManager.get("ui.launch._07"));
            command = command.replace("null","");
            logger.info(String.format("Getted Command Line : %s", command));
            MainPage.exit_code = null;
            MainPage.cleanLog();
            MainPage.d.setV(0, 95, Application.languageManager.get("ui.launch._08"));
            p = Runtime.getRuntime().exec(command);
            CountDownLatch latch = new CountDownLatch(2);
            new Thread(() -> {
                while (true) {
                    if (EnumWindow.getTaskPID().contains(p.pid())) {
                        MainPage.logger.info("Window Showed");
                        MainPage.d.close();
                        latch.countDown();
                        break;
                    }
                }
            }).start();
            MainPage.minecraft_running = true;
            new Thread(() -> {
                while (true){
                    try {
                        int ev = p.exitValue();
                        MainPage.d.close();
                        MainPage.minecraft_running = false;
                        MainPage.exit_code = (long) ev;
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
                    finally {
                        latch.countDown();
                    }
                }
            }).start();
            readProcessOutput(p);
        } catch (IOException e){
            e.printStackTrace();
        }
    }
    public static void readProcessOutput(final Process process) {
        read(process.getInputStream(), System.out);
        read(process.getErrorStream(), System.err);
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
}
