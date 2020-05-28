package com.capstone;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.capstone.Retrofit_Services.FileService;
import com.capstone.TCP_Client.Paths;

import java.io.File;

public class DashboardActivity extends AppCompatActivity {

    private int state;
    private FileService fileService;
    private ImageView imageView;
    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };
    private final int loadImages = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        ImageManager.readInImagePaths();
        setCameraImage();

        int permission = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);

        if (permission != PackageManager.PERMISSION_GRANTED) {
            //Toast.makeText(this, "Permissions not granted", Toast.LENGTH_LONG).show();
            // We don't have permission so prompt the user
            ActivityCompat.requestPermissions(
                    this,
                    PERMISSIONS_STORAGE,
                    REQUEST_EXTERNAL_STORAGE
            );
        }
        updateState(loadImages);

    }
    @Override
    public void onResume(){
        super.onResume();
        updateState(loadImages);
    }

    @Override
    // Return to Login Page on pressing back button - or do we want an intermediate Home Page or is Dashboard our Home Page?
    public void onBackPressed() {
        Intent startMain = new Intent(DashboardActivity.this, Login.class);
        startMain.addCategory(Intent.CATEGORY_HOME);
        startMain.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(startMain);
    }

    private void setCameraImage(){
        ImageView cameraImage = findViewById(R.id.cameraImageView);
        cameraImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DashboardActivity.this, TakePictureActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                startActivity(intent);
            }
        });
    }

    // Display scaled image on page
    private void displayImages() {

        for(String currentPath : ImageManager.getFilePaths()){
            // Get the dimensions of the View
            int targetW = imageView.getWidth();
            int targetH = imageView.getHeight();

            // Get the dimensions of the bitmap
            BitmapFactory.Options bmOptions = new BitmapFactory.Options();
            bmOptions.inJustDecodeBounds = true;

            int photoW = bmOptions.outWidth;
            int photoH = bmOptions.outHeight;

            // Determine how much to scale down the image
            int scaleFactor = Math.min(photoW / targetW, photoH / targetH);

            // Decode the image file into a Bitmap sized to fill the View
            bmOptions.inJustDecodeBounds = false;
            bmOptions.inSampleSize = scaleFactor;
            bmOptions.inPurgeable = true;

            Bitmap bitmap = BitmapFactory.decodeFile(currentPath, bmOptions);
            imageView.setImageBitmap(bitmap);
        }
    }

    private void updateState(int stateNumber){
        this.state = stateNumber;

        switch(stateNumber){
            case loadImages:
                break;
        }
        updateGUIState();
    }

    private void updateGUIState(){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                LinearLayout imageLayout = findViewById(R.id.imageLayout);
                DisplayMetrics displayMetrics = new DisplayMetrics();
                getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
                ImageView view;
                switch(state){
                    case loadImages:
                        imageLayout.removeAllViewsInLayout();

                        for(String currentPath : ImageManager.getFilePaths()){
                            System.out.println("CURRENT PATH" + currentPath + "********************************************");
                            view = new ImageView(imageLayout.getContext());
                            int height = displayMetrics.heightPixels/5;
                            int width = displayMetrics.widthPixels/2;
                            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(width,height);
                            view.setLayoutParams(params);
                            view.setMaxHeight(150);
                            view.setMaxWidth(LinearLayout.LayoutParams.MATCH_PARENT);

                            view.setImageBitmap(BitmapFactory.decodeFile(currentPath));
                            view.requestLayout();
                            imageLayout.addView(view);
                        }
                        break;
                }
            }
        });
    }
}
