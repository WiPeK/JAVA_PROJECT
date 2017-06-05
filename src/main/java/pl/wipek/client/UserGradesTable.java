package pl.wipek.client;

/**
 * Created by Krszysztof Adamczyk on 30.04.2017.
 */

import pl.wipek.db.GradesNH;
import pl.wipek.db.PartialGradesNH;

import java.util.Arrays;
import java.util.Set;

/**
 * @author Krszysztof Adamczyk
 * Containing template for data to table of user grades
 */
public class UserGradesTable {

    /**
     * contains name of subject
     */
    private String subjectName;

    /**
     * contains name and surname teacher who was teached subject
     */
    private String teacherNameSurname;

    /**
     * contains subject's grades
     */
    private char[] partialGrades = {'\0', '\0', '\0', '\0', '\0', '\0', '\0', '\0', '\0', '\0', '\0', '\0', '\0', '\0', '\0'};

    /**
     * contains subject's end grade
     */
    private double endGrade;

    /**
     * contains students grades
     * @see GradesNH
     */
    private GradesNH grades;

    /**
     * initializing fields values
     * @param grades Grades object
     */
    public UserGradesTable(GradesNH grades) {
        this.grades = grades;
        this.subjectName = grades.getCarriedSubjects().getSubject().getName();
        this.teacherNameSurname = grades.getCarriedSubjects().getTeacher().getUser().getName() + " " + grades.getCarriedSubjects().getTeacher().getUser().getSurname();
        Set<PartialGradesNH> pg = grades.getPartialGrades();
        int i = 0;
        for(PartialGradesNH partGr: pg) {
            this.partialGrades[i] = Integer.toString (partGr.getGrade().intValue()).charAt(0);
            i++;
        }
        this.endGrade = grades.getEndGrade() == null ? 0 : grades.getEndGrade() ;
    }

    /**
     * get value of subject Name
     * @return String with subject name
     */
    public String getSubjectName() {
        return subjectName;
    }

    /**
     * get value of teacher name and surname
     * @return String with teacher name and surname
     */
    public String getTeacherNameSurname() {
        return teacherNameSurname;
    }

    /**
     * get char array with grades
     * @return char array
     */
    public char[] getPartialGrades() {
        return partialGrades;
    }

    /**
     * get value of end grade
     * @return double value
     */
    public double getEndGrade() {
        return endGrade;
    }

    @Override
    public String toString() {
        return "UserGradesTable{" +
                "subjectName='" + subjectName + '\'' +
                ", teacherNameSurname='" + teacherNameSurname + '\'' +
                ", partialGrades=" + Arrays.toString(partialGrades) +
                ", endGrade=" + endGrade +
                ", grades=" + grades +
                '}';
    }
}