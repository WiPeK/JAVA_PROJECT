package pl.wipek.client;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * @author Krzysztof Adamczyk
 * Managing start of application
 */
public class Main extends Application {

    /**
     * title of application
     */
    private final static String title = "Dziennik";

    /**
     * path to FXML with root template
     */
    private final static String rootFXMLPath = "/views/root.fxml";

    /**
     * @author Krzysztof Adamczyk
     * @param primaryStage application stage
     * @throws Exception
     * initializing view from rootFXMLPath
     * seting method on window shown and windows closing
     */
    @Override
    public void start(Stage primaryStage) throws Exception{
        FXMLLoader loader = new FXMLLoader(getClass().getResource(rootFXMLPath));
        final Controller controller = new Controller();
        loader.setController(controller);
        Parent root = loader.load();
        primaryStage.setTitle(title);
        primaryStage.setScene(new Scene(root));

        primaryStage.addEventHandler(WindowEvent.WINDOW_SHOWN, (e) -> Platform.runLater(controller::handleWindowShownEvent));

        primaryStage.addEventHandler(WindowEvent.WINDOW_CLOSE_REQUEST, new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent event) {
                controller.exitApplication(event);
            }
        });

        primaryStage.show();
    }


    /**
     * starting application
     * @param args passed to main on startup
     */
    public static void main(String[] args) {
        Logger logger = LogManager.getRootLogger();
        logger.info("Start client");
        launch(args);
    }
}
