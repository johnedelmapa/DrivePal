package com.example.asus.drivepal;

import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;

public class ContactsActivity extends AppCompatActivity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contacts);


        findViewById(R.id.textViewContact1).setOnClickListener(this);
        findViewById(R.id.textViewContact2).setOnClickListener(this);
        findViewById(R.id.textViewContact3).setOnClickListener(this);

//        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
//        setSupportActionBar(toolbar);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("Contacts");
    }

    @Override
    public void onClick(View view) {
        switch(view.getId()){
            case R.id.textViewContact1:
                startActivity(new Intent(this, Contact1Activity.class));
                break;
        }
    }
}
