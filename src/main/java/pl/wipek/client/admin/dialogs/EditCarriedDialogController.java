package pl.wipek.client.admin.dialogs;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.util.Callback;
import javafx.util.StringConverter;
import pl.wipek.client.Controller;
import pl.wipek.client.admin.AdminCarriedController;
import pl.wipek.common.Action;
import pl.wipek.db.*;
import pl.wipek.server.db.*;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Optional;
import java.util.Set;

/**
 * @author Krzysztof Adamczyk on 05.06.2017.
 * Managing dialog when we can operate on subjects which is teaching in semesters by teacher in class
 */
public class EditCarriedDialogController {

    /**
     * @see AdminCarriedController
     */
    private AdminCarriedController adminCarriedController;

    /**
     * @see CarriedSubjectsNH
     */
    private CarriedSubjectsNH carried;

    /**
     * @see ComboBox
     * Contains users objects which correspondent to all teachers
     */
    @FXML
    private ComboBox<UsersNH> teacherComboBox;

    /**
     * @see ComboBox
     * Contains classes objects in which teacher will be teaching subject in semester
     */
    @FXML
    private ComboBox<ClassesNH> classComboBox;

    /**
     * @see ComboBox
     * Contains semesters which is not finished yet. We can choose semester in which will be teaching subject by teacher
     */
    @FXML
    private ComboBox<SemestersNH> semesterComboBox;

    /**
     * @see ComboBox
     * Contains subjects objects which is available in school to teaching
     */
    @FXML
    private ComboBox<SubjectsNH> subjectComboBox;

    /**
     * @see Button
     * Button respondend for deleting CarriedSubjects entity
     * on action EditCarriedDialogController.deleteButtonAction
     */
    @FXML
    private Button deleteButton;

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
     * Status managing object
     * true when objects is create
     * false when objects is editing
     */
    private boolean creating;

    /**
     * Default template date to showing dates
     */
    private DateFormat date = new SimpleDateFormat("dd/MM/yyyy");

    public EditCarriedDialogController(CarriedSubjectsNH carried, AdminCarriedController adminCarriedController) {
        this.adminCarriedController = adminCarriedController;
        this.carried = carried;
    }

    /**
     * Event on EditCarriedDialog is showing
     * Setting up components
     */
    @FXML
    public void handleWindowShownEvent() {
        this.creating = this.carried.getIdCarriedSubject() == 0;
        this.setUpTeacherComboBox();
        this.setUpClassComboBox();
        this.setUpSemesterComboBox();
        this.setUpSubjectComboBox();
        this.saveButton.setOnAction(this::saveButtonAction);
        this.deleteButton.setOnAction(this::deleteButtonAction);
        this.disableButton.setOnAction(e -> ((Node)(e.getSource())).getScene().getWindow().hide());
    }

    /**
     * Setting subjectComboBox
     * Fill with data from databases receiving from server request
     */
    private void setUpSubjectComboBox() {
        ObservableList<SubjectsNH> subjectToComboBox = FXCollections.observableArrayList();
        Set<Object> subjectsObjects = this.adminCarriedController.getController().getRelationHelper().getAllAsSet(new Action("getAllSubjects", "FROM Subjects sb"));
        subjectToComboBox.add(this.carried.getSubject());
        subjectsObjects.forEach(i -> subjectToComboBox.add(new SubjectsNH((Subjects) i)));

        this.subjectComboBox.setItems(subjectToComboBox);

        this.subjectComboBox.setCellFactory(new Callback<ListView<SubjectsNH>, ListCell<SubjectsNH>>() {
            @Override
            public ListCell<SubjectsNH> call(ListView<SubjectsNH> param) {
                return new ListCell<SubjectsNH>() {
                    @Override
                    protected void updateItem(SubjectsNH subject, boolean empty) {
                        super.updateItem(subject, empty);
                        setText(subject == null || empty ? "Brak" : subject.getName());
                    }
                };
            }
        });

        this.subjectComboBox.setConverter(new StringConverter<SubjectsNH>() {
            @Override
            public String toString(SubjectsNH object) {
                return object.getIdSubject() + " " + object.getName();
            }

            @Override
            public SubjectsNH fromString(String string) {
                String[] splited = string.split("\\s+");
                Optional<SubjectsNH> optSubject = subjectToComboBox.stream().filter(i -> i.getIdSubject() == Integer.parseInt(splited[0])).findFirst();
                carried.setSubject(optSubject.orElse(null));
                return null;
            }
        });

        this.subjectComboBox.setValue(this.carried.getSubject());
    }

