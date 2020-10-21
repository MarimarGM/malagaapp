package com.turismo.malagapp.model;

import java.io.Serializable;
import java.util.List;

public class EntityImpl implements IEntity, Serializable {

    private String name;
    private String categoria;
    private String descripcion;
    private long telefono;
    private String web;
    private String mail;
    private double latitude;
    private double longitude;
    private String ciudad;
    private List<String> fotoUrl;

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public String getCategoria() {
        return this.categoria;
    }

    @Override
    public String getDescripcion() {
        return this.descripcion;
    }

    @Override
    public long getTelefono() {
        return this.telefono;
    }

    @Override
    public String getWeb() {
        return this.web;
    }

    @Override
    public String getMail() {
        return null;
    }

    @Override
    public double getLatitude() {
        return this.latitude;
    }

    @Override
    public double getLongitude() {
        return this.longitude;
    }

    @Override
    public String getCiudad() {
        return this.ciudad;
    }

    @Override
    public List<String> getFotoUrl() {
        return this.fotoUrl;
    }
}
