package com.mcreater.amcl.theme;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXSlider;
import com.mcreater.amcl.api.reflect.ReflectHelper;
import com.mcreater.amcl.controls.SettingPage;
import com.mcreater.amcl.nativeInterface.ResourceGetter;
import com.mcreater.amcl.pages.interfaces.AbstractAnimationPage;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.beans.value.WritableValue;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.TitledPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.web.WebView;
import javafx.util.Duration;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Map;
import java.util.Vector;
import java.util.concurrent.ConcurrentHashMap;

public class ThemeManager {
    public static String themeName = "default";
    static Logger logger = LogManager.getLogger(ThemeManager.class);
    static Vector<JFXButton> buttons = new Vector<>();
    static Map<Parent, String> simpleParentsConstable = new ConcurrentHashMap<>();
    public static void setThemeName(String name){
        themeName = name;
    }
    public static void applyTopBar(VBox topBar){
        String cssPath = String.format("assets/themes/%s/TopBar.css", themeName);
        if (ResourceGetter.get(cssPath) == null){
            logger.warn("failed to load css file for top bar!");
        }
        else{
            topBar.getStylesheets().add(cssPath);
        }
        String theme_base_path = "assets/themes/%s/%s.css";
        for (Node n : GetAllNodes(topBar)){
            loadButtonAnimates(n);
            if (n instanceof JFXButton){
                String sheetPath = String.format(theme_base_path, themeName, n.getClass().getSimpleName());
                if (!(ResourceGetter.get(sheetPath) == null)){
                    ((Parent) n).getStylesheets().add(sheetPath);
                }
            }
        }
    }
    public static void applyNode(Parent n) {
        n.getStylesheets().add(String.format(ThemeManager.getPath(), n.getClass().getSimpleName()));
        simpleParentsConstable.put(n, n.getClass().getSimpleName());
    }
    public static void applyNode(Parent n, String clazz) {
        n.getStylesheets().add(String.format(ThemeManager.getPath(), clazz));
        simpleParentsConstable.put(n, clazz);
    }
    public static void apply(Vector<AbstractAnimationPage> pages) throws IllegalAccessException{
        Object o;
        Vector<Node> controls = new Vector<>();
        String theme_base_path = "assets/themes/%s/%s.css";
        for (AbstractAnimationPage page : pages){
            controls.addAll(GetAllNodes(page));
            for (Field f : ReflectHelper.getFields(page)){
                f.setAccessible(true);
                Object o1 = f.get(page);
                if (o1 instanceof SettingPage){
                    ((Parent) o1).getStylesheets().add(String.format(theme_base_path, themeName, o1.getClass().getSimpleName()));
                    controls.addAll(GetAllNodes(((SettingPage) o1).content));
                }
            }
        }
        for (Node n : controls){
            loadButtonAnimates(n);
            String sheetPath = String.format(theme_base_path, themeName, n.getClass().getSimpleName());
            logger.info(String.format("loading style for control %s", n.getClass().getSimpleName()));
            if (!(ResourceGetter.get(sheetPath) == null)){
                ((Parent) n).getStylesheets().clear();
                ((Parent) n).getStylesheets().add(sheetPath);
            }
            else {
                logger.warn(String.format("failed load style for control %s !", n.getClass().getSimpleName()));
            }
        }
        simpleParentsConstable.forEach((parent, s) -> parent.getStylesheets().clear());
        simpleParentsConstable.forEach(ThemeManager::applyNode);
    }
    public static Node loadSingleNodeAnimate(Node node){
        loadButtonAnimates(node);
        return node;
    }

    public static void loadButtonAnimateParent(Node node){
        generateAnimations(node, 0.6D, 1D, 200, node.opacityProperty());
    }
    public static void setButtonRadius(double radius){
        try {
            buttons.forEach(button -> button.setStyle(String.format("-fx-border-radius: %fpx; -fx-background-radius: %fpx", radius, radius)));
        }
        catch (Exception ignored){

        }
    }
    public static void loadButtonAnimates(Node... nodes){
        for (Node button : nodes) {
            if (button instanceof JFXButton){
                ((JFXButton) button).setButtonType(JFXButton.ButtonType.RAISED);
                button.setStyle("-fx-border-radius: 0px; -fx-background-radius: 0px");
                buttons.add((JFXButton) button);
            }
            if (button instanceof JFXButton || button instanceof JFXSlider || button instanceof JFXComboBox){
                button.setCursor(Cursor.HAND);
            }

            if (!(button instanceof Pane) && !(button instanceof SettingPage) && !(button instanceof WebView)) {
                generateAnimations(button, 0.6D, 1D, 200, button.opacityProperty());
            }
            else if (button instanceof Pane){
                loadButtonAnimates(GetAllNodes((Parent) button).toArray(new Node[0]));
            }
            if (button instanceof TitledPane){
                loadButtonAnimates(GetAllNodes((Parent) ((TitledPane) button).getContent()).toArray(new Node[0]));
            }
        }
    }
    public static <T> void generateAnimations(@NotNull Node button, T va1, T va2, double duration, WritableValue<T> target){
        if (target == button.opacityProperty()){
            button.setOpacity((double) va1);
        }
        KeyValue v1 = new KeyValue(target, va1);
        KeyValue v2 = new KeyValue(target, va2);
        Timeline in = new Timeline();
        in.setCycleCount(1);
        in.getKeyFrames().clear();
        in.getKeyFrames().add(new KeyFrame(Duration.ZERO, v1));
        in.getKeyFrames().add(new KeyFrame(new Duration(duration), v2));

        Timeline out = new Timeline();
        out.setCycleCount(1);
        out.getKeyFrames().clear();
        out.getKeyFrames().add(new KeyFrame(Duration.ZERO, v2));
        out.getKeyFrames().add(new KeyFrame(new Duration(duration), v1));
        button.setOnMouseEntered(event -> {
            out.stop();
            in.playFromStart();
        });
        button.setOnMouseExited(event -> {
            in.stop();
            out.playFromStart();
        });
    }
    public static String getPath(){
        return "assets/themes/" + ThemeManager.themeName + "/%s.css";
    }

    public static ArrayList<Node> GetAllNodes(Parent root){
        ArrayList<Node> Descendents = new ArrayList<>();
        root.getChildrenUnmodifiable().forEach(n -> {
            if (!Descendents.contains(n)){
                Descendents.add(n);
            }
            if (n instanceof Pane){
                Descendents.addAll(GetAllNodes((Pane) n));
            }
        });
        return Descendents;
    }
}
