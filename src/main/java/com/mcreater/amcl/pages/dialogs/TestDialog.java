package com.mcreater.amcl.pages.dialogs;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXDialogLayout;
import com.mcreater.amcl.util.FXUtils;

import static com.mcreater.amcl.Launcher.stage;

public class TestDialog extends AbstractDialog {
    public TestDialog(double width, double height) {
        super(stage);
        JFXDialogLayout layout = new JFXDialogLayout();
        JFXButton b = new JFXButton("test");
        b.setOnAction(event -> new TestDialog(width - 50, height - 50).Create());
        layout.setActions(b);
        FXUtils.ControlSize.set(layout, width, height);
        setContent(layout);
    }
}
