package com.capstone;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.capstone.TCP_Client.Credentials;
import com.capstone.TCP_Client.Paths;
import com.capstone.TCP_Client.RequestActions;

public class Register extends AppCompatActivity {

    RequestActions ra;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        ra = new RequestActions();
        ra.start();

        ProgressBar spinner = findViewById(R.id.registerProgressBar);
        spinner.setVisibility(View.INVISIBLE);

        setBackButton();
        setRegisterButton();
    }

    private void setRegisterButton(){
        Button register = findViewById(R.id.submitButton);
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        setStatus("Creating Account...");
                        ProgressBar spinner = findViewById(R.id.registerProgressBar);
                        spinner.setVisibility(View.VISIBLE);
                        spinner.setIndeterminate(true);
                    }
                });
                register();
            }
        });
    }

    private void setBackButton(){
        Button back = findViewById(R.id.backButton);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void goToDashboard(){
        Intent intent = new Intent(this, DashboardActivity.class);
        startActivity(intent);
    }

    public void register() {
        ra.addPOSTToQueue(Paths.getRegisterPath(), "username="+getUsername()+"&password="+getPassword());
        Thread check = new Thread(){
            @Override
            public void run() {
                super.run();
                checkForToken();
            }
        };
        check.start();
    }

    private String getUsername(){
        EditText username = findViewById(R.id.usernameText);
        return String.valueOf(username.getText());
    }

    private String getPassword(){
        EditText password = findViewById(R.id.passwordText);
        return String.valueOf(password.getText());
    }

    private void checkForToken(){
        String response = ra.getResponse();
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                ProgressBar spinner = findViewById(R.id.registerProgressBar);
                spinner.setVisibility(View.INVISIBLE);
            }
        });

        System.out.println(response);

        if(response.contains("200")){
            ra.addPOSTToQueue(Paths.getLoginPath(), "username="+getUsername()+"&password="+getPassword());
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    setStatus("Success! Logging In...");
                    ProgressBar spinner = findViewById(R.id.registerProgressBar);
                    spinner.setVisibility(View.VISIBLE);
                }
            });
            response = ra.getResponse();
            if(response.contains("token")) {
                Credentials.setToken(response.substring(response.indexOf("Bearer"), response.length() - 2));
                goToDashboard();
            }
        } else if(response.contains("Username taken")) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    TextView usernameColon = findViewById(R.id.usernameColon);
                    TextView passwordColon = findViewById(R.id.passwordColon);
                    usernameColon.setTextColor(Color.RED);
                    passwordColon.setTextColor(Color.RED);
                    setStatus("Username Taken");
                }
            });
        } else {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    TextView usernameColon = findViewById(R.id.usernameColon);
                    TextView passwordColon = findViewById(R.id.passwordColon);
                    usernameColon.setTextColor(Color.RED);
                    passwordColon.setTextColor(Color.RED);
                    setStatus("Invalid Credentials");
                }
            });
        }
    }

    private void setStatus(String status){
        TextView statusText = findViewById(R.id.statusText);
        statusText.setText(status);
    }

    public void login() {
        ra.addPOSTToQueue(Paths.getLoginPath(), "username="+getUsername()+"&password="+getPassword());
        Thread check = new Thread(){
            @Override
            public void run() {
                super.run();
                checkForToken();
            }
        };
        check.start();
    }

}
