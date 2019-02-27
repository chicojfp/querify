package io.breezil.queryfier.entities;

import java.util.Date;
import java.util.List;

public class State {
	List<City> cities;
	City main;
	String name;
	String governor;
	Number area;
	Date foundation;
	
	public List<City> getCities() {
		return cities;
	}
	public void setCities(List<City> cities) {
		this.cities = cities;
	}
	public City getMain() {
		return main;
	}
	public void setMain(City main) {
		this.main = main;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getGovernor() {
		return governor;
	}
	public void setGovernor(String governor) {
		this.governor = governor;
	}
	public Number getArea() {
		return area;
	}
	public void setArea(Number area) {
		this.area = area;
	}
	public Date getFoundation() {
		return foundation;
	}
	public void setFoundation(Date foundation) {
		this.foundation = foundation;
	}
	
	

}
