package com.agile.agilevisitor.helper;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.hardware.Camera;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.Surface;
import android.view.View;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentManager;

import com.agile.agilevisitor.R;
import com.agile.agilevisitor.customviews.MyTextview;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import butterknife.BindView;
import butterknife.ButterKnife;

public class CameraActivity extends AppCompatActivity {

    @BindView(R.id.frame_layout)
    FrameLayout frameLayout;
    @BindView(R.id.fab_take_photo)
    FloatingActionButton fabTakePhoto;
    @BindView(R.id.iv_back)
    ImageView ivBack;
    @BindView(R.id.tv_title)
    MyTextview tvTitle;
    @BindView(R.id.toolbar)
    Toolbar toolbar;


    public ImageSurfaceView mImageSurfaceView;
    public static Camera camera;
    public Camera mCamera = null;

    int numberOfCameras = 0, camId = 0;
    String timeStamp = "";
    File file = null;
    Display display;
    String toolbarTitle="";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);
        ButterKnife.bind(this);


        if(getIntent()!=null){
            toolbarTitle=getIntent().getStringExtra("toolbar_title");
        }

        initialize();

        fabTakePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                camera.takePicture(null, null, pictureCallback);
            }
        });

        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

    }

    private void initialize() {

        setSupportActionBar(toolbar);
        ivBack.setVisibility(View.VISIBLE);
        tvTitle.setVisibility(View.VISIBLE);

        if (toolbarTitle.equalsIgnoreCase("basic")){
            tvTitle.setText("Capture Selfie");
        }else{
            tvTitle.setText("Capture ID Proof - Aadhar card");
        }

        timeStamp = Utils.currentTimeStamp();
        file = new File(getExternalFilesDir(null), timeStamp + ".png");
        display = ((WindowManager) getSystemService(WINDOW_SERVICE)).getDefaultDisplay();

        camera = checkDeviceCamera();
        mImageSurfaceView = new ImageSurfaceView(CameraActivity.this, camera);
        if (display.getRotation() == Surface.ROTATION_0) {
            camera.setDisplayOrientation(90);
        }

        frameLayout.addView(mImageSurfaceView);
    }

    private Camera checkDeviceCamera() {

        numberOfCameras = Camera.getNumberOfCameras();

        if (numberOfCameras == 0) {
            Toast.makeText(this, "Your phone doesn't have any camera", Toast.LENGTH_LONG).show();
            Intent returnIntent = new Intent();
            setResult(Activity.RESULT_CANCELED, returnIntent);
            finish();

        } else if (numberOfCameras == 1) {
            camId = 0;
            try {
                mCamera = Camera.open(0);
            } catch (Exception e) {
                e.printStackTrace();
            }

        } else if (numberOfCameras == 2) {
            camId = 1;
            try {
                mCamera = Camera.open(1);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return mCamera;
    }

    Camera.PictureCallback pictureCallback = new Camera.PictureCallback() {

        @Override
        public void onPictureTaken(byte[] data, Camera camera) {

            Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
            if (bitmap == null) {
                return;
            }

            Matrix matrix = new Matrix();

            if (camId == 1) {
                matrix.postRotate(270 - 180);
                matrix.preScale(-1.0f, 1.0f);
            } else {
                matrix.postRotate(270 - 180);
            }

            DisplayMetrics displayMetrics = new DisplayMetrics();
            getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
            int height = displayMetrics.heightPixels * 70 / 100;
            int width = displayMetrics.widthPixels * 70 / 100;
            Bitmap scaledBitmap = Bitmap.createScaledBitmap(bitmap, height, width, true);
            Bitmap rotatedBitmap = Bitmap.createBitmap(scaledBitmap, 0, 0, scaledBitmap.getWidth(), scaledBitmap.getHeight(), matrix, true);


            ByteArrayOutputStream out = new ByteArrayOutputStream();
            rotatedBitmap.compress(Bitmap.CompressFormat.JPEG, 30, out);
            try {
                save(out);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        private void save(ByteArrayOutputStream bytes) throws IOException {
            OutputStream output = null;
            try {
                output = new FileOutputStream(file);
                output.write(bytes.toByteArray());

                Intent returnIntent = new Intent();
                returnIntent.putExtra("result", file.getPath());
                setResult(Activity.RESULT_OK, returnIntent);
                finish();

                if (camera != null) {
                    camera.stopPreview();
                    camera.release();
                    camera = null;
                }

            } finally {
                if (null != output) {
                    output.close();
                }
            }
        }
    };

    @Override
    public void onBackPressed() {

        FragmentManager fm=getSupportFragmentManager();

        if (fm.getBackStackEntryCount()>0){
            Intent returnIntent = new Intent();
            setResult(Activity.RESULT_CANCELED, returnIntent);
            finish();

            fm.popBackStack();
        }else{
            super.onBackPressed();
        }







    }
}
