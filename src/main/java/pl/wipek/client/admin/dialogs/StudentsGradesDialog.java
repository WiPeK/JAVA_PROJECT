package pl.wipek.client.admin.dialogs;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.util.StringConverter;
import pl.wipek.client.UserGradesTable;
import pl.wipek.client.admin.AdminGradesController;
import pl.wipek.common.Action;
import pl.wipek.db.*;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author Krzysztof Adamczyk on 08.06.2017.
 * Showing student gradesin admin panel
 */
public class StudentsGradesDialog extends DialogAbstractController<UsersNH> {

    /**
     * @see Label
     * Contains student name and surname
     */
    @FXML
    private Label studentNameSurnameLabel;

    /**
     * @see ComboBox
     * Contains students classes to choose by admin
     */
    @FXML
    private ComboBox<ClassesNH> classComboBox;

    /**
     * @see TableView
     * Contains list with all students
     */
    @FXML
    private TableView<UserGradesTable> studentGradesTable;

    /**
     * @see ObservableList
     * @see UserGradesTable
     * Contains UserGradesTable object for every student
     */
    private ObservableList<UserGradesTable> items = FXCollections.observableArrayList();

    /**
     * Default template date to showing dates
     */
    private DateFormat date = new SimpleDateFormat("dd/MM/yyyy");

    public StudentsGradesDialog(UsersNH user, AdminGradesController adminGradesController) {
        super(user, adminGradesController);
    }

    /**
     * Event on StudentsGradesDialog is showing
     * Setting up components
     */
    public void handleWindowShownEvent() {
        this.studentNameSurnameLabel.setText(this.item.getName() + " " + this.item.getSurname());
        this.item.getStudent().setAction(new Action("getStudentsClassesToStudent"));
        this.item.setStudent((StudentsNH)this.adminController.getController().getRelationHelper().getRelated(this.item.getStudent()));

        this.item.getStudent().setAction(new Action("getGradesToStudent"));
        this.item.setStudent((StudentsNH) this.adminController.getController().getRelationHelper().getRelated(this.item.getStudent()));
        this.fillTable();
        this.setUpClassComboBox();
    }

    /**
     * Event on saveButton action
     * Sending request to server for saving or updating entity with related sets
     *
     * @param event ActionEvent
     */
    @Override
    protected void saveButtonAction(ActionEvent event) {}

    /**
     * Event on deleteButton action
     * Sending request to server for deleting Users entity with related sets
     *
     * @param event ActionEvent
     */
    @Override
    protected void deleteButtonAction(ActionEvent event) {}

    /**
     * Setting up classComboBox with items and events
     */
    @FXML
    private void setUpClassComboBox() {
        ObservableList<ClassesNH> listToComboBox = FXCollections.observableArrayList();

        this.item.getStudent().getStudentsClasses().forEach(i -> {
            i.getClasses().setAction(new Action("getSemesterToClass"));
            i.setClasses((ClassesNH)this.adminController.getController().getRelationHelper().getRelated(i.getClasses()));
            i.getClasses().getSemester().setAction(new Action("getYearToSemester"));
            i.getClasses().setSemester((SemestersNH)this.adminController.getController().getRelationHelper().getRelated(i.getClasses().getSemester()));
            listToComboBox.add(i.getClasses());
        });
        this.classComboBox.setItems(listToComboBox);
        this.classComboBox.setConverter(new StringConverter<ClassesNH>() {
            @Override
            public String toString(ClassesNH object) {
                return object.getName() + " " + date.format(object.getSemester().getStartDate()) + "-" + date.format(object.getSemester().getEndDate()) + " " + object.getSemester().getSchoolYear().getName();
            }

            @Override
            public ClassesNH fromString(String string) {
                String[] splited = string.split("\\s+");
                Optional<ClassesNH> optClasses = listToComboBox.stream().filter(i -> {
                    Optional<StudentsClassesNH> optSC = i.getStudentsClasses().parallelStream().filter(it -> it.getClasses().getName().equals(splited[0]) && it.getClasses().getSemester().getSchoolYear().getName().equals(splited[3])).findFirst();
                    return i.getName().equals(splited[0]) && optSC.isPresent();
                }).findFirst();
                return optClasses.orElse(null);
            }
        });
        this.classComboBox.valueProperty().addListener((observable, oldValue, newValue) -> {
            Set<GradesNH> grades = this.item.getStudent().getGrades().parallelStream().filter(i -> i.getCarriedSubjects().getClasses().equals(newValue)).collect(Collectors.toCollection(HashSet::new));
            this.items.clear();
            grades.forEach(i -> items.add(new UserGradesTable(i)));
            this.studentGradesTable.setItems(items);
        });
    }

    /**
     * Filling TableView with columns
     */
    private void fillTable() {
        TableColumn<UserGradesTable, String> subjectColumn = new TableColumn<>("Przedmiot");
        subjectColumn.setMinWidth(100);
        subjectColumn.setCellValueFactory(new PropertyValueFactory<>("subjectName"));

        TableColumn<UserGradesTable, String> teacherColumn = new TableColumn<>("Nauczyciel");
        teacherColumn.setMinWidth(200);
        teacherColumn.setCellValueFactory(new PropertyValueFactory<>("teacherNameSurname"));

        TableColumn<UserGradesTable, String> gradesColumn = new TableColumn<>("Oceny");
        gradesColumn.setMaxWidth(100);

        final int MAXGRADESNUMBER = 15;

        for(int i = 0; i < MAXGRADESNUMBER; i++) {
            TableColumn<UserGradesTable, String> tmp = new TableColumn<>(Integer.toString(i+1));
            final int j = i;
            tmp.setCellValueFactory(c -> new SimpleStringProperty(Character.toString(c.getValue().getPartialGrades()[j])));
            gradesColumn.getColumns().add(tmp);
        }

        TableColumn<UserGradesTable, Double> endGradeColumn = new TableColumn<>("Ocena końcowa");
        endGradeColumn.setMinWidth(100);
        endGradeColumn.setCellValueFactory(new PropertyValueFactory<>("endGrade"));

        this.studentGradesTable.getColumns().addAll(subjectColumn, teacherColumn, gradesColumn, endGradeColumn);
        this.studentGradesTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
    }
}
