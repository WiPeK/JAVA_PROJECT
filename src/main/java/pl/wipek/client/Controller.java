package pl.wipek.client;

import pl.wipek.helpers.Config;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import pl.wipek.common.Action;
import pl.wipek.db.UsersNH;

import java.util.ArrayList;
import java.util.Optional;

/**
 * @author Krzysztof Adamczyk
 * Main Controller is responding for manage between all Controllers which manage parts of GUI
 */
public class Controller {

    /**
     * path to FXML with login view
     */
    private final static String loginFXMLPath = "/views/loginPanel.fxml";

    /**
     * path to FXML with center blank template
     */
    private final static String blankCenterFXMLPath = "/views/blankCenter.fxml";


    private final static String leftBarFXMLPath = "/views/leftBar.fxml";

    /**
     * Main template of GUI
     */
    @FXML
    private BorderPane rootBorderPane;

    /**
     * @see Client
     */
    private Client client;

    /**
     * Contains logged user data
     * @see UsersNH
     */
    private static UsersNH user;

    /**
     * Log4j2 logger is responding for log events to files
     */
    private static Logger logger = LogManager.getRootLogger();


    /**
     * Contains readed config file
     * @see Config#readConfig()
     */
    private ArrayList<String> config = new ArrayList<>(0);

    /**
     * @see LeftBar
     */
    private LeftBar leftBar;

    /**
     * @see ClassifiedsController
     */
    private ClassifiedsController classifiedsController;

    /**
     * @see Login
     */
    private Login login;

    /**
     * @see SubstitutesController
     */
    private SubstitutesController substitutesController;

    /**
     * @see RelationHelper
     */
    private RelationHelper relationHelper;

