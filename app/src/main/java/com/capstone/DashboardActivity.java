package com.capstone;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.loader.content.CursorLoader;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.Toast;

import java.io.File;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DashboardActivity extends AppCompatActivity {

    FileService fileService;
    Button buttonChooseFile, buttonUpload;
    String imagePath;
    ImageView imageView;
    private int state = 0;

    // Storage Permissions
    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        buttonChooseFile = findViewById(R.id.chooseImageButton);
        buttonUpload = findViewById(R.id.uploadImageButton);
//        fileService = APIUtils.getFileService();

        buttonUpload.setEnabled(false);

        int permission = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);

        if (permission != PackageManager.PERMISSION_GRANTED) {
            buttonChooseFile.setEnabled(false);
            Toast.makeText(this, "Permissions not granted", Toast.LENGTH_LONG).show();
            // We don't have permission so prompt the user
            ActivityCompat.requestPermissions(
                    this,
                    PERMISSIONS_STORAGE,
                    REQUEST_EXTERNAL_STORAGE
            );
        } else {
            buttonChooseFile.setEnabled(true);
        }

        setButtonCaptureImage();
        setButtonChooseFile();
        setButtonUpload();
        setDashboardImage();
        setCameraImage();
    }



    //
    private void setButtonCaptureImage(){
        Button captureImage = findViewById(R.id.captureImageButton);
        imageView = findViewById(R.id.capturedImage);

        captureImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                if (intent.resolveActivity(getPackageManager()) != null) {
                    startActivityForResult(intent, 1);
                }
            }
        });
    }

    // Set choose file button.
    private void setButtonChooseFile(){
//        buttonChooseFile = findViewById(R.id.buttonChooseFile);
        buttonChooseFile.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("image/*");
                startActivityForResult(intent, 0);
                //setImageOnPage();   //doesn't work

                buttonUpload.setEnabled(true);
            }
        });
    }

    // Set upload button.
    private void setButtonUpload(){
        buttonUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                File file = new File(imagePath);
                RequestBody requestBody = RequestBody.create(MediaType.parse("multipart/form-data"), file);
                MultipartBody.Part body = MultipartBody.Part.createFormData("myFile", file.getName(), requestBody);

                Call<FileInfo> call = fileService.upload(body);
                call.enqueue(new Callback<FileInfo>() {
                    @Override
                    public void onResponse(Call<FileInfo> call, Response<FileInfo> response) {
                        if(response.isSuccessful()){
                            String message = response.toString();
                            System.out.println( "*************************************Upload Successful: " + message);
                        }
                    }

                    @Override
                    public void onFailure(Call<FileInfo> call, Throwable t) {
                        System.out.println("ERROR: " + t.getMessage());
                    }
                });
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == 0) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                buttonChooseFile.setEnabled(true);
                buttonUpload.setEnabled(true);
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == RESULT_OK) {
            if (data == null) {
                Toast.makeText(this, "Unable to choose image.", Toast.LENGTH_SHORT).show();
                return;
            }
            Uri imageUri = data.getData();
            imagePath = getReadPathFromUri(imageUri);
        }
    }

    private String getReadPathFromUri(Uri uri){
        String[] projection = {MediaStore.Images.Media.DATA};
        CursorLoader loader = new CursorLoader(getApplicationContext(), uri, projection, null, null, null);
        Cursor cursor = loader.loadInBackground();
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        String result = cursor.getString(column_index);
        cursor.close();
        return result;
    }

    private void setImageOnPage(){
        ImageView myImageView = findViewById(R.id.capturedImage);
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        Bitmap myBitmap = BitmapFactory.decodeFile(imagePath, bmOptions);
        myImageView.setImageBitmap(myBitmap);
    }

    private void setDashboardImage(){
        ImageView dashboardImage = findViewById(R.id.dashboardImageView);
        dashboardImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                state = 0;
                updateGUIState();
            }
        });
    }

    private void setCameraImage(){
        ImageView cameraImage = findViewById(R.id.cameraImageView);
        cameraImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                state = 1;
                updateGUIState();
            }
        });
    }

    private void updateGUIState(){
        runOnUiThread(new Runnable() {

            public void run(){
                Button captureImageButton = findViewById(R.id.captureImageButton);
                Button chooseImageButton = findViewById(R.id.chooseImageButton);
                Button uploadImageButton = findViewById(R.id.uploadImageButton);
                ScrollView entriesScrollView = findViewById(R.id.entriesScrollView);

                switch(state) {
                    case 0: //Dashboard Clicked
                        captureImageButton.setVisibility(View.INVISIBLE);
                        chooseImageButton.setVisibility(View.INVISIBLE);
                        uploadImageButton.setVisibility(View.INVISIBLE);
                        entriesScrollView.setVisibility(View.VISIBLE);
                        break;
                    case 1: //Camera Clicked
                        captureImageButton.setVisibility(View.VISIBLE);
                        chooseImageButton.setVisibility(View.VISIBLE);
                        uploadImageButton.setVisibility(View.VISIBLE);
                        entriesScrollView.setVisibility(View.INVISIBLE);
                        break;
                    default:

                }

            }
        });
    }

}
