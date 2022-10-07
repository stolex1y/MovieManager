package ru.stolexiy.client.ui.view.controls;

import javafx.util.Pair;

import java.util.Arrays;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

import static jdk.internal.org.objectweb.asm.commons.Method.getMethod;

public class ChoiceEnumBox<T extends Enum<T>> extends ChoiceBox<T> {

    public ChoiceEnumBox() {
        setMaxHeight(46);
        setHeight(46);
    }

    public void setItemList(T[] enums, ResourceBundle bundle) {
        setItemList(Arrays.stream(enums)
                .map(it -> new Pair<>(it.name(), it))
                .map(pair -> {
                    try {
                        return new Pair<>(bundle.getString(pair.getKey().toLowerCase()), pair.getValue());
                    } catch (MissingResourceException ignored) {
                        return pair;
                    }
                })
                .collect(Collectors.toList()));
    }

    @Override
    public void setSelected(T selected) {
        setValue(getItems().stream().filter(it -> it.getValue() == selected).findFirst().get());
    }
}
