package com.mcreater.amcl.javafx;

import com.mcreater.amcl.Main;
import com.mcreater.amcl.StableMain;
import com.mcreater.amcl.nativeInterface.ResourceGetter;
import com.mcreater.amcl.taskmanager.TaskManager;
import com.mcreater.amcl.tasks.DownloadTask;
import com.mcreater.amcl.tasks.Task;
import com.mcreater.amcl.util.GetPath;
import com.mcreater.amcl.util.LinkPath;
import com.mcreater.amcl.util.xml.DepenciesXMLHandler;
import com.mcreater.amcl.util.xml.DepencyItem;
import org.xml.sax.SAXException;

import javax.swing.*;
import javax.xml.parsers.ParserConfigurationException;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.*;
import java.util.List;
import java.util.Vector;
import java.util.concurrent.CountDownLatch;

public class depenciesLoader {
    public static depencyLoadingFrame frame;
    public static String convertName(String name){
        List<String> names = List.of(name.split(":"));
        if (names.size() == 3){
            return String.format("%s\\%s\\%s\\%s-%s.jar", names.get(0).replace(".", File.separator), names.get(1), names.get(2), names.get(1), names.get(2));
        }
        else if (names.size() == 4){
            return String.format("%s\\%s\\%s\\%s-%s-%s.jar", names.get(0).replace(".", File.separator), names.get(1), names.get(3), names.get(1), names.get(3), names.get(2));
        }
        else {
            return name;
        }
    }
    public static String convertNameToUrl(String name){
        return convertName(name).replace(File.separator, "/");
    }
    public static void checkAndDownload() throws ParserConfigurationException, IOException, SAXException, InterruptedException, UnsupportedLookAndFeelException, ClassNotFoundException, InstantiationException, IllegalAccessException {
        Vector<Task> tasks = new Vector<>();
        for (DepencyItem item : DepenciesXMLHandler.load()){
            String local = item.getLocal();
            if (!new File(local).exists()) {
                new File(GetPath.get(local)).mkdirs();
                tasks.add(new DownloadTask(item.getURL(), local, 2048));
            }
        }
        UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        frame = new depencyLoadingFrame();
        frame.setResizable(false);
        frame.setIconImage(Toolkit.getDefaultToolkit().createImage(new ResourceGetter().getUrl("assets/grass.png")));
        if (tasks.size() > 0) {
            if (!isConnectable()){
                JOptionPane.showMessageDialog(frame, StableMain.manager.get("ui.pre.depencies.network.fail.title"), StableMain.manager.get("ui.pre.depencies.network.fail.mess"), JOptionPane.ERROR_MESSAGE);
                System.exit(0);
            }
            frame.setVisible(true);
            TaskManager.addTasks(tasks);
            TaskManager.bindSwing(frame);
            TaskManager.execute("<download depencies>");
            CountDownLatch latch = new CountDownLatch(1);
            frame.button.addActionListener(actionEvent -> latch.countDown());
            latch.await();
        }
        else{
            frame.setVisible(false);
        }
    }
    public static boolean isConnectable() throws IOException {
        return InetAddress.getByName("maven.aliyun.com").isReachable(3000);
    }
}
