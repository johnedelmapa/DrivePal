package com.example.asus.drivepal;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import java.util.Date;
import java.util.zip.Inflater;

public class Dashboard extends Fragment implements View.OnClickListener {


    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        getActivity().setTitle("Dashboard");

        getView().findViewById(R.id.textViewContacts).setOnClickListener(this);
        getView().findViewById(R.id.textViewVehicles).setOnClickListener(this);
        getView().findViewById(R.id.textViewNavigation).setOnClickListener(this);


        //Attach Current Time
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat format = new SimpleDateFormat("EEE, MMM d, ''yy");
        String time = format.format(calendar.getTime());
        TextView textView = getView().findViewById(R.id.textViewDate);
        textView.setText(time);


        SimpleDateFormat format1 = new SimpleDateFormat("h:mm a");
        String time1 = format1.format(calendar.getTime());
        TextView textView1 = getView().findViewById(R.id.textViewTime);
        textView1.setText(time1);



    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState0) {
        return inflater.inflate(R.layout.dashboard, container, false);


    }


    @Override
    public void onClick(View view) {


        switch(view.getId()){

            case R.id.textViewContacts:
                Intent intent = new Intent(getActivity(), ContactsActivity.class);
                startActivity(intent);
                break;
            case R.id.textViewVehicles:
                Intent intent1 = new Intent(getActivity(), VehiclesActivity.class);
                startActivity(intent1);
                break;
            case R.id.textViewNavigation:
                Intent intent2 = new Intent(getActivity(), MapsActivity.class);
                startActivity(intent2);
                break;

          


        }


    }
}
