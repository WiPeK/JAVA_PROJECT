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
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import pl.wipek.client.Controller;
import pl.wipek.client.admin.dialogs.EditSubstitutesDialogController;
import pl.wipek.common.Action;
import pl.wipek.db.SubstitutesNH;
import pl.wipek.server.db.Substitutes;

import java.util.Arrays;
import java.util.Date;
import java.util.Set;


/**
 * @author Created by Krszysztof Adamczyk on 24.05.2017.
 */
public class AdminSubstitutesController {

    private EditSubstitutesDialogController editSubstitutesDialogController;

    private final static String substitutesEditFXMLPath = "/views/substitutesEdit.fxml";

    private TableView<SubstitutesNH> substitutesManageTableView;

    private ObservableList<SubstitutesNH> substitutesNHObservableList = FXCollections.observableArrayList();

    private ComboBox<String> searchCriterium;

    private TextField searchInput;

    private AdminsController adminsController;

    public AdminSubstitutesController(AdminsController adminsController) {
        this.adminsController = adminsController;
    }

    @FXML
    public void buttonManageSubstitutesAction(ActionEvent event) {
        try{
            ScrollPane scrollPane = new ScrollPane();
            VBox vBox = new VBox();
            vBox.setMinWidth(754);
            Label title = new Label("Zastępstwa");

            this.substitutesManageTableView = this.getTable();
            this.substitutesManageTableView.setPrefHeight(630);

            vBox.getChildren().addAll(title, this.getSearchBar(), substitutesManageTableView);
            scrollPane.setContent(vBox);
            this.adminsController.getController().getRootBorderPane().setCenter(scrollPane);
        }catch (Exception e) {
            Controller.getLogger().error(e);
            e.printStackTrace();
        }
    }

    @FXML
    private TableView<SubstitutesNH> getTable() {
        TableView<SubstitutesNH> substitutesNHTableView = new TableView<>();
        substitutesNHTableView.setEditable(true);

        this.substitutesNHObservableList.removeAll(this.substitutesNHObservableList);

        Set<Object> substitutesObjects = this.adminsController.getController().getRelationHelper().getAllAsSet(new Action("getAllSubstitutes", "FROM Substitutes s"));
        substitutesObjects.forEach(i -> this.substitutesNHObservableList.add(new SubstitutesNH((Substitutes) i)));

        TableColumn<SubstitutesNH, Integer> idColumn = new TableColumn<>("ID");
        idColumn.setCellValueFactory(new PropertyValueFactory<>("idSubstitute"));
        idColumn.setMinWidth(50);
        idColumn.setMaxWidth(75);

        TableColumn<SubstitutesNH, String> adminNameSurname = new TableColumn<>("Administrator");
        adminNameSurname.setCellValueFactory(c -> new ReadOnlyStringWrapper(c.getValue().getAdmin().getUser().getName() + " " + c.getValue().getAdmin().getUser().getSurname()));
        adminNameSurname.setMinWidth(75);
        adminNameSurname.setMinWidth(140);

        TableColumn<SubstitutesNH, Date> dateColumn = new TableColumn<>("Data");
        dateColumn.setCellValueFactory(new PropertyValueFactory<>("date"));
        dateColumn.setMinWidth(30);
        dateColumn.setMinWidth(75);

        TableColumn<SubstitutesNH, String> bodyColumn = new TableColumn<>("Treść");
        bodyColumn.setCellValueFactory(new PropertyValueFactory<>("body"));
        bodyColumn.setMinWidth(150);
        bodyColumn.setMinWidth(200);

        substitutesNHTableView.setRowFactory(c -> {
            TableRow<SubstitutesNH> row = new TableRow<>();
            row.setOnMouseClicked(e -> {
                if(e.getButton() == MouseButton.PRIMARY && e.getClickCount() == 2) {
                    this.substitutesTableRowClick(row.getItem());
                }
            });
            return row;
        });

        substitutesNHTableView.getColumns().addAll(idColumn, adminNameSurname, dateColumn, bodyColumn);
        substitutesNHTableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        substitutesNHTableView.setItems(this.substitutesNHObservableList);
        return substitutesNHTableView;
    }

