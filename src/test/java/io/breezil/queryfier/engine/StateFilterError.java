package io.breezil.queryfier.engine;

import io.breezil.queryfier.engine.annotations.QEntity;
import io.breezil.queryfier.engine.annotations.QField;
import io.breezil.queryfier.entities.State;

@QEntity(name = State.class, alias = "s")
public class StateFilterError extends QBaseClass<State, StateFilterError> {
	String name;
	String governor;
	
	@QField(name="country.name")
	Integer country;
	
	public StateFilterError() {
		super();
	}
	
	public Integer getCountry() {
		return this.country;
	}
	public void setCountry(Integer country) {
		this.country = country;
	}

}
