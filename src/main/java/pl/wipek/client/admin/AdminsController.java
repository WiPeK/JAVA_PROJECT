package pl.wipek.client.admin;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import pl.wipek.client.Controller;

/**
 * @author  Created by Krszysztof Adamczyk on 14.05.2017.
 */
public class AdminsController {

    private Controller controller;

    private AdminUsersController adminUsersController;

    private AdminClassifiedsController adminClassifiedsController;

    private AdminSubstitutesController adminSubstitutesController;

    private AdminSubjectsController adminSubjectsController;

    private AdminSchoolYearsController adminSchoolYearsController;

    private AdminCarriedController adminCarriedController;

    private AdminClassesController adminClassesController;

    public AdminsController(Controller controller) throws Exception {
        this.controller = controller;
    }

    @FXML
    public void leftBarForAdmin() throws Exception {
        VBox vBox = new VBox();
        Button buttonManageUsers = new Button("Zarządzaj użytkownikami");
        buttonManageUsers.getStyleClass().add("classbutton");
        buttonManageUsers.setOnAction(this::buttonManageUsersAction);
        buttonManageUsers.setPrefWidth(270);
        buttonManageUsers.setPrefHeight(40);

        Button buttonManageClassifieds = new Button("Zarządzaj ogłoszeniami");
        buttonManageClassifieds.getStyleClass().add("classbutton");
        buttonManageClassifieds.setOnAction(this::buttonManageClassifiedsAction);
        buttonManageClassifieds.setPrefWidth(270);
        buttonManageClassifieds.setPrefHeight(40);

        Button buttonManageSubstitutes = new Button("Zarządzaj zastępstwami");
        buttonManageSubstitutes.getStyleClass().add("classbutton");
        buttonManageSubstitutes.setOnAction(this::buttonManageSubstitutesAction);
        buttonManageSubstitutes.setPrefWidth(270);
        buttonManageSubstitutes.setPrefHeight(40);

        Button buttonManageSchoolYears = new Button("Zarządzaj rocznikami");
        buttonManageSchoolYears.getStyleClass().add("classbutton");
        buttonManageSchoolYears.setOnAction(this::buttonManageSchoolYearsAction);
        buttonManageSchoolYears.setPrefWidth(270);
        buttonManageSchoolYears.setPrefHeight(40);

        Button buttonManageSubjects = new Button("Zarządzaj przedmiotami");
        buttonManageSubjects.getStyleClass().add("classbutton");
        buttonManageSubjects.setOnAction(this::buttonManageSubjectsAction);
        buttonManageSubjects.setPrefWidth(270);
        buttonManageSubjects.setPrefHeight(40);

        Button buttonManageClasses = new Button("Zarządzaj klasami");
        buttonManageClasses.getStyleClass().add("classbutton");
        buttonManageClasses.setOnAction(this::buttonManageClassesAction);
        buttonManageClasses.setPrefWidth(270);
        buttonManageClasses.setPrefHeight(40);

        Button buttonManageCarried = new Button("Zarządzaj nauczanymi przedmiotami");
        buttonManageCarried.getStyleClass().add("classbutton");
        buttonManageCarried.setOnAction(this::buttonManageCarriedAction);
        buttonManageCarried.setPrefWidth(270);
        buttonManageCarried.setPrefHeight(40);

        Button buttonManageGrades = new Button("Oceny");
        buttonManageGrades.getStyleClass().add("classbutton");
        buttonManageGrades.setOnAction(this::buttonManageGradesAction);
        buttonManageGrades.setPrefWidth(270);
        buttonManageGrades.setPrefHeight(40);

        Button buttonStatistics = new Button("Statystyki");
        buttonStatistics.getStyleClass().add("classbutton");
        buttonStatistics.setOnAction(this::buttonStatisticsAction);
        buttonStatistics.setPrefWidth(270);
        buttonStatistics.setPrefHeight(40);

        vBox.getChildren().addAll(buttonManageUsers, buttonManageClassifieds, buttonManageSubstitutes, buttonManageSchoolYears, buttonManageSubjects, buttonManageClasses, buttonManageCarried, buttonManageGrades, buttonStatistics);
        this.controller.getLeftBar().getLeftBarVBox().getChildren().add(vBox);
    }

    private void buttonManageGradesAction(ActionEvent event) {

    }

    private void buttonManageCarriedAction(ActionEvent event) {
        this.adminCarriedController = new AdminCarriedController(this);
        this.adminCarriedController.buttonManageCarriedAction(event);
    }

    @FXML
    private void buttonManageUsersAction(ActionEvent event){
        this.adminUsersController = new AdminUsersController(this);
        this.adminUsersController.buttonManageUsersAction(event);
    }

    @FXML
    private void buttonManageSubstitutesAction(ActionEvent event) {
        this.adminSubstitutesController = new AdminSubstitutesController(this);
        this.adminSubstitutesController.buttonManageSubstitutesAction(event);
    }

    @FXML
    private void buttonManageClassifiedsAction(ActionEvent event) {
        this.adminClassifiedsController = new AdminClassifiedsController(this);
        this.adminClassifiedsController.buttonManageClassifiedsAction(event);
    }

    @FXML
    private void buttonManageSchoolYearsAction(ActionEvent event) {
        this.adminSchoolYearsController = new AdminSchoolYearsController(this);
        this.adminSchoolYearsController.buttonManageSchoolYears(event);
    }

    @FXML
    private void buttonManageSubjectsAction(ActionEvent event) {
        this.adminSubjectsController = new AdminSubjectsController(this);
        this.adminSubjectsController.buttonManageSubjectsAction(event);
    }

    @FXML
    private void buttonManageClassesAction(ActionEvent event) {
        this.adminClassesController = new AdminClassesController(this);
        this.adminClassesController.buttonManageClassesAction(event);
    }

    @FXML
    private void buttonStatisticsAction(ActionEvent actionEvent) {

    }

    public Controller getController() {
        return controller;
    }
}
