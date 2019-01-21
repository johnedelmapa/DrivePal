package com.example.asus.drivepal;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;


public class WelcomeActivity extends AppCompatActivity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);


        findViewById(R.id.buttonSignup).setOnClickListener(this);
        findViewById(R.id.buttonLogin).setOnClickListener(this);

    }



    @Override
    public void onClick(View view) {
        switch(view.getId()){
            case R.id.buttonSignup:
                startActivity(new Intent(this, SignupActivity.class));
                break;
            case R.id.buttonLogin:
                startActivity(new Intent(this, LoginActivity.class));
                break;
        }
    }

}
