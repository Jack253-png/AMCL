package com.mcreater.amcl.pages;

import com.jfoenix.controls.JFXButton;
import com.mcreater.amcl.Launcher;
import com.mcreater.amcl.controls.AdvancedScrollPane;
import com.mcreater.amcl.controls.JFXProgressBar;
import com.mcreater.amcl.controls.items.BooleanItem;
import com.mcreater.amcl.controls.items.IntItem;
import com.mcreater.amcl.controls.items.ListItem;
import com.mcreater.amcl.controls.items.MuiltButtonListItem;
import com.mcreater.amcl.lang.LanguageManager;
import com.mcreater.amcl.pages.dialogs.commons.SimpleDialogCreater;
import com.mcreater.amcl.pages.interfaces.AbstractMenuBarPage;
import com.mcreater.amcl.pages.interfaces.Fonts;
import com.mcreater.amcl.theme.ThemeManager;
import com.mcreater.amcl.util.FXUtils;
import com.mcreater.amcl.util.FileUtils;
import com.mcreater.amcl.util.J8Utils;
import com.mcreater.amcl.util.Timer;
import com.mcreater.amcl.util.concurrent.Sleeper;
import com.mcreater.amcl.util.java.JavaInfoGetter;
import com.mcreater.amcl.util.net.FasterUrls;
import com.mcreater.amcl.util.system.MemoryReader;
import javafx.application.Platform;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import org.apache.commons.lang3.tuple.ImmutablePair;

import java.io.File;
import java.util.Map;
import java.util.Objects;
import java.util.Vector;
import java.util.concurrent.ExecutionException;

import static com.mcreater.amcl.Launcher.ADDMODSPAGE;
import static com.mcreater.amcl.Launcher.CONFIGPAGE;
import static com.mcreater.amcl.Launcher.DOWNLOADADDONSELECTPAGE;
import static com.mcreater.amcl.Launcher.DOWNLOADMCPAGE;
import static com.mcreater.amcl.Launcher.MODDOWNLOADPAGE;
import static com.mcreater.amcl.Launcher.USERSELECTPAGE;
import static com.mcreater.amcl.Launcher.VERSIONINFOPAGE;
import static com.mcreater.amcl.Launcher.VERSIONSELECTPAGE;

public class ConfigPage extends AbstractMenuBarPage {
    Label title;
    VBox configs_box;
    public JFXButton java_add;
    public JFXButton java_get;
    public JFXButton java_find;
    final Map<String, String> langs;
    JFXButton setting;
    AdvancedScrollPane p1;
    public BooleanItem item;
    public IntItem item2;
    public ListItem<Label> item3;
    public MuiltButtonListItem<Label> item4;
    public ListItem<Label> item5;
    public IntItem item6;
    Map<String, String> servers;
    public Pane p;
    public com.jfoenix.controls.JFXProgressBar bar1;
    public com.jfoenix.controls.JFXProgressBar bar2;
    public JFXButton looklike_setting;
    AdvancedScrollPane p2;
    Label ltitle;
    VBox looklike_config_box;
    public BooleanItem item7;
    public ConfigPage(int width, int height) {
        super(width, height);
        l = Launcher.MAINPAGE;
        this.setAlignment(Pos.TOP_CENTER);

        double t_size = Launcher.barSize;

        langs = J8Utils.createMap(String.class, String.class,
                "简体中文", "CHINESE",
                "English(US)", "ENGLISH");

        servers = J8Utils.createMap(String.class, String.class,
                "MCBBS", "MCBBS",
                "BMCLAPI", "BMCLAPI",
                "MOJANG", "MOJANG");

        title = new Label();
        title.setFont(Fonts.b_f);

        item = new BooleanItem("", this.width / 4 * 3 - 10);
        item.cont.setSelected(Launcher.configReader.configModel.change_game_dir);
        item.cont.selectedProperty().addListener((observable, oldValue, newValue) -> Launcher.configReader.configModel.change_game_dir = newValue);

        item2 = new IntItem("", this.width / 4 * 3 - 10);
        item2.cont.setMax(J8Utils.getMcMaxMemory());
        item2.cont.setMin(16);
        item2.cont.setValue(Launcher.configReader.configModel.max_memory);
        item2.cont.setOrientation(Orientation.HORIZONTAL);
        item2.cont.valueProperty().addListener((observable, oldValue, newValue) -> Launcher.configReader.configModel.max_memory = newValue.intValue());

        java_add = new JFXButton();
        java_add.setFont(Fonts.t_f);
        java_add.setOnAction(event -> {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle(Launcher.languageManager.get("ui.configpage.java_choose.title"));
            fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter(Launcher.languageManager.get("ui.configpage.java_choose.filename.description"), "java.exe"),
                    new FileChooser.ExtensionFilter(Launcher.languageManager.get("ui.configpage.java_choose.filename.description2"), "java"));
            File choosed_path = fileChooser.showOpenDialog(Launcher.stage);
            if (choosed_path != null) {
                if (!Launcher.configReader.configModel.selected_java.contains(choosed_path.getPath())) {
                    Launcher.configReader.configModel.selected_java.add(choosed_path.getPath());
                    load_java_list();
                }
            }
        });

