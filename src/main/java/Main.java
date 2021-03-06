import com.google.api.gax.paging.Page;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.storage.Blob;
import com.google.cloud.storage.Bucket;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.cloud.StorageClient;
import controller.HomeController;
import entity.database.Firebase;
import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import org.opencv.core.Core;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

/**
 * Class Main
 * Created by Alexis on 30/11/2018
 */
public class Main extends Application {

    public static void main(String[] args) {

        // chargement de la librairie Opencv nativement
        nu.pattern.OpenCV.loadShared();
        // lancement de l'application
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {

        Firebase.connect();

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("templates/home.fxml"));
            Parent root = null;

            root = loader.load();
            HomeController controller = loader.getController();

            primaryStage.setScene(new Scene(root));
            primaryStage.setTitle("GoSecuri");
            primaryStage.setX(500);
            primaryStage.setY(100);
            primaryStage.getIcons().add(new Image("assets/img/logo.png"));
            primaryStage.setResizable(false);
            primaryStage.show();

            primaryStage.setOnCloseRequest((we -> controller.setClosed()));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
