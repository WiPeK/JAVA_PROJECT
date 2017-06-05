package pl.wipek.client.admin;

import javafx.application.Platform;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.beans.property.SimpleObjectProperty;
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
import pl.wipek.client.admin.dialogs.EditClassesDialogController;
import pl.wipek.common.Action;
import pl.wipek.db.ClassesNH;
import pl.wipek.server.db.Classes;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Set;

/**
 * Created by Krszysztof Adamczyk on 30.05.2017.
 */
public class AdminClassesController {

    private AdminsController adminsController;

    private TableView<ClassesNH> classesManageTableView;

    private ObservableList<ClassesNH> classesNHObservableList = FXCollections.observableArrayList();

    private EditClassesDialogController editClassesDialogController;

    private final static String classesEditFXMLPath = "/views/classesEdit.fxml";

    public AdminClassesController(AdminsController adminsController) {
        this.adminsController = adminsController;
    }

    @FXML
    void buttonManageClassesAction(ActionEvent event) {
        ScrollPane scrollPane = new ScrollPane();
        VBox vBox = new VBox();
        vBox.setMinWidth(754);
        Label title = new Label("Klasy");

        this.classesManageTableView = this.getTable();
        this.classesManageTableView.setPrefHeight(530);

        Button newSubject = new Button("Dodaj nową klasę");
        newSubject.setOnAction(ae -> this.classesTableRowClick(new ClassesNH()));

        vBox.getChildren().addAll(title, newSubject, this.classesManageTableView);
        scrollPane.setContent(vBox);
        this.adminsController.getController().getRootBorderPane().setCenter(scrollPane);
    }

    private TableView<ClassesNH> getTable() {
        TableView<ClassesNH> classesTableView = new TableView<>();
        classesTableView.setEditable(true);

        Set<Object> classesObjects = this.adminsController.getController().getRelationHelper().getAllAsSet(new Action("getAllClasses", "FROM Classes cl"));
        for (Object i : classesObjects) {
            ClassesNH tmp = new ClassesNH((Classes)i);
            tmp.setAction(new Action("getCarriedSubjectsToClasses"));
            tmp = (ClassesNH)this.adminsController.getController().getRelationHelper().getRelated(tmp);
            tmp.setAction(new Action("getStudentsClassesToClasses"));
            tmp = (ClassesNH)this.adminsController.getController().getRelationHelper().getRelated(tmp);
            this.classesNHObservableList.add(tmp);
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
                    this.classesTableRowClick(row.getItem());
                }
            });
            return row;
        });

        classesTableView.getColumns().addAll(idCol, nameCol, semCol, studentsCol, carriedCol);
        classesTableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        classesTableView.setItems(this.classesNHObservableList);

        return classesTableView;
    }

    private void classesTableRowClick(ClassesNH item) {
        try{
            FXMLLoader loader = new FXMLLoader(getClass().getResource(classesEditFXMLPath));
            this.editClassesDialogController = new EditClassesDialogController(item, this);
            loader.setController(this.editClassesDialogController);
            Stage stage = new Stage();
            stage.setScene(new Scene(loader.load()));
            stage.addEventHandler(WindowEvent.WINDOW_SHOWN, (e) -> Platform.runLater(editClassesDialogController::handleWindowShownEvent));
            stage.show();
        }catch (Exception e) {
            Controller.getLogger().error(e);
            e.printStackTrace();
        }
    }

    public Controller getController() {
        return this.adminsController.getController();
    }

    public TableView<ClassesNH> getClassesManageTableView() {
        return classesManageTableView;
    }

    public ObservableList<ClassesNH> getClassesNHObservableList() {
        return classesNHObservableList;
    }
}
