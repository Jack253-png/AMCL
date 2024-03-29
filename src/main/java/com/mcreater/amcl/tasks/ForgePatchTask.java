package com.mcreater.amcl.tasks;

import com.mcreater.amcl.download.ForgeDownload;
import com.mcreater.amcl.util.FileUtils;
import com.mcreater.amcl.util.FileUtils.LinkPath;
import com.mcreater.amcl.util.J8Utils;
import com.mcreater.amcl.util.StringUtils;
import com.mcreater.amcl.util.builders.ThreadBuilder;
import com.mcreater.amcl.util.java.GetJarMainClass;
import com.mcreater.amcl.util.net.FasterUrls;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Vector;
import java.util.concurrent.CountDownLatch;

import static com.mcreater.amcl.util.ConsoleOutputHelper.printStreamToPrintStream;

public class ForgePatchTask extends AbstractCommandTask {
    public String jar;
    public Vector<String> classpath = new Vector<>();
    public String[] args_array;
    FasterUrls.Server server;

    public ForgePatchTask(String lib_base, String jar, Vector<String> classpath, String[] args_array, FasterUrls.Server server) throws IOException {
        super(new Vector<>());
        Vector<String> jars = new Vector<>();
        String mainjar = LinkPath.link(lib_base, StringUtils.GetFileBaseDir.forgeGet(jar));
        String mainClass = GetJarMainClass.get(mainjar);
        jars.add(mainjar);
        for (String s : classpath) {
            jars.add(LinkPath.link(lib_base, StringUtils.GetFileBaseDir.forgeGet(s)));
        }
        this.classpath.addAll(jars);
        StringBuilder b = new StringBuilder();
        for (String s1 : jars) {
            b.append(s1).append(File.pathSeparator);
        }
        b.replace(b.length(), b.length(), "");
        command.addAll(
                J8Utils.createList(
                        FileUtils.getJavaExecutable(),
                        "-cp",
                        b.toString(),
                        mainClass
                )
        );
        command.addAll(Arrays.asList(args_array));

        this.jar = mainjar;
        this.args_array = args_array;
        this.server = server;
    }

    public Integer execute() throws IOException {
        List<String> argList = Arrays.asList(args_array);
        if (!argList.contains("DOWNLOAD_MOJMAPS")) {
            return old_pa();
        } else {
            ForgeDownload.download_mojmaps(argList.get(argList.size() - 1), server);
            return 0;
        }
    }

    public int old_pa() throws IOException {
        Process p = Runtime.getRuntime().exec(command.toArray(new String[0]));
        while (true) {
            try {
                CountDownLatch latch = new CountDownLatch(2);
                ThreadBuilder.createBuilder().runTarget(() -> {
                    printStreamToPrintStream(p.getInputStream(), System.out);
                    latch.countDown();
                }).buildAndRun();
                ThreadBuilder.createBuilder().runTarget(() -> {
                    printStreamToPrintStream(p.getErrorStream(), System.err);
                    latch.countDown();
                }).buildAndRun();
                latch.await();
                return p.exitValue();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
