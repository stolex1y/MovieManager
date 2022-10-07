package ru.stolexiy.client.ui.view.controls;

import javafx.fxml.FXML;
import ru.stolexiy.data.Movie;
import ru.stolexiy.data.Person;

import java.util.ResourceBundle;

public class DialogCommandArg<T> extends DialogForm<T> {

    @FXML
    private ChoiceEnumBox choiceEnum;
    @FXML
    private InputField inputField;

    private FilterMovie filter;

    private final Movie testMovie = new Movie("Test", new Person("Test"));

    public DialogCommandArg(ResourceBundle bundle, FilterMovie filterMovie) {
        super("/layouts/dialog-command.fxml", bundle, null);
        this.filter = filterMovie;
        setTitles(bundle.getString("command.execution"));
        inputField.setVisible(true);
        inputField.setPromptText(bundle.getString("film." + filterMovie.getField().getName()));
    }

    public DialogCommandArg(ResourceBundle bundle, T[] enumValues) {
        super("/layouts/dialog-command.fxml", bundle, null);
        if (enumValues.length == 0 || !enumValues[0].getClass().isEnum())
            throw new IllegalArgumentException();
        setTitles(bundle.getString("command.execution"));
        choiceEnum.setVisible(true);
        choiceEnum.setItemList((Enum[]) enumValues, bundle);
    }

    private void setTitles(String title) {
        setTitle(title);
        labelTitle.setText(title);
    }

    @Override
    protected void setInitialValue(T initial) {
        setResult(null);
    }

    @Override
    protected void setButtons() {
        super.setButtons();
        buttonSave.setOnAction(event -> {
            if (inputField.isVisible()) {
                filter.setValue(inputField.textProperty().get());
                if (filter.isValidValue(testMovie)) {
                    setResult((T) filter.getValue());
                    close();
                } else {
                    setError(bundle.getString(filter.getErrorText()));
                }
            } else {
                setResult((T) choiceEnum.getSelected());
                close();
            }
        });
    }
}
