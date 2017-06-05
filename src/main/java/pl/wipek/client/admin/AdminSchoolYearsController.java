package pl.wipek.client.admin;

import javafx.application.Platform;
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
import pl.wipek.client.admin.dialogs.EditSchoolYearsDialogController;
import pl.wipek.common.Action;
import pl.wipek.db.SchoolYearsNH;
import pl.wipek.server.db.SchoolYears;

import java.util.Date;
import java.util.Set;

/**
 * Created by Krszysztof Adamczyk on 28.05.2017.
 */
public class AdminSchoolYearsController {

    private AdminsController adminsController;

    private TableView<SchoolYearsNH> schYrsManageTableView;

    private ObservableList<SchoolYearsNH> schYrsNHObservableList = FXCollections.observableArrayList();

    private final static String schoolYearsEditFXMLPath = "/views/schYrsEdit.fxml";

    private EditSchoolYearsDialogController editSchoolYearsDialogController;

    AdminSchoolYearsController(AdminsController adminsController) {
        this.adminsController = adminsController;
    }

    @FXML
    public void buttonManageSchoolYears(ActionEvent event) {
        ScrollPane scrollPane = new ScrollPane();
        VBox vBox = new VBox();
        vBox.setMinWidth(754);
        Label title = new Label("Lata szkolne");

        this.schYrsManageTableView = this.getTable();
        this.schYrsManageTableView.setPrefHeight(530);

        Button newSubject = new Button("Dodaj nowy rocznik");
        newSubject.setOnAction(ae -> this.schYrsTableRowClick(new SchoolYearsNH()));

        vBox.getChildren().addAll(title, newSubject, this.schYrsManageTableView);
        scrollPane.setContent(vBox);
        this.adminsController.getController().getRootBorderPane().setCenter(scrollPane);
    }

    private TableView<SchoolYearsNH> getTable() {
        TableView<SchoolYearsNH> schYrsTableView = new TableView<>();
        schYrsTableView.setEditable(true);

        this.schYrsNHObservableList.removeAll(this.schYrsNHObservableList);

        Set<Object> schYrsObjects = this.adminsController.getController().getRelationHelper().getAllAsSet(new Action("getAllSchoolYears", "FROM SchoolYears sy"));
        for (Object schYrsObject : schYrsObjects) {
            SchoolYearsNH tmp = new SchoolYearsNH((SchoolYears)schYrsObject);
            tmp.setAction(new Action("getSemestersToYear"));
            tmp = (SchoolYearsNH) this.adminsController.getController().getRelationHelper().getRelated(tmp);
            this.schYrsNHObservableList.add(tmp);
        }

        TableColumn<SchoolYearsNH, Integer> idColumn = new TableColumn<>("ID");
        idColumn.setCellValueFactory(new PropertyValueFactory<>("idSchoolYear"));
        idColumn.setMinWidth(50);
        idColumn.setMaxWidth(75);

        TableColumn<SchoolYearsNH, String> nameCol = new TableColumn<>("Nazwa");
        nameCol.setCellValueFactory(new PropertyValueFactory<>("name"));
        nameCol.setMinWidth(100);
        nameCol.setMaxWidth(125);

        TableColumn<SchoolYearsNH, Date> startCol = new TableColumn<>("Data rozpoczęcia");
        startCol.setCellValueFactory(new PropertyValueFactory<>("startDate"));
        startCol.setMinWidth(100);
        startCol.setMaxWidth(125);

        TableColumn<SchoolYearsNH, Date> endCol = new TableColumn<>("Data zakończenia");
        endCol.setCellValueFactory(new PropertyValueFactory<>("endDate"));
        endCol.setMinWidth(100);
        endCol.setMaxWidth(125);

        TableColumn<SchoolYearsNH, Integer> semCol = new TableColumn<>("Liczba semestrów");
        semCol.setCellValueFactory(p -> new SimpleObjectProperty<>(p.getValue().getSemesters().size()));
        semCol.setMinWidth(50);
        semCol.setMaxWidth(75);

        schYrsTableView.setRowFactory(c -> {
            TableRow<SchoolYearsNH> row = new TableRow<>();
            row.setOnMouseClicked(e -> {
                if(e.getButton() == MouseButton.PRIMARY && e.getClickCount() == 2) {
                    this.schYrsTableRowClick(row.getItem());
                }
            });
            return row;
        });

        schYrsTableView.getColumns().addAll(idColumn, nameCol, startCol, endCol, semCol);
        schYrsTableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        schYrsTableView.setItems(this.schYrsNHObservableList);
        return schYrsTableView;
    }

    @FXML
    private void schYrsTableRowClick(SchoolYearsNH item) {
        try{
            FXMLLoader loader = new FXMLLoader(getClass().getResource(schoolYearsEditFXMLPath));
            this.editSchoolYearsDialogController = new EditSchoolYearsDialogController(item, this);
            loader.setController(this.editSchoolYearsDialogController);
            Stage stage = new Stage();
            stage.setScene(new Scene(loader.load()));
            stage.addEventHandler(WindowEvent.WINDOW_SHOWN, (e) -> Platform.runLater(editSchoolYearsDialogController::handleWindowShownEvent));
            stage.show();
        }catch (Exception e) {
            Controller.getLogger().error(e);
            e.printStackTrace();
        }
    }

    public Controller getController() {
        return this.adminsController.getController();
    }

    public TableView<SchoolYearsNH> getSchYrsManageTableView() {
        return schYrsManageTableView;
    }

    public ObservableList<SchoolYearsNH> getSchYrsNHObservableList() {
        return schYrsNHObservableList;
    }
}
