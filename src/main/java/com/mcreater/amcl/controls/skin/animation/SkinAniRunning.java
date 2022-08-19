package com.mcreater.amcl.controls.skin.animation;

import com.mcreater.amcl.controls.skin.FunctionHelper;
import com.mcreater.amcl.controls.skin.SkinAnimation;
import com.mcreater.amcl.controls.skin.SkinTransition;
import com.mcreater.amcl.controls.skin.SkinView;
import javafx.util.Duration;

public final class SkinAniRunning extends SkinAnimation {

    private SkinTransition larmTransition, rarmTransition;

    public SkinAniRunning(int weight, int time, double angle, SkinView canvas) {
        larmTransition = new SkinTransition(Duration.millis(time),
                v -> v * (larmTransition.getCount() % 4 < 2 ? 1 : -1) * angle,
                canvas.larm.getXRotate().angleProperty(), canvas.rleg.getXRotate().angleProperty());

        rarmTransition = new SkinTransition(Duration.millis(time),
                v -> v * (rarmTransition.getCount() % 4 < 2 ? 1 : -1) * -angle,
                canvas.rarm.getXRotate().angleProperty(), canvas.lleg.getXRotate().angleProperty());

        FunctionHelper.alwaysB(SkinTransition::setAutoReverse, true, larmTransition, rarmTransition);
        FunctionHelper.alwaysB(SkinTransition::setCycleCount, 16, larmTransition, rarmTransition);
        FunctionHelper.always(transitions::add, larmTransition, rarmTransition);
        this.weight = weight;
        init();
    }

}
