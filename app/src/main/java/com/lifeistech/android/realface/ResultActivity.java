package com.lifeistech.android.realface;

import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.SparseArray;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.face.Face;
import com.google.android.gms.vision.face.FaceDetector;
import com.kii.cloud.storage.Kii;
import com.kii.cloud.storage.KiiObject;
import com.kii.cloud.storage.KiiUser;
import com.kii.cloud.storage.UserFields;
import com.kii.cloud.storage.exception.app.AppException;
import com.kii.cloud.storage.resumabletransfer.AlreadyStartedException;
import com.kii.cloud.storage.resumabletransfer.KiiRTransfer;
import com.kii.cloud.storage.resumabletransfer.KiiRTransferCallback;
import com.kii.cloud.storage.resumabletransfer.KiiRTransferProgressCallback;
import com.kii.cloud.storage.resumabletransfer.KiiUploader;
import com.kii.cloud.storage.resumabletransfer.StateStoreAccessException;
import com.kii.cloud.storage.resumabletransfer.SuspendedException;
import com.kii.cloud.storage.resumabletransfer.TerminatedException;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

public class ResultActivity extends AppCompatActivity {

    static String TAG = "Result Activity";
    private TextView pointTextView;
    private Button nextButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        pointTextView = (TextView) findViewById(R.id.pointTextView);
        nextButton = (Button) findViewById(R.id.nextButton);
        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), DataLoadActivity.class);
                startActivity(intent);
            }
        });

        detectFace(getIntent().getStringExtra("Name"));
    }

    public void detectFace(String name) {
        try {
            File dir = new File(Environment.getExternalStorageDirectory(), "Camera");
            File f = new File(dir, "img.jpg");
            InputStream inputStream = new FileInputStream(f);
            Bitmap bitmap = BitmapFactory.decodeStream(inputStream);

            // A new face detector is created for detecting the face and its landmarks.
            //
            // Setting "tracking enabled" to false is recommended for detection with unrelated
            // individual images (as opposed to video or a series of consecutively captured still
            // images).  For detection on unrelated individual images, this will give a more accurate
            // result.  For detection on consecutive images (e.g., live video), tracking gives a more
            // accurate (and faster) result.
            //
            // By default, landmark detection is not enabled since it increases detection time.  We
            // enable it here in order to visualize detected landmarks.
            FaceDetector detector = new FaceDetector.Builder(getApplicationContext())
                    .setTrackingEnabled(false)
                    .setLandmarkType(FaceDetector.ALL_LANDMARKS)
                    .setMode(FaceDetector.ACCURATE_MODE)
                    .setClassificationType(FaceDetector.ALL_CLASSIFICATIONS)
                    .build();

            // This is a temporary workaround for a bug in the face detector with respect to operating
            // on very small images.  This will be fixed in a future release.  But in the near term, use
            // of the SafeFaceDetector class will patch the issue.
            Detector<Face> safeDetector = new SafeFaceDetector(detector);

            // Create a frame from the bitmap and run face detection on the frame.
            Frame frame = new Frame.Builder().setBitmap(bitmap).build();
            SparseArray<Face> faces = safeDetector.detect(frame);

            if (!safeDetector.isOperational()) {
                // Note: The first time that an app using face API is installed on a device, GMS will
                // download a native library to the device in order to do detection.  Usually this
                // completes before the app is run for the first time.  But if that download has not yet
                // completed, then the above call will not detect any faces.
                //
                // isOperational() can be used to check if the required native library is currently
                // available.  The detector will automatically become operational once the library
                // download completes on device.
                Log.w(TAG, "Face detector dependencies are not yet available.");

                // Check for low storage.  If there is low storage, the native library will not be
                // downloaded, so detection will not become operational.
                IntentFilter lowstorageFilter = new IntentFilter(Intent.ACTION_DEVICE_STORAGE_LOW);
                boolean hasLowStorage = registerReceiver(null, lowstorageFilter) != null;

                if (hasLowStorage) {
                    Toast.makeText(this, R.string.low_storage_error, Toast.LENGTH_LONG).show();
                    Log.w(TAG, getString(R.string.low_storage_error));
                }
            }

            FaceView overlay = (FaceView) findViewById(R.id.faceView);
            overlay.setContent(bitmap, faces);

            for (int i = 0, size = faces.size(); i < size; i++) {
                Log.d(TAG, String.format("%.2f", faces.get(i).getIsSmilingProbability()) + "点");
            }

            int score = (int) (100f - faces.get(0).getIsSmilingProbability() * 100f);

            //getIsSmilingProbabilityがnullなら顔認識できてない
            pointTextView.setText(score + "点");

            // Although detector may be used multiple times for different images, it should be released
            // when it is no longer needed in order to free native resources.
            safeDetector.release();

            saveScore(name, score, bitmap);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public class User {
        private int score;
        private String name;
        private Bitmap bitmap;

            public User() {

            }

            public User(String name, int score, Bitmap bitmap) {
                this.name = name;
                this.score = score;
                this.bitmap = bitmap;
            }

        public String getName() {
            return name;
        }

        public int getScore() {
            return score;
        }

        public Bitmap getBitmap() {
            return bitmap;
        }

    }

    public void saveScore(String name, int score, Bitmap bitmap) {
        User user = new User(name, score, bitmap);
        AsyncSaveData asyncTask = new AsyncSaveData();
        asyncTask.execute(user);
    }

    public class AsyncSaveData extends AsyncTask<User, Integer, Boolean> {
        @Override
        protected Boolean doInBackground(User... users) {
            KiiObject object = Kii.bucket("user").object();

            object.set("name", users[0].getName());
            object.set("score", users[0].getScore());

            File dir = new File(Environment.getExternalStorageDirectory(), "Camera");
            File f = new File(dir, "img.jpg");

            KiiUploader uploader = object.uploader(getApplicationContext(), f);

            try {
                // You can set predefined fields and custom fields.
                UserFields userFields = new UserFields();
                userFields.putDisplayName("Player 1");
                userFields.set("HighScore", 0);
                KiiUser pseudoUser = KiiUser.registerAsPseudoUser(userFields);
                // Must save the token.
                // If it's lost the user will not be able to access KiiCloud.
                String accessToken = pseudoUser.getAccessToken();
                // (assuming that your application implements this function)
                //storeToken(accessToken);

                // Start uploading
                uploader.transferAsync(new KiiRTransferCallback() {
                    @Override
                    public void onStart(KiiRTransfer operator) {
                        Log.d(TAG, "start transfer");
                    }

                    @Override
                    public void onProgress(KiiRTransfer operator, long completedInBytes, long totalSizeinBytes) {
                        float progress = (float) completedInBytes / (float) totalSizeinBytes * 100.0f;
                        Log.d(TAG, progress + "% completed");
                    }

                    @Override
                    public void onTransferCompleted(KiiRTransfer operator, Exception exception) {
                        if (exception != null) {
                            // Error handling(Includes suspending/terminating)
                            return;
                        }
                    }
                });

            } catch (IOException e) {
                e.printStackTrace();
            } catch (AppException e) {
                e.printStackTrace();
            }

            return true;
        }
    }
}
