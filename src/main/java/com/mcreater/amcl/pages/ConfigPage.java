package com.mcreater.amcl.pages;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTreeView;
import com.mcreater.amcl.Launcher;
import com.mcreater.amcl.controls.items.BooleanItem;
import com.mcreater.amcl.controls.items.IntItem;
import com.mcreater.amcl.controls.items.ListItem;
import com.mcreater.amcl.controls.items.MuiltButtonListItem;
import com.mcreater.amcl.lang.LanguageManager;
import com.mcreater.amcl.pages.dialogs.FastInfomation;
import com.mcreater.amcl.pages.interfaces.AbstractMenuBarPage;
import com.mcreater.amcl.pages.interfaces.Fonts;
import com.mcreater.amcl.pages.interfaces.SettingPage;
import com.mcreater.amcl.util.java.JavaInfoGetter;
import com.mcreater.amcl.util.FXUtils;
import com.mcreater.amcl.util.concurrent.Sleeper;
import com.mcreater.amcl.util.system.CpuReader;
import com.mcreater.amcl.util.system.JavaHeapMemoryReader;
import com.mcreater.amcl.util.system.MemoryReader;
import com.mcreater.amcl.util.system.UsbDeviceReader;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.chart.Chart;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Label;
import javafx.scene.control.TreeItem;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.util.Pair;
import oshi.hardware.UsbDevice;

import java.io.File;
import java.lang.reflect.Field;
import java.util.Map;
import java.util.Objects;
import java.util.TreeMap;
import java.util.Vector;
import java.util.concurrent.ExecutionException;

public class ConfigPage extends AbstractMenuBarPage {
    Label title;
    VBox configs_box;
    public JFXButton java_add;
    public JFXButton java_get;
    final Map<String, String> langs;
    JFXButton setting;
    SettingPage p1;
    SettingPage p2;
    public BooleanItem item;
    public IntItem item2;
    public ListItem<Label> item3;
    public MuiltButtonListItem<Label> item4;
    public BooleanItem item5;
    public IntItem item6;
    JFXButton system;
    JFXTreeView<Label> view;
    XYChart.Series<Number, Number> usedMemory;
    XYChart.Series<Number, Number> totalMemory;
    XYChart.Series<Number, Number> freeMemory;
    int current = -5;
    XYChart.Series<Number, Number> cpuUsed;
    XYChart.Series<Number, Number> heapUsed;
    XYChart.Series<Number, Number> heapMax;
    IntItem item7;
    JFXButton startListen;
    Thread listenThread;
    EventHandler<ActionEvent> start;
    EventHandler<ActionEvent> end;
    LineChart<Number, Number> memory;
    LineChart<Number, Number> cpu;
    LineChart<Number, Number> jvm;
    public ConfigPage(int width, int height) throws NoSuchFieldException, IllegalAccessException {
        super(width, height);
        l = Launcher.MAINPAGE;
        this.setAlignment(Pos.TOP_CENTER);

        double t_size = Launcher.barSize;

        langs = new TreeMap<>();
        langs.put("简体中文", "CHINESE");
        langs.put("English(US)", "ENGLISH");

        title = new Label();
        title.setFont(Fonts.b_f);

        item = new BooleanItem("", this.width / 4 * 3);
        item.cont.setSelected(Launcher.configReader.configModel.change_game_dir);
        item.cont.selectedProperty().addListener((observable, oldValue, newValue) -> Launcher.configReader.configModel.change_game_dir = newValue);

        item2 = new IntItem("", this.width / 4 * 3);
        item2.cont.setMax(4096);
        item2.cont.setMin(256);
        item2.cont.setValue(Launcher.configReader.configModel.max_memory);
        item2.cont.setOrientation(Orientation.HORIZONTAL);
        item2.cont.valueProperty().addListener((observable, oldValue, newValue) -> Launcher.configReader.configModel.max_memory = newValue.intValue());

        java_add = new JFXButton();
        java_add.setDefaultButton(true);
        java_add.setFont(Fonts.t_f);
        java_add.setOnAction(event -> {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle(Launcher.languageManager.get("ui.configpage.java_choose.title"));
            fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter(Launcher.languageManager.get("ui.configpage.java_choose.filename.description"), "java.exe"));
            File choosed_path = fileChooser.showOpenDialog(Launcher.stage);
            if (choosed_path != null) {
                if (!Launcher.configReader.configModel.selected_java.contains(choosed_path.getPath())) {
                    Launcher.configReader.configModel.selected_java.add(choosed_path.getPath());
                    load_java_list();
                }
            }
        });

