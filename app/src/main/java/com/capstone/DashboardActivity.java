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
import android.widget.Toast;

import java.io.File;
import java.io.IOException;

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

        buttonChooseFile = findViewById(R.id.buttonChooseFile);
        buttonUpload = findViewById(R.id.uploadImageButton);
        fileService = APIUtils.getFileService();

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

        //TODO: access the new image file path
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

                buttonUpload.setEnabled(true);
            }
        });
    }

    // Set upload button.
    private void setButtonUpload(){
        buttonUpload = findViewById(R.id.uploadImageButton);
        //TODO: handle situation where button is clicked but no image has been selected

        buttonUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                File file = new File(imagePath);
                RequestBody requestBody = RequestBody.create(MediaType.parse("multipart/form-data"), file);
                //RequestBody requestBody = RequestBody.create(MediaType.parse("application/json;charset=utf-8"), file);
                MultipartBody.Part body = MultipartBody.Part.createFormData("myFile", file.getName(), requestBody);
                System.out.println(body);

                Call<FileInfo> call = fileService.upload(body);
                call.enqueue(new Callback<FileInfo>() {
                    @Override
                    public void onResponse(Call<FileInfo> call, Response<FileInfo> response) {
                        String message = response.raw().toString();
                        if (response.isSuccessful()) {
                            System.out.println( "*************************************Upload Successful: " + message);
                        } else {
                            System.out.println("*************************??: " + message);
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

    // Check this method
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == 1) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                buttonChooseFile.setEnabled(true);
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK && data != null && data.getData() != null) {
            Uri imageUri = data.getData();
            imagePath = getReadPathFromUri(imageUri);

            //Display selected image on page
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imageUri);
                ImageView imageView = findViewById(R.id.capturedImage);
                imageView.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            Toast.makeText(this, "Unable to choose image.", Toast.LENGTH_SHORT).show();
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

}
