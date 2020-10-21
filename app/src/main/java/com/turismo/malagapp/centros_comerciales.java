package com.turismo.malagapp;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.turismo.malagapp.ui.centroscomerciales.CentrosComercialesFragment;

public class centros_comerciales extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.centros_comerciales_activity);
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.container, CentrosComercialesFragment.newInstance())
                    .commitNow();
        }

    }
}
