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
import pl.wipek.db.CarriedSubjectsNH;
import pl.wipek.db.GradesNH;
import pl.wipek.db.SchoolYearsNH;
import pl.wipek.db.SemestersNH;
import pl.wipek.server.db.CarriedSubjects;
import pl.wipek.server.db.SchoolYears;

import java.util.*;

/**
 * @author Jarosław Składanowski on 08.06.2017.
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
     *
     * @param event ActionEvent button click
     */
    @FXML
    public void buttonStatisticsAction(ActionEvent event) {
        VBox vBox = new VBox();
        Label title = new Label("Statystyki");
        ScrollPane scrollPane = new ScrollPane();
        VBox scrPnVBox = new VBox();
        scrPnVBox.getChildren().addAll(this.getAmountBar(), this.getAvgStats());
        scrollPane.setContent(scrPnVBox);
        vBox.getChildren().addAll(title, scrollPane);
        this.adminsController.getController().getRootBorderPane().setCenter(vBox);


    }

    /**
     * Creating Bar with amounts from database
     *
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
            series.getData().add(new XYChart.Data<>(this.categories.get(i), (Number) stat));
            i--;
        }
        series.setName("Ilość");
        BarChart<String, Number> barChart = new BarChart<>(xAxis, yAxis);
        barChart.setTitle("Statystyki ilościowe");
        barChart.getData().add(series);
        barChart.resize(1200, 200);
        return barChart;
    }

    private ObservableList<String> getBarChartCategories() {
        String[] names = {"Ilość użytkowników", "Ilość uczniów", "Ilość nauczycieli", "Ilość administratorów", "Ilość przedmiotów", "Ilość klas"};
        categories.addAll(Arrays.asList(names));
        return categories;
    }

    /**
     * Creating line chart with averages of grades for every year
     *
     * @return
     */
    private LineChart<String, Number> getAvgStats() {
        Set<Object> schYrsObjects = this.adminsController.getController().getRelationHelper().getAllAsSet(new Action("getAllSchoolYears", "FROM SchoolYears sy order by idSchoolYear ASC"));
        CategoryAxis xAxis = new CategoryAxis();
        NumberAxis yAxis = new NumberAxis(3.0, 5.0, 0.25);
        xAxis.setLabel("Lata");
        yAxis.setLabel("Średnia");
        LineChart<String, Number> lineChart = new LineChart<String, Number>(xAxis, yAxis);
        lineChart.setTitle("Średnie uczniów na przestrzeni lat");
        XYChart.Series series = new XYChart.Series();
        series.setName("Średnie");
        ObservableList<Number> yearsAvg = FXCollections.observableArrayList();

        for (Object schYrsObject : schYrsObjects) {
            SchoolYearsNH tmp = new SchoolYearsNH((SchoolYears) schYrsObject);
            tmp.setAction(new Action("getSemestersToYear"));
            tmp = (SchoolYearsNH) this.adminsController.getController().getRelationHelper().getRelated(tmp);
            Set<SemestersNH> sem = tmp.getSemesters();
            List<Double> semestersAvg = new ArrayList<>();
            for (SemestersNH x : sem) {
                x.setAction(new Action("getGradesToSemester"));

                SemestersNH nazwa = (SemestersNH) this.adminsController.getController().getRelationHelper().getRelated(x);
                for (CarriedSubjectsNH carriedSubjectsNH : nazwa.getCarriedSubjects()) {
                    OptionalDouble semesterAverage = carriedSubjectsNH.getGrades().parallelStream().mapToDouble(GradesNH::getEndGrade).average();
                    if(semesterAverage.isPresent()) {
                        semestersAvg.add(semesterAverage.getAsDouble());
                    }
                }
                OptionalDouble yearAverage = semestersAvg.parallelStream().mapToDouble(i->i).average();
                if (yearAverage.isPresent()) {
                    yearsAvg.add(yearAverage.getAsDouble());
                }

            }
        }
        int i = 0;
        for (Object stat : schYrsObjects) {
            series.getData().add(new XYChart.Data<>(((SchoolYears) stat).getName(),yearsAvg.get(i)));
            i++;
        }
        lineChart.getData().add(series);
        lineChart.resize(1200, 200);
        return lineChart;
    }


}

