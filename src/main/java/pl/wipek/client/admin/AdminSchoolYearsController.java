package pl.wipek.client.admin;

import javafx.beans.property.SimpleObjectProperty;
import javafx.event.ActionEvent;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.VBox;
import pl.wipek.client.admin.dialogs.EditSchoolYearsDialogController;
import pl.wipek.common.Action;
import pl.wipek.db.SchoolYearsNH;
import pl.wipek.server.db.SchoolYears;

import java.util.Date;
import java.util.Set;

/**
 * @author Krzysztof Adamczyk on 28.05.2017.
 * Managing event after click on Managing schools years button
 */
public final class AdminSchoolYearsController extends AdminsAbstractController<SchoolYearsNH> {

    AdminSchoolYearsController(AdminsController adminsController) {
        super(adminsController);
        this.fxmlPath = "/views/schYrsEdit.fxml";
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
        Label title = new Label("Lata szkolne");

        this.tableView = this.getTable();
        this.tableView.setPrefHeight(530);

        Button newSubject = new Button("Dodaj nowy rocznik");
        newSubject.setOnAction(ae -> {
            this.setDialogAbstractController(new EditSchoolYearsDialogController(null, this));
            this.tableRowClickAction(new SchoolYearsNH());
        });

        vBox.getChildren().addAll(title, newSubject, this.tableView);
        scrollPane.setContent(vBox);
        this.adminsController.getController().getRootBorderPane().setCenter(scrollPane);
    }

    /**
     * Creating table with SchoolYearsNH objects
     * @return TableView
     */
    protected TableView<SchoolYearsNH> getTable() {
        TableView<SchoolYearsNH> schYrsTableView = new TableView<>();
        schYrsTableView.setEditable(true);

        this.observableList.clear();

        Set<Object> schYrsObjects = this.adminsController.getController().getRelationHelper().getAllAsSet(new Action("getAllSchoolYears", "FROM SchoolYears sy"));
        for (Object schYrsObject : schYrsObjects) {
            SchoolYearsNH tmp = new SchoolYearsNH((SchoolYears)schYrsObject);
            tmp.setAction(new Action("getSemestersToYear"));
            tmp = (SchoolYearsNH) this.adminsController.getController().getRelationHelper().getRelated(tmp);
            this.observableList.add(tmp);
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
                    this.setDialogAbstractController(new EditSchoolYearsDialogController(row.getItem(), this));
                    this.tableRowClickAction(row.getItem());
                }
            });
            return row;
        });

        schYrsTableView.getColumns().addAll(idColumn, nameCol, startCol, endCol, semCol);
        schYrsTableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        schYrsTableView.setItems(this.observableList);
        return schYrsTableView;
    }
}
