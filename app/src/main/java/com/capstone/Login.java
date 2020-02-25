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

public class Login extends AppCompatActivity {

    //Used to send GET and POST requests using sockets
    private RequestActions ra;
    private boolean registerState = false;
    private int state;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        ra = new RequestActions();
        ra.start();

        setSubmitButton();
        setRegisterButton();
    }

    private void setRegisterButton(){
        Button registerButton = findViewById(R.id.registerButton);
        registerButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Button submitButton = findViewById(R.id.submitButton);
                Button registerButton = findViewById(R.id.registerButton);
                if(registerState == false) {
                    registerState = true;
                    submitButton.setText("Register");
                    registerButton.setText("Already Have an Account?");
                } else {
                    registerState = false;
                    submitButton.setText("Login");
                    registerButton.setText("Need an Account?");
                }
            }
        });
    }

    //Set the login button. Specifically implement the onClick functionality
    private void setSubmitButton(){
        Button launchDashboard = findViewById(R.id.submitButton);
        launchDashboard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(registerState){
                    state = 0;
                    updateGUIState();
                    register();
                } else {
                    state = 1;
                    updateGUIState();
                    login();    //Creates and sends the request
                }
                toggleSpinner();
            }
        });
    }

    //Get the text from the username field
    private String getUsername(){
        EditText username = findViewById(R.id.usernameText);
        return String.valueOf(username.getText());
    }

    //Get the text from the password field
    private String getPassword(){
        EditText password = findViewById(R.id.passwordText);
        return String.valueOf(password.getText());
    }

    private void toggleSpinner(){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                ProgressBar spinner = findViewById(R.id.registerProgressBar);
                if(spinner.getVisibility() == View.INVISIBLE) {
                    spinner.setVisibility(View.VISIBLE);    //Display the progress bar for the user
                } else {
                    spinner.setVisibility(View.INVISIBLE);    //Display the progress bar for the user
                }
            }
        });

    }

    //Launches the dashboard activity
    public void goToDashboard(){
        Intent intent = new Intent(this, DashboardActivity.class);
        startActivity(intent);
    }

    //Add a login POST request to the queue
    public void login() {
        ra.addPOSTToQueue(Paths.getLoginPath(), "username="+getUsername()+"&password="+getPassword());

        //Create a thread to check for a token otherwise it blocks the main thread
        Thread check = new Thread(){
            @Override
            public void run() {
                super.run();
                checkForResponse();
            }
        };
        check.start();
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

    //Check for a response from the server
    private void checkForResponse(){
        String response = ra.getResponse(); //Wait for a response. This is a blocking call in RequestActions thread
        if(response.contains("_id")){   //Registration was successful
            ra.addPOSTToQueue(Paths.getLoginPath(), "username="+getUsername()+"&password="+getPassword());
            state = 4;
            updateGUIState();
            checkForResponse();
        } else if (response.contains("token")){
            Credentials.setToken(response.substring(response.indexOf("Bearer"), response.length()-2));
            goToDashboard();
        } else if(response.contains("Username taken")) {
            state = 3;
            updateGUIState();
        } else {
            state = 2;
            updateGUIState();
        }
    }

    //Call if a request returned a 4xx.
    // i.e. 400 bad request (username taken)
    private void updateGUIState(){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                switch(state){
                    case 0: //Attempt to Register
                        break;
                    case 1: //Attempt to Login
                        break;
                    case 2: //Invalid Credentials
                        TextView passwordColon = findViewById(R.id.passwordColon);
                        passwordColon.setTextColor(Color.RED);
                    case 3: //Username Taken
                        TextView usernameColon = findViewById(R.id.usernameColon);
                        usernameColon.setTextColor(Color.RED);
                        break;
                    case 4: // Successful Registration

                        break;
                    default:
                }
                TextView statusText = findViewById(R.id.statusText);
                statusText.setText(getStatusText());
            }
        });
    }

    private String getStatusText(){
        switch(state){
            case 0:
                return "Creating Account...";
            case 1:
                return "Authenticating...";
            case 2:
                return "Invalid Credentials";
            case 3:
                return "Username Taken";
            case 4:
                return "Success!";
        }
        return "";
    }

}
