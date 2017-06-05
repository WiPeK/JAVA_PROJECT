package pl.wipek.client;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.ComboBoxTableCell;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;
import pl.wipek.common.Action;
import pl.wipek.db.*;

import java.text.DecimalFormat;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author Created by Krszysztof Adamczyk on 14.05.2017.
 */
class TeachersController {

    private Controller controller;

    /**
     * contains dynamically added buttons on left bar accorded to school year and semesters
     */
    private Map<String, ButtonHelper> buttonHelpers = new HashMap<>(0);

    TeachersController(Controller controller) {
        this.controller = controller;
    }

    /**
     * @author Krzysztof Adamczyk
     * managing left bar for logged teacher
     * creating hierarchy:
     * school year
     *  -semesters
     *  --subject
     *  ---class
     *  on clicked class button teacher can insert/edit grades if semester is yet not closed
     */
    @FXML
    void leftBarForTeacher() {
        ScrollPane scrollPane = new ScrollPane();
        VBox vBox = new VBox();

        Set<SchoolYearsNH> schoolYears = new HashSet<>(0);
        Set<SemestersNH> semesters = new HashSet<>(0);
        Set<SubjectsNH> subjects = new HashSet<>(0);
        Set<ClassesNH> classes = new HashSet<>(0);
        Controller.getUser().getTeacher().setAction(new Action("getCarriedToTeacher"));
        Controller.getUser().setTeacher((TeachersNH) this.controller.getRelationHelper().getRelated(Controller.getUser().getTeacher()));
        Set<CarriedSubjectsNH> carriedSubjects = Controller.getUser().getTeacher().getCarriedSubjects();
        for(CarriedSubjectsNH carriedSubjectsNH: carriedSubjects) {
            semesters.add(carriedSubjectsNH.getSemester());
            schoolYears.add(carriedSubjectsNH.getSemester().getSchoolYear());
            subjects.add(carriedSubjectsNH.getSubject());
            classes.add(carriedSubjectsNH.getClasses());
        }

        schoolYears = schoolYears.stream().sorted(Comparator.comparing(SchoolYearsNH::getEndDate)).collect(Collectors.toSet());

        for(SchoolYearsNH schYrs: schoolYears) {
            Label labelYear = new Label(schYrs.getName());
            labelYear.getStyleClass().add("yearlabel");
            vBox.getChildren().add(labelYear);

            int idSem = 2;
            for(SemestersNH semester: semesters) {
                if(Objects.equals(semester.getSchoolYear().getIdSchoolYear(), schYrs.getIdSchoolYear())) {
                    Label semesterLabel = new Label("Semestr " + idSem);
                    vBox.getChildren().add(semesterLabel);

                    for(SubjectsNH subject: subjects) {
                        Optional<CarriedSubjectsNH> carriedSubjectOptional = carriedSubjects.stream()
                                .filter(item -> item.getSemester().getIdSemester() == semester.getIdSemester() && item.getSubject().getIdSubject() == subject.getIdSubject() && item.getTeacher().getUser().equals(Controller.getUser()))
                                .findFirst();
                        if(carriedSubjectOptional.isPresent()) {
                            CarriedSubjectsNH carriedSubject = carriedSubjectOptional.get();
                            Label subjectLabel = new Label(subject.getName());
                            vBox.getChildren().add(subjectLabel);
                            Set<ClassesNH> classesInSemester = classes.stream()
                                    .filter(item -> item.getIdClass() == carriedSubject.getClasses().getIdClass()).collect(Collectors.toSet());
                            for(ClassesNH classIterator: classesInSemester) {
                                Button classButton = new Button(classIterator.getName());
                                vBox.getChildren().add(classButton);
                                String buttonId = "Button" + new Random().nextInt();

                                ButtonHelper buttonHelper = new ButtonHelper();
                                buttonHelper.setButtonId(buttonId);
                                buttonHelper.setCarriedSubjects(carriedSubject);
                                buttonHelper.setIdSemester(idSem);
                                this.buttonHelpers.put(buttonId, buttonHelper);

                                classButton.setId(buttonId);
                                classButton.getStyleClass().add("classbutton");
                                classButton.setMinWidth(270);
                                classButton.setOnAction(this::classButtonAction);
                            }
                        }
                    }
                    idSem--;
                }
                vBox.getChildren().add(new Separator());
            }
        }
        scrollPane.setContent(vBox);
        this.controller.getLeftBar().getLeftBarVBox().getChildren().add(scrollPane);
    }

