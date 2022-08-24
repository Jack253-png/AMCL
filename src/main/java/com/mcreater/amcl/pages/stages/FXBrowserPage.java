package com.mcreater.amcl.pages.stages;

import com.mcreater.amcl.Launcher;
import com.mcreater.amcl.api.auth.MSAuth;
import com.mcreater.amcl.api.auth.users.MicrosoftUser;
import com.mcreater.amcl.nativeInterface.ResourceGetter;
import com.mcreater.amcl.pages.dialogs.FastInfomation;
import com.mcreater.amcl.pages.interfaces.Fonts;
import com.teamdev.jxbrowser.chromium.BrowserException;
import com.teamdev.jxbrowser.chromium.CookieStorage;
import com.teamdev.jxbrowser.chromium.events.FinishLoadingEvent;
import com.teamdev.jxbrowser.chromium.events.LoadAdapter;
import com.teamdev.jxbrowser.chromium.swing.BrowserView;
import javafx.application.Platform;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowEvent;

public class FXBrowserPage extends AbstractStage{
    BrowserView view;
    public JFrame frame;

    JButton back;
    JButton forward;
    JButton refresh;
    JTextField f;
    public MicrosoftUser user;
    public RuntimeException ex;
    public Runnable r = () -> {};

    public FXBrowserPage(String url){
        try {
            view = new BrowserView();
        }
        catch (BrowserException e){
            ex = e;
        }
        CookieStorage storage = view.getBrowser().getCookieStorage();
        storage.deleteAll();
        storage.save();
        view.getBrowser().loadURL(url);
        view.getBrowser().addLoadListener(new LoadAdapter() {
            public void onFinishLoadingFrame(FinishLoadingEvent event) {
                System.out.println(event.getValidatedURL());
                new Thread(() -> {
                    String url = event.getValidatedURL();
                    if (url.contains("The%20user%20has%20denied%20access%20to%20the%20scope%20requested%20by%20the%20client%20application.")){
                        view.getBrowser().loadURL(MSAuth.loginUrl);
                    }
                    else if (url.startsWith(MSAuth.redirectUrlSuffix)){
                        frame.dispose();
                        int start = url.indexOf("?code=");
                        String temp = url.substring(start);
                        int end = temp.indexOf("&lc=");
                        try {
                            user = new MSAuth().getUser(temp.substring(6, end));
                        }
                        catch (RuntimeException e){
                            ex = e;
                        }
                    }
                }).start();
            }
        });
        frame = new JFrame(){
            protected void processWindowEvent(WindowEvent event){
                if (event.getID() == WindowEvent.WINDOW_CLOSING){
                    r.run();
                }
                super.processWindowEvent(event);
            }
        };
        frame.setIconImage(Toolkit.getDefaultToolkit().createImage(new ResourceGetter().getUrl("assets/icons/grass.png")));


        back = new JButton("back");
        back.addActionListener(actionEvent -> view.getBrowser().goBack());
        forward = new JButton("forward");
        forward.addActionListener(actionEvent -> view.getBrowser().goForward());
        refresh = new JButton("refresh");
        refresh.addActionListener(actionEvent -> view.getBrowser().reload());

        new Thread("Button Thread"){
            public void run(){
                while (true){
                    try {
                        back.setEnabled(view.getBrowser().canGoBack());
                        forward.setEnabled(view.getBrowser().canGoForward());
                        frame.setTitle(view.getBrowser().getTitle());
                    }
                    catch (Exception ignored){

                    }
                }
            }
        }.start();

        back.setFont(Fonts.awt_t_f);
        forward.setFont(Fonts.awt_t_f);
        refresh.setFont(Fonts.awt_t_f);

        frame.setLayout(new BoxLayout(frame.getContentPane(), BoxLayout.Y_AXIS));
        JPanel bar = new JPanel();

        bar.setLayout(new FlowLayout(FlowLayout.LEFT));
        bar.add(back);
        bar.add(forward);
        bar.add(refresh);

        frame.add(bar);
        frame.add(view);
        frame.pack();
        frame.setBounds(100, 100, 500, 620);
        frame.setMinimumSize(new Dimension(500, 620));
        frame.setLocationRelativeTo(null);
    }
    public void open() {
        frame.show();
    }
}
