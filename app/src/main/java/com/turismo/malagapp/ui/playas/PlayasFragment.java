package com.turismo.malagapp.ui.playas;

import android.content.Context;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.turismo.malagapp.ListViewAdapter;
import com.turismo.malagapp.MainActivity;
import com.turismo.malagapp.R;
import com.turismo.malagapp.controller.GeoPos;
import com.turismo.malagapp.model.EntityImpl;
import com.turismo.malagapp.model.Playa;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class PlayasFragment extends Fragment {


    private PlayasViewModel mViewModel;
    private DatabaseReference databaseReference;

    private List<EntityImpl> listPlaya = new ArrayList<EntityImpl>();
    private List<EntityImpl> sortedListPlaya = new ArrayList<EntityImpl>();
    private List<String> listNombre = new ArrayList<String>();
    private List<ImageButton> listImgBut = new ArrayList<ImageButton>();
    private List<Integer> listImgView = new ArrayList<Integer>();
    private List<Double> listDistancia = new ArrayList<Double>();
    private List<String> listNombreSorted = new ArrayList<String>();
    private List<Integer> listImgViewSorted = new ArrayList<Integer>();
    private List<Double> listDistanciaSorted = new ArrayList<Double>();


    private ListViewAdapter adapter;
    private ListViewAdapter sortedAdapter;


    private ListView miListaPlayas;
    private ListView miListaNombre;
    private ListView miListaImgBut;
    private ListView miListaImgView;

    private ArrayAdapter<Playa> playaArrayAdapter;
    private ArrayAdapter<String> ciudadArrayAdapter;
    private ArrayAdapter<ImageButton> imgButArrayAdapter;
    private ArrayAdapter<ImageView> imageViewArrayAdapter;

    Context context;

    public static PlayasFragment newInstance() {
        return new PlayasFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.playas_fragment, container, false);

        Spinner spinner = (Spinner) view.findViewById(R.id.spinner_playas);
        miListaPlayas = (ListView) view.findViewById(R.id.listPlayas);
        String[] spinner_content = new String[]{
                "Alfabeticamente",
                "Por distancia"
        };
        ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(getActivity(), R.layout.spinner_item, spinner_content);
        spinnerArrayAdapter.setDropDownViewResource(R.layout.spinner_item);
        spinner.setAdapter(spinnerArrayAdapter);
        //-----------------------------------------------------
        if(MainActivity.grantedPermission){
            MainActivity.geoPos = new GeoPos(getContext());
            MainActivity.geoPos.getLocation(getContext());
        }
        inicializar();
        listar();
        //---------------------------------------------------------
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                if (position == 0){
                    System.out.println("Estamos en la opción de ordenado alfabético");
                    if(MainActivity.grantedPermission){
                        MainActivity.geoPos = new GeoPos(getContext());
                        MainActivity.geoPos.getLocation(getContext());
                    }
                    inicializar();
                    listar();
                }else {
                    if(MainActivity.grantedPermission){
                        System.out.println("Estamos en la opción de ordenado por distancia");
                        MainActivity.geoPos = new GeoPos(getContext());
                        MainActivity.geoPos.getLocation(getContext());
                        inicializar();
                        listarPorDistancia();
                    }else{
                        Toast.makeText(getContext(), "No tiene activada la geolocalización",
                                Toast.LENGTH_SHORT).show();
                        spinner.setSelection(0);
                    }
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // your code here
            }

        });

        //Enviar geopos-------------------------------------------
        //MainActivity.geoPos.getLocation(getContext());
        //System.out.println("------------------------------------------------------------------------------------------------------"+GeoPos.lat);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = ViewModelProviders.of(this).get(PlayasViewModel.class);
        // TODO: Use the ViewModel
    }

    private void listar() {
        databaseReference.child("Playa").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                listNombre.clear();
                listPlaya.clear();
                listImgView.clear();
                listDistancia.clear();
                Map<Playa, Double> distanciasPorPlaya = new HashMap<>();
                Map<Playa, Double> distanciasOrdenadas;
                for(DataSnapshot objSnapShot: dataSnapshot.getChildren()) {
                    Playa playa = objSnapShot.getValue(Playa.class);
                    listPlaya.add(playa);
                    if(MainActivity.grantedPermission){
                        distanciasPorPlaya.put(playa, GeoPos.getDistance(playa.getLatitude(), GeoPos.lat, playa.getLongitude(), GeoPos.lon, 0.0, 0.0));
                    }                }
                for(EntityImpl playa: listPlaya) {
                    Double distancia;
                    //-----------------
                    listNombre.add(playa.getName());
                    String fotoConExtension = playa.getFotoUrl().get(0).toString();
                    List<String> lista = Arrays.asList(fotoConExtension.split("\\."));
                    String fotoSinExtension = lista.get(0);
                    Resources resources = getActivity().getResources();

                    listImgView.add(resources.getIdentifier(fotoSinExtension,"drawable", getContext().getPackageName()));

                    //--------------------------------------
                    if(MainActivity.grantedPermission){
                        distancia = distanciasPorPlaya.get(playa);
                        listDistancia.add(distancia);
                        adapter = new ListViewAdapter(getContext(), listNombre, listImgView, listPlaya, listDistancia);
                    }else{
                        adapter = new ListViewAdapter(getContext(), listNombre, listImgView, listPlaya);
                    }

                    miListaPlayas.setAdapter(adapter);
                }
            }


            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
    private void listarPorDistancia() {
        databaseReference.child("Playa").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                listNombreSorted.clear();
                sortedListPlaya.clear();
                listImgView.clear();
                listDistancia.clear();

                Map<Playa, Double> distanciasPorPlaya = new HashMap<>();
                Map<Playa, Double> distanciasOrdenadas;
                for(DataSnapshot objSnapShot: dataSnapshot.getChildren()) {
                    Playa playa = objSnapShot.getValue(Playa.class);
                    distanciasPorPlaya.put(playa, GeoPos.getDistance(playa.getLatitude(), GeoPos.lat, playa.getLongitude(), GeoPos.lon, 0.0, 0.0));
                }
                distanciasOrdenadas = sortByComparator(distanciasPorPlaya, true);
                for(Playa playa: distanciasOrdenadas.keySet()) {
                    sortedListPlaya.add(playa);
                }
                for(EntityImpl alojamiento: sortedListPlaya) {
                    Double distancia;
                    //-----------------
                    listNombreSorted.add(alojamiento.getName());
                    //listAlojamiento.add(alojamiento);
                    String fotoConExtension = alojamiento.getFotoUrl().get(0).toString();
                    List<String> lista = Arrays.asList(fotoConExtension.split("\\."));
                    String fotoSinExtension = lista.get(0);
                    Resources resources = getActivity().getResources();

                    listImgViewSorted.add(resources.getIdentifier(fotoSinExtension,"drawable", getContext().getPackageName()));

                    //--------------------------------------
                    distancia = distanciasOrdenadas.get(alojamiento);
                    listDistanciaSorted.add(distancia);
                    //---------------------------------------
                    sortedAdapter = new ListViewAdapter(getContext(), listNombreSorted, listImgViewSorted, sortedListPlaya, listDistanciaSorted);

                    miListaPlayas.setAdapter(sortedAdapter);
                }
            }


            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


    private void inicializar() {
        FirebaseApp.initializeApp(getContext());
        databaseReference = FirebaseDatabase.getInstance().getReference();
    }
    private static Map<Playa, Double> sortByComparator(Map<Playa, Double> unsortMap, final boolean order)//Si recibe true en order el ordenado es ascendente
    {

        List<Map.Entry<Playa, Double>> list = new LinkedList<Map.Entry<Playa, Double>>(unsortMap.entrySet());

        // Sorting the list based on values
        Collections.sort(list, new Comparator<Map.Entry<Playa, Double>>()
        {
            public int compare(Map.Entry<Playa, Double> o1,
                               Map.Entry<Playa, Double> o2)
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
        Map<Playa, Double> sortedMap = new LinkedHashMap<Playa, Double>();
        for (Map.Entry<Playa, Double> entry : list)
        {
            sortedMap.put(entry.getKey(), entry.getValue());
        }

        return sortedMap;
    }

}
