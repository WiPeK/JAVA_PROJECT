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
import pl.wipek.client.admin.dialogs.EditClassifiedsDialogController;
import pl.wipek.common.Action;
import pl.wipek.db.ClassifiedsNH;
import pl.wipek.server.db.Classifieds;

import java.util.Arrays;
import java.util.Set;

/**
 * Created by Krszysztof Adamczyk on 24.05.2017.
 */
public class AdminClassifiedsController {

    private EditClassifiedsDialogController editClassifiedsDialogController;

    private final static String classifiedsEditFXMLPath = "/views/classifiedsEdit.fxml";

    private TableView<ClassifiedsNH> classifiedsManageTableView;

    private ObservableList<ClassifiedsNH> classifiedsNHObservableList = FXCollections.observableArrayList();

    private ComboBox<String> searchCriterium;

    private TextField searchInput;

    private AdminsController adminsController;

    public AdminClassifiedsController(AdminsController adminsController) {
        this.adminsController = adminsController;
    }

    @FXML
    public void buttonManageClassifiedsAction(ActionEvent event) {
        ScrollPane scrollPane = new ScrollPane();
        VBox vBox = new VBox();
        vBox.setMinWidth(754);
        Label title = new Label("Ogłoszenia");

        this.classifiedsManageTableView = this.getTable();
        this.classifiedsManageTableView.setPrefHeight(630);

        vBox.getChildren().addAll(title, this.getSearchBar(), classifiedsManageTableView);
        scrollPane.setContent(vBox);
        this.adminsController.getController().getRootBorderPane().setCenter(scrollPane);
    }

    @FXML
    private void searchByBody() {
        if(this.searchInput.getText().equals("")) {
            this.classifiedsManageTableView.setItems(this.classifiedsNHObservableList);
            this.classifiedsManageTableView.refresh();
        } else {
            ObservableList<ClassifiedsNH> tmpList = FXCollections.observableArrayList();
            this.classifiedsNHObservableList.forEach(i -> {
                if(i.getBody().toLowerCase().contains(this.searchInput.getText().toLowerCase())) {
                    tmpList.add(i);
                }
            });
            this.classifiedsManageTableView.setItems(tmpList);
            this.classifiedsManageTableView.refresh();
        }
    }

    @FXML
    private TableView<ClassifiedsNH> getTable() {
        TableView<ClassifiedsNH> classifiedsManageTableView = new TableView<>();
        classifiedsManageTableView.setEditable(true);

        this.classifiedsNHObservableList.removeAll(this.classifiedsNHObservableList);

        Set<Object> classifiedsObjects = this.adminsController.getController().getRelationHelper().getAllAsSet(new Action("getAllClassifieds", "FROM Classifieds c ORDER BY idClassifieds DESC"));
        classifiedsObjects.forEach(i -> this.classifiedsNHObservableList.add(new ClassifiedsNH((Classifieds)i)));

        TableColumn<ClassifiedsNH, Integer> idColumn = new TableColumn<>("ID");
        idColumn.setCellValueFactory(new PropertyValueFactory<>("idClassifieds"));
        idColumn.setMinWidth(50);
        idColumn.setMaxWidth(75);

        TableColumn<ClassifiedsNH, String> adminNameSurname = new TableColumn<>("Administrator");
        adminNameSurname.setCellValueFactory(c -> new ReadOnlyStringWrapper(c.getValue().getAdmin().getUser().getName() + " " + c.getValue().getAdmin().getUser().getSurname()));
        adminNameSurname.setMinWidth(75);
        adminNameSurname.setMinWidth(140);

        TableColumn<ClassifiedsNH, String> bodyColumn = new TableColumn<>("Treść");
        bodyColumn.setCellValueFactory(new PropertyValueFactory<>("body"));
        bodyColumn.setMinWidth(150);
        bodyColumn.setMinWidth(200);

        classifiedsManageTableView.setRowFactory(c -> {
            TableRow<ClassifiedsNH> row = new TableRow<>();
            row.setOnMouseClicked(e -> {
                if(e.getButton() == MouseButton.PRIMARY && e.getClickCount() == 2) {
                    this.classifiedsTableRowClick(row.getItem());
                }
            });
            return row;
        });

        classifiedsManageTableView.getColumns().addAll(idColumn, adminNameSurname, bodyColumn);
        classifiedsManageTableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        classifiedsManageTableView.setItems(this.classifiedsNHObservableList);
        return classifiedsManageTableView;
    }

    @FXML
    private void classifiedsTableRowClick(ClassifiedsNH item) {
        try{
            FXMLLoader loader = new FXMLLoader(getClass().getResource(classifiedsEditFXMLPath));
            this.editClassifiedsDialogController = new EditClassifiedsDialogController(item, this);
            loader.setController(this.editClassifiedsDialogController);
            Stage stage = new Stage();
            stage.setScene(new Scene(loader.load()));
            stage.addEventHandler(WindowEvent.WINDOW_SHOWN, (e) -> Platform.runLater(editClassifiedsDialogController::handleWindowShownEvent));
            stage.show();
        }catch (Exception e) {
            Controller.getLogger().error(e);
            e.printStackTrace();
        }
    }

    @FXML
    private HBox getSearchBar() {
        HBox hBox = new HBox();
        this.searchInput = new TextField();
        searchInput.setPromptText("Wpisz coś aby wyszukać...");
        searchInput.setOnKeyPressed(this::classifiedsSearchInputKeyPressed);
        String[] optionsArray = {"Administrator", "Treść"};
        this.searchCriterium = new ComboBox<>(FXCollections.observableArrayList(Arrays.asList(optionsArray)));
        searchCriterium.setPromptText("Wybierz kryterium wyszukiwania");
        Button newClassifieds = new Button("Dodaj nowe ogłoszenie");
        newClassifieds.setOnAction(ae -> this.classifiedsTableRowClick(new ClassifiedsNH()));
        hBox.getChildren().addAll(searchInput, searchCriterium, newClassifieds);
        return hBox;
    }

    @FXML
    private void classifiedsSearchInputKeyPressed(KeyEvent keyEvent) {
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
                    this.classifiedsManageTableView.setItems(this.classifiedsNHObservableList);
                    this.classifiedsManageTableView.refresh();
                }
            }
        }
    }

    @FXML
    private void searchByAdmin() {
        if(this.searchInput.getText().equals("")) {
            this.classifiedsManageTableView.setItems(this.classifiedsNHObservableList);
            this.classifiedsManageTableView.refresh();
        } else {
            ObservableList<ClassifiedsNH> tmpList = FXCollections.observableArrayList();
            this.classifiedsNHObservableList.forEach(i -> {
                if(i.getAdmin().getUser().getName().toLowerCase().contains(this.searchInput.getText().toLowerCase()) || i.getAdmin().getUser().getSurname().toLowerCase().contains(this.searchInput.getText().toLowerCase())) {
                    tmpList.add(i);
                }
            });
            this.classifiedsManageTableView.setItems(tmpList);
            this.classifiedsManageTableView.refresh();
        }
    }

    public TableView<ClassifiedsNH> getClassifiedsManageTableView() {
        return classifiedsManageTableView;
    }

    public ObservableList<ClassifiedsNH> getClassifiedsNHObservableList() {
        return classifiedsNHObservableList;
    }

    public Controller getController() {
        return this.adminsController.getController();
    }
}