        java_get = new JFXButton();
        java_get.setFont(Fonts.t_f);
        java_get.setOnAction(event -> new Thread(() -> {
            java_get.setDisable(true);
            if (Launcher.configReader.configModel.selected_java.contains(Launcher.configReader.configModel.selected_java_index) && new File(Launcher.configReader.configModel.selected_java_index).exists()) {
                Vector<String> v;
                try {
                    v = JavaInfoGetter.get(new File(Launcher.configReader.configModel.selected_java_index));
                } catch (ExecutionException | InterruptedException e) {
                    throw new RuntimeException(e);
                }
                SimpleDialogCreater.create(Launcher.languageManager.get("ui.configpage.java_info.title"), Launcher.languageManager.get("ui.configpage.java_info.Headercontent", v.get(0), v.get(1)), "");
            }
            else{
                SimpleDialogCreater.create(Launcher.languageManager.get("ui.configpage.select_java.title"), Launcher.languageManager.get("ui.configpage.select_java.Headercontent"), "");
            }
            java_get.setDisable(false);
        }).start());

        java_find = new JFXButton();
        java_find.setFont(Fonts.t_f);
        java_find.setOnAction(event -> {
            java_find.setDisable(true);
            new Thread(() -> {
                try {
                    Vector<File> f = FileUtils.getJavaTotal();
                    Vector<String> fina = new Vector<>();
                    f.forEach(file -> {
                        if (!Launcher.configReader.configModel.selected_java.contains(file.getAbsolutePath())) {
                            fina.add(file.getAbsolutePath());
                        }
                    });
                    Launcher.configReader.configModel.selected_java.addAll(fina);
                    FXUtils.Platform.runLater(this::load_java_list);
                }
                catch (Exception e) {
                    e.printStackTrace();
                }
                java_find.setDisable(false);
            }).start();
        });

        item4 = new MuiltButtonListItem<>("", this.width / 4 * 3 - 10);
        item4.addButtons(java_find, java_get, java_add);
        item4.cont.setOnAction(event -> {
            if (item4.cont.getValue() == null && item4.cont.getItems().size() > 0) {
                item4.cont.getSelectionModel().select(0);
            }
            if (item4.cont.getSelectionModel().getSelectedItem() != null) {
                Launcher.configReader.configModel.selected_java_index = item4.cont.getSelectionModel().getSelectedItem().getText();
                Launcher.configReader.write();
            }
        });

        load_java_list();

        item3 = new ListItem<>("", this.width / 4 * 3 - 10);
        for (Map.Entry<String, String> entry : langs.entrySet()) {
            Label l = new Label(entry.getKey());
            l.setFont(Fonts.t_f);
            item3.cont.getItems().add(l);
        }
        item3.cont.getSelectionModel().select(getKey(Launcher.configReader.configModel.language.toString()));
        item3.cont.setOnAction(event -> {
            Launcher.configReader.configModel.language = LanguageManager.LanguageType.valueOf(langs.get(item3.cont.getValue().getText()));
            Launcher.languageManager.setLanguage(Launcher.configReader.configModel.language);
            Launcher.setTitle();
        });

        item5 = new ListItem<>("", this.width / 4 * 3 - 10);
        for (Map.Entry<String, String> entry : servers.entrySet()){
            Label l = new Label(entry.getKey());
            l.setFont(Fonts.t_f);
            item5.cont.getItems().add(l);
        }
        item5.cont.getSelectionModel().select(getKey2(Launcher.configReader.configModel.downloadServer.toString()));
        item5.cont.setOnAction(event -> Launcher.configReader.configModel.downloadServer = FasterUrls.Servers.valueOf(servers.get(item5.cont.getValue().getText())));

