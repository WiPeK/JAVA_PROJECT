package pl.wipek.client.admin.dialogs;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import pl.wipek.client.admin.AdminsAbstractController;

/**
 * @author Krzysztof Adamczyk on 14.06.2017.
 */
public abstract class DialogAbstractController <T> {

    protected T item;

    protected AdminsAbstractController<T> adminController;

    /**
     * @see Button
     * Button is hiding dialog
     */
    @FXML
    protected Button disableButton;

    /**
     * @see Button
     * On action calling method which validate inputs data end save or update entity
     */
    @FXML
    protected Button saveButton;

    /**
     * @see Button
     * Button respond for deleting entity
     */
    @FXML
    protected Button deleteButton;

    /**
     * Status managing object
     * true when objects is create
     * false when objects is editing
     */
    protected boolean creating;

    public DialogAbstractController(T item, AdminsAbstractController<T> adminController) {
        this.item = item;
        this.adminController = adminController;
    }

    public void setItem(T item) {
        this.item = item;
    }

    public void setAdminController(AdminsAbstractController<T> adminController) {
        this.adminController = adminController;
    }

    /**
     * Event on dialog is showing
     * Setting up components
     */
    public abstract void handleWindowShownEvent();

    /**
     * Event on saveButton action
     * Sending request to server for saving or updating entity with related sets
     * @param event ActionEvent
     */
    @FXML
    protected abstract void saveButtonAction(ActionEvent event);

    /**
     * Event on deleteButton action
     * Sending request to server for deleting entity with related sets
     * @param event ActionEvent
     */
    @FXML
    protected abstract void deleteButtonAction(ActionEvent event);
}
