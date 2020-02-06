package com.capstone;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.capstone.TCP_Client.RequestActions;

public class LoginActivity extends AppCompatActivity {

    RequestActions ra;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        ra = new RequestActions(this);
        ra.start();

        setSubmitButton();
    }

    private void setSubmitButton(){
        Button launchDashboard = findViewById(R.id.submitButton);
        launchDashboard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ra.login(getUsername(), getPassword());
            }
        });
    }

    private String getUsername(){
        EditText username = findViewById(R.id.usernameText);
        return String.valueOf(username.getText());
    }

    private String getPassword(){
        EditText password = findViewById(R.id.passwordText);
        return String.valueOf(password.getText());
    }

    public void goToDashboard(){
        Intent intent = new Intent(this, DashboardActivity.class);
        startActivity(intent);
    }

}
