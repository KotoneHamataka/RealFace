package com.lifeistech.android.realface;

import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;

import com.kii.cloud.storage.Kii;
import com.kii.cloud.storage.KiiObject;
import com.kii.cloud.storage.KiiUser;
import com.kii.cloud.storage.UserFields;
import com.kii.cloud.storage.exception.app.AppException;
import com.kii.cloud.storage.resumabletransfer.KiiRTransfer;
import com.kii.cloud.storage.resumabletransfer.KiiRTransferCallback;
import com.kii.cloud.storage.resumabletransfer.KiiUploader;

import java.io.File;
import java.io.IOException;

/**
 * Created by kokushiseiya on 16/03/05.
 */
public class AsyncSaveData extends AsyncTask<User, Integer, Boolean> {
    static String TAG = "AsyncSaveData";

    @Override
    protected Boolean doInBackground(User... users) {
        KiiObject object = Kii.bucket("user").object();

        object.set("name", users[0].getName());
        object.set("score", users[0].getScore());

        File dir = new File(Environment.getExternalStorageDirectory(), "Camera");
        File f = new File(dir, "img.jpg");

        KiiUploader uploader = object.uploader(users[0].getContext(), f);

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
