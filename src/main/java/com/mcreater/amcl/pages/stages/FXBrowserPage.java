package com.mcreater.amcl.pages.stages;

import com.mcreater.amcl.api.auth.MSAuth;
import com.mcreater.amcl.api.auth.users.MicrosoftUser;
import com.mcreater.amcl.nativeInterface.ResourceGetter;
import com.mcreater.amcl.pages.dialogs.ProcessDialog;
import com.mcreater.amcl.pages.interfaces.Fonts;
import com.mcreater.amcl.util.NetworkUtils;
import com.teamdev.jxbrowser.chromium.BrowserException;
import com.teamdev.jxbrowser.chromium.Cookie;
import com.teamdev.jxbrowser.chromium.CookieStorage;
import com.teamdev.jxbrowser.chromium.events.FinishLoadingEvent;
import com.teamdev.jxbrowser.chromium.events.LoadAdapter;
import com.teamdev.jxbrowser.chromium.swing.BrowserView;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowEvent;
import java.util.HashMap;

public class FXBrowserPage extends AbstractStage{
    BrowserView view;
    public JFrame frame;

    JButton back;
    JButton forward;
    JButton refresh;
    public MicrosoftUser user;
    public Exception ex;
    public Runnable r = () -> {};
    ProcessDialog dialog;
    public static class CookieMap extends HashMap<String, Cookie> {
        final CookieStorage storage;
        public CookieMap(final CookieStorage storage){
            this.storage = storage;
            reloadCookies();
        }
        public void reloadCookies(){
            for (Cookie c : storage.getAllCookies()){
                put(c.getName(), c);
            }
        }
    }

    public void setDialog(ProcessDialog dialog){
        this.dialog = dialog;
    }
    public FXBrowserPage(String url){
        try {
            view = new BrowserView();
        }
        catch (BrowserException e){
            ex = e;
            return;
        }
        init(url);
    }
    public void init(String url){
        CookieStorage storage = view.getBrowser().getCookieStorage();
        storage.deleteAll();
        storage.save();

        CookieMap map = new CookieMap(storage);

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
                            map.reloadCookies();
                            Cookie emailCookie = map.get("MSPPre");

                            String email = NetworkUtils.decodeURL(emailCookie.getValue()).replace(map.get("MSPCID").getValue(), "").replace("|", "");

                            System.err.println(email);

                            MSAuth auth = new MSAuth();
                            auth.bindDialog(dialog);
                            user = auth.getUser(temp.substring(6, end));
                        }
                        catch (Exception e){
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
                    this.dispose();
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

        JPanel loadType = new JPanel();

        loadType.add(new JProgressBar());

        frame.add(bar);
        frame.add(loadType);
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
