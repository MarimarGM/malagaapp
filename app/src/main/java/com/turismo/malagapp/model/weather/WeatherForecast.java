package com.turismo.malagapp.model.weather;


import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class WeatherForecast implements Serializable {

	private String name;

	private List<WeatherEntry> entries = new ArrayList<>();

	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public List<WeatherEntry> getEntries() {
		return this.entries;
	}

	public void setEntries(List<WeatherEntry> entries) {
		this.entries = entries;
	}

	public void setCity(Map<String, Object> city) {
		setName(city.get("name").toString());
	}

}
