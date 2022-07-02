package com.mcreater.amcl.pages;

import com.mcreater.amcl.Application;
import com.mcreater.amcl.api.curseApi.CurseAPI;
import com.mcreater.amcl.api.curseApi.mod.CurseModModel;
import com.mcreater.amcl.api.curseApi.modFile.CurseModFileModel;
import com.mcreater.amcl.controls.ModFile;
import com.mcreater.amcl.pages.interfaces.AbstractAnimationPage;
import com.mcreater.amcl.pages.interfaces.SettingPage;
import com.mcreater.amcl.util.SetSize;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.scene.Node;
import javafx.scene.control.TitledPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;

import java.util.Comparator;
import java.util.Vector;

public class ModDownloadPage extends AbstractAnimationPage {
    public Vector<CurseModModel> reqMods;
    VBox v;
    Vector<ModFile> uis = new Vector<>();
    ChangeListener<Boolean> changeListener = (observable, oldValue, newValue) -> {};
    boolean coreSelected = false;
    ModFile last;
    Thread loadThread;
    String selectedVersion;
    GridPane p;
    public ModDownloadPage(double width, double height) {
        super(width, height);
        reqMods = new Vector<>();
        l = Application.ADDMODSPAGE;
        set();
        p = new GridPane();
        v = new VBox();
        SetSize.set(p, width, height);
        p.add(new SettingPage(800, 480 - 45, v), 0, 0, 1, 1);
        this.add(p, 0, 0, 1, 1);
    }
    public void setModContent(CurseModModel model){
        this.setDisable(true);
        do {
            try {
                Thread.sleep(10);
            } catch (InterruptedException ignored) {
            }
        } while (this.v.getChildren().size() != 0);
        loadThread = new Thread(() -> {
            Vector<String> versions = new Vector<>();
            Vector<CurseModFileModel> files = CurseAPI.getModFiles(model);
            for (CurseModFileModel m : files){
                for (String s : ModFile.getModLoaders(m.gameVersions, false)){
                    if (!versions.contains(s)){
                        versions.add(s);
                    }
                }
            }
            versions.sort(new VersionConparsion());
            for (String s1 : versions){
                TitledPane pane = new TitledPane();
                pane.setText(s1);
                pane.setExpanded(false);
                SetSize.setWidth(pane, 800);
                VBox b = new VBox();
                for (CurseModFileModel u : files){
                    if (u.gameVersions.contains(s1)){
                        b.getChildren().add(new ModFile(u, s1));
                    }
                }
                pane.setContent(b);
                for (Node n : b.getChildren()){
                    ModFile file = (ModFile) n;
                    uis.add(file);
                    changeListener = (observable, oldValue, newValue) -> {
                        if (last == file || last == null){
                            coreSelected = newValue;
                        }
                        if (newValue) {
                            last = file;
                            selectedVersion = file.version;
                            int temp = uis.indexOf(file);
                            for (int i = 0; i < uis.size(); i++) {
                                if (i != temp) {
                                    uis.get(i).checkBox.selectedProperty().set(false);
                                }
                            }
                        }
                        System.out.println(coreSelected);
                    };
                    file.checkBox.selectedProperty().addListener(this.changeListener);
                }
                Platform.runLater(() -> v.getChildren().add(pane));
            }
            this.setDisable(false);
        });
        loadThread.start();
    }
    public void refresh() {

    }
    public void refreshLanguage() {
        this.name = Application.languageManager.get("ui.moddownloadpage.name");
    }
    public void refreshType() {

    }
    public void onExitPage() {
        if (loadThread != null){
            loadThread.stop();
        }
        this.uis.clear();
        this.v.getChildren().clear();
    }
    public static class VersionConparsion implements Comparator<String> {
        public int compare(String compareValue1, String compareValue2) {
            compareValue1 = clearSnapShotVersion(compareValue1);
            compareValue2 = clearSnapShotVersion(compareValue2);
            String[] valueSplit1 = compareValue1.split("[.]");
            String[] valueSplit2 = compareValue2.split("[.]");
            int minLength = valueSplit1.length;
            if (minLength > valueSplit2.length) {
                minLength = valueSplit2.length;
            }
            for (int i = 0; i < minLength; i++) {
                int value1 = Integer.parseInt(valueSplit1[i]);
                int value2 = Integer.parseInt(valueSplit2[i]);
                if(value1 > value2){
                    return 1;
                }else if(value1 < value2){
                    return -1;
                }
            }
            return valueSplit1.length - valueSplit2.length;
        }
        public String clearSnapShotVersion(String raw){
            return raw.replace("-Snapshot", "");
        }
    }
}
