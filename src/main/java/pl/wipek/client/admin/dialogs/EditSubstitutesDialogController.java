package pl.wipek.client.admin.dialogs;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import pl.wipek.client.Controller;
import pl.wipek.client.admin.AdminSubstitutesController;
import pl.wipek.common.Action;
import pl.wipek.db.SubstitutesNH;
import pl.wipek.server.db.Substitutes;
import pl.wipek.validators.Validator;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.Optional;
import java.util.Set;

/**
 * @author Krzysztof Adamczyk on 24.05.2017.
 * Managing work with Substitutes entities in dialog running in AdminClassifiedsController
 */
public class EditSubstitutesDialogController extends DialogPane {

    /**
     * @see TextArea
     * Contains value of Substitute body
     */
    @FXML
    private TextArea bodyTextArea;

    /**
     * @see Label
     * Contains name and surname admin who add classified
     */
    @FXML
    private Label addedByLabel;

    /**
     * @see Button
     * Button respond for deleting Substitutes entity
     * on action EditSubstitutesDialogController.deleteSubstitutesButtonAction
     */
    @FXML
    private Button deleteSubstituteButton;

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
     * @see DatePicker
     * Contains date of substitute
     */
    @FXML
    private DatePicker datePicker;

    /**
     * @see SubstitutesNH
     */
    private SubstitutesNH substitutes;

    /**
     * @see AdminSubstitutesController
     */
    private AdminSubstitutesController adminSubstitutesController;

    /**
     * Status managing object
     * true when objects is create
     * false when objects is editing
     */
    private boolean creating;

    public EditSubstitutesDialogController(SubstitutesNH substitutes, AdminSubstitutesController adminSubstitutesController) {
        this.substitutes = substitutes;
        this.adminSubstitutesController = adminSubstitutesController;
    }

    /**
     * Event on EditSubstitutesDialog is showing
     * Setting up components
     */
    @FXML
    public void handleWindowShownEvent() {
        this.creating = this.substitutes.getIdSubstitute() == 0;
        this.addedByLabel.setText(!creating ? "Dodane przez: " + this.substitutes.getAdmin().getUser().getName() + " " + this.substitutes.getAdmin().getUser().getSurname() : "");
        this.deleteSubstituteButton.setDisable(creating);
        this.bodyTextArea.setText(creating ? "" : this.substitutes.getBody());
        Date date = creating ? new Date() : substitutes.getDate();
        LocalDate localDate = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        this.datePicker.setValue(localDate);
        this.deleteSubstituteButton.setOnAction(this::deleteSubstitutesButtonAction);
        this.disableButton.setOnAction(e -> ((Node)(e.getSource())).getScene().getWindow().hide());
        this.saveButton.setOnAction(this::saveButtonAction);
    }

    /**
     * Event on saveButton action
     * Sending request to server for saving or updating Substitutes entity with related sets
     * @param actionEvent ActionEvent
     */
    @FXML
    private void saveButtonAction(ActionEvent actionEvent) {
        if(Validator.validate(this.bodyTextArea.getText(), "minLength:3|maxLength:5000")) {
            LocalDate localDate = this.datePicker.getValue();
            LocalDate now = LocalDate.now();
            if(localDate.compareTo(now) < 0) {
                localDate = now;
            }
            this.substitutes.setDate(Date.from(localDate.atStartOfDay(ZoneId.systemDefault()).toInstant()));
            this.substitutes.setBody(this.bodyTextArea.getText());
            if(this.substitutes.getAdmin() == null) {
                this.substitutes.setAdmin(Controller.getUser().getAdmin());
            }
            this.substitutes.setAction(new Action("saveOrUpdate"));

            SubstitutesNH result = (SubstitutesNH) this.adminSubstitutesController.getController().getClient().requestServer(this.substitutes);
            if(result != null) {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Informacja");
                alert.setHeaderText("Aktualizacja zastępstw");
                alert.setContentText("Wykonywana przez Ciebie akcja zakończona sukcesem!");
                alert.showAndWait();
                Controller.getLogger().info((this.creating ? "Saving Substitutes: " : "Updating Substitutes: ") + result);
                this.adminSubstitutesController.getSubstitutesNHObservableList().removeAll(this.adminSubstitutesController.getSubstitutesNHObservableList());
                this.adminSubstitutesController.getSubstitutesManageTableView().setItems(null);
                Set<Object> substitutesObjects = this.adminSubstitutesController.getController().getRelationHelper().getAllAsSet(new Action("getAllClassifieds", "FROM Substitutes s"));
                substitutesObjects.forEach(i -> this.adminSubstitutesController.getSubstitutesNHObservableList().add(new SubstitutesNH((Substitutes) i)));
                this.adminSubstitutesController.getSubstitutesManageTableView().setItems(this.adminSubstitutesController.getSubstitutesNHObservableList());
                this.adminSubstitutesController.getSubstitutesManageTableView().refresh();
                ((Node)actionEvent.getSource()).getScene().getWindow().hide();
            } else {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Błąd");
                alert.setHeaderText("Problem z aktualizacją zestępstw");
                alert.setContentText("Wystąpił błąd z aktualizacją zastępstw. Spróbuj ponownie.");
                alert.showAndWait();
                Controller.getLogger().info((this.creating ? "Error in saving Substitutes: " : "Error in updating Substitutes: ") + this.substitutes);
            }
        }else {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Błąd wprowadzonych danych!");
            alert.setHeaderText("Niepoprawne dane!");
            alert.setContentText("Treść powinna posiadać od 3 do 5000 znaków\nData musi być datą z przyszłości");
            alert.showAndWait();
        }
    }

    /**
     * Event on deleteButton action
     * Sending request to server for deleting Substitutes entity
     * @param actionEvent ActionEvent
     */
    @FXML
    private void deleteSubstitutesButtonAction(ActionEvent actionEvent) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Usuwanie");
        alert.setHeaderText("Usuwanie zastępstwa");
        alert.setContentText("Czy na pewno chcesz wykonać czynność?");

        Optional<ButtonType> result = alert.showAndWait();
        if(result.isPresent()) {
            this.substitutes.setAction(new Action("remove"));
            Controller.getLogger().info("Deleting substitutes: " + this.substitutes);
            this.adminSubstitutesController.getController().getClient().requestServer(this.substitutes);
            Alert alertInfo = new Alert(Alert.AlertType.INFORMATION);
            this.adminSubstitutesController.getSubstitutesNHObservableList().remove(this.substitutes);
            this.adminSubstitutesController.getSubstitutesManageTableView().refresh();
            alertInfo.setTitle("Informacja");
            alertInfo.setHeaderText("Usuwanie zastępstwa");
            alertInfo.setContentText("Wykonywana przez Ciebie akcja zakończona sukcesem!");
            alertInfo.showAndWait();
            ((Node)actionEvent.getSource()).getScene().getWindow().hide();
        }else{
            actionEvent.consume();
        }
    }
}
