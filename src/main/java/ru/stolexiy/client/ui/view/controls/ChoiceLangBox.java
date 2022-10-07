package ru.stolexiy.client.ui.view.controls;

import javafx.event.Event;
import javafx.util.Pair;
import ru.stolexiy.client.ui.App;
import ru.stolexiy.client.ui.view.ResourceLoader;

import java.util.Comparator;
import java.util.Locale;

public class ChoiceLangBox extends ChoiceBox<Locale> {

    public ChoiceLangBox() {
        super();
        initChoice();
    }

    private void initChoice() {
        getItems().addAll(ResourceLoader.getAllLanguages());
        getItems().sort(Comparator.comparing(Pair::getKey));
        setValue(new Pair<>(App.mainApp.getBundle().getString("language"), Locale.getDefault()));
        setOnAction(this::onLangChoose);
    }

    public void onLangChoose(Event event) {
        Locale.setDefault(getValue().getValue());
        App.mainApp.updateBundleByDefaultLocale();
    }

}
