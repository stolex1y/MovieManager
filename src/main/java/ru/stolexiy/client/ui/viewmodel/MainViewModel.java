package ru.stolexiy.client.ui.viewmodel;

import javafx.application.Platform;
import javafx.beans.property.*;
import javafx.concurrent.Task;
import ru.stolexiy.client.exceptions.InternalServerException;
import ru.stolexiy.client.network.MovieRepository;
import ru.stolexiy.client.ui.view.LocalFormatter;
import ru.stolexiy.client.ui.view.controls.*;
import ru.stolexiy.data.*;

import java.lang.reflect.Field;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Collectors;

public class MainViewModel extends ViewModel {
    private final User user;
    private final List<Movie> movies = new ArrayList<>();
    private final ReentrantLock moviesLock = new ReentrantLock();
    private final List<Movie> moviesPage = new ArrayList<>();
    private final BooleanProperty pageLoaded = new SimpleBooleanProperty(false);
    private final BooleanProperty moviesLoaded = new SimpleBooleanProperty(false);
    private final IntegerProperty page = new SimpleIntegerProperty(1);
    private final BooleanProperty hasNextPage = new SimpleBooleanProperty(false);
    private final BooleanProperty hasPrevPage = new SimpleBooleanProperty(false);
    private final IntegerProperty pageCount = new SimpleIntegerProperty(0);
    private final int pageSize = 9;
    private final ObjectProperty<FilterMovie> filterBy = new SimpleObjectProperty<>(null);
    private final ObjectProperty<SortMovie> sortBy = new SimpleObjectProperty<>(null);
    private final BooleanProperty isUserFilms = new SimpleBooleanProperty(false);
    private Task<Void> loadMovies = getLoadMovies();

    public MainViewModel(ResourceBundle bundle, User user) {
        super(bundle);
        restoreState((MainViewModel) saved);
        this.user = user;
        if (sortBy.get() == null)
            initDefaultSort();
        loadMovies();

        page.addListener((obs, oldVal, newVal) -> updatePage());
        moviesLoaded.addListener((observable, oldValue, newValue) -> {
            if (newValue) {
                pageCount.set(countPage(movies.size()));
                updatePage();
            }
        });
        isUserFilms.addListener((observable, oldValue, newValue) -> restartLoadingMovies());
        filterBy.addListener((observable, oldValue, newValue) -> restartLoadingMovies());
        sortBy.addListener((observable, oldValue, newValue) -> restartLoadingMovies());
    }

    private void initDefaultSort() {
        try {
            this.sortBy.set(
                    new SortMovie(
                            new ClassField(Movie.class, Movie.class.getDeclaredField("id")),
                            true
                    ));
        } catch (NoSuchFieldException e) {
            e.printStackTrace(System.err);
            errorAlert(bundle.getString("error.client.internal"));
        }
    }

    private void restoreState(MainViewModel saved) {
        if (saved != null) {
            this.page.set(saved.page.get());
            this.filterBy.set(saved.filterBy.get());
            this.sortBy.set(saved.sortBy.get());
            this.isUserFilms.set(saved.isUserFilms.get());
        }
    }

    private Task<Void> getLoadMovies() {
        if (loadMovies == null || loadMovies.isDone()) {
            return new Task<Void>() {
                @Override
                protected Void call() throws Exception {
                    while (true) {
                        moviesLoaded.set(false);
                        movies.clear();
                        movies.addAll(MovieRepository.getAll(user));
                        filter(movies);
                        sort(movies);
                        moviesLoaded.set(true);
                        Thread.sleep(5000);
                    }
                }

                @Override
                protected void failed() {
                    if (getException().getClass() == InternalServerException.class) {
                        loadMovies();
                    }
                    /*if (getException().getClass() == InterruptedException.class) {
                        loadMovies();
                    }*/
                }
            };
        } else
            return loadMovies;
    }

    private int countPage(int moviesSize) {
        return (int) Math.ceil((double) moviesSize / pageSize);
    }

    public void restartLoadingMovies() {
        loadMovies.cancel(true);
        loadMovies();
    }

    private void loadMovies() {
        loadMovies = getLoadMovies();
        Platform.runLater(() -> tryExec(loadMovies));
    }

    public void addMovie() {
        Platform.runLater(() -> {
            Optional<Movie> result = new DialogMovie(bundle, null).showAndWait();
            if (!result.isPresent())
                return;
            tryExec(() -> {
                MovieRepository.add(user, result.get());
                restartLoadingMovies();
            });
        });
    }

