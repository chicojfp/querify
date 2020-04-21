package io.breezil.queryfier.engine;

import io.breezil.queryfier.engine.QBaseClass;
import io.breezil.queryfier.engine.annotations.QEntity;
import io.breezil.queryfier.engine.annotations.QField;
import io.breezil.queryfier.entities.State;

@QEntity(name = State.class, alias = "s")
public class StateFilterTest extends QBaseClass<State, StateFilterTest> {
	String name;
	String governor;

	@QField(name = "country.name")
	String country;
	String main;
	Long population;

	public StateFilterTest() {
		super();
	}

	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getGovernor() {
		return this.governor;
	}

	public void setGovernor(String governor) {
		this.governor = governor;
	}

	public String getCountry() {
		return this.country;
	}

	public void setCountry(String country) {
		this.country = country;
	}

	public String getMain() {
		return this.main;
	}

	public void setMain(String main) {
		this.main = main;
	}

	public Long getPopulation() {
		return population;
	}

	public void setPopulation(Long population) {
		this.population = population;
	}

}