    /**
     * Setting semesterComboBox
     * FFill with data from databases receiving from server request
     */
    private void setUpSemesterComboBox() {
        ObservableList<SemestersNH> listToComboBox = FXCollections.observableArrayList();

        Set<Object> semestersObjects = this.adminCarriedController.getController().getRelationHelper().getAllAsSet(new Action("getAllSemesters", "FROM Semesters sm"));
        listToComboBox.add(this.carried.getSemester());
        for (Object semObject : semestersObjects) {
            Semesters tmp = (Semesters) semObject;
            if(tmp.getEndDate().compareTo(new Date()) > 0) {
                listToComboBox.add(new SemestersNH(tmp));
            }
        }

        this.semesterComboBox.setItems(listToComboBox);

        this.semesterComboBox.setCellFactory(new Callback<ListView<SemestersNH>, ListCell<SemestersNH>>() {
            @Override
            public ListCell<SemestersNH> call(ListView<SemestersNH> l) {
                return new ListCell<SemestersNH>() {
                    @Override
                    protected void updateItem(SemestersNH semester, boolean empty) {
                        super.updateItem(semester, empty);
                        if(semester == null || empty) {
                            setText("Brak");
                        } else {
                            String comboBoxValue = date.format(semester.getStartDate()) + "-" + date.format(semester.getEndDate()) + " " + semester.getSchoolYear().getName();
                            setText(comboBoxValue);
                        }
                    }
                };
            }
        });

        this.semesterComboBox.setConverter(new StringConverter<SemestersNH>() {

            @Override
            public String toString(SemestersNH object) {
                return object.getIdSemester() + " " + date.format(object.getStartDate()) + "-" + date.format(object.getEndDate()) + " " + object.getSchoolYear().getName();
            }

            @Override
            public SemestersNH fromString(String string) {
                String[] splited = string.split("\\s+");
                Optional<SemestersNH> optSemester = listToComboBox.stream().filter(i -> i.getIdSemester() == Integer.parseInt(splited[0])).findFirst();
                carried.setSemester(optSemester.orElse(null));
                return null;
            }
        });

        this.semesterComboBox.setValue(this.creating ? null : this.carried.getSemester());
    }

    /**
     * Setting classComboBox
     * Fill with data from databases receiving from server request
     */
    private void setUpClassComboBox() {
        ObservableList<ClassesNH> listToClasses = FXCollections.observableArrayList();
        Set<Object> classesObjects = this.adminCarriedController.getController().getRelationHelper().getAllAsSet(new Action("getAllClasses", "FROM Classes cl"));
        listToClasses.add(this.carried.getClasses());
        classesObjects.forEach(i -> listToClasses.add(new ClassesNH((Classes) i)));

        this.classComboBox.setItems(listToClasses);

        this.classComboBox.setCellFactory(new Callback<ListView<ClassesNH>, ListCell<ClassesNH>>() {
            @Override
            public ListCell<ClassesNH> call(ListView<ClassesNH> param) {
                return new ListCell<ClassesNH>() {
                    @Override
                    protected void updateItem(ClassesNH classes, boolean empty) {
                        super.updateItem(classes, empty);
                        setText(classes == null || empty ? "Brak" : classes.getName() + " " + date.format(classes.getSemester().getStartDate()) + "-" + date.format(classes.getSemester().getEndDate()) + " " + classes.getSemester().getSchoolYear().getName());
                    }
                };
            }
        });

        this.classComboBox.setConverter(new StringConverter<ClassesNH>() {
            @Override
            public String toString(ClassesNH object) {
                String comboBoxValue = date.format(object.getSemester().getStartDate()) + "-" + date.format(object.getSemester().getEndDate()) + " " + object.getSemester().getSchoolYear().getName();
                return object.getIdClass() + " " + object.getName() + " " + comboBoxValue;
            }

            @Override
            public ClassesNH fromString(String string) {
                String[] splited = string.split("\\s+");
                Optional<ClassesNH> optClasses = listToClasses.stream().filter(i -> i.getIdClass() == Integer.parseInt(splited[0])).findFirst();
                carried.setClasses(optClasses.orElse(null));
                return null;
            }
        });

        this.classComboBox.setValue(this.creating ? null : this.carried.getClasses());
    }