        item6 = new IntItem("", this.width / 4 * 3 - 10);
        item6.cont.setMax(8192);
        item6.cont.setMin(512);
        item6.cont.setValue(Launcher.configReader.configModel.downloadChunkSize);
        item6.cont.setOrientation(Orientation.HORIZONTAL);
        item6.cont.valueProperty().addListener((observable, oldValue, newValue) -> Launcher.configReader.configModel.downloadChunkSize = newValue.intValue());

        item7 = new BooleanItem("", this.width / 4 * 3 - 10);
        item7.cont.setSelected(Launcher.configReader.configModel.enable_blur);
        item7.cont.selectedProperty().addListener((observable, oldValue, newValue) -> {
            Launcher.configReader.configModel.enable_blur = newValue;
            Launcher.clearBgBuffer();
        });

//        FXUtils.ControlSize.setHeight(item, 30);
//        FXUtils.ControlSize.setHeight(item2, 30);
//        FXUtils.ControlSize.setHeight(item2, 30);
//        FXUtils.ControlSize.setHeight(item3, 30);
//        FXUtils.ControlSize.setHeight(item4, 30);
//        FXUtils.ControlSize.setHeight(item5, 30);
//        FXUtils.ControlSize.setHeight(item6, 30);
//        FXUtils.ControlSize.setHeight(item7, 30);

        p = new Pane();
        bar1 = JFXProgressBar.createProgressBar();
        bar2 = JFXProgressBar.createProgressBar();
        bar1.setId("game-memory-up");
        bar2.setId("game-memory");

        ltitle = new Label();
        ltitle.setFont(Fonts.t_f);

        Label total = new Label();
        Label used = new Label();
        Label jvmmem = new Label();
        total.setFont(Fonts.t_f);
        used.setFont(Fonts.t_f);
        jvmmem.setFont(Fonts.t_f);

        VBox vo = new VBox(ltitle, p, total, used, jvmmem);
        vo.setSpacing(10);

        new Thread(() -> {
            while (true){
                double targetMCMem = item2.cont.getValue();

                double targetMCMemPercent = Timer.division(MemoryReader.getUsedMemory() + Launcher.configReader.configModel.max_memory * 1024 * 1024L, MemoryReader.getTotalMemory());
                double sysMemPercent = Timer.division(MemoryReader.getUsedMemory(), MemoryReader.getTotalMemory());

                String totalS = Launcher.languageManager.get("ui.configpage.mem.bar.total.name", MemoryReader.convertMemToString(MemoryReader.getTotalMemory()));
                String usedS = Launcher.languageManager.get("ui.configpage.mem.bar.used.name", MemoryReader.convertMemToString(MemoryReader.getUsedMemory()));

                String targetMcMemS;
                if (MemoryReader.getUsedMemory() + targetMCMem * 1024 * 1024L < MemoryReader.getTotalMemory()){
                    targetMcMemS = Launcher.languageManager.get("ui.configpage.mem.bar.jvmmem.name", MemoryReader.convertMemToString((long) (targetMCMem * 1024 * 1024L)));
                }
                else {
                    targetMcMemS = Launcher.languageManager.get("ui.configpage.mem.bar.jvmmem.out.name", MemoryReader.convertMemToString((long) (targetMCMem * 1024 * 1024L)), MemoryReader.convertMemToString(MemoryReader.getFreeMemory()));
                }
                Platform.runLater(() -> bar2.setProgress(sysMemPercent));
                Platform.runLater(() -> bar1.setProgress(targetMCMemPercent));

                Platform.runLater(() -> total.setText(totalS));
                Platform.runLater(() -> used.setText(usedS));
                Platform.runLater(() -> jvmmem.setText(targetMcMemS));
                Sleeper.sleep(10);
            }
        }).start();

        p.getChildren().addAll(bar1, bar2);

        FXUtils.ControlSize.setAll((double) width / 5 * 3, 3, p, bar1, bar2);

        configs_box = new VBox(item, item2, item4, item5, item6, vo);
        configs_box.setSpacing(10);

        looklike_config_box = new VBox(item3, item7);
        looklike_config_box.setSpacing(10);

        java_get.setButtonType(JFXButton.ButtonType.RAISED);
        java_add.setButtonType(JFXButton.ButtonType.RAISED);

