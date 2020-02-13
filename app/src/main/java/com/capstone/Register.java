package com.capstone;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.capstone.TCP_Client.RequestActions;

public class Register extends AppCompatActivity {

    RequestActions ra;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        ra = new RequestActions();
        ra.start();

    }
}
