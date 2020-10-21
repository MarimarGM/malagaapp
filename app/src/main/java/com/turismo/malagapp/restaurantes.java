package com.turismo.malagapp;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.turismo.malagapp.ui.restaurantes.RestaurantesFragment;

public class restaurantes extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.restaurantes_activity);
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.container, RestaurantesFragment.newInstance())
                    .commitNow();
        }

    }
}
