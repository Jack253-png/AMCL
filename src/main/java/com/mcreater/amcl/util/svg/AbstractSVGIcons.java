package com.mcreater.amcl.util.svg;

import javafx.beans.binding.ObjectBinding;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Paint;
import javafx.scene.shape.SVGPath;

public interface AbstractSVGIcons {
    default Node createSVGPath(String d, ObjectBinding<? extends Paint> fill, double width, double height){
        SVGPath path = new SVGPath();
        path.getStyleClass().add("svg");
        path.setContent(d);
        if (fill != null) {
            path.fillProperty().bind(fill);
        }

        if (!(width < 0.0D) && !(height < 0.0D)) {
            Group svg = new Group(path);
            double scale = Math.min(width / 24.0D, height / 24.0D);
            svg.setScaleX(scale);
            svg.setScaleY(scale);
            return svg;
        } else {
            StackPane pane = new StackPane(path);
            pane.setAlignment(Pos.CENTER);
            return pane;
        }
    }
    Node close(ObjectBinding<? extends Paint> fill, double width, double height);
    Node back(ObjectBinding<? extends Paint> fill, double width, double height);
    Node gear(ObjectBinding<? extends Paint> fill, double width, double height);
    Node plus(ObjectBinding<? extends Paint> fill, double width, double height);
    Node refresh(ObjectBinding<? extends Paint> fill, double width, double height);
    Node wrench(ObjectBinding<? extends Paint> fill, double width, double height);
    Node downloadOutline(ObjectBinding<? extends Paint> fill, double width, double height);
    Node delete(ObjectBinding<? extends Paint> fill, double width, double height);
    Node gearOutline(ObjectBinding<? extends Paint> fill, double width, double height);
    Node cancel(ObjectBinding<? extends Paint> fill, double width, double height);
    Node copy(ObjectBinding<? extends Paint> fill, double width, double height);
    Node dotsVertical(ObjectBinding<? extends Paint> fill, double width, double height);
    Node dotsHorizontal(ObjectBinding<? extends Paint> fill, double width, double height);
    Node deleteOutline(ObjectBinding<? extends Paint> fill, double width, double height);
    Node accountEdit(ObjectBinding<? extends Paint> fill, double width, double height);
    Node expand(ObjectBinding<? extends Paint> fill, double width, double height);
    Node collapse(ObjectBinding<? extends Paint> fill, double width, double height);
    Node navigate(ObjectBinding<? extends Paint> fill, double width, double height);
    Node rocketLaunchOutline(ObjectBinding<? extends Paint> fill, double width, double height);
    Node launchOutline(ObjectBinding<? extends Paint> fill, double width, double height);
    Node script(ObjectBinding<? extends Paint> fill, double width, double height);
    Node folderOpen(ObjectBinding<? extends Paint> fill, double width, double height);
    Node folderOutline(ObjectBinding<? extends Paint> fill, double width, double height);
    Node update(ObjectBinding<? extends Paint> fill, double width, double height);
    Node closeCircle(ObjectBinding<? extends Paint> fill, double width, double height);
    Node checkCircle(ObjectBinding<? extends Paint> fill, double width, double height);
    Node infoCircle(ObjectBinding<? extends Paint> fill, double width, double height);
    Node helpCircle(ObjectBinding<? extends Paint> fill, double width, double height);
    Node helpCircleOutline(ObjectBinding<? extends Paint> fill, double width, double height);
    Node alert(ObjectBinding<? extends Paint> fill, double width, double height);
    Node alertOutline(ObjectBinding<? extends Paint> fill, double width, double height);
    Node plusCircleOutline(ObjectBinding<? extends Paint> fill, double width, double height);
    Node importIcon(ObjectBinding<? extends Paint> fill, double width, double height);
    Node export(ObjectBinding<? extends Paint> fill, double width, double height);
    Node openInNew(ObjectBinding<? extends Paint> fill, double width, double height);
    Node triangle(ObjectBinding<? extends Paint> fill, double width, double height);
    Node home(ObjectBinding<? extends Paint> fill, double width, double height);
    Node viewList(ObjectBinding<? extends Paint> fill, double width, double height);
    Node check(ObjectBinding<? extends Paint> fill, double width, double height);
    Node arrowRight(ObjectBinding<? extends Paint> fill, double width, double height);
    Node wrenchOutline(ObjectBinding<? extends Paint> fill, double width, double height);
    Node upload(ObjectBinding<? extends Paint> fill, double width, double height);
    Node hanger(ObjectBinding<? extends Paint> fill, double width, double height);
    Node puzzle(ObjectBinding<? extends Paint> fill, double width, double height);
    Node cube(ObjectBinding<? extends Paint> fill, double width, double height);
    Node pack(ObjectBinding<? extends Paint> fill, double width, double height);
    Node textureBox(ObjectBinding<? extends Paint> fill, double width, double height);
    Node gamepad(ObjectBinding<? extends Paint> fill, double width, double height);
    Node fire(ObjectBinding<? extends Paint> fill, double width, double height);
    Node monitorScreenshot(ObjectBinding<? extends Paint> fill, double width, double height);
    Node texture(ObjectBinding<? extends Paint> fill, double width, double height);
    Node alphaCircleOutline(ObjectBinding<? extends Paint> fill, double width, double height);
    Node betaCircleOutline(ObjectBinding<? extends Paint> fill, double width, double height);
    Node releaseCircleOutline(ObjectBinding<? extends Paint> fill, double width, double height);
    Node informationOutline(ObjectBinding<? extends Paint> fill, double width, double height);
    Node handHearOutline(ObjectBinding<? extends Paint> fill, double width, double height);
    Node mojang(ObjectBinding<? extends Paint> fill, double width, double height);
    Node microsoft(ObjectBinding<? extends Paint> fill, double width, double height);
    Node accountOutline(ObjectBinding<? extends Paint> fill, double width, double height);
    Node accountGroupOutline(ObjectBinding<? extends Paint> fill, double width, double height);
    Node accountArrowRightOutline(ObjectBinding<? extends Paint> fill, double width, double height);
    Node styleOutline(ObjectBinding<? extends Paint> fill, double width, double height);
    Node applicationOutline(ObjectBinding<? extends Paint> fill, double width, double height);
    Node earth(ObjectBinding<? extends Paint> fill, double width, double height);
    Node bell(ObjectBinding<? extends Paint> fill, double width, double height);
    Node contentSaveMoveOutline(ObjectBinding<? extends Paint> fill, double width, double height);
    Node account(ObjectBinding<? extends Paint> fill, double width, double height);
    Node messageAlertOutline(ObjectBinding<? extends Paint> fill, double width, double height);
    Node checkCircleOutline(ObjectBinding<? extends Paint> fill, double width, double height);
    Node closeCircleOutline(ObjectBinding<? extends Paint> fill, double width, double height);
    Node clockOutline(ObjectBinding<? extends Paint> fill, double width, double height);
    Node magnify(ObjectBinding<? extends Paint> fill, double width, double height);
    Node menuUp(ObjectBinding<? extends Paint> fill, double width, double height);
    Node menuDown(ObjectBinding<? extends Paint> fill, double width, double height);
    Node restore(ObjectBinding<? extends Paint> fill, double width, double height);
    Node bug(ObjectBinding<? extends Paint> fill, double width, double height);
    Node discord(ObjectBinding<? extends Paint> fill, double width, double height);
    Node lan(ObjectBinding<? extends Paint> fill, double width, double height);
    Node thumbUpOutline(ObjectBinding<? extends Paint> fill, double width, double height);
    Node thumbDownOutline(ObjectBinding<? extends Paint> fill, double width, double height);
    Node selectAll(ObjectBinding<? extends Paint> fill, double width, double height);
    Node pencil(ObjectBinding<? extends Paint> fill, double width, double height);
    Node pencilOutline(ObjectBinding<? extends Paint> fill, double width, double height);
}
