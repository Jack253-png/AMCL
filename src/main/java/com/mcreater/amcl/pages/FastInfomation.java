package com.mcreater.amcl.pages;

import javafx.scene.control.Alert;

public class FastInfomation {
    public static void create(String Title,String HeaderText,String ContentText){
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(Title);
        alert.setHeaderText(HeaderText);
        alert.setContentText(ContentText);

        alert.showAndWait();
    }
}
