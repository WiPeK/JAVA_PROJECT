package pl.wipek.client;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import pl.wipek.client.admin.AdminsController;
import pl.wipek.common.Action;
import pl.wipek.db.SchoolYearsNH;
import pl.wipek.db.StudentsClassesNH;
import pl.wipek.db.UsersNH;

import java.util.Set;

/**
 * @author Created by Krzysztof Adamczyk on 18.04.2017.
 * LeftBar is responding for manage left of Controller#rootBorderPane after user logging
 */
public class LeftBar {

    /**
     * template left of Controller#rootBorderPane contains all other elements
     */
    @FXML
    private VBox leftBarVBox;

    /**
     * contains Logo label text
     */
    @FXML
    private AnchorPane logoAnchorPane;

    /**
     * contains logo text
     */
    @FXML
    private Label logoInscriptionLabel;

    /**
     * contains label with user name and surname and logout button
     */
    @FXML
    private AnchorPane loggedAsAnchorPane;

    /**
     * contains user name and surname
     */
    @FXML
    private Label loggedAsValueLabel;

    /**
     * button which user can log out
     */
    @FXML
    private Button loggoutButton;

    /**
     * button which user can show Classifieds in center Controller#rootBorderPane
     */
    @FXML
    private Button classifiedsLeftBarButton;

    /**
     * button which user can show Substitutes in center Controller#rootBorderPane
     */
    @FXML
    private Button substitutesLeftBarButton1;

    /**
     * contains set of students's school years
     */
    private Set<SchoolYearsNH> schoolYears;

    /**
     * contains set of user classes
     */
    private Set<StudentsClassesNH> studentsClasses;

    /**
     * Main Controller
     * @see Controller
     */
    private Controller controller;

    /**
     * @author Krzysztof Adamczyk
     * @param controller
     * initializing LeftBar when user will be logged successfully
     * @see Controller#changeLeftBar()
     */
    LeftBar(Controller controller) {
        this.controller = controller;
    }

    /**
     * @author Krzysztof Adamczyk
     * Call on loggoutButton click
     * Log out user and change left Controller#rootBorderPane to login view
     */
    @FXML
    private void logOut() {
        try{
            Boolean showClassifieds = true;
            if(this.controller.logOut(showClassifieds)) {
                Controller.setUser(null);
                this.controller.changeLeftToLogin();
                Controller.getLogger().info("Log out");
            } else {
                Controller.getLogger().error("Problem with logout");
            }
        }
        catch (Exception e) {
            Controller.getLogger().error(e);
            e.printStackTrace();
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

    /**
     * @author Krzysztof Adamczyk
     * fill loggedAsValueLabel with name and surname of logged user
     * called on initialized LeftBar after changed to LeftBar view after successfully user logging
     * calling property managing method depending on the user role
     */
    @FXML
    public void initialize() throws Exception {
        String loggedUserName = Controller.getUser().getName();
        String loggedUserSurname = Controller.getUser().getSurname();
        this.loggedAsValueLabel.setText(loggedUserName + " " + loggedUserSurname);
        Controller.getUser().setAction(new Action("getStudentToUser"));
        Controller.setUser((UsersNH) this.controller.getRelationHelper().getRelated(Controller.getUser()));
        if(Controller.getUser().getStudent() == null) {
            Controller.getUser().setAction(new Action("getTeacherToUser"));
            Controller.setUser((UsersNH)this.controller.getRelationHelper().getRelated(Controller.getUser()));
            if(Controller.getUser().getTeacher() == null) {
                Controller.getUser().setAction(new Action("getAdminToUser"));
                Controller.setUser((UsersNH)this.controller.getRelationHelper().getRelated(Controller.getUser()));
            }
        }

        if(Controller.getUser().getAdmin() != null) {
            new AdminsController(this.controller).leftBarForAdmin();
        }
        else if(Controller.getUser().getTeacher() != null) {
            new TeachersController(this.controller).leftBarForTeacher();
        }
        else if(Controller.getUser().getStudent() != null) {
            new StudentsController(this.controller).leftBarForStudent();
        }
    }

    public VBox getLeftBarVBox() {
        return leftBarVBox;
    }
}
