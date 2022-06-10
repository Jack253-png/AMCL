package com.mcreater.amcl.download.tasks;

import com.mcreater.amcl.game.launch.Launch;
import com.mcreater.amcl.util.GetJarMainClass;
import com.mcreater.amcl.util.GetPath;
import com.mcreater.amcl.util.LinkPath;

import java.io.IOException;
import java.util.Vector;

public class ForgePatchTask extends AbstractTask{
    Integer exit;
    public ForgePatchTask(String lib_base, String jar, Vector<String> classpath, String args) throws IOException {
        super("");
        Vector<String> jars = new Vector<>();
        String mainjar = LinkPath.link(lib_base, GetPath.forgeGet(jar));
        String mainClass = GetJarMainClass.get(mainjar);
        jars.add(mainjar);
        for (String s : classpath){
            jars.add(LinkPath.link(lib_base, GetPath.forgeGet(s)));
        }
        StringBuilder b = new StringBuilder("-cp \"");
        for (String s1 : jars){
            b.append(s1).append(";");
        }
        b.replace(b.length(), b.length(), "\"");
        String com = String.format("%s %s %s %s", LinkPath.link(System.getProperty("java.home"), "bin\\java.exe"), b.toString(), mainClass, args);
        this.command = com;
    }
    public Integer execute() throws IOException {
        Process p = Runtime.getRuntime().exec(command);
        while (true){
            try{
                System.out.println(Launch.ret(p.getInputStream()));
                exit = p.exitValue();
                return exit;
            }
            catch (Exception e){
                e.printStackTrace();
            }
        }
    }
}
