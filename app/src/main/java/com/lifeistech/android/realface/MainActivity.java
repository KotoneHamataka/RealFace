package com.lifeistech.android.realface;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;
import com.kii.cloud.storage.Kii;
import com.kii.cloud.storage.KiiBucket;
import com.kii.cloud.storage.KiiObject;
import com.kii.cloud.storage.exception.app.AppException;
import com.kii.cloud.storage.query.KiiQuery;
import com.kii.cloud.storage.query.KiiQueryResult;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "PhotoViewerActivity";

    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;
    private ImageView no1_imageView, no2_imageView, no3_imageView;
    private TextView no1_name, no1_score, no2_name, no2_score, no3_name, no3_score;

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
                Intent intent = new Intent(MainActivity.this, CameraActivity.class);
                startActivity(intent);
            }
        });
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();

        no1_imageView = (ImageView) findViewById(R.id.rank1_icon);
        no1_name = (TextView) findViewById(R.id.rank1_name);
        no1_score = (TextView) findViewById(R.id.rank1_score);
        no1_name.setVisibility(View.INVISIBLE);
        no1_score.setVisibility(View.INVISIBLE);
        no2_imageView = (ImageView) findViewById(R.id.rank2_icon);
        no2_name = (TextView) findViewById(R.id.rank2_name);
        no2_score = (TextView) findViewById(R.id.rank2_score);
        no2_name.setVisibility(View.INVISIBLE);
        no2_score.setVisibility(View.INVISIBLE);
        no3_imageView = (ImageView) findViewById(R.id.rank3_icon);
        no3_name = (TextView) findViewById(R.id.rank3_name);
        no3_score = (TextView) findViewById(R.id.rank3_score);
        no3_name.setVisibility(View.INVISIBLE);
        no3_score.setVisibility(View.INVISIBLE);



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

    public void slide(View view) {
        if (view.getId() == R.id.rank1_icon) {
            doAnimationToBtnXCode(1);
        } else if (view.getId() == R.id.rank2_icon) {
            doAnimationToBtnXCode(2);
        } else if (view.getId() == R.id.rank3_icon) {
            doAnimationToBtnXCode(3);
        }
    }

    /** プログラムで定義したプロパティアニメーションを実行 */
    private void doAnimationToBtnXCode(int rank) {
        android.view.animation.Interpolator interpolator = AnimationUtils.loadInterpolator(this, android.R.interpolator.decelerate_quint);

        ObjectAnimator animStart, animEnd, nameStart, nameEnd, scoreStart, scoreEnd;

        if (rank == 1) {
            animStart = ObjectAnimator.ofFloat(no1_imageView, "translationX", 0.f, 600.f).setDuration(3000);
            animEnd = ObjectAnimator.ofFloat(no1_imageView, "translationX", 600.f, 0.f).setDuration(3000);
            nameStart = ObjectAnimator.ofFloat(no1_name, "translationX", -300.f, 200.f).setDuration(2300);
            nameEnd = ObjectAnimator.ofFloat(no1_name, "translationX", 200.f, -300f).setDuration(2300);
            scoreStart = ObjectAnimator.ofFloat(no1_score, "translationX", -300.f, 200.f).setDuration(2300);
            scoreEnd = ObjectAnimator.ofFloat(no1_score, "translationX", 200.f, -300f).setDuration(2300);
        } else if (rank == 2) {
            animStart = ObjectAnimator.ofFloat(no2_imageView, "translationX", 0.f, -600.f).setDuration(3000);
            animEnd = ObjectAnimator.ofFloat(no2_imageView, "translationX", -600.f, 0.f).setDuration(3000);
            nameStart = ObjectAnimator.ofFloat(no2_name, "translationX", 300.f, -200.f).setDuration(2300);
            nameEnd = ObjectAnimator.ofFloat(no2_name, "translationX", -200.f, 300f).setDuration(2300);
            scoreStart = ObjectAnimator.ofFloat(no2_score, "translationX", 300.f, -200.f).setDuration(2300);
            scoreEnd = ObjectAnimator.ofFloat(no2_score, "translationX", -200.f, 300f).setDuration(2300);
        } else {
            animStart = ObjectAnimator.ofFloat(no3_imageView, "translationX", 0.f, 600.f).setDuration(3000);
            animEnd = ObjectAnimator.ofFloat(no3_imageView, "translationX", 600.f, 0.f).setDuration(3000);
            nameStart = ObjectAnimator.ofFloat(no3_name, "translationX", -300.f, 200.f).setDuration(2300);
            nameEnd = ObjectAnimator.ofFloat(no3_name, "translationX", 200.f, -300f).setDuration(2300);
            scoreStart = ObjectAnimator.ofFloat(no3_score, "translationX", -300.f, 200.f).setDuration(2300);
            scoreEnd = ObjectAnimator.ofFloat(no3_score, "translationX", 200.f, -300f).setDuration(2300);
        }
        animStart.setInterpolator(interpolator);
        animEnd.setInterpolator(interpolator);

        AnimatorSet set = new AnimatorSet();
        AnimatorSet setName = new AnimatorSet();
        AnimatorSet setScore = new AnimatorSet();
        set.playSequentially(animStart, animEnd);
        setName.playSequentially(nameStart, nameEnd);
        setScore.playSequentially(scoreStart, scoreEnd);
        set.start();
        setName.start();
        setScore.start();

        if (rank == 1) {
            no1_name.setVisibility(View.VISIBLE);
            no1_score.setVisibility(View.VISIBLE);
        } else if (rank == 2) {
            no2_name.setVisibility(View.VISIBLE);
            no2_score.setVisibility(View.VISIBLE);
        } else if (rank == 3) {
            no3_name.setVisibility(View.VISIBLE);
            no3_score.setVisibility(View.VISIBLE);
        }

    }

    public class AsyncLoadData extends AsyncTask<Void, Integer, String[][]> {
        @Override
        protected String[][] doInBackground(Void... voids) {
            KiiQuery score_query = new KiiQuery();
            score_query.sortByDesc("score");
            score_query.setLimit(3);

            String[][] userData = new String[3][2];

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

                    String name = object.getString("name");
                    int score = object.getInt("score");
                    userData[i-1][0] = name;
                    userData[i-1][1] = String.valueOf(score);


                    object.downloadBody(f);
                    i++;
                }

            } catch (IOException e) {
                e.printStackTrace();
            } catch (AppException e) {
                e.printStackTrace();
            }

            return userData;
        }

        @Override
        protected void onPostExecute(String[][] userData) {
            no1_name.setText(userData[0][0]);
            no1_score.setText(userData[0][1]);
            no2_name.setText(userData[1][0]);
            no2_score.setText(userData[1][1]);
            no3_name.setText(userData[2][0]);
            no3_score.setText(userData[2][1]);
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
