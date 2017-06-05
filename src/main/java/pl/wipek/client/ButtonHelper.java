package pl.wipek.client;

import pl.wipek.db.CarriedSubjectsNH;

/**
 * @author Krszysztof Adamczyk
 * Helping with managing dinamically created buttons
 */
class ButtonHelper {

    /**
     * button id
     */
    private String buttonId;

    /**
     * id school years associated with button
     */
    private int idYear;

    /**
     * id semester associated with button
     */
    private int idSemester;

    /**
     * contains semester id from databases
     */
    private int originalIdSemester;

    /**
     * contains carrieds subjects using to identified clicked button
     */
    private CarriedSubjectsNH carriedSubjects;

    /**
     * get value of id school years associated with button
     * @return id school years associated with button
     */
    public int getIdYear() {
        return idYear;
    }

    /**
     * set value of id school year associated with button
     * @param idYear school year id
     */
    public void setIdYear(int idYear) {
        this.idYear = idYear;
    }

    /**
     * get value of id semester associated with button
     * @return id semester associated with button
     */
    public int getIdSemester() {
        return idSemester;
    }

    /**
     * set value of id semester associated with button
     * @param idSemester semester id
     */
    public void setIdSemester(int idSemester) {
        this.idSemester = idSemester;
    }

    /**
     * get value if button id
     * @return String with id button
     */
    public String getButtonId() {
        return buttonId;
    }

    /**
     * set value of button id
     * @param buttonId String with button id
     */
    public void setButtonId(String buttonId) {
        this.buttonId = buttonId;
    }

    /**
     * get value of id semester from database
     * @return int
     */
    public int getOriginalIdSemester() {
        return originalIdSemester;
    }

    /**
     * set value of semester id from database
     * @param originalIdSemester int with semester id from database
     */
    public void setOriginalIdSemester(int originalIdSemester) {
        this.originalIdSemester = originalIdSemester;
    }

    @Override
    public String toString() {
        return "ButtonHelper{" +
                "buttonId='" + buttonId + '\'' +
                ", idYear=" + idYear +
                ", idSemester=" + idSemester +
                ", originalIdSemester=" + originalIdSemester +
                '}';
    }

    /**
     * get value of carriedSubject object
     * @return Carried Subject
     */
    public CarriedSubjectsNH getCarriedSubjects() {
        return carriedSubjects;
    }

    /**
     * set value of carried subject object
     * @param carriedSubjects Carried subject object
     */
    public void setCarriedSubjects(CarriedSubjectsNH carriedSubjects) {
        this.carriedSubjects = carriedSubjects;
    }
}
