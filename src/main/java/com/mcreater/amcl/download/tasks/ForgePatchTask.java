package com.mcreater.amcl.download.tasks;

import com.mcreater.amcl.download.ForgeDownload;
import com.mcreater.amcl.game.launch.Launch;
import com.mcreater.amcl.util.GetJarMainClass;
import com.mcreater.amcl.util.GetPath;
import com.mcreater.amcl.util.LinkPath;

import java.io.IOException;
import java.util.List;
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
        this.command = String.format("\"%s\" %s %s %s", LinkPath.link(System.getProperty("java.home"), "bin\\java.exe"), b, mainClass, args);
    }
    public Integer execute() throws IOException {
        if (!command.contains("DOWNLOAD_MOJMAPS")) {
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
        else{
            List<String> l = List.of(command.split(" "));
            ForgeDownload.download_mojmaps(l.get(l.size() - 1));
            return 0;
        }
    }
}
