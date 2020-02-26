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
                if(registerState == false) {
                    updateState(5); // Switch to register
                } else {
                    updateState(6); // Switch to login
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
                    updateState(0); // Login
                } else {
                    updateState(1); // Register
                }
            }
        });
    }

    //Check for a response from the server
    private void checkForResponse(){
        String response = ra.getResponse(); //Wait for a response. This is a blocking call in RequestActions thread
        if(response.contains("_id")){   //Registration was successful
            updateState(4);
        } else if (response.contains("token")){
            Credentials.setToken(response.substring(response.indexOf("Bearer"), response.length()-2));
            Intent intent = new Intent(this, DashboardActivity.class);
            startActivity(intent);  //Start Dashboard activity
        } else if(response.contains("Username taken")) {
            updateState(3);
        } else {
            updateState(2);
        }
    }

    private void updateState(int stateNumber){
        this.state = stateNumber;
        EditText username = findViewById(R.id.usernameText);
        EditText password = findViewById(R.id.passwordText);

        switch(stateNumber){
            case 0:
                ra.addPOSTToQueue(Paths.getRegisterPath(), "username="+username.getText()+"&password="+password.getText());
                break;
            case 1:
            case 4:
                ra.addPOSTToQueue(Paths.getLoginPath(), "username="+username.getText()+"&password="+password.getText());
                break;
            case 2:
                break;
            case 5:
                registerState = true;
                break;
            case 6:
                registerState = false;
                break;
        }
        if(waitForResponse()){
            Thread check = new Thread(){    //Create a new thread so the main thread isn't blocked
                @Override
                public void run() {
                    super.run();
                    checkForResponse();
                }
            };
            check.start();
        }
        updateGUIState();
    }

    //Call if a request returned a 4xx.
    // i.e. 400 bad request (username taken)
    private void updateGUIState(){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Button submitButton = findViewById(R.id.submitButton);
                Button registerButton = findViewById(R.id.registerButton);
                TextView passwordColon = findViewById(R.id.passwordColon);
                TextView usernameColon = findViewById(R.id.usernameColon);
                ProgressBar spinner = findViewById(R.id.registerProgressBar);
                TextView statusText = findViewById(R.id.statusText);

                switch(state){
                    case 0: //Attempt to Register
                        break;
                    case 1: //Attempt to Login
                        break;
                    case 2: //Invalid Credentials
                        passwordColon.setTextColor(Color.RED);
                    case 3: //Username Taken
                        usernameColon.setTextColor(Color.RED);
                        break;
                    case 4: // Successful Registration
                        break;
                    case 5: //Don't have an account
                        submitButton.setText("Register");
                        registerButton.setText("Already Have an Account?");
                        break;
                    case 6: //Already have an account
                        submitButton.setText("Login");
                        registerButton.setText("Need an Account?");
                        break;
                    default:
                }
                if(getSpinnerVisibility()){
                    spinner.setVisibility(View.VISIBLE);
                } else {
                    spinner.setVisibility(View.INVISIBLE);
                }
                statusText.setText(getStatusText());
            }
        });
    }

    //Add the state numbers for which the spinner should spin
    private boolean getSpinnerVisibility(){
        switch(state){
            case 0:
            case 1:
                return true;
        }
        return false;
    }

    private boolean waitForResponse(){
        switch(state){
            case 0:
            case 1:
            case 4:
                return true;
        }
        return false;
    }

    //Set the status text to match the current state
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
