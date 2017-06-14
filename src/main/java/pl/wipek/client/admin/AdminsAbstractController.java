package pl.wipek.client.admin;


import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.TableView;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import pl.wipek.client.Controller;
import pl.wipek.client.admin.dialogs.DialogAbstractController;

/**
 * @author Krzysztof Adamczyk on 14.06.2017.
 * Abstract class to admin controllers
 */
public abstract class AdminsAbstractController<T> {

    /**
     * @see AdminsController
     */
    protected AdminsController adminsController;

    /**
     * @see TableView
     */
    protected TableView<T> tableView;

    /**
     * @see ObservableList
     * Contains items to table view
     */
    protected ObservableList<T> observableList = FXCollections.observableArrayList();

    /**
     * Contains path to fxml file with view
     */
    protected String fxmlPath;

    /**
     * @see DialogAbstractController
     */
    protected DialogAbstractController<T> dialogAbstractController;

    public AdminsAbstractController(AdminsController adminsController) {
        this.adminsController = adminsController;
    }

    /**
     * Event on manage button click
     * Setting up center of Controller rootBorderPane
     * @param event ActionEvent button click
     */
    public abstract void manageButtonAction(ActionEvent event);

    /**
     * Creating main table with items
     * @return TableView
     */
    protected abstract TableView<T> getTable();

    /**
     * Event on tableView row click
     * @param item object from table row
     */
    protected void tableRowClickAction(T item) {
        try{
            FXMLLoader loader = new FXMLLoader(getClass().getResource(this.fxmlPath));
            this.dialogAbstractController.setItem(item);
            this.dialogAbstractController.setAdminController(this);
            loader.setController(this.dialogAbstractController);
            Stage stage = new Stage();
            stage.setScene(new Scene(loader.load()));
            stage.addEventHandler(WindowEvent.WINDOW_SHOWN, (e) -> Platform.runLater(dialogAbstractController::handleWindowShownEvent));
            stage.show();
        }catch (Exception e) {
            Controller.getLogger().error(e);
            e.printStackTrace();
        }
    }

    public void setDialogAbstractController(DialogAbstractController<T> dialogAbstractController) {
        this.dialogAbstractController = dialogAbstractController;
    }

    /**
     * @see Controller
     * Return Controller Object
     * @return Controller
     */
    public Controller getController() {
        return this.adminsController.getController();
    }

    /**
     * Return table view
     * @return TableView
     */
    public TableView<T> getTableView() {
        return tableView;
    }

    /**
     * Return Observable list with table items
     * @return ObservableList
     */
    public ObservableList<T> getObservableList() {
        return observableList;
    }
}
