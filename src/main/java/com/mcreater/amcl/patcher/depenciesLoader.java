package com.mcreater.amcl.patcher;

import com.mcreater.amcl.StableMain;
import com.mcreater.amcl.tasks.Task;
import com.mcreater.amcl.tasks.taskmanager.TaskManager;
import com.mcreater.amcl.util.J8Utils;
import com.mcreater.amcl.util.svg.SwingIcons;

import javax.swing.*;
import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.util.List;
import java.util.Vector;
import java.util.concurrent.CountDownLatch;

public class DepenciesLoader {
    public static DepencyLoadingFrame frame;
    public static String convertName(String name){
        List<String> names = J8Utils.createList(name.split(":"));
        if (names.size() == 3){
            return String.format("%s/%s/%s/%s-%s.jar", names.get(0).replace(".", File.separator), names.get(1), names.get(2), names.get(1), names.get(2)).replace("\\", File.separator);
        }
        else if (names.size() == 4){
            return String.format("%s/%s/%s/%s-%s-%s.jar", names.get(0).replace(".", File.separator), names.get(1), names.get(3), names.get(1), names.get(3), names.get(2)).replace("\\", File.separator);
        }
        else {
            return name;
        }
    }
    public static String convertNameToUrl(String name){
        return convertName(name).replace(File.separator, "/");
    }
    public static void checkAndDownload(Task... items) throws IOException, InterruptedException {
        Vector<Task> tasks = new Vector<>(J8Utils.createList(items));
        frame = new DepencyLoadingFrame();
        frame.setResizable(false);
        frame.setIconImage(SwingIcons.swingIcon);
        if (tasks.size() > 0) {
            if (!isConnectable()){
                StableMain.splashScreen.setVisible(false);
                JOptionPane.showMessageDialog(frame, StableMain.manager.get("ui.pre.depencies.network.fail.title"), StableMain.manager.get("ui.pre.depencies.network.fail.mess"), JOptionPane.ERROR_MESSAGE);
                System.exit(0);
            }
            frame.setVisible(true);
            StableMain.splashScreen.setVisible(false);
            TaskManager.addTasks(tasks);
            TaskManager.setUpdater((integer, s) -> {
                frame.progressBar.setValue(integer);
                frame.progressBar.setString(s);
                frame.progressBar.setIndeterminate(false);
            });
            TaskManager.execute("<download depencies>");
            frame.button.setEnabled(true);
            frame.progressBar.setString(StableMain.manager.get("ui.depencies.downloadSuccess.name"));
            frame.progressBar.setValue(100);
            CountDownLatch latch = new CountDownLatch(1);
            frame.button.addActionListener(actionEvent -> latch.countDown());
            latch.await();
        }
        else{
            frame.setVisible(false);
        }
    }
    public static boolean isConnectable() throws IOException {
        return InetAddress.getByName("maven.aliyun.com").isReachable(30000);
    }
}
