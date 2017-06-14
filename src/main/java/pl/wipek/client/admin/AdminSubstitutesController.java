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
public final class AdminSubstitutesController extends AdminsAbstractController<SubstitutesNH> {

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

    public AdminSubstitutesController(AdminsController adminsController) {
        super(adminsController);
        this.fxmlPath = "/views/substitutesEdit.fxml";
    }

    /**
     * Event on manage button click
     * Setting up center of Controller rootBorderPane
     *
     * @param event ActionEvent button click
     */
    @Override
    public void manageButtonAction(ActionEvent event) {
        try{
            ScrollPane scrollPane = new ScrollPane();
            VBox vBox = new VBox();
            vBox.setMinWidth(754);
            Label title = new Label("Zastępstwa");

            this.tableView = this.getTable();
            this.tableView.setPrefHeight(630);

            vBox.getChildren().addAll(title, this.getSearchBar(), tableView);
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
    protected TableView<SubstitutesNH> getTable() {
        TableView<SubstitutesNH> substitutesNHTableView = new TableView<>();
        substitutesNHTableView.setEditable(true);

        this.observableList.clear();

        Set<Object> substitutesObjects = this.adminsController.getController().getRelationHelper().getAllAsSet(new Action("getAllSubstitutes", "FROM Substitutes s"));
        substitutesObjects.forEach(i -> this.observableList.add(new SubstitutesNH((Substitutes) i)));

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
                    this.setDialogAbstractController(new EditSubstitutesDialogController(row.getItem(), this));
                    this.tableRowClickAction(row.getItem());
                }
            });
            return row;
        });

        substitutesNHTableView.getColumns().addAll(idColumn, adminNameSurname, dateColumn, bodyColumn);
        substitutesNHTableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        substitutesNHTableView.setItems(this.observableList);
        return substitutesNHTableView;
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
        newSubstitutes.setOnAction(ae -> {
            this.setDialogAbstractController(new EditSubstitutesDialogController(null, this));
            this.tableRowClickAction(new SubstitutesNH());
        });
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
                    this.tableView.setItems(this.observableList);
                    this.tableView.refresh();
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
            this.tableView.setItems(this.observableList);
            this.tableView.refresh();
        } else {
            ObservableList<SubstitutesNH> tmpList = FXCollections.observableArrayList();
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
     * Method respond for searching in substitutes admins names or surnames when search by author criteria is selected
     */
    @FXML
    private void searchByAdmin() {
        if(this.searchInput.getText().equals("")) {
            this.tableView.setItems(this.observableList);
            this.tableView.refresh();
        } else {
            ObservableList<SubstitutesNH> tmpList = FXCollections.observableArrayList();
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
