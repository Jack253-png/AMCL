package com.mcreater.amcl.pages.stages.browser;

import com.mcreater.amcl.StableMain;
import com.mcreater.amcl.api.auth.users.MicrosoftUser;
import com.mcreater.amcl.pages.dialogs.commons.ProcessDialog;
import com.mcreater.amcl.pages.stages.AbstractStage;

public abstract class AbstractWebBrowser extends AbstractStage {
    public Thread loginThread = new Thread(() -> {});
    public MicrosoftUser user;
    public Exception ex;
    ProcessDialog dialog;
    public void setDialog(ProcessDialog dialog){
        this.dialog = dialog;
    }
    public static AbstractWebBrowser getBrowserImpl(String url) throws Exception {
        return getBrowserImpl(url, BrowserType.WEBKIT);
    }
    public static AbstractWebBrowser getBrowserImpl(String url, BrowserType type) throws Exception {
        switch (type) {
            default:
            case WEBKIT:
                return new WebkitWebBrowser(url);
            case CHROUMIUM:
                StableMain.checkJXBrowser2();
                return new ChroumiumWebBrowser(url);
        }
    }
    public enum BrowserType {
        WEBKIT,
        CHROUMIUM
    }
}
