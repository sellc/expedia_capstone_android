package com.capstone;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class LoginActivity extends AppCompatActivity {

    Connection c;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //c = new Connection();
        //c.start();
//        setStatusText("Authenticating...");

        setSubmitButton();
    }

    private void setSubmitButton(){
        Button launchDashboard = findViewById(R.id.submitButton);
        launchDashboard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setStatusText("Authenticating...");
                EditText username = findViewById(R.id.usernameText);
                EditText password = findViewById(R.id.passwordText);
                //Connection.setUserInput("{Username:" + username.getText() + "}{Password:" + password.getText()+ "}");

//                while(!Connection.hasNewResponse());
//                System.out.println(Connection.getServerResponse());


//                if(Connection.getServerResponse().equals("1")){
//                    setStatusText("Login Successful");
                    goToDashboard();
//                } else {
//                    setStatusText("Invalid Credentials");
//                }
            }
        });
    }

    private void goToDashboard(){
        setStatusText("");
        Intent intent = new Intent(this, DashboardActivity.class);
        startActivity(intent);
    }

    private void setStatusText(String status){
        TextView statusText = findViewById(R.id.statusTextView);
        statusText.setText(status);
    }
}
