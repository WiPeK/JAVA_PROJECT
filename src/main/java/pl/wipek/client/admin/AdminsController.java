package pl.wipek.client.admin;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import pl.wipek.client.Controller;

/**
 * @author  Created by Krszysztof Adamczyk on 14.05.2017.
 * Managing left bar action for admin logging and clicking on buttons
 */
public class AdminsController {

    /**
     * @see Controller
     */
    private Controller controller;

    private AdminsAbstractController adminsAbstractController;

    /**
     * @see AdminUsersController
     */
    private AdminUsersController adminUsersController;

    /**
     * @see AdminClassifiedsController
     */
    private AdminClassifiedsController adminClassifiedsController;

    /**
     * @see AdminSubstitutesController
     */
    private AdminSubstitutesController adminSubstitutesController;

    /**
     * @see AdminSubjectsController
     */
    private AdminSubjectsController adminSubjectsController;

    /**
     * @see AdminSchoolYearsController
     */
    private AdminSchoolYearsController adminSchoolYearsController;

    /**
     * @see AdminCarriedController
     */
    private AdminCarriedController adminCarriedController;

    /**
     * @see AdminClassesController
     */
    private AdminClassesController adminClassesController;

    /**
     * @see AdminGradesController
     */
    private AdminGradesController adminGradesController;

    /**
     * @see AdminStatisticsController
     */
    private AdminStatisticsController adminStatisticsController;

    public AdminsController(Controller controller) throws Exception {
        this.controller = controller;
    }

    /**
     * Initializing left bar with buttons after admin logging
     * @throws Exception
     */
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

    /**
     * Event on buttonManageGrades click
     * @param event ActionEvent
     */
    private void buttonManageGradesAction(ActionEvent event) {
        this.adminGradesController = new AdminGradesController(this);
        this.adminGradesController.buttonStudentGradesAction(event);
    }

    /**
     * Event on buttonManageCarried click
     * @param event ActionEvent
     */
    private void buttonManageCarriedAction(ActionEvent event) {
        this.adminCarriedController = new AdminCarriedController(this);
        this.adminCarriedController.buttonManageCarriedAction(event);
    }

    /**
     * Event on buttonManageUsers click
     * @param event ActionEvent
     */
    private void buttonManageUsersAction(ActionEvent event){
        this.adminsAbstractController = new AdminUsersController(this);
        this.adminsAbstractController.manageButtonAction(event);
    }

    /**
     * Event on buttonManageSubstitutes click
     * @param event ActionEvent
     */
    private void buttonManageSubstitutesAction(ActionEvent event) {
        this.adminsAbstractController = new AdminSubstitutesController(this);
        this.adminsAbstractController.manageButtonAction(event);
    }

    /**
     * Event on buttonManageClassifieds click
     * @param event ActionEvent
     */
    private void buttonManageClassifiedsAction(ActionEvent event) {
        this.adminClassifiedsController = new AdminClassifiedsController(this);
        this.adminClassifiedsController.buttonManageClassifiedsAction(event);
    }

    /**
     * Event on buttonManageSchoolYears click
     * @param event ActionEvent
     */
    private void buttonManageSchoolYearsAction(ActionEvent event) {
        this.adminSchoolYearsController = new AdminSchoolYearsController(this);
        this.adminSchoolYearsController.buttonManageSchoolYears(event);
    }

    /**
     * Event on buttonManageSubjects click
     * @param event ActionEvent
     */
    private void buttonManageSubjectsAction(ActionEvent event) {
        this.adminSubjectsController = new AdminSubjectsController(this);
        this.adminSubjectsController.buttonManageSubjectsAction(event);
    }

    /**
     * Event on buttonManageClasses click
     * @param event ActionEvent
     */
    private void buttonManageClassesAction(ActionEvent event) {
        this.adminClassesController = new AdminClassesController(this);
        this.adminClassesController.buttonManageClassesAction(event);
    }

    /**
     * Event on buttonStatistics click
     * @param event ActionEvent
     */
    private void buttonStatisticsAction(ActionEvent event) {
        this.adminStatisticsController = new AdminStatisticsController(this);
        this.adminStatisticsController.buttonStatisticsAction(event);
    }

    /**
     * @see Controller
     * Return Controller Object
     * @return Controller
     */
    public Controller getController() {
        return controller;
    }
}
