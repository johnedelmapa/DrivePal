package com.example.asus.drivepal;

import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.storage.StorageManager;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;


import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.database.FirebaseDatabase;

import java.io.IOException;
import java.util.zip.Inflater;

public class Profile extends Fragment implements View.OnClickListener {

    private static final int CHOOSE_IMAGE = 101;
    private ImageView imageView;
    private EditText editTextDisplayName, editTextEmail, editTextPassword, editTextName, editTextLicenseNo;
    private Uri uriProfileImage;
    private ProgressBar progressBar;
    private String profileImageUrl;

    private FirebaseDatabase mFirebaseDatabase;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private DatabaseReference myRef;
    private String userID;

    private static final String TAG = "ViewDatabase";

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        getActivity().setTitle("Profile");

        mAuth = FirebaseAuth.getInstance();
        editTextDisplayName = (EditText)(getView().findViewById(R.id.editTextDisplayName));
        imageView = (ImageView) (getView().findViewById(R.id.imageView));
        progressBar = (ProgressBar) (getView().findViewById(R.id.progressbar));

        mFirebaseDatabase = FirebaseDatabase.getInstance();
        myRef = mFirebaseDatabase.getReference();
        FirebaseUser user = mAuth.getCurrentUser();
        userID = user.getUid();

        editTextEmail = (EditText)(getView().findViewById(R.id.editTextEmail));
        editTextPassword = (EditText)(getView().findViewById(R.id.editTextPassword));
        editTextName = (EditText)(getView().findViewById(R.id.editTextName));
        editTextLicenseNo = (EditText)(getView().findViewById(R.id.editTextLicenseNo));

        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    // User is signed in
                    Log.d(TAG, "onAuthStateChanged:signed_in:" + user.getUid());
                } else {
                    // User is signed out
                    Log.d(TAG, "onAuthStateChanged:signed_out");
                }
                // ...
            }

        };



        imageView.setOnClickListener(new View.OnClickListener(){

                                         @Override
                                         public  void onClick(View view) { showImageChooser();
                                         }
                                     });

        loadUserInformation();

        getView().findViewById(R.id.buttonSave).setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                saveUserInformation();
            }
        });
    }



    private void loadUserInformation() {
        FirebaseUser user = mAuth.getCurrentUser();

        if(user !=null) {
            if (user.getPhotoUrl() != null) {
                //   String photoUri = user.getPhotoUrl().toString();
                Glide.with(this)
                        .load(user.getPhotoUrl().toString())
                        .into(imageView);
            }
            if (user.getDisplayName() != null) {
                //    String displayName = user.getDisplayName();
                editTextDisplayName.setText(user.getDisplayName());
            } if (user.getEmail() != null) {
                editTextEmail.setText(user.getEmail());
            }
        }


    }


    private void saveUserInformation(){
        String displayName = editTextDisplayName.getText().toString();

        if(displayName.isEmpty()) {
            editTextDisplayName.setError("Name Required");
            editTextDisplayName.requestFocus();
            return;
        }

        FirebaseUser user = mAuth.getCurrentUser();

        if(user!=null && profileImageUrl !=null){
            final UserProfileChangeRequest profile = new UserProfileChangeRequest.Builder()
                    .setDisplayName(displayName).setPhotoUri(Uri.parse(profileImageUrl))
                    .build();
            user.updateProfile(profile)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()){
                                Toast.makeText(getActivity(), "Profile Updated",
                                        Toast.LENGTH_LONG).show();
                            }
                        }
                    });
        }
    }



    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState0) {
        return inflater.inflate(R.layout.profile, container, false);

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == CHOOSE_IMAGE ){
            uriProfileImage = data.getData();

            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContext().getContentResolver(), uriProfileImage);
                imageView.setImageBitmap(bitmap);
                uploadImageToFirebaseStorage();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }

    private void uploadImageToFirebaseStorage() {
        StorageReference profileImageRef = FirebaseStorage.getInstance().getReference("profilepics/" +System.currentTimeMillis() + ".jpg");
        if(uriProfileImage !=null) {
            progressBar.setVisibility(View.VISIBLE);
            profileImageRef.putFile(uriProfileImage).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    progressBar.setVisibility(View.GONE);
                    profileImageUrl = taskSnapshot.getDownloadUrl().toString();
                }
            })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressBar.setVisibility(View.GONE);
                            Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }

    public void  showImageChooser(){
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Profile Image"), CHOOSE_IMAGE);
    }

    @Override
    public void onClick(View view) {


    }

}

