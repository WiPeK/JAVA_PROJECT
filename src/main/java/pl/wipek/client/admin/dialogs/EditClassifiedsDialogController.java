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
public class EditClassifiedsDialogController extends DialogPane {

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

    /**
     * @see Button
     * Button respondend for deleting Classified entity
     * on action EditClassifiedsDialogController.deleteClassifiedsButtonAction
     */
    @FXML
    private Button deleteClassifiedsButton;

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
     * @see ClassifiedsNH
     */
    private ClassifiedsNH classifieds;

    /**
     * @see AdminClassifiedsController
     */
    private AdminClassifiedsController adminClassifiedsController;

    /**
     * Status managing object
     * true when objects is create
     * false when objects is editing
     */
    private boolean creating;

    public EditClassifiedsDialogController(ClassifiedsNH classifieds, AdminClassifiedsController adminClassifiedsController) throws Exception {
        this.classifieds = classifieds;
        this.adminClassifiedsController = adminClassifiedsController;
    }

    /**
     * Event on EditClassifiedsDialog is showing
     * Setting up components
     */
    @FXML
    public void handleWindowShownEvent() {
        this.creating = this.classifieds.getIdClassifieds() == 0;
        this.addedByLabel.setText(!creating ? "Dodane przez: " + this.classifieds.getAdmin().getUser().getName() + " " + this.classifieds.getAdmin().getUser().getSurname() : "");
        this.deleteClassifiedsButton.setDisable(creating);
        this.bodyTextArea.setText(creating ? "" : this.classifieds.getBody());
        this.deleteClassifiedsButton.setOnAction(this::deleteClassifiedsButtonAction);
        this.disableButton.setOnAction(e -> ((Node)(e.getSource())).getScene().getWindow().hide());
        this.saveButton.setOnAction(this::saveButtonAction);
    }

    /**
     * Event on saveButton action
     * Sending request to server for saving or updating Classifieds entity with related sets
     * @param actionEvent ActionEvent
     */
    @FXML
    private void saveButtonAction(ActionEvent actionEvent) {
        if(Validator.validate(this.bodyTextArea.getText(), "minLength:3|maxLength:5000")) {
            this.classifieds.setBody(this.bodyTextArea.getText());
            if(this.classifieds.getAdmin() == null) {
                this.classifieds.setAdmin(Controller.getUser().getAdmin());
            }
            this.classifieds.setAction(new Action("saveOrUpdate"));

            ClassifiedsNH result = (ClassifiedsNH) this.adminClassifiedsController.getController().getClient().requestServer(this.classifieds);
            if(result != null) {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Informacja");
                alert.setHeaderText("Aktualizacja ogłoszeń");
                alert.setContentText("Wykonywana przez Ciebie akcja zakończona sukcesem!");
                alert.showAndWait();
                Controller.getLogger().info((this.creating ? "Saving Classifieds: " : "Updating Classifieds: ") + result);
                this.adminClassifiedsController.getClassifiedsNHObservableList().removeAll(this.adminClassifiedsController.getClassifiedsNHObservableList());
                this.adminClassifiedsController.getClassifiedsManageTableView().setItems(null);
                Set<Object> classifiedsObjects = this.adminClassifiedsController.getController().getRelationHelper().getAllAsSet(new Action("getAllClassifieds", "FROM Classifieds c ORDER BY idClassifieds DESC"));
                classifiedsObjects.forEach(i -> this.adminClassifiedsController.getClassifiedsNHObservableList().add(new ClassifiedsNH((Classifieds)i)));
                this.adminClassifiedsController.getClassifiedsManageTableView().setItems(this.adminClassifiedsController.getClassifiedsNHObservableList());
                this.adminClassifiedsController.getClassifiedsManageTableView().refresh();
                ((Node)actionEvent.getSource()).getScene().getWindow().hide();
            } else {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Błąd");
                alert.setHeaderText("Problem z aktualizacją ogłoszeń");
                alert.setContentText("Wystąpił błąd z aktualizacją ogłoszeń. Spróbuj ponownie.");
                alert.showAndWait();
                Controller.getLogger().info((this.creating ? "Error in saving Classifieds: " : "Error in updating Classifieds: ") + this.classifieds);
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
     * Sending request to server for deleting Classifieds entity
     * @param actionEvent ActionEvent
     */
    @FXML
    private void deleteClassifiedsButtonAction(ActionEvent actionEvent) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Usuwanie");
        alert.setHeaderText("Usuwanie ogłoszenia");
        alert.setContentText("Czy na pewno chcesz wykonać czynność?");

        Optional<ButtonType> result = alert.showAndWait();
        if(result.isPresent()) {
            this.classifieds.setAction(new Action("remove"));
            Controller.getLogger().info("Deleting classifieds: " + this.classifieds);
            this.adminClassifiedsController.getController().getClient().requestServer(this.classifieds);
            Alert alertInfo = new Alert(Alert.AlertType.INFORMATION);
            this.adminClassifiedsController.getClassifiedsNHObservableList().remove(this.classifieds);
            this.adminClassifiedsController.getClassifiedsManageTableView().refresh();
            alertInfo.setTitle("Informacja");
            alertInfo.setHeaderText("Usuwanie ogłoszenia");
            alertInfo.setContentText("Wykonywana przez Ciebie akcja zakończona sukcesem!");
            alertInfo.showAndWait();
            ((Node)actionEvent.getSource()).getScene().getWindow().hide();
        }else{
            actionEvent.consume();
        }
    }
}
