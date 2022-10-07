package ru.stolexiy.client.ui.view.controls;

import javafx.fxml.FXML;
import javafx.util.Pair;
import ru.stolexiy.data.Ignore;
import ru.stolexiy.data.Movie;
import ru.stolexiy.data.Person;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.*;
import java.util.stream.Collectors;

public class SortChoiceDialog extends DialogForm<SortMovie> {

    @FXML
    private ChoiceBox<ClassField> choiceField;
    @FXML
    private ChoiceBox<Boolean> choiceSortOrder;

    public SortChoiceDialog(ResourceBundle bundle, SortMovie sort) {
        super("/layouts/dialog-sort.fxml", bundle, sort);
        labelTitle.setText(bundle.getString("sorting"));
        setTitle(bundle.getString("sorting"));
        choiceField.setPromptText(bundle.getString("sorting.field"));
        choiceSortOrder.setPromptText(bundle.getString("sorting.order"));

        choiceField.setOnAction(event -> result.get().setClassField(choiceField.getSelected()));
        choiceSortOrder.setOnAction(event -> result.get().setOrder(choiceSortOrder.getSelected()));
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

    @Override
    protected void setInitialValue(SortMovie initial) {
        fillChoiceFieldItems();
        choiceSortOrder.setItemList(Arrays.asList(
                new Pair<>(bundle.getString("sorting.ascending"), true),
                new Pair<>(bundle.getString("sorting.descending"), false)
        ));
        setResult(new SortMovie(null, true));
        if (initial != null) {
            result.get().setClassField(initial.getClassField());
            result.get().setOrder(initial.isAscending());
            choiceField.setSelected(initial.getClassField());
            choiceSortOrder.setSelected(initial.isAscending());
        }
    }

    @Override
    protected void setButtons() {
        super.setButtons();
        buttonSave.setOnAction(event -> {
            close();
        });
    }
}
