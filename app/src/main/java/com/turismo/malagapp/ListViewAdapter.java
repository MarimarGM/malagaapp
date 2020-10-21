package com.turismo.malagapp;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.RequiresApi;

import com.turismo.malagapp.controller.GeoPos;
import com.turismo.malagapp.model.EntityImpl;

import java.util.List;

public class ListViewAdapter extends BaseAdapter {
    // Declare Variables
    Context context;
    List<String> titulos;
    List<Integer> imagenes;
    LayoutInflater inflater;
    List<EntityImpl> items;
    List<Double> distancia;
    String txtDistance ="";

    //Añado la lista de museos generada desde la clase museo fragment en el metodo listar
    public ListViewAdapter(Context context, List<String> titulos, List<Integer> imagenes, List<EntityImpl> items, List<Double> distancia) {
        System.out.println("creo adapter----------------------------------------------------");
        this.context = context;
        this.titulos = titulos;
        this.imagenes = imagenes;
        this.items = items;
        this.distancia = distancia;
    }

    public ListViewAdapter(Context context, List<String> titulos, List<Integer> imagenes, List<EntityImpl> items) {
        System.out.println("creo adapter----------------------------------------------------");
        this.context = context;
        this.titulos = titulos;
        this.imagenes = imagenes;
        this.items = items;
    }



    @Override
    public int getCount() {
        return titulos.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public View getView(final int position, View convertView, ViewGroup parent) {

        // Declare Variables
        TextView txtTitle;
        final ImageView imgImg;
        TextView distanceView;
        double distance;
        double distanceConverted = 0.0;


        //http://developer.android.com/intl/es/reference/android/view/LayoutInflater.html
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View itemView = inflater.inflate(R.layout.row, parent, false);

        // Locate the TextViews in listview_item.xml
        txtTitle = (TextView) itemView.findViewById(R.id.idText);
        imgImg = (ImageView) itemView.findViewById(R.id.idImage);
        distanceView = (TextView) itemView.findViewById(R.id.idTxtDistance);

        // Capture position and set to the TextViews
        txtTitle.setText(titulos.get(position));
        imgImg.setImageResource(imagenes.get(position));
        imgImg.setClipToOutline(true);
        //Calculate the distance and set to the textview
        if(MainActivity.grantedPermission){
            distance = GeoPos.getDistance(items.get(position).getLatitude(), GeoPos.lat, items.get(position).getLongitude(), GeoPos.lon, 0.0, 0.0);
            distanceConverted = Math.round(distancia.get(position) / 1000 * 10) / 10.0;
            if(distanceConverted < 1){
                //distance = Math.round(distanceConverted*1000);
                distance = distancia.get(position);
                int distancia = (int) distance;
                txtDistance = "Estás a "+distancia +" m";
            }else{
                txtDistance = "Estás a "+distanceConverted +" km";
            }
            distanceView.setText(txtDistance);
        }


        //-----------------------------------------------------------------------




        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Creo una variable String en la que capturo el nombre del museo de la lista en la posición position
                String name = titulos.get(position);
                Integer image = imagenes.get(position);
                String descripcion = items.get(position).getDescripcion().toString();
                Double latitude = items.get(position).getLatitude();
                Double longitude = items.get(position).getLongitude();
                String url = "null";
                String mail = "null";
                String phone = "null";
                try{
                    url = items.get(position).getWeb().toString();
                }catch (Exception e){
                    e.printStackTrace();
                }
                try{
                    mail = items.get(position).getMail().toString();
                }catch (Exception e){
                    e.printStackTrace();
                }


                try{
                    phone = String.valueOf(items.get(position).getTelefono());
                }catch (Exception e){
                    e.printStackTrace();
                }

                //System.out.println(descripcion);
                //Iniciando la actividad
                Intent intent = new Intent(view.getContext(),ItemActivity.class);
                //Añadiendo los datos al intent
                intent.putExtra("Nombre", name);
                intent.putExtra("Descripcion", descripcion);
                intent.putExtra("ImagenInt", image);
                //intent.putExtra("telf", phone);
                intent.putExtra("web", url);
                intent.putExtra("mail", mail);
                intent.putExtra("phone", phone);
                intent.putExtra("latitude", latitude);
                intent.putExtra("longitude", longitude);
                context.startActivity(intent);
            }
        });

        return itemView;
    }
    public void updateResults(List<String> titulos, List<Integer> imagenes, List<EntityImpl> items, List<Double> distancia) {
        System.out.println("actualizo adapter----------------------------------------------------");
        this.titulos = titulos;
        this.imagenes = imagenes;
        this.items = items;
        this.distancia = distancia;
        notifyDataSetChanged();
    }
}
