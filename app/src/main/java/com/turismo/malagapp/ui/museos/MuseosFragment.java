package com.turismo.malagapp.ui.museos;

import android.content.Context;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
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
import com.turismo.malagapp.model.Museo;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * @author marim
 */
public class MuseosFragment extends Fragment {

    private static final String ALFABETICAMENTE = "Alfabeticamente";
    private static final int PRIMERA_POSICION = 0;
    private static final String QUITAR_EXTENSION = "\\.";
    private static final String POR_DISTANCIA = "Por distancia";
    private static final String MUSEO = "Museo";
    private static final double ELEVACION_POR_DEFECTO = 0.0;
    private MuseosViewModel mViewModel;
    private DatabaseReference databaseReference;

    private List<EntityImpl> listMuseo = new ArrayList<>();
    private List<EntityImpl> sortedListMuseo = new ArrayList<EntityImpl>();
    private List<String> listNombre = new ArrayList<String>();
    private List<Integer> listImgView = new ArrayList<Integer>();
    private List<Double> listDistancia = new ArrayList<Double>();
    private List<String> listNombreSorted = new ArrayList<String>();
    private List<Integer> listImgViewSorted = new ArrayList<Integer>();
    private List<Double> listDistanciaSorted = new ArrayList<Double>();

    private ListViewAdapter adapter;
    private ListViewAdapter sortedAdapter;

    private ListView miListaMuseos;

    Context context;

    public static MuseosFragment newInstance() {
        return new MuseosFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.museos_fragment, container, false);
        Spinner spinner = view.findViewById(R.id.spinner_museos);
        miListaMuseos = view.findViewById(R.id.listMuseos);
        String[] spinner_content = new String[]{
                ALFABETICAMENTE,
                POR_DISTANCIA
        };


        ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<>(getActivity(), R.layout.spinner_item, spinner_content);
        spinnerArrayAdapter.setDropDownViewResource(R.layout.spinner_item);
        spinner.setAdapter(spinnerArrayAdapter);
        //-----------------------------------------------------
        listarConPosicion();
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
            public void onNothingSelected(AdapterView<?> parentView) { }

        });
        return view;
    }

    @Override
    public void onActivityCreated(final @Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = ViewModelProviders.of(this).get(MuseosViewModel.class);
        // TODO: Use the ViewModel
    }

    private void listarConPosicion() {
        if(MainActivity.grantedPermission){
            MainActivity.geoPos = new GeoPos(getContext());
            MainActivity.geoPos.getLocation(getContext());
        }
        inicializar();
        listar();
    }

    private void listarPorDistanciaConPosicion() {
        if(MainActivity.grantedPermission){
            MainActivity.geoPos = new GeoPos(getContext());
            MainActivity.geoPos.getLocation(getContext());
        }
        inicializar();
        listarPorDistancia();
    }

    private void listarPorDistancia() {
        databaseReference.child(MUSEO).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                listNombreSorted.clear();
                sortedListMuseo.clear();
                listImgView.clear();
                listDistancia.clear();

                Map<Museo, Double> distanciasPorMuseo = new HashMap<>();
                Map<Museo, Double> distanciasOrdenadas = new HashMap<>();
                for (DataSnapshot objSnapShot : dataSnapshot.getChildren()) {
                    Museo museo = objSnapShot.getValue(Museo.class);
                    distanciasPorMuseo.put(museo, GeoPos.getDistance(museo.getLatitude(), GeoPos.lat, museo.getLongitude(), GeoPos.lon, 0.0, 0.0));
                }

                distanciasOrdenadas = sortByComparator(distanciasPorMuseo, true);
                sortedListMuseo.addAll(distanciasOrdenadas.keySet());

                for (EntityImpl museo : sortedListMuseo) {
                    Double distancia;
                    //-----------------
                    listNombreSorted.add(museo.getName());
                    //listAlojamiento.add(alojamiento);
                    String fotoConExtension = getFirstPosition(museo.getFotoUrl());
                    List<String> lista = Arrays.asList(fotoConExtension.split(QUITAR_EXTENSION));
                    String fotoSinExtension = lista.get(0);
                    Resources resources = getActivity().getResources();

                    listImgViewSorted.add(resources.getIdentifier(fotoSinExtension, "drawable", getContext().getPackageName()));

                    //--------------------------------------
                    distancia = distanciasOrdenadas.get(museo);
                    listDistanciaSorted.add(distancia);
                    //---------------------------------------
                    sortedAdapter = new ListViewAdapter(getContext(), listNombreSorted, listImgViewSorted, sortedListMuseo, listDistanciaSorted);

                    miListaMuseos.setAdapter(sortedAdapter);
                }
            }


            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private String getFirstPosition(final List<String> lista) {
        return lista.get(0);
    }

    private void listar() {
        databaseReference.child(MUSEO).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                listNombre.clear();
                listMuseo.clear();
                listImgView.clear();
                listDistancia.clear();
                Map<Museo, Double> distanciasPorMuseo = new HashMap<>();
                for (DataSnapshot objSnapShot : dataSnapshot.getChildren()) {
                    Museo museo = objSnapShot.getValue(Museo.class);
                    listMuseo.add(museo);
                    if(MainActivity.grantedPermission) {
                        if (museo != null) {
                            distanciasPorMuseo.put(museo, GeoPos.getDistance(
                                    museo.getLatitude(),
                                    GeoPos.lat,
                                    museo.getLongitude(),
                                    GeoPos.lon,
                                    ELEVACION_POR_DEFECTO,
                                    ELEVACION_POR_DEFECTO)
                            );
                        }
                    }
                }
                for (EntityImpl museo : listMuseo) {
                    Double distancia;
                    //-----------------
                    listNombre.add(museo.getName());
                    String fotoConExtension = museo.getFotoUrl().get(0);
                    List<String> lista = Arrays.asList(fotoConExtension.split(QUITAR_EXTENSION));
                    String fotoSinExtension = getFirstPosition(lista);
                    Resources resources = getActivity().getResources();

                    listImgView.add(resources.getIdentifier(fotoSinExtension, "drawable", getContext().getPackageName()));

                    //--------------------------------------
                    if(MainActivity.grantedPermission){
                        distancia = distanciasPorMuseo.get(museo);
                        listDistancia.add(distancia);
                        adapter = new ListViewAdapter(getContext(), listNombre, listImgView, listMuseo, listDistancia);
                    }else{
                        adapter = new ListViewAdapter(getContext(), listNombre, listImgView, listMuseo);
                    }

                    miListaMuseos.setAdapter(adapter);
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

    private static Map<Museo, Double> sortByComparator(final Map<Museo, Double> unsortMap, final boolean order)//Si recibe true en order el ordenado es ascendente
    {

        List<Map.Entry<Museo, Double>> list = new LinkedList<>(unsortMap.entrySet());

        // Sorting the list based on values
        Collections.sort(list, (o1, o2) ->
                order ? o1.getValue().compareTo(o2.getValue()) : o2.getValue().compareTo(o1.getValue()));

        // Maintaining insertion order with the help of LinkedList
        Map<Museo, Double> sortedMap = new LinkedHashMap<>();
        for (Map.Entry<Museo, Double> entry : list) {
            sortedMap.put(entry.getKey(), entry.getValue());
        }

        return sortedMap;
    }

}
