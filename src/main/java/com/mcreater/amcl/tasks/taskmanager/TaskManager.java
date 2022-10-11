package com.mcreater.amcl.tasks.taskmanager;

import com.mcreater.amcl.Launcher;
import com.mcreater.amcl.StableMain;
import com.mcreater.amcl.patcher.DepencyLoadingFrame;
import com.mcreater.amcl.pages.dialogs.commons.ProcessDialog;
import com.mcreater.amcl.tasks.Task;
import com.mcreater.amcl.util.J8Utils;
import com.mcreater.amcl.util.concurrent.Sleeper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.util.Collection;
import java.util.Vector;
import java.util.concurrent.CountDownLatch;

public abstract class TaskManager {
    public static Vector<Task> tasks = new Vector<>();
    public static ProcessDialog dialog;
    public static DepencyLoadingFrame frame;
    public static int index;
    public static long downloadedBytes;
    private TaskManager(){}
    public static void addTasks(Task... t){
        tasks.addAll(J8Utils.createList(t));
    }
    public static void addTasks(Collection<Task> t){
        tasks.addAll(t);
    }
    public static void bind(ProcessDialog dialog, int index){
        TaskManager.dialog = dialog;
        TaskManager.index = index;
        if (dialog != null) dialog.setV(index, 0);
    }
    public static void bindSwing(DepencyLoadingFrame frame){
        TaskManager.frame = frame;
    }
    public synchronized static void execute1Thread(String reason) throws IOException {
        Logger logger = LogManager.getLogger(TaskManager.class);
        int executed = 0;
        logger.info(String.format("executing tasks %s", reason));
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

        new Thread("Manager Counting Thread"){
            public void run(){
                if (dialog != null){
                    dialog.setV(index, 0);
                }
                long downloaded;
                long all = tasks.size();
                String cc;
                long temp = size;
                do {
                    downloaded = latch.getCount();
                    cc = String.format("%s %d / %d", reason, tasks.size() - downloaded, tasks.size());
                    System.out.print(J8Utils.repeat("\b", cc.length()) + cc);
                    if (temp != latch.getCount()) {
                        if (dialog != null) {
                            if (tasks.size() != 0) {
                                dialog.setV(index, (int) ((double) (tasks.size() - downloaded)) * 100 / tasks.size(), String.format(Launcher.languageManager.get("ui.fix._02"), reason, tasks.size() - downloaded, tasks.size()));
                            }
                        }
                    }
                    temp = downloaded;
                    if (frame != null){
                        if (tasks.size() != 0) {
                            frame.progressBar.setValue((int) (((double) (tasks.size() - downloaded)) * 100 / tasks.size()));
                            frame.progressBar.setIndeterminate(false);
                            frame.progressBar.setString(String.format(StableMain.manager.get("ui.depencies.downloading.name"), tasks.size() - downloaded, tasks.size()));
                        }
                    }
                    downloadedBytes = 0;
                    Sleeper.sleep(500);
                }
                while (downloaded != 0);

                if (dialog != null) {
                    dialog.setV(index, 100);
                }

                System.out.print(J8Utils.repeat("\b", cc.length()) + reason + String.format(" %d / %d", all, all));
                System.out.println();
                if (frame != null){
                    frame.button.setEnabled(true);
                    frame.progressBar.setString(StableMain.manager.get("ui.depencies.downloadSuccess.name"));
                    frame.progressBar.setValue(100);
                }
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
                            e.printStackTrace();
                        }
                    }
                }
            }.start();
        }
        latch.await();
        tasks.clear();
        if (dialog != null) {
            dialog.setV(index, 100);
        }
        downloadedBytes = 0;
    }
    public static Vector<Task> changeTasksPool(Vector<Task> tasks, String name){
        for (Task t : tasks){
            t.pool.setName(name);
        }
        return tasks;
    }
}
