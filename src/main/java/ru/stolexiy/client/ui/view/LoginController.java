package ru.stolexiy.client.ui.view;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import ru.stolexiy.client.ui.App;
import ru.stolexiy.client.ui.view.controls.ChoiceBox;
import ru.stolexiy.client.ui.view.controls.InputField;
import ru.stolexiy.client.ui.viewmodel.AuthViewModel;

import java.net.URL;
import java.util.*;

public class LoginController implements Initializable {

    public static final String layout = "/layouts/login.fxml";
    public static final boolean fullSize = false;
    @FXML
    private ChoiceBox choiceLang;
    @FXML
    private Label labelError;
    @FXML
    private Label labelTitle;
    @FXML
    private InputField inputLogin;
    @FXML
    private InputField inputPass;
    @FXML
    private Button buttonLogin;
    @FXML
    private Hyperlink linkSignin;

    private AuthViewModel viewModel;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        viewModel = new AuthViewModel(resources);

        labelTitle.setFont(ResourceLoader.loadFont("RubikMonoOne-Regular", 20));

        linkSignin.setFont(ResourceLoader.loadFont("NunitoSans-Regular", 16));
        setOnAction(linkSignin, event -> App.mainApp.loadScene(SignupController.layout, SignupController.fullSize));

        buttonLogin.setFont(ResourceLoader.loadFont("NunitoSans-Bold", 20));
        setOnAction(buttonLogin, event -> viewModel.signIn());

        labelError.setFont(ResourceLoader.loadFont("NunitoSans-Light", 16));
        labelError.textProperty().bind(viewModel.errorTextProperty());

        inputLogin.textProperty().bindBidirectional(viewModel.loginProperty());
        inputPass.textProperty().bindBidirectional(viewModel.passProperty());
    }

    private void setOnAction(ButtonBase node, EventHandler<ActionEvent> handler) {
        node.setOnAction(handler);
        node.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                node.getOnAction().handle(null);
            }
        });
    }
}