        java_get = new JFXButton();
        java_get.setDefaultButton(true);
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
                Platform.runLater(() -> FastInfomation.create(Launcher.languageManager.get("ui.configpage.java_info.title"), String.format(Launcher.languageManager.get("ui.configpage.java_info.Headercontent"), v.get(0), v.get(1), v.get(2), v.get(3)), ""));
            }
            else{
                Platform.runLater(() -> FastInfomation.create(Launcher.languageManager.get("ui.configpage.select_java.title"), Launcher.languageManager.get("ui.configpage.select_java.Headercontent"), ""));
            }
            java_get.setDisable(false);
        }).start());

        item4 = new MuiltButtonListItem<>("", this.width / 4 * 3);
        item4.addButtons(java_get, java_add);
        item4.cont.setOnAction(event -> {
            if (item4.cont.getValue() == null && item4.cont.getItems().size() > 0) {
                item4.cont.getSelectionModel().select(0);
            }
            Launcher.configReader.configModel.selected_java_index = item4.cont.getValue().getText();
            Launcher.configReader.write();
        });

        load_java_list();

        item3 = new ListItem<>("", this.width / 4 * 3);
        for (Map.Entry<String, String> entry : langs.entrySet()) {
            Label l = new Label(entry.getKey());
            l.setFont(Fonts.t_f);
            item3.cont.getItems().add(l);
        }
        item3.cont.getSelectionModel().select(getKey(Launcher.configReader.configModel.language));
        item3.cont.setOnAction(event -> {
            Launcher.configReader.configModel.language = langs.get(item3.cont.getValue().getText());
            Launcher.languageManager.setLanguage(LanguageManager.valueOf(Launcher.configReader.configModel.language));
            Launcher.setTitle();
        });

        item5 = new BooleanItem("", this.width / 4 * 3);
        item5.cont.selectedProperty().set(Launcher.configReader.configModel.fastDownload);
        item5.cont.setOnAction(event -> Launcher.configReader.configModel.fastDownload = item5.cont.isSelected());

        item6 = new IntItem("", this.width / 4 * 3);
        item6.cont.setMax(8192);
        item6.cont.setMin(512);
        item6.cont.setValue(Launcher.configReader.configModel.downloadChunkSize);
        item6.cont.setOrientation(Orientation.HORIZONTAL);
        item6.cont.valueProperty().addListener((observable, oldValue, newValue) -> Launcher.configReader.configModel.downloadChunkSize = newValue.intValue());

        item7 = new IntItem("", this.width / 4 * 3);
        item7.cont.setMax(1000);
        item7.cont.setMin(500);
        item7.cont.setValue(Launcher.configReader.configModel.showingUpdateSpped);
        item7.cont.setOrientation(Orientation.HORIZONTAL);
        item7.cont.valueProperty().addListener((observable, oldValue, newValue) -> Launcher.configReader.configModel.showingUpdateSpped = newValue.intValue());

        FXUtils.ControlSize.setHeight(item, 30);
        FXUtils.ControlSize.setHeight(item2, 30);
        FXUtils.ControlSize.setHeight(item2, 30);
        FXUtils.ControlSize.setHeight(item3, 30);
        FXUtils.ControlSize.setHeight(item4, 30);
        FXUtils.ControlSize.setHeight(item5, 30);
        FXUtils.ControlSize.setHeight(item6, 30);
        FXUtils.ControlSize.setHeight(item7, 30);

        configs_box = new VBox();
        configs_box.setSpacing(10);
        configs_box.getChildren().addAll(item, item2, item3, item4, item5, item6, item7);
        configs_box.setId("config-box");

        java_get.setButtonType(JFXButton.ButtonType.RAISED);
        java_add.setButtonType(JFXButton.ButtonType.RAISED);

        mainBox = new VBox();

        view = new JFXTreeView<>();
        TreeItem<Label> rootItem = new TreeItem<>(new Label("USB Devices"));
