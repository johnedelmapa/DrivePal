package com.example.asus.drivepal;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.asus.drivepal.models.Contact;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

public class ContactActivity extends AppCompatActivity implements View.OnClickListener {

    ProgressBar progressBar;
    EditText editTextGivenname, editTextMiddlename, editTextFamilyname, editTextphoneNumber, editTextEmail, editTextRelationship;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact);

        editTextGivenname = (EditText) findViewById(R.id.editTextGivenname);
        editTextMiddlename = (EditText) findViewById(R.id.editTextMiddlename);
        editTextFamilyname = (EditText) findViewById(R.id.editTextFamilyname);
        editTextphoneNumber = (EditText) findViewById(R.id.editTextphoneNumber);
        editTextEmail = (EditText) findViewById(R.id.editTextEmail);
        editTextRelationship = (EditText) findViewById(R.id.editTextRelationship);
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

    private void AddContact(){

        final String givenname = editTextGivenname.getText().toString().trim();
        final String middlename = editTextMiddlename.getText().toString().trim();
        final String familyname = editTextFamilyname.getText().toString().trim();
        final String phonenumber = editTextphoneNumber.getText().toString().trim();
        final String email = editTextEmail.getText().toString().trim();
        final String relationship = editTextRelationship.getText().toString().trim();

        if(givenname.isEmpty()) {
            editTextGivenname.setError("Given name is required");
            editTextGivenname.requestFocus();
            return;
        }


        if(middlename.isEmpty()) {
            editTextMiddlename.setError("Middle name is required");
            editTextMiddlename.requestFocus();
            return;
        }

        if(familyname.isEmpty()) {
            editTextFamilyname.setError("Family name is required");
            editTextFamilyname.requestFocus();
            return;
        }

        if(phonenumber.isEmpty()) {
            editTextphoneNumber.setError("Phone number is required");
            editTextphoneNumber.requestFocus();
            return;
        }


        if(email.isEmpty()) {
            editTextEmail.setError("Email is required");
            editTextEmail.requestFocus();
            return;
        }

        if(relationship.isEmpty()) {
            editTextRelationship.setError("Relationship is required");
            editTextRelationship.requestFocus();
            return;
        }

        progressBar.setVisibility(View.VISIBLE);

        Contact contact = new Contact(
                givenname,
                middlename,
                familyname,
                phonenumber,
                email,
                relationship
        );

        FirebaseDatabase.getInstance().getReference("Contacts/ContactOne")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .setValue(contact).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                progressBar.setVisibility(View.GONE);
                if (task.isSuccessful()) {
                    //finish();
                    startActivity(new Intent(ContactActivity.this, DashboardActivity.class));
                    Toast.makeText(ContactActivity.this, "Welcome to DrivePal", Toast.LENGTH_LONG).show();
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
                AddContact();
                break;
        }
    }
}
