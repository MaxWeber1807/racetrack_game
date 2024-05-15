package gui;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.io.IOException;

/**
 * Class that starts our application.
 *
 * @author Max Weber
 */
public class ApplicationMain extends Application {


    /**
     * Creating the stage and showing it. This is where the initial size and the
     * title of the window are set.
     *
     * @param stage the stage to be shown
     * @throws IOException
     */
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(ApplicationMain.class.getResource("Play.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 800, 400);
        stage.setTitle("Racetrack");

        UserInterfaceController controller = fxmlLoader.getController();
        controller.setStage(stage);

        stage.widthProperty().addListener((observable, oldWidth, newWidth) -> {
            double newHeight = newWidth.doubleValue() / UserInterfaceController.TARGET_RATIO;
            stage.setHeight(newHeight);
        });

        stage.heightProperty().addListener((observable, oldHeight, newHeight) -> {
            double newWidth = newHeight.doubleValue() * UserInterfaceController.TARGET_RATIO;
            stage.setWidth(newWidth);
        });
        scene.getStylesheets().add("style.css");
        stage.setScene(scene);
        stage.setMinHeight(480);
        stage.setMinWidth(640);
        stage.show();
    }

    /**
     * Main method
     *
     * @param args unused
     */
    public static void main(String... args) {
        launch(args);
    }
}
