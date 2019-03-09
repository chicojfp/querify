package io.breezil.queryfier.engine;

public class QSort extends QSection {
	private String order;

	public QSort(String name, String order) {
		super(name, null);
		this.order = order;
	}

	public String getOrder() {
		return order;
	}

	public void setOrder(String order) {
		this.order = order;
	}

	public String toString(String parentAlias) {
		String alias = configureAlias(parentAlias);
		return String.format("%s%s %s", alias, this.item, this.order);
	}

}
