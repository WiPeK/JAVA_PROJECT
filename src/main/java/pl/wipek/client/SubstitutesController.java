package pl.wipek.client;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Separator;
import javafx.scene.layout.VBox;
import pl.wipek.common.Action;
import pl.wipek.db.SubstitutesNH;
import pl.wipek.server.db.Substitutes;

import java.text.SimpleDateFormat;
import java.util.HashSet;
import java.util.Set;

/**
 * @author Created by Krzysztof Adamczyk on 26.04.2017.
 * Managing Substitutes
 */
class SubstitutesController {

    /**
     * title of bar shown on the top
     */
    private final static String barTitle = "ZASTĘPSTWA";

    /**
     * main scrollPane containing all elements
     */
    @FXML
    private ScrollPane centerScrollPane;

    @FXML
    private VBox content = new VBox();

    /**
     * Main Controller
     * @see Controller
     */
    private Controller controller;

    /**
     * @author Krzysztof Adamczyk
     * @param controller
     * initializing SubstitutesController when user click substitutesLeftBarButton1
     * @see Controller#changeCenterToSubstitute()
     */
    SubstitutesController(Controller controller) {
        this.controller = controller;
    }

    /**
     * Method called on shown Main Window and after click button (Zastępstwa) at left bar
     * Downloading All Substitutes from server
     * @see pl.wipek.server.db.HibernateUtil
     * then fill Controller#rootBorderPane with data
     * @see LeftBar#showSubstitutesLeftBarButton()
     * @see Login#showSubstitutesLeftBarButton()
     */
    @FXML
    void showSubstitutes() {
        Platform.runLater(() -> {
            this.content.getStyleClass().add("classifiedvbox");
            this.centerScrollPane.setContent(this.content);

            Label labelTitle = new Label(barTitle);
            labelTitle.getStyleClass().add("labeltitle");
            this.content.getChildren().add(labelTitle);

            Set<Object> substitutesObjects = this.controller.getRelationHelper().getAllAsSet(new Action("getAllSubstitutes", "FROM Substitutes s ORDER BY s.date DESC"));
            Set<SubstitutesNH> substitutes = new HashSet<>(0);
            substitutesObjects.forEach(i -> substitutes.add(new SubstitutesNH((Substitutes)i)));
            if(!substitutes.isEmpty()) {
                try{
                    for(SubstitutesNH clsf: substitutes) {
                        VBox vBox = new VBox();
                        Label date = new Label(new SimpleDateFormat("dd-MM-yyyy").format(clsf.getDate()));
                        date.getStyleClass().add("datelabel");
                        Label body = new Label(clsf.getBody());
                        body.getStyleClass().add("classifiedsbody");
                        body.setMaxWidth(this.content.getWidth()-30);
                        body.setWrapText(true);

                        clsf.setAction(new Action("getAdminToSubstitutes"));
                        clsf = (SubstitutesNH) this.controller.getRelationHelper().getRelated(clsf);

                        Label adminLabel = new Label(clsf.getAdmin().getUser().getName() + " " + clsf.getAdmin().getUser().getSurname());
                        adminLabel.getStyleClass().add("classifiedsID");
                        Separator separator = new Separator();
                        vBox.getChildren().addAll(date, body, adminLabel, separator);
                        this.content.getChildren().add(vBox);
                    }
                }catch (Exception e) {
                    Controller.getLogger().error(e);
                    e.printStackTrace();
                }
            }
        });
    }
}
