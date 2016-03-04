package com.lifeistech.android.realface;

import android.app.Application;

import com.google.android.gms.vision.face.FaceDetector;
import com.kii.cloud.storage.Kii;

/**
 * Created by kokushiseiya on 16/03/04.
 */
public class RealFaceApplication extends Application {
    private FaceDetector mFaceDetector;

    @Override
    public void onCreate() {
        super.onCreate();

        // initialize the Kii SDK!
        Kii.initialize("b01a9cf8", "543a682c092bda086edcb5387a49a024", Kii.Site.JP);

        mFaceDetector = new FaceDetector.Builder(this)
                .setTrackingEnabled(false)
                        //.setMode(FaceDetector.FAST_MODE)
                .setLandmarkType(FaceDetector.ALL_LANDMARKS)
                .build();
    }

    public FaceDetector getFaceDetector() {
        return mFaceDetector;
    }
}
