package com.example.asus.drivepal;

import android.*;
import android.Manifest;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.asus.drivepal.models.PlaceInfo;
import com.example.asus.drivepal.models.PolygonTest;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.AutocompletePrediction;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceBuffer;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polygon;
import com.google.android.gms.maps.model.PolygonOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


import java.util.Formatter;
import java.util.Locale;
import java.util.Map;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.app.Activity;
import android.content.Context;
import android.support.v4.app.ActivityCompat;
import android.view.Menu;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;


public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback, IBaseGpsListener,
        GoogleApiClient.OnConnectionFailedListener{


    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        Toast.makeText(this, "Navigate and pin your destination", Toast.LENGTH_LONG).show();
        Log.d(TAG, "onMapReady: Navigate and pin your destination");
        mMap = googleMap;

      //  mMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(this, R.raw.dark));

        if (mLocationPermissionsGranted) {
            getDeviceLocation();

            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }

            mMap.setMyLocationEnabled(true);
            mMap.getUiSettings().setMyLocationButtonEnabled(false);
            mMap.setTrafficEnabled(true);

            // Enable / Disable Compass icon
            mMap.getUiSettings().setCompassEnabled(true);

            // Enable / Disable Rotate gesture
            mMap.getUiSettings().setRotateGesturesEnabled(true);

            init();
        }
    }


    private static final String TAG = "MapsActivity";

    private static final String FINE_LOCATION = Manifest.permission.ACCESS_FINE_LOCATION;
    private static final String COURSE_LOCATION = Manifest.permission.ACCESS_COARSE_LOCATION;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1234;
    private static final float DEFAULT_ZOOM = 15f;
    private static final float DIRECTION_ZOOM = 12f;
    private static final int PLACE_PICKER_REQUEST = 1;
    private static final LatLngBounds LAT_LNG_BOUNDS = new LatLngBounds(
            new LatLng(-40, -168), new LatLng(71, 136));


    //widgets
    private AutoCompleteTextView mSearchText;
    private ImageView mGps, mInfo, mPlacePicker, mSatellite, mTraffic;
    private RelativeLayout lTraffic;
    public RelativeLayout warning;
    public RelativeLayout mType;

    //vars
    private Boolean mLocationPermissionsGranted = false;
    private GoogleMap mMap;
    private FusedLocationProviderClient mFusedLocationProviderClient;
    private PlaceAutocompleteAdapter mPlaceAutocompleteAdapter;
    private GoogleApiClient mGoogleApiClient;
    private PlaceInfo mPlace;
    private Marker mMarker;

    public LatLng point1;
    public LatLng point2;
    public String cSpeed;
    public PolygonTest mPoly = new PolygonTest();

    public CheckBox ch_default, ch_satellite, ch_terrain, ch_hybrid, ch_night, ch_dark, ch_retro, ch_silver;

    public MediaPlayer alert;

    ArrayList<LatLng> listPoints;

    public TextView txtSpeedLimit;

    private static final int COLOR_BLACK_ARGB = 0xff000000;
    private static final int COLOR_WHITE_ARGB = 0xffffffff;
    private static final int COLOR_GREEN_ARGB = 0xff388E3C;
    private static final int COLOR_PURPLE_ARGB = 0xff81C784;
    private static final int COLOR_ORANGE_ARGB = 0xffF57F17;
    private static final int COLOR_BLUE_ARGB = 0xffF9A825;
    private static final int COLOR_TRANSPARENT = 0xcccccc;
    private static final int POLYGON_STROKE_WIDTH_PX = 8;
    private static final int PATTERN_DASH_LENGTH_PX = 20;
    private static final int PATTERN_GAP_LENGTH_PX = 20;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        //Never Sleep the phone
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        listPoints = new ArrayList<>();

        //Lines of codes for GPS SPEEDOMETER
        LocationManager locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);
        this.updateSpeed(null);

        CheckBox chkUseMetricUntis = (CheckBox) this.findViewById(R.id.chkMetricUnits);
        chkUseMetricUntis.setOnCheckedChangeListener(new OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                // TODO Auto-generated method stub
                MapsActivity.this.updateSpeed(null);
            }
        }); //END Lines of codes for GPS SPEEDOMETER

        mSearchText = (AutoCompleteTextView) findViewById(R.id.input_search);
        mGps = (ImageView) findViewById(R.id.ic_gps);
        mInfo = (ImageView) findViewById(R.id.place_info);
        mPlacePicker = (ImageView) findViewById(R.id.place_picker);
        mSatellite = (ImageView) findViewById(R.id.satellite);
        mTraffic = (ImageView) findViewById(R.id.info_traffic);
        lTraffic = (RelativeLayout) findViewById(R.id.traffic_layout);
        warning = (RelativeLayout) findViewById(R.id.warningLayout);
        mType = (RelativeLayout) findViewById(R.id.mapType);

        ch_default = (CheckBox) findViewById(R.id.ch_default);
        ch_satellite = (CheckBox) findViewById(R.id.ch_satellite);
        ch_terrain = (CheckBox) findViewById(R.id.ch_terrain);
        ch_hybrid = (CheckBox) findViewById(R.id.ch_hybrid);

        ch_dark = (CheckBox) findViewById(R.id.ch_dark);
        ch_night = (CheckBox) findViewById(R.id.ch_night);
        ch_retro = (CheckBox) findViewById(R.id.ch_retro);
        ch_silver = (CheckBox) findViewById(R.id.ch_silver);

        txtSpeedLimit = (TextView) findViewById(R.id.txtSpeedLimit);

        getLocationPermission();
    }

    // All required functions of gps speedometer
    public void finish()
    {
        super.finish();
        System.exit(0);
    }

    public String GetCurrentSpeed (String speed) {
        return speed;
    }

    private void updateSpeed(CLocation location) {
        // TODO Auto-generated method stub
        float nCurrentSpeed = 0;

        if(location != null)
        {
            location.setUseMetricunits(this.useMetricUnits());
            nCurrentSpeed = location.getSpeed();
        }

        Formatter fmt = new Formatter(new StringBuilder());
        fmt.format(Locale.US, "%5.1f", nCurrentSpeed);
        String strCurrentSpeed = fmt.toString();
        strCurrentSpeed = strCurrentSpeed.replace(' ', '0');

        TextView txtCurrentSpeed = (TextView) this.findViewById(R.id.txtCurrentSpeed);
        txtCurrentSpeed.setText(strCurrentSpeed);

        String speedo = strCurrentSpeed.replace(' ', '0');

        cSpeed = speedo;
        final Double speed = Double.parseDouble(cSpeed);

        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        try{
            if(mLocationPermissionsGranted){

                final Task location1 = mFusedLocationProviderClient.getLastLocation();
                location1.addOnCompleteListener(new OnCompleteListener() {
                    @Override
                    public void onComplete(@NonNull Task task) {
                        if(task.isSuccessful()){
                            Log.d(TAG, "onComplete: found location!");
                            Location currentLocation = (Location) task.getResult();

                            double clongitude = currentLocation.getLongitude();
                            double clatitude = currentLocation.getLatitude();

                            Polygon alijisRoad = mMap.addPolygon(new PolygonOptions().clickable(true).add(
                                    new LatLng(10.647369, 122.935117),
                                    new LatLng(10.634889, 122.957535),
                                    new LatLng(10.635606, 122.957896),
                                    new LatLng(10.647626, 122.935625),
                                    new LatLng(10.647369, 122.935117)));
                            alijisRoad.setStrokeWidth(POLYGON_STROKE_WIDTH_PX);
                            alijisRoad.setStrokeColor(COLOR_GREEN_ARGB);
                            alijisRoad.setFillColor(COLOR_BLUE_ARGB);
                            List listPointsAlijis = alijisRoad.getPoints();
                            LatLng[] arrayLatLngAlijis = new LatLng[listPointsAlijis.size()];
                            for(int i = 0; i < listPointsAlijis.size(); i++){
                                arrayLatLngAlijis[i]= (LatLng)listPointsAlijis.get(i);
                            }


                            Polygon polygon1 = mMap.addPolygon(new PolygonOptions().clickable(true).add(
                                            new LatLng(10.628769, 122.978000),
                                            new LatLng(10.626301, 122.977667),
                                            new LatLng(10.625974, 122.980069),
                                            new LatLng(10.628400, 122.980391),
                                            new LatLng(10.628769, 122.978000)));
                            polygon1.setStrokeWidth(POLYGON_STROKE_WIDTH_PX);
                            polygon1.setStrokeColor(COLOR_GREEN_ARGB);
                            polygon1.setFillColor(COLOR_PURPLE_ARGB);
                            List listPoints = polygon1.getPoints();

                            LatLng[] arrayLatLng = new LatLng[listPoints.size()];
                            for(int i = 0; i < listPoints.size(); i++){
                                arrayLatLng[i]= (LatLng)listPoints.get(i);
                            }
                            if (mPoly.PointIsInRegion(clatitude, clongitude,  arrayLatLng)){
                                Toast.makeText(MapsActivity.this, "Geo-fencing Testing ", Toast.LENGTH_LONG).show();
                                txtSpeedLimit.setText("001.0");

                                if (speed >= 0010.0) {
                                    WarningTrigger();
                                } else {

                                    try {
                                        warning.setVisibility(View.GONE);
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }

                            } else {
                                Toast.makeText(MapsActivity.this, "You are not in the testing zone", Toast.LENGTH_LONG).show();
                            }

                            if (mPoly.PointIsInRegion(clatitude, clongitude,  arrayLatLngAlijis)) {
                                Toast.makeText(MapsActivity.this, "Alijis Road Speed Limit", Toast.LENGTH_LONG).show();
                                txtSpeedLimit.setText("060.0");

                                if (speed >= 060.0) {
                                    WarningTrigger();
                                } else {

                                    try {
                                        warning.setVisibility(View.GONE);
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }
                            }

                        }else{
                            Log.d(TAG, "onComplete: current location is null");
                            Toast.makeText(MapsActivity.this, "unable to get current location", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        }catch (SecurityException e){
            Log.e(TAG, "getDeviceLocation: SecurityException: " + e.getMessage() );
        }


    }


    public void WarningTrigger(){

        Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        vibrator.vibrate(1500); // for 1500 ms
        warning.setVisibility(View.VISIBLE);

        if(alert != null && alert.isPlaying()) {
            alert.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer alert) {
                    alert = MediaPlayer.create(MapsActivity.this,R.raw.warning);
                    alert.start();
                }
            });
        } else {

           alert = MediaPlayer.create(MapsActivity.this,R.raw.warning);
           alert.start();
        }

    }

    private boolean useMetricUnits() {
        // TODO Auto-generated method stub
        CheckBox chkUseMetricUnits = (CheckBox) this.findViewById(R.id.chkMetricUnits);
        return chkUseMetricUnits.isChecked();
    }

    @Override
    public void onLocationChanged(Location location) {
        // TODO Auto-generated method stub
        if(location != null)
        {
            CLocation myLocation = new CLocation(location, this.useMetricUnits());
            this.updateSpeed(myLocation);
        }
    }

    @Override
    public void onProviderDisabled(String provider) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onProviderEnabled(String provider) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onGpsStatusChanged(int event) {
        // TODO Auto-generated method stub

    } //End required functions of gps speedometer


    private void init(){
        Log.d(TAG, "init: initializing");

        mGoogleApiClient = new GoogleApiClient
                .Builder(this)
                .addApi(Places.GEO_DATA_API)
                .addApi(Places.PLACE_DETECTION_API)
                .enableAutoManage(this, this)
                .build();
        mSearchText.setOnItemClickListener(mAutocompleteClickListener);

        mPlaceAutocompleteAdapter = new PlaceAutocompleteAdapter(this, mGoogleApiClient,
                LAT_LNG_BOUNDS, null);

        mSearchText.setAdapter(mPlaceAutocompleteAdapter);

        mSearchText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {
                if(actionId == EditorInfo.IME_ACTION_SEARCH
                        || actionId == EditorInfo.IME_ACTION_DONE
                        || keyEvent.getAction() == KeyEvent.ACTION_DOWN
                        || keyEvent.getAction() == KeyEvent.KEYCODE_ENTER){

                    //execute our method for searching
                    geoLocate();
                }

                return false;
            }
        });

        mGps.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "onClick: clicked gps icon");
                getDeviceLocation();
            }
        });

        mInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "onClick: clicked place info");
                try{
                    if(mMarker.isInfoWindowShown()){
                        mMarker.hideInfoWindow();
                    }else{
                        Log.d(TAG, "onClick: place info: " + mPlace.toString());
                        mMarker.showInfoWindow();
                    }
                }catch (NullPointerException e){
                    Log.e(TAG, "onClick: NullPointerException: " + e.getMessage() );
                }
            }
        });

        mPlacePicker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();

                try {
                    startActivityForResult(builder.build(MapsActivity.this), PLACE_PICKER_REQUEST);
                } catch (GooglePlayServicesRepairableException e) {
                    Log.e(TAG, "onClick: GooglePlayServicesRepairableException: " + e.getMessage() );
                } catch (GooglePlayServicesNotAvailableException e) {
                    Log.e(TAG, "onClick: GooglePlayServicesNotAvailableException: " + e.getMessage() );
                }
            }
        });


        mSatellite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
              if(mType.getVisibility() == (View.GONE)) {
                  mType.setVisibility(View.VISIBLE);
              } else {
                  mType.setVisibility(View.GONE);
              }
            }
        });

        mTraffic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(lTraffic.getVisibility() == (View.GONE)) {
                    lTraffic.setVisibility(View.VISIBLE);

                } else
                    lTraffic.setVisibility(View.GONE);
            }
        });


        hideSoftKeyboard();

    }

    public void onCheckboxClicked(View view) {

        switch(view.getId()) {

            case R.id.ch_default:
                mMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(this, R.raw.def));
                mMap.setMapType(mMap.MAP_TYPE_NORMAL);
                ch_satellite.setChecked(false);
                ch_terrain.setChecked(false);
                ch_hybrid.setChecked(false);
                ch_night.setChecked(false);
                ch_night.setChecked(false);
                ch_dark.setChecked(false);
                ch_retro.setChecked(false);
                ch_silver.setChecked(false);
                break;

            case R.id.ch_satellite:
                mMap.setMapType(mMap.MAP_TYPE_SATELLITE);
                ch_default.setChecked(false);
                ch_terrain.setChecked(false);
                ch_hybrid.setChecked(false);
                ch_night.setChecked(false);
                ch_night.setChecked(false);
                ch_dark.setChecked(false);
                ch_retro.setChecked(false);
                ch_silver.setChecked(false);
                break;

            case R.id.ch_terrain:
                mMap.setMapType(mMap.MAP_TYPE_TERRAIN);
                ch_default.setChecked(false);
                ch_satellite.setChecked(false);
                ch_hybrid.setChecked(false);
                ch_night.setChecked(false);
                ch_night.setChecked(false);
                ch_dark.setChecked(false);
                ch_retro.setChecked(false);
                ch_silver.setChecked(false);
                break;

            case R.id.ch_hybrid:
                mMap.setMapType(mMap.MAP_TYPE_HYBRID);
                ch_default.setChecked(false);
                ch_satellite.setChecked(false);
                ch_terrain.setChecked(false);
                ch_night.setChecked(false);
                ch_night.setChecked(false);
                ch_dark.setChecked(false);
                ch_retro.setChecked(false);
                ch_silver.setChecked(false);
                break;

            case R.id.ch_silver:
                mMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(this, R.raw.silver));
                mMap.setMapType(mMap.MAP_TYPE_NORMAL);
                ch_default.setChecked(false);
                ch_satellite.setChecked(false);
                ch_terrain.setChecked(false);
                ch_hybrid.setChecked(false);
                ch_night.setChecked(false);
                ch_dark.setChecked(false);
                ch_retro.setChecked(false);
                break;

            case R.id.ch_retro:
                mMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(this, R.raw.retro));
                mMap.setMapType(mMap.MAP_TYPE_NORMAL);
                ch_default.setChecked(false);
                ch_satellite.setChecked(false);
                ch_terrain.setChecked(false);
                ch_hybrid.setChecked(false);
                ch_night.setChecked(false);
                ch_dark.setChecked(false);
                ch_silver.setChecked(false);
                break;

            case R.id.ch_dark:
                mMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(this, R.raw.dark));
                mMap.setMapType(mMap.MAP_TYPE_NORMAL);
                ch_default.setChecked(false);
                ch_satellite.setChecked(false);
                ch_terrain.setChecked(false);
                ch_hybrid.setChecked(false);
                ch_night.setChecked(false);
                ch_retro.setChecked(false);
                ch_silver.setChecked(false);
                break;

            case R.id.ch_night:
                mMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(this, R.raw.night));
                mMap.setMapType(mMap.MAP_TYPE_NORMAL);
                ch_default.setChecked(false);
                ch_satellite.setChecked(false);
                ch_terrain.setChecked(false);
                ch_hybrid.setChecked(false);
                ch_dark.setChecked(false);
                ch_retro.setChecked(false);
                ch_silver.setChecked(false);
                break;
        }
    }


    private void MapDirection(){

        //Reset marker when already 2
        if (listPoints.size() == 2) {
            listPoints.clear();
            mMap.clear();
        }
        //Save first point
        try {
            if (mLocationPermissionsGranted) {

                final Task location = mFusedLocationProviderClient.getLastLocation();
                location.addOnCompleteListener(new OnCompleteListener() {
                    @Override
                    public void onComplete(@NonNull Task task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "onComplete: found location!");
                            //Point One/Current Location
                            point1 = getPoint1(point1);
                            listPoints.add(point1);
                            MarkerOptions markerOptions1 = new MarkerOptions();
                            markerOptions1.position(point1);
                            //mMap.addMarker(markerOptions1);

                            //Point Two/ Destination
                            // LatLng point2 = new LatLng(10.6840, 122.9563);
                            point2 = getPoint2(point2);
                            listPoints.add(point2);
                            MarkerOptions markerOptions2 = new MarkerOptions();
                            markerOptions2.position(point2);
                            mMap.addMarker(markerOptions2);


                            if (listPoints.size() == 2) {
                                //Create the URL to get request from first marker to second marker
                                String url = getRequestUrl(listPoints.get(0), listPoints.get(1));
                                TaskRequestDirections taskRequestDirections = new TaskRequestDirections();
                                taskRequestDirections.execute(url);

                                moveCamera(point1, DEFAULT_ZOOM, "My Location");
                                hideSoftKeyboard();
                            }


                        } else {
                            Log.d(TAG, "onComplete: current location is null");
                            Toast.makeText(MapsActivity.this, "unable to get current location", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        } catch (SecurityException e) {
            Log.e(TAG, "getDeviceLocation: SecurityException: " + e.getMessage());
        }


    }

    private String getRequestUrl(LatLng origin, LatLng dest) {
        //Value of origin
        String str_org = "origin=" + origin.latitude +","+origin.longitude;
        //Value of destination
        String str_dest = "destination=" + dest.latitude+","+dest.longitude;
        //Set value enable the sensor
        String sensor = "sensor=false";
        //Mode for find direction
        String mode = "mode=driving";
        String key = "key=AIzaSyBDfiipxRbT8yzuBmO_3Y3BPX4VxFRiEmQ";
        //Build the full param
        String param = str_org +"&" + str_dest + "&" +sensor+"&" +mode+"&"+key;
        //Output format
        String output = "json";



        //Create url to request
        String url = "https://maps.googleapis.com/maps/api/directions/" + output + "?" + param;
        return url;
    }


    private String requestDirection(String reqUrl) throws IOException {
        String responseString = "";
        InputStream inputStream = null;
        HttpURLConnection httpURLConnection = null;
        try{
            URL url = new URL(reqUrl);
            httpURLConnection = (HttpURLConnection) url.openConnection();
            httpURLConnection.connect();

            //Get the response result
            inputStream = httpURLConnection.getInputStream();
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

            StringBuffer stringBuffer = new StringBuffer();
            String line = "";
            while ((line = bufferedReader.readLine()) != null) {
                stringBuffer.append(line);
            }

            responseString = stringBuffer.toString();
            bufferedReader.close();
            inputStreamReader.close();

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (inputStream != null) {
                inputStream.close();
            }
            httpURLConnection.disconnect();
        }
        return responseString;
    }

    public class TaskRequestDirections extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... strings) {
            String responseString = "";
            try {
                responseString = requestDirection(strings[0]);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return  responseString;
        }


        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            //Parse json here
            TaskParser taskParser = new TaskParser();
            taskParser.execute(s);
        }
    }

    public class TaskParser extends AsyncTask<String, Void, List<List<HashMap<String, String>>> > {

        @Override
        protected List<List<HashMap<String, String>>> doInBackground(String... strings) {
            JSONObject jsonObject = null;
            List<List<HashMap<String, String>>> routes = null;
            try {
                jsonObject = new JSONObject(strings[0]);
                DirectionsParser directionsParser = new DirectionsParser();
                routes = directionsParser.parse(jsonObject);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return routes;
        }

        @Override
        protected void onPostExecute(List<List<HashMap<String, String>>> lists) {
            //Get list route and display it into the map

            ArrayList points = null;

            PolylineOptions polylineOptions = null;

            for (List<HashMap<String, String>> path : lists) {
                points = new ArrayList();
                polylineOptions = new PolylineOptions();

                for (HashMap<String, String> point : path) {
                    double lat = Double.parseDouble(point.get("lat"));
                    double lon = Double.parseDouble(point.get("lon"));

                    points.add(new LatLng(lat,lon));
                }

                polylineOptions.addAll(points);
                polylineOptions.width(15);
                polylineOptions.color(Color.BLUE);
                polylineOptions.geodesic(true);
            }

            if (polylineOptions!=null) {
                mMap.addPolyline(polylineOptions);
            } else {
                Toast.makeText(getApplicationContext(), "Direction not found!", Toast.LENGTH_SHORT).show();
            }

        }
    }



    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PLACE_PICKER_REQUEST) {
            if (resultCode == RESULT_OK) {
                Place place = PlacePicker.getPlace(this, data);

                PendingResult<PlaceBuffer> placeResult = Places.GeoDataApi
                        .getPlaceById(mGoogleApiClient, place.getId());
                placeResult.setResultCallback(mUpdatePlaceDetailsCallback);
            }
        }
    }

    private void geoLocate(){
        Log.d(TAG, "geoLocate: geolocating");

        String searchString = mSearchText.getText().toString();

        Geocoder geocoder = new Geocoder(MapsActivity.this);
        List<Address> list = new ArrayList<>();
        try{
            list = geocoder.getFromLocationName(searchString, 1);
        }catch (IOException e){
            Log.e(TAG, "geoLocate: IOException: " + e.getMessage() );
        }

        if(list.size() > 0){
            Address address = list.get(0);

            Log.d(TAG, "geoLocate: found a location: " + address.toString());
            //Toast.makeText(this, address.toString(), Toast.LENGTH_SHORT).show();

            moveCamera(new LatLng(address.getLatitude(), address.getLongitude()), DEFAULT_ZOOM,
                    address.getAddressLine(0));
        }
    }

    private void getDeviceLocation(){
        Log.d(TAG, "getDeviceLocation: getting the devices current location");

        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        try{
            if(mLocationPermissionsGranted){

                final Task location = mFusedLocationProviderClient.getLastLocation();
                location.addOnCompleteListener(new OnCompleteListener() {
                    @Override
                    public void onComplete(@NonNull Task task) {
                        if(task.isSuccessful()){
                            Log.d(TAG, "onComplete: found location!");
                            Location currentLocation = (Location) task.getResult();

                            moveCamera(new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude()),
                                    DEFAULT_ZOOM,
                                    "My Location");

                            point1 = new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());

                        }else{
                            Log.d(TAG, "onComplete: current location is null");
                            Toast.makeText(MapsActivity.this, "unable to get current location", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        }catch (SecurityException e){
            Log.e(TAG, "getDeviceLocation: SecurityException: " + e.getMessage() );
        }
    }

    public LatLng getPoint2(LatLng point2){
        return point2;
    }
    public LatLng getPoint1(LatLng point1){
        return point1;
    }



    private void moveCamera(LatLng latLng, float zoom, PlaceInfo placeInfo){
        Log.d(TAG, "moveCamera: moving the camera to: lat: " + latLng.latitude + ", lng: " + latLng.longitude );
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, zoom));

        mMap.clear();

        mMap.setInfoWindowAdapter(new CustomInfoWindowAdapter(MapsActivity.this));

        if(placeInfo != null){
            try{
                String snippet = "Address: " + placeInfo.getAddress() + "\n" +
                        "Phone Number: " + placeInfo.getPhoneNumber() + "\n" +
                        "Website: " + placeInfo.getWebsiteUri() + "\n" +
                        "Price Rating: " + placeInfo.getRating() + "\n";

                MarkerOptions options = new MarkerOptions()
                        .position(latLng)
                        .title(placeInfo.getName())
                        .snippet(snippet);
                mMarker = mMap.addMarker(options);

            }catch (NullPointerException e){
                Log.e(TAG, "moveCamera: NullPointerException: " + e.getMessage() );
            }
        }else{
            mMap.addMarker(new MarkerOptions().position(latLng));
        }

        hideSoftKeyboard();
    }

    private void moveCamera(LatLng latLng, float zoom, String title){
        Log.d(TAG, "moveCamera: moving the camera to: lat: " + latLng.latitude + ", lng: " + latLng.longitude );
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, zoom));

        if(!title.equals("My Location")){
            MarkerOptions options = new MarkerOptions()
                    .position(latLng)
                    .title(title);
            mMap.addMarker(options);
        }

        hideSoftKeyboard();
    }

    private void initMap(){
        Log.d(TAG, "initMap: initializing map");
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);

        mapFragment.getMapAsync(MapsActivity.this);
    }

    private void getLocationPermission(){
        Log.d(TAG, "getLocationPermission: getting location permissions");
        String[] permissions = {Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION};

        if(ContextCompat.checkSelfPermission(this.getApplicationContext(),
                FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
            if(ContextCompat.checkSelfPermission(this.getApplicationContext(),
                    COURSE_LOCATION) == PackageManager.PERMISSION_GRANTED){
                mLocationPermissionsGranted = true;
                initMap();
            }else{
                ActivityCompat.requestPermissions(this,
                        permissions,
                        LOCATION_PERMISSION_REQUEST_CODE);
            }
        }else{
            ActivityCompat.requestPermissions(this,
                    permissions,
                    LOCATION_PERMISSION_REQUEST_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        Log.d(TAG, "onRequestPermissionsResult: called.");
        mLocationPermissionsGranted = false;

        switch(requestCode){
            case LOCATION_PERMISSION_REQUEST_CODE:{
                if(grantResults.length > 0){
                    for(int i = 0; i < grantResults.length; i++){
                        if(grantResults[i] != PackageManager.PERMISSION_GRANTED){
                            mLocationPermissionsGranted = false;
                            Log.d(TAG, "onRequestPermissionsResult: permission failed");
                            return;
                        }
                    }
                    Log.d(TAG, "onRequestPermissionsResult: permission granted");
                    mLocationPermissionsGranted = true;
                    //initialize our map
                    initMap();
                }
            }
        }
    }

    private void hideSoftKeyboard(){
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
    }

    /************************* Google places API autocomplete suggestions*************************/

    private AdapterView.OnItemClickListener mAutocompleteClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
            hideSoftKeyboard();

            final AutocompletePrediction item = mPlaceAutocompleteAdapter.getItem(i);
            final String placeId = item.getPlaceId();

            PendingResult<PlaceBuffer> placeResult = Places.GeoDataApi
                    .getPlaceById(mGoogleApiClient, placeId);
            placeResult.setResultCallback(mUpdatePlaceDetailsCallback);
        }
    };



    private ResultCallback<PlaceBuffer> mUpdatePlaceDetailsCallback = new ResultCallback<PlaceBuffer>() {
        @Override
        public void onResult(@NonNull PlaceBuffer places) {
            if(!places.getStatus().isSuccess()){
                Log.d(TAG, "onResult: Place query did not complete successfully: " + places.getStatus().toString());
                places.release();
                return;
            }
            final Place place = places.get(0);

            try{
                mPlace = new PlaceInfo();
                mPlace.setName(place.getName().toString());
                Log.d(TAG, "onResult: name: " + place.getName());
                mPlace.setAddress(place.getAddress().toString());
                Log.d(TAG, "onResult: address: " + place.getAddress());
//                mPlace.setAttributions(place.getAttributions().toString());
//                Log.d(TAG, "onResult: attributions: " + place.getAttributions());
                mPlace.setId(place.getId());
                Log.d(TAG, "onResult: id:" + place.getId());
                mPlace.setLatlng(place.getLatLng());
                Log.d(TAG, "onResult: latlng: " + place.getLatLng());
                mPlace.setRating(place.getRating());
                Log.d(TAG, "onResult: rating: " + place.getRating());
                mPlace.setPhoneNumber(place.getPhoneNumber().toString());
                Log.d(TAG, "onResult: phone number: " + place.getPhoneNumber());
                mPlace.setWebsiteUri(place.getWebsiteUri());
                Log.d(TAG, "onResult: website uri: " + place.getWebsiteUri());

                Log.d(TAG, "onResult: place: " + mPlace.toString());
            }catch (NullPointerException e){
                Log.e(TAG, "onResult: NullPointerException: " + e.getMessage() );
            }

            moveCamera(new LatLng(place.getViewport().getCenter().latitude,
                    place.getViewport().getCenter().longitude), DEFAULT_ZOOM, mPlace);

            point2 = new LatLng(place.getViewport().getCenter().latitude,
                    place.getViewport().getCenter().longitude);

            getPoint2(point2);
            places.release();
            MapDirection();

        }
    };


}










