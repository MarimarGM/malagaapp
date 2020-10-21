package com.turismo.malagapp;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.turismo.malagapp.ui.naturaleza.NaturalezaFragment;

public class naturaleza extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.naturaleza_activity);
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.container, NaturalezaFragment.newInstance())
                    .commitNow();
        }

    }
}
