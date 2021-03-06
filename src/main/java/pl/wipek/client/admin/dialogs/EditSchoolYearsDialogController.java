package pl.wipek.client.admin.dialogs;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import pl.wipek.client.Controller;
import pl.wipek.client.admin.AdminSchoolYearsController;
import pl.wipek.common.Action;
import pl.wipek.db.SchoolYearsNH;
import pl.wipek.db.SemestersNH;
import pl.wipek.server.db.SchoolYears;
import pl.wipek.server.db.Semesters;
import pl.wipek.validators.Validator;

import java.time.LocalDate;
import java.util.Optional;

/**
 * @author Krzysztof Adamczyk on 28.05.2017.
 * Managing work with school years and semesters
 */
public class EditSchoolYearsDialogController extends DialogAbstractController<SchoolYearsNH> {

    /**
     * @see Label
     * Contains state: editing or creating state and year name if editing
     */
    @FXML
    private Label yearHeadLabel;

    /**
     * @see TextField
     * Input which contains editable value with school year name
     */
    @FXML
    private TextField yearNameTextField;

    /**
     * @see DatePicker
     * Contains date of school year start
     */
    @FXML
    private DatePicker startDatePicker;

    /**
     * @see DatePicker
     * Contains date of school year end
     */
    @FXML
    private DatePicker endDatePicker;

    /**
     * @see DatePicker
     * Contains date of first semester start
     */
    @FXML
    private DatePicker semStartDatePicker;

    /**
     * @see DatePicker
     * Contains date of first semester end
     */
    @FXML
    private DatePicker semEndDatePicker;

    /**
     * @see DatePicker
     * Contains date of second semester start
     */
    @FXML
    private DatePicker semTStartDatePicker;

    /**
     * @see DatePicker
     * Cpntains date of second semester end
     */
    @FXML
    private DatePicker semTEndDatePicker;

    public EditSchoolYearsDialogController(SchoolYearsNH schoolYears, AdminSchoolYearsController adminSchoolYearsController) {
        super(schoolYears, adminSchoolYearsController);
    }

    /**
     * Event on EditSchoolYearsDialog is showing
     * Setting up components
     */
    @FXML
    public void handleWindowShownEvent() {
        this.creating = this.item.getIdSchoolYear() == 0;
        this.yearHeadLabel.setText(creating ? "Nowy rok" : "Edycja " + this.item.getName());
        this.deleteButton.setDisable(creating);
        this.yearNameTextField.setText(creating ? "" : this.item.getName());
        this.startDatePicker.setValue(creating ? LocalDate.now() : this.item.getStartDateAsLocalDate());
        this.endDatePicker.setValue(creating ? LocalDate.now() : this.item.getEndDateAsLocalDate());
        SemestersNH[] semesters = new SemestersNH[2];
        int it = 0;
        for (SemestersNH sem: this.item.getSemesters()) {
            semesters[it++] = sem;
        }
        this.semStartDatePicker.setValue(creating ? LocalDate.now() : semesters[0].getStartDateAsLocalDate());
        this.semEndDatePicker.setValue(creating ? LocalDate.now() : semesters[0].getEndDateAsLocalDate());
        this.semTStartDatePicker.setValue(creating ? LocalDate.now() : semesters[1].getStartDateAsLocalDate());
        this.semTEndDatePicker.setValue(creating ? LocalDate.now() : semesters[0].getEndDateAsLocalDate());
        this.disableButton.setOnAction(e -> ((Node)(e.getSource())).getScene().getWindow().hide());
        this.deleteButton.setOnAction(this::deleteButtonAction);
        this.saveButton.setOnAction(this::saveButtonAction);
    }

