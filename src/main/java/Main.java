import controller.HomeController;
import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import org.opencv.core.Core;

/**
 * Class Main
 * Created by Alexis on 30/11/2018
 */
public class Main extends Application {

    public static void main(String[] args) {

        // load the native OpenCV library
        nu.pattern.OpenCV.loadShared();
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {

        try
        {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("templates/home.fxml"));
            Parent root = null;

            root = loader.load();
            HomeController controller = loader.getController();

            primaryStage.setScene(new Scene(root));
            primaryStage.setTitle("GoSecuri");
            primaryStage.setX(780);
            primaryStage.setY(50);
            primaryStage.setResizable(false);
            primaryStage.show();

            primaryStage.setOnCloseRequest((we -> controller.setClosed()));
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

    }
}
