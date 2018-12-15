package com.example.asus.drivepal;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class ChangePasswordActivity extends AppCompatActivity {

    private Button ChangePasswordButton;
    private EditText ChangePasswordField;
    private FirebaseAuth mAuth;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forget_password);

        mAuth = FirebaseAuth.getInstance();

        ChangePasswordButton = (Button) findViewById(R.id.buttonSend);
        ChangePasswordField = (EditText) findViewById(R.id.editTextChangePassword);
        progressBar = (ProgressBar) findViewById(R.id.progressbar);

        ChangePasswordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String user = ChangePasswordField.getText().toString();

                if(TextUtils.isEmpty(user)) {
                    Toast.makeText(ChangePasswordActivity.this, "Please write your valid email address", Toast.LENGTH_SHORT).show();
                } else {
                    progressBar.setVisibility(View.VISIBLE);
                    mAuth.sendPasswordResetEmail(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()) {
                                progressBar.setVisibility(View.GONE);
                                Toast.makeText(ChangePasswordActivity.this, "We have sent you an email with reset instructions", Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(ChangePasswordActivity.this, LoginActivity.class));
                            } else {
                                String message = task.getException().getMessage();
                                Toast.makeText(ChangePasswordActivity.this, "Error Occurred" + message, Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            }
        });

    }
}