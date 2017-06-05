package pl.wipek.client.admin;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
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
 * Created by Krszysztof Adamczyk on 24.05.2017.
 */
public class AdminSubjectsController {

    private AdminsController adminsController;

    private TableView<SubjectsNH> subjectsManageTableView;

    private ObservableList<SubjectsNH> subjectsNHObservableList = FXCollections.observableArrayList();

    public AdminSubjectsController(AdminsController adminsController) {
        this.adminsController = adminsController;
    }

    @FXML
    public void buttonManageSubjectsAction(ActionEvent event) {
        ScrollPane scrollPane = new ScrollPane();
        VBox vBox = new VBox();
        vBox.setMinWidth(754);
        Label title = new Label("Przedmioty");

        this.subjectsManageTableView = this.getTable();
        this.subjectsManageTableView.setPrefHeight(530);

        Button newSubject = new Button("Dodaj nowy przedmiot");
        newSubject.setOnAction(ae -> this.subjectsTableRowClick(new SubjectsNH()));

        vBox.getChildren().addAll(title, newSubject, this.subjectsManageTableView);
        scrollPane.setContent(vBox);
        this.adminsController.getController().getRootBorderPane().setCenter(scrollPane);
    }

    @FXML
    private TableView<SubjectsNH> getTable() {
        TableView<SubjectsNH> subjectsTableView = new TableView<>();
        subjectsTableView.setEditable(true);

        Set<Object> subjectsObjects = this.adminsController.getController().getRelationHelper().getAllAsSet(new Action("getAllSubjects", "FROM Subjects s"));
        subjectsObjects.forEach(i -> this.subjectsNHObservableList.add(new SubjectsNH((Subjects) i)));

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

        this.subjectsNHObservableList.clear();
        subjectsTableView.getColumns().addAll(idColumn, nameColumn);
        subjectsTableView.setItems(this.subjectsNHObservableList);
        return subjectsTableView;
    }

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
                this.subjectsNHObservableList.removeAll(this.subjectsNHObservableList);
                Set<Object> subjectsObjects = this.adminsController.getController().getRelationHelper().getAllAsSet(new Action("getAllSubjects", "FROM Subjects s"));
                subjectsObjects.forEach(i -> this.subjectsNHObservableList.add(new SubjectsNH((Subjects) i)));
                this.subjectsManageTableView.setItems(this.subjectsNHObservableList);
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
                this.subjectsNHObservableList.removeAll(this.subjectsNHObservableList);
                Set<Object> subjectsObjects = this.adminsController.getController().getRelationHelper().getAllAsSet(new Action("getAllSubjects", "FROM Subjects s"));
                subjectsObjects.forEach(i -> this.subjectsNHObservableList.add(new SubjectsNH((Subjects) i)));
                this.subjectsManageTableView.setItems(this.subjectsNHObservableList);
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

        Platform.runLater(() -> name.requestFocus());

        dialog.setResultConverter(dialogButton -> new Pair<>(dialogButton, name.getText()));

        Optional<Pair<ButtonType, String>> resultDialog = dialog.showAndWait();
        Pair<String, String> result = null;
        if(resultDialog.isPresent()) {
            result = new Pair<>(resultDialog.get().getKey().getText(), resultDialog.get().getValue());
        }
        return result;
    }
}
