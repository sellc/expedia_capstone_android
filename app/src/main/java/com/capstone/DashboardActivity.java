package com.capstone;

import android.Manifest;
import android.content.Context;
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
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.capstone.Retrofit_Services.FileService;
import com.capstone.TCP_Client.Paths;

import org.w3c.dom.Text;

import java.io.File;
import java.util.Scanner;

public class DashboardActivity extends AppCompatActivity {

    private int state;
    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };
    private DisplayMetrics displayMetrics = new DisplayMetrics();
    private int height = 0;
    private int width = 0;

    private final int loadImages = 0;
    private final int imageClicked = 1;
    private String clickedImageFilePath = "";
    private String classifications = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_dashboard);

        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        height = displayMetrics.heightPixels/3;
        width = displayMetrics.widthPixels/2;

        ImageManager.readInClassifiedImages();
        setCameraImage();

        int permission = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);

        if (permission != PackageManager.PERMISSION_GRANTED) {
            // We don't have permission so prompt the user
            ActivityCompat.requestPermissions(
                    this,
                    PERMISSIONS_STORAGE,
                    REQUEST_EXTERNAL_STORAGE
            );
        }
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

    private void updateState(int stateNumber){
        this.state = stateNumber;

        switch(stateNumber){
            case loadImages:
                break;
            case imageClicked:
                classifications = ImageManager.getClassifications(clickedImageFilePath);
                Scanner classificationParser = new Scanner(classifications);
                while(classificationParser.hasNext()){
                    classifications += classificationParser.next() + "\n";
                }
                break;
        }
        updateGUIState();
    }

    private void updateGUIState(){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                LinearLayout imageLayout = findViewById(R.id.imageLayout);
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(width,height);
                ScrollView resultsDisplay = findViewById(R.id.imageClassifications);
                LinearLayout imageClassificationLinearLayout = findViewById(R.id.imageClassificationsLinearLayout);
                TextView classification = new TextView(imageClassificationLinearLayout.getContext());

                switch(state){
                    case loadImages:
                        imageLayout.removeAllViewsInLayout();
                        for(String currentPath : ImageManager.getFilePaths()){
                            imageLayout.addView(setIndividualImageView(imageLayout.getContext(), params, currentPath));
                        }
                        break;
                    case imageClicked:
                        imageClassificationLinearLayout.removeAllViews();
                        classification.setText(classifications);
                        imageClassificationLinearLayout.addView(classification);
                        resultsDisplay.setVisibility(View.VISIBLE);
                        break;
                }
            }
        });
    }

    private FrameLayout setIndividualFrameLayout(Context context, LinearLayout.LayoutParams params, String imagePath){
        FrameLayout frame = new FrameLayout(context);
        return frame;
    }

    private ImageView setIndividualImageView(Context context, LinearLayout.LayoutParams params, String imagePath){
        ImageView view = new ImageView(context);
        view.setLayoutParams(params);
        view.setBackgroundResource(R.drawable.outline);
        view.setPadding(3, 3, 3, 3);
        view.setImageBitmap(BitmapFactory.decodeFile(imagePath));
        view.requestLayout();
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                clickedImageFilePath = imagePath;
                updateState(imageClicked);
            }
        });
        return view;
    }

    private TextView setIndividualTextView(Context context, LinearLayout.LayoutParams params, String imagePath){
        TextView textClassifications = new TextView(context);
        classifications = ImageManager.getClassifications(imagePath);
        Scanner classificationParser = new Scanner(classifications);
        while(classificationParser.hasNext()){
            classifications += classificationParser.next() + "\n";
        }
        textClassifications.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){

            }
        });
        return textClassifications;
    }
}
