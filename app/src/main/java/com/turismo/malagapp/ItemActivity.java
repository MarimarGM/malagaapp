package com.turismo.malagapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Point;
import android.net.Uri;
import android.os.Bundle;
import android.view.Display;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class ItemActivity extends AppCompatActivity {
    private String titulo;
    private String descripcionString;
    private int imageResource;
    private TextView descripcion;
    private ImageView image;
    private Bundle bundle;
    private Button telfButton;
    private Button webButton;
    private Button mailButton;
    private Button locationButton;
    private LinearLayout leftLayout;
    private LinearLayout rightLayout;
    private String phone;
    private String web;
    private String mail;
    private Double latitude;
    private Double longitude;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item);
        titulo = getIntent().getStringExtra("Nombre");
        descripcionString = getIntent().getStringExtra("Descripcion");
        bundle = getIntent().getExtras();
        phone = getIntent().getStringExtra("telf");
        web = getIntent().getStringExtra("web");
        mail = getIntent().getStringExtra("mail");
        phone = getIntent().getStringExtra("phone");
        imageResource = bundle.getInt("ImagenInt");
        latitude = getIntent().getDoubleExtra("latitude", 0);
        longitude = getIntent().getDoubleExtra("longitude", 0);
        System.out.println("El entero de la imagen es: "+imageResource);
        descripcion = (TextView) findViewById(R.id.idDescripcion);
        image = (ImageView) findViewById(R.id.idImagenItem);

        //-------------------------------
        descripcion.setText(descripcionString);
        //-----------------------------------
        image.setImageResource(imageResource);
        //-----------------------------------
        setTitle(titulo);

        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int width = size.x;
        float doubleBtnWidth = (float) (width/2.0);
        int buttonWidth = (Integer)Math.round(doubleBtnWidth);
        telfButton = (Button)findViewById(R.id.button_phone);
        locationButton = (Button)findViewById(R.id.button_location);
        mailButton = (Button)findViewById(R.id.button_mail);
        webButton = (Button)findViewById(R.id.button_web);
        leftLayout = (LinearLayout)findViewById(R.id.linear_left_row);
        rightLayout = (LinearLayout)findViewById(R.id.linear_right_row);
        leftLayout.setMinimumWidth(buttonWidth);
        rightLayout.setMinimumWidth(buttonWidth);

        webButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if(!web.equals("null")){

                    if (!web.startsWith("http://") && !web.startsWith("https://")){
                        web = "http://" + web;
                    }
                    Uri uri = Uri.parse(web);
                    Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                    startActivity(intent);
                    System.out.println(web+"-----------------------------TENGO URL-------------------------------------");
                }else{
                    Toast.makeText(v.getContext(), "URL no disponible",
                            Toast.LENGTH_SHORT).show();
                    System.out.println(web+"-----------------------------NO TENGO URL-------------------------------------");
                }

            }
        });

        mailButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if(!mail.equals("null")){
                    Intent intentMail = new Intent(Intent.ACTION_SEND);
                    intentMail.setType("message/rfc822");
                    intentMail.putExtra(Intent.EXTRA_EMAIL  , new String[]{mail});
                    intentMail.putExtra(Intent.EXTRA_SUBJECT, "Asunto del mensaje");
                    intentMail.putExtra(Intent.EXTRA_TEXT   , "Cuerpo del mensaje");
                    try {
                        startActivity(Intent.createChooser(intentMail, "Enviar email..."));
                    } catch (android.content.ActivityNotFoundException ex) {
                        Toast.makeText(ItemActivity.this, "No hay clientes de email instalados.", Toast.LENGTH_SHORT).show();
                    }
                }else{
                    Toast.makeText(v.getContext(), "Mail no disponible",
                            Toast.LENGTH_SHORT).show();
                }

            }
        });

        telfButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if(!phone.equals("null") && !phone.equals("0")){
                    String telefono = "tel:" + phone;
                    Uri uri = Uri.parse(telefono);
                    Intent intentPhone = new Intent(Intent.ACTION_DIAL, uri);
                    try {
                        startActivity(Intent.createChooser(intentPhone, "Haciendo llamada"));
                    } catch (android.content.ActivityNotFoundException ex) {
                        Toast.makeText(ItemActivity.this, "No hay aplicacion de contactos instalada.", Toast.LENGTH_SHORT).show();
                    }
                }else{
                    Toast.makeText(v.getContext(), "Telefono no disponible",
                            Toast.LENGTH_SHORT).show();
                }

            }
        });

        locationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Uri gmmIntentUri = Uri.parse("geo:"+latitude+","+longitude+"?q="+ titulo);
                Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                mapIntent.setPackage("com.google.android.apps.maps");
                if (mapIntent.resolveActivity(getPackageManager()) != null) {
                    startActivity(mapIntent);
                }
            }
        });
    }
}
