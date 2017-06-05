package pl.wipek.client;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
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
class StudentsController {

    private Controller controller;

    private Set<SchoolYearsNH> schoolYears = new HashSet<>(0);

    /**
     * contains dynamically added buttons on left bar accorded to school year and semesters
     */
    private Map<String, ButtonHelper> buttonHelpers = new HashMap<>(0);

    StudentsController(Controller controller) {
        this.controller = controller;
    }

    /**
     * @author Krzysztof Adamczyk
     * call when logged user role is "student" and fill left bar propertly
     */
    @FXML
    void leftBarForStudent() {
        Controller.getUser().getStudent().setAction(new Action("getStudentsClassesToStudent"));
        Controller.getUser().setStudent((StudentsNH) this.controller.getRelationHelper().getRelated(Controller.getUser().getStudent()));
        Set<StudentsClassesNH> studentsClasses1 = Controller.getUser().getStudent().getStudentsClasses();

        for(StudentsClassesNH stCl: studentsClasses1) {
            stCl.setAction(new Action("getClassToStudentsClasses"));
            stCl = (StudentsClassesNH) this.controller.getRelationHelper().getRelated(stCl);
            stCl.getClasses().setAction(new Action("getSemesterToClass"));
            stCl.setClasses((ClassesNH) this.controller.getRelationHelper().getRelated(stCl.getClasses()));
            stCl.getClasses().getSemester().setAction(new Action("getYearToSemester"));
            stCl.getClasses().setSemester((SemestersNH) this.controller.getRelationHelper().getRelated(stCl.getClasses().getSemester()));
            schoolYears.add(stCl.getClasses().getSemester().getSchoolYear());
        }
        for(SchoolYearsNH schYrs: this.schoolYears) {
            Label labelYear = new Label(schYrs.getName());
            labelYear.getStyleClass().add("yearlabel");
            this.controller.getLeftBar().getLeftBarVBox().getChildren().add(labelYear);

            schYrs.setAction(new Action("getSemestersToYear"));
            schYrs = (SchoolYearsNH) this.controller.getRelationHelper().getRelated(schYrs);
            Set<SemestersNH> semesters = schYrs.getSemesters();
            int idSem = 2;
            for(SemestersNH sems: semesters) {
                //check student was learning in semester, normally student should learn in 2 semesters in one class
                Optional<StudentsClassesNH> studentsClasses = Controller.getUser().getStudent().getStudentsClasses().stream().filter(item -> item.getClasses().getSemester().getIdSemester() == sems.getIdSemester()).findAny();

                if(studentsClasses.isPresent()) {
                    Button semButton = new Button("Semestr " + idSem);
                    String buttonId = "sem" + idSem + "Button" + schYrs.getIdSchoolYear();

                    ButtonHelper buttonHelper = new ButtonHelper();
                    buttonHelper.setButtonId(buttonId);
                    buttonHelper.setIdYear(schYrs.getIdSchoolYear());
                    buttonHelper.setIdSemester(idSem);
                    buttonHelper.setOriginalIdSemester(sems.getIdSemester());
                    this.buttonHelpers.put(buttonId, buttonHelper);

                    semButton.setId(buttonId);
                    semButton.getStyleClass().add("semesterbutton");
                    semButton.setMinWidth(270);
                    semButton.setOnAction(this::semesterButtonAction);
                    this.controller.getLeftBar().getLeftBarVBox().getChildren().add(semButton);
                    idSem--;
                }

            }
        }
    }

    /**
     * @author Krszysztof Adamczyk
     * action after click semester button
     * showing table with grades
     */
    @FXML
    private void semesterButtonAction(ActionEvent event) {
        String buttonId = ((Button)event.getSource()).getId();
        ButtonHelper buttonHelper = this.buttonHelpers.get(buttonId);
        if(buttonHelper != null) {
            try {
                VBox vBox = new VBox();

                Optional<SchoolYearsNH> optional = this.schoolYears.stream().filter(item -> item.getIdSchoolYear() == buttonHelper.getIdYear()).findFirst();
                String topLabelContent = optional.get().getName();
                Optional<StudentsClassesNH> studentsClasses = Controller.getUser().getStudent().getStudentsClasses().stream().filter(item -> item.getClasses().getSemester().getIdSemester() == buttonHelper.getOriginalIdSemester()).findFirst();
                topLabelContent += " - semestr " + buttonHelper.getIdSemester() + " " + studentsClasses.get().getClasses().getName();
                Label labelSemesterView = new Label(topLabelContent);
                labelSemesterView.getStyleClass().add("labelsemesterview");

                Controller.getUser().getStudent().setAction(new Action("getGradesToStudent"));
                Controller.getUser().setStudent((StudentsNH) this.controller.getRelationHelper().getRelated(Controller.getUser().getStudent()));

                Set<GradesNH> grades = Controller.getUser().getStudent().getGrades()
                        .stream().filter(i -> i.getCarriedSubjects().getSemester().getIdSemester() == buttonHelper.getOriginalIdSemester())
                        .collect(Collectors.toSet());



                TableView<UserGradesTable> gradesTable = this.getGradesTable(grades);

                ScrollPane scrollPane = new ScrollPane();
                scrollPane.setContent(gradesTable);

                DecimalFormat decimalFormat = new DecimalFormat("0.00");
                Double semesterAverage = grades.stream().mapToDouble(GradesNH::getEndGrade).average().getAsDouble();
                Label averageSemester = new Label("Średnia ocen: " + decimalFormat.format(semesterAverage));

                if(buttonHelper.getIdSemester() == 2) {
                    OptionalDouble avg = Controller.getUser().getStudent().getGrades()
                            .stream().filter(i -> i.getCarriedSubjects().getSemester().getIdSemester() == buttonHelper.getOriginalIdSemester()-1)
                            .collect(Collectors.toSet())
                            .stream().mapToDouble(GradesNH::getEndGrade).average();

                    Double firstSemesterAverage = avg.isPresent() ? avg.getAsDouble() : 0;

                    Double yearAverage;
                    if(firstSemesterAverage == 0) {
                        yearAverage = semesterAverage;
                    }
                    else {
                        yearAverage = (semesterAverage + firstSemesterAverage)/2;
                    }
                    Label yearAverageLabel = new Label("Średnia roczna: " + decimalFormat.format(yearAverage));
                    vBox.getChildren().addAll(labelSemesterView, scrollPane, averageSemester, yearAverageLabel);
                }
                else {
                    vBox.getChildren().addAll(labelSemesterView, scrollPane, averageSemester);
                }

                this.controller.getRootBorderPane().setCenter(vBox);

            }catch (Exception e) {
                Controller.getLogger().error(e);
                e.printStackTrace();
            }
        }
    }

    /**
     * creating unmodifiedable table with student grades
     * @param grades student grades in semester
     * @return TableView UserGradesTable
     * @see UserGradesTable
     */
    @FXML
    private TableView<UserGradesTable> getGradesTable(Set<GradesNH> grades) {
        ObservableList<UserGradesTable> userGradesTables = FXCollections.observableArrayList();
        grades.forEach((i) -> {
            userGradesTables.add(new UserGradesTable(i));
        });

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

        TableView<UserGradesTable> table = new TableView<>();
        table.setItems(userGradesTables);
        table.getColumns().addAll(subjectColumn, teacherColumn, gradesColumn, endGradeColumn);
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        return table;
    }
}
