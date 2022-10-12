package com.mcreater.amcl.tasks.taskmanager;

import com.mcreater.amcl.Launcher;
import com.mcreater.amcl.tasks.Task;
import com.mcreater.amcl.util.J8Utils;
import com.mcreater.amcl.util.concurrent.Sleeper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.Collection;
import java.util.Vector;
import java.util.concurrent.CountDownLatch;
import java.util.function.BiConsumer;

public abstract class TaskManager {
    public static Vector<Task> tasks = new Vector<>();
    public static BiConsumer<Integer, String> updater = (value, mess) -> {};
    public static Runnable finishRunnable = () -> {};
    public static void setUpdater(@NotNull BiConsumer<Integer, String> updater) {
        TaskManager.updater = updater;
    }
    public static long downloadedBytes;
    private TaskManager(){}
    public static void addTasks(Task... t){
        tasks.addAll(J8Utils.createList(t));
    }
    public static void addTasks(Collection<Task> t){
        tasks.addAll(t);
    }
    public synchronized static void execute1Thread(String reason) throws IOException {
        Logger logger = LogManager.getLogger(TaskManager.class);
        int executed = 0;
        logger.info(String.format("executing tasks %s", reason));
        for (Task t : tasks){
            Integer exit = t.execute();
            if (exit != null){
                if (exit != 0){
                    throw new IOException();
                }
            }
            executed += 1;
            logger.info(String.format("executed %d of %d", executed, tasks.size()));
            if (tasks.size() != 0) {
                updater.accept((int) ((double) executed) * 100 / tasks.size(), String.format(Launcher.languageManager.get("ui.fix._03"), executed, tasks.size()));
            }
        }
        tasks.clear();
    }
    public synchronized static void execute(String reason) throws InterruptedException {
        int size = tasks.size();
        CountDownLatch latch = new CountDownLatch(size);
        updater.accept(0, String.format(Launcher.languageManager.get("ui.fix._02"), reason, 0, tasks.size()));
        new Thread("Manager Counting Thread"){
            public void run(){
                long downloaded;
                long all = tasks.size();
                String cc;
                long temp = size;
                do {
                    downloaded = latch.getCount();
                    cc = String.format("%s %d / %d", reason, tasks.size() - downloaded, tasks.size());
                    System.out.print(J8Utils.repeat("\b", cc.length()) + cc);
                    if (temp != latch.getCount()) {
                        if (tasks.size() != 0) {
                            updater.accept((int) ((double) (tasks.size() - downloaded)) * 100 / tasks.size(), String.format(Launcher.languageManager.get("ui.fix._02"), reason, tasks.size() - downloaded, tasks.size()));
                        }
                    }
                    temp = downloaded;
                    downloadedBytes = 0;
                    Sleeper.sleep(500);
                }
                while (downloaded != 0);

                System.out.print(J8Utils.repeat("\b", cc.length()) + reason + String.format(" %d / %d", all, all));
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
                            e.printStackTrace();
                        }
                    }
                }
            }.start();
        }
        latch.await();
        updater.accept(100, String.format(Launcher.languageManager.get("ui.fix._02"), reason, tasks.size(), tasks.size()));
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
