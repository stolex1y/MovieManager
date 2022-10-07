package ru.stolexiy.client.ui.view.controls;

import javafx.fxml.FXML;
import javafx.util.Pair;
import ru.stolexiy.data.Country;
import ru.stolexiy.data.Person;
import ru.stolexiy.data.Validator;

import java.util.Optional;
import java.util.ResourceBundle;

public class DialogPerson extends DialogForm<Person> {
    @FXML
    private InputField name;
    @FXML
    private InputField growthInMetres;
    @FXML
    private ChoiceEnumBox<Country> nationality;
    @FXML
    private InputField filmCount;

    public DialogPerson(ResourceBundle bundle, Person initial) {
        super("/layouts/dialog-person.fxml", bundle, initial);

        nationality.setItemList(Country.values(), bundle);

        if (isEditing) {
            resetValues();
        }
    }

    @Override
    protected void setInitialValue(Person initial) {
        String title;
        if (initial != null) {
            isEditing = true;
            oldValue = initial;
            setResult(new Person(oldValue));
            title = bundle.getString("person.editing");
        } else {
            isEditing = false;
            setResult(new Person("Director"));
            title = bundle.getString("person.adding");
        }
        labelTitle.setText(title);
        setTitle(title);
    }

    @Override
    protected void setButtons() {
        super.setButtons();
        buttonSave.setOnAction(event -> {
            Optional<Pair<Validator, InputField>> firstInvalid = validators.stream()
                    .filter(pair -> {
                        boolean isValid = pair.getKey()
                                .vaildateAndSet(pair.getValue().textProperty().getValue());
                        return !isValid;
                    }).findFirst();
            if (!firstInvalid.isPresent()) {
                result.get().setNationality(nationality.getSelected());
                close();
            } else {
                setError(firstInvalid.get().getKey(), firstInvalid.get().getValue());
            }
        });
    }

    @Override
    protected void fillValidators() {
        addValidator(new Validator(result.get()::setName), name);
        addValidator(new Validator(result.get()::setGrowthInMetres), growthInMetres);
        addValidator(new Validator(result.get()::setFilmCount), filmCount);
    }

    private void resetValues() {
        name.setText(oldValue.getName());
        growthInMetres.setText(oldValue.getGrowthInMetres());
        nationality.setSelected(oldValue.getNationality());
        filmCount.setText(oldValue.getFilmCount());
    }

}
