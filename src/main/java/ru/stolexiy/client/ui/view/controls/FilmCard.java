package ru.stolexiy.client.ui.view.controls;

import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.Labeled;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import ru.stolexiy.client.exceptions.InternalClientException;
import ru.stolexiy.client.exceptions.InternalServerException;
import ru.stolexiy.client.network.MovieRepository;
import ru.stolexiy.client.ui.App;
import ru.stolexiy.client.ui.view.LocalFormatter;
import ru.stolexiy.client.ui.view.ResourceLoader;
import ru.stolexiy.client.ui.viewmodel.ViewModel;
import ru.stolexiy.data.Movie;
import ru.stolexiy.data.User;

import java.io.IOException;
import java.util.Optional;
import java.util.ResourceBundle;

public class FilmCard extends ViewModel {

    private VBox root;
    @FXML
    private Label title;
    @FXML
    private ImageView poster;
    @FXML
    private VBox charKeys;
    @FXML
    private VBox charValues;
    @FXML
    private Label filmAbout;
    @FXML
    private Label id;
    @FXML
    private Label owner;
    @FXML
    private Label productionYear;
    @FXML
    private Label country;
    @FXML
    private Label genre;
    @FXML
    private Label director;
    @FXML
    private Label budget;
    @FXML
    private Label fees;
    @FXML
    private Label mpaaRating;
    @FXML
    private Label durationInMinutes;
    @FXML
    private Label oscarsCount;
    @FXML
    private Label creationDate;
    @FXML
    private ImageView buttonEdit;
    @FXML
    private ImageView buttonDrop;
    private final Movie movie;
    private Runnable onDropped;
    private User user;
    private Task<Void> dropMovie = getDropMovieTask();
    private Task<Void> editMovie = getEditMovieTask();
    private Runnable onEdit;

    public FilmCard(ResourceBundle bundle, User user, Movie movie) {
        super(bundle);
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/layouts/film-card.fxml"));
        fxmlLoader.setResources(App.mainApp.getBundle());
        fxmlLoader.setController(this);
        try {
            root = fxmlLoader.load();
        } catch (IOException e) {
            e.printStackTrace(System.err);
            App.mainApp.errorAlert("error.client.internal");
        }

        this.title.setFont(ResourceLoader.loadFont("RubikMonoOne-Regular", 20));

        filmAbout.setFont(ResourceLoader.loadFont("NunitoSans-Bold", 18));

        charKeys.getChildren().forEach(it -> ((Labeled) it).setFont(ResourceLoader.loadFont("NunitoSans-Regular", 18)));
        charValues.getChildren().forEach(it -> ((Labeled) it).setFont(ResourceLoader.loadFont("NunitoSans-Regular", 18)));

        this.user = user;
        this.movie = movie;

        setMovie(movie);

        if (this.user.getLogin().equals(movie.getOwner())) {
            buttonEdit.setImage(new Image("/img/edit-en.png"));
            buttonEdit.getStyleClass().add("clickable");
            buttonDrop.setImage(new Image("/img/drop-en.png"));
            buttonDrop.getStyleClass().add("clickable");
            buttonEdit.setOnMouseClicked(event -> {
                Optional<Movie> result = new DialogMovie(bundle, movie).showAndWait();
                if (result.isPresent()) {
                    setMovie(result.get());
                    tryExec(getEditMovieTask());
                }
            });
            buttonDrop.setOnMouseClicked(event -> tryExec(getDropMovieTask()));
        } else {
            buttonEdit.setImage(new Image("/img/edit-dis.png"));
            buttonDrop.setImage(new Image("/img/drop-dis.png"));
            buttonEdit.setDisable(true);
            buttonDrop.setDisable(true);
        }
    }

    public void setMovie(Movie movie) {
        this.movie.copy(movie);
        Platform.runLater(() -> {
            title.setText(movie.getName());
            id.textProperty().setValue(String.valueOf(movie.getId()));
            owner.textProperty().setValue(movie.getOwner());
            productionYear.setText(String.valueOf(movie.getProductionYear()));
            country.setText(bundle.getString(movie.getCountry().name().toLowerCase()));
            genre.setText(bundle.getString(movie.getGenre().name().toLowerCase()));
            director.setText(movie.getDirector().getName());
            budget.setText(LocalFormatter.formatMoney(movie.getBudget()));
            fees.setText(LocalFormatter.formatMoney(movie.getFees()));
            mpaaRating.setText(movie.getMpaaRating().name());
            durationInMinutes.setText(String.valueOf(movie.getDurationInMinutes()));
            oscarsCount.setText(String.valueOf(movie.getOscarsCount()));
            creationDate.setText(LocalFormatter.formatDateTime(movie.getCreationDate()));
        });
    }

    private Task<Void> getDropMovieTask() {
        if (dropMovie == null || dropMovie.isDone())
            return new Task<Void>() {
                @Override
                protected Void call() throws InternalServerException, InternalClientException {
                    if (movie != null) {
                        MovieRepository.removeById(user, movie.getId());
                        Platform.runLater(onDropped);
                    }
                    return null;
                }
            };
        else
            return dropMovie;
    }

    private Task<Void> getEditMovieTask() {
        return new Task<Void>() {
            @Override
            protected Void call() throws InternalServerException, InternalClientException {
                if (movie != null) {
                    MovieRepository.update(user, movie);
                    Platform.runLater(onEdit);
                }
                return null;
            }
        };
    }

    public void setOnDropped(Runnable onDropped) {
        this.onDropped = onDropped;
    }

    public void setOnEdit(Runnable onEdit) {
        this.onEdit = onEdit;
    }

    public Node getCard() {
        return root;
    }

    public Movie getMovie() {
        return movie;
    }
}
