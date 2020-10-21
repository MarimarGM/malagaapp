package com.turismo.malagapp.ui.cultura;

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
import com.turismo.malagapp.model.Cultura;
import com.turismo.malagapp.model.EntityImpl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class CulturaFragment extends Fragment {


    private DatabaseReference databaseReference;
    private CulturaViewModel mViewModel;

    private List<EntityImpl> listCultura = new ArrayList<>();
    private List<EntityImpl> sortedListCultura = new ArrayList<EntityImpl>();
    private List<String> listNombre = new ArrayList<>();
    private List<Integer> listImgView = new ArrayList<>();
    private List<Double> listDistancia = new ArrayList<Double>();
    private List<String> listNombreSorted = new ArrayList<String>();
    private List<Integer> listImgViewSorted = new ArrayList<Integer>();
    private List<Double> listDistanciaSorted = new ArrayList<Double>();

    private ListViewAdapter adapter;
    private ListViewAdapter sortedAdapter;

    private ListView miListaCultura;

    Context context;

    public static CulturaFragment newInstance() {
        return new CulturaFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.cultura_fragment, container, false);
        Spinner spinner = (Spinner) view.findViewById(R.id.spinner_cultura);
        miListaCultura = (ListView) view.findViewById(R.id.listCultura);
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
        mViewModel = ViewModelProviders.of(this).get(CulturaViewModel.class);
        // TODO: Use the ViewModel
    }

    private void listar() {
        databaseReference.child("Cultura").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                listNombre.clear();
                listCultura.clear();
                listImgView.clear();
                listDistancia.clear();
                Map<Cultura, Double> distanciasPorAlojamiento = new HashMap<>();
                Map<Cultura, Double> distanciasOrdenadas;
                for(DataSnapshot objSnapShot: dataSnapshot.getChildren()) {
                    Cultura cultura = objSnapShot.getValue(Cultura.class);
                    listCultura.add(cultura);
                    if(MainActivity.grantedPermission){
                        distanciasPorAlojamiento.put(cultura, GeoPos.getDistance(cultura.getLatitude(), GeoPos.lat, cultura.getLongitude(), GeoPos.lon, 0.0, 0.0));
                    }                }
                for(EntityImpl cultura: listCultura) {
                    Double distancia;
                    //-----------------
                    listNombre.add(cultura.getName());
                    String fotoConExtension = cultura.getFotoUrl().get(0).toString();
                    List<String> lista = Arrays.asList(fotoConExtension.split("\\."));
                    String fotoSinExtension = lista.get(0);
                    Resources resources = getActivity().getResources();

                    listImgView.add(resources.getIdentifier(fotoSinExtension,"drawable", getContext().getPackageName()));

                    //--------------------------------------
                    if(MainActivity.grantedPermission){
                        distancia = distanciasPorAlojamiento.get(cultura);
                        listDistancia.add(distancia);
                        adapter = new ListViewAdapter(getContext(), listNombre, listImgView, listCultura, listDistancia);
                    }else{
                        adapter = new ListViewAdapter(getContext(), listNombre, listImgView, listCultura);
                    }

                    miListaCultura.setAdapter(adapter);
                }
            }


            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
    private void listarPorDistancia() {
        databaseReference.child("Cultura").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                listNombreSorted.clear();
                sortedListCultura.clear();
                listImgView.clear();
                listDistancia.clear();

                Map<Cultura, Double> distanciasPorCultura = new HashMap<>();
                Map<Cultura, Double> distanciasOrdenadas;
                for(DataSnapshot objSnapShot: dataSnapshot.getChildren()) {
                    Cultura cultura = objSnapShot.getValue(Cultura.class);
                    distanciasPorCultura.put(cultura, GeoPos.getDistance(cultura.getLatitude(), GeoPos.lat, cultura.getLongitude(), GeoPos.lon, 0.0, 0.0));
                }
                distanciasOrdenadas = sortByComparator(distanciasPorCultura, true);
                for(Cultura cultura: distanciasOrdenadas.keySet()) {
                    sortedListCultura.add(cultura);
                }
                for(EntityImpl cultura: sortedListCultura) {
                    Double distancia;
                    //-----------------
                    listNombreSorted.add(cultura.getName());
                    //listAlojamiento.add(alojamiento);
                    String fotoConExtension = cultura.getFotoUrl().get(0).toString();
                    List<String> lista = Arrays.asList(fotoConExtension.split("\\."));
                    String fotoSinExtension = lista.get(0);
                    Resources resources = getActivity().getResources();

                    listImgViewSorted.add(resources.getIdentifier(fotoSinExtension,"drawable", getContext().getPackageName()));

                    //--------------------------------------
                    distancia = distanciasOrdenadas.get(cultura);
                    listDistanciaSorted.add(distancia);
                    //---------------------------------------
                    sortedAdapter = new ListViewAdapter(getContext(), listNombreSorted, listImgViewSorted, sortedListCultura, listDistanciaSorted);

                    miListaCultura.setAdapter(sortedAdapter);
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

    private static Map<Cultura, Double> sortByComparator(Map<Cultura, Double> unsortMap, final boolean order)//Si recibe true en order el ordenado es ascendente
    {

        List<Map.Entry<Cultura, Double>> list = new LinkedList<Map.Entry<Cultura, Double>>(unsortMap.entrySet());

        // Sorting the list based on values
        Collections.sort(list, new Comparator<Map.Entry<Cultura, Double>>()
        {
            public int compare(Map.Entry<Cultura, Double> o1,
                               Map.Entry<Cultura, Double> o2)
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
        Map<Cultura, Double> sortedMap = new LinkedHashMap<Cultura, Double>();
        for (Map.Entry<Cultura, Double> entry : list)
        {
            sortedMap.put(entry.getKey(), entry.getValue());
        }

        return sortedMap;
    }
}
