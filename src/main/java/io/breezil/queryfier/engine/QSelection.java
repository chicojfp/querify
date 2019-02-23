package io.breezil.queryfier.engine;

public class QSelection {
	private String name;
	private String alias;
	
	public QSelection(String name, String alias) {
		this.name = name;
		this.alias = alias;
	}
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getAlias() {
		return alias;
	}
	public void setAlias(String alias) {
		this.alias = alias;
	}
	
	@Override
	public String toString() {
		return this.name + " " + this.alias; 
	}

}
