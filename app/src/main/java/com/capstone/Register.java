package com.capstone;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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

        setBackButton();
    }

    private void setRegisterButton(){
        Button register = findViewById(R.id.registerButton);
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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

    private void setStatus(String status){
        TextView statusText = findViewById(R.id.statusText);
        statusText.setText(status);
    }

}
