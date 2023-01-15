package com.mcreater.amcl.tasks;

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

import static com.mcreater.amcl.util.FileUtils.OperateUtil.createDirectoryDirect;
import static com.mcreater.amcl.util.FileUtils.PathUtil.buildPath;
import static com.mcreater.amcl.util.ConsoleOutputHelper.printStreamToPrintStream;

public class ForgeExtractTask extends AbstractCommandTask {
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
        while (true) {
            try {
                new Thread(() -> printStreamToPrintStream(p.getInputStream(), System.out)).start();
                new Thread(() -> printStreamToPrintStream(p.getErrorStream(), System.err)).start();
                copy();
                exit = p.exitValue();
                return exit;
            }
            catch (Exception e){
                e.printStackTrace();
            }
        }
    }
    public void copy() {
        List<String> files = getAllFile(buildPath("forgeTemp", "maven", "net", "minecraftforge"));
        files.forEach(s -> {
            String loc = LinkPath.link(extractPath, new File(s).getName());
            createDirectoryDirect(extractPath);
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

    public List<String> getAllFile(String p) {
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
