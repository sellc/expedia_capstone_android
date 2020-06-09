package com.capstone;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
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

    private final int attemptToRegister = 0;
    private final int attemptToLogin = 1;
    private final int invalidCredentials = 2;
    private final int usernameTaken = 3;
    private final int successfulRegistration = 4;
    private final int loginOrRegisterToggleSwitch = 5;
    private final int successfulLogin = 6;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
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
                hideKeyboard();
                updateState(loginOrRegisterToggleSwitch); // Switch to register
            }
        });
    }

    //Set the login button. Specifically implement the onClick functionality
    private void setSubmitButton(){
        Button launchDashboard = findViewById(R.id.submitButton);
        launchDashboard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideKeyboard();
                if(registerState){
                    updateState(attemptToRegister);
                } else {
                    updateState(attemptToLogin);
                }
            }
        });
    }

    private void hideKeyboard(){
        try {
            InputMethodManager imm = (InputMethodManager)getSystemService(INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(findViewById(R.id.passwordText).getWindowToken(), 0);
        } catch (Exception e) {
            System.out.println("Keyboard is already minimized");
        }
    }

    //Check for a response from the server
    private void checkForResponse(){
        String response = ra.getResponse(); //Wait for a response. This is a blocking call in RequestActions thread
        if(response.contains("_id")){   //Registration was successful
            updateState(successfulRegistration);
            updateState(attemptToLogin);
        } else if (response.contains("token")){
            updateState(successfulLogin);
            Credentials.setToken(response.substring(response.indexOf("Bearer"), response.length()-2));
            Intent intent = new Intent(this, DashboardActivity.class);
            startActivity(intent);  //Start Dashboard activity
        } else if(response.contains("Username taken")) {
            updateState(usernameTaken);
        } else {
            updateState(invalidCredentials);
        }
    }

    private void updateState(int stateNumber){
        this.state = stateNumber;
        EditText username = findViewById(R.id.usernameText);
        EditText password = findViewById(R.id.passwordText);

        switch(stateNumber){
            case attemptToRegister:
                ra.addPOSTToQueue(Paths.getRegisterPath(), "username="+username.getText()+"&password="+password.getText());
                break;
            case attemptToLogin:
            case successfulRegistration:
                ra.addPOSTToQueue(Paths.getLoginPath(), "username="+username.getText()+"&password="+password.getText());
                break;
            case invalidCredentials:
                break;
            case loginOrRegisterToggleSwitch:
                if(registerState) {
                    registerState = false;
                } else {
                    registerState = true;
                }
                break;
            case successfulLogin:
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
                EditText password = findViewById(R.id.passwordText);

                switch(state){
                    case attemptToRegister:
                        break;
                    case attemptToLogin:
                        break;
                    case invalidCredentials:
                        passwordColon.setTextColor(Color.RED);
                    case usernameTaken:
                        usernameColon.setTextColor(Color.RED);
                        break;
                    case successfulRegistration:
                        break;
                    case successfulLogin:
                        password.setText("");
                        registerState = false;
                    case loginOrRegisterToggleSwitch:
                        if(registerState) {
                            submitButton.setText("Register");
                            registerButton.setText("Already Have an Account?");
                        } else {
                            submitButton.setText("Login");
                            registerButton.setText("Need an Account?");
                        }
                        break;
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
            case attemptToRegister:
            case attemptToLogin:
                return true;
        }
        return false;
    }

    private boolean waitForResponse(){
        switch(state){
            case attemptToRegister:
            case attemptToLogin:
            case successfulRegistration:
                return true;
        }
        return false;
    }

    //Set the status text to match the current state
    private String getStatusText(){
        switch(state){
            case attemptToRegister:
                return "Creating Account...";
            case attemptToLogin:
                return "Authenticating...";
            case invalidCredentials:
                return "Invalid Credentials";
            case usernameTaken:
                return "Username Taken";
            case successfulRegistration:
                return "Success!";
        }
        return "";
    }

}
