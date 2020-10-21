package com.turismo.malagapp;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.turismo.malagapp.ui.monumentos.MonumentosFragment;

public class monumentos extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.monumentos_activity);
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.container, MonumentosFragment.newInstance())
                    .commitNow();
        }

    }
}
