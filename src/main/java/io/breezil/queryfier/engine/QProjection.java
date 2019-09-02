package io.breezil.queryfier.engine;

import io.breezil.queryfier.engine.enums.AggregationFunctionEnum;

public class QProjection extends QSection {
	public static final String SPLITTER = "@";
	private AggregationFunctionEnum groupFunction;
	
	public QProjection(String item, String alias) {
		super(item, alias);
		this.parseItem(item);
	}
	
	public QProjection(String item) {
		super(item, null);
		parseItem(item);
	}
	
	private void parseItem(String item) {
		if (item.contains(SPLITTER)) {
			String[] funcItem = item.split(SPLITTER);
			this.groupFunction = AggregationFunctionEnum.parse(funcItem[0]);
			this.item = funcItem[1];
			this.alias = item.replace(SPLITTER, "_");
		}
//		this.groupedBy = (this.groupFunction == null);
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
		String canonicalFieldName = String.format(" %s%s ", parentAlias, this.item);
		if (this.groupFunction != null) {
			canonicalFieldName = this.groupFunction.aplicarMascara(canonicalFieldName);
		}
		return String.format(" %s AS %s", canonicalFieldName, this.alias);
	}
	
	public String toStringWithoutAlias(String parentAlias) {
		parentAlias = configureAlias(parentAlias);
		return String.format(" %s%s ", parentAlias, this.item);
	}

	public AggregationFunctionEnum getGroupFunction() {
		return groupFunction;
	}

	public void setGroupFunction(AggregationFunctionEnum groupFunction) {
		this.groupFunction = groupFunction;
	}

	public boolean hasAggregation() {
		return this.groupFunction != null;
	}

}
