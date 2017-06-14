package pl.wipek.client.admin;

import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.beans.property.SimpleObjectProperty;
import javafx.event.ActionEvent;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.VBox;
import pl.wipek.client.admin.dialogs.EditClassesDialogController;
import pl.wipek.common.Action;
import pl.wipek.db.ClassesNH;
import pl.wipek.server.db.Classes;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Set;

/**
 * @author Krzysztof Adamczyk on 30.05.2017.
 * Managing event after click on Managing class button
 */
public final class AdminClassesController extends AdminsAbstractController<ClassesNH> {

    public AdminClassesController(AdminsController adminsController) {
        super(adminsController);
        this.fxmlPath = "/views/classesEdit.fxml";
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
        Label title = new Label("Klasy");

        this.tableView = this.getTable();
        this.tableView.setPrefHeight(530);

        Button newSubject = new Button("Dodaj nową klasę");
        newSubject.setOnAction(ae -> {
            this.setDialogAbstractController(new EditClassesDialogController(null, this));
            this.tableRowClickAction(new ClassesNH());
        });

        vBox.getChildren().addAll(title, newSubject, this.tableView);
        scrollPane.setContent(vBox);
        this.adminsController.getController().getRootBorderPane().setCenter(scrollPane);
    }

    /**
     * Creating table with ClassesNH objects
     * @return TableView
     */
    protected TableView<ClassesNH> getTable() {
        TableView<ClassesNH> classesTableView = new TableView<>();
        classesTableView.setEditable(true);

        Set<Object> classesObjects = this.adminsController.getController().getRelationHelper().getAllAsSet(new Action("getAllClasses", "FROM Classes cl"));
        for (Object i : classesObjects) {
            ClassesNH tmp = new ClassesNH((Classes)i);
            tmp.setAction(new Action("getCarriedSubjectsToClasses"));
            tmp = (ClassesNH)this.adminsController.getController().getRelationHelper().getRelated(tmp);
            tmp.setAction(new Action("getStudentsClassesToClasses"));
            tmp = (ClassesNH)this.adminsController.getController().getRelationHelper().getRelated(tmp);
            this.observableList.add(tmp);
        }

        TableColumn<ClassesNH, Integer> idCol = new TableColumn<>("ID");
        idCol.setCellValueFactory(new PropertyValueFactory<>("idClass"));
        idCol.setMinWidth(50);
        idCol.setMaxWidth(75);

        TableColumn<ClassesNH, String> nameCol = new TableColumn<>("Nazwa");
        nameCol.setCellValueFactory(new PropertyValueFactory<>("name"));
        idCol.setMinWidth(50);
        idCol.setMaxWidth(75);

        TableColumn<ClassesNH, String> semCol = new TableColumn<>("Semestr");
        DateFormat date = new SimpleDateFormat("dd/MM/yyyy");
        semCol.setCellValueFactory(c -> new ReadOnlyStringWrapper(date.format(c.getValue().getSemester().getStartDate())  + " " + date.format(c.getValue().getSemester().getEndDate()) + " Rok " + c.getValue().getSemester().getSchoolYear().getName()));
        semCol.setMinWidth(75);
        semCol.setMinWidth(170);

        TableColumn<ClassesNH, Integer> studentsCol = new TableColumn<>("Ilość uczniów");
        studentsCol.setCellValueFactory(p -> new SimpleObjectProperty<>(p.getValue().getStudentsClasses().size()));
        studentsCol.setMinWidth(75);
        studentsCol.setMaxWidth(200);

        TableColumn<ClassesNH, Integer> carriedCol = new TableColumn<>("Ilość przedmiotów");
        carriedCol.setCellValueFactory(p -> new SimpleObjectProperty<>(p.getValue().getCarriedSubjects().size()));
        carriedCol.setMinWidth(75);
        carriedCol.setMaxWidth(200);

        classesTableView.setRowFactory(c -> {
            TableRow<ClassesNH> row = new TableRow<>();
            row.setOnMouseClicked(e -> {
                if(e.getButton() == MouseButton.PRIMARY && e.getClickCount() == 2) {
                    this.setDialogAbstractController(new EditClassesDialogController(row.getItem(), this));
                    this.tableRowClickAction(row.getItem());
                }
            });
            return row;
        });

        classesTableView.getColumns().addAll(idCol, nameCol, semCol, studentsCol, carriedCol);
        classesTableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        classesTableView.setItems(this.observableList);

        return classesTableView;
    }
}
