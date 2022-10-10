package com.mcreater.amcl.tasks;

import com.mcreater.amcl.api.reflect.ReflectHelper;
import com.mcreater.amcl.api.reflect.ReflectedJar;
import com.mcreater.amcl.download.OriginalDownload;
import com.mcreater.amcl.game.launch.Launch;
import com.mcreater.amcl.nativeInterface.NoExitSecurityManager;
import com.mcreater.amcl.util.J8Utils;
import com.mcreater.amcl.util.LogLineDetecter;
import com.mcreater.amcl.util.java.GetJarMainClass;
import com.mcreater.amcl.util.FileUtils.LinkPath;

import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.List;

public class ForgeExtractTask extends AbstractTask{
    public Integer exit = null;
    public String extractPath;
    public String jarpath;
    public String[] args;
    public ForgeExtractTask(String command, String extractPath, String jarpath, String[] args) {
        super(command);
        this.extractPath = extractPath;
        this.jarpath = jarpath;
        this.args = args;
    }

    @Override
    public Integer execute() throws IOException {
        // return new_ex();
        return old_ex();
    }
    public int new_ex() throws IOException {
        SecurityManager man = System.getSecurityManager();
        System.setSecurityManager(new NoExitSecurityManager());
        try {
            String jarp = this.jarpath;
            ReflectedJar jar = ReflectHelper.getReflectedJar(jarp);
            int main = jar.createNewInstance(jar.getJarClass(GetJarMainClass.get(jarp)));
            jar.invokeMethod(main, "main", new Object[]{this.args}, String[].class);
        }
        catch (InvocationTargetException e){
            e.printStackTrace();
        }
        catch (ClassNotFoundException | InstantiationException | IllegalAccessException | NoSuchMethodException e){
            return 1;
        }
        finally {
            copy();
            System.setSecurityManager(man);
        }
        return 0;
    }
    public int old_ex() throws IOException {
        copy();
        Process p = Runtime.getRuntime().exec(command);
        while (true){
            try{
                System.out.println(Launch.ret(p.getInputStream()));
                System.out.println(Launch.ret(p.getErrorStream()));
                copy();
                exit = p.exitValue();
                return exit;
            }
            catch (Exception e){
                e.printStackTrace();
            }
        }
    }
    public void copy(){
        List<String> files = getAllFile("forgeTemp/maven/net/minecraftforge");
        files.forEach(s -> {
            String loc = LinkPath.link(extractPath, getFileName(s));
            OriginalDownload.createNewDir(loc);
            try {
                FileChannel output = new FileOutputStream(loc).getChannel();
                try (FileChannel input = new FileInputStream(s).getChannel()){
                    output.transferFrom(input, 0, input.size());
                }
                output.close();
            }
            catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
    }
    public String getFileName(String p){
        List<String> f = J8Utils.createList(p.split("\\\\"));
        return f.get(f.size() - 1);
    }
    public List<String> getAllFile(String p){
        List<String> list = new ArrayList<>();
        File baseFile = new File(p);
        File[] files = baseFile.listFiles();
        if (new File(p).isFile() || !new File(p).exists()){
            return list;
        }
        for (File file : files) {
            if (file.isDirectory()) {
                list.addAll(getAllFile(file.getAbsolutePath()));
            } else {
                list.add(file.getAbsolutePath());
            }
        }
        return list;
    }
}
