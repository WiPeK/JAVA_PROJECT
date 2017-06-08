package pl.wipek.client.admin.dialogs;

import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
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
 * Created by Krszysztof Adamczyk on 30.05.2017.
 */
public class EditClassesDialogController {

    private ClassesNH classes;

    private AdminClassesController adminClassesController;

    @FXML
    private TextField classNameTextField;

    @FXML
    private ComboBox<SemestersNH> semestersComboBox;

    @FXML
    private Button saveButton;

    @FXML
    private Button disableButton;

    @FXML
    private Button deleteButton;

    @FXML
    private Label headerLabel;

    @FXML
    private TableView<UsersNH> studentsTableView;

    private ObservableList<UsersNH> list = FXCollections.observableArrayList();

    public EditClassesDialogController(ClassesNH classes, AdminClassesController adminClassesController) {
        this.classes = classes;
        this.adminClassesController = adminClassesController;
    }

    @FXML
    public void handleWindowShownEvent() {
        boolean creating = this.classes.getIdClass() == 0;
        this.headerLabel.setText(creating ? "Dodawanie nowej klasy" : "Edycja klasy " + this.classes.getName());
        this.classNameTextField.setText(creating ? "" : this.classes.getName());
        DateFormat date = new SimpleDateFormat("dd/MM/yyyy");
        ObservableList<SemestersNH> listToComboBox = FXCollections.observableArrayList();

        Set<Object> semestersObjects = this.adminClassesController.getController().getRelationHelper().getAllAsSet(new Action("getAllSemesters", "FROM Semesters sm"));
        listToComboBox.add(this.classes.getSemester());
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
                classes.setSemester(optSemester.orElse(null));
                return null;
            }
        });

        this.semestersComboBox.setValue(this.classes.getSemester());
        this.saveButton.setOnAction(this::saveButtonAction);
        this.deleteButton.setOnAction(this::deleteButtonAction);
        this.disableButton.setOnAction(e -> ((Node)(e.getSource())).getScene().getWindow().hide());
        this.fillTable();
    }

    private void fillTable() {
        this.studentsTableView.setEditable(true);

        Set<UsersNH> tmpList = new HashSet<>(0);
        Set<Object> usersObjects = this.adminClassesController.getController().getRelationHelper().getAllAsSet(new Action("getAllUsers", "FROM Users u"));
        usersObjects.forEach(i -> tmpList.add(new UsersNH((Users)i)));
        Set<UsersNH> studentsList = tmpList.stream().filter(i -> i.getStudent() != null).collect(Collectors.toCollection(HashSet::new));
        for (UsersNH it : studentsList) {
            UsersNH tmp = it;
            tmp.getStudent().setAction(new Action("getStudentsClassesToStudent"));
            tmp.setStudent((StudentsNH)this.adminClassesController.getController().getRelationHelper().getRelated(tmp.getStudent()));

            Optional<StudentsClassesNH> res = tmp.getStudent().getStudentsClasses().parallelStream().filter(i -> i.getClasses().getIdClass() == this.classes.getIdClass()).findFirst();
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

    private void deleteButtonAction(ActionEvent event) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Usuwanie");
        alert.setHeaderText("Usuwanie klasy " + this.classes.getName());
        alert.setContentText("Czy na pewno chcesz wykonać czynność?");

        Optional<ButtonType> result = alert.showAndWait();
        if(result.isPresent()) {
            if (result.get() == ButtonType.OK){
                this.classes.setAction(new Action("remove"));
                this.adminClassesController.getController().getClient().requestServer(this.classes);
                Alert alertInfo = new Alert(Alert.AlertType.INFORMATION);
                alertInfo.setTitle("Informacja");
                alertInfo.setHeaderText("Usuwanie klasy");
                alertInfo.setContentText("Wykonywana przez Ciebie akcja zakończona sukcesem!");
                alertInfo.showAndWait();
                Controller.getLogger().info("Usunięto klase");
                ((Node)event.getSource()).getScene().getWindow().hide();
                this.adminClassesController.getClassesNHObservableList().clear();
                this.adminClassesController.buttonManageClassesAction(new ActionEvent());
            } else {
                event.consume();
            }
        }
    }

    private void saveButtonAction(ActionEvent event) {
        if(Validator.validate(this.classNameTextField.getText(), "minLength:2|maxLength:50")
                && this.semestersComboBox.getSelectionModel() != null) {
            this.classes.setName(this.classNameTextField.getText());
            this.classes.setSemester(this.semestersComboBox.getValue());

            for (UsersNH usersNH : this.list) {
                Optional<StudentsClassesNH> hasStudentClasses = usersNH.getStudent().getStudentsClasses().parallelStream().filter(i -> i.getClasses().equals(this.classes)).findFirst();
                if(!hasStudentClasses.isPresent() && usersNH.isInClass()) {
                    Optional<StudentsClassesNH> isInClasses = this.classes.getStudentsClasses().parallelStream().filter(i -> i.getStudent().equals(usersNH.getStudent())).findFirst();
                    if(!isInClasses.isPresent()) {
                        this.classes.getStudentsClasses().add(new StudentsClassesNH(usersNH.getStudent(), this.classes));
                    }
                } else if(hasStudentClasses.isPresent() && !usersNH.isInClass()) {
                    Optional<StudentsClassesNH> isInClasses = this.classes.getStudentsClasses().stream().filter(i -> i.getStudent().equals(usersNH.getStudent())).findFirst();
                    this.classes.getStudentsClasses().remove(isInClasses.orElse(null));
                }
            }

            this.classes.setAction(new Action("saveOrUpdate"));
            ClassesNH result = (ClassesNH)this.adminClassesController.getController().getClient().requestServer(this.classes);
            if(result != null) {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Informacja");
                alert.setHeaderText("Aktualizacja klas");
                alert.setContentText("Wykonywana przez Ciebie akcja zakończona sukcesem!");
                alert.showAndWait();
                Controller.getLogger().info("Edytowano lub dodano klasę");
                ((Node)event.getSource()).getScene().getWindow().hide();
                this.adminClassesController.getClassesNHObservableList().clear();
                this.adminClassesController.buttonManageClassesAction(new ActionEvent());
            } else {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Błąd");
                alert.setHeaderText("Problem z aktualizacją klas");
                alert.setContentText("Wystąpił błąd z aktualizacją klas. Spróbuj ponownie.");
                alert.showAndWait();
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
