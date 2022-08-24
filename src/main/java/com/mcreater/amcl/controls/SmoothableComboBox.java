package com.mcreater.amcl.controls;

import com.mcreater.amcl.pages.interfaces.Fonts;
import com.mcreater.amcl.theme.ThemeManager;
import com.mcreater.amcl.util.FXUtils;
import javafx.scene.control.Labeled;
import javafx.scene.control.TitledPane;
import javafx.scene.layout.BorderStrokeStyle;
import javafx.scene.layout.Region;
import javafx.scene.paint.Color;

public class SmoothableComboBox<T extends Region> extends SmoothableListView<T> {
    public TitledPane pane;
    public void select(int index){
        super.select(index);
        try {
            pane.setText(((Labeled) vecs.get(index)).getText());
        }
        catch (Exception ignored){}
    }
    public void clear(){
        super.clear();
        pane.setText("");
    }
    public SmoothableComboBox(double width, double height) {
        super(width, height);
        pane = new TitledPane();
        pane.setContent(page);
        pane.setExpanded(false);
        pane.setFont(Fonts.t_f);
        pane.getStylesheets().add(String.format(ThemeManager.getPath(), "TitledPane"));
        pane.setBorder(FXUtils.generateBorder(Color.BLACK, BorderStrokeStyle.SOLID, false, false, true, false, 1));
        setOnAction(() -> {
            try {
                pane.setText(((Labeled) selectedItem).getText());
            }
            catch (Exception ignored){}
        });
    }
}
