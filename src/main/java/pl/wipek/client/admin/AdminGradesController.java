package pl.wipek.client.admin;

import javafx.application.Platform;
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
import pl.wipek.client.admin.dialogs.StudentsGradesDialog;
import pl.wipek.common.Action;
import pl.wipek.db.UsersNH;
import pl.wipek.server.db.Users;

import java.util.HashSet;
import java.util.Set;

/**
 * @author Krzysztof Adamczyk on 06.06.2017.
 * Managing event after click on Grades button
 */
public class AdminGradesController {

    /**
     * Contains path to fxml file with view
     */
    private final static String studentGradesFXMLPath = "/views/studentGrades.fxml";

    /**
     * @see AdminsController
     */
    private AdminsController adminsController;

    /**
     * @see TableView
     */
    private TableView<UsersNH> usersManageTableView;

    /**
     * @see ObservableList
     * Contains UsersNH items
     */
    private ObservableList<UsersNH> observableList = FXCollections.observableArrayList();

    /**
     * @see StudentsGradesDialog
     */
    private StudentsGradesDialog studentsGradesDialog;

    public AdminGradesController(AdminsController adminsController) {
        this.adminsController = adminsController;
    }

    /**
     * Event on buttonGrades button click
     * Setting up center of Controller rootBorderPane
     * @param event ActionEvent button click
     */
    @FXML
    public void buttonStudentGradesAction(ActionEvent event) {
        try {
            ScrollPane scrollPane = new ScrollPane();
            VBox vBox = new VBox();
            vBox.setMinWidth(754);
            Label title = new Label("Oceny");

            this.usersManageTableView = this.getTable();
            this.usersManageTableView.setPrefHeight(630);

            vBox.getChildren().addAll(title, usersManageTableView);
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
    private TableView<UsersNH> getTable() {
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
                    this.usersTableRowClick(row.getItem());
                }
            });
            return row;
        });

        table.getColumns().addAll(idCol, userNameColumn, userSurnameColumn, userPeselColumn);
        table.setItems(this.observableList);
        return table;
    }

    /**
     * Event on usersManageTableView row click
     * @param item UsersNH from table row
     */
    private void usersTableRowClick(UsersNH item) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(studentGradesFXMLPath));
            this.studentsGradesDialog = new StudentsGradesDialog(item, this);
            loader.setController(this.studentsGradesDialog);
            Stage stage = new Stage();
            stage.setScene(new Scene(loader.load()));
            stage.addEventHandler(WindowEvent.WINDOW_SHOWN, (e) -> Platform.runLater(studentsGradesDialog::handleWindowShownEvent));
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
}
