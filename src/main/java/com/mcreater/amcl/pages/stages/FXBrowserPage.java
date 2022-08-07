package com.mcreater.amcl.pages.stages;

import com.mcreater.amcl.api.auth.MSAuth;
import com.mcreater.amcl.nativeInterface.ResourceGetter;
import com.mcreater.amcl.pages.interfaces.Fonts;
import com.teamdev.jxbrowser.chromium.CookieStorage;
import com.teamdev.jxbrowser.chromium.events.FinishLoadingEvent;
import com.teamdev.jxbrowser.chromium.events.LoadAdapter;
import com.teamdev.jxbrowser.chromium.swing.BrowserView;
import org.controlsfx.dialog.Wizard;

import javax.swing.*;
import javax.swing.text.html.HTMLDocument;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeListener;

public class FXBrowserPage extends AbstractStage{
    BrowserView view;
    JFrame frame;

    JButton back;
    JButton forward;
    JButton refresh;
    JLabel l;
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
                        frame.hide();
                    }
                    else if (url.startsWith(MSAuth.redirectUrlSuffix)){
                        frame.hide();
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
        frame.setLayout(new BoxLayout(frame.getContentPane(), BoxLayout.PAGE_AXIS));

        back = new JButton("back");
        back.addActionListener(actionEvent -> view.getBrowser().goBack());
        forward = new JButton("forward");
        forward.addActionListener(actionEvent -> view.getBrowser().goForward());
        refresh = new JButton("refresh");
        refresh.addActionListener(actionEvent -> view.getBrowser().reload());

        new Thread("Button Thread"){
            public void run(){
                while (true){
                    back.setEnabled(view.getBrowser().canGoBack());
                    forward.setEnabled(view.getBrowser().canGoForward());
                    l.setText(view.getBrowser().getURL());
                }
            }
        }.start();

        l = new JLabel("   "+url);
        l.setFont(Fonts.awt_t_f);
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.X_AXIS));
        panel.add(back);
        panel.add(forward);
        panel.add(refresh);
        panel.add(l);

        back.setFont(Fonts.awt_t_f);
        forward.setFont(Fonts.awt_t_f);
        refresh.setFont(Fonts.awt_t_f);

        frame.getContentPane().add(panel);
        frame.getContentPane().add(view);
        frame.pack();
        frame.setBounds(100, 100, 500, 620);
        frame.setLocationRelativeTo(null);
        frame.show();
    }
    public void open() {

    }
}
