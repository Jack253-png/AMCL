package com.mcreater.amcl.tasks.manager;

import com.mcreater.amcl.Launcher;
import com.mcreater.amcl.StableMain;
import com.mcreater.amcl.lang.AbstractLanguageManager;
import com.mcreater.amcl.tasks.Task;
import com.mcreater.amcl.util.J8Utils;
import com.mcreater.amcl.util.builders.ThreadBuilder;
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
        synchronized (TaskManager.class) {
            TaskManager.updater = updater;
        }
    }
    public static void setFinishRunnable(@NotNull Runnable runnable) {
        synchronized (TaskManager.class) {
            TaskManager.finishRunnable = runnable;
        }
    }
    public static long downloadedBytes;
    private TaskManager(){}
    public static void addTasks(Task... t){
        tasks.addAll(J8Utils.createList(t));
    }
    public static void addTasks(Collection<Task> t){
        tasks.addAll(t);
    }
    public synchronized static void executeForge(String reason) throws IOException {
        synchronized (TaskManager.class) {
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
                    updater.accept((int) ((double) executed) * 100 / tasks.size(), Launcher.languageManager.get("ui.fix._03", executed, tasks.size()));
                }
            }
            tasks.clear();
        }
    }
    public synchronized static void execute(String reason) throws InterruptedException {
        execute(reason, false);
    }
    public synchronized static void execute(String reason, boolean isSwingEnv) throws InterruptedException {
        synchronized (TaskManager.class) {
            int size = tasks.size();
            CountDownLatch latch = new CountDownLatch(size);
            AbstractLanguageManager lang;
            if (isSwingEnv) lang = StableMain.manager;
            else lang = Launcher.languageManager;

            String key;
            if (isSwingEnv) key = "ui.fix._02.swing";
            else key = "ui.fix._02";

            updater.accept(0, String.format(lang.get(key), 0, tasks.size()));
            for (Task t : tasks) {
                ThreadBuilder.createBuilder()
                        .runTarget(() -> {
                            while (true) {
                                try {
                                    t.execute();
                                    latch.countDown();
                                    break;
                                } catch (Throwable e1) {
                                    e1.printStackTrace();
                                }
                            }
                        })
                        .name(String.format("Task %s", t.toString()))
                        .buildAndRun();
            }
            long downloaded;
            long all = tasks.size();
            String cc;
            long temp = size;
            do {
                downloaded = latch.getCount();
                if (temp != latch.getCount()) {
                    if (tasks.size() != 0) {
                        updater.accept((int) ((double) (tasks.size() - downloaded)) * 100 / tasks.size(), String.format(lang.get(key), tasks.size() - downloaded, tasks.size()));
                    } else {
                        updater.accept(100, String.format(lang.get(key), all, all));
                    }
                }
                temp = downloaded;
                downloadedBytes = 0;
                Sleeper.sleep(500);
                cc = String.format("%s %d / %d", reason, tasks.size() - downloaded, tasks.size());
                System.out.print(J8Utils.repeat("\b", cc.length()) + cc);
            }
            while (downloaded != 0);

            cc = String.format("%s %d / %d", reason, all, all);

            System.out.print(J8Utils.repeat("\b", cc.length()));
            System.out.print(cc);
            System.out.println();

            if (tasks.size() != 0) updater.accept(100, String.format(lang.get(key), tasks.size(), tasks.size()));
            tasks.clear();
            downloadedBytes = 0;
            finishRunnable.run();
        }
    }
}
