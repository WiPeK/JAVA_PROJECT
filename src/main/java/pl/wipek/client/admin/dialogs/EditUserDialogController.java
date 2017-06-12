package pl.wipek.client.admin.dialogs;

import pl.wipek.helpers.Hasher;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import pl.wipek.client.Controller;
import pl.wipek.client.admin.AdminUsersController;
import pl.wipek.common.Action;
import pl.wipek.db.AdminsNH;
import pl.wipek.db.StudentsNH;
import pl.wipek.db.TeachersNH;
import pl.wipek.db.UsersNH;
import pl.wipek.server.db.Users;
import pl.wipek.validators.Validator;

import java.util.Date;
import java.util.Optional;
import java.util.Set;

/**
 * @author Krzysztof Adamczyk on 17.05.2017.
 * Managing work with User entities in dialog running in AdminUsersController after click on row in table with all users.
 */
public class EditUserDialogController extends DialogPane {

    /**
     * @see Label
     * Contains operation status value
     */
    @FXML
    private Label createOrEditLabel;

    /**
     * @see Label
     * Contains user name and surname when user is edited
     */
    @FXML
    private Label editedUserNameSurnameLabel;

    /**
     * @see TextField
     * Contains user name when editing or empty when creating user
     */
    @FXML
    private TextField userNameTextBox;

    /**
     * @see TextField
     * Contains user surname when editing or empty when creating user
     */
    @FXML
    private TextField userSurnameTextBox;

    /**
     * @see TextField
     * Contains email name when editing or empty when creating user
     */
    @FXML
    private TextField userEmailTextBox;

    /**
     * @see TextField
     * Contains user pesel when editing or empty when creating user
     */
    @FXML
    private TextField userPeselTextBox;

    /**
     * @see PasswordField
     * Contains user password
     */
    @FXML
    private PasswordField userPasswordField;

    /**
     * @see ComboBox
     * Contains type of created or updated user
     */
    @FXML
    private ComboBox<String> userTypeComboBox;

    /**
     * @see TextField
     * Contains user academic degree if teacher or admin
     */
    @FXML
    private TextField userTitleField;

    /**
     * @see Label
     * Contains date when user was created
     */
    @FXML
    private Label userCreateDateLabel;

    /**
     * @see Label
     * Contains date when user last was logged in
     */
    @FXML
    private Label userLastLoginDateLabel;

    /**
     * @see Label
     * Contains date when user last was logged out
     */
    @FXML
    private Label userLastLogoutLabel;

    /**
     * @see Button
     * Button is hiding dialog
     */
    @FXML
    private Button disableButton;

    /**
     * @see Button
     * On action calling method which validate inputs data end save or update entity
     */
    @FXML
    private Button saveButton;

    /**
     * @see Label
     * Contains user academic degree if teacher or admin
     */
    @FXML
    private Label userTitleLabel;

    /**
     * @see Label
     * Contains user create date
     */
    @FXML
    private Label createDateLabel;

    /**
     * @see Label
     * Contains user last login date
     */
    @FXML
    private Label lastLoginLabel;

    /**
     * @see Label
     * Contains last logout date user
     */
    @FXML
    private Label lastLogoutLabel;

    /**
     * @see Button
     * Button respond for deleting Users entity
     */
    @FXML
    private Button deleteUserButton;

    /**
     * @see UsersNH
     */
    private UsersNH user;

    /**
     * @see AdminUsersController
     */
    private AdminUsersController adminUsersController;

    /**
     * Status managing object
     * true when objects is create
     * false when objects is editing
     */
    private boolean creating;

    public EditUserDialogController(UsersNH user, AdminUsersController adminUsersController) throws Exception {
        this.user = user;
        this.adminUsersController = adminUsersController;
    }

