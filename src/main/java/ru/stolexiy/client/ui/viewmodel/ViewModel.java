package ru.stolexiy.client.ui.viewmodel;

import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.concurrent.WorkerStateEvent;
import ru.stolexiy.client.exceptions.InternalServerException;
import ru.stolexiy.client.ui.App;
import ru.stolexiy.client.ui.view.controls.Alert;

import java.util.ResourceBundle;

public abstract class ViewModel {

    protected final ViewModel saved;

    protected final ResourceBundle bundle;

    private final Alert warningAlert = new Alert(Alert.AlertType.WARNING);

    public ViewModel(ResourceBundle bundle) {
        this.bundle = bundle;
        saved = afterRestart();
        save();
    }

    public final void save() {
        App.mainApp.putSaveArg(getClass(), this);
    }

    protected <T extends ViewModel> T afterRestart() {
        return App.mainApp.getSaveArg(getClass());
    }

    protected <T> void tryExec(Task<T> task) {
        task.addEventHandler(WorkerStateEvent.WORKER_STATE_FAILED, event -> {
            Throwable ex = task.getException();
            if (ex != null) {
                if (ex instanceof InternalServerException) {
                    ex.printStackTrace(System.err);
                    warningAlert(bundle.getString("error.server.internal"));
                } else if (!(ex instanceof InterruptedException)) {
                    ex.printStackTrace(System.err);
                    errorAlert(bundle.getString("error.client.internal"));
                }
            }
        });
        App.mainApp.submitTask(task);
    }

    protected void tryExec(NetworkTask networkTask) {
        tryExec(new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                networkTask.run();
                return null;
            }
        });
    }

    protected void errorAlert(String msg) {
        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setContentText(msg);
            alert.setTitle(bundle.getString("error"));
            alert.showAndWait();
//            App.mainApp.appClose();
        });
    }

    protected void warningAlert(String msg) {
        Platform.runLater(() -> {
            if (!warningAlert.isShowing() || !warningAlert.getContentText().equals(msg)) {
                warningAlert.setContentText(msg);
                warningAlert.setTitle(bundle.getString("error"));
                warningAlert.show();
            }
        });
    }

    public ResourceBundle getBundle() {
        return bundle;
    }

    protected static interface NetworkTask {
        void run() throws Exception;
    }
}
