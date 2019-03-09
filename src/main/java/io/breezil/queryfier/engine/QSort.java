package io.breezil.queryfier.engine;

public class QSort {
	private String name;
	private String order;
	private boolean hasJoinAlias;
	
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

	public void hasJoinAlias(boolean hasJoinAlias) {
		this.hasJoinAlias = hasJoinAlias;
	}
	
	public String toString(String parentAlias) {
		return String.format("%s %s", this.name, this.order);
	}
	
}
