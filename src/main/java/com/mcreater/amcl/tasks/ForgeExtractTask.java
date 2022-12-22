package com.mcreater.amcl.tasks;

import com.mcreater.amcl.download.OriginalDownload;
import com.mcreater.amcl.game.launch.Launch;
import com.mcreater.amcl.util.FileUtils;
import com.mcreater.amcl.util.FileUtils.LinkPath;
import com.mcreater.amcl.util.J8Utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Vector;

import static com.mcreater.amcl.util.FileUtils.OperateUtil.createDirectory;
import static com.mcreater.amcl.util.FileUtils.OperateUtil.createDirectoryDirect;

public class ForgeExtractTask extends AbstractExecutableTask {
    public Integer exit = null;
    public String extractPath;
    public String jarpath;
    public String[] args;
    public ForgeExtractTask(String extractPath, String jarpath, String[] args) {
        super(new Vector<>(
                J8Utils.createList(
                        FileUtils.getJavaExecutable(),
                        "-jar",
                        jarpath
                )
        ));
        command.addAll(Arrays.asList(args));

        this.extractPath = extractPath;
        this.jarpath = jarpath;
        this.args = args;
    }

    @Override
    public Integer execute() throws IOException {
        return old_ex();
    }

    public int old_ex() throws IOException {
        copy();
        Process p = Runtime.getRuntime().exec(command.toArray(new String[0]));
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
            createDirectory(loc);
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
