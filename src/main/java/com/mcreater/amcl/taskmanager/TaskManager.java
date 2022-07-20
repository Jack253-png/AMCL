package com.mcreater.amcl.taskmanager;

import com.mcreater.amcl.tasks.Task;
import com.mcreater.amcl.util.Sleeper;
import javafx.scene.effect.DropShadow;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Vector;
import java.util.concurrent.CountDownLatch;

public class TaskManager {
    public static Vector<Task> tasks = new Vector<>();
    public static Logger logger = LogManager.getLogger(TaskManager.class);
    public static void addTasks(Task... t){
        tasks.addAll(List.of(t));
    }
    public static void addTasks(Collection<Task> t){
        tasks.addAll(t);
    }
    public synchronized static void execute(String reason) throws InterruptedException {
        int size = tasks.size();
        CountDownLatch latch = new CountDownLatch(size);
        logger.info(String.format("executing tasks in reason %s", reason));new Thread("Manager Counting Thread"){
            public void run(){
                long downloaded = 0;
                do {
                    downloaded = latch.getCount();
                    logger.info(String.format("executed %d of %d", tasks.size() - downloaded, tasks.size()));
                    Sleeper.sleep(100);
                }
                while (downloaded != 0);
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
                        catch (IOException e){
                            logger.error("Error while executing", e);
                        }
                    }
                }
            }.start();
        }
        latch.await();
        tasks.clear();
    }
    public static Vector<Task> changeTasksPool(Vector<Task> tasks, String name){
        for (Task t : tasks){
            t.pool.setName(name);
        }
        return tasks;
    }
}