    /**
     * Setting teacherComboBox
     * Fill with data from databases receiving from server request
     */
    private void setUpTeacherComboBox() {
        ObservableList<UsersNH> listToTeacher = FXCollections.observableArrayList();
        Set<Object> teacherObjects = this.adminCarriedController.getController().getRelationHelper().getAllAsSet(new Action("getAllTeachers", "FROM Teachers tch"));
        teacherObjects.forEach(i -> listToTeacher.add(new UsersNH(((Teachers)i).getUser())));

        this.teacherComboBox.setItems(listToTeacher);

        this.teacherComboBox.setCellFactory(new Callback<ListView<UsersNH>, ListCell<UsersNH>>() {
            @Override
            public ListCell<UsersNH> call(ListView<UsersNH> param) {
                return new ListCell<UsersNH>() {
                    @Override
                    protected void updateItem(UsersNH user, boolean empty) {
                        super.updateItem(user, empty);
                        if(user == null || empty) {
                            setText("Brak");
                        } else {
                            String value = user.getName() + " " + user.getSurname();
                            setText(value);
                        }
                    }
                };
            }
        });

        this.teacherComboBox.setConverter(new StringConverter<UsersNH>() {
            @Override
            public String toString(UsersNH object) {
                return object.getIdUser() + " " + object.getName() + " " + object.getSurname();
            }

            @Override
            public UsersNH fromString(String string) {
                String[] splited = string.split("\\s+");
                Optional<UsersNH> optUser = listToTeacher.stream().filter(i -> i.getIdUser() == Integer.parseInt(splited[0])).findFirst();
                carried.setTeacher(optUser.map(UsersNH::getTeacher).orElse(null));
                return null;
            }
        });

        this.teacherComboBox.setValue(this.creating ? null : this.carried.getTeacher().getUser());
    }

    /**
     * Event on deleteButton action
     * Sending request to server for deleting CarriedSubjects entity
     * @param event ActionEvent
     */
    private void deleteButtonAction(ActionEvent event) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Usuwanie");
        alert.setHeaderText("Usuwanie nauczanego przedmiotu");
        alert.setContentText("Czy na pewno chcesz wykonać czynność?");

        Optional<ButtonType> result = alert.showAndWait();
        if(result.isPresent()) {
            Controller.getLogger().info("Deleting CarriedSubject: " + this.carried);
            this.carried.setAction(new Action("remove"));
            this.adminCarriedController.getController().getClient().requestServer(this.carried);
            Alert alertInfo = new Alert(Alert.AlertType.INFORMATION);
            this.adminCarriedController.getCarriedSubjectsNHObservableList().remove(this.carried);
            this.adminCarriedController.getCarriedSubjectsNHTableView().refresh();
            alertInfo.setTitle("Informacja");
            alertInfo.setHeaderText("Usuwanie nauczanego przedmiotu");
            alertInfo.setContentText("Wykonywana przez Ciebie akcja zakończona sukcesem!");
            alertInfo.showAndWait();
            ((Node)event.getSource()).getScene().getWindow().hide();
        }else{
            event.consume();
        }
    }

    /**
     * Event on saveButton action
     * Sending request to server for saving or updating CarriedSubjects entity with related sets
     * @param event ActionEvent
     */
    private void saveButtonAction(ActionEvent event) {
        if(this.teacherComboBox.getValue() != null && this.classComboBox.getValue() != null && this.semesterComboBox.getValue() != null && this.subjectComboBox.getValue() != null) {
            this.carried.setSubject(this.subjectComboBox.getValue());
            this.carried.setClasses(this.classComboBox.getValue());
            this.carried.setSemester(this.semesterComboBox.getValue());
            this.carried.setTeacher(this.teacherComboBox.getValue().getTeacher());
            this.carried.setAction(new Action("saveOrUpdate"));

            CarriedSubjects result = (CarriedSubjects) this.adminCarriedController.getController().getClient().requestServer(this.carried);
            if(result != null) {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Informacja");
                alert.setHeaderText("Aktualizacja nauczanych przedmiotów");
                alert.setContentText("Wykonywana przez Ciebie akcja zakończona sukcesem!");
                alert.showAndWait();
                Controller.getLogger().info((this.creating ? "Saving CarriedSubject: " : "Updating CarriedSubject: ") + result);
                this.adminCarriedController.getCarriedSubjectsNHObservableList().removeAll(this.adminCarriedController.getCarriedSubjectsNHObservableList());
                this.adminCarriedController.getCarriedSubjectsNHTableView().setItems(null);
                this.adminCarriedController.buttonManageCarriedAction(new ActionEvent());
                ((Node)event.getSource()).getScene().getWindow().hide();
            } else {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Błąd");
                alert.setHeaderText("Problem z aktualizacją nauczanych przedmiotów");
                alert.setContentText("Wystąpił błąd z aktualizacją nauczanych przedmiotów. Spróbuj ponownie.");
                alert.showAndWait();
                Controller.getLogger().info((this.creating ? "Error in saving CarriedSubject: " : "Error in updating CarriedSubject: ") + this.carried);
            }
        }else {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Błąd wprowadzonych danych!");
            alert.setHeaderText("Niepoprawne dane!");
            alert.setContentText("Wybierz wszystkie opcje");
            alert.showAndWait();
        }
    }
}
