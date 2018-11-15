package com.example.asus.drivepal;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class DashboardActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    public TextView menuEmail;
    private static final int CHOOSE_IMAGE = 101;
    private ImageView imageView;
    private Uri uriProfileImage;
    private String profileImageUrl;

    FirebaseAuth firebaseAuth;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;
    FirebaseUser firebaseUser;
    String FinalCurrentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);


        //Assign username and email to nav bar
        imageView = (ImageView) findViewById(R.id.menuProfile);
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference databaseReference = firebaseDatabase.getReference("Users");
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
        FinalCurrentUser = firebaseUser.getUid();

        // Toast.makeText(this,FinalCurrentUser,Toast.LENGTH_LONG).show();

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot dataS: dataSnapshot.getChildren()){
                    if(FinalCurrentUser.equals(dataS.getRef().getKey())) {
                        final String menuName = dataS.child("fullname").getValue(String.class);
                        final  String menuEmail = dataS.child("email").getValue(String.class);
                        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
                        navigationView.setNavigationItemSelectedListener(DashboardActivity.this);

                        TextView menuUserName = (TextView) navigationView.getHeaderView(0).findViewById(R.id.menuName);
                        TextView menuUserEmail = (TextView) navigationView.getHeaderView(0).findViewById(R.id.menuEmail);

                        menuUserName.setText(menuName);
                        menuUserEmail.setText(menuEmail);
                        //loadUserInformation();

                        //Toast.makeText(DashboardActivity.this,Mainname,Toast.LENGTH_LONG).show();
                        //Toast.makeText(DashboardActivity.this,Mainemail,Toast.LENGTH_LONG).show();
                    }

                    onDataChange(dataS);
                }


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }

        }); //End Assign username and email to nav bar


        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        displaySelectedScreen(R.id.nav_dashboard);
    }

    private void loadUserInformation() {

        FirebaseUser user = firebaseAuth.getCurrentUser();

        if(user !=null) {
            if (user.getPhotoUrl() != null) {
                //   String photoUri = user.getPhotoUrl().toString();
                Glide.with(this)
                        .load(user.getPhotoUrl().toString())
                        .into(imageView);
            }
        }

    }



    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.dashboard, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void displaySelectedScreen(int id){
        Fragment fragment = null;

         switch(id) {
             case R.id.nav_dashboard:
                 fragment = new Dashboard();
                 break;
             case R.id.nav_profile:
                 fragment = new Profile();
                 break;
             case R.id.nav_activityLog:
                 fragment = new ActivityLog();
                 break;
             case R.id.nav_help:
                 fragment = new Help();
                 break;
             case R.id.nav_about:
                 fragment = new About();
                 break;
             case R.id.nav_logout:
                 FirebaseAuth.getInstance().signOut();
                 finish();
                 startActivity(new Intent(this, WelcomeActivity.class));
         }

         if(fragment !=null){
             FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
             ft.replace(R.id.content_dashboard, fragment);
             ft.commit();
         }


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        displaySelectedScreen(id);

        return true;
    }
}
