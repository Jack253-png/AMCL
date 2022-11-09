package com.mcreater.amcl.pages;

import com.mcreater.amcl.Launcher;
import com.mcreater.amcl.pages.interfaces.AbstractAnimationPage;
import com.mcreater.amcl.util.FXUtils;
import com.mcreater.amcl.util.J8Utils;
import javafx.scene.layout.VBox;

import static com.mcreater.amcl.Launcher.ADDMODSPAGE;
import static com.mcreater.amcl.Launcher.CONFIGPAGE;
import static com.mcreater.amcl.Launcher.DOWNLOADADDONSELECTPAGE;
import static com.mcreater.amcl.Launcher.DOWNLOADMCPAGE;
import static com.mcreater.amcl.Launcher.MODDOWNLOADPAGE;
import static com.mcreater.amcl.Launcher.USERSELECTPAGE;
import static com.mcreater.amcl.Launcher.VERSIONINFOPAGE;
import static com.mcreater.amcl.Launcher.VERSIONSELECTPAGE;

public class UserSelectPage extends AbstractAnimationPage {
    VBox sideBar = new VBox();
    public UserSelectPage(double width, double height) {
        super(width, height);
        l = Launcher.MAINPAGE;

        sideBar = new VBox();
        sideBar.setId("config-menu");

        FXUtils.ControlSize.set(sideBar, width / 4, height);

        nodes.add(null);

        BindedPageproperty().get().addAll(J8Utils.createList(
                ADDMODSPAGE,
                CONFIGPAGE,
                DOWNLOADADDONSELECTPAGE,
                DOWNLOADMCPAGE,
                MODDOWNLOADPAGE,
                USERSELECTPAGE,
                VERSIONINFOPAGE,
                VERSIONSELECTPAGE
        ));
        add(sideBar, 0, 0, 1, 1);
    }

    public void refresh() {

    }

    public void refreshLanguage() {

    }

    public void refreshType() {

    }

    public void onExitPage() {

    }
}
