package com.mcreater.amcl.theme;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXSlider;
import com.mcreater.amcl.Launcher;
import com.mcreater.amcl.api.reflect.ReflectHelper;
import com.mcreater.amcl.controls.AdvancedScrollPane;
import com.mcreater.amcl.lang.LanguageManager;
import com.mcreater.amcl.nativeInterface.ResourceGetter;
import com.mcreater.amcl.pages.interfaces.AbstractAnimationPage;
import com.mcreater.amcl.util.FXUtils;
import javafx.animation.Timeline;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.ObjectBinding;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.WritableValue;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.TitledPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Map;
import java.util.Vector;
import java.util.concurrent.ConcurrentHashMap;

import static com.mcreater.amcl.util.FXUtils.ColorUtil.reverse;

public class ThemeManager {
    public static String themeName = "dark";
    public static final SimpleObjectProperty<Color> themeIconDark = new SimpleObjectProperty<>(Color.BLACK);
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
    public static void addLis(ChangeListener<Color> listener) {
        listener.changed(themeIconDark, themeIconDark.get(), themeIconDark.get());
        themeIconDark.addListener(listener);
    }

    public static ObjectBinding<Color> createPaintBinding() {
        return Bindings.createObjectBinding(() -> reverse(themeIconDark.get()));
    }

    public static void applyNode(Parent n) {
        applyNode(n, n.getClass().getSimpleName());
    }
    public static void applyNode(Parent n, String clazz) {
        String sheetPath = String.format(ThemeManager.getPath(), n.getClass().getSimpleName());
        if (!(ResourceGetter.get(sheetPath) == null)) {
            n.getStylesheets().add(sheetPath);
        }
        simpleParentsConstable.put(n, clazz);
    }
    public static void apply(Vector<AbstractAnimationPage> pages) throws IllegalAccessException{
        Vector<Node> controls = new Vector<>();
        String theme_base_path = "assets/themes/%s/%s.css";
        for (AbstractAnimationPage page : pages){
            controls.addAll(GetAllNodes(page));
            for (Field f : ReflectHelper.getFields(page)){
                f.setAccessible(true);
                Object o1 = f.get(page);
                if (o1 instanceof AdvancedScrollPane){
                    ((Parent) o1).getStylesheets().add(String.format(theme_base_path, themeName, o1.getClass().getSimpleName()));
                    controls.addAll(GetAllNodes(((AdvancedScrollPane) o1).content));
                }
            }
        }
        for (Node n : controls){
            loadButtonAnimates(n);
            String sheetPath = String.format(theme_base_path, themeName, n.getClass().getSimpleName());
            logger.info(String.format("loading style for control %s", n.getClass().getSimpleName()));
            if (n instanceof Parent) {
                ((Parent) n).getStylesheets().clear();
                ((Parent) n).getStylesheets().add(sheetPath);
            }
        }
        simpleParentsConstable.forEach((parent, s) -> parent.getStylesheets().clear());
        simpleParentsConstable.forEach(ThemeManager::applyNode);
    }
    public static Node loadSingleNodeAnimate(Node node){
        if (node instanceof Parent) applyNode((Parent) node);
        loadButtonAnimates(node);
        return node;
    }

    public static void loadButtonAnimateParent(Node node){
        if (node instanceof Parent) applyNode((Parent) node);
        generateAnimations(node, 0.6D, 1D, 200, node.opacityProperty());
    }
    public static void setButtonRadius(double radius){
        try {
            buttons.forEach(button -> button.setStyle(String.format("-fx-border-radius: %fpx; -fx-background-radius: %fpx", radius, radius)));
        }
        catch (Exception ignored){

        }
    }
    public static void loadButtonAnimates(Node... nodes) {
        for (Node n : nodes) {
            if (n instanceof Parent) applyNode((Parent) n);
        }
        for (Node button : nodes) {
            if (button instanceof JFXButton){
                ((JFXButton) button).setButtonType(JFXButton.ButtonType.RAISED);
                button.setStyle("-fx-border-radius: 0px; -fx-background-radius: 0px");
                buttons.add((JFXButton) button);
            }
            if (button instanceof JFXButton || button instanceof JFXSlider || button instanceof JFXComboBox){
                button.setCursor(Cursor.HAND);
            }

            if (!(button instanceof Pane) && !(button instanceof AdvancedScrollPane)) {
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
        Timeline in = FXUtils.AnimationUtils.genSingleCycleAnimation(target, va1, va2, 0, duration);
        Timeline out = FXUtils.AnimationUtils.genSingleCycleAnimation(target, va2, va1, 0, duration);

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

    public static void freshTheme() throws IllegalAccessException {
        ThemeManager.apply(LanguageManager.bindedPages);
        ThemeManager.applyTopBar(Launcher.top);
        Launcher.setPageCore();
        LanguageManager.bindedPages.forEach(AbstractAnimationPage::refreshType);
    }
}
