package com.example.asus.drivepal;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener  {

    FirebaseAuth mAuth;
    EditText editTextEmail, editTextPassword;
    TextView forgetPassword;
    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mAuth = FirebaseAuth.getInstance();

        editTextEmail = (EditText) findViewById(R.id.editTextEmail);
        editTextPassword = (EditText) findViewById(R.id.editTextResetPassword);
        forgetPassword = (TextView) findViewById(R.id.forgetPassword);
        progressBar = (ProgressBar) findViewById(R.id.progressbar);

        findViewById(R.id.buttonSignup).setOnClickListener(this);
        findViewById(R.id.buttonLogin).setOnClickListener(this);
        findViewById(R.id.createAccount).setOnClickListener(this);
        findViewById(R.id.forgetPassword).setOnClickListener(this);


    }


    private void userLogin(){

        String email = editTextEmail.getText().toString().trim();
        String password = editTextPassword.getText().toString().trim();

        if(email.isEmpty()) {
            editTextEmail.setError("Email is required");
            editTextEmail.requestFocus();
            return;
        }

        if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            editTextEmail.setError("Invalid Email Address");
            editTextEmail.requestFocus();
            return;
        }

        if(password.isEmpty()) {
            editTextPassword.setError("Password is required");
            editTextPassword.requestFocus();
            return;
        }

        if(password.length()<6) {
            editTextPassword.setError("Password must be 6 characters");
            editTextPassword.requestFocus();
            return;
        }

        progressBar.setVisibility(View.VISIBLE);

        mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                progressBar.setVisibility(View.GONE);
                if(task.isSuccessful()) {
                    finish();
                    Intent intent = new Intent(LoginActivity.this, DashboardActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                }else{
                    Toast.makeText(getApplicationContext(), task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();

        if (mAuth.getCurrentUser() != null) {
            finish();
            startActivity(new Intent(this, DashboardActivity.class));
        }
    }

    @Override
    public void onClick(View view) {
        switch(view.getId()){
            case R.id.buttonSignup:
                finish();
                startActivity(new Intent(this, SignupActivity.class));
                break;
            case R.id.createAccount:
                finish();
                startActivity(new Intent(this, SignupActivity.class));
                break;

            case R.id.buttonLogin:
                userLogin();
                break;

            case R.id.forgetPassword:
                startActivity(new Intent(this, ForgetPasswordActivity.class));
                break;


        }
    }

}
