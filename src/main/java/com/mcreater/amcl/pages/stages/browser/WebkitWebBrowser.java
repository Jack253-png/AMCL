package com.mcreater.amcl.pages.stages.browser;

import com.mcreater.amcl.api.auth.MSAuth;
import com.mcreater.amcl.theme.ThemeManager;
import com.mcreater.amcl.util.svg.Icons;
import javafx.application.Platform;
import javafx.concurrent.Worker;
import javafx.scene.Scene;
import javafx.scene.web.WebView;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.net.CookieHandler;
import java.net.CookieManager;

public class WebkitWebBrowser extends AbstractWebBrowser {
    Logger logger = LogManager.getLogger(WebkitWebBrowser.class);
    WebView webView;
    public WebkitWebBrowser(String url){
        initOwner(new Stage());
        initStyle(StageStyle.DECORATED);

        webView = new WebView();
        CookieManager manager = new CookieManager();
        CookieHandler.setDefault(manager);
        manager.getCookieStore().getCookies().forEach(httpCookie -> System.out.println(httpCookie.getValue()));
        manager.getCookieStore().removeAll();
        webView.getEngine().titleProperty().addListener((observable, oldValue, newValue) -> setTitle(newValue));
        webView.getEngine().locationProperty().addListener((observable, oldValue, newValue) -> {
            loginThread = new Thread(() -> {
                logger.info("Redirected to " + newValue);
                if (newValue.contains("The%20user%20has%20denied%20access%20to%20the%20scope%20requested%20by%20the%20client%20application.")){
                    webView.getEngine().load(MSAuth.LOGIN_URL);
                }
                else if (newValue.startsWith(MSAuth.REDIRECT_URL_SUFFIX)){
                    Platform.runLater(this::close);
                    int start = newValue.indexOf("?code=");
                    String temp = newValue.substring(start);
                    int end = temp.indexOf("&lc=");
                    try {
                        MSAuth.AUTH_INSTANCE.setUpdater((integer, s) -> dialog.setV(0, integer, s));
                        user = MSAuth.AUTH_INSTANCE.getUser(temp.substring(6, end));
                    }
                    catch (Exception e){
                        ex = e;
                    }
                }
            });
            loginThread.start();
        }
        );
        webView.getEngine().load(url);
        webView.getEngine().getLoadWorker().progressProperty().addListener((observable, oldValue, newValue) -> logger.info(String.format("Load precent : %.2f", newValue.doubleValue() * 100) + "%"));
        webView.getEngine().getLoadWorker().messageProperty().addListener((observable, oldValue, newValue) -> logger.info("Web Engine message : " + newValue));
        webView.getEngine().getLoadWorker().stateProperty().addListener((o, ov, nv) -> {
            if (nv == Worker.State.FAILED) {
                logger.error("WebView Failed: ", webView.getEngine().getLoadWorker().getException());
            }
        });

        this.getIcons().add(Icons.fxIcon.get());
        webView.getStylesheets().add(String.format(ThemeManager.getPath(), "WebView"));

        setOnCloseRequest(event -> webView.getEngine().load("about::blank"));
    }
    public void open() {
        Scene s = new Scene(webView);
        this.setScene(s);
        this.show();
    }
}
