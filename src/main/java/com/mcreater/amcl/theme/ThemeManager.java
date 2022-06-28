package com.mcreater.amcl.theme;

import com.mcreater.amcl.Application;
import com.mcreater.amcl.nativeInterface.ResourceGetter;
import com.mcreater.amcl.pages.interfaces.AbstractAnimationPage;
import com.mcreater.amcl.pages.interfaces.SettingPage;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Vector;

public class ThemeManager {
    public static String themeName = "default";
    Logger logger = LogManager.getLogger(ThemeManager.class);
    public void setThemeName(String name){
        themeName = name;
    }
    public void applyTopBar(VBox topBar){
        String cssPath = String.format("assets/themes/%s/topBar.css", themeName);
        if (new ResourceGetter().get(cssPath) == null){
            logger.warn("failed to load css file for top bar!");
        }
        else{
            topBar.getStylesheets().add(cssPath);
        }
    }
    public void apply(Application application) throws IllegalAccessException {
        Object o;
        Vector<AbstractAnimationPage> pages = new Vector<>();
        Vector<Node> controls = new Vector<>();
        String theme_base_path = "assets/themes/%s/%s.css";
        for (Field f : application.getClass().getDeclaredFields()) {
            f.setAccessible(true);
            o = f.get(application);
            if (o instanceof AbstractAnimationPage) {
                pages.add((AbstractAnimationPage) o);
            }
        }
        for (AbstractAnimationPage page : pages){
            controls.addAll(GetAllNodes(page));
            for (Field f : page.getClass().getDeclaredFields()){
                f.setAccessible(true);
                Object o1 = f.get(page);
                if (o1 instanceof SettingPage){
                    ((Parent) o1).getStylesheets().add(String.format(theme_base_path, themeName, o1.getClass().getSimpleName()));
                    controls.addAll(GetAllNodes(((SettingPage) o1).content));
                }
            }
        }
        for (Node n : controls){
            String sheetPath = String.format(theme_base_path, themeName, n.getClass().getSimpleName());
            if (!(new ResourceGetter().get(sheetPath) == null)){
                ((Parent) n).getStylesheets().add(sheetPath);
            }
        }
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
