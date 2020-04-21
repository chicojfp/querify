package io.breezil.queryfier.engine;

import io.breezil.queryfier.engine.annotations.QField;
import io.breezil.queryfier.entities.City;

//@QEntity(name = City.class, alias = "c")
public class CityFilterTest extends QBaseClass<City, CityFilterTest> {
	private static final long serialVersionUID = 1543673095346863136L;
	
	String name;
	@QField(name="major.name")
	String major;
	
	@QField(name="country.name")
	String country;
	@QField(name="state.name")
	String state;
	
	public CityFilterTest() {
		super();
	}
	
	public String getName() {
		return this.name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getMajor() {
		return this.major;
	}
	public void setMajor(String major) {
		this.major = major;
	}
	public String getCountry() {
		return this.country;
	}
	public void setCountry(String country) {
		this.country = country;
	}

	public void setState(String state) {
		this.state = state;
	}
}
