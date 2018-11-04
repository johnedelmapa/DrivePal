package com.example.asus.drivepal;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.asus.drivepal.models.Vehicle;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

public class VehicleActivity extends AppCompatActivity implements View.OnClickListener {

    ProgressBar progressBar;
    EditText editTextManufacturer, editTextModel, editTextType, editTextColor, editTextPlateNo, editTextEngineNo;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vehicle);

        editTextManufacturer = (EditText) findViewById(R.id.editTextManufacturer);
        editTextModel = (EditText) findViewById(R.id.editTextModel);
        editTextType = (EditText) findViewById(R.id.editTextType);
        editTextColor = (EditText) findViewById(R.id.editTextColor);
        editTextPlateNo = (EditText) findViewById(R.id.editTextPlateNo);
        editTextEngineNo = (EditText) findViewById(R.id.editTextEngineNo);
        progressBar = (ProgressBar) findViewById(R.id.progressbar);

        mAuth = FirebaseAuth.getInstance();

        findViewById(R.id.buttonAdd).setOnClickListener(this);
    }

    @Override
    protected void onStart() {
        super.onStart();

        if (mAuth.getCurrentUser() != null) {
            //handle the already login user
        }
    }

    private void AddVehicle(){
        final String manufacturer = editTextManufacturer.getText().toString().trim();
        final String model = editTextModel.getText().toString().trim();
        final String type = editTextType.getText().toString().trim();
        final String color = editTextColor.getText().toString().trim();
        final String plateno = editTextPlateNo.getText().toString().trim();
        final String engineno = editTextEngineNo.getText().toString().trim();

        if(manufacturer.isEmpty()) {
            editTextManufacturer.setError("Vehicle Manufacturer is required");
            editTextManufacturer.requestFocus();
            return;
        }


        if(model.isEmpty()) {
            editTextModel.setError("Vehicle Model is required");
            editTextModel.requestFocus();
            return;
        }

        if(type.isEmpty()) {
            editTextType.setError("Vehicle Type Password is required");
            editTextType.requestFocus();
            return;
        }

        if(color.isEmpty()) {
            editTextColor.setError("Vehicle Color is required");
            editTextColor.requestFocus();
            return;
        }


        if(plateno.isEmpty()) {
            editTextPlateNo.setError("Vehicle Plate No. is required");
            editTextPlateNo.requestFocus();
            return;
        }

        if(engineno.isEmpty()) {
            editTextType.setError("Vehicle Engine No. Password is required");
            editTextType.requestFocus();
            return;
        }

        progressBar.setVisibility(View.VISIBLE);

        Vehicle vehicle = new Vehicle(
                manufacturer,
                model,
                type,
                color,
                plateno,
                engineno
        );

        FirebaseDatabase.getInstance().getReference("Vehicles/VehicleOne")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .setValue(vehicle).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                progressBar.setVisibility(View.GONE);
                if (task.isSuccessful()) {
                    //finish();
                    startActivity(new Intent(VehicleActivity.this, ContactActivity.class));
                } else {
                    Toast.makeText(getApplicationContext(), task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });

    }


    @Override
    public void onClick(View view) {
        switch(view.getId()) {
            case R.id.buttonAdd:
                AddVehicle();
                break;
        }
    }
}
