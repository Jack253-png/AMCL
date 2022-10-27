package com.mcreater.amcl.pages.stages;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.utils.JFXSmoothScroll;
import com.mcreater.amcl.Launcher;
import com.mcreater.amcl.api.auth.MSAuth;
import com.mcreater.amcl.api.auth.users.MicrosoftUser;
import com.mcreater.amcl.controls.JFXProgressBar;
import com.mcreater.amcl.pages.dialogs.commons.ProcessDialog;
import com.mcreater.amcl.pages.interfaces.Fonts;
import com.mcreater.amcl.theme.ThemeManager;
import com.mcreater.amcl.util.FXUtils;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.concurrent.Worker;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.web.WebView;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.net.CookieHandler;
import java.net.CookieManager;
import java.net.HttpCookie;
import java.util.concurrent.Callable;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class NativeBrowserPage extends AbstractStage {
    Logger logger = LogManager.getLogger(NativeBrowserPage.class);
    WebView webView;
    public MicrosoftUser user;
    public Exception ex;
    ProcessDialog dialog;

    JFXButton refresh;
    JFXProgressBar loadState;
    VBox v = new VBox();
    public void setDialog(ProcessDialog dialog){
        this.dialog = dialog;
    }
    public Thread loginThread = new Thread();
    public NativeBrowserPage(String url){
        initOwner(new Stage());
        initStyle(StageStyle.DECORATED);

        refresh = new JFXButton();
        refresh.setGraphic(Launcher.getSVGManager().refresh(Bindings.createObjectBinding((Callable<Paint>) () -> Color.BLACK), 20, 20));
        refresh.setOnAction(event -> webView.getEngine().reload());
        refresh.setFont(Fonts.t_f);

        loadState = new JFXProgressBar(0);

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
        webView.getEngine().getLoadWorker().progressProperty().addListener((observable, oldValue, newValue) -> {
            JFXSmoothScroll.smoothScrollBarToValue(loadState, newValue.doubleValue());
            logger.info(String.format("Load precent : %.2f", newValue.doubleValue() * 100) + "%");
        });
        webView.getEngine().getLoadWorker().messageProperty().addListener((observable, oldValue, newValue) -> {
            logger.info("Web Engine message : " + newValue);
        });
        webView.getEngine().getLoadWorker().stateProperty().addListener((o, ov, nv) -> {
            if (nv == Worker.State.FAILED) {
                logger.error("WebView Failed: ", webView.getEngine().getLoadWorker().getException());
            }
        });

        refresh.minWidthProperty().bind(widthProperty());
        loadState.minWidthProperty().bind(widthProperty());
        refresh.maxWidthProperty().bind(widthProperty());
        loadState.maxWidthProperty().bind(widthProperty());

        FXUtils.ControlSize.setHeight(refresh, 50);
        FXUtils.ControlSize.setHeight(loadState, 3);

        this.getIcons().add(new Image("assets/icons/grass.png"));

        ThemeManager.applyNode(webView);
        setResizable(false);

        v.getChildren().addAll(refresh, loadState, webView);
        ThemeManager.loadButtonAnimates(v);
        setOnCloseRequest(event -> webView.getEngine().load("about::blank"));
    }
    public void open() {
        Scene s = new Scene(v);
        this.setScene(s);
        this.show();
    }
}
