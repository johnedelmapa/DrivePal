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
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;


import com.example.asus.drivepal.models.PlaceInfo;
import com.example.asus.drivepal.models.PolygonTest;
import com.example.asus.drivepal.models.Violation;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.AutocompleteFilter;
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
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

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
import java.util.Timer;
import java.util.TimerTask;

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
import android.widget.RadioButton;
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
        //Toast.makeText(this, "Navigate and pin your destination", Toast.LENGTH_LONG).show();
        Log.d(TAG, "onMapReady: Navigate and pin your destination");
        mMap = googleMap;

        LatLng one = new LatLng(10.613618, 122.912933);
        LatLng two = new LatLng(10.72531,  123.029415);

        LatLngBounds.Builder builder = new LatLngBounds.Builder();

        //add them to builder
        builder.include(one);
        builder.include(two);

        LatLngBounds bounds = builder.build();

        //get width and height to current display screen
        int width = getResources().getDisplayMetrics().widthPixels;
        int height = getResources().getDisplayMetrics().heightPixels;

        // 20% padding
        int padding = (int) (width * 0.20);

        //set latlong bounds
        mMap.setLatLngBoundsForCameraTarget(bounds);

        //move camera to fill the bound to screen
        mMap.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds, width, height, padding));

        //set zoom to level to current so that you won't be able to zoom out viz. move outside bounds
        mMap.setMinZoomPreference(mMap.getCameraPosition().zoom);

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

    private FirebaseDatabase mFirebaseDatabase;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private DatabaseReference myRef;
    private String userID;
    FirebaseAuth firebaseAuth;
    String FinalCurrentUser;

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
    public RelativeLayout warning, onewayViolation;
    public RelativeLayout mType, chooseVehicle, ArrivalLayout;

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

    public ImageView ch_default, ch_satellite, ch_terrain, ch_hybrid, ch_night, ch_dark, ch_retro, ch_silver;

    public MediaPlayer alert;

    ArrayList<LatLng> listPoints;

    public TextView txtSpeedLimit, textViewVehicle1, textViewVehicle2, textViewVehicle3;

    private static final int COLOR_BLACK_ARGB = 0xff000000;
    private static final int COLOR_WHITE_ARGB = 0xffffffff;
    private static final int COLOR_GREEN_ARGB = 0xff388E3C;
    private static final int COLOR_PURPLE_ARGB = 0xff81C784;
    private static final int COLOR_ORANGE_ARGB = 0xffF57F17;
    private static final int COLOR_BLUE_ARGB = 0xffF9A825;
    private static final int COLOR_TRANSPARENT = 0xcccccc;
    private static final int POLYGON_STROKE_WIDTH_PX = 5;
    private static final int PATTERN_DASH_LENGTH_PX = 20;
    private static final int PATTERN_GAP_LENGTH_PX = 20;

    public String violationType1, violationType2, LicenseNo, fullName, PlateNo, Manufacturer1, Model1, Color1;
    public Double CurrentLat, Currentlong;
    public Double DestinationLat = 0.0;
    public Double DestinationLong = 0.0;
    public TextView currentlat, currentlng, deslat, deslng, distanceDetails;
    public Button buttonOk;
    public static int nav = 0;
    public static int entryPoint = 0;
    public static int violationTrigger = 0;
    public static int oneWayCondition = 0;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        //Never Sleep the phone
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        mAuth = FirebaseAuth.getInstance();
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();
        userID = user.getUid();
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference databaseReference = firebaseDatabase.getReference("Users");
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
        FinalCurrentUser = firebaseUser.getUid();


        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot dataS: dataSnapshot.getChildren()){
                    if(FinalCurrentUser.equals(dataS.getRef().getKey())) {
                        final String name = dataS.child("fullname").getValue(String.class);
                        final  String licenseNo = dataS.child("licenseNo").getValue(String.class);

                        LicenseNo = licenseNo;
                        fullName = name;
                    }

                    onDataChange(dataS);
                }


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });




        violationType1 = "Over Speeding";
        violationType2 = "One Way";



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
        onewayViolation = (RelativeLayout) findViewById(R.id.onewayWarning);
        mType = (RelativeLayout) findViewById(R.id.mapType);
        chooseVehicle = (RelativeLayout) findViewById(R.id.chooseVehicle);
        ArrivalLayout = (RelativeLayout) findViewById(R.id.ArrivalLayout);

        buttonOk = (Button) findViewById(R.id.buttonOk);
        ch_default = (ImageView) findViewById(R.id.ch_default);
        ch_satellite = (ImageView) findViewById(R.id.ch_satellite);
        ch_terrain = (ImageView) findViewById(R.id.ch_terrain);
        ch_hybrid = (ImageView) findViewById(R.id.ch_hybrid);

        ch_dark = (ImageView) findViewById(R.id.ch_dark);
        ch_night = (ImageView) findViewById(R.id.ch_night);
        ch_retro = (ImageView) findViewById(R.id.ch_retro);
        ch_silver = (ImageView) findViewById(R.id.ch_silver);

        txtSpeedLimit = (TextView) findViewById(R.id.txtSpeedLimit);
        textViewVehicle1 = (TextView) findViewById(R.id.textViewVehicle1);
        textViewVehicle2 = (TextView) findViewById(R.id.textViewVehicle2);
        textViewVehicle3 = (TextView) findViewById(R.id.textViewVehicle3);

        currentlat = (TextView) findViewById(R.id.currentlat);
        currentlng = (TextView) findViewById(R.id.currentlng);
        deslat = (TextView) findViewById(R.id.deslat);
        deslng = (TextView) findViewById(R.id.deslng);
        distanceDetails = (TextView) findViewById(R.id.distanceDetails);


        getLocationPermission();


    }

    // All required functions of gps speedometer
    public void finish()
    {
        super.finish();
        System.exit(0);
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


                            Polygon testingEntry = mMap.addPolygon(new PolygonOptions().clickable(true).add(
                                    new LatLng(10.626837, 122.979022),
                                    new LatLng(10.626594, 122.978984),
                                    new LatLng(10.626652, 122.978630),
                                    new LatLng(10.626900, 122.978689),
                                    new LatLng(10.626837, 122.979022)
                            ));

                            testingEntry.setStrokeWidth(POLYGON_STROKE_WIDTH_PX);
                            testingEntry.setStrokeColor(COLOR_TRANSPARENT);
                            testingEntry.setFillColor(COLOR_TRANSPARENT);
                            List listPointstestingEntry = testingEntry.getPoints();
                            LatLng[] arrayLatLngtestingEntry  = new LatLng[listPointstestingEntry.size()];
                            for(int i = 0; i < listPointstestingEntry.size(); i++){
                                arrayLatLngtestingEntry[i]= (LatLng)listPointstestingEntry.get(i);
                            }


                            Polygon testingZonez = mMap.addPolygon(new PolygonOptions().clickable(true).add(
                                    new LatLng(10.626895, 122.978620),
                                    new LatLng(10.626658, 122.978587),
                                    new LatLng(10.626747, 122.978008),
                                    new LatLng(10.626974, 122.978057),
                                    new LatLng(10.626895, 122.978620)
                            ));
                            testingZonez.setStrokeWidth(POLYGON_STROKE_WIDTH_PX);
                            testingZonez.setStrokeColor(COLOR_TRANSPARENT);
                            testingZonez.setFillColor(COLOR_TRANSPARENT);
                            List listPointstestingZonez = testingZonez.getPoints();
                            LatLng[] arrayLatLngtestingZonez  = new LatLng[listPointstestingZonez.size()];
                            for(int i = 0; i < listPointstestingZonez.size(); i++){
                                arrayLatLngtestingZonez[i]= (LatLng)listPointstestingZonez.get(i);
                            }


                            Polygon testingExit = mMap.addPolygon(new PolygonOptions().clickable(true).add(
                                    new LatLng(10.627000, 122.977987),
                                    new LatLng(10.626737, 122.977949),
                                    new LatLng(10.626779, 122.977746),
                                    new LatLng(10.627000, 122.977783),
                                    new LatLng(10.627000, 122.977987)
                            ));

                            testingExit.setStrokeWidth(POLYGON_STROKE_WIDTH_PX);
                            testingExit.setStrokeColor(COLOR_TRANSPARENT);
                            testingExit.setFillColor(COLOR_TRANSPARENT);
                            List listPointstestingExit = testingExit.getPoints();
                            LatLng[] arrayLatLngtestingExit  = new LatLng[listPointstestingExit.size()];
                            for(int i = 0; i < listPointstestingExit.size(); i++){
                                arrayLatLngtestingExit[i]= (LatLng)listPointstestingExit.get(i);
                            }


                            if (mPoly.PointIsInRegion(clatitude, clongitude,  arrayLatLngtestingEntry)) {
                                Toast.makeText(MapsActivity.this, "You are about to enter a one way zone, please turn around", Toast.LENGTH_SHORT).show();
                                entryPoint = 1;
                                oneWayCondition = 1;
                            }

                            if (mPoly.PointIsInRegion(clatitude, clongitude,  arrayLatLngtestingZonez) && entryPoint == 1) {
                                WarningTriggerOneWay();
                                if(oneWayCondition == 1) {
                                    ReportTriggerOneWay();
                                    oneWayCondition = 0;
                                }
                            } else {
                                onewayViolation.setVisibility(View.GONE);
                            }


                            if (mPoly.PointIsInRegion(clatitude, clongitude,  arrayLatLngtestingExit)) {
                                entryPoint = 0;
                            }


                            Polygon lizaresEntry = mMap.addPolygon(new PolygonOptions().clickable(true).add(
                                    new LatLng(10.658164, 122.949485),
                                    new LatLng(10.657995, 122.949378),
                                    new LatLng(10.658185, 122.948998),
                                    new LatLng(10.658348, 122.949137),
                                    new LatLng(10.658164, 122.949485)
                            ));

                            lizaresEntry.setStrokeColor(COLOR_GREEN_ARGB);
                            lizaresEntry.setFillColor(COLOR_TRANSPARENT);
                            List listPointslizaresEntry = lizaresEntry.getPoints();
                            LatLng[] arrayLatLnglizaresEntry  = new LatLng[listPointslizaresEntry.size()];
                            for(int i = 0; i < listPointslizaresEntry.size(); i++){
                                arrayLatLnglizaresEntry[i]= (LatLng)listPointslizaresEntry.get(i);
                            }

                            Polygon lizaresZone = mMap.addPolygon(new PolygonOptions().clickable(true).add(
                                    new LatLng(10.658348, 122.949137),
                                    new LatLng(10.658169, 122.949067),
                                    new LatLng(10.658517, 122.948365),
                                    new LatLng(10.658686, 122.948488),
                                    new LatLng(10.658348, 122.949137)
                            ));

                            lizaresZone.setStrokeColor(COLOR_ORANGE_ARGB);
                            lizaresZone.setFillColor(COLOR_TRANSPARENT);
                            List listPointslizaresZone = lizaresZone.getPoints();
                            LatLng[] arrayLatLnglizaresZone  = new LatLng[listPointslizaresZone.size()];
                            for(int i = 0; i < listPointslizaresZone.size(); i++){
                                arrayLatLnglizaresZone[i]= (LatLng)listPointslizaresZone.get(i);
                            }



                            if (mPoly.PointIsInRegion(clatitude, clongitude,  arrayLatLnglizaresEntry)) {
                                 Toast.makeText(MapsActivity.this, "You are about to enter a one way zone, please turn around", Toast.LENGTH_SHORT).show();
                                 entryPoint = 1;
                            }

                            if (mPoly.PointIsInRegion(clatitude, clongitude,  arrayLatLnglizaresZone) && entryPoint == 1) {
                                Toast.makeText(MapsActivity.this, "You've been report for one way violation", Toast.LENGTH_SHORT).show();
//                                entryPoint = 0;
                            }


                            Polygon alijisRoad = mMap.addPolygon(new PolygonOptions().clickable(true).add(
                                    new LatLng(10.647309, 122.935426),
                                    new LatLng(10.635062, 122.957768),
                                    new LatLng(10.632763, 122.960175),
                                    new LatLng(10.629254, 122.983449),
                                    new LatLng(10.630045, 122.983588),
                                    new LatLng(10.633166, 122.963120),
                                    new LatLng(10.633472, 122.960321),
                                    new LatLng(10.636139, 122.957039),
                                    new LatLng(10.647567, 122.935544),
                                    new LatLng(10.647309, 122.935426)
                            ));
                            alijisRoad.setStrokeWidth(POLYGON_STROKE_WIDTH_PX);
                            alijisRoad.setStrokeColor(COLOR_TRANSPARENT);
                            alijisRoad.setFillColor(COLOR_TRANSPARENT);
                            List listPointsAlijis = alijisRoad.getPoints();
                            LatLng[] arrayLatLngAlijis = new LatLng[listPointsAlijis.size()];
                            for(int i = 0; i < listPointsAlijis.size(); i++){
                                arrayLatLngAlijis[i]= (LatLng)listPointsAlijis.get(i);
                            }


                            Polygon lacsonRoad = mMap.addPolygon(new PolygonOptions().clickable(true).add(
                                    new LatLng(10.652110, 122.943354),
                                    new LatLng(10.654451, 122.944406),
                                    new LatLng(10.655458, 122.944513),
                                    new LatLng(10.656512, 122.944684),
                                    new LatLng(10.698395, 122.961845),
                                    new LatLng(10.704468, 122.962081),
                                    new LatLng(10.704498, 122.962425),
                                    new LatLng(10.698417, 122.962382),
                                    new LatLng(10.656308, 122.944939),
                                    new LatLng(10.654621, 122.944810),
                                    new LatLng(10.651985, 122.943630),
                                    new LatLng(10.652110, 122.943354)
                            ));
                            lacsonRoad.setStrokeWidth(POLYGON_STROKE_WIDTH_PX);
                            lacsonRoad.setStrokeColor(COLOR_TRANSPARENT);
                            lacsonRoad.setFillColor(COLOR_TRANSPARENT);
                            List listPointsLacson = lacsonRoad.getPoints();
                            LatLng[] arrayLatLngLacson = new LatLng[listPointsLacson.size()];
                            for(int i = 0; i < listPointsLacson.size(); i++){
                                arrayLatLngLacson[i]= (LatLng)listPointsLacson.get(i);
                            }

                            Polygon testingZone = mMap.addPolygon(new PolygonOptions().clickable(true).add(
                                    new LatLng(10.628737, 122.978021),
                                    new LatLng(10.626259, 122.977635),
                                    new LatLng(10.625943, 122.980081),
                                    new LatLng(10.628379, 122.980370),
                                    new LatLng(10.628737, 122.978021)
                            ));
                            testingZone.setStrokeWidth(POLYGON_STROKE_WIDTH_PX);
                            testingZone.setStrokeColor(COLOR_TRANSPARENT);
                            testingZone.setFillColor(COLOR_TRANSPARENT);
                            List listPointsTesting = testingZone.getPoints();
                            LatLng[] arrayLatLngTesting = new LatLng[listPointsTesting.size()];
                            for(int i = 0; i < listPointsTesting.size(); i++){
                                arrayLatLngTesting[i]= (LatLng)listPointsTesting.get(i);
                            }


                            Polygon panaadRoad = mMap.addPolygon(new PolygonOptions().clickable(true).add(
                                    new LatLng(10.623579, 122.967392),
                                    new LatLng(10.625503, 122.962581),
                                    new LatLng(10.626853, 122.964351),
                                    new LatLng(10.626890, 122.966120),
                                    new LatLng(10.625645, 122.968266),
                                    new LatLng(10.623489, 122.967510),
                                    new LatLng(10.623579, 122.967392)

                            ));
                            panaadRoad.setStrokeWidth(POLYGON_STROKE_WIDTH_PX);
                            panaadRoad.setStrokeColor(COLOR_TRANSPARENT);
                            panaadRoad.setFillColor(COLOR_TRANSPARENT);
                            List listPointsPanaad = panaadRoad.getPoints();
                            LatLng[] arrayLatLngPanaad = new LatLng[listPointsPanaad.size()];
                            for(int i = 0; i < listPointsPanaad.size(); i++){
                                arrayLatLngPanaad[i]= (LatLng)listPointsPanaad.get(i);
                            }


                            Polygon benildeRoad = mMap.addPolygon(new PolygonOptions().clickable(true).add(
                                    new LatLng(10.623473, 122.967344),
                                    new LatLng(10.620457, 122.963777),
                                    new LatLng(10.620147, 122.963948),
                                    new LatLng(10.621813, 122.968131),
                                    new LatLng(10.623433, 122.967656),
                                    new LatLng(10.623473, 122.967344)

                            ));
                            benildeRoad.setStrokeWidth(POLYGON_STROKE_WIDTH_PX);
                            benildeRoad.setStrokeColor(COLOR_TRANSPARENT);
                            benildeRoad.setFillColor(COLOR_TRANSPARENT);
                            List listPointsBenilde = benildeRoad.getPoints();
                            LatLng[] arrayLatLngBenilde = new LatLng[listPointsBenilde.size()];
                            for(int i = 0; i < listPointsBenilde.size(); i++){
                                arrayLatLngBenilde[i]= (LatLng)listPointsBenilde.get(i);
                            }


                            if (mPoly.PointIsInRegion(clatitude, clongitude,  arrayLatLngAlijis)) {
                                txtSpeedLimit.setText("080.0");
                                if (speed >= 0080.0) {
                                    WarningTrigger();
                                    ReportTrigger();
                                } else {
                                    try {
                                        warning.setVisibility(View.GONE);
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }
                            } else if (mPoly.PointIsInRegion(clatitude, clongitude,  arrayLatLngTesting)){
                                txtSpeedLimit.setText("060.0");
                                // Toast.makeText(MapsActivity.this, "You are in the testing zone", Toast.LENGTH_SHORT).show();
                                if (speed >= 060.0) {
                                    WarningTrigger();
                                    ReportTrigger();
                                } else {
                                    try {
                                        warning.setVisibility(View.GONE);
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }
                            } else if (mPoly.PointIsInRegion(clatitude, clongitude,  arrayLatLngLacson)){
                                txtSpeedLimit.setText("080.0");
                                if (speed >= 0080.0) {
                                    WarningTrigger();
                                    ReportTrigger();
                                } else {
                                    try {
                                        warning.setVisibility(View.GONE);
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }
                            } else if (mPoly.PointIsInRegion(clatitude, clongitude,  arrayLatLngPanaad)){
                                txtSpeedLimit.setText("040.0");
                                if (speed >= 0040.0) {
                                    WarningTrigger();
                                    ReportTrigger();
                                } else {
                                    try {
                                        warning.setVisibility(View.GONE);
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }
                            } else if (mPoly.PointIsInRegion(clatitude, clongitude,  arrayLatLngBenilde)){
                                txtSpeedLimit.setText("020.0");
                                Toast.makeText(MapsActivity.this, "School Zone", Toast.LENGTH_SHORT).show();
                                if (speed >= 0020.0) {
                                    WarningTrigger();
                                    ReportTrigger();
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

    public double getCurrentLat(Double lat) {
        return lat;
    }

    public double getCurrentLong(Double lng) {
        return lng;
    }

    public double getDestinationLat(Double lat) {
        return lat;
    }

    public double getDistinationLong(Double lng) {
        return lng;
    }


    public void ReportTrigger(){

        final String violationtype = violationType1.toString().trim();
        final String fullname = fullName.toString().trim();
        final String licenseno = LicenseNo.toString().trim();
        final String plateno = PlateNo.toString().trim();

        final String color = Color1.toString().trim();
        final String manufacturer = Manufacturer1.toString().trim();
        final String model = Model1.toString().trim();

        Violation violation = new Violation(violationtype, fullname, licenseno, plateno, color, manufacturer, model);

        FirebaseDatabase.getInstance().getReference("Violations")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid()).push()
                .setValue(violation).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    //finish();
                    Toast.makeText(MapsActivity.this, "You've been reported", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getApplicationContext(), task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }


    public void resetEntrypoint(){
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                entryPoint = 0;
            }
        }, 10000);
    }


    public void ReportTriggerOneWay(){

        final String violationtype = violationType2.toString().trim();
        final String fullname = fullName.toString().trim();
        final String licenseno = LicenseNo.toString().trim();
        final String plateno = PlateNo.toString().trim();

        final String color = Color1.toString().trim();
        final String manufacturer = Manufacturer1.toString().trim();
        final String model = Model1.toString().trim();

        Violation violation = new Violation(violationtype, fullname, licenseno, plateno, color, manufacturer, model);

        FirebaseDatabase.getInstance().getReference("Violations")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid()).push()
                .setValue(violation).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    //finish();
                    Toast.makeText(MapsActivity.this, "You've been reported", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getApplicationContext(), task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
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

    public void WarningTriggerOneWay(){
        onewayViolation.setVisibility(View.VISIBLE);
        Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        vibrator.vibrate(1500);
//        alert = MediaPlayer.create(MapsActivity.this,R.raw.oneway);
//        alert.start();

    }

    public void SuccussfullyArrived(){
        Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        vibrator.vibrate(500);
        alert = MediaPlayer.create(MapsActivity.this,R.raw.success);
        alert.start();
    }

    public void getVehicleOne(){
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference databaseReference1 = firebaseDatabase.getReference("Vehicles/VehicleOne/VehicleInfo");
        databaseReference1.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot dataS: dataSnapshot.getChildren()){
                    if(FinalCurrentUser.equals(dataS.getRef().getKey())) {
                        final String placeno = dataS.child("plateno").getValue(String.class);
                        final String model = dataS.child("model").getValue(String.class);
                        final String manufacturer = dataS.child("manufacturer").getValue(String.class);
                        final String color = dataS.child("color").getValue(String.class);
                        PlateNo = placeno;
                        Model1 = model;
                        Manufacturer1 = manufacturer;
                        Color1 = color;
                    }
                    onDataChange(dataS);
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

    public void getVehicleTwo(){
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference databaseReference1 = firebaseDatabase.getReference("Vehicles/VehicleTwo/VehicleInfo");
        databaseReference1.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot dataS: dataSnapshot.getChildren()){
                    if(FinalCurrentUser.equals(dataS.getRef().getKey())) {
                        final String placeno = dataS.child("plateno").getValue(String.class);
                        final String model = dataS.child("model").getValue(String.class);
                        final String manufacturer = dataS.child("manufacturer").getValue(String.class);
                        final String color = dataS.child("color").getValue(String.class);

                        PlateNo = placeno;
                        Model1 = model;
                        Manufacturer1 = manufacturer;
                        Color1 = color;
                    }
                    onDataChange(dataS);
                }


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public void getVehicleThree(){
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference databaseReference1 = firebaseDatabase.getReference("Vehicles/VehicleThree/VehicleInfo");
        databaseReference1.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot dataS: dataSnapshot.getChildren()){
                    if(FinalCurrentUser.equals(dataS.getRef().getKey())) {
                        final String placeno = dataS.child("plateno").getValue(String.class);
                        final String model = dataS.child("model").getValue(String.class);
                        final String manufacturer = dataS.child("manufacturer").getValue(String.class);
                        final String color = dataS.child("color").getValue(String.class);

                        PlateNo = placeno;
                        Model1 = model;
                        Manufacturer1 = manufacturer;
                        Color1 = color;
                    }
                    onDataChange(dataS);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
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

        getDistance();
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

//        LatLngBounds Bacolod = new LatLngBounds(new LatLng(10.613618, 122.912933),
//                new LatLng(10.72531, 123.029415));


        mGoogleApiClient = new GoogleApiClient
                .Builder(this)
                .addApi(Places.GEO_DATA_API)
                .addApi(Places.PLACE_DETECTION_API)
                .enableAutoManage(this, this)
                .build();
        mSearchText.setOnItemClickListener(mAutocompleteClickListener);

        AutocompleteFilter autocompleteFilter = new AutocompleteFilter.Builder()
                .setTypeFilter(Place.TYPE_COUNTRY)
                .setCountry("PH")
                .build();

        mPlaceAutocompleteAdapter = new PlaceAutocompleteAdapter(this, mGoogleApiClient,
                LAT_LNG_BOUNDS, autocompleteFilter);

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

        textViewVehicle1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                chooseVehicle.setVisibility(View.GONE);
                getVehicleOne();
            }
        });

        textViewVehicle2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                chooseVehicle.setVisibility(View.GONE);
                getVehicleTwo();
            }
        });

        textViewVehicle3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                chooseVehicle.setVisibility(View.GONE);
                getVehicleThree();
            }
        });

        hideSoftKeyboard();

    }

    public void onCheckboxClicked(View view) {

        switch(view.getId()) {

            case R.id.ch_default:
                mMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(this, R.raw.def));
                mMap.setMapType(mMap.MAP_TYPE_NORMAL);
                break;

            case R.id.ch_satellite:
                mMap.setMapType(mMap.MAP_TYPE_SATELLITE);
                break;

            case R.id.ch_terrain:
                mMap.setMapType(mMap.MAP_TYPE_TERRAIN);
                break;

            case R.id.ch_hybrid:
                mMap.setMapType(mMap.MAP_TYPE_HYBRID);
                break;

            case R.id.ch_silver:
                mMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(this, R.raw.silver));
                mMap.setMapType(mMap.MAP_TYPE_NORMAL);
                break;

            case R.id.ch_retro:
                mMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(this, R.raw.retro));
                mMap.setMapType(mMap.MAP_TYPE_NORMAL);
                break;

            case R.id.ch_dark:
                mMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(this, R.raw.dark));
                mMap.setMapType(mMap.MAP_TYPE_NORMAL);
                break;

            case R.id.ch_night:
                mMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(this, R.raw.night));
                mMap.setMapType(mMap.MAP_TYPE_NORMAL);
                break;
        }

    }


    private void MapDirection(){

        nav = 1;

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

                            Circle circle = mMap.addCircle(new CircleOptions()
                                    .center(point2)
                                    .radius(30)
                                    .strokeColor(android.R.color.transparent)
                                    .fillColor(Color.GREEN));


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

    private void getDistance(){
        try {
            if (mLocationPermissionsGranted) {

                final Task location = mFusedLocationProviderClient.getLastLocation();
                location.addOnCompleteListener(new OnCompleteListener() {
                    @Override
                    public void onComplete(@NonNull Task task) {
                        if (task.isSuccessful()) {

                            Location currentLocation = (Location) task.getResult();
                            double clongitude = currentLocation.getLongitude();
                            double clatitude = currentLocation.getLatitude();

                            CurrentLat = getCurrentLat(clatitude);
                            Currentlong = getCurrentLong(clongitude);

                            double end_lat = DestinationLat;
                            double end_lng = DestinationLong;

                                Location loc1 = new Location("");
                                loc1.setLatitude(CurrentLat);
                                loc1.setLongitude(Currentlong);

                                Location loc2 = new Location("");
                                loc2.setLatitude(DestinationLat);
                                loc2.setLongitude(DestinationLong);

                                float distanceInMeters = loc1.distanceTo(loc2);

//                                Toast.makeText(MapsActivity.this, distanceInMeters + "Meters", Toast.LENGTH_SHORT).show();


                                if (distanceInMeters <= 30 && nav == 1) {
                                    nav = 0;
                                    ArrivalLayout.setVisibility(View.VISIBLE);
                                    SuccussfullyArrived();
                                    buttonOk.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {
                                            ArrivalLayout.setVisibility(View.GONE);
                                            listPoints.clear();
                                            mMap.clear();
                                        }
                                    });

                                }
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


            DestinationLat = getDestinationLat(place.getViewport().getCenter().latitude);
            DestinationLong = getDistinationLong( place.getViewport().getCenter().longitude);

            getDestinationLat(DestinationLat);
            getDistinationLong(DestinationLong);

            getPoint2(point2);
            places.release();
            MapDirection();

        }
    };


}










