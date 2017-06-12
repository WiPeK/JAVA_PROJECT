package pl.wipek.client.admin;

import javafx.application.Platform;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import pl.wipek.client.Controller;
import pl.wipek.client.admin.dialogs.EditCarriedDialogController;
import pl.wipek.common.Action;
import pl.wipek.db.CarriedSubjectsNH;
import pl.wipek.server.db.CarriedSubjects;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Set;

/**
 * @author Krszysztof Adamczyk on 30.05.2017.
 * Managing action after click on "Zarzadzaj Nauczanymi Przedmiotami" on left bar
 */
public class AdminCarriedController {

    /**
     * @see AdminsController
     */
    private AdminsController adminsController;

    /**
     * @see TableView
     */
    private TableView<CarriedSubjectsNH> carriedSubjectsNHTableView;

    /**
     * Contains path to fxml file with view
     */
    private final static String carriedYearsEditFXMLPath = "/views/carried.fxml";

    /**
     * @see ObservableList
     * Contains CarriedSubjectNH items
     */
    private ObservableList<CarriedSubjectsNH> carriedSubjectsNHObservableList = FXCollections.observableArrayList();

    /**
     * @see EditCarriedDialogController
     */
    private EditCarriedDialogController editCarriedDialogController;

    public AdminCarriedController(AdminsController adminsController) {
        this.adminsController = adminsController;
    }

    /**
     * Event on buttonManageCarried button click
     * Setting up center of Controller rootBorderPane
     * @param event ActionEvent button click
     */
    @FXML
    public void buttonManageCarriedAction(ActionEvent event) {
        ScrollPane scrollPane = new ScrollPane();
        VBox vBox = new VBox();
        vBox.setMinWidth(754);
        Label title = new Label("Nauczane przedmioty");

        this.carriedSubjectsNHTableView = this.getTable();
        this.carriedSubjectsNHTableView.setPrefHeight(530);

        Button newSubject = new Button("Dodaj przedmiot nauczycielowi");
        newSubject.setOnAction(ae -> this.carriedSubjectsTableRowClick(new CarriedSubjectsNH()));

        vBox.getChildren().addAll(title, newSubject, this.carriedSubjectsNHTableView);
        scrollPane.setContent(vBox);
        this.adminsController.getController().getRootBorderPane().setCenter(scrollPane);
    }

    /**
     * Creating table with CarriedSubjectsNH objects
     * @return TableView
     */
    private TableView<CarriedSubjectsNH> getTable() {
        TableView<CarriedSubjectsNH> csTableView = new TableView<>();
        csTableView.setEditable(true);
        Set<Object> csObjects = this.adminsController.getController().getRelationHelper().getAllAsSet(new Action("getAllCarriedSubjects", "FROM CarriedSubjects cs"));
        csObjects.forEach(i -> this.carriedSubjectsNHObservableList.add(new CarriedSubjectsNH((CarriedSubjects)i)));

        TableColumn<CarriedSubjectsNH, Integer> idCol = new TableColumn<>("ID");
        idCol.setCellValueFactory(new PropertyValueFactory<>("idCarriedSubject"));
        idCol.setMinWidth(50);
        idCol.setMinWidth(75);

        TableColumn<CarriedSubjectsNH, String> teacherNameSurnameCol = new TableColumn<>("Nauczyciel");
        teacherNameSurnameCol.setCellValueFactory(c -> new ReadOnlyStringWrapper(c.getValue().getTeacher().getUser().getSurname() + " " + c.getValue().getTeacher().getUser().getName()));
        teacherNameSurnameCol.setMinWidth(75);
        teacherNameSurnameCol.setMinWidth(170);

        TableColumn<CarriedSubjectsNH, String> classCol = new TableColumn<>("Klasa");
        classCol.setCellValueFactory(c -> new ReadOnlyStringWrapper(c.getValue().getClasses().getName()));
        classCol.setMinWidth(50);
        classCol.setMaxWidth(75);

        TableColumn<CarriedSubjectsNH, String> semesterCol = new TableColumn<>("Semestr");
        DateFormat date = new SimpleDateFormat("dd/MM/yyyy");
        semesterCol.setCellValueFactory(c -> new ReadOnlyStringWrapper(date.format(c.getValue().getSemester().getStartDate())  + " " + date.format(c.getValue().getSemester().getEndDate()) + " Rok " + c.getValue().getSemester().getSchoolYear().getName()));
        semesterCol.setMinWidth(75);
        semesterCol.setMinWidth(170);

        TableColumn<CarriedSubjectsNH, String> subjectCol = new TableColumn<>("Przedmiot");
        subjectCol.setCellValueFactory(c -> new ReadOnlyStringWrapper(c.getValue().getSubject().getName()));
        subjectCol.setMinWidth(75);
        subjectCol.setMinWidth(170);

        csTableView.setRowFactory(c -> {
            TableRow<CarriedSubjectsNH> row = new TableRow<>();
            row.setOnMouseClicked(e -> {
                if(e.getButton() == MouseButton.PRIMARY && e.getClickCount() == 2) {
                    this.carriedSubjectsTableRowClick(row.getItem());
                }
            });
            return row;
        });

        csTableView.getColumns().addAll(idCol, teacherNameSurnameCol, classCol, semesterCol, subjectCol);
        csTableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        csTableView.setItems(this.carriedSubjectsNHObservableList);
        return csTableView;
    }

    /**
     * Event on carriedSubjectsNHTableView row click
     * @param item CarriedSubjectNH from table row
     */
    @FXML
    private void carriedSubjectsTableRowClick(CarriedSubjectsNH item) {
        try{
            FXMLLoader loader = new FXMLLoader(getClass().getResource(carriedYearsEditFXMLPath));
            this.editCarriedDialogController = new EditCarriedDialogController(item, this);
            loader.setController(this.editCarriedDialogController);
            Stage stage = new Stage();
            stage.setScene(new Scene(loader.load()));
            stage.addEventHandler(WindowEvent.WINDOW_SHOWN, (e) -> Platform.runLater(editCarriedDialogController::handleWindowShownEvent));
            stage.show();
        }catch (Exception e) {
            Controller.getLogger().error(e);
            e.printStackTrace();
        }
    }

    /**
     * @see Controller
     * Return Controller Object
     * @return Controller
     */
    public Controller getController() {
        return this.adminsController.getController();
    }

    /**
     * Return carriedSubjectsNHTableView object
     * @return TableView
     */
    public TableView<CarriedSubjectsNH> getCarriedSubjectsNHTableView() {
        return carriedSubjectsNHTableView;
    }

    /**
     * Return Observable list with table items
     * @return ObservableList
     */
    public ObservableList<CarriedSubjectsNH> getCarriedSubjectsNHObservableList() {
        return carriedSubjectsNHObservableList;
    }
}
