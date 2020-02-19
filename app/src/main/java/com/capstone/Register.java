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

    //Set the register button
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
                        spinner.setVisibility(View.VISIBLE);    //Display the progress bar for the user
                        spinner.setIndeterminate(true);
                    }
                });
                register();
            }
        });
    }

    //Set the back button
    private void setBackButton(){
        Button back = findViewById(R.id.backButton);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();   //End the activity and return to the last activity
            }
        });
    }

    //Start a dashboard activity
    private void goToDashboard(){
        Intent intent = new Intent(this, DashboardActivity.class);
        startActivity(intent);
    }

    //Add a POST request to the queue and try to register a user
    public void register() {
        ra.addPOSTToQueue(Paths.getRegisterPath(), "username="+getUsername()+"&password="+getPassword());
        Thread check = new Thread(){    //Create a new thread so the main thread isn't blocked
            @Override
            public void run() {
                super.run();
                checkForResponse();
            }
        };
        check.start();
    }

    //Get the username text
    private String getUsername(){
        EditText username = findViewById(R.id.usernameText);
        return String.valueOf(username.getText());
    }

    //Get the password text
    private String getPassword(){
        EditText password = findViewById(R.id.passwordText);
        return String.valueOf(password.getText());
    }

    //Check for a response from the server
    private void checkForResponse(){
        String response = ra.getResponse(); //Wait for a response. This is a blocking call in RequestActions thread
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                ProgressBar spinner = findViewById(R.id.registerProgressBar);
                spinner.setVisibility(View.INVISIBLE);  //Hide the spinner
            }
        });

//        System.out.println(response);

        if(response.contains("200")){   //Registration was successful
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
            if(response.contains("token")) {    //Login was successful
                Credentials.setToken(response.substring(response.indexOf("Bearer"), response.length() - 2));
                goToDashboard();
            }
        } else if(response.contains("Username taken")) {
            error("Username Taken");
        } else {
            error("Invalid Credentials");
        }
    }

    //Call if a request returned a 4xx.
    // i.e. 400 bad request (username taken)
    private void error(String reason){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                TextView usernameColon = findViewById(R.id.usernameColon);
                TextView passwordColon = findViewById(R.id.passwordColon);
                usernameColon.setTextColor(Color.RED);
                passwordColon.setTextColor(Color.RED);
                setStatus(reason);
            }
        });
    }

    //Set the status text
    private void setStatus(String status){
        TextView statusText = findViewById(R.id.statusText);
        statusText.setText(status);
    }

}
