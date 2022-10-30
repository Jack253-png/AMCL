package com.mcreater.amcl.pages.stages;

import com.google.gson.Gson;
import com.mcreater.amcl.Launcher;
import com.mcreater.amcl.StableMain;
import com.mcreater.amcl.api.auth.MSAuth;
import com.mcreater.amcl.api.auth.users.MicrosoftUser;
import com.mcreater.amcl.nativeInterface.OSInfo;
import com.mcreater.amcl.nativeInterface.ResourceGetter;
import com.mcreater.amcl.pages.dialogs.commons.ProcessDialog;
import com.mcreater.amcl.patcher.DepenciesLoader;
import com.mcreater.amcl.tasks.Task;
import com.mcreater.amcl.util.concurrent.Sleeper;
import com.mcreater.amcl.util.svg.Icons;
import com.teamdev.jxbrowser.browser.Browser;
import com.teamdev.jxbrowser.engine.Engine;
import com.teamdev.jxbrowser.engine.EngineOptions;
import com.teamdev.jxbrowser.engine.ProprietaryFeature;
import com.teamdev.jxbrowser.engine.RenderingMode;
import com.teamdev.jxbrowser.view.javafx.BrowserView;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.InputStreamReader;
import java.net.CookieHandler;
import java.net.CookieManager;
import java.util.Map;
import java.util.Vector;

import static com.mcreater.amcl.StableMain.checkJXBrowser;

public class NativeBrowserPage extends AbstractStage {
    Logger logger = LogManager.getLogger(NativeBrowserPage.class);
    public MicrosoftUser user;
    public Exception ex;
    ProcessDialog dialog;
    public void setDialog(ProcessDialog dialog){
        this.dialog = dialog;
    }
    public Thread loginThread = new Thread(() -> {});
    boolean started;
    public NativeBrowserPage(String url){
        initOwner(new Stage());
        initStyle(StageStyle.DECORATED);

        CookieManager manager = new CookieManager();
        CookieHandler.setDefault(manager);
        manager.getCookieStore().getCookies().forEach(httpCookie -> System.out.println(httpCookie.getValue()));
        manager.getCookieStore().removeAll();

        System.setProperty("jxbrowser.license.key", "6P835FT5HAPTB03TPIEFPGU5ECGJN8GMGDD79MD7Y52NVP0K0IV6FHYZVQI25H0MLGI2");
        Engine engine = Engine.newInstance(
                EngineOptions.newBuilder(RenderingMode.HARDWARE_ACCELERATED)
                        .enableProprietaryFeature(ProprietaryFeature.AAC)
                        .enableProprietaryFeature(ProprietaryFeature.H_264)
                        .build());
        engine.cookieStore().deleteAll();

        Browser browser = engine.newBrowser();
        BrowserView view = BrowserView.newInstance(browser);

        new Thread(() -> {
            do {
                Platform.runLater(() -> setTitle(view.getBrowser().title()));
                String newValue = view.getBrowser().url();
                loginThread = new Thread(() -> {
                    if (newValue.contains("The%20user%20has%20denied%20access%20to%20the%20scope%20requested%20by%20the%20client%20application.")) {
                        view.getBrowser().navigation().loadUrl(MSAuth.LOGIN_URL);
                    } else if (newValue.startsWith(MSAuth.REDIRECT_URL_SUFFIX)) {
                        Platform.runLater(this::close);
                        int start = newValue.indexOf("?code=");
                        String temp = newValue.substring(start);
                        int end = temp.indexOf("&lc=");
                        try {
                            MSAuth.AUTH_INSTANCE.setUpdater((integer, s) -> dialog.setV(0, integer, s));
                            user = MSAuth.AUTH_INSTANCE.getUser(temp.substring(6, end));
                        } catch (Exception e) {
                            e.printStackTrace();
                            ex = e;
                        }
                        engine.close();
                    }
                });
                if (newValue.startsWith(MSAuth.REDIRECT_URL_SUFFIX) || newValue.contains("The%20user%20has%20denied%20access%20to%20the%20scope%20requested%20by%20the%20client%20application.")) {
                    if (!started) {
                        loginThread.start();
                        started = true;
                    }
                }
                Sleeper.sleep(1000);
            } while (ex == null && user == null);
        }).start();

        this.getIcons().add(Icons.fxIcon.get());
        browser.navigation().loadUrl(url);

        Scene s = new Scene(view);
        this.setScene(s);
    }
    public void open() {
        this.show();
    }
}
