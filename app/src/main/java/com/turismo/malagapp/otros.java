package com.turismo.malagapp;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.turismo.malagapp.ui.otros.OtrosFragment;

public class otros extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.otros_activity);
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.container, OtrosFragment.newInstance())
                    .commitNow();
        }

    }
}
