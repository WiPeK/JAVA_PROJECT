package pl.wipek.client;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Separator;
import javafx.scene.layout.VBox;
import pl.wipek.common.Action;
import pl.wipek.db.ClassifiedsNH;
import pl.wipek.server.db.Classifieds;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by Krzysztof Adamczyk on 24.04.2017.
 * Class is responding for manage Classifieds
 */
class ClassifiedsController{

    /**
     * title of bar shown on the top
     */
    private final static String barTitle = "OGŁOSZENIA";

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
     * initializing ClassifiedsController when Controller rootBorderPane will be changed to Classifieds View
     * @see Controller#handleWindowShownEvent()
     * @see Controller#changeCenterToClassifieds()
     */
    ClassifiedsController(Controller controller) {
        this.controller = controller;
    }

    /**
     * Method called on shown Main Window and after click button (Ogłoszenia) at left bar
     * Downloading All Classifieds from server
     * then fill Controller#rootBorderPane with data
     * @see LeftBar#showClassifiedsLeftBarButton()
     * @see Login#showClassifiedsLeftBarButton()
     */
    @FXML
    void showClassifieds() {
        Platform.runLater(() -> {
            this.content.getStyleClass().add("classifiedvbox");
            this.centerScrollPane.setContent(this.content);

            Label labelTitle = new Label(barTitle);
            labelTitle.getStyleClass().add("labeltitle");
            this.content.getChildren().add(labelTitle);

            Set<Object> classifiedsObjects = this.controller.getRelationHelper().getAllAsSet(new Action("getAllClassifieds", "FROM Classifieds c ORDER BY idClassifieds DESC"));
            Set<ClassifiedsNH> classifieds = new HashSet<>(0);
            classifiedsObjects.forEach(i -> classifieds.add(new ClassifiedsNH((Classifieds)i)));
            if(!classifieds.isEmpty()) {
                try{
                    for(ClassifiedsNH clsf: classifieds) {
                        VBox vBox = new VBox();
                        Label body = new Label(clsf.getBody());
                        body.getStyleClass().add("classifiedsbody");
                        body.setMaxWidth(this.content.getWidth()-30);
                        body.setWrapText(true);

                        clsf.setAction(new Action("getAdminToClassifieds"));
                        clsf = (ClassifiedsNH) this.controller.getRelationHelper().getRelated(clsf);

                        Label adminLabel = new Label(clsf.getAdmin().getUser().getName() + " " + clsf.getAdmin().getUser().getSurname());
                        adminLabel.getStyleClass().add("classifiedsID");
                        Separator separator = new Separator();
                        vBox.getChildren().addAll(body, adminLabel, separator);
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
