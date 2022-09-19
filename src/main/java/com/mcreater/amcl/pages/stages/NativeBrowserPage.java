package com.mcreater.amcl.pages.stages;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.utils.JFXSmoothScroll;
import com.mcreater.amcl.api.auth.MSAuth;
import com.mcreater.amcl.api.auth.users.MicrosoftUser;
import com.mcreater.amcl.controls.JFXProgressBar;
import com.mcreater.amcl.pages.dialogs.commons.ProcessDialog;
import com.mcreater.amcl.pages.interfaces.Fonts;
import com.mcreater.amcl.theme.ThemeManager;
import com.mcreater.amcl.util.FXUtils;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Worker;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.web.WebView;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

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

        refresh = new JFXButton("Refresh");
        refresh.setOnAction(event -> webView.getEngine().reload());
        refresh.setFont(Fonts.t_f);

        loadState = new JFXProgressBar(0);

        webView = new WebView();
        webView.getEngine().titleProperty().addListener((observable, oldValue, newValue) -> setTitle(newValue));
        webView.getEngine().locationProperty().addListener((observable, oldValue, newValue) -> {
            loginThread = new Thread(() -> {
                logger.info("Redirected to " + newValue);
                if (newValue.contains("The%20user%20has%20denied%20access%20to%20the%20scope%20requested%20by%20the%20client%20application.")){
                    webView.getEngine().load(MSAuth.loginUrl);
                }
                else if (newValue.startsWith(MSAuth.redirectUrlSuffix)){
                    Platform.runLater(this::close);
                    int start = newValue.indexOf("?code=");
                    String temp = newValue.substring(start);
                    int end = temp.indexOf("&lc=");
                    try {
                        MSAuth auth = new MSAuth();
                        auth.bindDialog(dialog);
                        user = auth.getUser(temp.substring(6, end));
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
        webView.getEngine().getLoadWorker().messageProperty().addListener((observable, oldValue, newValue) -> logger.info("Web Engine message : " + newValue));

        refresh.minWidthProperty().bind(widthProperty());
        loadState.minWidthProperty().bind(widthProperty());
        refresh.maxWidthProperty().bind(widthProperty());
        loadState.maxWidthProperty().bind(widthProperty());

        FXUtils.ControlSize.setHeight(refresh, 50);
        FXUtils.ControlSize.setHeight(loadState, 3);

        this.getIcons().add(new Image("assets/icons/grass.png"));
        webView.getStylesheets().add(String.format(ThemeManager.getPath(), "WebView"));
        setResizable(false);

        v.getChildren().addAll(refresh, loadState, webView);
        ThemeManager.loadButtonAnimates(v);
    }
    public void open() {
        Scene s = new Scene(v);
        this.setScene(s);
        this.show();
    }
}
