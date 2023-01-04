package com.mcreater.amcl.pages.interfaces;

import com.mcreater.amcl.Launcher;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.GridPane;

import java.util.List;
import java.util.Vector;

public abstract class AbstractAnimationPage extends GridPane implements AnimationPage {
    public String name;
    public AbstractAnimationPage l;
    public double width;
    public double height;
    public final List<NodeInfo> nodes = new Vector<>();

    final SimpleObjectProperty<WritableImage> bufferedBackgroundproperty = new SimpleObjectProperty<>(null);
    public final void setBufferedBackground(WritableImage image) {
        bufferedBackgroundproperty.set(image);
    }
    public final WritableImage getBufferedBackground() {
        return bufferedBackgroundproperty.get();
    }
    public final SimpleObjectProperty<WritableImage> bufferedBackgroundproperty(){
        return bufferedBackgroundproperty;
    }

    final SimpleObjectProperty<List<AbstractAnimationPage>> bindedPageproperty = new SimpleObjectProperty<>(new Vector<>());
    public final SimpleObjectProperty<List<AbstractAnimationPage>> bindedPageproperty(){
        return bindedPageproperty;
    }

    public AbstractAnimationPage(double width, double height){
        this.setMinSize(width, height);
        this.setMaxSize(width, height);
        this.width = width;
        this.height = height;
        set(Launcher.stage.opacityProperty());
    }
    public boolean getCanMovePage() {
        return Launcher.stage.opacityProperty().get() == 1;
    }
    public abstract void refresh();
    public abstract void refreshLanguage();
    public abstract void refreshType();
    public abstract void onExitPage();
    public void clearNodes() {}
}
