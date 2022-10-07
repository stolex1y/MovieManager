package ru.stolexiy.client.ui.view.controls;

import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.DatePicker;
import javafx.util.Pair;
import ru.stolexiy.data.Ignore;
import ru.stolexiy.data.Movie;
import ru.stolexiy.data.Person;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

public class FilterChoiceDialog extends DialogForm<FilterMovie> {
    @FXML
    private ChoiceBox<ClassField> choiceField;
    @FXML
    private ChoiceEnumBox choiceEnum;
    @FXML
    private InputField inputField;
    @FXML
    private DatePicker datePicker;

    private Node chosen;
    private final Movie testMovie = new Movie("A", new Person("A"));

    public FilterChoiceDialog(ResourceBundle bundle, FilterMovie filter) {
        super("/layouts/dialog-filter.fxml", bundle, filter);
        choiceField.setPromptText(bundle.getString("filtering.field"));
        inputField.setPromptText(bundle.getString("filtering.value"));
        datePicker.setPromptText(bundle.getString("filtering.value"));
        choiceEnum.setPromptText(bundle.getString("filtering.value"));

        choiceField.setOnAction(event -> {
            chooseInputType();
        });
    }

    private void fillChoiceFieldItems() {
        final List<Pair<String, ClassField>> listOfFields = new ArrayList<>();
        listOfFields.addAll(
                Arrays.stream(Movie.class.getDeclaredFields())
                        .filter(f -> (!Modifier.isStatic(f.getModifiers()) && f.getAnnotation(Ignore.class) == null))
                        .map((Field field) -> {
                            String fieldName = "film." + field.getName();
                            try {
                                return new Pair<>(bundle.getString(fieldName),
                                        new ClassField(Movie.class, field));
                            } catch (MissingResourceException e) {
                                return new Pair<>(fieldName,
                                        new ClassField(Movie.class, field));
                            }
                        })
                        .collect(Collectors.toList())
        );
        listOfFields.addAll(
                Arrays.stream(Person.class.getDeclaredFields())
                        .filter(f -> !Modifier.isStatic(f.getModifiers()) && (f.getAnnotation(Ignore.class) == null))
                        .map((Field field) -> {
                            String fieldName = "person." + field.getName();
                            try {
                                return new Pair<>(bundle.getString("film.director") +
                                        ": " + bundle.getString(fieldName).toLowerCase(),
                                        new ClassField(Person.class, field));
                            } catch (MissingResourceException e) {
                                return new Pair<>(fieldName,
                                        new ClassField(Person.class, field));
                            }
                        })
                        .collect(Collectors.toList())
        );

        choiceField.setItemList(listOfFields);
    }

    private void chooseInputType() {
        choiceEnum.setVisible(false);
        inputField.setVisible(false);
        datePicker.setVisible(false);
        Class chosenFieldType = choiceField.getSelected().getField().getType();
        if (chosenFieldType.isEnum()) {
            choiceEnum.setVisible(true);
            chosen = choiceEnum;
            choiceEnum.setItemList((Enum[]) chosenFieldType.getEnumConstants(), bundle);
        } else if (chosenFieldType == LocalDateTime.class) {
            datePicker.setVisible(true);
            chosen = datePicker;
        } else {
            inputField.setVisible(true);
            chosen = inputField;
        }
    }

    @Override
    protected void setButtons() {
        super.setButtons();
        buttonSave.setOnAction(event -> {
            boolean isValid = true;
            String error = "";
            result.get().setClassField(choiceField.getSelected());
            if (chosen == datePicker) {
                if (datePicker.getValue() == null) {
                    isValid = false;
                    error = bundle.getString("error.need.notnull");
                } else
                    result.get().setValue(datePicker.getValue());
            } else if (chosen == inputField) {
                result.get().setValue(inputField.getTextField().getText());
                if (!result.get().isValidValue(testMovie)) {
                    isValid = false;
                    error = bundle.getString(result.get().getErrorText());
                }
            } else {
                result.get().setValue(choiceEnum.getSelected());
            }

            if (isValid)
                close();
            else {
                setError(error);
            }
        });
    }

    private void setChosenValue(Object value) {
        if (chosen == null)
            return;
        if (chosen == choiceEnum)
            choiceEnum.setSelected((Enum) value);
        else if (chosen == datePicker)
            datePicker.setValue((LocalDate) value);
        else {
            if (value instanceof Double)
                inputField.setText((double) value);
            else if (value instanceof Number)
                inputField.setText(((Number) value).longValue());
            else
                inputField.setText(value.toString());
        }
    }

    @Override
    protected void setInitialValue(FilterMovie initial) {
        fillChoiceFieldItems();
        if (initial != null) {
            oldValue = initial;
            setResult(new FilterMovie(oldValue.getField(), oldValue.getFieldClass(), oldValue.getValue()));
            choiceField.setSelected(initial.getClassField());
            chooseInputType();
            setChosenValue(initial.getValue());
        } else {
            chooseInputType();
            setResult(new FilterMovie(null, null, null));
        }
        labelTitle.setText(bundle.getString("filtering"));
        setTitle(bundle.getString("filtering"));
    }
}

