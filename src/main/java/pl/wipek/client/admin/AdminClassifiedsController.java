package pl.wipek.client.admin;

import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
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
public final class AdminClassifiedsController extends AdminsAbstractController<ClassifiedsNH> {

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

    public AdminClassifiedsController(AdminsController adminsController) {
        super(adminsController);
        this.fxmlPath = "/views/classifiedsEdit.fxml";
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
        Label title = new Label("Ogłoszenia");

        this.tableView = this.getTable();
        this.tableView.setPrefHeight(630);

        vBox.getChildren().addAll(title, this.getSearchBar(), tableView);
        scrollPane.setContent(vBox);
        this.adminsController.getController().getRootBorderPane().setCenter(scrollPane);
    }

    /**
     * Creating table with Classifieds objects
     * @return TableView
     */
    @FXML
    protected TableView<ClassifiedsNH> getTable() {
        TableView<ClassifiedsNH> classifiedsManageTableView = new TableView<>();
        classifiedsManageTableView.setEditable(true);

        this.observableList.clear();

        Set<Object> classifiedsObjects = this.adminsController.getController().getRelationHelper().getAllAsSet(new Action("getAllClassifieds", "FROM Classifieds c ORDER BY idClassifieds DESC"));
        classifiedsObjects.forEach(i -> this.observableList.add(new ClassifiedsNH((Classifieds)i)));

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
                    this.setDialogAbstractController(new EditClassifiedsDialogController(row.getItem(), this));
                    this.tableRowClickAction(row.getItem());
                }
            });
            return row;
        });

        classifiedsManageTableView.getColumns().addAll(idColumn, adminNameSurname, bodyColumn);
        classifiedsManageTableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        classifiedsManageTableView.setItems(this.observableList);
        return classifiedsManageTableView;
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
        newClassifieds.setOnAction(ae -> {
            this.setDialogAbstractController(new EditClassifiedsDialogController(null, this));
            this.tableRowClickAction(new ClassifiedsNH());
        });
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
                    this.tableView.setItems(this.observableList);
                    this.tableView.refresh();
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
            this.tableView.setItems(this.observableList);
            this.tableView.refresh();
        } else {
            ObservableList<ClassifiedsNH> tmpList = FXCollections.observableArrayList();
            this.observableList.forEach(i -> {
                if(i.getBody().toLowerCase().contains(this.searchInput.getText().toLowerCase())) {
                    tmpList.add(i);
                }
            });
            this.tableView.setItems(tmpList);
            this.tableView.refresh();
        }
    }

    /**
     * Method respond for searching in classifieds admins names or surnames when search by author criteria is selected
     */
    @FXML
    private void searchByAdmin() {
        if(this.searchInput.getText().equals("")) {
            this.tableView.setItems(this.observableList);
            this.tableView.refresh();
        } else {
            ObservableList<ClassifiedsNH> tmpList = FXCollections.observableArrayList();
            this.observableList.forEach(i -> {
                if(i.getAdmin().getUser().getName().toLowerCase().contains(this.searchInput.getText().toLowerCase()) || i.getAdmin().getUser().getSurname().toLowerCase().contains(this.searchInput.getText().toLowerCase())) {
                    tmpList.add(i);
                }
            });
            this.tableView.setItems(tmpList);
            this.tableView.refresh();
        }
    }
}
