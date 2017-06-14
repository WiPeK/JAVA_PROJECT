package pl.wipek.client.admin;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.chart.*;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.VBox;
import pl.wipek.common.Action;

import java.util.Arrays;
import java.util.Set;

/**
 * @author Krzysztof Adamczyk on 08.06.2017.
 * Showing statictics
 */
public class AdminStatisticsController {

    /**
     * @see AdminsController
     */
    private AdminsController adminsController;

    /**
     * @see ObservableList
     */
    private ObservableList<String> categories = FXCollections.observableArrayList();

    public AdminStatisticsController(AdminsController adminsController) {
        this.adminsController = adminsController;
    }

    /**
     * Event on buttonStatistics button click
     * Setting up center of Controller rootBorderPane
     * @param event ActionEvent button click
     */
    @FXML
    public void buttonStatisticsAction(ActionEvent event) {
        VBox vBox = new VBox();
        Label title = new Label("Statystyki");
        ScrollPane scrollPane = new ScrollPane();
        VBox scrPnVBox = new VBox();
        scrPnVBox.getChildren().addAll(this.getAmountBar());
        scrollPane.setContent(scrPnVBox);
        vBox.getChildren().addAll(title, scrollPane);
        this.adminsController.getController().getRootBorderPane().setCenter(vBox);
    }

    /**
     * Creating Bar with amounts from database
     * @return
     */
    private BarChart<String, Number> getAmountBar() {
        Set<Object> stats = this.adminsController.getController().getRelationHelper().getAllAsSet(new Action("getAmountStats"));
        NumberAxis yAxis = new NumberAxis(0, 1500, 10);
        CategoryAxis xAxis = new CategoryAxis();
        xAxis.setCategories(this.getBarChartCategories());
        xAxis.setTickLabelRotation(90);
        yAxis.setLabel("Ilość");

        XYChart.Series<String, Number> series = new XYChart.Series<>();
        int i = 5;
        for (Object stat : stats) {
            series.getData().add(new XYChart.Data<>(this.categories.get(i), (Number)stat));
            i--;
        }
        series.setName("Ilość");
        BarChart<String, Number> barChart = new BarChart<>(xAxis, yAxis);
        barChart.setTitle("Statystyki ilościowe");
        barChart.getData().add(series);
        barChart.resize(600, 200);
        return barChart;
    }

    private ObservableList<String> getBarChartCategories() {
        String[] names = {"Ilość użytkowników", "Ilość uczniów", "Ilość nauczycieli", "Ilość administratorów", "Ilość przedmiotów", "Ilość klas"};
        categories.addAll(Arrays.asList(names));
        return categories;
    }



}
