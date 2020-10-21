package com.turismo.malagapp.controller;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class GeoPos {

    private FusedLocationProviderClient fusedLocationProviderClient;
    public static Double lat;
    public static Double lon;

    public GeoPos(Context ctx){
        this.fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(ctx);
        //Check permission
        //checkPermission(ctx);
    }

    public void checkPermission(Context ctx){
        if(ActivityCompat.checkSelfPermission(ctx
                , Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED){
            //When permission denied
            ActivityCompat.requestPermissions((Activity) ctx
                    , new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 44);
        }
    }

    public void getLocation(Context ctx) {
        HashMap<String, Double> coord = new HashMap<>();
        fusedLocationProviderClient.getLastLocation().addOnCompleteListener(new OnCompleteListener<Location>() {

            @Override
            public void onComplete(@NonNull Task<Location> task) {
                Location location = task.getResult();
                if(location != null){
                    try {
                        //Initialize geocoder
                        Geocoder geocoder = new Geocoder(ctx,
                                Locale.getDefault());
                        //Initializa address list

                        List<Address> addresses = geocoder.getFromLocation(
                                location.getLatitude(), location.getLongitude(), 1
                        );


                        //coord.put("latitude", addresses.get(0).getLatitude());
                        GeoPos.lat = addresses.get(0).getLatitude();
                        //coord.put("longitude", addresses.get(0).getLongitude());
                        GeoPos.lon = addresses.get(0).getLongitude();
                        System.out.println("Estoy entrando en el try de la localizacion--------------------------------------------------------------------------");

                    } catch (IOException e) {
                        System.out.println("No entro en el try de la localizacion");
                        e.printStackTrace();
                    }
                }

            }
        });
        //return coord;
    }
    /**
     * Calculate distance between two points in latitude and longitude taking
     * into account height difference. If you are not interested in height
     * difference pass 0.0. Uses Haversine method as its base.
     *
     * lat1, lon1 Start point lat2, lon2 End point el1 Start altitude in meters
     * el2 End altitude in meters
     * @returns Distance in Meters
     */
    public static double getDistance(double lat1, double lat2, double lon1,
                                     double lon2, double el1, double el2) {

        final int R = 6371; // Radius of the earth

        double latDistance = Math.toRadians(lat2 - lat1);
        double lonDistance = Math.toRadians(lon2 - lon1);
        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        double distance = R * c * 1000; // convert to meters

        double height = el1 - el2;

        distance = Math.pow(distance, 2) + Math.pow(height, 2);

        return Math.sqrt(distance);
    }
}
