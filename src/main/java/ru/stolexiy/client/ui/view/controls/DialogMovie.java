package ru.stolexiy.client.ui.view.controls;

import javafx.fxml.FXML;
import javafx.util.Pair;
import ru.stolexiy.data.*;

import java.util.*;

public class DialogMovie extends DialogForm<Movie> {
    @FXML
    private InputField name;
    @FXML
    private InputField productionYear;
    @FXML
    private ChoiceEnumBox<Country> country;
    @FXML
    private ChoiceEnumBox<MovieGenre> genre;
    @FXML
    private InputField director;
    @FXML
    private InputField budget;
    @FXML
    private InputField fees;
    @FXML
    private ChoiceEnumBox<MpaaRating> mpaaRating;
    @FXML
    private InputField duration;
    @FXML
    private InputField oscarsCount;

    public DialogMovie(ResourceBundle bundle, Movie initial) {
        super("/layouts/dialog-movie.fxml", bundle, initial);

        mpaaRating.setItemList(MpaaRating.values(), bundle);
        genre.setItemList(MovieGenre.values(), bundle);
        country.setItemList(Country.values(), bundle);

        if (isEditing) {
            resetValues();
        }

        director.getTextField().setEditable(false);
        director.onClicked(event -> {
            Person init = oldValue != null ? oldValue.getDirector() : result.get().getDirector();
            DialogPerson dialogPerson = new DialogPerson(bundle, init);
            Optional<Person> result = dialogPerson.showAndWait();
            result.ifPresent(person -> {
                this.result.get().setDirector(person);
                setDirectorName(person.getName());
            });
        });
    }

    @Override
    protected void setInitialValue(Movie initial) {
        String title;
        if (initial != null) {
            isEditing = true;
            oldValue = initial;
            setResult(new Movie(oldValue));
            title = bundle.getString("film.editing");
        } else {
            isEditing = false;
            setResult(new Movie("Film", null));
            title = bundle.getString("film.adding");
        }
        labelTitle.setText(title);
        setTitle(title);
    }

    private void resetValues() {
        name.setText(oldValue.getName());
        productionYear.setText(oldValue.getProductionYear());
        country.setSelected(oldValue.getCountry());
        genre.setSelected(oldValue.getGenre());
        director.setText(oldValue.getDirector().getName());
        budget.setText(oldValue.getBudget());
        fees.setText(oldValue.getFees());
        mpaaRating.setSelected(oldValue.getMpaaRating());
        duration.setText(oldValue.getDurationInMinutes());
        oscarsCount.setText(oldValue.getOscarsCount());
    }

    @Override
    protected void setButtons() {
        super.setButtons();
        buttonSave.setOnAction(event -> {
            Optional<Pair<Validator, InputField>> firstInvalid = validators.stream()
                    .filter(pair -> pair.getValue().isVisible())
                    .filter(pair -> {
                        boolean isValid = pair.getKey()
                                .vaildateAndSet(pair.getValue().textProperty().getValue());
                        return !isValid;
                    }).findFirst();
            if (!firstInvalid.isPresent()) {
                result.get().setCountry(country.getSelected());
                result.get().setMpaaRating(mpaaRating.getSelected());
                result.get().setGenre(genre.getSelected());
                close();
            } else {
                setError(firstInvalid.get().getKey(), firstInvalid.get().getValue());
            }
        });
    }

    @Override
    protected void fillValidators() {
        addValidator(new Validator(result.get()::setName), name);
        addValidator(new Validator(result.get()::setProductionYear), productionYear);
        addValidator(new Validator(result.get()::setOscarsCount), oscarsCount);
        addValidator(new Validator(result.get()::setBudget), budget);
        addValidator(new Validator(result.get()::setFees), fees);
        addValidator(new Validator(result.get()::setDurationInMinutes), duration);
        addValidator(new Validator(this::setDirectorName), director);
    }

    private void setDirectorName(String director) {
        if (director == null || director.equals(""))
            throw new IllegalArgumentException("error.film.director");
        else
            this.director.setText(director);
    }
}
