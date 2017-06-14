package pl.wipek.client.admin;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.util.Pair;
import pl.wipek.client.Controller;
import pl.wipek.common.Action;
import pl.wipek.db.SubjectsNH;
import pl.wipek.server.db.Subjects;
import pl.wipek.validators.Validator;

import java.util.Optional;
import java.util.Set;

/**
 * @author Krzysztof Adamczyk on 24.05.2017.
 * Managing event after click Managing Subjects button
 */
public final class AdminSubjectsController extends AdminsAbstractController<SubjectsNH> {

    public AdminSubjectsController(AdminsController adminsController) {
        super(adminsController);
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
        Label title = new Label("Przedmioty");

        this.tableView = this.getTable();
        this.tableView.setPrefHeight(530);

        Button newSubject = new Button("Dodaj nowy przedmiot");
        newSubject.setOnAction(ae -> this.subjectsTableRowClick(new SubjectsNH()));

        vBox.getChildren().addAll(title, newSubject, this.tableView);
        scrollPane.setContent(vBox);
        this.adminsController.getController().getRootBorderPane().setCenter(scrollPane);
    }

    /**
     * Creating table with SubjectsNH objects
     * @return TableView
     */
    @FXML
    protected TableView<SubjectsNH> getTable() {
        TableView<SubjectsNH> subjectsTableView = new TableView<>();
        subjectsTableView.setEditable(true);

        Set<Object> subjectsObjects = this.adminsController.getController().getRelationHelper().getAllAsSet(new Action("getAllSubjects", "FROM Subjects s"));
        subjectsObjects.forEach(i -> this.observableList.add(new SubjectsNH((Subjects) i)));

        TableColumn<SubjectsNH, Integer> idColumn = new TableColumn<>("ID");
        idColumn.setCellValueFactory(new PropertyValueFactory<>("idSubject"));
        idColumn.setMinWidth(50);
        idColumn.setMaxWidth(75);

        TableColumn<SubjectsNH, String> nameColumn = new TableColumn<>("Nazwa");
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));

        subjectsTableView.setRowFactory(c -> {
            TableRow<SubjectsNH> row = new TableRow<>();
            row.setOnMouseClicked(e -> {
                if(e.getButton() == MouseButton.PRIMARY && e.getClickCount() == 2) {
                    this.subjectsTableRowClick(row.getItem());
                }
            });
            return row;
        });

        this.observableList.clear();
        subjectsTableView.getColumns().addAll(idColumn, nameColumn);
        subjectsTableView.setItems(this.observableList);
        return subjectsTableView;
    }

    /**
     * Event on subjectsManageTableView row click
     * @param subjects SubjectsNH from table row
     */
    @FXML
    private void subjectsTableRowClick(SubjectsNH subjects) {
        SubjectsNH subject = subjects;
        Pair<String, String> result = this.showDialog(subjects);
        if(result.getKey().equals("Zapisz") && Validator.validate(result.getValue(), "minLength:2|maxLength:50")) {
            subject.setAction(new Action("saveOrUpdate"));
            subject.setName(result.getValue());
            SubjectsNH res = (SubjectsNH) this.adminsController.getController().getClient().requestServer(subject);
            if(res != null) {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Informacja");
                alert.setHeaderText("Aktualizacja przedmiotów");
                alert.setContentText("Wykonywana przez Ciebie akcja zakończona sukcesem!");
                alert.showAndWait();
                this.observableList.removeAll(this.observableList);
                Set<Object> subjectsObjects = this.adminsController.getController().getRelationHelper().getAllAsSet(new Action("getAllSubjects", "FROM Subjects s"));
                subjectsObjects.forEach(i -> this.observableList.add(new SubjectsNH((Subjects) i)));
                this.tableView.setItems(this.observableList);
                Controller.getLogger().info("Edytowano lub dodano przedmiot");
            }else {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Błąd");
                alert.setHeaderText("Problem z aktualizacją przedmiotów");
                alert.setContentText("Wystąpił błąd z aktualizacją przedmiotów. Spróbuj ponownie.");
                alert.showAndWait();
            }
        }
        else if(result.getKey().equals("Usuń")) {
            subject.setAction(new Action("remove"));
            Boolean res = (Boolean) this.adminsController.getController().getClient().requestServer(subject);
            if(res) {
                Alert alertInfo = new Alert(Alert.AlertType.INFORMATION);
                alertInfo.setTitle("Informacja");
                alertInfo.setHeaderText("Usuwanie przedmiotu");
                alertInfo.setContentText("Wykonywana przez Ciebie akcja zakończona sukcesem!");
                alertInfo.showAndWait();
                Controller.getLogger().info("Deleting Subject: " + subjects);
                this.observableList.removeAll(this.observableList);
                Set<Object> subjectsObjects = this.adminsController.getController().getRelationHelper().getAllAsSet(new Action("getAllSubjects", "FROM Subjects s"));
                subjectsObjects.forEach(i -> this.observableList.add(new SubjectsNH((Subjects) i)));
                this.tableView.setItems(this.observableList);
                Controller.getLogger().info("Usunięto przedmiot");
            } else {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Błąd");
                alert.setHeaderText("Problem z usuwaniem przedmiotu");
                alert.setContentText("Wystąpił błąd z usuwaniem przedmiotu. Spróbuj ponownie.");
                alert.showAndWait();
            }
        }
    }

    /**
     * Method show dialog when admin can add, edit or delete Subject
     * @param subjects SubjectNH
     * @return Pair<String, String>
     */
    private Pair<String, String> showDialog(SubjectsNH subjects) {
        Dialog<Pair<ButtonType, String>> dialog = new Dialog<>();
        dialog.setTitle("Przedmiot");
        dialog.setHeaderText("Edycja przedmiotu " + subjects.getName());

        ButtonType deleteButtonType = new ButtonType("Usuń");
        ButtonType saveButtonType = new ButtonType("Zapisz", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(deleteButtonType, saveButtonType, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        TextField name = new TextField();
        if(subjects.getIdSubject() > 0) {
            name.setText(subjects.getName());
        }
        name.setPromptText("Wpisz nazwę...");

        grid.add(new Label("Nazwa przedmiotu:"), 0, 0);
        grid.add(name, 1, 0);

        Node saveButton = dialog.getDialogPane().lookupButton(saveButtonType);


        name.textProperty().addListener((observable, oldValue, newValue) -> {
            saveButton.setDisable(newValue.trim().isEmpty());
        });

        dialog.getDialogPane().setContent(grid);

        Platform.runLater(name::requestFocus);

        dialog.setResultConverter(dialogButton -> new Pair<>(dialogButton, name.getText()));

        Optional<Pair<ButtonType, String>> resultDialog = dialog.showAndWait();
        Pair<String, String> result = null;
        if(resultDialog.isPresent()) {
            result = new Pair<>(resultDialog.get().getKey().getText(), resultDialog.get().getValue());
        }
        return result;
    }
}
