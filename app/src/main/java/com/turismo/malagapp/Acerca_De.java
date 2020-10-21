package com.turismo.malagapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;

public class Acerca_De extends AppCompatActivity {
    ImageButton dani,marcos,marimar,jesus;

    String daniUrl,marcosUrl,marimarUrl,jesusUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_acerca__de);

        dani= findViewById(R.id.imageDani);
        marcos= findViewById(R.id.imageMarcos);
        marimar= findViewById(R.id.imageMarimar);
        jesus= findViewById(R.id.imageJesus);


        daniUrl="https://www.linkedin.com/in/daniel-zumaquero-mayor-39a178110/";
        marcosUrl="https://www.linkedin.com/in/marcos-aguirre-mi%C3%B1arro/";
        marimarUrl="https://www.linkedin.com/in/marimargemio/";
        jesusUrl="https://www.linkedin.com/in/jesus-j-corrales-guillen-20b38a15a";

        dani.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri uri = Uri.parse(daniUrl);
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(intent);
            }
        });


        marcos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri uri = Uri.parse(marcosUrl);
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(intent);
            }
        });

        marimar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri uri = Uri.parse(marimarUrl);
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(intent);
            }
        });

        jesus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri uri = Uri.parse(jesusUrl);
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(intent);
            }
        });


    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if(item.getItemId() == android.R.id.home) {
            Acerca_De.this.finish();
        }
        return super.onOptionsItemSelected(item);
    }
}
