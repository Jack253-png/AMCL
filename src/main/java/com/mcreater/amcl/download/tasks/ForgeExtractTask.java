package com.mcreater.amcl.download.tasks;

import com.mcreater.amcl.game.launch.Launch;
import com.mcreater.amcl.util.LinkPath;

import java.io.*;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class ForgeExtractTask extends AbstractTask{
    public Integer exit = null;
    public String extractPath;
    public ForgeExtractTask(String command, String extractPath) {
        super(command);
        this.extractPath = extractPath;
    }

    @Override
    public Integer execute() throws IOException {
        Process p = Runtime.getRuntime().exec(command);
        while (true){
            try{
                System.out.println(Launch.ret(p.getInputStream()));
                System.out.println(Launch.ret(p.getErrorStream()));
                List<String> files = getAllFile("forgeTemp/maven/net/minecraftforge");
                files.forEach(s -> {
                    String loc = LinkPath.link(extractPath, getFileName(s));
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


                exit = p.exitValue();
                return exit;
            }
            catch (Exception e){
                e.printStackTrace();
            }
        }
    }
    public String getFileName(String p){
        List<String> f = List.of(p.split("\\\\"));
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
