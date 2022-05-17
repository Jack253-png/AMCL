package com.mcreater.amcl.game.launch;

import com.google.gson.Gson;
import com.google.gson.internal.LinkedTreeMap;
import com.mcreater.amcl.game.getPath;
import com.mcreater.amcl.model.LibModel;
import com.mcreater.amcl.model.VersionJsonModel;
import com.mcreater.amcl.pages.MainPage;
import com.mcreater.amcl.util.FileStringReader;
import com.mcreater.amcl.util.LinkCommands;
import com.mcreater.amcl.util.LinkPath;
import com.mcreater.amcl.util.ZipUtil;

import java.io.*;
import java.util.ArrayList;
import java.util.Objects;
import java.util.Vector;

public class Launch {
    String java;
    String jvm;
    String cp;
    String mem;
    String mainClass;
    String arguments;
    String forge_jvm;
    String fgvm;
    public void launch(String java_path,String dir,String version_name,boolean ie,String launcherv){
        java = java_path;

        if (!new File(dir).exists()){
            throw new IllegalStateException("Null Minecraft Dir");
        }
        File f = new File(LinkPath.link(dir, "versions\\" + version_name));
        if (!f.exists()){
            throw new IllegalStateException("Null Version Dir");
        }
        File json_file = new File(LinkPath.link(f.getPath(),version_name + ".json"));
        File jar_file = new File(LinkPath.link(f.getPath(),version_name + ".jar"));

        if (!json_file.exists() || !jar_file.exists()){
            throw new IllegalStateException("Missing Main Files");
        }

        String json_result = FileStringReader.read(json_file.getPath());
        Gson g = new Gson();
        VersionJsonModel r = g.fromJson(json_result, VersionJsonModel.class);

        File libf = new File(LinkPath.link(dir, "libraries"));
        if (!libf.exists()){
            throw new IllegalStateException("Null Lib Dir");
        }
        Vector<String> libs = new Vector<>();
        Vector<String> natives = new Vector<>();

        for (LibModel l : r.libraries){
            if (l.downloads != null) {
                if (l.downloads.classifiers != null) {
                    if (l.downloads.classifiers.get("natives-windows") != null) {
                        natives.add(LinkPath.link(libf.getPath(), l.downloads.classifiers.get("natives-windows").path));
                    }
                }
            }
            if (new File(LinkPath.link(libf.getPath(), getPath.get(l.name))).exists()) {
                libs.add(LinkPath.link(libf.getPath(), getPath.get(l.name)));
            }
        }
        File nativef = new File(LinkPath.link(f.getPath(),version_name + "-natives"));
        if (!nativef.exists()){
            boolean b = nativef.mkdirs();
            if (!b){
                throw new IllegalStateException("Null Native Dir");
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
                throw new IllegalStateException("Null to Unzip Native");
            }
        }
        StringBuilder classpath = new StringBuilder("-cp \"");
        for (String s : libs){
            classpath.append(s).append(";");
        }
        classpath.append(jar_file).append("\"");
//        if (!String.valueOf(classpath).contains("org\\apache\\logging\\log4j\\log4j-api")){
//            classpath.append(LinkPath.link(libf.getPath(), "org\\apache\\logging\\log4j\\log4j-api\\2.17.0\\log4j-api-2.17.0.jar")).append(";");
//        }
//
//        if (!String.valueOf(classpath).contains("org\\apache\\logging\\log4j\\log4j-core")){
//            classpath.append(LinkPath.link(libf.getPath(), "org\\apache\\logging\\log4j\\log4j-core\\2.17.0\\log4j-core-2.17.0.jar")).append(";");
//        }
//
//        if (!String.valueOf(classpath).contains("net\\sf\\jopt-simple\\jopt-simple")){
//            System.out.println(0);
//            classpath.append(LinkPath.link(libf.getPath(), "net\\sf\\jopt-simple\\jopt-simple\\5.0.4\\jopt-simple-5.0.4.jar")).append(";");
//        }

        mem = "-Xmn256m -Xmx4096m";
        mainClass = r.mainClass;
        StringBuilder agm = new StringBuilder();
        if (r.minecraftArguments != null) {
            arguments = r.minecraftArguments;
        }
        else{
            for (Object s : r.arguments.game){
                if (s != null) {
                    try {
                        agm.append((String) s).append(" ");
                    } catch (ClassCastException e) {
                        LinkedTreeMap ltm = (LinkedTreeMap) s;
                        for (String s1 : (ArrayList<String>) ltm.get("value")) {
                            if (!s1.contains("demo")) {
                                agm.append(s1).append(" ");
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
        arguments = arguments.replace("${version_type}","AMCL");
        arguments = arguments.replace("${resolution_width}","854");
        arguments = arguments.replace("${resolution_height}","480");
        if (!ie) {
            arguments = arguments.replace("${game_directory}", dir);
        }
        else{
            arguments = arguments.replace("${game_directory}", f.getPath());
        }
        arguments = arguments.replace("${user_properties}","{}");
        arguments = arguments.replace("${auth_uuid}","0".repeat(18));
        arguments = arguments.replace("${auth_access_token}","0".repeat(18));
        arguments = arguments.replace("${auth_session}","0".repeat(18));
        arguments = arguments.replace("${game_assets}",LinkPath.link(dir, "assets"));

        jvm = "-Dfile.encoding=GB18030 -Dminecraft.client.jar=${jar_path} -XX:+UnlockExperimentalVMOptions -XX:+UseG1GC -XX:G1NewSizePercent=20 -XX:G1ReservePercent=20 -XX:MaxGCPauseMillis=50 -XX:G1HeapRegionSize=16m -XX:-UseAdaptiveSizePolicy -XX:-OmitStackTraceInFastThrow -XX:-DontCompileHugeMethods -Dfml.ignoreInvalidMinecraftCertificates=true -Dfml.ignorePatchDiscrepancies=true -Djava.rmi.server.useCodebaseOnly=true -Dcom.sun.jndi.rmi.object.trustURLCodebase=false -Dcom.sun.jndi.cosnaming.object.trustURLCodebase=false -Dlog4j2.formatMsgNoLookups=true -Dlog4j.configurationFile=D:\\mods\\util\\.minecraft\\versions\\1.17.1\\log4j2.xml -XX:HeapDumpPath=MojangTricksIntelDriversForPerformance_javaw.exe_minecraft.exe.heapdump -Djava.library.path=${native_path} -Dminecraft.launcher.brand=${launcher_brand} -Dminecraft.launcher.version=${launcher_version}";
        jvm = jvm.replace("${jar_path}",jar_file.getPath());
        jvm = jvm.replace("${native_path}",nativef.getPath());
        jvm = jvm.replace("${launcher_brand}","AMCL");
        jvm = jvm.replace("${launcher_version}",launcherv);

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
                    fgvm = String.valueOf(forge_jvm);
                    fgvm = fgvm.replace("${version_name}", version_name);
                    fgvm = fgvm.replace("${primary_jar_name}", version_name + ".jar");
                    fgvm = fgvm.replace("${library_directory}", libf.getPath());
                    StringBuilder forge_libs = new StringBuilder();
                    for (LibModel l : r.libraries) {
                        if (l.name.contains("cpw.mods") || l.name.contains("org.ow2")) {
                            forge_libs.append(LinkPath.link(libf.getPath(), getPath.get(l.name).replace("\\", "/"))).append(";");
                        }
                    }
                    forge_libs = new StringBuilder(forge_libs.substring(0, forge_libs.length() - 1));
                    fgvm = fgvm.replace("-p ", "-p " + forge_libs);
                    fgvm = fgvm.replace("--add-modules ALL-MODULE-PATH", " --add-modules ALL-MODULE-PATH");
                }
                else{
                    forge_jvm = "";
                }
            }
        }

        try {

            String command = LinkCommands.link(java, jvm, String.valueOf(classpath), mem, fgvm,mainClass.replace(" ",""), arguments);
            command = command.replace("null","");
            System.out.println(command);
            MainPage.exit_code = -1;
            MainPage.cleanLog();
            Process p = Runtime.getRuntime().exec(command);
            MainPage.minecraft_running = true;
            new Thread(() -> {
                while (true){
                    try {
                        int ev = p.exitValue();
                        MainPage.minecraft_running = false;
                        MainPage.exit_code = ev;
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
        }
        catch (IOException e){
            e.printStackTrace();
        }
    }
    private static void readProcessOutput(final Process process) {
        read(process.getInputStream(), System.out);
        read(process.getErrorStream(), System.err);
    }
    private static void read(InputStream inputStream, PrintStream out) {
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
}
