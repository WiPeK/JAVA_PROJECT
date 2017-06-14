package pl.wipek.client.admin;

import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.event.ActionEvent;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.VBox;
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
public final class AdminCarriedController extends AdminsAbstractController<CarriedSubjectsNH> {

    public AdminCarriedController(AdminsController adminsController) {
        super(adminsController);
        this.fxmlPath = "/views/carried.fxml";
    }

    /**
     * Event on manage button click
     * Setting up center of Controller rootBorderPane
     *
     * @param event ActionEvent button click
     */
    @Override
    public void manageButtonAction(ActionEvent event) {
        ScrollPane scrollPane = new ScrollPane();
        VBox vBox = new VBox();
        vBox.setMinWidth(754);
        Label title = new Label("Nauczane przedmioty");

        this.tableView = this.getTable();
        this.tableView.setPrefHeight(530);

        Button newSubject = new Button("Dodaj przedmiot nauczycielowi");
        newSubject.setOnAction(ae -> {
            this.setDialogAbstractController(new EditCarriedDialogController(null, this));
            this.tableRowClickAction(new CarriedSubjectsNH());
        });

        vBox.getChildren().addAll(title, newSubject, this.tableView);
        scrollPane.setContent(vBox);
        this.adminsController.getController().getRootBorderPane().setCenter(scrollPane);
    }

    /**
     * Creating table with CarriedSubjectsNH objects
     * @return TableView
     */
    protected TableView<CarriedSubjectsNH> getTable() {
        TableView<CarriedSubjectsNH> csTableView = new TableView<>();
        csTableView.setEditable(true);
        Set<Object> csObjects = this.adminsController.getController().getRelationHelper().getAllAsSet(new Action("getAllCarriedSubjects", "FROM CarriedSubjects cs"));
        csObjects.forEach(i -> this.observableList.add(new CarriedSubjectsNH((CarriedSubjects)i)));

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
                    this.setDialogAbstractController(new EditCarriedDialogController(row.getItem(), this));
                    this.tableRowClickAction(row.getItem());
                }
            });
            return row;
        });

        csTableView.getColumns().addAll(idCol, teacherNameSurnameCol, classCol, semesterCol, subjectCol);
        csTableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        csTableView.setItems(this.observableList);
        return csTableView;
    }
}
