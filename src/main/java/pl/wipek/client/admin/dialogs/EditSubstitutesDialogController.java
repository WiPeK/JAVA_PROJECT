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
public final class EditSubstitutesDialogController extends DialogAbstractController<SubstitutesNH> {

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
     * @see DatePicker
     * Contains date of substitute
     */
    @FXML
    private DatePicker datePicker;

    public EditSubstitutesDialogController(SubstitutesNH substitutes, AdminSubstitutesController adminSubstitutesController) {
       super(substitutes, adminSubstitutesController);
    }

    /**
     * Event on EditSubstitutesDialog is showing
     * Setting up components
     */
    @FXML
    public void handleWindowShownEvent() {
        this.creating = this.item.getIdSubstitute() == 0;
        this.addedByLabel.setText(!creating ? "Dodane przez: " + this.item.getAdmin().getUser().getName() + " " + this.item.getAdmin().getUser().getSurname() : "");
        this.deleteButton.setDisable(creating);
        this.bodyTextArea.setText(creating ? "" : this.item.getBody());
        Date date = creating ? new Date() : item.getDate();
        LocalDate localDate = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        this.datePicker.setValue(localDate);
        this.deleteButton.setOnAction(this::deleteButtonAction);
        this.disableButton.setOnAction(e -> ((Node)(e.getSource())).getScene().getWindow().hide());
        this.saveButton.setOnAction(this::saveButtonAction);
    }

    /**
     * Event on saveButton action
     * Sending request to server for saving or updating Substitutes entity with related sets
     * @param actionEvent ActionEvent
     */
    @FXML
    protected void saveButtonAction(ActionEvent actionEvent) {
        if(Validator.validate(this.bodyTextArea.getText(), "minLength:3|maxLength:5000")) {
            LocalDate localDate = this.datePicker.getValue();
            LocalDate now = LocalDate.now();
            if(localDate.compareTo(now) < 0) {
                localDate = now;
            }
            this.item.setDate(Date.from(localDate.atStartOfDay(ZoneId.systemDefault()).toInstant()));
            this.item.setBody(this.bodyTextArea.getText());
            if(this.item.getAdmin() == null) {
                this.item.setAdmin(Controller.getUser().getAdmin());
            }
            this.item.setAction(new Action("saveOrUpdate"));

            SubstitutesNH result = (SubstitutesNH) this.adminController.getController().getClient().requestServer(this.item);
            if(result != null) {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Informacja");
                alert.setHeaderText("Aktualizacja zastępstw");
                alert.setContentText("Wykonywana przez Ciebie akcja zakończona sukcesem!");
                alert.showAndWait();
                Controller.getLogger().info((this.creating ? "Saving Substitutes: " : "Updating Substitutes: ") + result);
                this.adminController.getObservableList().removeAll(this.adminController.getObservableList());
                this.adminController.getTableView().setItems(null);
                Set<Object> substitutesObjects = this.adminController.getController().getRelationHelper().getAllAsSet(new Action("getAllClassifieds", "FROM Substitutes s"));
                substitutesObjects.forEach(i -> this.adminController.getObservableList().add(new SubstitutesNH((Substitutes) i)));
                this.adminController.getTableView().setItems(this.adminController.getObservableList());
                this.adminController.getTableView().refresh();
                ((Node)actionEvent.getSource()).getScene().getWindow().hide();
            } else {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Błąd");
                alert.setHeaderText("Problem z aktualizacją zestępstw");
                alert.setContentText("Wystąpił błąd z aktualizacją zastępstw. Spróbuj ponownie.");
                alert.showAndWait();
                Controller.getLogger().info((this.creating ? "Error in saving Substitutes: " : "Error in updating Substitutes: ") + this.item);
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
     * Sending request to server for deleting Users entity with related sets
     *
     * @param event ActionEvent
     */
    @Override
    protected void deleteButtonAction(ActionEvent event) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Usuwanie");
        alert.setHeaderText("Usuwanie zastępstwa");
        alert.setContentText("Czy na pewno chcesz wykonać czynność?");

        Optional<ButtonType> result = alert.showAndWait();
        if(result.isPresent()) {
            this.item.setAction(new Action("remove"));
            Controller.getLogger().info("Deleting substitutes: " + this.item);
            this.adminController.getController().getClient().requestServer(this.item);
            Alert alertInfo = new Alert(Alert.AlertType.INFORMATION);
            this.adminController.getObservableList().remove(this.item);
            this.adminController.getTableView().refresh();
            alertInfo.setTitle("Informacja");
            alertInfo.setHeaderText("Usuwanie zastępstwa");
            alertInfo.setContentText("Wykonywana przez Ciebie akcja zakończona sukcesem!");
            alertInfo.showAndWait();
            ((Node)event.getSource()).getScene().getWindow().hide();
        }else{
            event.consume();
        }
    }

}
