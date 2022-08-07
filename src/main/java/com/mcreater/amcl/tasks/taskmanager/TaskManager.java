package com.mcreater.amcl.tasks.taskmanager;

import com.mcreater.amcl.Launcher;
import com.mcreater.amcl.patcher.depencyLoadingFrame;
import com.mcreater.amcl.pages.dialogs.ProcessDialog;
import com.mcreater.amcl.tasks.Task;
import com.mcreater.amcl.util.concurrent.Sleeper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.Vector;
import java.util.concurrent.CountDownLatch;

public abstract class TaskManager {
    public static Vector<Task> tasks = new Vector<>();
    public static Logger logger = LogManager.getLogger(TaskManager.class);
    public static ProcessDialog dialog;
    public static depencyLoadingFrame frame;
    public static int index;
    public static long downloadedBytes;
    private TaskManager(){}
    public static void addTasks(Task... t){
        tasks.addAll(List.of(t));
    }
    public static void addTasks(Collection<Task> t){
        tasks.addAll(t);
    }
    public static void bind(ProcessDialog dialog, int index){
        TaskManager.dialog = dialog;
        TaskManager.index = index;
        dialog.setV(index, 0);
    }
    public static void bindSwing(depencyLoadingFrame frame){
        TaskManager.frame = frame;
    }
    public synchronized static void execute1Thread(String reason) throws IOException {
        int executed = 0;
        logger.info(String.format("executing tasks in reason %s", reason));
        dialog.setV(index, 0);
        for (Task t : tasks){
            Integer exit = t.execute();
            if (exit != null){
                if (exit != 0){
                    throw new IOException();
                }
            }
            executed += 1;
            logger.info(String.format("executed %d of %d", executed, tasks.size()));
            logger.info("downloaded bytes speed : " + ((double) downloadedBytes) / 1024 / 1024 + "MB/s");
            if (dialog != null) {
                if (tasks.size() != 0) {
                    dialog.setV(index, (int) ((double) executed) * 100 / tasks.size(), String.format(Launcher.languageManager.get("ui.fix._03"), executed, tasks.size()));
                }
            }
        }
        tasks.clear();
    }
    public synchronized static void execute(String reason) throws InterruptedException {
        int size = tasks.size();
        CountDownLatch latch = new CountDownLatch(size);
        logger.info(String.format("executing tasks in reason %s", reason));
        new Thread("Manager Counting Thread"){
            public void run(){
                if (dialog != null){
                    dialog.setV(index, 0);
                }
                long downloaded;
                String cc;
                do {
                    downloaded = latch.getCount();
                    if (tasks.size() > 1) {
                        String dd = "█";
                        String ed = " ";
                        int c = (int) (((double) (tasks.size() - downloaded)) * 100 / tasks.size());
                        cc = String.format("%d / %d [%s%s]", tasks.size() - downloaded, tasks.size(), dd.repeat(c), ed.repeat(100 - c));
                        System.out.print("\b".repeat(cc.length()) + cc);
                    }
                    else {
                        cc = String.format("%d / %d", tasks.size() - downloaded, tasks.size());
                        System.out.print("\b".repeat(cc.length()) + cc);
                    }
//                    logger.info(String.format("executed %d of %d", tasks.size() - downloaded, tasks.size()));
//                    logger.info("downloaded bytes speed : " + ((double) downloadedBytes) / 1024 / 1024 + "MB/s");
                    if (dialog != null) {
                        if (tasks.size() != 0) {
                            dialog.setV(index, (int) ((double) (tasks.size() - downloaded)) * 100 / tasks.size(), String.format(Launcher.languageManager.get("ui.fix._02"), reason, tasks.size() - downloaded, tasks.size()));
                        }
                    }
                    if (frame != null){
                        if (tasks.size() != 0) {
                            frame.progressBar.setValue((int) (((double) (tasks.size() - downloaded)) * 100 / tasks.size()));
                            frame.progressBar.setIndeterminate(false);
                            frame.progressBar.setString(String.format("下载依赖库中(%d/%d)", tasks.size() - downloaded, tasks.size()));
                        }
                    }
                    downloadedBytes = 0;
                }
                while (tasks.size() != 0);
                if (dialog != null) {
                    dialog.setV(index, 100);
                }
                if (frame != null){
                    frame.button.setEnabled(true);
                    frame.progressBar.setString("下载完成");
                    frame.progressBar.setValue(100);
                }
                System.out.println();
            }
        }.start();
        for (Task t : tasks){
            new Thread(String.format("pool %s task %s", t.pool, t.toString())){
                public void run(){
                    while (true){
                        try{
                            t.execute();
                            latch.countDown();
                            break;
                        }
                        catch (Error e1){
                            break;
                        }
                        catch (IOException e){
                            logger.error("Error while executing", e);
                        }
                    }
                }
            }.start();
        }
        latch.await();
        tasks.clear();
        downloadedBytes = 0;
    }
    public static Vector<Task> changeTasksPool(Vector<Task> tasks, String name){
        for (Task t : tasks){
            t.pool.setName(name);
        }
        return tasks;
    }
}
