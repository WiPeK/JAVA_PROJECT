package pl.wipek.client.admin.dialogs;

import javafx.beans.property.SimpleBooleanProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.util.Callback;
import javafx.util.StringConverter;
import pl.wipek.client.Controller;
import pl.wipek.client.admin.AdminClassesController;
import pl.wipek.common.Action;
import pl.wipek.db.*;
import pl.wipek.server.db.Semesters;
import pl.wipek.server.db.Users;
import pl.wipek.validators.Validator;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author Krzysztof Adamczyk on 30.05.2017.
 * Managing dialog when we working on classes
 */
public class EditClassesDialogController extends DialogAbstractController<ClassesNH> {

    /**
     * @see TextField
     * Contains label with class name
     */
    @FXML
    private TextField classNameTextField;

    /**
     * @see ComboBox
     * Contains semesters options value from database
     */
    @FXML
    private ComboBox<SemestersNH> semestersComboBox;

    /**
     * @see Label
     * Contains state String: editing or creating
     */
    @FXML
    private Label headerLabel;

    /**
     * @see TableView
     * Contains users objects which are students who can be assigned to class
     */
    @FXML
    private TableView<UsersNH> studentsTableView;

    /**
     * @see ObservableList
     * Contains list with users which are students, content is items for studentsTableView
     */
    private ObservableList<UsersNH> list = FXCollections.observableArrayList();

    /**
     * Default template date to showing dates
     */
    private DateFormat date = new SimpleDateFormat("dd/MM/yyyy");

    public EditClassesDialogController(ClassesNH classes, AdminClassesController adminClassesController) {
        super(classes, adminClassesController);
    }

    /**
     * Event on EditClassesDialog is showing
     * Setting up components
     */
    @FXML
    public void handleWindowShownEvent() {
        this.creating = this.item.getIdClass() == 0;
        this.headerLabel.setText(creating ? "Dodawanie nowej klasy" : "Edycja klasy " + this.item.getName());
        this.classNameTextField.setText(creating ? "" : this.item.getName());
        this.setUpSemestersComboBox();
        this.saveButton.setOnAction(this::saveButtonAction);
        this.deleteButton.setOnAction(this::deleteButtonAction);
        this.disableButton.setOnAction(e -> ((Node)(e.getSource())).getScene().getWindow().hide());
        this.fillTable();
    }

    /**
     * Setting semestersComboBox
     * Fill with data from databases receiving from server request
     */
    private void setUpSemestersComboBox() {
        ObservableList<SemestersNH> listToComboBox = FXCollections.observableArrayList();

        Set<Object> semestersObjects = this.adminController.getController().getRelationHelper().getAllAsSet(new Action("getAllSemesters", "FROM Semesters sm"));
        listToComboBox.add(this.item.getSemester());
        for (Object semObject : semestersObjects) {
            Semesters tmp = (Semesters) semObject;
            if(tmp.getEndDate().compareTo(new Date()) > 0) {
                listToComboBox.add(new SemestersNH(tmp));
            }
        }

        this.semestersComboBox.setItems(listToComboBox);
        this.semestersComboBox.setCellFactory(new Callback<ListView<SemestersNH>, ListCell<SemestersNH>>() {
            @Override
            public ListCell<SemestersNH> call(ListView<SemestersNH> l) {
                return new ListCell<SemestersNH>() {
                    @Override
                    protected void updateItem(SemestersNH semester, boolean empty) {
                        super.updateItem(semester, empty);
                        if(semester == null || empty) {
                            setText("Brak");
                        } else {
                            String comboBoxValue = semester.getIdSemester() + " " + date.format(semester.getStartDate()) + "-" + date.format(semester.getEndDate()) + " " + semester.getSchoolYear().getName();
                            setText(comboBoxValue);
                        }
                    }
                };
            }
        });

        this.semestersComboBox.setConverter(new StringConverter<SemestersNH>() {
            @Override
            public String toString(SemestersNH object) {
                return object.getIdSemester() + " " + date.format(object.getStartDate()) + "-" + date.format(object.getEndDate()) + " " + object.getSchoolYear().getName();
            }

            @Override
            public SemestersNH fromString(String string) {
                String[] splited = string.split("\\s+");
                Optional<SemestersNH> optSemester = listToComboBox.stream().filter(i -> i.getIdSemester() == Integer.parseInt(splited[0])).findFirst();
                item.setSemester(optSemester.orElse(null));
                return null;
            }
        });

        this.semestersComboBox.setValue(this.item.getSemester());
    }

