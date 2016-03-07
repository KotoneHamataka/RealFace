package com.lifeistech.android.realface;

import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.hardware.Camera;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.LayoutDirection;
import android.util.Log;
import android.util.Size;
import android.util.SparseArray;
import android.view.Gravity;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.GlideDrawableImageViewTarget;
import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.face.Face;
import com.google.android.gms.vision.face.FaceDetector;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

public class CameraActivity extends AppCompatActivity {

    static String TAG = "Camera Activity";
    FrameLayout fl;
    SurfaceView sv;
    SurfaceHolder sh;
    Camera cam;
    Handler handler = new Handler();
    String userName;
    Button btn;
    private final int GIF_NUM = 13;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        //requestWindowFeature(Window.FEATURE_NO_TITLE);

        fl = new FrameLayout(this);
        setContentView(fl);

        sv = new SurfaceView(this);
        sh = sv.getHolder();
        sh.addCallback(new SurfaceHolderCallback());

        btn = new Button(this);
        btn.setLayoutParams(new FrameLayout.LayoutParams(200, 200, Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL));
        btn.setBackgroundResource(R.drawable.btn_oval);
        btn.setOnClickListener(new TakePictureClickListener());

        fl.addView(sv);
        fl.addView(btn);
    }

    class SurfaceHolderCallback implements SurfaceHolder.Callback {
        @Override
        public void surfaceCreated(SurfaceHolder holder) {
            openCamera(holder);
        }

        @Override
        public void surfaceChanged(SurfaceHolder holder, int f, int w, int h) {
            changeCameraState(w, h);
        }

        @Override
        public void surfaceDestroyed(SurfaceHolder holder) {
            //closeCamera();
        }

        private void openCamera(SurfaceHolder holder) {
            int frontCameraId = -1;
            int backCameraId = -1;
            int numberOfCameras = Camera.getNumberOfCameras();
            Camera.CameraInfo cameraInfo = new Camera.CameraInfo();
            for (int i = 0; i < numberOfCameras; i++) {
                // 指定したカメラの情報を取得
                Camera.getCameraInfo(i, cameraInfo);
                if (cameraInfo.facing == Camera.CameraInfo.CAMERA_FACING_BACK) {
                    backCameraId = i;
                } else if (cameraInfo.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
                    frontCameraId = i;
                }
            }
            int id = -1;
            if (frontCameraId != -1) { // フロントカメラを指定
                id = frontCameraId;
            }

            // if (backCameraId != -1) { / バックカメラを指定
            // id = backCameraId;
            //}

            if (id >= 0) {
                cam = Camera.open(id);
            } else {
                cam = Camera.open();
            }

            try {
                cam.setPreviewDisplay(holder);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        private void closeCamera() {
            // カメラインスタンス開放
            cam.release();
            cam = null;
        }

        private void changeCameraState(int width, int height) {
            Log.d(TAG, "surfaceChanged width:" + width + " height:" + height);

            Camera.Parameters parameters = cam.getParameters();

            // デバッグ用表示
            Camera.Size size = parameters.getPictureSize();
            Log.d(TAG, "getPictureSize width:" + size.width + " size.height:" + size.height);
            size = parameters.getPreviewSize();
            Log.d(TAG, "getPreviewSize width:" + size.width + " size.height:" + size.height);

            // プレビューのサイズを変更
            parameters.setPreviewSize(size.width, size.height);    // 画面サイズに合わせて変更しようとしたが失敗する
            // 使用できるサイズはカメラごとに決まっているみたいなので、うまくいかなければこちらを使う
            // parameters.setPreviewSize(640, 480);

            // 縦画面の場合回転させる
            /*
            if (width < height) {
                // 縦画面
                cam.setDisplayOrientation(90);
            }else{
                // 横画面
                cam.setDisplayOrientation(0);
            }
            */

            int degrees = 0;
            int rotation = getWindowManager().getDefaultDisplay().getRotation();
            switch (rotation) {
                case Surface.ROTATION_0: degrees = 0; break;
                case Surface.ROTATION_90: degrees = 90; break;
                case Surface.ROTATION_180: degrees = 180; break;
                case Surface.ROTATION_270: degrees = 270; break;
            }
            Log.d(TAG, degrees + "");

            if (degrees == 0) {
                // 縦画面
                cam.setDisplayOrientation(90);
            } else if (degrees == 90) {
                // 横画面
                cam.setDisplayOrientation(0);
            } else if (degrees == 270) {
                // 横画面
                cam.setDisplayOrientation(180);
            }

            // パラメーターセット
            cam.setParameters(parameters);
            // プレビュー開始
            cam.startPreview();
        }

    }

    class TakePictureClickListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            /*
            Intent intent = new Intent(CameraActivity.this, SmileActivity.class);
            startActivity(intent);
            */
            View blankView = new View(getApplicationContext());
            blankView.setBackgroundColor(Color.WHITE);
            fl.addView(blankView);

            ImageView gifImageView = new ImageView(getApplicationContext());
            GlideDrawableImageViewTarget imageViewTarget = new GlideDrawableImageViewTarget(gifImageView);

            Random random = new Random();
            int gifNum = random.nextInt(GIF_NUM) + 1;
            Glide.with(getApplicationContext())
                    .load(Uri.parse("file:///android_asset/make_smile_" + gifNum +".gif"))
                    .into(imageViewTarget);

            fl.addView(gifImageView);
            fl.removeView(btn);

            //タイマーセット
            Timer timer = new Timer(true);
            //10秒後にタイマーセット
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            //5秒後に撮影
                            if (cam != null) {
                                cam.takePicture(null, null, new TakePictureCallback());

                            } else {
                                Toast.makeText(getApplicationContext(), "Cam is Null", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });

                }
            }, 10000);

        }
    }

    class TakePictureCallback implements Camera.PictureCallback {
        @Override
        public void onPictureTaken(byte[] data, Camera camera) {
            if (data != null) {

                int rotation = getWindowManager().getDefaultDisplay().getRotation();
                int degrees = 0; //端末の向き(度換算)
                switch (rotation) {
                    case Surface.ROTATION_0: degrees = 0; break;
                    case Surface.ROTATION_90: degrees = 90; break;
                    case Surface.ROTATION_180: degrees = 180; break;
                    case Surface.ROTATION_270: degrees = 270; break;
                }
                Matrix m = new Matrix(); //Bitmapの回転用Matrix
                if (degrees == 0) {
                    m.setRotate(270);
                } else if (degrees == 90) {
                    m.setRotate(0);
                } else if (degrees == 270) {
                    m.setRotate(180);
                }
                Bitmap original = BitmapFactory.decodeByteArray(data, 0, data.length);
                Bitmap rotated = Bitmap.createBitmap(original, 0, 0, original.getWidth(), original.getHeight(), m, true);

                FileOutputStream fos = null;
                try {
                    File dir = new File(Environment.getExternalStorageDirectory(), "Camera");
                    if(!dir.exists()) {
                        dir.mkdir();
                    }
                    File f = new File(dir, "img.jpg");
                    fos = new FileOutputStream(f);
                    rotated.compress(Bitmap.CompressFormat.JPEG, 100, fos);
                    fos.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }

                original.recycle();
                rotated.recycle();

                cam.release();
                cam = null;

                Intent intent = new Intent(getApplicationContext(), ResultActivity.class);
                intent.putExtra("Name", userName);
                startActivity(intent);
            }
        }
    }
}
