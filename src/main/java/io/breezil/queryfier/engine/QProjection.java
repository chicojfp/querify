package io.breezil.queryfier.engine;

public class QProjection extends QSection {
	
	public QProjection() {
		super(null, null);
	}
	
	public QProjection(String item, String alias) {
		super(item, alias);
	}

	public String getAlias() {
		return this.alias;
	}

	public void setAlias(String alias) {
		this.alias = alias;
	}

	@Override
	public String toString() {
		return toString("");
	}

	public String toString(String parentAlias) {
		parentAlias = configureAlias(parentAlias);
		return String.format(" %s%s AS %s", parentAlias, this.item, this.alias);
	}

}
