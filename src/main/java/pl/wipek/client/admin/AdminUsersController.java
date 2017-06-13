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
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import pl.wipek.client.Controller;
import pl.wipek.client.admin.dialogs.EditUserDialogController;
import pl.wipek.common.Action;
import pl.wipek.db.UsersNH;
import pl.wipek.server.db.Users;

import java.util.Arrays;
import java.util.Set;

/**
 * @author Krzysztof Adamczyk on 24.05.2017.
 * Managing event after click on Managing Users button
 */
public class AdminUsersController {

    /**
     * Contains path to fxml file with view
     */
    private final static String userEditFXMLPath = "/views/userEdit.fxml";

    /**
     * @see ObservableList
     * Contains UsersNH items
     */
    private ObservableList<UsersNH> usersManageTableObservableList = FXCollections.observableArrayList();

    /**
     * @see EditUserDialogController
     */
    private EditUserDialogController editUserDialogController;

    /**
     * @see TableView
     */
    private TableView<UsersNH> usersManageTableTableView;

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

    AdminUsersController(AdminsController adminsController){
        this.adminsController = adminsController;
    }

    /**
     * Event on buttonManageUsers button click
     * Setting up center of Controller rootBorderPane
     * @param event ActionEvent button click
     */
    @FXML
    public void buttonManageUsersAction(ActionEvent event){
        try {
            ScrollPane scrollPane = new ScrollPane();
            VBox vBox = new VBox();
            vBox.setMinWidth(754);
            Label title = new Label("Użytkownicy");

            this.usersManageTableTableView = this.getTable();
            this.usersManageTableTableView.setPrefHeight(630);

            vBox.getChildren().addAll(title, this.getSearchBar(), usersManageTableTableView);
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
    @FXML
    private TableView<UsersNH> getTable() throws Exception {
        TableView<UsersNH> usersManageTableTableView = new TableView<>();
        usersManageTableTableView.setEditable(true);

        this.usersManageTableObservableList.clear();
        Set<Object> usersObjects = this.adminsController.getController().getRelationHelper().getAllAsSet(new Action("getAllUsers", "FROM Users u"));
        usersObjects.forEach(i -> this.usersManageTableObservableList.add(new UsersNH((Users)i)));

        TableColumn<UsersNH, Integer> idColumn = new TableColumn<>("ID");
        idColumn.setCellValueFactory(new PropertyValueFactory<>("idUser"));
        idColumn.setMinWidth(50);
        idColumn.setMaxWidth(75);

        TableColumn<UsersNH, String> userNameColumn = new TableColumn<>("Imię");
        userNameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        userNameColumn.setMinWidth(150);

        TableColumn<UsersNH, String> userSurnameColumn = new TableColumn<>("Nazwisko");
        userSurnameColumn.setCellValueFactory(new PropertyValueFactory<>("surname"));
        userSurnameColumn.setMinWidth(150);

        TableColumn<UsersNH, String> userEmailColumn = new TableColumn<>("Email");
        userEmailColumn.setCellValueFactory(new PropertyValueFactory<>("email"));
        userEmailColumn.setMinWidth(150);

        TableColumn<UsersNH, String> userPeselColumn = new TableColumn<>("Pesel");
        userPeselColumn.setCellValueFactory(new PropertyValueFactory<>("pesel"));
        userPeselColumn.setMinWidth(150);

        TableColumn<UsersNH, String> userTypeColumn = new TableColumn<>("Rola");
        userTypeColumn.setCellValueFactory(new PropertyValueFactory<>("type"));
        userTypeColumn.setMinWidth(50);

        usersManageTableTableView.setRowFactory(c -> {
            TableRow<UsersNH> row = new TableRow<>();
            row.setOnMouseClicked(e -> {
                if(e.getButton() == MouseButton.PRIMARY && e.getClickCount() == 2) {
                    this.usersTableRowClick(row.getItem());
                }
            });
            return row;
        });

        usersManageTableTableView.getColumns().addAll(idColumn, userNameColumn, userSurnameColumn, userEmailColumn, userPeselColumn, userTypeColumn);
        usersManageTableTableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        usersManageTableTableView.setItems(usersManageTableObservableList);
        return usersManageTableTableView;
    }

    /**
     * Event on usersManageTableTableView row click
     * @param item UsersNH from table row
     */
    @FXML
    private void usersTableRowClick(UsersNH item) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(userEditFXMLPath));
            this.editUserDialogController = new EditUserDialogController(item, this);
            loader.setController(this.editUserDialogController);
            Stage stage = new Stage();
            stage.setScene(new Scene(loader.load()));
            stage.addEventHandler(WindowEvent.WINDOW_SHOWN, (e) -> Platform.runLater(editUserDialogController::handleWindowShownEvent));
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
    private HBox getSearchBar() throws Exception {
        HBox hBox = new HBox();
        this.searchInput = new TextField();
        searchInput.setPromptText("Wpisz coś aby wyszukać...");
        searchInput.setOnKeyPressed(this::searchInputKeyPressed);
        String[] optionsArray = {"Email", "Imię", "Nazwisko", "Pesel"};
        this.searchCriterium = new ComboBox<>(FXCollections.observableArrayList(Arrays.asList(optionsArray)));
        searchCriterium.setPromptText("Wybierz kryterium wyszukiwania");
        Button newUser = new Button("Dodaj nowego użytkownika");
        newUser.setOnAction(ae -> this.usersTableRowClick(new UsersNH()));
        hBox.getChildren().addAll(searchInput, searchCriterium, newUser);
        return hBox;
    }

    /**
     * Event on typing in searchInput key is pressed
     * @param keyEvent KeyEvent
     */
    @FXML
    private void searchInputKeyPressed(KeyEvent keyEvent) {
        try {
            if(keyEvent.getCode().equals(KeyCode.ENTER)) {
                String criteria = this.searchCriterium.getSelectionModel().getSelectedItem();
                if(criteria != null) {
                    if(criteria.equals("Email")) {
                        this.searchByEmail();
                    }
                    else if(criteria.equals("Imię")) {
                        this.searchByName();
                    }
                    else if(criteria.equals("Nazwisko")) {
                        this.searchBySurname();
                    }
                    else if(criteria.equals("Pesel")) {
                        this.searchByPesel();
                    }
                    else {
                        this.usersManageTableTableView.setItems(this.usersManageTableObservableList);
                        this.usersManageTableTableView.refresh();
                    }
                }
            }
        }
        catch (Exception e) {
            Controller.getLogger().error(e);
            e.printStackTrace();
        }
    }

    /**
     * Method respond for searching in users emails when search by email criteria is selected
     */
    @FXML
    private void searchByEmail() throws Exception {
        if(this.searchInput.getText().equals("")) {
            this.usersManageTableTableView.setItems(this.usersManageTableObservableList);
            this.usersManageTableTableView.refresh();
        } else {
            ObservableList<UsersNH> tmpList = FXCollections.observableArrayList();
            this.usersManageTableObservableList.forEach(i -> {
                if(i.getEmail().toLowerCase().contains(this.searchInput.getText().toLowerCase())) {
                    tmpList.add(i);
                }
            });
            this.usersManageTableTableView.setItems(tmpList);
            this.usersManageTableTableView.refresh();
        }
    }

    /**
     * Method respond for searching in users names when search by name criteria is selected
     */
    @FXML
    private void searchByName() throws Exception {
        if(this.searchInput.getText().equals("")) {
            this.usersManageTableTableView.setItems(this.usersManageTableObservableList);
            this.usersManageTableTableView.refresh();
        } else {
            ObservableList<UsersNH> tmpList = FXCollections.observableArrayList();
            this.usersManageTableObservableList.forEach(i -> {
                if(i.getName().toLowerCase().contains(this.searchInput.getText().toLowerCase())) {
                    tmpList.add(i);
                }
            });
            this.usersManageTableTableView.setItems(tmpList);
            this.usersManageTableTableView.refresh();
        }
    }

    /**
     * Method respond for searching in users surnames when search by surname criteria is selected
     */
    @FXML
    private void searchBySurname() throws Exception {
        if(this.searchInput.getText().equals("")) {
            this.usersManageTableTableView.setItems(this.usersManageTableObservableList);
            this.usersManageTableTableView.refresh();
        } else {
            ObservableList<UsersNH> tmpList = FXCollections.observableArrayList();
            this.usersManageTableObservableList.forEach(i -> {
                if(i.getSurname().toLowerCase().contains(this.searchInput.getText().toLowerCase())) {
                    tmpList.add(i);
                }
            });
            this.usersManageTableTableView.setItems(tmpList);
            this.usersManageTableTableView.refresh();
        }
    }

    /**
     * Method respond for searching in users pesels when search by pesel criteria is selected
     */
    @FXML
    private void searchByPesel() throws Exception {
        if(this.searchInput.getText().equals("")) {
            this.usersManageTableTableView.setItems(this.usersManageTableObservableList);
            this.usersManageTableTableView.refresh();
        } else {
            ObservableList<UsersNH> tmpList = FXCollections.observableArrayList();
            this.usersManageTableObservableList.forEach(i -> {
                if(i.getPesel().toLowerCase().contains(this.searchInput.getText().toLowerCase())) {
                    tmpList.add(i);
                }
            });
            this.usersManageTableTableView.setItems(tmpList);
            this.usersManageTableTableView.refresh();
        }
    }

    /**
     * Return Observable list with table items
     * @return ObservableList
     */
    public ObservableList<UsersNH> getUsersManageTableObservableList() {
        return usersManageTableObservableList;
    }

    /**
     * Return usersManageTableTableView object
     * @return TableView
     */
    public TableView<UsersNH> getUsersManageTableTableView() {
        return usersManageTableTableView;
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
