package pl.wipek.client;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import pl.wipek.validators.Validator;


/**
 * Created by Krzysztof Adamczyk on 14.04.2017.
 * Controller is responding for managing user loging
 */
public class Login {

    /**
     * template left of Controller#rootBorderPane contains all other elements
     */
    @FXML private VBox loginVBox;

    /**
     * TextField to typing user email necessary to logging
     */
    @FXML private TextField emailTextField;

    /**
     * PasswordField to typing user password necessary to logging
     */
    @FXML private PasswordField passwordPasswordField;

    /**
     * Button which is use to user logging when email and password are valid
     * by default is disabled
     */
    @FXML private Button loginButton;

    /**
     * label to show errors with logging
     */
    @FXML private Label errorLoginLabel;

    /**
     * @see Controller
     */
    private Controller controller;

    /**
     * @author Krzysztof Adamczyk
     * @param controller
     * initializing on application start
     * @see Controller#handleWindowShownEvent()
     */
    public Login(Controller controller) {
        this.controller = controller;
    }

    /**
     * @author Krzysztof Adamczyk
     * action on key pressed in emailTextField and passwordPasswordField
     * it is checking values of inputs and validate
     * when emailTextField and passwordPasswordField are valids then try set enable loginButton
     * shows errors in errorLoginLabel when login inputs are invalid
     */
    @FXML
    private void waitForValidEmailAndPassword() {
        if(!Validator.validate(this.emailTextField.getText(), "minLength:4|maxLen:255|isValidEmail") || !Validator.validate(this.passwordPasswordField.getText(), "minLength:4|maxLen:50")) {
            this.errorLoginLabel.setText("Wpisz poprawny email i hasło!");
        } else {
            this.errorLoginLabel.setText("");
            this.tryEnableLoginButton();
        }
    }

    /**
     * @author Krzysztof Adamczyk
     * by default loginButton is disabled, when loginTextBox and passwordTextBox will be valid then loginButton will be enabled
     * when loginButton yet is disabled enable it
     */
    @FXML
    private void tryEnableLoginButton() {
        if(this.loginButton.isDisabled()) {
            this.loginButton.setDisable(false);
        }
    }

    /**
     * @author Krzysztof Adamczyk
     * action called on loginButton
     */
    @FXML
    private void actionLoginButton() {
        if(Validator.validate(this.emailTextField.getText(), "minLength:4|maxLen:255|isValidEmail") && Validator.validate(this.passwordPasswordField.getText(), "minLength:4|maxLen:50")) {
            if(this.controller.tryLogIn(this.emailTextField.getText(), this.passwordPasswordField.getText())) {
                this.controller.changeLeftBar();
            } else {
                this.errorLoginLabel.setText("Logowanie nieudane!");
                Controller.getLogger().info("Logowanie odrzucone przez serwer");
            }
        } else {
            this.errorLoginLabel.setText("Wpisz poprawny email i hasło!");
        }
    }

    /**
     * @author Krzysztof Adamczyk
     * @see Controller#changeCenterToClassifieds()
     * @see ClassifiedsController#showClassifieds()
     * call on classifiedsLeftBarButton click
     * change center Controller#rootBorderPane to Classifieds view and fill it with data
     */
    @FXML
    private void showClassifiedsLeftBarButton() {
        Platform.runLater(() -> {
            this.controller.changeCenterToClassifieds();
            this.controller.getClassifiedsController().showClassifieds();
        });
    }

    /**
     * @author Krzysztof Adamczyk
     * @see Controller#changeCenterToSubstitute()
     * @see SubstitutesController#showSubstitutes()
     * call on substitutesLeftBarButton1 click
     * change center Controller#rootBorderPane to Substitutes view and fill it with data
     */
    @FXML
    private void showSubstitutesLeftBarButton() {
        Platform.runLater(() -> {
            this.controller.changeCenterToSubstitute();
            this.controller.getSubstitutesController().showSubstitutes();
        });
    }

}
