package ru.stolexiy.client.ui.view.controls;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.AccessibleRole;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;
import ru.stolexiy.client.ui.view.LocalFormatter;
import ru.stolexiy.client.ui.view.ResourceLoader;

import java.util.Locale;

public class InputField extends StackPane {
    private TextField input;
    private final Label prompt;
    private final BooleanProperty password = new SimpleBooleanProperty(false);
    private final StringProperty promptText = new SimpleStringProperty("Prompt");

    public InputField() {
        prompt = new Label();
        prompt.textProperty().bind(promptTextProperty());
        prompt.getStyleClass().add("login-input-prompt, clickable");
        prompt.setVisible(false);
        prompt.setFont(ResourceLoader.loadFont("NunitoSans-Light", 12));
        prompt.setStyle("-fx-font-weight: 300;");
        prompt.setPadding(new Insets(0, 0, 0, 20));

        setTextField();
        passwordProperty().addListener(it -> {
            this.getChildren().clear();
            setTextField();
            this.getChildren().addAll(input, prompt);
        });

        this.getStylesheets().add(ResourceLoader.loadStylesheet("auth"));
        this.getStyleClass().clear();
        this.setAccessibleRole(AccessibleRole.TEXT_FIELD);
        StackPane.setAlignment(prompt, Pos.TOP_LEFT);
        this.getChildren().addAll(input, prompt);
        this.setMaxHeight(46);
        this.setMaxWidth(400);
    }

    public final boolean isPassword() {
        return password.get();
    }

    public final String getPromptText() {
        return promptText.get();
    }

    public final BooleanProperty passwordProperty() {
        return password;
    }

    public final StringProperty promptTextProperty() {
        return promptText;
    }

    public final void setPassword(boolean password) {
        passwordProperty().setValue(password);
    }

    public final void setPromptText(String promptText) {
        promptTextProperty().setValue(promptText);
    }

    private void setTextField() {
        if (!isPassword())
            input = new TextField();
        else
            input = new PasswordField();

        input.getStyleClass().clear();
        input.getStyleClass().add("login-input");
        input.promptTextProperty().bind(promptTextProperty());
        input.setFont(ResourceLoader.loadFont("Nunitosans-Regular", 18));
        input.textProperty().addListener(listener -> {
            String newValue = ((StringProperty) listener).get();
            if (isPassword()) {
                if (newValue.equals("")) {
                    input.setFont(ResourceLoader.loadFont("Nunitosans-Regular", 18));
                } else {
                    input.setFont(null);
                }
            }
            /*FadeTransition fadeOutTransition = new FadeTransition(Duration.millis(500), prompt);
            if (input.getText().equals("") && prompt.isVisible()) {
                fadeOutTransition.setFromValue(1.0);
                fadeOutTransition.setToValue(0.0);
                fadeOutTransition.setOnFinished(event -> {
                    prompt.setVisible(false);
                });
            } else if (!input.getText().equals("") && !prompt.isVisible()) {
                fadeOutTransition.setFromValue(0.0);
                fadeOutTransition.setToValue(1.0);
                fadeOutTransition.setOnFinished(event -> {
                    prompt.setVisible(true);
                });

            }
            fadeOutTransition.play();*/
            prompt.setVisible(!newValue.equals(""));
        });
        input.setPadding(new Insets(15, 0, 5, 20));
    }

    public StringProperty textProperty() {
        return input.textProperty();
    }

    public TextField getTextField() {
        return input;
    }

    public void setText(String text) {
        input.setText(text);
    }

    public void setText(double text) {
        input.setText(LocalFormatter.formatNumber(Locale.getDefault(), text, 2));
    }

    public void setText(long text) {
        input.setText(String.valueOf(text));
    }

    public void onClicked(EventHandler<? super MouseEvent> value) {
        this.setOnMouseClicked(value);
        this.input.setOnMouseClicked(value);
        this.prompt.setOnMouseClicked(value);
    }
}
