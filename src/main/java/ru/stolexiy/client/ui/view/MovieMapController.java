package ru.stolexiy.client.ui.view;

import javafx.application.Application;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.chart.BubbleChart;
import javafx.scene.chart.ScatterChart;
import javafx.scene.chart.XYChart;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import javafx.util.Pair;
import ru.stolexiy.data.Country;

import java.net.URL;
import java.util.Arrays;
import java.util.Objects;
import java.util.ResourceBundle;

public class MovieMapController extends Application implements Initializable {
    @FXML
    public BubbleChart<Number, Number> bubbleChart;
    @FXML
    public ScatterChart<Number, Number> scatterChart;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
//        bubbleChart.setX
        Pair<Country, XYChart.Data<Number, Number>>[] countries = new Pair[] {
            new Pair(Country.USA, new XYChart.Data<>(339, 512, 121)),
            new Pair(Country.CANADA, new XYChart.Data<>(428, 729, 72)),
//            new Pair(Country.NEW_ZEALAND, new XYChart.Data<>(1711, 247, 40)),
            new Pair(Country.SPAIN, new XYChart.Data<>(800, 542, 47)),
            new Pair(Country.RUSSIA, new XYChart.Data<>(1400, 726, 147))
        };
        Arrays.stream(countries).peek(it -> {
            Number x = it.getValue().getXValue();
            Number y = it.getValue().getYValue();
//            it.getValue().setXValue(x.doubleValue() / 1920 * 50);
//            it.getValue().setYValue(y.doubleValue() / 1000 * 80);
        }).map(it -> createCountry(it.getKey(), it.getValue())).forEach(bubbleChart.getData()::add);
    }

    private XYChart.Series<Number, Number> createCountry(Country country, XYChart.Data<Number, Number> data) {
        XYChart.Series<Number, Number> series = new XYChart.Series<>();
        series.setName(country.name());
        series.getData().add(data);
        return series;
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        ResourceBundle bundle = ResourceLoader.loadResourceBundle();
        Pane root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/layouts/movie-map.fxml")), bundle);
        primaryStage.setScene(new Scene(root));
        primaryStage.centerOnScreen();
        primaryStage.setMaximized(true);
        primaryStage.setTitle(bundle.getString("title"));
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
