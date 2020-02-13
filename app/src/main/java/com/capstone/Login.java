package com.capstone;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.capstone.TCP_Client.Credentials;
import com.capstone.TCP_Client.POSTRequest;
import com.capstone.TCP_Client.Paths;
import com.capstone.TCP_Client.RequestActions;

public class Login extends AppCompatActivity {

    //Used to send GET and POST requests using sockets
    RequestActions ra;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        ra = new RequestActions();
        ra.start();

        setLoginButton();
    }

    //Set the login button. Specifically implement the onClick functionality
    private void setLoginButton(){
        Button launchDashboard = findViewById(R.id.submitButton);
        launchDashboard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //Calls the UI thread for an update
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        setStatus("Authenticating...");
                    }
                });

                login();    //Creates and sends the request
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

    //Set the status textView with the provided String
    private void setStatus(String status){
        TextView statusText = findViewById(R.id.statusText);
        statusText.setText(status);
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
                checkForToken();
            }
        };
        check.start();
    }

    //Scan the response for a token
    private void checkForToken(){
        String response = ra.getResponse();
        if(response.contains("token")){
            Credentials.setToken(response.substring(response.indexOf("Bearer"), response.length()-2));
            goToDashboard();
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
}
