package com.turismo.malagapp.model.weather;

public class WindResponseDto {

    private String speed;
    private String deg;

    public WindResponseDto() {
    }

    public String getSpeed() {
        return speed;
    }

    public void setSpeed(String speed) {
        this.speed = speed;
    }

    public String getDeg() {
        return deg;
    }

    public void setDeg(String deg) {
        this.deg = deg;
    }
}
