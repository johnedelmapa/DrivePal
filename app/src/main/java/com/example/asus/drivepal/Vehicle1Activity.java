package com.example.asus.drivepal;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class Vehicle1Activity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "ViewDatabase";
    private ProgressBar progressBar;
    private EditText editTextManufacturer, editTextModel, editTextType, editTextColor, editTextPlateNo, editTextEngineNo;


    private FirebaseDatabase mFirebaseDatabase;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private DatabaseReference myRef;
    private String userID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vehicle1);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("Vehicle One");

        mAuth = FirebaseAuth.getInstance();
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        myRef = mFirebaseDatabase.getReference().child("Vehicles");
        FirebaseUser user = mAuth.getCurrentUser();
        userID = user.getUid();

        editTextManufacturer = (EditText) findViewById(R.id.editTextManufacturer);
        editTextModel = (EditText) findViewById(R.id.editTextModel);
        editTextType = (EditText) findViewById(R.id.editTextType);
        editTextColor = (EditText) findViewById(R.id.editTextColor);
        editTextPlateNo = (EditText) findViewById(R.id.editTextPlateNo);
        editTextEngineNo = (EditText) findViewById(R.id.editTextEngineNo);
        progressBar = (ProgressBar) findViewById(R.id.progressbar);

        findViewById(R.id.buttonAdd).setOnClickListener(this);

        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    // User is signed in
                    Log.d(TAG, "onAuthStateChanged:signed_in:" + user.getUid());
                    toastMessage("Successfully signed in with: " + user.getEmail());
                } else {
                    // User is signed out
                    Log.d(TAG, "onAuthStateChanged:signed_out");
                    toastMessage("Successfully signed out.");
                }
                // ...
            }

        };

        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                showData(dataSnapshot);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void showData(DataSnapshot dataSnapshot) {
        for(DataSnapshot ds : dataSnapshot.getChildren()){
            Vehicle uInfo = new Vehicle();
            uInfo.setManufacturer(ds.child(userID).getValue(Vehicle.class).getManufacturer());
            uInfo.setModel(ds.child(userID).getValue(Vehicle.class).getModel());
            uInfo.setType(ds.child(userID).getValue(Vehicle.class).getType());
            uInfo.setColor(ds.child(userID).getValue(Vehicle.class).getColor());
            uInfo.setPlateno(ds.child(userID).getValue(Vehicle.class).getPlateno());
            uInfo.setEngineno(ds.child(userID).getValue(Vehicle.class).getEngineno());

//            //display all the information
//            Log.d(TAG, "showData: manufacturer: " + uInfo.getManufacturer());
//            Log.d(TAG, "showData: model: " + uInfo.getModel());
//            Log.d(TAG, "showData: type: " + uInfo.getType());
//            Log.d(TAG, "showData: color: " + uInfo.getColor());
//            Log.d(TAG, "showData: plateno: " + uInfo.getPlateno());
//            Log.d(TAG, "showData: engineno: " + uInfo.getEngineno());


            editTextManufacturer.setText(uInfo.getManufacturer());
            editTextModel.setText(uInfo.getModel());
            editTextType.setText(uInfo.getType());

            editTextColor.setText(uInfo.getColor());
            editTextPlateNo.setText(uInfo.getPlateno());
            editTextEngineNo.setText(uInfo.getEngineno());
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }

    private void toastMessage(String message){
        Toast.makeText(this,message,Toast.LENGTH_SHORT).show();
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
                    Toast.makeText(Vehicle1Activity.this, "Vehicle Registered", Toast.LENGTH_SHORT).show();
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
