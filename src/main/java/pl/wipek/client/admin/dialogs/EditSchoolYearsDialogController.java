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
 * Created by Krszysztof Adamczyk on 28.05.2017.
 */
public class EditSchoolYearsDialogController {

    private SchoolYearsNH schoolYears;

    private AdminSchoolYearsController adminSchoolYearsController;

    @FXML
    private Label yearHeadLabel;

    @FXML
    private TextField yearNameTextField;

    @FXML
    private DatePicker startDatePicker;

    @FXML
    private DatePicker endDatePicker;

    @FXML
    private DatePicker semStartDatePicker;

    @FXML
    private DatePicker semEndDatePicker;

    @FXML
    private DatePicker semTStartDatePicker;

    @FXML
    private DatePicker semTEndDatePicker;

    @FXML
    private Button deleteButton;

    @FXML
    private Button disableButton;

    @FXML
    private Button saveButton;

    public EditSchoolYearsDialogController(SchoolYearsNH schoolYears, AdminSchoolYearsController adminSchoolYearsController) {
        this.schoolYears = schoolYears;
        this.adminSchoolYearsController = adminSchoolYearsController;
    }

    @FXML
    public void handleWindowShownEvent() {
        boolean creating = this.schoolYears.getIdSchoolYear() == 0;
        this.yearHeadLabel.setText(creating ? "Nowy rok" : "Edycja " + this.schoolYears.getName());
        this.deleteButton.setDisable(creating);
        this.yearNameTextField.setText(creating ? "" : this.schoolYears.getName());
        this.startDatePicker.setValue(creating ? LocalDate.now() : this.schoolYears.getStartDateAsLocalDate());
        this.endDatePicker.setValue(creating ? LocalDate.now() : this.schoolYears.getEndDateAsLocalDate());
        SemestersNH[] semesters = new SemestersNH[2];
        int it = 0;
        for (SemestersNH sem: this.schoolYears.getSemesters()) {
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

    private void saveButtonAction(ActionEvent event) {
        if(Validator.validate(this.yearNameTextField.getText(), "minLength:2|maxLength:50")
                && this.isEndDateAfterStartDate(this.startDatePicker.getValue(), this.endDatePicker.getValue())
                && this.isEndDateAfterStartDate(this.semStartDatePicker.getValue(), this.semEndDatePicker.getValue())
                && this.isEndDateAfterStartDate(this.semTStartDatePicker.getValue(), this.semTEndDatePicker.getValue())) {

            SchoolYears schoolYears = new SchoolYears();
            schoolYears.setIdSchoolYear(this.schoolYears.getIdSchoolYear());
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
            SchoolYears result = (SchoolYears)this.adminSchoolYearsController.getController().getClient().requestServer(schoolYears);
            if(result != null) {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Informacja");
                alert.setHeaderText("Aktualizacja roczników");
                alert.setContentText("Wykonywana przez Ciebie akcja zakończona sukcesem!");
                alert.showAndWait();
                Controller.getLogger().info("Edytowano lub dodano rocznik");
                ((Node)event.getSource()).getScene().getWindow().hide();
                this.adminSchoolYearsController.buttonManageSchoolYears(new ActionEvent());
            } else {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Błąd");
                alert.setHeaderText("Problem z aktualizacją roku");
                alert.setContentText("Wystąpił błąd z aktualizacją roku. Spróbuj ponownie.");
                alert.showAndWait();
            }
        } else {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Błąd wprowadzonych danych!");
            alert.setHeaderText("Niepoprawne dane!");
            alert.setContentText("Nazwa roku: 2-50 liter\nData zakończenia powinna być po dacie rozpoczęcia");
            alert.showAndWait();
        }
    }

    private void deleteButtonAction(ActionEvent event) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Usuwanie");
        alert.setHeaderText("Usuwanie rocznika " + this.schoolYears.getName());
        alert.setContentText("Czy na pewno chcesz wykonać czynność?");

        Optional<ButtonType> result = alert.showAndWait();
        if(result.isPresent()) {
            if (result.get() == ButtonType.OK){
                this.schoolYears.setAction(new Action("remove"));
                this.adminSchoolYearsController.getController().getClient().requestServer(this.schoolYears);
                this.adminSchoolYearsController.getSchYrsNHObservableList().remove(this.schoolYears);
                this.adminSchoolYearsController.getSchYrsManageTableView().refresh();
                Alert alertInfo = new Alert(Alert.AlertType.INFORMATION);
                alertInfo.setTitle("Informacja");
                alertInfo.setHeaderText("Usuwanie rocznika");
                alertInfo.setContentText("Wykonywana przez Ciebie akcja zakończona sukcesem!");
                alertInfo.showAndWait();
                Controller.getLogger().info("Usunięto rok");
                ((Node)event.getSource()).getScene().getWindow().hide();
            } else {
                event.consume();
            }
        }
    }

    private boolean isEndDateAfterStartDate(LocalDate start, LocalDate end) {
        return end.isAfter(start);
    }
}
