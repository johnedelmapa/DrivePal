package com.example.asus.drivepal;

import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.asus.drivepal.models.Vehicle;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class Vehicle2Activity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "ViewDatabase";
    private ProgressBar progressBar;
    private TextView editTextManufacturer, editTextModel, editTextColor, editTextPlateNo, editTextEngineNo;


    private FirebaseDatabase mFirebaseDatabase;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private DatabaseReference myRef;
    private String userID;
    public Button btn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vehicle2);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("Vehicle Two");

        mAuth = FirebaseAuth.getInstance();
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        myRef = mFirebaseDatabase.getReference().child("Vehicles/VehicleTwo");
        FirebaseUser user = mAuth.getCurrentUser();
        userID = user.getUid();

        editTextManufacturer = (TextView) findViewById(R.id.editTextManufacturer);
        editTextModel = (TextView) findViewById(R.id.editTextModel);
        editTextColor = (TextView) findViewById(R.id.editTextColor);
        editTextPlateNo = (TextView) findViewById(R.id.editTextPlateNo);
        editTextEngineNo = (TextView) findViewById(R.id.editTextEngineNo);
        progressBar = (ProgressBar) findViewById(R.id.progressbar);

        findViewById(R.id.buttonAdd).setOnClickListener(this);
        btn = (Button) findViewById(R.id.buttonAdd);

        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                try { showData(dataSnapshot); btn.setEnabled(false);
                } catch(Exception e) {
                    Toast.makeText(Vehicle2Activity.this, "No Registered Vehicle", Toast.LENGTH_SHORT).show();
                }
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
            uInfo.setColor(ds.child(userID).getValue(Vehicle.class).getColor());
            uInfo.setPlateno(ds.child(userID).getValue(Vehicle.class).getPlateno());
            uInfo.setEngineno(ds.child(userID).getValue(Vehicle.class).getEngineno());

            editTextManufacturer.setText(uInfo.getManufacturer());
            editTextModel.setText(uInfo.getModel());

            editTextColor.setText(uInfo.getColor());
            editTextPlateNo.setText(uInfo.getPlateno());
            editTextEngineNo.setText(uInfo.getEngineno());
        }
    }

    @Override
    public void onClick(View view) {
        switch(view.getId()) {
            case R.id.buttonAdd:
                startActivity(new Intent(this, CreateVehicle2.class));
                break;
        }
    }
}
