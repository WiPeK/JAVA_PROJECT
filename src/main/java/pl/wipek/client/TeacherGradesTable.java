package pl.wipek.client;

import pl.wipek.common.Action;
import pl.wipek.db.GradesNH;
import pl.wipek.db.PartialGradesNH;
import pl.wipek.db.UsersNH;

import java.util.Date;

/**
 * @author Created by Krszysztof Adamczyk on 02.05.2017.
 */
public class TeacherGradesTable {

    /**
     * grade object
     */
    private GradesNH grade;

    /**
     * contains student name and surname
     */
    private String studentNameSurname;

    /**
     * contains subject's grades
     */
    private char[] partialGrades = {'\0', '\0', '\0', '\0', '\0', '\0', '\0', '\0', '\0', '\0', '\0', '\0', '\0', '\0', '\0'};

    /**
     * contains subject's end grade
     */
    private double endGrade;

    /**
     * get student's name and surname
     * @return String
     */
    public String getStudentNameSurname() {
        return studentNameSurname;
    }

    /**
     * get value of char array with student's grades in subject
     * @return char array
     */
    char[] getPartialGrades() {
        return partialGrades;
    }

    /**
     * get end grade value
     * @return double
     */
    public double getEndGrade() {
        return endGrade;
    }

    TeacherGradesTable(GradesNH grades) {
        UsersNH user = grades.getStudent().getUser();
        this.grade = grades;
        this.studentNameSurname = user.getName() + " " + user.getSurname();
        this.endGrade = grades.getEndGrade() == null ? 0 : grades.getEndGrade();
        int i = 0;
        for(PartialGradesNH partialGrade: grades.getPartialGrades()) {
            this.partialGrades[i] = Integer.toString(partialGrade.getGrade().intValue()).charAt(0);
            i++;
        }
    }

    void updateEndGrade(Double newValue, Client client) {
        this.grade.setEndGrade(newValue);
        this.endGrade = newValue;
        this.grade.setAction(new Action("update"));
        client.requestServer(this.grade);
    }

    public void setEndGrade(double endGrade) {
        this.endGrade = endGrade;
    }

    void updatePartialGrade(int partialGradeIndex, String newValue, Client client) {
        Object[] partialGradesArray = this.grade.getPartialGrades().toArray();
        PartialGradesNH partialGradesNH = partialGradeIndex >= partialGradesArray.length ? null : (PartialGradesNH)partialGradesArray[partialGradeIndex];
        if(newValue.equals("brak")) {
            partialGradesNH.setAction(new Action("remove"));
            client.requestServer(partialGradesNH);
            this.grade.getPartialGrades().remove(partialGradesNH);
            this.partialGrades[partialGradeIndex] = '\0';
        }else{
            if(partialGradesNH == null) {
                partialGradesNH = new PartialGradesNH();
                partialGradesNH.setGrades(this.grade);
                partialGradesNH.setAction(new Action("save"));
                this.grade.getPartialGrades().add(partialGradesNH);
                this.partialGrades[partialGradeIndex] = newValue.charAt(0);
            }
            else {
                partialGradesNH.setAction(new Action("update"));
            }

            partialGradesNH.setGrade(Double.parseDouble(newValue));
            partialGradesNH.setDate(new Date());
            client.requestServer(partialGradesNH);
        }
    }
}
