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
 * @author Created by Krszysztof Adamczyk on 17.05.2017.
 */
public class EditUserDialogController extends DialogPane {

    @FXML
    private Label createOrEditLabel;

    @FXML
    private Label editedUserNameSurnameLabel;

    @FXML
    private TextField userNameTextBox;

    @FXML
    private TextField userSurnameTextBox;

    @FXML
    private TextField userEmailTextBox;

    @FXML
    private TextField userPeselTextBox;

    @FXML
    private PasswordField userPasswordField;

    @FXML
    private ComboBox<String> userTypeComboBox;

    @FXML
    private TextField userTitleField;

    @FXML
    private Label userCreateDateLabel;

    @FXML
    private Label userLastLoginDateLabel;

    @FXML
    private Label userLastLogoutLabel;

    @FXML
    private Button disableButton;

    @FXML
    private Button saveButton;

    @FXML
    private Label userTitleLabel;

    @FXML
    private Label createDateLabel;

    @FXML
    private Label lastLoginLabel;

    @FXML
    private Label lastLogoutLabel;

    @FXML
    private Button deleteUserButton;

    private UsersNH user;

    private AdminUsersController adminUsersController;

    public EditUserDialogController(UsersNH user, AdminUsersController adminUsersController) throws Exception {
        this.user = user;
        this.adminUsersController = adminUsersController;
    }

    @FXML
    public void handleWindowShownEvent(){
        try {
            boolean creating = this.user.getIdUser() == 0;
            this.deleteUserButton.setDisable(creating);
            this.createOrEditLabel.setText(creating ? "Tworzenie nowego użytkownika" : "Edycja użytkownika");
            this.editedUserNameSurnameLabel.setText(creating ? "" : this.user.getName() + " " + this.user.getSurname());
            this.userNameTextBox.setText(creating ? "" : this.user.getName());
            this.userSurnameTextBox.setText(creating ? "" : this.user.getSurname());
            this.userEmailTextBox.setText(creating ? "" : this.user.getEmail());
            this.userPeselTextBox.setText(creating ? "" : this.user.getPesel());
            ObservableList<String> listToComboBox = FXCollections.observableArrayList();
            listToComboBox.addAll("Administrator", "Nauczyciel", "Uczeń", "Brak");
            this.userTypeComboBox.setItems(listToComboBox);
            this.userTypeComboBox.getSelectionModel().select(this.user.getType().equals("") ? "Brak" : this.user.getType());
            if(this.user.getType() != null && !this.user.getType().equals("Uczeń")) {
                this.userTitleLabel.setDisable(false);
                this.userTitleField.setDisable(false);
                this.userTitleField.setText(this.user.getAdmin() == null ? (this.user.getTeacher() == null ? "" : this.user.getTeacher().getTitle()) : this.user.getAdmin().getTitle());
            }
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
                this.adminUsersController.getController().getClient().requestServer(this.user);
                this.adminUsersController.getUsersManageTableObservableList().remove(this.user);
                this.adminUsersController.getUsersManageTableTableView().refresh();
                Alert alertInfo = new Alert(Alert.AlertType.INFORMATION);
                alertInfo.setTitle("Informacja");
                alertInfo.setHeaderText("Usuwanie użytkownika");
                alertInfo.setContentText("Wykonywana przez Ciebie akcja zakończona sukcesem!");
                alertInfo.showAndWait();
                Controller.getLogger().info("Usunięto użytkownika");
                ((Node)event.getSource()).getScene().getWindow().hide();
            } else {
                event.consume();
            }
        }
    }

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
                Controller.getLogger().info("Edytowano lub dodano użytkownika");
                ((Node)event.getSource()).getScene().getWindow().hide();
            }else {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Błąd");
                alert.setHeaderText("Problem z aktualizacją użytkowników");
                alert.setContentText("Wystąpił błąd z aktualizacją użytkowników. Spróbuj ponownie.");
                alert.showAndWait();
            }
        } else {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Błąd wprowadzonych danych!");
            alert.setHeaderText("Niepoprawne dane!");
            alert.setContentText("Imię: 2-50 liter\nNazwisko: 2-50 liter\nPesel: 11 cyfr");
            alert.showAndWait();
        }
    }

    @FXML
    private boolean validPassword() {
        return this.userPasswordField.getText().length() <= 0 || Validator.validate(this.userPasswordField.getText(), "minLength:4|maxLength:255");
    }
}
