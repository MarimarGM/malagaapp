package com.turismo.malagapp.ui.naturaleza;

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
import com.turismo.malagapp.model.Naturaleza;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class NaturalezaFragment extends Fragment {

    private NaturalezaViewModel mViewModel;
    private DatabaseReference databaseReference;

    private List<EntityImpl> listNaturaleza = new ArrayList<EntityImpl>();
    private List<EntityImpl> sortedListNaturaleza = new ArrayList<EntityImpl>();
    private List<String> listNombre = new ArrayList<String>();
    private List<ImageButton> listImgBut = new ArrayList<ImageButton>();
    private List<Integer> listImgView = new ArrayList<Integer>();
    private List<Double> listDistancia = new ArrayList<Double>();
    private List<String> listNombreSorted = new ArrayList<String>();
    private List<Integer> listImgViewSorted = new ArrayList<Integer>();
    private List<Double> listDistanciaSorted = new ArrayList<Double>();

    private ListViewAdapter adapter;
    private ListViewAdapter sortedAdapter;


    private ListView miListaNaturaleza;
    private ListView miListaNombre;
    private ListView miListaImgBut;
    private ListView miListaImgView;

    private ArrayAdapter<Naturaleza> naturalezaArrayAdapter;
    private ArrayAdapter<String> ciudadArrayAdapter;
    private ArrayAdapter<ImageButton> imgButArrayAdapter;
    private ArrayAdapter<ImageView> imageViewArrayAdapter;

    public static NaturalezaFragment newInstance() {
        return new NaturalezaFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.naturaleza_fragment, container, false);
        Spinner spinner = (Spinner) view.findViewById(R.id.spinner_naturaleza);
        miListaNaturaleza = (ListView) view.findViewById(R.id.listNaturaleza);
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
        mViewModel = ViewModelProviders.of(this).get(NaturalezaViewModel.class);
        // TODO: Use the ViewModel
    }

    private void listar() {
        databaseReference.child("Naturaleza").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                listNombre.clear();
                listNaturaleza.clear();
                listImgView.clear();
                listDistancia.clear();
                Map<Naturaleza, Double> distanciasPorNaturaleza = new HashMap<>();
                Map<Naturaleza, Double> distanciasOrdenadas;
                for(DataSnapshot objSnapShot: dataSnapshot.getChildren()) {
                    Naturaleza naturaleza = objSnapShot.getValue(Naturaleza.class);
                    listNaturaleza.add(naturaleza);
                    if(MainActivity.grantedPermission){
                        distanciasPorNaturaleza.put(naturaleza, GeoPos.getDistance(naturaleza.getLatitude(), GeoPos.lat, naturaleza.getLongitude(), GeoPos.lon, 0.0, 0.0));
                    }                }
                for(EntityImpl naturaleza: listNaturaleza) {
                    Double distancia;
                    //-----------------
                    listNombre.add(naturaleza.getName());
                    String fotoConExtension = naturaleza.getFotoUrl().get(0).toString();
                    List<String> lista = Arrays.asList(fotoConExtension.split("\\."));
                    String fotoSinExtension = lista.get(0);
                    Resources resources = getActivity().getResources();

                    listImgView.add(resources.getIdentifier(fotoSinExtension,"drawable", getContext().getPackageName()));

                    //--------------------------------------
                    if(MainActivity.grantedPermission){
                        distancia = distanciasPorNaturaleza.get(naturaleza);
                        listDistancia.add(distancia);
                        adapter = new ListViewAdapter(getContext(), listNombre, listImgView, listNaturaleza, listDistancia);
                    }else{
                        adapter = new ListViewAdapter(getContext(), listNombre, listImgView, listNaturaleza);
                    }

                    miListaNaturaleza.setAdapter(adapter);
                }
            }


            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void listarPorDistancia() {
        databaseReference.child("Naturaleza").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                listNombreSorted.clear();
                sortedListNaturaleza.clear();
                listImgView.clear();
                listDistancia.clear();

                Map<Naturaleza, Double> distanciasPorNaturaleza = new HashMap<>();
                Map<Naturaleza, Double> distanciasOrdenadas;
                for(DataSnapshot objSnapShot: dataSnapshot.getChildren()) {
                    Naturaleza naturaleza = objSnapShot.getValue(Naturaleza.class);
                    distanciasPorNaturaleza.put(naturaleza, GeoPos.getDistance(naturaleza.getLatitude(), GeoPos.lat, naturaleza.getLongitude(), GeoPos.lon, 0.0, 0.0));
                }
                distanciasOrdenadas = sortByComparator(distanciasPorNaturaleza, true);
                for(Naturaleza naturaleza: distanciasOrdenadas.keySet()) {
                    sortedListNaturaleza.add(naturaleza);
                }
                for(EntityImpl alojamiento: sortedListNaturaleza) {
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
                    sortedAdapter = new ListViewAdapter(getContext(), listNombreSorted, listImgViewSorted, sortedListNaturaleza, listDistanciaSorted);

                    miListaNaturaleza.setAdapter(sortedAdapter);
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
    private static Map<Naturaleza, Double> sortByComparator(Map<Naturaleza, Double> unsortMap, final boolean order)//Si recibe true en order el ordenado es ascendente
    {

        List<Map.Entry<Naturaleza, Double>> list = new LinkedList<Map.Entry<Naturaleza, Double>>(unsortMap.entrySet());

        // Sorting the list based on values
        Collections.sort(list, new Comparator<Map.Entry<Naturaleza, Double>>()
        {
            public int compare(Map.Entry<Naturaleza, Double> o1,
                               Map.Entry<Naturaleza, Double> o2)
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
        Map<Naturaleza, Double> sortedMap = new LinkedHashMap<Naturaleza, Double>();
        for (Map.Entry<Naturaleza, Double> entry : list)
        {
            sortedMap.put(entry.getKey(), entry.getValue());
        }

        return sortedMap;
    }

}
