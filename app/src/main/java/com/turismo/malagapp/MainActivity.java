package com.turismo.malagapp;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentTransaction;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.turismo.malagapp.controller.GeoPos;
import com.google.android.material.navigation.NavigationView;
import com.turismo.malagapp.ui.home.HomeFragment;

import java.util.HashMap;

public class MainActivity extends AppCompatActivity{



    private AppBarConfiguration mAppBarConfiguration;


    public static GeoPos geoPos;
    private Double lat;
    private Double lon;
    private HashMap<String, Double> coordinatesMap;
    public static boolean grantedPermission = false;
    public static boolean fromRefresh = false;
    public static boolean pulsadoAceptado = false;
    public static boolean fromAcercaDe = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);


        if ((ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) && !fromAcercaDe) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 44);

        }else if(fromAcercaDe){
            setContentView(R.layout.activity_main);
            fromAcercaDe = false;
            init();
        }else {
            grantedPermission = true;
            pulsadoAceptado = true;
            geoPos = new GeoPos(MainActivity.this);
            geoPos.getLocation(this);
            setContentView(R.layout.activity_main);
            init();

        }


    }

    private void init() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        View s = findViewById(R.id.nav_home);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home, R.id.nav_playas, R.id.nav_museos, R.id.nav_monumentos,
                R.id.nav_cultura, R.id.nav_naturaleza, R.id.nav_otros,
                R.id.nav_centros_comerciales, R.id.nav_restaurantes, R.id.nav_luxury,
                R.id.nav_alojamientos, R.id.nav_puntos_informacion)
                .setDrawerLayout(drawer)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 44: {
                if (permissions[0].equals(Manifest.permission.ACCESS_FINE_LOCATION)) {
                    if(!fromRefresh){
                        if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                            grantedPermission = true;
                            geoPos = new GeoPos(MainActivity.this);
                            geoPos.getLocation(this);
                            setContentView(R.layout.activity_main);
                            init();


                        } else{
                            setContentView(R.layout.activity_main);
                            init();
                        }
                    }else{
                        try {
                            Thread.sleep(1000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                            grantedPermission = true;
                            pulsadoAceptado = true;
                            HomeFragment.mapButton.setVisibility(View.VISIBLE);
                            HomeFragment.mapButton.setEnabled(true);
                            HomeFragment.locButton.setVisibility(View.VISIBLE);
                            HomeFragment.locButton.setEnabled(true);
                            HomeFragment.setLocButton.setVisibility(View.INVISIBLE);
                            HomeFragment.setLocButton.setEnabled(false);
                            geoPos = new GeoPos(MainActivity.this);
                            geoPos.getLocation(this);
                            for(int i = 0; i < 2; i++){
                                if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                                    geoPos = new GeoPos(MainActivity.this);
                                    geoPos.getLocation(MainActivity.this);
                                }
                                try {
                                    Thread.sleep(500);
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                            }


                        }
                    }

                }
            }

        }
    }

    @Override
    public void onResume() {

        super.onResume();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.action_settings:
                Intent intent = new Intent(MainActivity.this, Acerca_De.class);
                startActivity(intent);
                fromAcercaDe = true;
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }

}
