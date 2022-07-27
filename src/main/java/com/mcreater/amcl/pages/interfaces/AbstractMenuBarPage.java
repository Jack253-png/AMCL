package com.mcreater.amcl.pages.interfaces;

import com.jfoenix.controls.JFXButton;
import com.mcreater.amcl.Launcher;
import com.mcreater.amcl.util.SetSize;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.layout.VBox;
import javafx.util.Pair;

import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

public abstract class AbstractMenuBarPage extends AbstractAnimationPage{
    public Vector<SettingPage> pages;
    public Vector<JFXButton> menubuttons;
    public Map<SettingPage, JFXButton> totalMap;
    public SettingPage last;
    public VBox mainBox;
    public JFXButton setted;
    public VBox menu;
    LoadPageEvent e = (i) -> {};
    public interface LoadPageEvent{
        void run(int i);
    }
    public AbstractMenuBarPage(double width, double height) {
        super(width, height);
        pages = new Vector<>();
        menubuttons = new Vector<>();
        menu = new VBox();
        menu.setId("config-menu");
        SetSize.set(menu, this.width / 4,this.height - Launcher.barSize);
        totalMap = new HashMap<>();
        last = null;
    }
    public void setOnAction(LoadPageEvent runnable){
        this.e = runnable;
    }
    public void setType(JFXButton b){
        setted = b;
        for (Node bs : menu.getChildren()){
            bs.setDisable(bs == b);
        }
    }
    public void setP1(int i){
        SettingPage p = pages.get(i);
        if (p.CanMovePage() && last != p) {
            if (last != null) {
                last.setOut();
            }
            last = p;
            last.setIn();
            last.setTypeAll(true);
            last.in.stop();
            last.setTypeAll(false);
            this.getChildren().clear();
            mainBox = new VBox();
            mainBox.setAlignment(Pos.TOP_CENTER);
            mainBox.getChildren().addAll(p);
            SetSize.set(mainBox, this.width / 4 * 3, this.height - Launcher.barSize);
            this.add(menu, 0, 0, 1, 1);
            this.add(mainBox, 1, 0, 1, 1);
        }
        setType(menubuttons.get(i));
        e.run(i);
    }
    public void addNewPair(Pair<JFXButton, SettingPage> pair){
        pages.add(pair.getValue());
        menubuttons.add(pair.getKey());
        totalMap.put(pair.getValue(), pair.getKey());
        menu.getChildren().add(pair.getKey());
    }
    public void setButtonType(JFXButton.ButtonType type){
        menubuttons.forEach(b -> b.setButtonType(type));
    }
}
