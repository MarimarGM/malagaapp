package com.turismo.malagapp.ui.home;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.turismo.malagapp.HomeMap;
import com.turismo.malagapp.ListViewAdapter;
import com.turismo.malagapp.MainActivity;
import com.turismo.malagapp.R;
import com.turismo.malagapp.builder.RetrofitClientInstance;
import com.turismo.malagapp.controller.GeoPos;
import com.turismo.malagapp.endpoint.WeatherService;
import com.turismo.malagapp.model.EntityImpl;
import com.turismo.malagapp.model.weather.WeatherDescriptionResponseDto;
import com.turismo.malagapp.model.weather.WeatherResponseDto;
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeFragment extends Fragment implements Serializable{

    Context context;

    private HomeViewModel homeViewModel;
    private TextView temperatura;
    private ImageView icono_weather;
    private DatabaseReference databaseReference;

    private List<EntityImpl> listHome = new ArrayList<EntityImpl>();
    private List<EntityImpl> sortedListHome = new ArrayList<EntityImpl>();
    private List<String> listNombre = new ArrayList<String>();
    private List<Integer> listImgView = new ArrayList<Integer>();
    private List<Double> listDistancia = new ArrayList<Double>();
    private List<String> listNombreSorted = new ArrayList<String>();
    private List<Integer> listImgViewSorted = new ArrayList<Integer>();
    private List<Double> listDistanciaSorted = new ArrayList<Double>();
    public static Button locButton;
    public static Button setLocButton;
    public static Button mapButton;

    private ListViewAdapter adapter;
    private ListViewAdapter sortedAdapter;
    private Double distanceHome = 0.0;
    private ListView miListaHome;
    private boolean puedoUsarMapa = true;


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        homeViewModel =
                ViewModelProviders.of(this).get(HomeViewModel.class);
        View root = inflater.inflate(R.layout.fragment_home, container, false);
        //final TextView textView = root.findViewById(R.id.text_home);

        miListaHome = (ListView) root.findViewById(R.id.listHome);
        locButton = (Button)root.findViewById(R.id.homeLocationButton);
        setLocButton = (Button)root.findViewById(R.id.setLocationButton);
        mapButton = (Button)root.findViewById(R.id.idButtonMap);
        temperatura = root.findViewById(R.id.weather);
        icono_weather = root.findViewById(R.id.imageView_Weather);
        setLocButton.setEnabled(false);
        setLocButton.setVisibility(View.INVISIBLE);
        if(MainActivity.grantedPermission == false){
            mapButton.setEnabled(false);
            mapButton.setVisibility(View.INVISIBLE);
            locButton.setEnabled(false);
            locButton.setVisibility(View.INVISIBLE);
            setLocButton.setEnabled(true);
            setLocButton.setVisibility(View.VISIBLE);
            puedoUsarMapa = false;
        }

        homeViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                //textView.setText(s);
            }
        });

        locButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!MainActivity.grantedPermission){

                    Toast.makeText(getContext(), "No tiene activada la geolocalización",
                            Toast.LENGTH_SHORT).show();
                }


                for(int i = 0; i < 2; i++){
                    if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                        MainActivity.geoPos = new GeoPos(getContext());
                        MainActivity.geoPos.getLocation(getContext());
                    }
                    try {
                        Thread.sleep(500);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                    System.out.println("-------------------------------if de permisos concedidos.-------------------------------------------------");
                        FragmentTransaction ftr = getFragmentManager().beginTransaction();
                        ftr.detach(HomeFragment.this);
                        miListaHome.clearDisappearingChildren();
                        getWeather();
                        MainActivity.geoPos.getLocation(getContext());
                        inicializar();
                        listarPorDistancia();
                        mapButton.setEnabled(true);
                        ftr.attach(HomeFragment.this).commit();

                }
                puedoUsarMapa = true;
            }
        });

        setLocButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!(MainActivity.grantedPermission || MainActivity.pulsadoAceptado)){

                    ActivityCompat.requestPermissions((Activity) getContext(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 44);

                    MainActivity.fromRefresh = true;
                }

            }
        });



        mapButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(puedoUsarMapa){
                    //Iniciando la actividad
                    Intent intent = new Intent(getContext(), HomeMap.class);
                    //Añadiendo los datos al intent
                    intent.putExtra("lista", (Serializable) sortedListHome);
                    intent.putExtra("listaImagenes", (Serializable) listImgViewSorted);
                    startActivity(intent);
                }else{
                    Toast.makeText(getContext(), "Debe actualizar la ubicación para usar el mapa",
                            Toast.LENGTH_SHORT).show();
                }

            }
        });

        inicializar();
        getWeather();
        if(MainActivity.grantedPermission){

            MainActivity.geoPos = new GeoPos(getContext());
            MainActivity.geoPos.getLocation(getContext());

            listarPorDistancia();
        }else{
            listar();
        }

        return root;
    }


    public void getWeather() {
        WeatherService service = RetrofitClientInstance.getRetrofitInstance().create(WeatherService.class);
        Call<WeatherResponseDto> o = service.getWeather(
                "Malaga,es",
                "fe708622d682253fd39202c5fbda9a7e",
                "metric"
        );
        o.enqueue(new Callback<WeatherResponseDto>() {
            @Override
            public void onResponse(Call<WeatherResponseDto> call, Response<WeatherResponseDto> response) {


                //se establece la fecha
                Date fecha = Calendar.getInstance().getTime();
                String fechaFormateada = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(new Date());

                //Se establecen el TextView, el ImageView y el Context usados en el home fragment
                /*TextView temperatura = root.findViewById(R.id.weather);
                ImageView icono_weather  = findViewById(R.id.imageView_Weather);*/
                Context c = getContext();

                //se recupera el id de la imagen y se establece el icono
                List<WeatherDescriptionResponseDto> lista = response.body().getWeather();
                WeatherDescriptionResponseDto weather = lista.get(0);
                String codigo = "d" + weather.getIcon();
                //Asigno la id de la imagen a la int id y asigno la id al imageviewere
                int id = c.getResources().getIdentifier("drawable/" + codigo, null, c.getPackageName());
                icono_weather.setImageResource(id);

                //Se establecen el nombre de la ciudad y la temperatura, redondeando la ultima y añadiendolo
                //al string que se va a reenderizar en pantalla
                String city = response.body().getName();
                String t = response.body().getMain().getTemp();
                float f = Float.parseFloat(t);
                int temper = Math.round(f);
                String end = " " + city + ", " + Integer.toString(temper) + "º";
                temperatura.setText(end);
            }

            @Override
            public void onFailure(Call<WeatherResponseDto> call, Throwable t) {
                //TextView s = findViewById(R.id.weather);
                temperatura.setText("");
            }
        });
    }
    private void inicializar() {
        FirebaseApp.initializeApp(getContext());
        databaseReference = FirebaseDatabase.getInstance().getReference();
    }



            private void listarPorDistancia() {
                databaseReference.child("Todos").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        listNombreSorted.clear();
                        sortedListHome.clear();
                        listImgViewSorted.clear();
                        listDistanciaSorted.clear();

                        Map<EntityImpl, Double> distanciasPorHome = new HashMap<>();
                        Map<EntityImpl, Double> distanciasOrdenadas;
                        for(DataSnapshot objSnapShot: dataSnapshot.getChildren()) {

                            EntityImpl home = objSnapShot.getValue(EntityImpl.class);

                            distanceHome = GeoPos.getDistance(home.getLatitude(), GeoPos.lat, home.getLongitude(), GeoPos.lon, 0.0, 0.0);

                            distanciasPorHome.put(home, distanceHome);
                        }
                        distanciasOrdenadas = sortByComparator(distanciasPorHome, true);

                        for(EntityImpl sortedHome: distanciasOrdenadas.keySet()) {
                            sortedListHome.add(sortedHome);
                        }
                        //for(EntityImpl home: sortedListHome) {
                        for(int i = 0; i < 20; i++){
                            Double distancia;
                            //-----------------
                            //listNombreSorted.add(home.getName());
                            listNombreSorted.add(sortedListHome.get(i).getName());
                            //String fotoConExtension = home.getFotoUrl().get(0).toString();
                            String fotoConExtension = sortedListHome.get(i).getFotoUrl().get(0).toString();
                            List<String> lista = Arrays.asList(fotoConExtension.split("\\."));
                            String fotoSinExtension = lista.get(0);
                            Resources resources = getActivity().getResources();

                            listImgViewSorted.add(resources.getIdentifier(fotoSinExtension,"drawable", getContext().getPackageName()));

                            //--------------------------------------
                            //distancia = distanciasOrdenadas.get(home);
                            distancia = distanciasOrdenadas.get(sortedListHome.get(i));
                            listDistanciaSorted.add(distancia);
                            //---------------------------------------
                            sortedAdapter = new ListViewAdapter(getContext(), listNombreSorted, listImgViewSorted, sortedListHome, listDistanciaSorted);

                            miListaHome.setAdapter(sortedAdapter);
                        }
                    }


            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

            private void listar() {
                databaseReference.child("Todos").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        listNombre.clear();
                        listHome.clear();
                        listImgView.clear();

                        for(DataSnapshot objSnapShot: dataSnapshot.getChildren()) {

                            EntityImpl home = objSnapShot.getValue(EntityImpl.class);
                            listHome.add(home);
                        }

                        for(int i = 0; i < 20; i++){
                            listNombre.add(listHome.get(i).getName());
                            String fotoConExtension = listHome.get(i).getFotoUrl().get(0).toString();
                            List<String> lista = Arrays.asList(fotoConExtension.split("\\."));
                            String fotoSinExtension = lista.get(0);
                            Resources resources = getActivity().getResources();

                            listImgView.add(resources.getIdentifier(fotoSinExtension,"drawable", getContext().getPackageName()));

                            //---------------------------------------
                            adapter = new ListViewAdapter(getContext(), listNombre, listImgView, listHome);

                            miListaHome.setAdapter(adapter);
                        }
                    }


                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }



    private static Map<EntityImpl, Double> sortByComparator(Map<EntityImpl, Double> unsortMap, final boolean order)//Si recibe true en order el ordenado es ascendente
    {

        List<Map.Entry<EntityImpl, Double>> list = new LinkedList<Map.Entry<EntityImpl, Double>>(unsortMap.entrySet());

        // Sorting the list based on values
        Collections.sort(list, new Comparator<Map.Entry<EntityImpl, Double>>()
        {
            public int compare(Map.Entry<EntityImpl, Double> o1,
                               Map.Entry<EntityImpl, Double> o2)
            {
                if (order)
                {
                    return o1.getValue().compareTo(o2.getValue());
                }
                else
                {
                    return o2.getValue().compareTo(o1.getValue());

                }
            }
        });

        // Maintaining insertion order with the help of LinkedList
        Map<EntityImpl, Double> sortedMap = new LinkedHashMap<EntityImpl, Double>();
        for (Map.Entry<EntityImpl, Double> entry : list)
        {
            sortedMap.put(entry.getKey(), entry.getValue());
        }

        return sortedMap;
    }

    private class Async extends AsyncTask {




        @Override
        protected Object doInBackground(Object[] objects) {

            return null;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            MainActivity.geoPos = new GeoPos(getContext());
        }

        @Override
        protected void onPostExecute(Object o) {
            super.onPostExecute(o);
            if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

                System.out.println("-------------------------------if de permisos concedidos.-------------------------------------------------");
                MainActivity.grantedPermission = true;
                FragmentTransaction ftr = getFragmentManager().beginTransaction();
                ftr.detach(HomeFragment.this);
                miListaHome.clearDisappearingChildren();
                getWeather();
                //MainActivity.geoPos = new GeoPos(getContext());
                MainActivity.geoPos.getLocation(getContext());
                inicializar();
                listarPorDistancia();
                mapButton.setEnabled(true);
                ftr.attach(HomeFragment.this).commit();
            }
        }
    }

}