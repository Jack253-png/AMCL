package com.mcreater.amcl.controls;

import com.mcreater.amcl.api.auth.MSAuth;
import javafx.scene.control.Label;

public class CapeSelectionLabel extends Label {
    private final MSAuth.McProfileModel.McCapeModel model;
    public CapeSelectionLabel(MSAuth.McProfileModel.McCapeModel model) {
        super(model.alias);
        this.model = model;
    }
    public MSAuth.McProfileModel.McCapeModel getModel() {
        return model;
    }
}
