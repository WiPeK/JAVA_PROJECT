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
 * Managing event after click on Managing Substitutes button
 */
public class AdminSubstitutesController {

    /**
     * @see EditSubstitutesDialogController
     */
    private EditSubstitutesDialogController editSubstitutesDialogController;

    /**
     * Contains path to fxml file with view
     */
    private final static String substitutesEditFXMLPath = "/views/substitutesEdit.fxml";

    /**
     * @see TableView
     */
    private TableView<SubstitutesNH> substitutesManageTableView;

    /**
     * @see ObservableList
     * Contains SubstitutesNH items
     */
    private ObservableList<SubstitutesNH> substitutesNHObservableList = FXCollections.observableArrayList();

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

    public AdminSubstitutesController(AdminsController adminsController) {
        this.adminsController = adminsController;
    }

    /**
     * Event on buttonManageSubstitutes button click
     * Setting up center of Controller rootBorderPane
     * @param event ActionEvent button click
     */
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

    /**
     * Creating table with Substitutes objects
     * @return TableView
     */
    @FXML
    private TableView<SubstitutesNH> getTable() {
        TableView<SubstitutesNH> substitutesNHTableView = new TableView<>();
        substitutesNHTableView.setEditable(true);

        this.substitutesNHObservableList.clear();

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

    /**
     * Event on substitutesManageTableView row click
     * @param item SubstitutesNH from table row
     */
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

    /**
     * Creating search bar components
     * @return HBox with components
     */
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

    /**
     * Event on typing in substitutesSearchInput key is pressed
     * @param keyEvent KeyEvent
     */
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

    /**
     * Method respond for searching in substitutes bodies when search by body criteria is selected
     */
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

    /**
     * Method respond for searching in substitutes admins names or surnames when search by author criteria is selected
     */
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

    /**
     * Return substitutesManageTableView object
     * @return TableView
     */
    public TableView<SubstitutesNH> getSubstitutesManageTableView() {
        return substitutesManageTableView;
    }

    /**
     * Return Observable list with table items
     * @return ObservableList
     */
    public ObservableList<SubstitutesNH> getSubstitutesNHObservableList() {
        return substitutesNHObservableList;
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
