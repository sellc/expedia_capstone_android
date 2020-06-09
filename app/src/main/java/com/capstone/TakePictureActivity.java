package com.capstone;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.loader.content.CursorLoader;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.capstone.Retrofit_Services.APIUtils;
import com.capstone.Retrofit_Services.Classification;
import com.capstone.Retrofit_Services.FileService;
import com.capstone.Retrofit_Services.Result;
import com.capstone.TCP_Client.Credentials;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class TakePictureActivity extends AppCompatActivity {

    private static final int REQUEST_SELECT_PHOTO = 0;
    private static final int REQUEST_TAKE_PHOTO = 1;

    // Storage Permissions
    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    FileService fileService;
    Button buttonChooseFile, captureImage, buttonUpload;
    TextView resultTextView;
    ImageView imageView;
    String imagePath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_take_picture);

        buttonChooseFile = findViewById(R.id.chooseImageButton);
        captureImage = findViewById(R.id.captureImageButton);
        buttonUpload = findViewById(R.id.uploadImageButton);
        resultTextView = findViewById(R.id.resultsTextView);
        imageView = findViewById(R.id.capturedImage);

        fileService = APIUtils.getFileService();

        buttonUpload.setEnabled(false);

        // Enable user to grant permissions if required
        int permission = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);

        if (permission != PackageManager.PERMISSION_GRANTED) {
            buttonChooseFile.setEnabled(false);
            //Toast.makeText(this, "Permissions not granted", Toast.LENGTH_LONG).show();
            // We don't have permission so prompt the user
            ActivityCompat.requestPermissions(
                    this,
                    PERMISSIONS_STORAGE,
                    REQUEST_EXTERNAL_STORAGE
            );
        } else {
            buttonChooseFile.setEnabled(true);
        }

        // Initialize button functionality
        setButtonCaptureImage();
        setButtonChooseFile();
        setButtonUpload();
        setDashboardImage();
    }

    // Take a picture using the camera app
    private void setButtonCaptureImage() {
        captureImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                // Ensure that there's a camera activity to handle the intent
                if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
                    // Create the File where the photo should go
                    File photoFile = null;
                    try {
                        photoFile = createImageFile();
                    } catch (IOException ex) {
                        // Error occurred while creating the File
                        Toast.makeText(getApplicationContext(), "Error creating file.", Toast.LENGTH_LONG).show();
                    }
                    // Continue only if the File was successfully created
                    if (photoFile != null) {
                        Uri photoURI = FileProvider.getUriForFile(TakePictureActivity.this,
                                "com.capstone.android.fileprovider",
                                photoFile);
                        takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                        startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
                    }
                }
            }
        });
    }

    // Choose a picture from the gallery
    private void setButtonChooseFile(){
        buttonChooseFile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("image/*");
                startActivityForResult(intent, REQUEST_SELECT_PHOTO);
            }
        });
    }

    // Upload the selected picture to the server
    private void setButtonUpload(){
        buttonUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Clear text box and start spinner
                resultTextView.setText("");
                ProgressBar spinner = findViewById(R.id.progressBar);
                spinner.setVisibility(View.VISIBLE);

                File file = new File(imagePath);
                RequestBody requestBody = RequestBody.create(MediaType.parse("multipart/form-data"), file);
                MultipartBody.Part body = MultipartBody.Part.createFormData("myFile", file.getName(), requestBody);

                String jwt = Credentials.getToken();
                Call<Result> call = fileService.uploadAuth(jwt, body);
                call.enqueue(new Callback<Result>() {
                    @Override
                    public void onResponse(Call<Result> call, Response<Result> response) {
                        spinner.setVisibility(View.GONE);
                        String message = response.raw().toString();
                        if (response.isSuccessful()) {
                            System.out.println( "**********************Upload Successful: " + message);
                            displayResults(response);
                        } else {
                            System.out.println(message);
                        }
                    }

                    @Override
                    public void onFailure(Call<Result> call, Throwable t) {
                        spinner.setVisibility(View.GONE);
                        System.out.println("ERROR: " + t.getMessage());
                        resultTextView.setText("Server is down. Try again later.");
                    }
                });
            }
        });
    }

    // If permissions are granted, allow user to select file from gallery
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

        if (resultCode == RESULT_OK && data != null) {
            if (requestCode == REQUEST_SELECT_PHOTO && data.getData() != null) {
                Uri imageUri = data.getData();
                imagePath = getReadPathFromUri(imageUri);   // Set path as that of picture selected
            } else if (requestCode == REQUEST_TAKE_PHOTO) {
                galleryAddPic();
            }
            displayImage();
        } else {
            Toast.makeText(this, "Unable to choose image.", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onBackPressed() {
        Intent startMain = new Intent(TakePictureActivity.this, Login.class);
        startMain.addCategory(Intent.CATEGORY_HOME);
        startMain.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(startMain);
    }

    // Get the path from the image selected
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

    // Create a file to store the image
    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile (
                imageFileName,      /* prefix */
                ".jpg",      /* suffix */
                storageDir         /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        imagePath = image.getAbsolutePath();
        return image;
    }

    // Add picture to gallery
    private void galleryAddPic() {
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        File f = new File(imagePath);
        Uri contentUri = Uri.fromFile(f);
        mediaScanIntent.setData(contentUri);
        this.sendBroadcast(mediaScanIntent);
    }

    // Display scaled image on page
    private void displayImage() {
        // Get the dimensions of the View
        int targetW = imageView.getWidth();
        int targetH = imageView.getHeight();

        // Get the dimensions of the bitmap
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        bmOptions.inJustDecodeBounds = true;

        int photoW = bmOptions.outWidth;
        int photoH = bmOptions.outHeight;

        // Determine how much to scale down the image
        int scaleFactor = Math.min(photoW/targetW, photoH/targetH);

        // Decode the image file into a Bitmap sized to fill the View
        bmOptions.inJustDecodeBounds = false;
        bmOptions.inSampleSize = scaleFactor;
        bmOptions.inPurgeable = true;

        Bitmap bitmap = BitmapFactory.decodeFile(imagePath, bmOptions);
        imageView.setImageBitmap(bitmap);

        // Allow the user to now upload the image to server
        buttonUpload.setEnabled(true);
    }

    // Display ML classification results on the page
    private void displayResults(Response<Result> response) {
        StringBuilder sb = new StringBuilder();
        if (response.body() != null) {
            Result result = response.body();
//            String id = result.get_id();
//            String title = result.getTitle();
//            String user = result.getUser();
//            String imageUrl = result.getImageUrl();
            List<Classification> classifications = result.getClassification();
            String classificationList = "";
            for (Classification classification : classifications) {
                String className = classification.getClassName();
                double probability = classification.getProbability() * 100;
                sb.append(String.format("%s...... %.2f%%\n\n", className, probability));
                classificationList += "," + className + "," + probability;
            }
            ImageManager.addClassifiedImage(imagePath, classificationList);
            resultTextView.setText(sb.toString());
        } else {
            resultTextView.setText("No Response");
        }
    }

    // Button to switch to dashboard
    private void setDashboardImage(){
        ImageView dashboardImage = findViewById(R.id.dashboardImageView);
        dashboardImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(TakePictureActivity.this, DashboardActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                startActivity(intent);
            }
        });
    }

}
