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
import pl.wipek.validators.Validator;

import java.util.Date;
import java.util.Optional;

/**
 * @author Krzysztof Adamczyk on 17.05.2017.
 * Managing work with User entities in dialog running in AdminUsersController after click on row in table with all users.
 */
public final class EditUserDialogController extends DialogAbstractController<UsersNH> {

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

    public EditUserDialogController(UsersNH user, AdminUsersController adminUsersController){
        super(user, adminUsersController);
    }

    /**
     * Event on EditUserDialog is showing
     * Setting up components
     */
    @FXML
    public void handleWindowShownEvent(){
        try {
            creating = this.item.getIdUser() == 0;
            this.deleteButton.setDisable(creating);
            this.createOrEditLabel.setText(creating ? "Tworzenie nowego użytkownika" : "Edycja użytkownika");
            this.editedUserNameSurnameLabel.setText(creating ? "" : this.item.getName() + " " + this.item.getSurname());
            this.userNameTextBox.setText(creating ? "" : this.item.getName());
            this.userSurnameTextBox.setText(creating ? "" : this.item.getSurname());
            this.userEmailTextBox.setText(creating ? "" : this.item.getEmail());
            this.userPeselTextBox.setText(creating ? "" : this.item.getPesel());
            this.setUpUserTypeComboBox();
            if(this.item.getType() != null && !this.item.getType().equals("Uczeń")) {
                this.userTitleLabel.setDisable(false);
                this.userTitleField.setDisable(false);
                this.userTitleField.setText(this.item.getAdmin() == null ? (this.item.getTeacher() == null ? "" : this.item.getTeacher().getTitle()) : this.item.getAdmin().getTitle());
            }

            this.userCreateDateLabel.setText(creating ? "" : (this.item.getCreateDate() == null ? "" : this.item.getCreateDate().toString()));
            this.userLastLoginDateLabel.setText(creating ? "" : (this.item.getLastLogIn() == null ? "" : this.item.getLastLogIn().toString()));
            this.userLastLogoutLabel.setText(creating ? "" : (this.item.getLastLogOut() == null ? "" : this.item.getLastLogOut().toString()));
            if(creating) {
                this.createDateLabel.setVisible(false);
                this.lastLoginLabel.setVisible(false);
                this.lastLogoutLabel.setVisible(false);
            }
            this.saveButton.setOnAction(this::saveButtonAction);
            this.deleteButton.setOnAction(this::deleteButtonAction);
            this.disableButton.setOnAction(e -> ((Node)(e.getSource())).getScene().getWindow().hide());
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
        this.userTypeComboBox.getSelectionModel().select(this.item.getType().equals("") ? "Brak" : this.item.getType());

        this.userTypeComboBox.setOnAction(e -> {
            String selected = this.userTypeComboBox.getSelectionModel().getSelectedItem();
            if(selected.equals(listToComboBox.get(0)) || selected.equals(listToComboBox.get(1))) {
                this.userTitleLabel.setDisable(false);
                this.userTitleField.setDisable(false);
                this.userTitleField.setText(this.item.getAdmin() == null ? (this.item.getTeacher() == null ? "" : this.item.getTeacher().getTitle()) : this.item.getAdmin().getTitle());
            }else {
                this.userTitleLabel.setDisable(true);
                this.userTitleField.setDisable(true);
            }
        });
    }

    /**
     * Event on saveButton action
     * Sending request to server for saving or updating Users entity with related sets
     * @param event ActionEvent
     */
    @FXML
    protected void saveButtonAction(ActionEvent event) {
        if(Validator.validate(this.userNameTextBox.getText(), "minLength:2|maxLength:50|onlyLetters")
                && Validator.validate(this.userSurnameTextBox.getText(), "minLength:2|maxLength:50|onlyLetters")
                && Validator.validate(this.userEmailTextBox.getText(), "isValidEmail")
                && Validator.validate(this.userPeselTextBox.getText(), "exactLength:11|onlyNumbers")
                && this.validPassword()) {

            this.item.setName(this.userNameTextBox.getText());
            this.item.setSurname(this.userSurnameTextBox.getText());
            this.item.setEmail(this.userEmailTextBox.getText());
            this.item.setPesel(this.userPeselTextBox.getText());
            this.item.setCreateDate(new Date());

            if(this.userPasswordField.getText().length() >= 4) {
                this.item.setPassword(Hasher.hashSHA512(this.userPasswordField.getText()));
            }
            this.item.setAction(new Action("saveOrUpdate"));
            if(!this.userTypeComboBox.getSelectionModel().isEmpty()) {
                if(this.userTypeComboBox.getSelectionModel().getSelectedItem().equals("Administrator")) {
                    if(this.item.getStudent() != null) {
                        this.item.getStudent().setAction(new Action("remove"));
                    }
                    if(this.item.getTeacher() != null) {
                        this.item.getTeacher().setAction(new Action("remove"));
                    }
                    AdminsNH adminsNH = new AdminsNH();
                    adminsNH.setUser(this.item);
                    int idAdmin = this.item.getAdmin() != null ? this.item.getAdmin().getIdAdmin() : 0;
                    adminsNH.setIdAdmin(idAdmin);
                    adminsNH.setTitle(this.userTitleField.getText());
                    this.item.setAdmin(adminsNH);
                    this.item.setType("Administrator");
                }
                else if(this.userTypeComboBox.getSelectionModel().getSelectedItem().equals("Nauczyciel")) {
                    if(this.item.getStudent() != null) {
                        this.item.getStudent().setAction(new Action("remove"));
                    }
                    if(this.item.getAdmin() != null) {
                        this.item.getAdmin().setAction(new Action("remove"));
                    }
                    TeachersNH teachersNH = new TeachersNH();
                    teachersNH.setUser(this.item);
                    int idTeacher = this.item.getTeacher() != null ? this.item.getTeacher().getIdTeacher() : 0;
                    teachersNH.setIdTeacher(idTeacher);
                    teachersNH.setTitle(this.userTitleField.getText());
                    this.item.setTeacher(teachersNH);
                    this.item.setType("Nauczyciel");
                }
                else if(this.userTypeComboBox.getSelectionModel().getSelectedItem().equals("Uczeń")) {
                    if(this.item.getTeacher() != null) {
                        this.item.getTeacher().setAction(new Action("remove"));
                    }
                    if(this.item.getAdmin() != null) {
                        this.item.getAdmin().setAction(new Action("remove"));
                    }
                    StudentsNH studentsNH = new StudentsNH();
                    int idStudent = this.item.getStudent() != null ? this.item.getStudent().getIdStudent() : 0;
                    studentsNH.setIdStudent(idStudent);
                    studentsNH.setUser(this.item);
                    this.item.setStudent(studentsNH);
                    this.item.setType("Uczeń");
                }
                else if(this.userTypeComboBox.getSelectionModel().getSelectedItem().equals("Brak")) {
                    this.item.setType("");
                }
            }

            UsersNH result = (UsersNH) this.adminController.getController().getClient().requestServer(this.item);
            if(result != null) {
                this.adminController.getObservableList().clear();
                this.adminController.getTableView().setItems(null);
                this.adminController.manageButtonAction(new ActionEvent());
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
                Controller.getLogger().info((this.creating ? "Error in saving user: " : "Error in updating user: ") + this.item);
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
     * Event on deleteButton action
     * Sending request to server for deleting Users entity with related sets
     *
     * @param event ActionEvent
     */
    @Override
    protected void deleteButtonAction(ActionEvent event) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Usuwanie");
        alert.setHeaderText("Usuwanie użytkownika " + this.item.getName() + " " + this.item.getSurname());
        alert.setContentText("Czy na pewno chcesz wykonać czynność?");

        Optional<ButtonType> result = alert.showAndWait();
        if(result.isPresent()) {
            if (result.get() == ButtonType.OK){
                this.item.setAction(new Action("remove"));
                Controller.getLogger().info("Deleting user: " + this.item);
                this.adminController.getController().getClient().requestServer(this.item);
                this.adminController.getObservableList().remove(this.item);
                this.adminController.getTableView().refresh();
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
     * Check password is valid
     * @return boolean true if password passing validation otherwise false
     */
    private boolean validPassword() {
        return this.userPasswordField.getText().length() <= 0 || Validator.validate(this.userPasswordField.getText(), "minLength:4|maxLength:255");
    }
}
