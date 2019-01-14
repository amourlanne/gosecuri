package entity.database;

import com.google.api.gax.paging.Page;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.storage.Blob;
import com.google.cloud.storage.Bucket;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.cloud.StorageClient;
import javafx.scene.image.Image;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Class Firebase
 * Created by Alexis on 14/01/2019
 */
public class Firebase {

    // http://googleapis.github.io/google-cloud-java/google-cloud-clients/apidocs/com/google/cloud/storage/Bucket.html
    public static Bucket bucket;

    private static final String bucketName = "gosecuri-f61f0";

    public static void connect() {

        if (bucket == null) {
            try {
                FileInputStream serviceAccount =
                        new FileInputStream("src/main/resources/assets/firebase/serviceAccountKey.json");

                FirebaseOptions options = new FirebaseOptions.Builder()
                        .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                        .setStorageBucket(bucketName + ".appspot.com")
                        .setDatabaseUrl("https://" + bucketName + ".firebaseio.com")
                        .build();

                FirebaseApp.initializeApp(options);

                bucket = StorageClient.getInstance().bucket();

            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static Blob getImage( String name ) {
        if(bucket != null) {
            return bucket.get(name);
        }
        return null;
    }

    public static List<Image> getAllImage() {
        List<Image> images = new ArrayList<>();
        if(bucket != null) {
            Firebase.bucket.list().iterateAll().forEach((blob) -> {
                String img_url = "https://firebasestorage.googleapis.com/v0/b/"
                        + bucketName + ".appspot.com/o/"
                        + blob.getName()
                        + "?alt=media&token="
                        + blob.getMetadata().get("firebaseStorageDownloadTokens");
                System.out.println(img_url);
                images.add(new Image(img_url));
            });
        }
        return images;
    }
}
