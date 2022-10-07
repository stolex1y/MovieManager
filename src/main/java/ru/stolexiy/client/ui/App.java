package ru.stolexiy.client.ui;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import ru.stolexiy.client.ui.view.SignupController;
import ru.stolexiy.client.ui.view.ResourceLoader;
import ru.stolexiy.client.ui.viewmodel.ViewModel;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class App extends Application {
    public static App mainApp;
    private Pane root;
    private String fxml;
    private Stage stage;
    private ResourceBundle bundle;
    private ExecutorService executorService = Executors.newFixedThreadPool(4);

    private final Map<String, Object> args = new HashMap<>();

    private final Map<Class<? extends ViewModel>, ViewModel> saveArgs = new HashMap<>();

    @Override
    public void start(Stage primaryStage) {
        App.mainApp = this;
        this.stage = primaryStage;
        this.stage.setOnCloseRequest(event -> appClose());
        loadScene(SignupController.layout, SignupController.fullSize, null);
    }

    public void appClose() {
        executorService.shutdownNow();
        Platform.exit();
        System.exit(0);
    }

    public void updateBundleByDefaultLocale() {
        if (bundle.getLocale() != Locale.getDefault()) {
            executorService.shutdownNow();
            executorService = Executors.newFixedThreadPool(4);
            loadScene(fxml, stage.isMaximized(), args);
        }
    }

    public ResourceBundle getBundle() {
        return bundle;
    }

    public void loadScene(String fxml, boolean fullScreen, Map<String, Object> args) {
        if (args != this.args && args != null && !args.isEmpty()) {
            this.args.clear();
            this.args.putAll(args);
        }
        try {
            this.bundle = ResourceLoader.loadResourceBundle();
            this.fxml = fxml;
            Pane root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource(fxml)), bundle);
            setRoot(root, bundle, fullScreen);
        } catch (IOException e) {
            errorAlert(getBundle().getString("error.client.internal"));
            e.printStackTrace(System.err);
        }
    }

    public void loadScene(String fxml, boolean fullScreen) {
        loadScene(fxml, fullScreen, null);
    }

    private void setRoot(Pane root, ResourceBundle bundle, boolean fullScreen) {
        if (this.root == null) {
            this.root = root;
            stage.setScene(new Scene(root));
        } else {
            stage.close();
            stage.getScene().setRoot(root);
        }
        stage.centerOnScreen();
        stage.setMaximized(fullScreen);
        stage.setTitle(bundle.getString("title"));
        stage.show();
        this.bundle = bundle;
    }

    public void errorAlert(String msg) {
        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.ERROR, msg, ButtonType.OK);
            alert.setOnCloseRequest((event) -> stage.close());
            alert.setTitle(bundle.getString("error"));
            alert.setHeaderText("");
            alert.showAndWait();
            appClose();
        });
    }

    public void submitTask(Runnable runnable) {
        executorService.execute(runnable);
    }

    public <T> T getArg(String key) {
        return (T) args.getOrDefault(key, null);
    }

    public void putSaveArg(Class<? extends ViewModel> key, ViewModel value) {
        saveArgs.put(key, value);
    }

    public <T extends ViewModel> T getSaveArg(Class<? extends ViewModel> key) {
        return (T) saveArgs.getOrDefault(key, null);
    }
}
