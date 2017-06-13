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
 * @author Krzysztof Adamczyk on 24.05.2017.
 * Managing event after click on Managing classifieds button
 */
public class AdminClassifiedsController {

    /**
     * @see EditClassifiedsDialogController
     */
    private EditClassifiedsDialogController editClassifiedsDialogController;

    /**
     * Contains path to fxml file with view
     */
    private final static String classifiedsEditFXMLPath = "/views/classifiedsEdit.fxml";

    /**
     * @see TableView
     */
    private TableView<ClassifiedsNH> classifiedsManageTableView;

    /**
     * @see ObservableList
     * Contains ClassifiedsNH items
     */
    private ObservableList<ClassifiedsNH> classifiedsNHObservableList = FXCollections.observableArrayList();

    /**
     * @see ComboBox
     * Contains criteria to search in table
     */
    private ComboBox<String> searchCriterium;

    /**
     * @see TextField
     * Input when admin can type searched phrase
     */
    private TextField searchInput;

    /**
     * @see AdminsController
     */
    private AdminsController adminsController;

    public AdminClassifiedsController(AdminsController adminsController) {
        this.adminsController = adminsController;
    }

    /**
     * Event on buttonManageClassifieds button click
     * Setting up center of Controller rootBorderPane
     * @param event ActionEvent button click
     */
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

    /**
     * Creating table with Classifieds objects
     * @return TableView
     */
    @FXML
    private TableView<ClassifiedsNH> getTable() {
        TableView<ClassifiedsNH> classifiedsManageTableView = new TableView<>();
        classifiedsManageTableView.setEditable(true);

        this.classifiedsNHObservableList.clear();

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

    /**
     * Event on classifiedsManageTableView row click
     * @param item ClassifiedsNH from table row
     */
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

    /**
     * Creating search bar components
     * @return HBox with components
     */
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

    /**
     * Event on typing in classifiedsSearchInput key is pressed
     * @param keyEvent KeyEvent
     */
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

    /**
     * Method respond for searching in classifieds bodies when search by body criteria is selected
     */
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

    /**
     * Method respond for searching in classifieds admins names or surnames when search by author criteria is selected
     */
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

    /**
     * Return classifiedsManageTableView object
     * @return TableView
     */
    public TableView<ClassifiedsNH> getClassifiedsManageTableView() {
        return classifiedsManageTableView;
    }

    /**
     * Return Observable list with table items
     * @return ObservableList
     */
    public ObservableList<ClassifiedsNH> getClassifiedsNHObservableList() {
        return classifiedsNHObservableList;
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