    /**
     * Initializing table, adding columns and setting table items from database receiving from server
     */
    private void fillTable() {
        this.studentsTableView.setEditable(true);

        Set<UsersNH> tmpList = new HashSet<>(0);
        Set<Object> usersObjects = this.adminController.getController().getRelationHelper().getAllAsSet(new Action("getAllUsers", "FROM Users u"));
        usersObjects.forEach(i -> tmpList.add(new UsersNH((Users)i)));
        Set<UsersNH> studentsList = tmpList.stream().filter(i -> i.getStudent() != null).collect(Collectors.toCollection(HashSet::new));
        for (UsersNH it : studentsList) {
            UsersNH tmp = it;
            tmp.getStudent().setAction(new Action("getStudentsClassesToStudent"));
            tmp.setStudent((StudentsNH)this.adminController.getController().getRelationHelper().getRelated(tmp.getStudent()));

            Optional<StudentsClassesNH> res = tmp.getStudent().getStudentsClasses().parallelStream().filter(i -> i.getClasses().getIdClass() == this.item.getIdClass()).findFirst();
            tmp.setInClass(res.isPresent());
            list.add(tmp);
        }

        TableColumn<UsersNH, String> userNameColumn = new TableColumn<>("Imię");
        userNameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        userNameColumn.setMinWidth(150);

        TableColumn<UsersNH, String> userSurnameColumn = new TableColumn<>("Nazwisko");
        userSurnameColumn.setCellValueFactory(new PropertyValueFactory<>("surname"));
        userSurnameColumn.setMinWidth(150);

        TableColumn<UsersNH, String> userPeselColumn = new TableColumn<>("Pesel");
        userPeselColumn.setCellValueFactory(new PropertyValueFactory<>("pesel"));
        userPeselColumn.setMinWidth(150);

        TableColumn<UsersNH, Boolean> checkCol = new TableColumn<>("W tej klasie?");
        checkCol.setMinWidth(150);
        checkCol.setCellValueFactory(param -> {
            UsersNH user = param.getValue();

            SimpleBooleanProperty booleanProperty = new SimpleBooleanProperty(user.isInClass());
            booleanProperty.addListener((observable, oldValue, newValue) -> user.setInClass(newValue));
            return booleanProperty;
        });
        checkCol.setCellFactory(CheckBoxTableCell.forTableColumn(checkCol));

        this.studentsTableView.getColumns().addAll(userNameColumn, userSurnameColumn, userPeselColumn, checkCol);
        this.studentsTableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        this.studentsTableView.setItems(list);
    }

    /**
     * Event on deleteButton action
     * Sending request to server for deleting Classes entity
     * @param event ActionEvent
     */
    protected void deleteButtonAction(ActionEvent event) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Usuwanie");
        alert.setHeaderText("Usuwanie klasy " + this.item.getName());
        alert.setContentText("Czy na pewno chcesz wykonać czynność?");

        Optional<ButtonType> result = alert.showAndWait();
        if(result.isPresent()) {
            if (result.get() == ButtonType.OK){
                Controller.getLogger().info("Deleting Classes: " + this.item);
                this.item.setAction(new Action("remove"));
                this.adminController.getController().getClient().requestServer(this.item);
                Alert alertInfo = new Alert(Alert.AlertType.INFORMATION);
                alertInfo.setTitle("Informacja");
                alertInfo.setHeaderText("Usuwanie klasy");
                alertInfo.setContentText("Wykonywana przez Ciebie akcja zakończona sukcesem!");
                alertInfo.showAndWait();
                ((Node)event.getSource()).getScene().getWindow().hide();
                this.adminController.getObservableList().clear();
                this.adminController.manageButtonAction(new ActionEvent());
            } else {
                event.consume();
            }
        }
    }

    /**
     * Event on saveButton action
     * Sending request to server for saving or updating Classes entity with related sets
     * @param event ActionEvent
     */
    protected void saveButtonAction(ActionEvent event) {
        if(Validator.validate(this.classNameTextField.getText(), "minLength:2|maxLength:50")
                && this.semestersComboBox.getSelectionModel() != null) {
            this.item.setName(this.classNameTextField.getText());
            this.item.setSemester(this.semestersComboBox.getValue());

            for (UsersNH usersNH : this.list) {
                Optional<StudentsClassesNH> hasStudentClasses = usersNH.getStudent().getStudentsClasses().parallelStream().filter(i -> i.getClasses().equals(this.item)).findFirst();
                if(!hasStudentClasses.isPresent() && usersNH.isInClass()) {
                    Optional<StudentsClassesNH> isInClasses = this.item.getStudentsClasses().parallelStream().filter(i -> i.getStudent().equals(usersNH.getStudent())).findFirst();
                    if(!isInClasses.isPresent()) {
                        this.item.getStudentsClasses().add(new StudentsClassesNH(usersNH.getStudent(), this.item));
                    }
                } else if(hasStudentClasses.isPresent() && !usersNH.isInClass()) {
                    Optional<StudentsClassesNH> isInClasses = this.item.getStudentsClasses().stream().filter(i -> i.getStudent().equals(usersNH.getStudent())).findFirst();
                    this.item.getStudentsClasses().remove(isInClasses.orElse(null));
                }
            }

            this.item.setAction(new Action("saveOrUpdate"));
            ClassesNH result = (ClassesNH)this.adminController.getController().getClient().requestServer(this.item);
            if(result != null) {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Informacja");
                alert.setHeaderText("Aktualizacja klas");
                alert.setContentText("Wykonywana przez Ciebie akcja zakończona sukcesem!");
                alert.showAndWait();
                Controller.getLogger().info((this.creating ? "Saving Classes: " : "Updating Classes: ") + result);
                ((Node)event.getSource()).getScene().getWindow().hide();
                this.adminController.getObservableList().clear();
                this.adminController.manageButtonAction(new ActionEvent());
            } else {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Błąd");
                alert.setHeaderText("Problem z aktualizacją klas");
                alert.setContentText("Wystąpił błąd z aktualizacją klas. Spróbuj ponownie.");
                alert.showAndWait();
                Controller.getLogger().info((this.creating ? "Error in saving Classes: " : "Error in updating Classes: ") + this.item);
            }

        } else {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Błąd wprowadzonych danych!");
            alert.setHeaderText("Niepoprawne dane!");
            alert.setContentText("Nazwa klasy: 2-50 liter");
            alert.showAndWait();
        }
    }
}