//        loadAllDevice(rootItem);
        view.setRoot(rootItem);
        view.setShowRoot(false);
        view.setStyle("-fx-vbar-policy: always");

        NumberAxis xAxis = new NumberAxis();
        NumberAxis yAxis = new NumberAxis();
        memory = new LineChart<>(xAxis, yAxis);
        changeFont(memory);
        memory.setHorizontalGridLinesVisible(false);
        memory.setVerticalGridLinesVisible(false);
        usedMemory = new XYChart.Series<>();
        totalMemory = new XYChart.Series<>();
        freeMemory = new XYChart.Series<>();
        memory.getData().addAll(usedMemory, totalMemory, freeMemory);

        NumberAxis xAxis2 = new NumberAxis();
        NumberAxis yAxis2 = new NumberAxis();
        cpu = new LineChart<>(xAxis2, yAxis2);
        changeFont(cpu);
        cpu.setHorizontalGridLinesVisible(false);
        cpu.setVerticalGridLinesVisible(false);
        cpuUsed = new XYChart.Series<>();
        cpu.getData().addAll(cpuUsed);

        NumberAxis x = new NumberAxis();
        NumberAxis y = new NumberAxis();
        jvm = new LineChart<>(x, y);
        changeFont(jvm);
        jvm.setHorizontalGridLinesVisible(false);
        jvm.setVerticalGridLinesVisible(false);
        heapUsed = new XYChart.Series<>();
        heapMax = new XYChart.Series<>();
        jvm.getData().addAll(heapUsed, heapMax);

        start = event -> {
            addMem();
            startListen.setText(Launcher.languageManager.get("ui.configpage.systemInfo.listen.stop"));
            startListen.setOnAction(end);
        };
        end = event -> {
            listenThread.stop();
            startListen.setText(Launcher.languageManager.get("ui.configpage.systemInfo.listen.start"));
            startListen.setOnAction(start);
        };

        startListen = new JFXButton();
        startListen.setFont(Fonts.t_f);
        startListen.setOnAction(start);

        FXUtils.ControlSize.setWidth(memory, this.width / 4 * 3);
        FXUtils.ControlSize.setWidth(cpu, this.width / 4 * 3);
        FXUtils.ControlSize.setWidth(jvm, this.width / 4 * 3);

        FXUtils.ControlSize.set(view, this.width / 4 * 2.95, 400);
        VBox v = new VBox(startListen, memory, cpu, jvm);
        v.setAlignment(Pos.CENTER_LEFT);
        FXUtils.ControlSize.setWidth(v, this.width / 4 * 3);

        p1 = new SettingPage(this.width / 4 * 3, this.height - t_size, configs_box);
        p2 = new SettingPage(this.width / 4 * 3, this.height - t_size, v, false);

        setting = new JFXButton();
        setting.setFont(Fonts.s_f);
        setting.setOnAction(event -> super.setP1(0));
        system = new JFXButton();
        system.setFont(Fonts.s_f);
        system.setOnAction(event -> super.setP1(1));
        FXUtils.ControlSize.setWidth(setting, this.width / 4);
        FXUtils.ControlSize.setWidth(system, this.width / 4);
        super.addNewPair(new Pair<>(setting, p1));
        super.addNewPair(new Pair<>(system, p2));
        super.setP1(0);
        super.setButtonType(JFXButton.ButtonType.RAISED);
    }
    public void changeFont(Chart c) throws NoSuchFieldException, IllegalAccessException {
        Field f = Chart.class.getDeclaredField("titleLabel");
        f.setAccessible(true);
        ((Label) f.get(c)).setFont(Fonts.s_f);
    }
    public void addMem(){
        new Thread(() -> {
            listenThread = new Thread(() -> {
                while (true){
                    current += 1;
                    if (current >= 0) {
                        Runnable r = () -> {
                            int curr = Launcher.configReader.configModel.showingUpdateSpped * current;
                            usedMemory.getData().add(new XYChart.Data<>(curr, MemoryReader.getUsedMemory()));
                            totalMemory.getData().add(new XYChart.Data<>(curr, MemoryReader.getTotalMemory()));
                            freeMemory.getData().add(new XYChart.Data<>(curr, MemoryReader.getFreeMemory()));
                            cpuUsed.getData().add(new XYChart.Data<>(curr, CpuReader.getCpuUsed() * 100));
                            heapMax.getData().add(new XYChart.Data<>(curr, JavaHeapMemoryReader.getMaxMem()));
                            heapUsed.getData().add(new XYChart.Data<>(curr, JavaHeapMemoryReader.getUsedMem()));
                            check(usedMemory);
                            check(totalMemory);
                            check(freeMemory);
                            check(cpuUsed);
                            check(heapMax);
                            check(heapUsed);
                        };
                        Platform.runLater(r);
                        Sleeper.sleep(Launcher.configReader.configModel.showingUpdateSpped);
                    }
                }
            });
            listenThread.start();
        }).start();
    }
    public void check(XYChart.Series<Number, Number> s){
        Runnable r = () -> {
            if (s.getData().size() > 100){
                s.getData().remove(0);
                if (s == usedMemory){
                    current -= 1;
                }
                for (int index = 0;index < s.getData().size();index++){
                    XYChart.Data<Number, Number> n = s.getData().get(index);
                    n.setXValue(Launcher.configReader.configModel.showingUpdateSpped * index);
                }
            }
        };
        r.run();
    }
    public static void loadAllDevice(TreeItem<Label> root){
        Vector<UsbDevice> devices = UsbDeviceReader.getDevices();
        for (UsbDevice d : devices){
            Label l = new Label(d.getName());
            l.setFont(Fonts.t_f);
            TreeItem<Label> child = new TreeItem<>(l);
            root.getChildren().add(child);
            loadNodeDevice(child, d);
        }
    }
    public static void loadNodeDevice(TreeItem<Label> root, UsbDevice device){
        if (device.getConnectedDevices().length > 0){
            for (UsbDevice d : device.getConnectedDevices()){
                Label l = new Label(d.getName());
                l.setFont(Fonts.t_f);
                TreeItem<Label> child = new TreeItem<>(l);
                root.getChildren().add(child);
                loadNodeDevice(child, d);
            }
        }
    }
    public void load_java_list(){
        item4.cont.getItems().clear();
        for (String s : Launcher.configReader.configModel.selected_java) {
            Label l = new Label(s);
            l.setFont(Fonts.t_f);
            item4.cont.getItems().add(l);
        }
        if (Launcher.configReader.configModel.selected_java.contains(Launcher.configReader.configModel.selected_java_index) || new File(Launcher.configReader.configModel.selected_java_index).exists()){
            item4.cont.getSelectionModel().select(Launcher.configReader.configModel.selected_java.indexOf(Launcher.configReader.configModel.selected_java_index));
        }
        else{
            item4.cont.getSelectionModel().clearSelection();
        }
    }
    public void refresh(){
        p1.set(this.opacityProperty());
        p2.set(this.opacityProperty());
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

        java_get.setText(Launcher.languageManager.get("ui.configpage.java_get.name"));
        java_add.setText(Launcher.languageManager.get("ui.configpage.java_add.name"));
        setting.setText(Launcher.languageManager.get("ui.configpage.menu._01"));
        system.setText(Launcher.languageManager.get("ui.configpage.menu._02"));
        startListen.setText(Launcher.languageManager.get("ui.configpage.systemInfo.listen.start"));

        memory.setTitle(Launcher.languageManager.get("ui.configpage.systemInfo.charts.1.title"));
        cpu.setTitle(Launcher.languageManager.get("ui.configpage.systemInfo.charts.2.title"));
        jvm.setTitle(Launcher.languageManager.get("ui.configpage.systemInfo.charts.3.title"));
    }

    public void refreshType(){

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
}
