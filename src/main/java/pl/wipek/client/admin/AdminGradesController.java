package pl.wipek.client.admin;

import javafx.event.ActionEvent;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.VBox;
import pl.wipek.client.Controller;
import pl.wipek.client.admin.dialogs.StudentsGradesDialog;
import pl.wipek.common.Action;
import pl.wipek.db.UsersNH;
import pl.wipek.server.db.Users;

import java.util.Set;

/**
 * @author Krzysztof Adamczyk on 06.06.2017.
 * Managing event after click on Grades button
 */
public final class AdminGradesController extends AdminsAbstractController<UsersNH> {

    public AdminGradesController(AdminsController adminsController) {
        super(adminsController);
        this.fxmlPath = "/views/studentGrades.fxml";
    }

    /**
     * Event on manage button click
     * Setting up center of Controller rootBorderPane
     *
     * @param event ActionEvent button click
     */
    @Override
    public void manageButtonAction(ActionEvent event) {
        try {
            ScrollPane scrollPane = new ScrollPane();
            VBox vBox = new VBox();
            vBox.setMinWidth(754);
            Label title = new Label("Oceny");

            this.tableView = this.getTable();
            this.tableView.setPrefHeight(630);

            vBox.getChildren().addAll(title, tableView);
            scrollPane.setContent(vBox);
            this.adminsController.getController().getRootBorderPane().setCenter(scrollPane);
        }catch (Exception e) {
            Controller.getLogger().error(e);
            e.printStackTrace();
        }
    }

    /**
     * Creating table with UsersNH objects
     * @return TableView
     */
    protected TableView<UsersNH> getTable() {
        TableView<UsersNH> table = new TableView<>();
        Set<Object> usersObjects = this.adminsController.getController().getRelationHelper().getAllAsSet(new Action("getAllUsers", "FROM Users u"));
        usersObjects.forEach(i -> {
            if(((Users)i).getStudent() != null) {
                this.observableList.add(new UsersNH((Users)i));
            }
        });

        TableColumn<UsersNH, Integer> idCol = new TableColumn<>("ID");
        idCol.setCellValueFactory(new PropertyValueFactory<>("idUser"));
        idCol.setMinWidth(50);

        TableColumn<UsersNH, String> userNameColumn = new TableColumn<>("Imię");
        userNameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        userNameColumn.setMinWidth(150);

        TableColumn<UsersNH, String> userSurnameColumn = new TableColumn<>("Nazwisko");
        userSurnameColumn.setCellValueFactory(new PropertyValueFactory<>("surname"));
        userSurnameColumn.setMinWidth(150);

        TableColumn<UsersNH, String> userPeselColumn = new TableColumn<>("Pesel");
        userPeselColumn.setCellValueFactory(new PropertyValueFactory<>("pesel"));
        userPeselColumn.setMinWidth(150);
        
        table.setRowFactory(c -> {
            TableRow<UsersNH> row = new TableRow<>();
            row.setOnMouseClicked(e -> {
                if(e.getButton() == MouseButton.PRIMARY && e.getClickCount() == 2) {
                    this.setDialogAbstractController(new StudentsGradesDialog(row.getItem(), this));
                    this.tableRowClickAction(row.getItem());
                }
            });
            return row;
        });

        table.getColumns().addAll(idCol, userNameColumn, userSurnameColumn, userPeselColumn);
        table.setItems(this.observableList);
        return table;
    }

}
