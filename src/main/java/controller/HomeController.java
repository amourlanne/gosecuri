package controller;


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

    // un timer pour enregistrer le stream de la vidéo
    private ScheduledExecutorService timer;

    // l'objet OpenCV qui enregistre la vidéo
    private VideoCapture capture = new VideoCapture();

    private Mat frame;

    private boolean cameraActive = false;

    // un identifiant unique pour notre caméra
    private static int cameraId = 0;

    private int absoluteFaceSize = 0;

    private CascadeClassifier faceCascade = new CascadeClassifier();

    public void startCamera() {

        if (!this.cameraActive) {
            this.capture.open(cameraId);

            this.faceCascade.load("src/main/resources/files/lbpcascades/lbpcascade_frontalface.xml");

            // is the video stream available?
            if (this.capture.isOpened()) {
                this.cameraActive = true;

                // periode d'enregistrement: 33 ms (30 frames/sec)
                Integer period = 33;

                // fonction de récuperation de l'image
                Runnable frameGrabber = () -> {
                    // recupération de l'image
                    Mat frame = grabFrame();
                    // convertion de l'image OpenCV en ImageView javafx
                    Image imageToShow = Utils.mat2Image(frame);
                    updateImageView(ImageViewFXML, imageToShow);
                };

                this.timer = Executors.newSingleThreadScheduledExecutor();
                this.timer.scheduleAtFixedRate(frameGrabber, 0, period, TimeUnit.MILLISECONDS);
            } else {
                System.err.println("Impossible to open the camera connection...");
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
            Imgcodecs.imwrite( "src/main/resources/files/frame.jpg", this.frame );
        }
    }

    /**
     * Get a frame from the opened video stream (if any)
     *
     * @return the {@link Mat} to show
     */
    private Mat grabFrame() {
        // init everything
        Mat frame = new Mat();

        // check if the capture is open
        if (this.capture.isOpened()) {
            try {
                // read the current frame
                this.capture.read(frame);

                // if the frame is not empty, process it
                if (!frame.empty())
                {
                    // face detection
                    this.detectAndDisplay(frame);
                }

            } catch (Exception e) {
                // log the error
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
        if (this.absoluteFaceSize == 0)
        {
            int height = grayFrame.rows();
            if (Math.round(height * 0.2f) > 0)
            {
                this.absoluteFaceSize = Math.round(height * 0.2f);
            }
        }

        // detect faces
        this.faceCascade.detectMultiScale(grayFrame, faces, 1.1, 2, Objdetect.CASCADE_SCALE_IMAGE,
                new Size(this.absoluteFaceSize, this.absoluteFaceSize), new Size());

        // each rectangle in faces is a face: draw them!
        Rect[] facesArray = faces.toArray();
        for (int i = 0; i < facesArray.length; i++) {

            this.frame = new Mat(frame,facesArray[i]);

            Imgproc.rectangle(frame, facesArray[i].tl(), facesArray[i].br(), new Scalar(0, 255, 0), 1);
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
