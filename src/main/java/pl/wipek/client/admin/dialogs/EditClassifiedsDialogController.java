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
 * @author Created by Krszysztof Adamczyk on 24.05.2017.
 */
public class EditClassifiedsDialogController extends DialogPane {

    @FXML
    private TextArea bodyTextArea;

    @FXML
    private Label addedByLabel;

    @FXML
    private Button deleteClassifiedsButton;

    @FXML
    private Button disableButton;

    @FXML
    private Button saveButton;

    private ClassifiedsNH classifieds;

    private AdminClassifiedsController adminClassifiedsController;

    public EditClassifiedsDialogController(ClassifiedsNH classifieds, AdminClassifiedsController adminClassifiedsController) throws Exception {
        this.classifieds = classifieds;
        this.adminClassifiedsController = adminClassifiedsController;
    }

    @FXML
    public void handleWindowShownEvent() {
        boolean creating = this.classifieds.getIdClassifieds() == 0;
        this.addedByLabel.setText(!creating ? "Dodane przez: " + this.classifieds.getAdmin().getUser().getName() + " " + this.classifieds.getAdmin().getUser().getSurname() : "");
        this.deleteClassifiedsButton.setDisable(creating);
        this.bodyTextArea.setText(creating ? "" : this.classifieds.getBody());
        this.deleteClassifiedsButton.setOnAction(this::deleteClassifiedsButtonAction);
        this.disableButton.setOnAction(e -> ((Node)(e.getSource())).getScene().getWindow().hide());
        this.saveButton.setOnAction(this::saveButtonAction);
    }

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
            }
        }else {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Błąd wprowadzonych danych!");
            alert.setHeaderText("Niepoprawne dane!");
            alert.setContentText("Treść powinna posiadać od 3 do 5000 znaków");
            alert.showAndWait();
        }
    }

    @FXML
    private void deleteClassifiedsButtonAction(ActionEvent actionEvent) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Usuwanie");
        alert.setHeaderText("Usuwanie ogłoszenia");
        alert.setContentText("Czy na pewno chcesz wykonać czynność?");

        Optional<ButtonType> result = alert.showAndWait();
        if(result.isPresent()) {
            this.classifieds.setAction(new Action("remove"));
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
