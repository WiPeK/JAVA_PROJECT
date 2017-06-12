package pl.wipek.client.admin.dialogs;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
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
 * Showing student grades
 */
public class StudentsGradesDialog {

    private UsersNH user;

    private AdminGradesController adminGradesController;

    @FXML
    private Label studentNameSurnameLabel;

    @FXML
    private ComboBox<ClassesNH> classComboBox;

    @FXML
    private TableView<UserGradesTable> studentGradesTable;

    private ObservableList<UserGradesTable> items = FXCollections.observableArrayList();

    public StudentsGradesDialog(UsersNH user, AdminGradesController adminGradesController) {
        this.user = user;
        this.adminGradesController = adminGradesController;
    }

    public void handleWindowShownEvent() {
        this.studentNameSurnameLabel.setText(this.user.getName() + " " + this.user.getSurname());
        this.user.getStudent().setAction(new Action("getStudentsClassesToStudent"));
        this.user.setStudent((StudentsNH)this.adminGradesController.getController().getRelationHelper().getRelated(this.user.getStudent()));

        ObservableList<ClassesNH> listToComboBox = FXCollections.observableArrayList();

        this.user.getStudent().getStudentsClasses().forEach(i -> {
            i.getClasses().setAction(new Action("getSemesterToClass"));
            i.setClasses((ClassesNH)this.adminGradesController.getController().getRelationHelper().getRelated(i.getClasses()));
            i.getClasses().getSemester().setAction(new Action("getYearToSemester"));
            i.getClasses().setSemester((SemestersNH)this.adminGradesController.getController().getRelationHelper().getRelated(i.getClasses().getSemester()));
            listToComboBox.add(i.getClasses());
        });
        DateFormat date = new SimpleDateFormat("dd/MM/yyyy");
        this.user.getStudent().setAction(new Action("getGradesToStudent"));
        this.user.setStudent((StudentsNH) this.adminGradesController.getController().getRelationHelper().getRelated(this.user.getStudent()));

        this.fillTable();

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
            Set<GradesNH> grades = this.user.getStudent().getGrades().parallelStream().filter(i -> i.getCarriedSubjects().getClasses().equals(newValue)).collect(Collectors.toCollection(HashSet::new));
            this.items.clear();
            grades.forEach(i -> items.add(new UserGradesTable(i)));
            this.studentGradesTable.setItems(items);
        });

    }

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
