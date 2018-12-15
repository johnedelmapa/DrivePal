package com.example.asus.drivepal;

import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class VehiclesActivity extends AppCompatActivity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vehicles);

        findViewById(R.id.textViewVehicle1).setOnClickListener(this);
        findViewById(R.id.textViewVehicle2).setOnClickListener(this);
        findViewById(R.id.textViewVehicle3).setOnClickListener(this);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("Vehicles");
    }

    @Override
    public void onClick(View view) {
        switch(view.getId()){
            case R.id.textViewVehicle1:
                startActivity(new Intent(this, Vehicle1Activity.class));
                break;
            case R.id.textViewVehicle2:
                startActivity(new Intent(this, Vehicle2Activity.class));
                break;
            case R.id.textViewVehicle3:
                startActivity(new Intent(this, Vehicle3Activity.class));

        }
    }
}
