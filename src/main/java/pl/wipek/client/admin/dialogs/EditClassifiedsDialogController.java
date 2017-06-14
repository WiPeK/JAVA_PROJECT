package pl.wipek.client.admin.dialogs;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import pl.wipek.client.Controller;
import pl.wipek.client.admin.AdminClassifiedsController;
import pl.wipek.common.Action;
import pl.wipek.db.ClassifiedsNH;
import pl.wipek.server.db.Classifieds;
import pl.wipek.validators.Validator;

import java.util.Optional;
import java.util.Set;

/**
 * @author Created by Krzysztof Adamczyk on 24.05.2017.
 * Managing work with Classifieds entities
 */
public final class EditClassifiedsDialogController extends DialogAbstractController<ClassifiedsNH> {

    /**
     * @see TextArea
     * Contains classifieds body
     */
    @FXML
    private TextArea bodyTextArea;

    /**
     * @see Label
     * Contains name and surname admin who add classified
     */
    @FXML
    private Label addedByLabel;

    public EditClassifiedsDialogController(ClassifiedsNH classifieds, AdminClassifiedsController adminClassifiedsController) {
        super(classifieds, adminClassifiedsController);
    }

    /**
     * Event on EditClassifiedsDialog is showing
     * Setting up components
     */
    @FXML
    public void handleWindowShownEvent() {
        this.creating = this.item.getIdClassifieds() == 0;
        this.addedByLabel.setText(!creating ? "Dodane przez: " + this.item.getAdmin().getUser().getName() + " " + this.item.getAdmin().getUser().getSurname() : "");
        this.deleteButton.setDisable(creating);
        this.bodyTextArea.setText(creating ? "" : this.item.getBody());
        this.deleteButton.setOnAction(this::deleteButtonAction);
        this.disableButton.setOnAction(e -> ((Node)(e.getSource())).getScene().getWindow().hide());
        this.saveButton.setOnAction(this::saveButtonAction);
    }

    /**
     * Event on saveButton action
     * Sending request to server for saving or updating Classifieds entity with related sets
     * @param actionEvent ActionEvent
     */
    @FXML
    protected void saveButtonAction(ActionEvent actionEvent) {
        if(Validator.validate(this.bodyTextArea.getText(), "minLength:3|maxLength:5000")) {
            this.item.setBody(this.bodyTextArea.getText());
            if(this.item.getAdmin() == null) {
                this.item.setAdmin(Controller.getUser().getAdmin());
            }
            this.item.setAction(new Action("saveOrUpdate"));

            ClassifiedsNH result = (ClassifiedsNH) this.adminController.getController().getClient().requestServer(this.item);
            if(result != null) {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Informacja");
                alert.setHeaderText("Aktualizacja ogłoszeń");
                alert.setContentText("Wykonywana przez Ciebie akcja zakończona sukcesem!");
                alert.showAndWait();
                Controller.getLogger().info((this.creating ? "Saving Classifieds: " : "Updating Classifieds: ") + result);
                this.adminController.getObservableList().clear();
                this.adminController.getTableView().setItems(null);
                Set<Object> classifiedsObjects = this.adminController.getController().getRelationHelper().getAllAsSet(new Action("getAllClassifieds", "FROM Classifieds c ORDER BY idClassifieds DESC"));
                classifiedsObjects.forEach(i -> this.adminController.getObservableList().add(new ClassifiedsNH((Classifieds)i)));
                this.adminController.getTableView().setItems(this.adminController.getObservableList());
                this.adminController.getTableView().refresh();
                ((Node)actionEvent.getSource()).getScene().getWindow().hide();
            } else {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Błąd");
                alert.setHeaderText("Problem z aktualizacją ogłoszeń");
                alert.setContentText("Wystąpił błąd z aktualizacją ogłoszeń. Spróbuj ponownie.");
                alert.showAndWait();
                Controller.getLogger().info((this.creating ? "Error in saving Classifieds: " : "Error in updating Classifieds: ") + this.item);
            }
        }else {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Błąd wprowadzonych danych!");
            alert.setHeaderText("Niepoprawne dane!");
            alert.setContentText("Treść powinna posiadać od 3 do 5000 znaków");
            alert.showAndWait();
        }
    }

    /**
     * Event on deleteButton action
     * Sending request to server for deleting entity with related sets
     *
     * @param event ActionEvent
     */
    @Override
    protected void deleteButtonAction(ActionEvent event) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Usuwanie");
        alert.setHeaderText("Usuwanie ogłoszenia");
        alert.setContentText("Czy na pewno chcesz wykonać czynność?");

        Optional<ButtonType> result = alert.showAndWait();
        if(result.isPresent()) {
            this.item.setAction(new Action("remove"));
            Controller.getLogger().info("Deleting classifieds: " + this.item);
            this.adminController.getController().getClient().requestServer(this.item);
            Alert alertInfo = new Alert(Alert.AlertType.INFORMATION);
            this.adminController.getObservableList().remove(this.item);
            this.adminController.getTableView().refresh();
            alertInfo.setTitle("Informacja");
            alertInfo.setHeaderText("Usuwanie ogłoszenia");
            alertInfo.setContentText("Wykonywana przez Ciebie akcja zakończona sukcesem!");
            alertInfo.showAndWait();
            ((Node)event.getSource()).getScene().getWindow().hide();
        }else{
            event.consume();
        }
    }
}
