package com.turismo.malagapp.model;

import java.util.List;

public interface IEntity {
    public String getName();
    public String getCategoria();
    public String getDescripcion();
    public long getTelefono();
    public String getWeb();
    public String getMail();
    public double getLatitude();
    public double getLongitude();
    public String getCiudad();
    public List<String> getFotoUrl();
    
}