        mainBox = new VBox();

        p1 = new AdvancedScrollPane(this.width / 4 * 3, this.height - t_size, configs_box, false);
        p2 = new AdvancedScrollPane(this.width / 4 * 3, this.height - t_size, looklike_config_box, false);

        setting = new JFXButton();
        setting.setFont(Fonts.s_f);
        setting.setOnAction(event -> super.setP1(0));
        FXUtils.ControlSize.setWidth(setting, this.width / 4);

        looklike_setting = new JFXButton();
        looklike_setting.setFont(Fonts.s_f);
        looklike_setting.setOnAction(event -> super.setP1(1));
        FXUtils.ControlSize.setWidth(looklike_setting, this.width / 4);

        super.addNewPair(new ImmutablePair<>(setting, p1));
        super.addNewPair(new ImmutablePair<>(looklike_setting, p2));
        super.setP1(0);
        super.setButtonType(JFXButton.ButtonType.RAISED);
        nodes.add(null);
        bindedPageproperty().get().addAll(J8Utils.createList(
                ADDMODSPAGE,
                CONFIGPAGE,
                DOWNLOADADDONSELECTPAGE,
                DOWNLOADMCPAGE,
                MODDOWNLOADPAGE,
                USERSELECTPAGE,
                VERSIONINFOPAGE,
                VERSIONSELECTPAGE
        ));
    }

    public void load_java_list(){
        item4.cont.getItems().clear();
        for (String s : Launcher.configReader.configModel.selected_java) {
            Label l = new Label(s);
            l.setFont(Fonts.t_f);
            item4.cont.getItems().add(l);
        }
        if (Launcher.configReader.configModel.selected_java.contains(Launcher.configReader.configModel.selected_java_index)){
            item4.cont.getSelectionModel().select(Launcher.configReader.configModel.selected_java.indexOf(Launcher.configReader.configModel.selected_java_index));
        }
        else {
            item4.cont.getSelectionModel().selectFirst();
        }
    }
    public void refresh(){
        p1.set(this.opacityProperty());
        setType(setted);
    }
    public void refreshLanguage(){
        name = Launcher.languageManager.get("ui.configpage.name");
        title.setText(Launcher.languageManager.get("ui.configpage.title.name"));

        item.name.setText(Launcher.languageManager.get("ui.configpage.change_label.name"));
        item2.name.setText(Launcher.languageManager.get("ui.configpage.mem_label.name"));
        item3.name.setText(Launcher.languageManager.get("ui.configpage.lang_label.name"));
        item4.name.setText(Launcher.languageManager.get("ui.configpage.java_label.name"));
        item5.name.setText(Launcher.languageManager.get("ui.configpage.item5.name"));
        item6.name.setText(Launcher.languageManager.get("ui.configpage.item6.name"));
        item7.name.setText(Launcher.languageManager.get("ui.configpage.item7.name"));

        setting.setText(Launcher.languageManager.get("ui.configpage.menu._01"));
        looklike_setting.setText(Launcher.languageManager.get("ui.configpage.menu._02"));
        ltitle.setText(Launcher.languageManager.get("ui.configpage.mem.bar.title"));
    }

    public void refreshType(){
        java_add.setGraphic(Launcher.getSVGManager().plus(ThemeManager.createPaintBinding(), 15, 15));
        java_get.setGraphic(Launcher.getSVGManager().dotsHorizontal(ThemeManager.createPaintBinding(), 15, 15));
        java_find.setGraphic(Launcher.getSVGManager().back(ThemeManager.createPaintBinding(), 15, 15));
    }

    public void onExitPage() {

    }

    private int getKey(String value){
        for (Label l : item3.cont.getItems()){
            String key = "";
            for (Map.Entry<String, String> entry : langs.entrySet()){
                if (Objects.equals(entry.getValue(), value)){
                    key = entry.getKey();
                }
            }
            if (Objects.equals(l.getText(), key)){
                return item3.cont.getItems().indexOf(l);
            }
        }
        return -1;
    }
    private int getKey2(String value){
        for (Label l : item5.cont.getItems()){
            String key = "";
            for (Map.Entry<String, String> entry : servers.entrySet()){
                if (Objects.equals(entry.getValue(), value)){
                    key = entry.getKey();
                }
            }
            if (Objects.equals(l.getText(), key)){
                return item5.cont.getItems().indexOf(l);
            }
        }
        return -1;
    }
}