    /**
     * Event on EditUserDialog is showing
     * Setting up components
     */
    @FXML
    public void handleWindowShownEvent(){
        try {
            creating = this.user.getIdUser() == 0;
            this.deleteUserButton.setDisable(creating);
            this.createOrEditLabel.setText(creating ? "Tworzenie nowego użytkownika" : "Edycja użytkownika");
            this.editedUserNameSurnameLabel.setText(creating ? "" : this.user.getName() + " " + this.user.getSurname());
            this.userNameTextBox.setText(creating ? "" : this.user.getName());
            this.userSurnameTextBox.setText(creating ? "" : this.user.getSurname());
            this.userEmailTextBox.setText(creating ? "" : this.user.getEmail());
            this.userPeselTextBox.setText(creating ? "" : this.user.getPesel());
            this.setUpUserTypeComboBox();
            if(this.user.getType() != null && !this.user.getType().equals("Uczeń")) {
                this.userTitleLabel.setDisable(false);
                this.userTitleField.setDisable(false);
                this.userTitleField.setText(this.user.getAdmin() == null ? (this.user.getTeacher() == null ? "" : this.user.getTeacher().getTitle()) : this.user.getAdmin().getTitle());
            }

            this.userCreateDateLabel.setText(creating ? "" : (this.user.getCreateDate() == null ? "" : this.user.getCreateDate().toString()));
            this.userLastLoginDateLabel.setText(creating ? "" : (this.user.getLastLogIn() == null ? "" : this.user.getLastLogIn().toString()));
            this.userLastLogoutLabel.setText(creating ? "" : (this.user.getLastLogOut() == null ? "" : this.user.getLastLogOut().toString()));
            if(creating) {
                this.createDateLabel.setVisible(false);
                this.lastLoginLabel.setVisible(false);
                this.lastLogoutLabel.setVisible(false);
            }
            this.disableButton.setOnAction(e -> ((Node)(e.getSource())).getScene().getWindow().hide());
            this.saveButton.setOnAction(this::saveButtonAction);
            this.deleteUserButton.setOnAction(this::deleteUserButtonAction);
        }catch (Exception e) {
            Controller.getLogger().error(e);
            e.printStackTrace();
        }
    }

    /**
     * Setting userTypeComboBox
     */
    @FXML
    private void setUpUserTypeComboBox() {
        ObservableList<String> listToComboBox = FXCollections.observableArrayList();
        listToComboBox.addAll("Administrator", "Nauczyciel", "Uczeń", "Brak");
        this.userTypeComboBox.setItems(listToComboBox);
        this.userTypeComboBox.getSelectionModel().select(this.user.getType().equals("") ? "Brak" : this.user.getType());

        this.userTypeComboBox.setOnAction(e -> {
            String selected = this.userTypeComboBox.getSelectionModel().getSelectedItem();
            if(selected.equals(listToComboBox.get(0)) || selected.equals(listToComboBox.get(1))) {
                this.userTitleLabel.setDisable(false);
                this.userTitleField.setDisable(false);
                this.userTitleField.setText(this.user.getAdmin() == null ? (this.user.getTeacher() == null ? "" : this.user.getTeacher().getTitle()) : this.user.getAdmin().getTitle());
            }else {
                this.userTitleLabel.setDisable(true);
                this.userTitleField.setDisable(true);
            }
        });
    }

    /**
     * Event on deleteButton action
     * Sending request to server for deleting Users entity with related sets
     * @param event ActionEvent
     */
    @FXML
    private void deleteUserButtonAction(ActionEvent event) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Usuwanie");
        alert.setHeaderText("Usuwanie użytkownika " + this.user.getName() + " " + this.user.getSurname());
        alert.setContentText("Czy na pewno chcesz wykonać czynność?");

