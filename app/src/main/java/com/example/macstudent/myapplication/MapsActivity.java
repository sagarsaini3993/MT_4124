package com.example.macstudent.myapplication;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.telecom.Call;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.common.api.Response;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Callback;
import okhttp3.ResponseBody;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    // Used for Log.d statements
    // ---------------------------
    final static String TAG = "SAGAR";


    // OKhttp client variables
    // --------------------
    OkHttpClient client = new OkHttpClient();


    // Google Maps outlet
    // -------------------
    private GoogleMap mMap;


    // Variables for managing instructors
    // ----------------------------------
    ArrayList<Professor> professors = new ArrayList<Professor>();

    // Location variables
    // -------------------
    double userLatitude;
    double userLongitude;

    LocationManager manager;
    LocationListener userLocationListener;


    // CONSTANTS
    // -------------
    private static final int EARTH_RADIUS = 6371; // Approx Earth radius in KM


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        //@TODO: Part 4 - Uncomment this code.

//         1. Setup the location manager variable
        this.manager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);

        // 2. Setup LocationListeners
        setupLocationListener();

        // 3. Setup permissions
        setupPermissions();

    }

    public void setupLocationListener() {
        this.userLocationListener = new LocationListener() {

            // This function gets run when the phone receives a new location
            @Override
            public void onLocationChanged(Location location) {
                // @TODO: Part 4 - Write the code to output the person's latitude and longitude to the screen

                Log.d(TAG, "The user location changed!");
                Log.d(TAG,"New location: " + location.toString());



                // @TODO: Part 5 - Write the code to save the person's latitude and longitude to the variables

                userLatitude = location.getLatitude();
                userLongitude = location.getLongitude();

            }

            // IGNORE THIS FUNCTION!
            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {

            }

            // IGNORE THIS FUNCTION!
            @Override
            public void onProviderEnabled(String provider) {

            }

            // IGNORE THIS FUNCTION!
            @Override
            public void onProviderDisabled(String provider) {

            }
        };
    }

    @SuppressLint("MissingPermission")
    public void setupPermissions() {
        if (Build.VERSION.SDK_INT < 23) {

            this.manager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this.userLocationListener);


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
            this.manager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this.userLocationListener);

        }
        // 5b.  This is for phones AFTER Marshmallow
        else {

            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // Show the popup box! ask for permission
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            } else {
                // Do this code if the user PREVIOUSLY gave us permission to get location.
                // (ie: You already have permission!)
                this.manager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this.userLocationListener);

            }

        }
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        // @TODO: PART 1 - Write code to add zoom controls

        UiSettings uiSettings = googleMap.getUiSettings();
        uiSettings.setZoomControlsEnabled(true);

        mMap = googleMap;



        // @TODO: PART 2 - Copy and paste this code into the JSON function.
        LatLng sydney = new LatLng(-34, 151);
        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(sydney,12));

        // All the JSON API + Parsing code is inside this function.
        this.doJSONStuff(mMap);
    }


    public void doJSONStuff(final GoogleMap mMap) {

        //@TODO: PART 2 - Change this URL
//        String URL = "https://api.sunrise-sunset.org/json?lat=36.7201600&lng=-4.4203400";
        String URL = "https://myawesomeproject-ded96.firebaseio.com/college.json";
        Request request = new Request.Builder()
                .url(URL)
                .build();


        // Get the data from the url
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(okhttp3.Call call, IOException e) {
                Log.d(TAG, "an error occured!");
                e.printStackTrace();
            }

            @Override
            public void onResponse(okhttp3.Call call, okhttp3.Response response) throws IOException {
                try (ResponseBody responseBody = response.body()) {
                    if (!response.isSuccessful()) {
                        throw new IOException("Unexpected code " + response);
                    }

                    // Get the data from the API and convert it to a JSON dictionary
                    String dataFromAPI = responseBody.string();
                    JSONObject obj = new JSONObject(dataFromAPI);

                    // @FIXME: Write your code to parse the JSON
                    // -- the instructor information is inside an array called "instructors"
                    // -- lat/lng is inside a dictionary called "location"
                    // -----------------------------------------------------


                    // @TODO: PART 2 - 1. Write the code to parse out the instructors array
                    JSONArray instructorsArray = obj.getJSONArray("instructors");


                    // @TODO: PART 2 - 2. Write the code to iterate through each item in the array.
                    // Remember - you are trying to pull out the latitude & longitude for each instructor!

                    for (int i = 0; i < instructorsArray.length(); i++) {
                        // do something

                        // convert the current instructor to a dictionary
                        // The instructors will be Jenelle, Kadeem, Pritesh, etc....
                        // --------------
                        JSONObject instructor = instructorsArray.getJSONObject(i);

                        // Get the name, latitude, and longitude of the instructor
                        // -------------
                        // @TODO: PART 2 - 3. Write code to parse out the instructor's name
                        final String name = instructor.getString("name");
                        Log.d(TAG, "Instructor name is " +instructor);

                        // @TODO: PART 2 - 4. Write name to parse out the latitude and longitude

                        JSONObject location = instructor.getJSONObject("location");
                        final double lat = location.getDouble("latitude");
                        final double lng = location.getDouble("longitude");

                        // @TODO: PART 2 - 5. Write code to output the name, latitude, and longitude to the Terminal

                        Log.d(TAG, "Name: " + name);
                        Log.d(TAG, "Latitude: " + lat);
                        Log.d(TAG, "Longitude: " + lng);
                        Log.d(TAG, "--------");

                        // @TODO: PART 5 - Create a new Professor object & add it to the ArrayList

                        Professor p = new Professor(name, lat, lng);
                        professors.add(p);

                        // @TODO:  Show the Professor on the map
                        // output this status, sunrise and sunset time to the user interface
                        MapsActivity.this.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                // @TODO: Put your map code in here

                                Log.d(TAG, "Your map marker code should go here.");
                                LatLng sydney = new LatLng(lat, lng);
                                mMap.addMarker(new MarkerOptions().position(sydney).title(name));
                                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(sydney, 12));


                            }
                        });


                    } // end for loop

                }
                catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

    }

    // ACTIONS
    // ------------

    // This is the code that runs when the person presses the GET DISTANCES button.
    public void getDistancesPressed(View view) {
        TextView t = (TextView) findViewById(R.id.textview1);
        t.setText("");

        //@TODO: Get the user's latitude and longitude
        // HINT: There  are class variables called userLatitude and userLongitude!

        //@TODO: Loop through the list of instructors.
        for (int i = 0; i < this.professors.size(); i++) {
            //@TODO: For each instructor, get their latitude and longitude

            //@TODO: Use the haversine formula to calculate the distance between the user and insructor
//            double distance = getDistance(0,0, 1, 1);
            double distance = getDistance(userLatitude,userLongitude, this.professors.get(i).getLat(), this.professors.get(i).getLng());

            //@TODO: Output the instructor NAME + DISTANCE to the textview.
//            String abc = "Your output goes here: " + String.format("%.2f", distance) + " km \n";
            String abc = this.professors.get(i).getName() + " is " + String.format("%.2f", distance) + " km away \n";
            t.append(abc);
        }
    }


    // PERMISSIONS POPUP
    // -------------

    // This is a copy and paste of the template shown in class. You don't need to do anything with this function.
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        // Show the popup box
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);


        // If the person clicks ALLOW, then get the person's location
        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                // Same as manager.startUpdatingLocations in IOS
                // -- In code below, you are telling the app to use the GPS to get location.
                // -- When you get the location, run the userLocationListerer
                this.manager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0,0, userLocationListener);
            }
        }
    }



    // HAVERSINE FORMULA STUFF
    // -------------------------

    // Here is the formula for calculating the Haversine formula.
    // You give the function a starting & ending latitude/longitude
    // The function will return the distance between the two points
    // You can do something with that distance.
    public double getDistance(double startLat, double startLong,
                              double endLat, double endLong) {

        double dLat  = Math.toRadians((endLat - startLat));
        double dLong = Math.toRadians((endLong - startLong));

        startLat = Math.toRadians(startLat);
        endLat   = Math.toRadians(endLat);

        double a = haversin(dLat) + Math.cos(startLat) * Math.cos(endLat) * haversin(dLong);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        return EARTH_RADIUS * c; // <-- d
    }

    public static double haversin(double val) {
        return Math.pow(Math.sin(val / 2), 2);
    }


}


// Link to the instruction booklet:  Check the bottom of the Professor.java file.
