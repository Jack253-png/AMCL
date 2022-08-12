package com.mcreater.amcl.pages.stages;

import com.mcreater.amcl.api.auth.MSAuth;
import com.mcreater.amcl.nativeInterface.ResourceGetter;
import com.mcreater.amcl.pages.interfaces.Fonts;
import com.teamdev.jxbrowser.chromium.CookieStorage;
import com.teamdev.jxbrowser.chromium.events.FinishLoadingEvent;
import com.teamdev.jxbrowser.chromium.events.LoadAdapter;
import com.teamdev.jxbrowser.chromium.swing.BrowserView;

import javax.swing.*;
import java.awt.*;

public class FXBrowserPage extends AbstractStage{
    BrowserView view;
    JFrame frame;

    JButton back;
    JButton forward;
    JButton refresh;
    JTextField f;
    public FXBrowserPage(String url){
        view = new BrowserView();
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
                        frame.dispose();
                    }
                    else if (url.startsWith(MSAuth.redirectUrlSuffix)){
                        frame.dispose();
                        int start = url.indexOf("?code=");
                        String temp = url.substring(start);
                        int end = temp.indexOf("&lc=");
                        System.out.println(new MSAuth().getUser(temp.substring(6, end)));
                    }
                }).start();
            }
        });
        frame = new JFrame();
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

        frame.show();
    }
    public void open() {

    }
}
