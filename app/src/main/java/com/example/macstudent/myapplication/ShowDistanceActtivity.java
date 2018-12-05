package com.example.macstudent.myapplication;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import java.util.ArrayList;

public class ShowDistanceActtivity extends AppCompatActivity {


    // Constants
    // --------------
    private static final int EARTH_RADIUS = 6371; // Approx Earth radius in KM


    // Outlets
    // ---------------
    TextView textview1;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_distance_activitty);

        //@TODO: Write the code to get the user's latitude and longitude from the Intent


        // @TODO: Writ the code to get the instructors out of the intent
        Bundle b = getIntent().getExtras();
        ArrayList<Professor> professors = (ArrayList<Professor>) b.getSerializable("professorList");


        //@TODO: Loop through the list of instructors.
        for (int i = 0; i < professors.size(); i++) {
            //@TODO: For each instructor, get their latitude and longitude
            //@TODO: Use the haversine formula to calculate the distance between the user and insructor

            //@TODO: Output the instructor NAME + DISTANCE to the textview.

        }



    }

    public void backButtonPressed(View view) {
        this.finish();
    }



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

