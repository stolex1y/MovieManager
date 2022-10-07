package ru.stolexiy.client.ui.view;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import ru.stolexiy.client.ui.App;
import ru.stolexiy.client.ui.view.controls.ChoiceLangBox;
import ru.stolexiy.client.ui.view.controls.FilmCard;
import ru.stolexiy.client.ui.viewmodel.MainViewModel;
import ru.stolexiy.data.Movie;

import java.net.URL;
import java.util.*;
import java.util.stream.Collectors;

public class MainController implements Initializable {

    public static final String layout = "/layouts/main.fxml";

    private static final int pageSize = 9;
    @FXML
    public Label labelTitle;
    @FXML
    public Label labelLogin;
    @FXML
    public MenuButton menuCollection;
    @FXML
    public MenuButton menuStatistic;
    @FXML
    public Hyperlink linkFilmsMap;
    @FXML
    public Button buttonFilmsAll;
    @FXML
    public Button buttonFilmsUser;
    @FXML
    public Label labelPages;
    @FXML
    public ImageView buttonPrevPage;
    @FXML
    public Label labelPage;
    @FXML
    public ImageView buttonNextPage;
    @FXML
    public VBox vboxMenu;
    @FXML
    public ImageView buttonAddMovie;
    @FXML
    public ImageView buttonSort;
    @FXML
    public ImageView buttonFilter;
    @FXML
    public MenuItem menuItemDirectorAsc;
    @FXML
    public MenuItem menuItemDropLowerId;
    @FXML
    public MenuItem menuItemClear;
    @FXML
    public MenuItem menuItemOscarsAvg;
    @FXML
    public MenuItem menuItemBudgetSum;
    @FXML
    public MenuItem menuItemCountFilmCountry;
    @FXML
    public MenuItem menuItemCountFilmYear;
    @FXML
    public MenuItem menuItemCountBudgetLess;
    @FXML
    public MenuItem menuItemInfo;
    @FXML
    private ChoiceLangBox choiceLang;
    @FXML
    private GridPane films;
    @FXML
    private ScrollPane scrollPane;
    private final List<FilmCard> filmCards = new ArrayList<>();
    private MainViewModel viewModel;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        viewModel = new MainViewModel(resources, App.mainApp.getArg("user"));
        labelLogin.setText(viewModel.getUser().getLogin());
        setPropertiesBind();
        setButtons();
        setFonts();
        setStyles();
        viewModel.updatePage();
    }

    private void setFonts() {
        labelPage.setFont(ResourceLoader.loadFont("NunitoSans-Regular", 18));
        labelTitle.setFont(ResourceLoader.loadFont("RubikMonoOne-Regular", 20));
        vboxMenu.getChildren()
                .stream()
                .filter(it -> it.getClass() == MenuButton.class)
                .map(it -> (MenuButton) it)
                .forEach(it -> it.setFont(ResourceLoader.loadFont("NunitoSans-Bold", 20)));
        linkFilmsMap.setFont(ResourceLoader.loadFont("NunitoSans-Bold", 20));
        buttonFilmsAll.setFont(ResourceLoader.loadFont("NunitoSans-Bold", 20));
        buttonFilmsUser.setFont(ResourceLoader.loadFont("NunitoSans-Bold", 20));
        labelLogin.setFont(ResourceLoader.loadFont("NunitoSans-Regular", 18));

    }

    private void setButtons() {
        buttonNextPage.setOnMouseClicked(event -> viewModel.nextPage());
        buttonPrevPage.setOnMouseClicked(event -> viewModel.prevPage());

        buttonFilmsAll.setOnAction(event -> {
            viewModel.setIsAllFilms();
            buttonFilmsAll.getStyleClass().add("underlined-button");
            buttonFilmsUser.getStyleClass().remove("underlined-button");
        });
        buttonFilmsUser.setOnAction(event -> {
            viewModel.setIsUserFilms();
            buttonFilmsUser.getStyleClass().add("underlined-button");
            buttonFilmsAll.getStyleClass().remove("underlined-button");
        });

        buttonAddMovie.setOnMouseClicked(event -> viewModel.addMovie());
        buttonFilter.setOnMouseClicked(event -> viewModel.filter());
        buttonSort.setOnMouseClicked(event -> viewModel.sort());

        menuItemDirectorAsc.setOnAction(event -> viewModel.getAllDirectorsAsc());
        menuItemDropLowerId.setOnAction(event -> viewModel.removeLowerThanId());
        menuItemClear.setOnAction(event -> viewModel.clearUserMovies());
        menuItemOscarsAvg.setOnAction(event -> viewModel.getAvgOscarsCount());
        menuItemBudgetSum.setOnAction(event -> viewModel.getAllBudgetsSum());
        menuItemCountFilmCountry.setOnAction(event -> viewModel.getCountByCountry());
        menuItemCountFilmYear.setOnAction(event -> viewModel.getCountByYear());
        menuItemCountBudgetLess.setOnAction(event -> viewModel.getCountLowerBudget());
        menuItemInfo.setOnAction(event -> viewModel.getCollectionInfo());

    }

    private void setPropertiesBind() {
        viewModel.pageLoadedProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue) {
                List<Movie> page = new ArrayList<>(viewModel.getMoviesPage());
                updateFilmCards(page);
            }
        });
        labelPage.textProperty().bind(viewModel.pageProperty().asString());
        buttonNextPage.disableProperty().bind(viewModel.hasNextPageProperty().not());
        buttonPrevPage.disableProperty().bind(viewModel.hasPrevPageProperty().not());
        viewModel.hasNextPageProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue) {
                buttonNextPage.setImage(new Image("/img/next-en.png"));
                buttonNextPage.getStyleClass().add("clickable");
            } else {
                buttonNextPage.setImage(new Image("/img/next-dis.png"));
                buttonNextPage.getStyleClass().remove("clickable");
            }
        });
        viewModel.hasPrevPageProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue) {
                buttonPrevPage.setImage(new Image("/img/prev-en.png"));
                buttonPrevPage.getStyleClass().add("clickable");
            } else {
                buttonPrevPage.setImage(new Image("/img/prev-dis.png"));
                buttonPrevPage.getStyleClass().remove("clickable");
            }
        });

        viewModel.filterByProperty().addListener(((observable, oldValue, newValue) -> {
            if (newValue != null) {
                buttonFilter.setImage(new Image("/img/filter-en.png"));
            } else {
                buttonFilter.setImage(new Image("/img/filter-dis.png"));
            }
        }));
    }

    private void setStyles() {
        vboxMenu.getChildren()
                .stream()
                .filter(it -> it.getClass() == MenuButton.class)
                .map(it -> (MenuButton) it)
                .forEach(it -> {
                    it.setStyle("-fx-background-color: transparent; -fx-background-radius: 0; -fx-border-radius: 0");
                    it.setPadding(new Insets(0));
                });
        buttonFilmsUser.setStyle("-fx-background-color: transparent");
        buttonFilmsAll.setStyle("-fx-background-color: transparent");

        if (viewModel.getIsUserFilms())
            buttonFilmsUser.getStyleClass().add("underlined-button");
        else
            buttonFilmsAll.getStyleClass().add("underlined-button");
    }

    private void fillGridPane(List<FilmCard> page) {
        int row = 0, column = 0;
        films.getChildren().clear();
        for (FilmCard film : page) {
            films.add(film.getCard(), column, row);
            if (column == 1) {
                GridPane.setMargin(film.getCard(), new Insets(0, 20, 40, 20));
            } else {
                GridPane.setMargin(film.getCard(), new Insets(0, 0, 40, 0));
            }
            if (column == 2) {
                row++;
            }
            column = (column + 1) % 3;
        }
    }

    private void updateFilmCards(List<Movie> page) {
        if (filmCards.size() == 0 || !listsEquals(filmCards, page)) {
            Platform.runLater(() -> {
                filmCards.clear();
                filmCards.addAll(page.stream().map(movie -> new FilmCard(viewModel.getBundle(), viewModel.getUser(), movie))
                        .peek(filmCard -> {
                            filmCard.setOnDropped(viewModel::restartLoadingMovies);
                            filmCard.setOnEdit(viewModel::restartLoadingMovies);
                        })
                        .collect(Collectors.toList()));
                fillGridPane(filmCards);
            });
        }
    }

    private boolean listsEquals(List<FilmCard> old, List<Movie> updated) {
        List<Movie> moviesOld = old.stream().map(FilmCard::getMovie).collect(Collectors.toList());
        if (moviesOld.size() != updated.size())
            return false;
        for (int i = 0; i < old.size(); i++) {
            if (!moviesOld.get(i).equals(updated.get(i)))
                return false;
        }
        return true;
    }

}
