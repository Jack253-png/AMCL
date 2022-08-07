package com.mcreater.amcl.pages.stages;

import com.mcreater.amcl.util.operatingSystem.LocateHelper;
import org.cef.*;
import org.cef.browser.*;
import org.cef.callback.CefBeforeDownloadCallback;
import org.cef.callback.CefDownloadItem;
import org.cef.callback.CefDownloadItemCallback;
import org.cef.handler.*;
import org.cef.network.CefRequest;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Locale;

public class JcefBrowserPage extends AbstractStage{
    JFrame frame;
    public JcefBrowserPage(String url){
        CefApp.addAppHandler(new CefAppHandlerAdapter(null) {
            @Override
            public void stateHasChanged(org.cef.CefApp.CefAppState state) {
                if (state == CefApp.CefAppState.TERMINATED) System.exit(0);
            }
        });
        CefSettings settings = new CefSettings();
        settings.windowless_rendering_enabled = false;
        settings.locale = Locale.getDefault().getDisplayLanguage();
        CefApp cefApp=CefApp.getInstance(settings);
        CefClient cefClient = cefApp.createClient();
        cefClient.addLoadHandler(new CefLoadHandler() {
            public void onLoadingStateChange(CefBrowser cefBrowser, boolean b, boolean b1, boolean b2) {

            }
            public void onLoadStart(CefBrowser cefBrowser, CefFrame cefFrame, CefRequest.TransitionType transitionType) {
                System.out.printf("%s\n", transitionType);
            }
            public void onLoadEnd(CefBrowser cefBrowser, CefFrame cefFrame, int i) {
                System.out.printf("%s\n", i);
            }
            public void onLoadError(CefBrowser cefBrowser, CefFrame cefFrame, ErrorCode errorCode, String s, String s1) {
                System.err.printf("%s %s %s\n", errorCode.getCode(), s, s1);
            }
        });
        CefBrowser cefBrowser = cefClient.createBrowser(url, false, false);
        frame = new JFrame();
        frame.getContentPane().add(cefBrowser.getUIComponent(), BorderLayout.CENTER);
        frame.pack();
        frame.setTitle("Test For MSA");
        frame.setSize(500, 620);
        frame.setResizable(false);
        frame.setVisible(true);
        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                CefApp.getInstance().dispose();
                frame.dispose();
            }
        });
        cefClient.addDisplayHandler(new CefDisplayHandler() {
            @Override
            public void onAddressChange(CefBrowser cefBrowser, CefFrame cefFrame, String s) {
                if (s.contains("https://login.live.com/oauth20_desktop.srf?code=")){
                    System.out.println(s.substring(s.indexOf("=")+1));
                }
            }

            @Override
            public void onTitleChange(CefBrowser cefBrowser, String s) {

            }

            @Override
            public boolean onTooltip(CefBrowser cefBrowser, String s) {
                return false;
            }

            @Override
            public void onStatusMessage(CefBrowser cefBrowser, String s) {

            }

            @Override
            public boolean onConsoleMessage(CefBrowser cefBrowser, CefSettings.LogSeverity logSeverity, String s, String s1, int i) {
                return false;
            }

            @Override
            public boolean onCursorChange(CefBrowser cefBrowser, int i) {
                return false;
            }
        });
    }
    public void open() {
        frame.show();
    }
}
