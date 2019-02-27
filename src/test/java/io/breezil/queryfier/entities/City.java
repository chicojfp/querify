package io.breezil.queryfier.entities;

import java.util.Date;

public class City {
	String name;
	String major;
	Long pupulation;
	Number area;
	Date foundation;
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getMajor() {
		return major;
	}
	public void setMajor(String major) {
		this.major = major;
	}
	public Long getPupulation() {
		return pupulation;
	}
	public void setPupulation(Long pupulation) {
		this.pupulation = pupulation;
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
