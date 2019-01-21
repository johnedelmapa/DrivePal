package com.example.asus.drivepal;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.asus.drivepal.models.Vehicle;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Spinner;

import java.util.ArrayList;
import java.util.List;


public class CreateVehicle3 extends AppCompatActivity implements View.OnClickListener, OnItemSelectedListener {

    ProgressBar progressBar;
    EditText editTextType, editTextColor, editTextPlateNo, editTextEngineNo;
    private FirebaseAuth mAuth;

    Spinner spinnerManufacturer, spinnerModel, spinnerColor;

    public String manufacturer1, model1, color1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vehicle);

//        editTextColor = (EditText) findViewById(R.id.editTextColor);
        editTextPlateNo = (EditText) findViewById(R.id.editTextPlateNo);
        editTextEngineNo = (EditText) findViewById(R.id.editTextEngineNo);
        progressBar = (ProgressBar) findViewById(R.id.progressbar);
        mAuth = FirebaseAuth.getInstance();

        spinnerManufacturer = (Spinner) findViewById(R.id.spinnerManufacturer);
        spinnerModel = (Spinner) findViewById(R.id.spinnerModel);
        spinnerManufacturer.setOnItemSelectedListener(this);

        findViewById(R.id.buttonAdd).setOnClickListener(this);


        spinnerColor = (Spinner) findViewById(R.id.spinnerColor);
        spinnerColor.setOnItemSelectedListener(this);

        List<String> categories = new ArrayList<String>();
        categories.add("White");
        categories.add("Black");
        categories.add("Silver");
        categories.add("Grey");
        categories.add("Red");
        categories.add("Blue");
        categories.add("Green");
        categories.add("Yellow");

        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, categories);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerColor.setAdapter(dataAdapter);
    }

    @Override

    public void onItemSelected(AdapterView<?> parent, View arg1, int pos, long arg3) {

        if(parent.getId() == R.id.spinnerColor) {
            String item = parent.getItemAtPosition(pos).toString();
            color1 = item;
        }


        if(parent.getId() == R.id.spinnerManufacturer) {

            parent.getItemAtPosition(pos);
            if (pos == 0) {
                ArrayAdapter<CharSequence> adapter = ArrayAdapter
                        .createFromResource(this, R.array.model_toyota,
                                android.R.layout.simple_spinner_item);
                spinnerModel.setAdapter(adapter);
                String text = spinnerModel.getSelectedItem().toString();
                model1 = getModel(text);

                String text1 = "Toyota";
                manufacturer1 = getManufacturer(text1);

            } else if (pos == 1) {
                ArrayAdapter<CharSequence> adapter = ArrayAdapter
                        .createFromResource(this, R.array.model_ford,
                                android.R.layout.simple_spinner_item);

                spinnerModel.setAdapter(adapter);
                String text = spinnerModel.getSelectedItem().toString();
                model1 = getModel(text);

                String text1 = "Ford";
                manufacturer1 = getManufacturer(text1);

            } else if (pos == 2) {

                ArrayAdapter<CharSequence> adapter = ArrayAdapter
                        .createFromResource(this, R.array.model_honda,
                                android.R.layout.simple_spinner_item);

                spinnerModel.setAdapter(adapter);
                String text = spinnerModel.getSelectedItem().toString();
                model1 = getModel(text);

                String text1 = "Honda";
                manufacturer1 = getManufacturer(text1);
            } else if (pos == 3) {

                ArrayAdapter<CharSequence> adapter = ArrayAdapter
                        .createFromResource(this, R.array.model_isuzu,
                                android.R.layout.simple_spinner_item);

                spinnerModel.setAdapter(adapter);
                String text = spinnerModel.getSelectedItem().toString();
                model1 = getModel(text);

                String text1 = "Isuzu";
                manufacturer1 = getManufacturer(text1);
            } else if (pos == 4) {

                ArrayAdapter<CharSequence> adapter = ArrayAdapter
                        .createFromResource(this, R.array.model_nissan,
                                android.R.layout.simple_spinner_item);

                spinnerModel.setAdapter(adapter);
                String text = spinnerModel.getSelectedItem().toString();
                model1 = getModel(text);

                String text1 = "Nissan";
                manufacturer1 = getManufacturer(text1);
            } else if (pos == 5) {

                ArrayAdapter<CharSequence> adapter = ArrayAdapter
                        .createFromResource(this, R.array.model_kawasaki,
                                android.R.layout.simple_spinner_item);

                spinnerModel.setAdapter(adapter);
                String text = spinnerModel.getSelectedItem().toString();
                model1 = getModel(text);

                String text1 = "Kawasaki";
                manufacturer1 = getManufacturer(text1);
            } else if (pos == 6) {

                ArrayAdapter<CharSequence> adapter = ArrayAdapter
                        .createFromResource(this, R.array.model_yamaha,
                                android.R.layout.simple_spinner_item);

                spinnerModel.setAdapter(adapter);
                String text = spinnerModel.getSelectedItem().toString();
                model1 = getModel(text);

                String text1 = "Yamaha";
                manufacturer1 = getManufacturer(text1);
            }

        }

    }

    @Override

    public void onNothingSelected(AdapterView<?> arg0) {

    }

    public String getManufacturer(String Manufacturer){
        return Manufacturer;
    }

    public String getModel(String Model){
        return Model;
    }


    @Override
    protected void onStart() {
        super.onStart();

        if (mAuth.getCurrentUser() != null) {
            //handle the already login user
        }
    }

    private void AddVehicle(){
        final String manufacturer = manufacturer1.toString().trim();
        final String model = model1.toString().trim();
        final String color = color1.toString().trim();
        final String plateno = editTextPlateNo.getText().toString().trim();
        final String engineno = editTextEngineNo.getText().toString().trim();

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

        final Vehicle vehicle = new Vehicle(
                manufacturer,
                model,
                color,
                plateno,
                engineno
        );

        /////// for validation of plate number and engine number

        final DatabaseReference postRef = FirebaseDatabase.getInstance().getReference("RegVehicle");

        postRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                boolean isExisting = false;
                for(DataSnapshot data: dataSnapshot.getChildren()){
                    String testplate = data.child("PlateNum").getValue().toString();
                    String testengine = data.child("EngineNum").getValue().toString();
                    if (testplate.equals(plateno)&& testengine.equals(engineno)) {

                        //////////////////////////////
                        FirebaseDatabase.getInstance().getReference("Vehicles/VehicleThree/VehicleInfo")
                                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                .setValue(vehicle).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                progressBar.setVisibility(View.GONE);
                                if (task.isSuccessful()) {
                                    //finish();
                                    Toast.makeText(CreateVehicle3.this, "Vehicle Registered Successfully", Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(getApplicationContext(), task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            }
                        }); /// end
                        isExisting = true;
                        break;
                    }
                }
                if(!isExisting)
                    Toast.makeText(getApplicationContext(), "Plate Number and Engine Number doesn't match the database \n Please Try again.", Toast.LENGTH_SHORT).show();

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

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
