package com.turismo.malagapp.model;

import java.util.List;

public class Playa extends EntityImpl{
    private String name;
    private String descripcion;
    private double latitude;
    private double longitude;
    private String ciudad;
    private List<String> fotoUrl;

    public Playa(){

    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public String getCiudad() {
        return ciudad;
    }

    public void setCiudad(String ciudad) {
        this.ciudad = ciudad;
    }

    public List<String> getFotoUrl() {
        return fotoUrl;
    }

    public void setFotoUrl(List<String> fotoUrl) {
        this.fotoUrl = fotoUrl;
    }

    @Override
    public String toString() {
        return name;
    }
}
