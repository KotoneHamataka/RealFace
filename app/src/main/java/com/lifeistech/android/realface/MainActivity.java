package com.lifeistech.android.realface;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.SparseArray;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.util.ViewPreloadSizeProvider;
import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.face.Face;
import com.google.android.gms.vision.face.FaceDetector;
import com.kii.cloud.storage.Kii;
import com.kii.cloud.storage.KiiBucket;
import com.kii.cloud.storage.KiiObject;
import com.kii.cloud.storage.KiiUser;
import com.kii.cloud.storage.UserFields;
import com.kii.cloud.storage.exception.app.AppException;
import com.kii.cloud.storage.query.KiiQuery;
import com.kii.cloud.storage.query.KiiQueryResult;
import com.kii.cloud.storage.resumabletransfer.KiiRTransfer;
import com.kii.cloud.storage.resumabletransfer.KiiRTransferCallback;
import com.kii.cloud.storage.resumabletransfer.KiiUploader;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "PhotoViewerActivity";

    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;
    private ImageView no1_imageView, no2_imageView, no3_imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, PrepareCameraActivity.class);
                startActivity(intent);
            }
        });
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();

        no1_imageView = (ImageView) findViewById(R.id.no1_imageView);
        no2_imageView = (ImageView) findViewById(R.id.no2_imageView);
        no3_imageView = (ImageView) findViewById(R.id.no3_imageView);

        KiiBucket appBucket = Kii.bucket("user");

        AsyncLoadData asyncTask = new AsyncLoadData();
        asyncTask.execute();

        File dir = new File(Environment.getExternalStorageDirectory(), "Camera");
        File f1 = new File(dir, "rank1.jpg");
        File f2 = new File(dir, "rank2.jpg");
        File f3 = new File(dir, "rank3.jpg");
        setIcon(f1, no1_imageView);
        setIcon(f2, no2_imageView);
        setIcon(f3, no3_imageView);

        /*
        ImageView imageView = (ImageView) findViewById(R.id.imageView);
        Glide.with(getApplicationContext())
                .load(Uri.parse("file:///android_asset/make_smile_01.gif"))
                .asGif()
                .crossFade()
                .into(imageView);
        */


    }

    public class AsyncLoadData extends AsyncTask<Void, Integer, Boolean> {
        @Override
        protected Boolean doInBackground(Void... voids) {
            KiiQuery score_query = new KiiQuery();
            score_query.sortByDesc("score");
            score_query.setLimit(3);

            try {
                int i = 1;
                File dir = new File(
                        Environment.getExternalStorageDirectory(), "Camera");
                if(!dir.exists()) {
                    dir.mkdir();
                }

                KiiQueryResult<KiiObject> result = Kii.bucket("user").query(score_query);
                List<KiiObject> objectList = result.getResult();
                for (KiiObject object : objectList){
                    File f = new File(dir, "rank" + i + ".jpg");
                    object.refresh();

                    int score = object.getInt("score");
                    String name = object.getString("name");
                    object.downloadBody(f);
                    i++;
                }

            } catch (IOException e) {
                e.printStackTrace();
            } catch (AppException e) {
                e.printStackTrace();
            }

            return true;
        }
    }

    public void setIcon(File file, ImageView imageView) {
        final RealFaceApplication application = (RealFaceApplication) getApplicationContext();

        Picasso.with(getApplicationContext())
                .load(file)
                .transform(new FaceTrimming(application.getFaceDetector()))
                .memoryPolicy(MemoryPolicy.NO_STORE)
                .into(imageView);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


}