    public void updatePage() {
        pageLoaded.set(false);
        moviesPage.clear();
        moviesPage.addAll(
                movies.stream()
                        .skip((long) pageSize * (page.get() - 1))
                        .limit(pageSize)
                        .collect(Collectors.toList())
        );
        pageLoaded.set(true);
        hasNextPage.setValue(page.intValue() < pageCount.get());
        hasPrevPage.setValue(page.intValue() > 1);
    }

    public IntegerProperty pageProperty() {
        return page;
    }

    public boolean getIsUserFilms() {
        return isUserFilms.get();
    }

    private void filter(List<Movie> movies) {
        FilterMovie filter = filterBy.get();

        movies.removeIf(m -> {
            if (isUserFilms.get() && !m.getOwner().equals(user.getLogin())) {
                return true;
            } else if (filter == null) {
                return false;
            } else {
                Field field = filter.getField();
                Class fieldClass = filter.getClassField().getClazz();
                Object filterValue = filter.getValue();
                field.setAccessible(true);
                Object currValue = null;
                try {
                    if (fieldClass == Movie.class) {
                        currValue = field.get(m);
                    } else {
                        currValue = field.get(m.getDirector());
                    }
                } catch (IllegalAccessException e) {
                    errorAlert(bundle.getString("error.client.internal"));
                }
                if (field.getType() == LocalDateTime.class)
                    return !filterValue.equals(((LocalDateTime) currValue).toLocalDate());
                else
                    return !filterValue.equals(currValue);
            }
        });
    }

    public void sort() {
        Platform.runLater(() -> {
            Optional<SortMovie> result = new SortChoiceDialog(bundle, sortBy.get()).showAndWait();
            if (result.isPresent())
                sortBy.set(result.get());
            else
                initDefaultSort();
        });
    }

    private void sort(List<Movie> movies) {
        if (sortBy.get() == null)
            return;
        boolean ascending = sortBy.get().isAscending();
        Comparator<Movie> comparator = Comparator.comparing((Movie movie) -> {
            Field field = sortBy.get().getField();
            Class clazz = sortBy.get().getFieldClass();
            field.setAccessible(true);
            try {
                if (clazz == Movie.class)
                    return (Comparable) field.get(movie);
                else
                    return (Comparable) field.get(movie.getDirector());
            } catch (IllegalAccessException e) {
                e.printStackTrace(System.err);
                errorAlert(bundle.getString("error.client.internal"));
            }
            return null;
        });
        if (!ascending) {
            comparator = comparator.reversed();
        }
        movies.sort(comparator);
    }

    public List<Movie> getMoviesPage() {
        return moviesPage;
    }

    public BooleanProperty pageLoadedProperty() {
        return pageLoaded;
    }

    public User getUser() {
        return user;
    }

    public void setIsUserFilms() {
        if (!isUserFilms.get()) {
            page.set(1);
            isUserFilms.set(true);
        }
    }

    public void setIsAllFilms() {
        if (isUserFilms.get()) {
            page.set(1);
            isUserFilms.set(false);
        }
    }

    public BooleanProperty hasNextPageProperty() {
        return hasNextPage;
    }

    public BooleanProperty hasPrevPageProperty() {
        return hasPrevPage;
    }

    public void nextPage() {
        if (hasNextPage.get())
            page.set(page.get() + 1);
    }

    public void prevPage() {
        if (hasPrevPage.get())
            page.set(page.get() - 1);
    }

    public void filter() {
        Platform.runLater(() -> {
            Optional<FilterMovie> result = new FilterChoiceDialog(bundle, filterBy.get()).showAndWait();
            if (result.isPresent())
                filterBy.set(result.get());
            else
                filterBy.set(null);
        });
    }

    public ObjectProperty<FilterMovie> filterByProperty() {
        return filterBy;
    }

    public void getAllDirectorsAsc() {
        tryExec(() -> {
            List<Person> persons = MovieRepository.getAllDirectorsAsc(user);
            Platform.runLater(() -> {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle(bundle.getString("command.execution"));
                alert.setContentText(persons.stream().map(Person::getName).distinct().collect(Collectors.joining("\n")));
                alert.showAndWait();
            });
        });
    }

