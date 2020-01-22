package com.capstone;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class LoginActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

//        double start = System.currentTimeMillis();
//        TextView status = findViewById(R.id.statusTextView);
//        ProgressBar progress = findViewById(R.id.progressBar);
//        progress.setProgress(25);
//        status.setText("Pinging Server");

//        while(System.currentTimeMillis() - start < 5000 && status.getText().equals("Pinging Server"));
//        while(System.currentTimeMillis() - start < 15000);

//        progress.setProgress(50);
        setDashboardButton();

    }

    private void setDashboardButton(){
        Button launchDashboard = findViewById(R.id.submitButton);
        launchDashboard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToDashboard();
            }
        });
    }

    private void goToDashboard(){
        Intent intent = new Intent(this, DashboardActivity.class);
        startActivity(intent);
    }
}
