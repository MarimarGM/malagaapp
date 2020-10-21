package com.turismo.malagapp.model.weather;

import java.util.List;

public class WeatherResponseDto {

    private CoordResponseDto coord;
    private List<WeatherDescriptionResponseDto> weather;
    private String base;
    private MainResponseDto main;
    private int visibility;
    private WindResponseDto wind;
    private CloudsResponseDto clouds;
    private int dt;
    private SysResponseDto sys;
    private int timezone;
    private int id;
    private String name;
    private int cod;

    public WeatherResponseDto() {
    }

    public CoordResponseDto getCoord() {
        return coord;
    }

    public void setCoord(CoordResponseDto coord) {
        this.coord = coord;
    }

    public List<WeatherDescriptionResponseDto> getWeather() {
        return weather;
    }

    public void setWeather(List<WeatherDescriptionResponseDto> weather) {
        this.weather = weather;
    }

    public String getBase() {
        return base;
    }

    public void setBase(String base) {
        this.base = base;
    }

    public MainResponseDto getMain() {
        return main;
    }

    public void setMain(MainResponseDto main) {
        this.main = main;
    }

    public int getVisibility() {
        return visibility;
    }

    public void setVisibility(int visibility) {
        this.visibility = visibility;
    }

    public WindResponseDto getWind() {
        return wind;
    }

    public void setWind(WindResponseDto wind) {
        this.wind = wind;
    }

    public CloudsResponseDto getClouds() {
        return clouds;
    }

    public void setClouds(CloudsResponseDto clouds) {
        this.clouds = clouds;
    }

    public int getDt() {
        return dt;
    }

    public void setDt(int dt) {
        this.dt = dt;
    }

    public SysResponseDto getSys() {
        return sys;
    }

    public void setSys(SysResponseDto sys) {
        this.sys = sys;
    }

    public int getTimezone() {
        return timezone;
    }

    public void setTimezone(int timezone) {
        this.timezone = timezone;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getCod() {
        return cod;
    }

    public void setCod(int cod) {
        this.cod = cod;
    }
}