    /**
     * @author Krzysztof Adamczyk
     * @param event event
     * Action on clicked class button in teacher left bar
     * showing table with students and grades
     */
    @FXML
    private void classButtonAction(ActionEvent event) {
        //todo replace it to center controller
        String buttonId = ((Button)event.getSource()).getId();
        ButtonHelper buttonHelper = this.buttonHelpers.get(buttonId);
        if(buttonHelper != null) {
            try {
                VBox vBox = new VBox();

                CarriedSubjectsNH carriedSubjects = buttonHelper.getCarriedSubjects();
                String topLabelContent = carriedSubjects.getSemester().getSchoolYear().getName() + " - ";
                topLabelContent += "Semestr " + buttonHelper.getIdSemester() + " - ";
                topLabelContent += carriedSubjects.getSubject().getName() + " - ";
                topLabelContent += carriedSubjects.getClasses().getName();

                Label labelTop = new Label(topLabelContent);
                labelTop.getStyleClass().add("labelsemesterview");

                ScrollPane scrollPane = new ScrollPane();
                Set<GradesNH> grades = carriedSubjects.getGrades();
                //todo sorting users by surname
                TableView<TeacherGradesTable> teacherGradesTableTable = this.getTeacherTable(grades);
                teacherGradesTableTable.setPrefHeight(630);

                Date now = new Date();
                if(carriedSubjects.getSemester().getEndDate().compareTo(now) >= 0) {
                    teacherGradesTableTable.setEditable(true);
                }

                Double classAverage = grades.parallelStream().mapToDouble(GradesNH::getEndGrade).average().getAsDouble();

                DecimalFormat decimalFormat = new DecimalFormat("0.00");
                Label labelBottom = new Label("Średnia klasy: " + decimalFormat.format(classAverage));
                labelBottom.getStyleClass().add("labelsemesterview");
                scrollPane.setContent(teacherGradesTableTable);

                vBox.getChildren().addAll(labelTop, scrollPane, labelBottom);

                this.controller.getRootBorderPane().setCenter(vBox);
            }
            catch (Exception e) {
                Controller.getLogger().error(e);
                e.printStackTrace();
            }
        }
    }

    /**
     * @author Krzysztof Adamczyk
     * creating table for students, grades in class for subject in semester and school year
     * @param grades set with grades for all students in class
     * @return Teacher TableView
     */
    @FXML
    private TableView<TeacherGradesTable> getTeacherTable(Set<GradesNH> grades) {
        ObservableList<TeacherGradesTable> teacherGradesTables = FXCollections.observableArrayList();
        grades.forEach(i -> teacherGradesTables.add(new TeacherGradesTable(i)));

        TableView<TeacherGradesTable> table = new TableView<>();

        TableColumn<TeacherGradesTable, String> studentColumn = new TableColumn<>("Uczeń");
        studentColumn.setMinWidth(150);
        studentColumn.setCellValueFactory(new PropertyValueFactory<>("studentNameSurname"));

        TableColumn<TeacherGradesTable, String> gradesColumn = new TableColumn<>("Oceny");
        gradesColumn.setMaxWidth(100);

        final int MAXGRADESNUMBER = 15;

        for(int i = 0; i < MAXGRADESNUMBER; i++) {
            TableColumn<TeacherGradesTable, String> tmp = new TableColumn<>(Integer.toString(i+1));
            ObservableList<String> observableList = FXCollections.observableArrayList();
            observableList.addAll("brak","1","2","3","4","5","6");
            tmp.setCellFactory(ComboBoxTableCell.forTableColumn(observableList));
            int finalI = i;
            tmp.setOnEditCommit(event -> {
                TeacherGradesTable editedPartialGrade = event.getTableView().getItems().get(event.getTablePosition().getRow());
                editedPartialGrade.updatePartialGrade(finalI, event.getNewValue(), this.controller.getClient());
                table.refresh();
            });

            final int j = i;
            tmp.setCellValueFactory(c -> new SimpleStringProperty(Character.toString(c.getValue().getPartialGrades()[j])));
            gradesColumn.getColumns().add(tmp);
        }

        TableColumn<TeacherGradesTable, Double> endGradeColumn = new TableColumn<>("Ocena końcowa");
        endGradeColumn.setMinWidth(100);
        endGradeColumn.setCellValueFactory(new PropertyValueFactory<>("endGrade"));

        ObservableList<Double> observableList = FXCollections.observableArrayList();
        observableList.addAll(1.0, 2.0, 2.5, 3.0, 3.5, 4.0, 4.5, 5.0, 5.5, 6.0);
        endGradeColumn.setCellFactory(ComboBoxTableCell.forTableColumn(observableList));

        endGradeColumn.setOnEditCommit(event -> {
            TeacherGradesTable editedEndGrade = event.getTableView().getItems().get(event.getTablePosition().getRow());
            editedEndGrade.updateEndGrade(event.getNewValue() ,this.controller.getClient());
            table.refresh();
        });


        table.setItems(teacherGradesTables);
        table.getColumns().addAll(studentColumn, gradesColumn, endGradeColumn);
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        return table;
    }
}
