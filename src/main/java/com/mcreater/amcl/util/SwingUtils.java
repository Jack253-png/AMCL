package com.mcreater.amcl.util;

import com.mcreater.amcl.StableMain;
import com.mcreater.amcl.nativeInterface.ResourceGetter;
import com.mcreater.amcl.pages.interfaces.Fonts;
import com.mcreater.amcl.util.svg.SwingIcons;
import javafx.scene.ImageCursor;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.net.URL;

public class SwingUtils {
    public static void showMessage(String title, String content, Runnable r){
        JDialog dialog = new JDialog(){
            protected void processWindowEvent(WindowEvent event){
                if (event.getID() == WindowEvent.WINDOW_CLOSING){
                    return;
                }
                super.processWindowEvent(event);
            }
        };
        FlowLayout layout = new FlowLayout(FlowLayout.CENTER);

        dialog.setSize(250, 100);
        dialog.setTitle(title);
        JLabel label = new JLabel(content);
        label.setFont(Fonts.awt_t_f);
        JButton button = new JButton(StableMain.manager.get("ui.dialogs.information.ok.name"));
        button.setFont(Fonts.awt_t_f);
        button.addActionListener(e -> r.run());

        dialog.setLayout(layout);
        dialog.add(label);
        dialog.add(button);

        dialog.setIconImage(SwingIcons.swingIcon);
        dialog.setVisible(true);
        dialog.setAlwaysOnTop(true);
        dialog.setResizable(false);
    }
    public static class SplashScreen extends JFrame {
        private boolean showSplash = false;
        public void setShowSplash(boolean b){
            showSplash = b;
        }
        public boolean getShowSplash(){
            return showSplash;
        }

        public SplashScreen(){
            setCursor(new Cursor(Cursor.WAIT_CURSOR));
            Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();

            double screenWidth = screenSize.getWidth();
            double screenHeight = screenSize.getHeight();

            int width = 150;
            int height = 150;

            setUndecorated(true);
            setBackground(new Color(0, 0, 0, 0));
            setSize(width, height);
            setLocation((int) (screenWidth / 2 - width / 2), (int) (screenHeight / 2 - height / 2));
            add(new ImageView(new ResourceGetter().getUrl("assets/icons/grass.png")));
            setIconImage(SwingIcons.swingIcon);
            setAlwaysOnTop(true);
            setVisible(false);
            setTitle("AMCL");
        }
        public void setVisible(boolean visible) {
            super.setVisible(getShowSplash() && visible);
        }
    }
    public static class ImageView extends JPanel {
        URL imageURL;
        public ImageView(URL url){
            imageURL = url;
        }
        public void setImageURL(URL url){
            imageURL = url;
            repaint();
        }
        protected void paintComponent(Graphics g){
            int width = getWidth();
            int height = getHeight();

            g.clearRect(0, 0, width, height);
            try {
                g.drawImage(ImageIO.read(imageURL), 0, 0, width, height, null);
            }
            catch (IOException ignored) {}
        }
    }
}
