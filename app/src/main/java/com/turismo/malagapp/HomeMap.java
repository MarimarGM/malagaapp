package com.turismo.malagapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.drawable.Drawable;
import android.os.Bundle;

import com.turismo.malagapp.controller.GeoPos;
import com.turismo.malagapp.model.EntityImpl;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class HomeMap extends AppCompatActivity implements OnMapReadyCallback, Serializable {

    private GoogleMap mMap;
    private List<EntityImpl> listaElementosMapa = new ArrayList<>();
    private List<Integer> listaImagenesMapa = new ArrayList<>();
    private Marker marker;
    Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_map);
        setTitle("Mapa");
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);

        listaElementosMapa = (ArrayList<EntityImpl>) getIntent().getSerializableExtra("lista");
        listaImagenesMapa = (ArrayList<Integer>) getIntent().getSerializableExtra("listaImagenes");

        mapFragment.getMapAsync(this);
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setMyLocationEnabled(true);
        mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
        LatLng miPosicion = new LatLng(GeoPos.lat, GeoPos.lon);
        //mMap.addMarker(new MarkerOptions().position(miPosicion).title("Estás aquí"));

        for (int i = 0; i < 20; i++) {
            LatLng pos = new LatLng(listaElementosMapa.get(i).getLatitude(), listaElementosMapa.get(i).getLongitude());
            //mMap.addMarker(new MarkerOptions().position(pos).title(listaElementosMapa.get(i).getName()));

            MarkerOptions options = new MarkerOptions().position(pos);
            Bitmap bitmap = createUserBitmap(listaImagenesMapa.get(i));
            if (bitmap != null) {
                options.title(listaElementosMapa.get(i).getName());
                options.icon(BitmapDescriptorFactory.fromBitmap(bitmap));
                options.anchor(0.5f, 0.907f);
                marker = mMap.addMarker(options);
                marker.setTag(i);

            }


            mMap.moveCamera(CameraUpdateFactory.newLatLng(miPosicion));

            // Zoom in, animating the camera.
            mMap.animateCamera(CameraUpdateFactory.zoomTo(17), 2000, null);
        }

        mMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
            @Override
            public void onInfoWindowClick(Marker marker) {
                Intent intent = new Intent(HomeMap.this,ItemActivity.class);
                //Añadiendo los datos al intent
                intent.putExtra("Nombre", listaElementosMapa.get((Integer)marker.getTag()).getName());
                intent.putExtra("Descripcion", listaElementosMapa.get((Integer)marker.getTag()).getDescripcion());
                intent.putExtra("ImagenInt", listaImagenesMapa.get((Integer)marker.getTag()));
                intent.putExtra("web", listaElementosMapa.get((Integer)marker.getTag()).getWeb());
                intent.putExtra("mail", listaElementosMapa.get((Integer)marker.getTag()).getMail());
                intent.putExtra("phone", listaElementosMapa.get((Integer)marker.getTag()).getTelefono());
                intent.putExtra("latitude", listaElementosMapa.get((Integer)marker.getTag()).getLatitude());
                intent.putExtra("longitude", listaElementosMapa.get((Integer)marker.getTag()).getLongitude());
                startActivity(intent);
            }
        });
    }

    private Bitmap createUserBitmap(int drawableResource) {
        Bitmap result = null;
        try {
            result = Bitmap.createBitmap(dp(62), dp(76), Bitmap.Config.ARGB_8888);
            result.eraseColor(Color.TRANSPARENT);
            Canvas canvas = new Canvas(result);
            Drawable drawable = getResources().getDrawable(R.drawable.livepin);
            drawable.setBounds(0, 0, dp(62), dp(76));
            drawable.draw(canvas);

            Paint roundPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
            RectF bitmapRect = new RectF();
            canvas.save();

            Bitmap bitmap = BitmapFactory.decodeResource(getResources(), drawableResource);
            //Bitmap bitmap = BitmapFactory.decodeFile(path.toString()); /*generate bitmap here if your image comes from any url*/
            if (bitmap != null) {
                BitmapShader shader = new BitmapShader(bitmap, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);
                Matrix matrix = new Matrix();
                float scale = dp(52) / (float) bitmap.getWidth();
                matrix.postTranslate(dp(5), dp(5));
                matrix.postScale(scale, scale);
                roundPaint.setShader(shader);
                shader.setLocalMatrix(matrix);
                bitmapRect.set(dp(5), dp(5), dp(52 + 5), dp(52 + 5));
                canvas.drawRoundRect(bitmapRect, dp(26), dp(26), roundPaint);
            }
            canvas.restore();
            try {
                canvas.setBitmap(null);
            } catch (Exception e) {}
        } catch (Throwable t) {
            t.printStackTrace();
        }
        return result;
    }

    public int dp(float value) {
        if (value == 0) {
            return 0;
        }
        return (int) Math.ceil(getResources().getDisplayMetrics().density * value);
    }


}