    @FXML
    private void substitutesTableRowClick(SubstitutesNH item) {
        try{
            FXMLLoader loader = new FXMLLoader(getClass().getResource(substitutesEditFXMLPath));
            this.editSubstitutesDialogController = new EditSubstitutesDialogController(item, this);
            loader.setController(this.editSubstitutesDialogController);
            Stage stage = new Stage();
            stage.setScene(new Scene(loader.load()));
            stage.addEventHandler(WindowEvent.WINDOW_SHOWN, (e) -> Platform.runLater(this.editSubstitutesDialogController::handleWindowShownEvent));
            stage.show();
        }catch (Exception e) {
            Controller.getLogger().error(e);
            e.printStackTrace();
        }
    }

    @FXML
    private void searchByBody() {
        if(this.searchInput.getText().equals("")) {
            this.substitutesManageTableView.setItems(this.substitutesNHObservableList);
            this.substitutesManageTableView.refresh();
        } else {
            ObservableList<SubstitutesNH> tmpList = FXCollections.observableArrayList();
            this.substitutesNHObservableList.forEach(i -> {
                if(i.getBody().toLowerCase().contains(this.searchInput.getText().toLowerCase())) {
                    tmpList.add(i);
                }
            });
            this.substitutesManageTableView.setItems(tmpList);
            this.substitutesManageTableView.refresh();
        }
    }

    @FXML
    private HBox getSearchBar() {
        HBox hBox = new HBox();
        this.searchInput = new TextField();
        searchInput.setPromptText("Wpisz coś aby wyszukać...");
        searchInput.setOnKeyPressed(this::substitutesSearchInputKeyPressed);
        String[] optionsArray = {"Administrator", "Treść"};
        this.searchCriterium = new ComboBox<>(FXCollections.observableArrayList(Arrays.asList(optionsArray)));
        searchCriterium.setPromptText("Wybierz kryterium wyszukiwania");
        Button newSubstitutes = new Button("Dodaj nowe ogłoszenie");
        newSubstitutes.setOnAction(ae -> this.substitutesTableRowClick(new SubstitutesNH()));
        hBox.getChildren().addAll(searchInput, searchCriterium, newSubstitutes);
        return hBox;
    }

    @FXML
    private void substitutesSearchInputKeyPressed(KeyEvent keyEvent) {
        if(keyEvent.getCode().equals(KeyCode.ENTER)) {
            String criteria = this.searchCriterium.getSelectionModel().getSelectedItem();
            if(criteria != null) {
                if(criteria.equals("Administrator")) {
                    this.searchByAdmin();
                }
                else if(criteria.equals("Treść")) {
                    this.searchByBody();
                }
                else {
                    this.substitutesManageTableView.setItems(this.substitutesNHObservableList);
                    this.substitutesManageTableView.refresh();
                }
            }
        }
    }

    @FXML
    private void searchByAdmin() {
        if(this.searchInput.getText().equals("")) {
            this.substitutesManageTableView.setItems(this.substitutesNHObservableList);
            this.substitutesManageTableView.refresh();
        } else {
            ObservableList<SubstitutesNH> tmpList = FXCollections.observableArrayList();
            this.substitutesNHObservableList.forEach(i -> {
                if(i.getAdmin().getUser().getName().toLowerCase().contains(this.searchInput.getText().toLowerCase()) || i.getAdmin().getUser().getSurname().toLowerCase().contains(this.searchInput.getText().toLowerCase())) {
                    tmpList.add(i);
                }
            });
            this.substitutesManageTableView.setItems(tmpList);
            this.substitutesManageTableView.refresh();
        }
    }

    public TableView<SubstitutesNH> getSubstitutesManageTableView() {
        return substitutesManageTableView;
    }

    public ObservableList<SubstitutesNH> getSubstitutesNHObservableList() {
        return substitutesNHObservableList;
    }

    public Controller getController() {
        return this.adminsController.getController();
    }
}
