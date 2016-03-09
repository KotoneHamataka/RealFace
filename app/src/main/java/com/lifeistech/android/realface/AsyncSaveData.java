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

            object.save();
            // Start uploading
            object.uploadBody(f, "image/jpeg");
        } catch (IOException e) {
            e.printStackTrace();
        } catch (AppException e) {
            e.printStackTrace();
        }

        return true;
    }
}
