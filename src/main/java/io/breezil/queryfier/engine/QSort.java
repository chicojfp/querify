package io.breezil.queryfier.engine;

public class QSort {
	private String name;
	private String order;
	
	public QSort(String name, String order) {
		this.name = name;
		this.order = order;
	}
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public String getOrder() {
		return order;
	}
	
	public void setOrder(String order) {
		this.order = order;
	}
	
}
