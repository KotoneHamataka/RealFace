package com.lifeistech.android.realface;

import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.kii.cloud.storage.Kii;
import com.kii.cloud.storage.KiiBucket;
import com.kii.cloud.storage.KiiObject;
import com.kii.cloud.storage.KiiUser;
import com.kii.cloud.storage.UserFields;
import com.kii.cloud.storage.exception.app.AppException;
import com.kii.cloud.storage.resumabletransfer.AlreadyStartedException;
import com.kii.cloud.storage.resumabletransfer.KiiRTransfer;
import com.kii.cloud.storage.resumabletransfer.KiiRTransferManager;
import com.kii.cloud.storage.resumabletransfer.KiiRTransferProgressCallback;
import com.kii.cloud.storage.resumabletransfer.KiiUploader;
import com.kii.cloud.storage.resumabletransfer.StateStoreAccessException;
import com.kii.cloud.storage.resumabletransfer.SuspendedException;
import com.kii.cloud.storage.resumabletransfer.TerminatedException;

import java.io.File;
import java.io.IOException;
import java.util.List;


public class DataLoadActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_data_load);

        AsyncUpload asyncTask = new AsyncUpload();
        asyncTask.execute();
    }

    public class AsyncUpload extends AsyncTask<Void, Integer, Boolean> {
        @Override
        protected Boolean doInBackground(Void... voids) {
            // Create a bucket instance.
            KiiBucket bucket = Kii.bucket("user");

// Get a KiiRTransferManager.
            KiiRTransferManager manager = bucket.getTransferManager();

// Get all KiiUploader instances.
            List<KiiUploader> uploaders = null;
            try {
                uploaders = manager.listUploadEntries(getApplicationContext());
            } catch (StateStoreAccessException e1) {
                // Failed to access the local storage.
            }

// Choose the uploader to resume.
// (e.g. let a user select the one to resume)
// In this snippet, we will simply pick us the first one.
            KiiUploader uploader = uploaders.get(0);

// Resume the file upload.
            try {
                uploader.transfer(new KiiRTransferProgressCallback() {
                    @Override
                    public void onProgress(KiiRTransfer operator, long completedInBytes, long totalSizeinBytes) {
                        float progress = (float)completedInBytes / (float)totalSizeinBytes * 100.0f;
                        Log.d("DataLoadActivity", progress + "% completed");
                    }
                });
            } catch (AlreadyStartedException e) {
                // Upload already in progress.
            } catch (SuspendedException e) {
                // Upload suspended (e.g. network error or user interruption).
            } catch (TerminatedException e) {
                // Upload terminated (e.g. file not found or user interruption).
            } catch (StateStoreAccessException e) {
                // Failed to access the local storage.
            }
            return true;
        }
    }
}
