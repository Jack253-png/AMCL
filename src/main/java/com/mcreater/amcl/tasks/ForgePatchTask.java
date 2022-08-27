package com.mcreater.amcl.tasks;

import com.mcreater.amcl.api.reflect.ReflectHelper;
import com.mcreater.amcl.api.reflect.ReflectedJar;
import com.mcreater.amcl.download.ForgeDownload;
import com.mcreater.amcl.game.launch.Launch;
import com.mcreater.amcl.nativeInterface.NoExitSecurityManager;
import com.mcreater.amcl.util.J8Utils;
import com.mcreater.amcl.util.StringUtils;
import com.mcreater.amcl.util.FileUtils.LinkPath;
import com.mcreater.amcl.util.java.GetJarMainClass;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.Vector;

public class ForgePatchTask extends AbstractTask{
    Integer exit;
    public String jar;
    public Vector<String> classpath = new Vector<>();
    public String[] args_array;
    public ForgePatchTask(String lib_base, String jar, Vector<String> classpath, String args, String[] args_array) throws IOException {
        super("");
        Vector<String> jars = new Vector<>();
        String mainjar = LinkPath.link(lib_base, StringUtils.GetFileBaseDir.forgeGet(jar));
        String mainClass = GetJarMainClass.get(mainjar);
        jars.add(mainjar);
        for (String s : classpath){
            jars.add(LinkPath.link(lib_base, StringUtils.GetFileBaseDir.forgeGet(s)));
        }
        this.classpath.addAll(jars);
        StringBuilder b = new StringBuilder("-cp \"");
        for (String s1 : jars){
            b.append(s1).append(File.pathSeparator);
        }
        b.replace(b.length(), b.length(), "\"");
        this.command = String.format("\"%s\" %s %s %s", LinkPath.link(System.getProperty("java.home"), "bin\\java.exe"), b, mainClass, args);
        this.jar = mainjar;
        this.args_array = args_array;
    }
    public Integer execute() throws IOException {
        if (!command.contains("DOWNLOAD_MOJMAPS")) {
//            return new_pa();
            return old_pa();
        }
        else{
            List<String> l = J8Utils.createList(command.split(" "));
            ForgeDownload.download_mojmaps(l.get(l.size() - 1));
            return 0;
        }
    }
    public int new_pa() {
        try {
            ReflectedJar jar = ReflectHelper.getReflectedJar(this.classpath.toArray(new String[0]));
            int main = jar.createNewInstance(jar.getJarClass(GetJarMainClass.get(this.jar)));
            jar.invokeMethod(main, "main", new Object[]{args_array}, String[].class);
            return 0;
        }
        catch (Exception e){
            e.printStackTrace();
            return 1;
        }
    }
    public int old_pa() throws IOException {
        System.out.println(command);
        Process p = Runtime.getRuntime().exec(command);
        while (true) {
            try {
                System.out.println(Launch.ret(p.getInputStream()));
                System.err.println(Launch.ret(p.getErrorStream()));
                exit = p.exitValue();
                return exit;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
