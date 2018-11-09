package com.example.asus.drivepal;

import android.support.annotation.NonNull;
import android.support.constraint.solver.widgets.Snapshot;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;
import com.example.asus.drivepal.models.Contact;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class Contact2Activity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "ViewDatabase2";
    private ProgressBar progressBar;
    private EditText editTextGivenname, editTextMiddlename, editTextFamilyname, editTextphoneNumber, editTextEmail, editTextRelationship;

    private FirebaseDatabase mFirebaseDatabase;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private DatabaseReference myRef;
    private String userID;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact2);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("Contact Two");

        mAuth = FirebaseAuth.getInstance();
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        myRef = mFirebaseDatabase.getReference().child("Contacts/ContactTwo");
        FirebaseUser user = mAuth.getCurrentUser();
        userID = user.getUid();

        editTextGivenname = (EditText) findViewById(R.id.editTextGivenname2);
        editTextMiddlename = (EditText) findViewById(R.id.editTextMiddlename2);
        editTextFamilyname = (EditText) findViewById(R.id.editTextFamilyname2);
        editTextphoneNumber = (EditText) findViewById(R.id.editTextphoneNumber2);
        editTextEmail = (EditText) findViewById(R.id.editTextEmail2);
        editTextRelationship = (EditText) findViewById(R.id.editTextRelationship2);
        progressBar = (ProgressBar) findViewById(R.id.progressbar);


        findViewById(R.id.buttonAdd2).setOnClickListener(this);

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
            Contact contact = new Contact();
            contact.setGivenname(ds.child(userID).getValue(Contact.class).getGivenname());
            contact.setMiddlename(ds.child(userID).getValue(Contact.class).getMiddlename());
            contact.setFamilyname(ds.child(userID).getValue(Contact.class).getFamilyname());
            contact.setPhonenumber(ds.child(userID).getValue(Contact.class).getPhonenumber());
            contact.setEmail(ds.child(userID).getValue(Contact.class).getEmail());
            contact.setRelationship(ds.child(userID).getValue(Contact.class).getRelationship());


            editTextGivenname.setText(contact.getGivenname());
            editTextMiddlename.setText(contact.getMiddlename());
            editTextFamilyname.setText(contact.getFamilyname());
            editTextphoneNumber.setText(contact.getPhonenumber());
            editTextEmail.setText(contact.getEmail());
            editTextRelationship.setText(contact.getRelationship());


//            if(editTextGivenname.getText().toString().isEmpty() && editTextMiddlename.getText().toString().isEmpty()
//                    && editTextFamilyname.getText().toString().isEmpty() && editTextphoneNumber.getText().toString().isEmpty()
//                    &&  editTextEmail.getText().toString().isEmpty() && editTextRelationship.getText().toString().isEmpty()) {
//                editTextGivenname.setText("null");
//                editTextMiddlename.setText("null");
//                editTextFamilyname.setText("null");
//                editTextphoneNumber.setText("null");
//                editTextEmail.setText("null");
//                editTextRelationship.setText("null");
//                return;
//            } else {
//                editTextGivenname.setText(contact.getGivenname());
//                editTextMiddlename.setText(contact.getMiddlename());
//                editTextFamilyname.setText(contact.getFamilyname());
//                editTextphoneNumber.setText(contact.getPhonenumber());
//                editTextEmail.setText(contact.getEmail());
//                editTextRelationship.setText(contact.getRelationship());
//                return;
//            }

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

        FirebaseDatabase.getInstance().getReference("Contacts/ContactTwo/ContactInfo")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .setValue(contact).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                progressBar.setVisibility(View.GONE);
                if (task.isSuccessful()) {
                    //finish();
                    Toast.makeText(Contact2Activity.this, "Contact Registered", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getApplicationContext(), task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    @Override
    public void onClick(View view) {
        switch(view.getId()) {
            case R.id.buttonAdd2:
                AddContact();
                break;
        }
    }
}