    /**
     * Event on saveButton action
     * Validate inputs values
     * Sending request to server for saving or updating Classifieds entity with related sets
     * @param event ActionEvent
     */
    protected void saveButtonAction(ActionEvent event) {
        if(Validator.validate(this.yearNameTextField.getText(), "minLength:2|maxLength:50")
                && this.isEndDateAfterStartDate(this.startDatePicker.getValue(), this.endDatePicker.getValue())
                && this.isEndDateAfterStartDate(this.semStartDatePicker.getValue(), this.semEndDatePicker.getValue())
                && this.isEndDateAfterStartDate(this.semTStartDatePicker.getValue(), this.semTEndDatePicker.getValue())) {

            SchoolYears schoolYears = new SchoolYears();
            schoolYears.setIdSchoolYear(this.item.getIdSchoolYear());
            schoolYears.setName(this.yearNameTextField.getText());
            schoolYears.setStartDateFromLocalDate(this.startDatePicker.getValue());
            schoolYears.setEndDateFromLocalDate(this.endDatePicker.getValue());
            Semesters firstSemesters = new Semesters();
            firstSemesters.setStartDateFromLocalDate(this.semStartDatePicker.getValue());
            firstSemesters.setEndDateFromLocalDate(this.semEndDatePicker.getValue());
            firstSemesters.setSchoolYear(schoolYears);
            Semesters secondSemesters = new Semesters();
            secondSemesters.setStartDateFromLocalDate(this.semTStartDatePicker.getValue());
            secondSemesters.setEndDateFromLocalDate(this.semTEndDatePicker.getValue());
            secondSemesters.setSchoolYear(schoolYears);
            schoolYears.getSemesters().add(firstSemesters);
            schoolYears.getSemesters().add(secondSemesters);

            schoolYears.setAction(new Action("saveOrUpdate"));
            SchoolYears result = (SchoolYears)this.adminController.getController().getClient().requestServer(schoolYears);
            if(result != null) {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Informacja");
                alert.setHeaderText("Aktualizacja roczników");
                alert.setContentText("Wykonywana przez Ciebie akcja zakończona sukcesem!");
                alert.showAndWait();
                Controller.getLogger().info((this.creating ? "Saving SchoolYear: " : "Updating SchoolYear: ") + result);
                ((Node)event.getSource()).getScene().getWindow().hide();
                this.adminController.manageButtonAction(new ActionEvent());
            } else {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Błąd");
                alert.setHeaderText("Problem z aktualizacją roku");
                alert.setContentText("Wystąpił błąd z aktualizacją roku. Spróbuj ponownie.");
                alert.showAndWait();
                Controller.getLogger().info((this.creating ? "Error in saving SchoolYear: " : "Error in updating SchoolYear: ") + this.item);
            }
        } else {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Błąd wprowadzonych danych!");
            alert.setHeaderText("Niepoprawne dane!");
            alert.setContentText("Nazwa roku: 2-50 liter\nData zakończenia powinna być po dacie rozpoczęcia");
            alert.showAndWait();
        }
    }

    /**
     * Event on deleteButton action
     * Sending request to server for deleting SchoolYear entity
     * @param event ActionEvent
     */
    protected void deleteButtonAction(ActionEvent event) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Usuwanie");
        alert.setHeaderText("Usuwanie rocznika " + this.item.getName());
        alert.setContentText("Czy na pewno chcesz wykonać czynność?");

        Optional<ButtonType> result = alert.showAndWait();
        if(result.isPresent()) {
            if (result.get() == ButtonType.OK){
                this.item.setAction(new Action("remove"));
                this.adminController.getController().getClient().requestServer(this.item);
                this.adminController.getObservableList().remove(this.item);
                this.adminController.getTableView().refresh();
                Alert alertInfo = new Alert(Alert.AlertType.INFORMATION);
                alertInfo.setTitle("Informacja");
                alertInfo.setHeaderText("Usuwanie rocznika");
                alertInfo.setContentText("Wykonywana przez Ciebie akcja zakończona sukcesem!");
                alertInfo.showAndWait();
                Controller.getLogger().info("Deleting classifieds: " + this.item);
                ((Node)event.getSource()).getScene().getWindow().hide();
            } else {
                event.consume();
            }
        }
    }

    /**
     * Validate are end date is after start date
     * @param start start date
     * @param end end date
     * @return boolean true if end date is after start date otherwise false
     */
    private boolean isEndDateAfterStartDate(LocalDate start, LocalDate end) {
        return end.isAfter(start);
    }
}