    /**
     * @author Krzysztof Adamczyk
     * @see Main#start(Stage)
     * method called before Main windom will be shown
     * must be used instead of Constructor and initialize because here we can use FXML objects which now are initialized
     * responding for reading config file
     * initializing Client object with data from readed file
     * loading login view
     * loading center view
     * fill by default center bar with Classifieds
     */
    @FXML
    void handleWindowShownEvent() {
        try{
//            this.config = new Config().readConfig();
            this.config.add("127.0.0.1");
            this.config.add("54321");
//            if(config.isEmpty()) {
//                this.config.add("127.0.0.1");
//                this.config.add("54321");
//                logger.info("Config file empty");
//            }
            this.client = new Client(this.config.get(0), Integer.parseInt(this.config.get(1)));
            this.relationHelper = new RelationHelper(this.client);

            FXMLLoader leftLoader = new FXMLLoader(getClass().getResource(loginFXMLPath));
            this.login = new Login(this);
            leftLoader.setController(this.login);
            this.rootBorderPane.setLeft(leftLoader.load());

            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource(blankCenterFXMLPath));
            this.classifiedsController = new ClassifiedsController(this);
            fxmlLoader.setController(this.classifiedsController);
            this.rootBorderPane.setCenter(fxmlLoader.load());
            new Thread(() -> this.classifiedsController.showClassifieds()).start();
        }catch (Exception e) {
            logger.error(e);
            e.printStackTrace();
        }
    }

    /**
     * @author Krzysztof Adamczyk
     * @see Main#start(Stage)
     * Method called on Main window is clossing
     * Logged out user
     * closing connection with server
     */
    @FXML
    void exitApplication(WindowEvent event) {
        try{
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("WYJŚCIE");
            alert.setHeaderText("Zamknięcie aplikacji");
            alert.setContentText("Czy na pewno chcesz zamknąć okno?");

            Optional<ButtonType> result = alert.showAndWait();
            if(result.isPresent()) {
                if(result.get() == ButtonType.OK) {
                    if(user != null) {
                        Boolean showClassifieds = false;
                        if(this.logOut(showClassifieds)) {
                            logger.info("Log out");
                        } else {
                            logger.info("Log out error");
                        }
                    }
                    Action action = new Action("EXIT");
                    this.client.requestServer(action);
                    this.client.closeClient();
                    logger.info("Close app");
                    Platform.exit();
                } else {
                    event.consume();
                }
            }
        }
        catch (Exception e) {
            logger.error(e);
            e.printStackTrace();
        }
    }

    /**
     * @author Krzysztof Adamczyk
     * @see Login#actionLoginButton()
     * @param email user email from emailTextField
     * @param password user password from passwordPasswordField
     * @return Boolean true when User authentication in server will be successfully otherwise return false
     */
    boolean tryLogIn(String email, String password) {
        UsersNH auth = new UsersNH();
        auth.setEmail(email);
        auth.setPassword(password);
        auth.setAction(new Action("login"));
        Object request = this.client.requestServer(auth);
        if(request instanceof UsersNH) {
            user = (UsersNH) request;
            logger.info("Login success");
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Logowanie");
            alert.setHeaderText("Status logowania");
            alert.setContentText("Zalogowano pomyślnie!");
            alert.showAndWait();
            return true;
        } else {
            return false;
        }
    }

    /**
     * @author Krzysztof Adamczyk
     * @see LeftBar#logOut()
     * @see Controller#exitApplication(WindowEvent)
     * @return Boolean true when User will be successfully logged out from server otherwise return false
     */
    boolean logOut(Boolean showClassifieds) throws Exception {
        user.setAction(new Action("logOut"));
        Object request = this.client.requestServer(user);
        this.rootBorderPane.setCenter(null);
        FXMLLoader leftLoader = new FXMLLoader(getClass().getResource(loginFXMLPath));
        leftLoader.setController(this.login);
        this.rootBorderPane.setLeft(leftLoader.load());
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource(blankCenterFXMLPath));
        fxmlLoader.setController(this.classifiedsController);
        this.rootBorderPane.setCenter(fxmlLoader.load());
        if(showClassifieds) {
            this.classifiedsController.showClassifieds();
        }
        user = null;
        return (Boolean) request;
    }

    /**
     * @author Krzysztof Adamczyk
     * @see Login#actionLoginButton()
     * Method change left of rootBorderPane to leftBar view after successfully user logging
     */
    @FXML
    void changeLeftBar() {
        logger.info("Success login");
        try{
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource(leftBarFXMLPath));
            this.leftBar = new LeftBar(this);
            fxmlLoader.setController(this.leftBar);
            this.rootBorderPane.setLeft(fxmlLoader.load());
        }catch (Exception e) {
            logger.error(e);
            e.printStackTrace();
        }
    }

    /**
     * @author Krzysztof Adamczyk
     * @see LeftBar#logOut()
     * Method change left of rootBorderPane to login view after successfully user logged out
     */
    @FXML
    void changeLeftToLogin() {
        logger.info("Change to login view");
        try{
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource(loginFXMLPath));
            fxmlLoader.setController(this.login);
            this.rootBorderPane.setLeft(fxmlLoader.load());
        }catch (Exception e) {
            logger.error(e);
            e.printStackTrace();
        }
    }

    /**
     * @author Krzysztof Adamczyk
     * @param command contains String of action to execute in server
     * @return Object of Entity which contains all owned records from database
     */
    Object getAll(String command) {
        return this.client.requestServer(new Action(command));
    }

    /**
     * @author Krzysztof Adamczyk
     * @see Login#showSubstitutesLeftBarButton()
     * @see LeftBar#showSubstitutesLeftBarButton()
     * Changing center of rootBorderPane to Substitutes view
     */
    @FXML
    void changeCenterToSubstitute() {
        try{
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource(blankCenterFXMLPath));
            this.substitutesController = new SubstitutesController(this);
            fxmlLoader.setController(this.substitutesController);
            this.rootBorderPane.setCenter(fxmlLoader.load());
        }catch (Exception e) {
            logger.error(e);
            e.printStackTrace();
        }
    }

    /**
     * @author Krzysztof Adamczyk
     * @see Login#showClassifiedsLeftBarButton()
     * @see LeftBar#showClassifiedsLeftBarButton()
     * Changing center of rootBorderPane to Classifieds view
     */
    @FXML
    void changeCenterToClassifieds() {
        try{
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource(blankCenterFXMLPath));
            fxmlLoader.setController(this.classifiedsController);
            this.rootBorderPane.setCenter(null);
            this.rootBorderPane.setCenter(fxmlLoader.load());
        }catch (Exception e) {
            logger.error(e);
            e.printStackTrace();
        }
    }

    /**
     * get Client object for connect in db package
     * @return Client
     */
    public Client getClient() {
        return this.client;
    }

    public BorderPane getRootBorderPane() {
        return rootBorderPane;
    }

    public LeftBar getLeftBar() {
        return leftBar;
    }

    public RelationHelper getRelationHelper() {
        return relationHelper;
    }

    public static Logger getLogger() {
        return logger;
    }

    public static UsersNH getUser() {
        return user;
    }

    ClassifiedsController getClassifiedsController() {
        return classifiedsController;
    }

    SubstitutesController getSubstitutesController() {
        return substitutesController;
    }

    public static void setUser(UsersNH user) {
        Controller.user = user;
    }
}
