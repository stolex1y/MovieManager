package ru.stolexiy.client.ui.view.controls;

import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import ru.stolexiy.client.ui.view.ResourceLoader;

public class Alert extends javafx.scene.control.Alert {
    public Alert(AlertType type) {
        super(type, "", ButtonType.OK);
        setHeaderText("");
        setGraphic(null);
        getDialogPane().getStylesheets().add(ResourceLoader.loadStylesheet("alert"));
        getDialogPane().getStyleClass().add("alert");
        ButtonBar buttonBar = (ButtonBar) getDialogPane().lookup(".alert > .button-bar");
        Button button = (Button) buttonBar.getButtons().get(0);
        button.getStyleClass().add("alert-button");
        button.setFont(ResourceLoader.loadFont("NunitoSans-Bold", 20));
        button.setText("ok");

        Label label = (Label) getDialogPane().lookup(".alert .label.content");
        label.setFont(ResourceLoader.loadFont("NunitoSans-Regular", 18));
        label.getStyleClass().add("alert-content");


    }
}
