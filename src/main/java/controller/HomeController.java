package controller;


import entity.database.Firebase;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import org.opencv.core.*;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import org.opencv.objdetect.CascadeClassifier;
import org.opencv.objdetect.Objdetect;
import org.opencv.videoio.VideoCapture;
import utils.Utils;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Class HomeController
 * Created by Alexis on 16/02/2018
 */
public class HomeController implements Initializable {

    @FXML
    private ImageView ImageViewFXML;

    @FXML
    private ImageView ImageViewFXML1;

    // un timer pour enregistrer le stream de la vidéo
    private ScheduledExecutorService timer;

    // l'objet OpenCV qui enregistre la vidéo
    private VideoCapture capture = new VideoCapture();

    // l'image de la camera
    private Mat frame;

    // le tableau des rectangles de visage
    private List<Rect> facesList;

    private boolean cameraActive = false;

    // un identifiant unique pour notre caméra
    private static int cameraId = 0;

    private int absoluteFaceSize = 0;

    private CascadeClassifier faceCascade = new CascadeClassifier();

    public void startCamera() {

        if (!this.cameraActive) {
            this.capture.open(cameraId);

            this.faceCascade.load("src/main/resources/assets/lbpcascades/lbpcascade_frontalface.xml");

            // is the video stream available?
            if (this.capture.isOpened()) {
                this.cameraActive = true;

                // periode d'enregistrement: 33 ms (30 frames/sec)
                Integer period = 33;

                // fonction de récuperation de l'image
                Runnable frameGrabber = () -> {
                    // recupération de l'image
                    this.frame = grabFrame();
                    // convertion de l'image OpenCV en ImageView javafx
                    Image imageToShow = Utils.mat2Image(this.frame);
                    updateImageView(ImageViewFXML, imageToShow);
                };

                this.timer = Executors.newSingleThreadScheduledExecutor();

                this.timer.scheduleAtFixedRate(frameGrabber, 0, period, TimeUnit.MILLISECONDS);
            } else {
                System.err.println("Impossible d'établir la connexion avec la camera...");
            }
        }
    }

    public void stopCamera() {
        if (this.cameraActive) {
            this.cameraActive = false;
            this.stopAcquisition();
        }
    }

    @FXML
    void onMouseClickedTakePhoto(MouseEvent event) {
        if (this.cameraActive) {
            int idx = 0;
            for ( Rect rect : this.facesList ) {
                Imgcodecs.imwrite( "src/main/resources/assets/faces/face" + idx + ".jpg", new Mat(this.frame,rect) );
                ++ idx;
            }
        }
    }

    private Mat grabFrame() {

        Mat frame = new Mat();

        // verifier si la capture video est ouverte
        if (this.capture.isOpened()) {
            try {
                // lecture de l'image en cour
                this.capture.read(frame);

                // detection des visage si l'image n'est pas vide
                if (!frame.empty()) {
                    // face detection
                    this.detectAndDisplay(frame);
                }
            } catch (Exception e) {
                System.err.println("Exception during the image elaboration: " + e);
            }
        }

        return frame;
    }

    private void detectAndDisplay(Mat frame)
    {
        MatOfRect faces = new MatOfRect();
        Mat grayFrame = new Mat();

        // convert the frame in gray scale
        Imgproc.cvtColor(frame, grayFrame, Imgproc.COLOR_BGR2GRAY);
        // equalize the frame histogram to improve the result
        Imgproc.equalizeHist(grayFrame, grayFrame);

        // compute minimum face size (20% of the frame height, in our case)
        if (this.absoluteFaceSize == 0) {

            int height = grayFrame.rows();
            if (Math.round(height * 0.2f) > 0) {
                this.absoluteFaceSize = Math.round(height * 0.2f);
            }
        }

        // detection des rectangles de visages
        this.faceCascade.detectMultiScale(grayFrame, faces, 1.1, 2, Objdetect.CASCADE_SCALE_IMAGE,
                new Size(this.absoluteFaceSize, this.absoluteFaceSize), new Size());

        // pour chaque rectangle de visage: dessiner un rectangle de couleur (0, 255, 0)
        this.facesList = faces.toList();

        for ( Rect rect : this.facesList ) {
            Imgproc.rectangle(frame, rect.tl(), rect.br(), new Scalar(0, 255, 0), 1);
        }
    }

    /**
     * Stop the acquisition from the camera and release all the resources
     */
    private void stopAcquisition() {
        if (this.timer != null && !this.timer.isShutdown()) {
            try {
                // stopper le timer
                this.timer.shutdown();
                this.timer.awaitTermination(33, TimeUnit.MILLISECONDS);
            } catch (InterruptedException e) {
                System.err.println("Exception in stopping the frame capture, trying to release the camera now... " + e);
            }
        }

        if (this.capture.isOpened()) {
            // release de la camera
            this.capture.release();
        }
    }

    /**
     * Mise a jour de l'ImageView dans la thread principal JavaFX
     */
    private void updateImageView(ImageView view, Image image) {
        Utils.onFXThread(view.imageProperty(), image);
    }

    /**
     * Stopper la camera lorsque l'on quitte l'application
     */
    public void setClosed() {
        this.stopAcquisition();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        this.startCamera();
    }
}
