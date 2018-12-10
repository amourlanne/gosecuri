import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * Class Main
 * Created by Alexis on 30/11/2018
 */
public class Main extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("templates/home.fxml"));
        primaryStage.setTitle("GoSecuri");
        primaryStage.setScene(new Scene(root));
        primaryStage.setX(780);
        primaryStage.setY(50);
        primaryStage.setResizable(false);
        primaryStage.show();
    }
}
