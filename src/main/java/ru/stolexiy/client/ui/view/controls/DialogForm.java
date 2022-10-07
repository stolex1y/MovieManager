package ru.stolexiy.client.ui.view.controls;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.Window;
import javafx.util.Pair;
import ru.stolexiy.client.ui.App;
import ru.stolexiy.client.ui.view.ResourceLoader;
import ru.stolexiy.data.*;

import java.io.IOException;
import java.util.*;

public abstract class DialogForm<T> {

    private VBox root;

    @FXML
    protected Label labelTitle;
    @FXML
    protected Label labelError;
    @FXML
    protected Button buttonSave;
    @FXML
    protected Button buttonCancel;
    private double prefX = Double.NaN;
    private double prefY = Double.NaN;
    private Scene scene;
    protected final Stage stage = new Stage() {
        @Override
        public void centerOnScreen() {
            Window owner = getOwner();
            if (owner != null) {
                positionStage();
            } else {
                if (getWidth() > 0 && getHeight() > 0) {
                    super.centerOnScreen();
                }
            }
        }
    };

    private final Parent DUMMY_ROOT = new Region();
    protected final ResourceBundle bundle;

    protected T oldValue;
    protected Optional<T> result = Optional.empty();
    protected final List<Pair<Validator, InputField>> validators = new ArrayList<>();

    protected boolean isEditing = false;

    public DialogForm(String fxml, ResourceBundle bundle, T initial) {
        this.bundle = bundle;
        load(fxml);
        stage.setResizable(false);

        labelTitle.setFont(ResourceLoader.loadFont("RubikMonoOne-Regular", 20));
        labelError.setFont(ResourceLoader.loadFont("NunitoSans-Light", 16));

        setInitialValue(initial);
        fillValidators();
        setButtons();

        stage.setOnCloseRequest(event -> result = Optional.empty());

        buttonSave.getStyleClass().add("clickable");
        buttonCancel.getStyleClass().add("clickable");
    }

    protected abstract void setInitialValue(T initial);

    protected void setButtons() {
        buttonSave.setFont(ResourceLoader.loadFont("NunitoSans-Bold", 20));
        buttonCancel.setFont(ResourceLoader.loadFont("NunitoSans-Bold", 20));
        buttonCancel.setOnAction(event -> {
            result = Optional.empty();
            close();
        });
    }

    protected void fillValidators() {
    }

    protected void addValidator(Validator validator, InputField node) {
        validators.add(new Pair<>(validator, node));
        node.getTextField().focusedProperty().addListener(((observable, oldValue, newValue) -> {
            if (oldValue && !newValue) {
                validator.vaildateAndSet(node.textProperty().getValue());
                setError(validator, node);
            }
        }));
    }

    protected void setError(Validator validator, InputField node) {
        if (validator.getException() != null) {
            String error = "'" + node.promptTextProperty().get() + "': ";
            try {
                error += bundle.getString(validator.getException().getMessage()).toLowerCase();
            } catch (MissingResourceException ex) {
                error += bundle.getString("error");
            }
            labelError.setText(error);
        } else {
            labelError.setText("");
        }
    }

    protected void setError(String error) {
        labelError.setText(error);
    }

    private void load(String fxml) {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource(fxml));
        fxmlLoader.setResources(bundle);
        fxmlLoader.setController(this);
        try {
            root = fxmlLoader.load();
            if (scene == null) {
                scene = new Scene(root);
                stage.setScene(scene);
            } else {
                scene.setRoot(root);
            }

            root.autosize();
            stage.sizeToScene();
            stage.initModality(Modality.APPLICATION_MODAL);
        } catch (IOException e) {
            e.printStackTrace(System.err);
            App.mainApp.errorAlert(bundle.getString("error.client.internal"));
        }
    }

    private void positionStage() {
        double x = getX();
        double y = getY();

        // if the user has specified an x/y location, use it
        if (!Double.isNaN(x) && !Double.isNaN(y) &&
                Double.compare(x, prefX) != 0 && Double.compare(y, prefY) != 0) {
            // weird, but if I don't call setX/setY here, the stage
            // isn't where I expect it to be (in instances where a single
            // dialog is shown and closed multiple times). I expect the
            // second showing to be in the place the dialog was when it
            // was closed the first time, but on Windows it jumps to the
            // top-left of the screen.
            setX(x);
            setY(y);
            return;
        }

        // Firstly we need to force CSS and layout to happen, as the dialogPane
        // may not have been shown yet (so it has no dimensions)
        root.applyCss();
        root.layout();

        final Window owner = getOwner();
        final Scene ownerScene = owner.getScene();

        // scene.getY() seems to represent the y-offset from the top of the titlebar to the
        // start point of the scene, so it is the titlebar height
        final double titleBarHeight = ownerScene.getY();

        // because Stage does not seem to centre itself over its owner, we
        // do it here.

        // then we can get the dimensions and position the dialog appropriately.
        final double dialogWidth = root.prefWidth(-1);
        final double dialogHeight = root.prefHeight(dialogWidth);

//        stage.sizeToScene();

        x = owner.getX() + (ownerScene.getWidth() / 2.0) - (dialogWidth / 2.0);
        y = owner.getY() + titleBarHeight / 2.0 + (ownerScene.getHeight() / 2.0) - (dialogHeight / 2.0);

        prefX = x;
        prefY = y;

        setX(x);
        setY(y);
    }

    public double getX() {
        return stage.getX();
    }

    public void setX(double x) {
        stage.setX(x);
    }

    public Node getRoot() {
        return stage.getScene().getRoot();
    }

    public double getY() {
        return stage.getY();
    }

    public void setY(double y) {
        stage.setY(y);
    }

    public Window getOwner() {
        return stage.getOwner();
    }

    public Optional<T> showAndWait() {
        scene.setRoot(root);
        stage.centerOnScreen();
        stage.showAndWait();
        return result;
    }

    public void close() {
        if (stage.isShowing()) {
            stage.hide();
        }

        // Refer to RT-40687 for more context
        if (scene != null) {
            scene.setRoot(DUMMY_ROOT);
        }
    }

    public void setTitle(String title) {
        stage.setTitle(title);
    }

    protected void setResult(T result) {
        if (result == null)
            this.result = Optional.empty();
        else
            this.result = Optional.of(result);
    }
}