    public void removeLowerThanId() {
        Platform.runLater(() -> {
            try {
                FilterMovie fieldFilter = new FilterMovie(
                        Movie.class.getDeclaredField("id"),
                        Movie.class,
                        null
                );
                Optional<Integer> id = new DialogCommandArg<Integer>(bundle, fieldFilter).showAndWait();
                if (!id.isPresent())
                    return;
                tryExec(() -> {
                    MovieRepository.removeLowerThanId(user, id.get());
                    restartLoadingMovies();
                    Platform.runLater(() -> {
                        Alert alert = new Alert(Alert.AlertType.INFORMATION);
                        alert.setTitle(bundle.getString("command.execution"));
                        alert.setContentText(bundle.getString("command.execution.success"));
                        alert.showAndWait();
                    });
                });
            } catch (NoSuchFieldException e) {
                e.printStackTrace(System.err);
                errorAlert(bundle.getString("error.client.internal"));
            }
        });
    }

    public void clearUserMovies() {
        tryExec(() -> {
            MovieRepository.clearUserMovies(user);
            restartLoadingMovies();
            Platform.runLater(() -> {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle(bundle.getString("command.execution"));
                alert.setContentText(bundle.getString("command.execution.success"));
                alert.showAndWait();
            });
        });
    }

    public void getAvgOscarsCount() {
        tryExec(() -> {
            double avg = MovieRepository.getAvgOscarsCount(user);
            Platform.runLater(() -> {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle(bundle.getString("command.execution"));
                alert.setContentText(LocalFormatter.formatNumber(avg, 2));
                alert.showAndWait();
            });
        });
    }

    public void getAllBudgetsSum() {
        tryExec(() -> {
            long count = MovieRepository.getAllBudgetsSum(user);
            Platform.runLater(() -> {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle(bundle.getString("command.execution"));
                alert.setContentText(LocalFormatter.formatMoney(count));
                alert.showAndWait();
            });
        });
    }

    public void getCountByCountry() {
        Platform.runLater(() -> {
            Optional<Country> country = new DialogCommandArg<>(bundle, Country.values()).showAndWait();
            if (!country.isPresent())
                return;
            tryExec(() -> {
                long count = MovieRepository.getCountByCountry(user, country.get());
                Platform.runLater(() -> {
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setTitle(bundle.getString("command.execution"));
                    alert.setContentText(String.valueOf(count));
                    alert.showAndWait();
                });
            });
        });

    }

    public void getCountByYear() {
        Platform.runLater(() -> {
            try {
                FilterMovie fieldFilter = new FilterMovie(
                        Movie.class.getDeclaredField("productionYear"),
                        Movie.class,
                        null
                );
                Optional<Integer> year = new DialogCommandArg<Integer>(bundle, fieldFilter).showAndWait();
                if (!year.isPresent())
                    return;
                tryExec(() -> {
                    long count = MovieRepository.getCountByYear(user, year.get());
                    Platform.runLater(() -> {
                        Alert alert = new Alert(Alert.AlertType.INFORMATION);
                        alert.setTitle(bundle.getString("command.execution"));
                        alert.setContentText(String.valueOf(count));
                        alert.showAndWait();
                    });
                });
            } catch (NoSuchFieldException e) {
                e.printStackTrace(System.err);
                errorAlert(bundle.getString("error.client.internal"));
            }
        });
    }

    public void getCountLowerBudget() {
        Platform.runLater(() -> {
            try {
                FilterMovie fieldFilter = new FilterMovie(
                        Movie.class.getDeclaredField("budget"),
                        Movie.class,
                        null
                );
                Optional<Long> budget = new DialogCommandArg<Long>(bundle, fieldFilter).showAndWait();
                if (!budget.isPresent())
                    return;
                tryExec(() -> {
                    long count = MovieRepository.getCountLowerBudget(user, budget.get());
                    Platform.runLater(() -> {
                        Alert alert = new Alert(Alert.AlertType.INFORMATION);
                        alert.setTitle(bundle.getString("command.execution"));
                        alert.setContentText(String.valueOf(count));
                        alert.showAndWait();
                    });
                });
            } catch (NoSuchFieldException e) {
                e.printStackTrace(System.err);
                errorAlert(bundle.getString("error.client.internal"));
            }
        });
    }

    public void getCollectionInfo() {
        tryExec(() -> {
            String info = MovieRepository.getCollectionInfo(user);
            Platform.runLater(() -> {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle(bundle.getString("command.execution"));
                alert.setContentText(info);
                alert.showAndWait();
            });
        });
    }
}
