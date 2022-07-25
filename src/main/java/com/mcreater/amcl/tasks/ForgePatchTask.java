package com.mcreater.amcl.tasks;

import com.mcreater.amcl.download.ForgeDownload;
import com.mcreater.amcl.game.launch.Launch;
import com.mcreater.amcl.util.GetJarMainClass;
import com.mcreater.amcl.util.GetPath;
import com.mcreater.amcl.util.LinkPath;

import java.io.File;
import java.io.IOException;
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
        String mainjar = LinkPath.link(lib_base, GetPath.forgeGet(jar));
        String mainClass = GetJarMainClass.get(mainjar);
        jars.add(mainjar);
        for (String s : classpath){
            jars.add(LinkPath.link(lib_base, GetPath.forgeGet(s)));
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
//            SecurityManager sm = System.getSecurityManager();
//            System.setSecurityManager(new NoExitSecurityManager());
//            try {
//                ReflectedJar jar = ReflectHelper.getReflectedJar(this.classpath.toArray(new String[0]));
//                int main = jar.createNewInstance(jar.getJarClass(GetJarMainClass.get(this.jar)));
//                jar.invokeMethod(main, "main", new Object[]{args_array}, String[].class);
//            }
//            catch (ClassNotFoundException | InstantiationException | IllegalAccessException | NoSuchMethodException | InvocationTargetException e){
//                e.printStackTrace();
//                return 1;
//            }
//            finally {
//                System.setSecurityManager(sm);
//            }
            return old_pa();
        }
        else{
            List<String> l = List.of(command.split(" "));
            ForgeDownload.download_mojmaps(l.get(l.size() - 1));
        }
        return 0;
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