        Optional<ButtonType> result = alert.showAndWait();
        if(result.isPresent()) {
            if (result.get() == ButtonType.OK){
                this.user.setAction(new Action("remove"));
                Controller.getLogger().info("Deleting user: " + this.user);
                this.adminUsersController.getController().getClient().requestServer(this.user);
                this.adminUsersController.getUsersManageTableObservableList().remove(this.user);
                this.adminUsersController.getUsersManageTableTableView().refresh();
                Alert alertInfo = new Alert(Alert.AlertType.INFORMATION);
                alertInfo.setTitle("Informacja");
                alertInfo.setHeaderText("Usuwanie użytkownika");
                alertInfo.setContentText("Wykonywana przez Ciebie akcja zakończona sukcesem!");
                alertInfo.showAndWait();
                ((Node)event.getSource()).getScene().getWindow().hide();
            } else {
                event.consume();
            }
        }
    }

    /**
     * Event on saveButton action
     * Sending request to server for saving or updating Users entity with related sets
     * @param event ActionEvent
     */
    @FXML
    private void saveButtonAction(ActionEvent event) {
        if(Validator.validate(this.userNameTextBox.getText(), "minLength:2|maxLength:50|onlyLetters")
                && Validator.validate(this.userSurnameTextBox.getText(), "minLength:2|maxLength:50|onlyLetters")
                && Validator.validate(this.userEmailTextBox.getText(), "isValidEmail")
                && Validator.validate(this.userPeselTextBox.getText(), "exactLength:11|onlyNumbers")
                && this.validPassword()) {

            this.user.setName(this.userNameTextBox.getText());
            this.user.setSurname(this.userSurnameTextBox.getText());
            this.user.setEmail(this.userEmailTextBox.getText());
            this.user.setPesel(this.userPeselTextBox.getText());
            this.user.setCreateDate(new Date());

            if(this.userPasswordField.getText().length() >= 4) {
                this.user.setPassword(Hasher.hashSHA512(this.userPasswordField.getText()));
            }
            this.user.setAction(new Action("saveOrUpdate"));
            if(!this.userTypeComboBox.getSelectionModel().isEmpty()) {
                if(this.userTypeComboBox.getSelectionModel().getSelectedItem().equals("Administrator")) {
                    if(this.user.getStudent() != null) {
                        this.user.getStudent().setAction(new Action("remove"));
                    }
                    if(this.user.getTeacher() != null) {
                        this.user.getTeacher().setAction(new Action("remove"));
                    }
                    AdminsNH adminsNH = new AdminsNH();
                    adminsNH.setUser(this.user);
                    int idAdmin = this.user.getAdmin() != null ? this.user.getAdmin().getIdAdmin() : 0;
                    adminsNH.setIdAdmin(idAdmin);
                    adminsNH.setTitle(this.userTitleField.getText());
                    this.user.setAdmin(adminsNH);
                    this.user.setType("Administrator");
                }
                else if(this.userTypeComboBox.getSelectionModel().getSelectedItem().equals("Nauczyciel")) {
                    if(this.user.getStudent() != null) {
                        this.user.getStudent().setAction(new Action("remove"));
                    }
                    if(this.user.getAdmin() != null) {
                        this.user.getAdmin().setAction(new Action("remove"));
                    }
                    TeachersNH teachersNH = new TeachersNH();
                    teachersNH.setUser(this.user);
                    int idTeacher = this.user.getTeacher() != null ? this.user.getTeacher().getIdTeacher() : 0;
                    teachersNH.setIdTeacher(idTeacher);
                    teachersNH.setTitle(this.userTitleField.getText());
                    this.user.setTeacher(teachersNH);
                    this.user.setType("Nauczyciel");
                }
                else if(this.userTypeComboBox.getSelectionModel().getSelectedItem().equals("Uczeń")) {
                    if(this.user.getTeacher() != null) {
                        this.user.getTeacher().setAction(new Action("remove"));
                    }
                    if(this.user.getAdmin() != null) {
                        this.user.getAdmin().setAction(new Action("remove"));
                    }
                    StudentsNH studentsNH = new StudentsNH();
                    int idStudent = this.user.getStudent() != null ? this.user.getStudent().getIdStudent() : 0;
                    studentsNH.setIdStudent(idStudent);
                    studentsNH.setUser(this.user);
                    this.user.setStudent(studentsNH);
                    this.user.setType("Uczeń");
                }
                else if(this.userTypeComboBox.getSelectionModel().getSelectedItem().equals("Brak")) {
                    this.user.setType("");
                }
            }

            UsersNH result = (UsersNH) this.adminUsersController.getController().getClient().requestServer(this.user);
            if(result != null) {
                this.adminUsersController.getUsersManageTableObservableList().removeAll(this.adminUsersController.getUsersManageTableObservableList());
                this.adminUsersController.getUsersManageTableTableView().setItems(null);
                Set<Object> usersObjects = this.adminUsersController.getController().getRelationHelper().getAllAsSet(new Action("getAllUsers", "FROM Users u"));
                usersObjects.forEach(i -> this.adminUsersController.getUsersManageTableObservableList().add(new UsersNH((Users)i)));
                this.adminUsersController.getUsersManageTableTableView().setItems(this.adminUsersController.getUsersManageTableObservableList());
                this.adminUsersController.getUsersManageTableTableView().refresh();
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Informacja");
                alert.setHeaderText("Aktualizacja użytkowników");
                alert.setContentText("Wykonywana przez Ciebie akcja zakończona sukcesem!");
                alert.showAndWait();
                Controller.getLogger().info((this.creating ? "Saving User: " : "Updating User: ") + result);
                ((Node)event.getSource()).getScene().getWindow().hide();
            }else {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Błąd");
                alert.setHeaderText("Problem z aktualizacją użytkowników");
                alert.setContentText("Wystąpił błąd z aktualizacją użytkowników. Spróbuj ponownie.");
                alert.showAndWait();
                Controller.getLogger().info((this.creating ? "Error in saving user: " : "Error in updating user: ") + this.user);
            }
        } else {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Błąd wprowadzonych danych!");
            alert.setHeaderText("Niepoprawne dane!");
            alert.setContentText("Imię: 2-50 liter\nNazwisko: 2-50 liter\nPesel: 11 cyfr");
            alert.showAndWait();
        }
    }

    /**
     * Check password is valid
     * @return boolean true if password passing validation otherwise false
     */
    private boolean validPassword() {
        return this.userPasswordField.getText().length() <= 0 || Validator.validate(this.userPasswordField.getText(), "minLength:4|maxLength:255");
    }
}
