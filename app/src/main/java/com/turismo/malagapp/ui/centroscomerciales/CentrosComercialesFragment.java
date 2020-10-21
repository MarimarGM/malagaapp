package com.turismo.malagapp.ui.centroscomerciales;

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
import com.turismo.malagapp.model.Shopping;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class CentrosComercialesFragment extends Fragment {

    private CentrosComercialesViewModel mViewModel;
    private DatabaseReference databaseReference;

    private List<EntityImpl> listShopping = new ArrayList<EntityImpl>();
    private List<EntityImpl> sortedListShopping = new ArrayList<EntityImpl>();
    private List<String> listNombre = new ArrayList<String>();
    private List<Integer> listImgView = new ArrayList<Integer>();
    private List<Double> listDistancia = new ArrayList<Double>();
    private List<String> listNombreSorted = new ArrayList<String>();
    private List<Integer> listImgViewSorted = new ArrayList<Integer>();
    private List<Double> listDistanciaSorted = new ArrayList<Double>();

    private ListViewAdapter adapter;
    private ListViewAdapter sortedAdapter;

    private ListView miListaShopping;


    public static CentrosComercialesFragment newInstance() {
        return new CentrosComercialesFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.centros_comerciales_fragment, container, false);
        Spinner spinner = (Spinner) view.findViewById(R.id.spinner_centros_comerciales);
        miListaShopping = (ListView) view.findViewById(R.id.listCentrosComerciales);
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


        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = ViewModelProviders.of(this).get(CentrosComercialesViewModel.class);
        // TODO: Use the ViewModel
    }
    private void listarPorDistancia() {
        databaseReference.child("Shopping").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                listNombreSorted.clear();
                sortedListShopping.clear();
                listImgView.clear();
                listDistancia.clear();

                Map<Shopping, Double> distanciasPorItem = new HashMap<>();
                Map<Shopping, Double> distanciasOrdenadas;
                for(DataSnapshot objSnapShot: dataSnapshot.getChildren()) {
                    Shopping shopping = objSnapShot.getValue(Shopping.class);
                    distanciasPorItem.put(shopping, GeoPos.getDistance(shopping.getLatitude(), GeoPos.lat, shopping.getLongitude(), GeoPos.lon, 0.0, 0.0));
                }
                distanciasOrdenadas = sortByComparator(distanciasPorItem, true);
                for(Shopping shopping: distanciasOrdenadas.keySet()) {
                    sortedListShopping.add(shopping);
                }
                for(EntityImpl shopping: sortedListShopping) {
                    Double distancia;
                    //-----------------
                    listNombreSorted.add(shopping.getName());
                    String fotoConExtension = shopping.getFotoUrl().get(0).toString();
                    List<String> lista = Arrays.asList(fotoConExtension.split("\\."));
                    String fotoSinExtension = lista.get(0);
                    Resources resources = getActivity().getResources();

                    listImgViewSorted.add(resources.getIdentifier(fotoSinExtension,"drawable", getContext().getPackageName()));

                    //--------------------------------------
                    if(MainActivity.grantedPermission){
                        distancia = distanciasPorItem.get(shopping);
                        listDistancia.add(distancia);
                        adapter = new ListViewAdapter(getContext(), listNombre, listImgView, listShopping, listDistancia);
                    }else{
                        adapter = new ListViewAdapter(getContext(), listNombre, listImgView, listShopping);
                    }
                    //---------------------------------------

                    miListaShopping.setAdapter(sortedAdapter);
                }
            }


            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void listar() {
        databaseReference.child("Shopping").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                listNombre.clear();
                listShopping.clear();
                listImgView.clear();
                listDistancia.clear();
                Map<Shopping, Double> distanciasPorItem = new HashMap<>();
                Map<Shopping, Double> distanciasOrdenadas;
                for(DataSnapshot objSnapShot: dataSnapshot.getChildren()) {
                    Shopping shopping = objSnapShot.getValue(Shopping.class);
                    listShopping.add(shopping);
                    if(MainActivity.grantedPermission){
                        distanciasPorItem.put(shopping, GeoPos.getDistance(shopping.getLatitude(), GeoPos.lat, shopping.getLongitude(), GeoPos.lon, 0.0, 0.0));
                    }                }
                for(EntityImpl shopping: listShopping) {
                    Double distancia;
                    //-----------------
                    listNombre.add(shopping.getName());
                    String fotoConExtension = shopping.getFotoUrl().get(0).toString();
                    List<String> lista = Arrays.asList(fotoConExtension.split("\\."));
                    String fotoSinExtension = lista.get(0);
                    Resources resources = getActivity().getResources();

                    listImgView.add(resources.getIdentifier(fotoSinExtension,"drawable", getContext().getPackageName()));

                    //--------------------------------------
                    distancia = distanciasPorItem.get(shopping);
                    listDistancia.add(distancia);
                    if(MainActivity.grantedPermission){
                        distancia = distanciasPorItem.get(shopping);
                        listDistancia.add(distancia);
                        adapter = new ListViewAdapter(getContext(), listNombre, listImgView, listShopping, listDistancia);
                    }else{
                        adapter = new ListViewAdapter(getContext(), listNombre, listImgView, listShopping);
                    }
                    //---------------------------------------

                    if(MainActivity.grantedPermission){
                        distancia = distanciasPorItem.get(shopping);
                        listDistancia.add(distancia);
                        adapter = new ListViewAdapter(getContext(), listNombre, listImgView, listShopping, listDistancia);
                    }else{
                        adapter = new ListViewAdapter(getContext(), listNombre, listImgView, listShopping);
                    }

                    miListaShopping.setAdapter(adapter);
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

    private static Map<Shopping, Double> sortByComparator(Map<Shopping, Double> unsortMap, final boolean order)//Si recibe true en order el ordenado es ascendente
    {

        List<Map.Entry<Shopping, Double>> list = new LinkedList<Map.Entry<Shopping, Double>>(unsortMap.entrySet());

        // Sorting the list based on values
        Collections.sort(list, new Comparator<Map.Entry<Shopping, Double>>()
        {
            public int compare(Map.Entry<Shopping, Double> o1,
                               Map.Entry<Shopping, Double> o2)
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
        Map<Shopping, Double> sortedMap = new LinkedHashMap<Shopping, Double>();
        for (Map.Entry<Shopping, Double> entry : list)
        {
            sortedMap.put(entry.getKey(), entry.getValue());
        }

        return sortedMap;
    }
}
