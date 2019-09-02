package io.breezil.queryfier.engine.enums;

public enum AggregationFunctionEnum {
	SUM,
	COUNT,
	MAX,
	MIN,
	AVG;
	
	public static AggregationFunctionEnum parse(String value) {
		if (value == null) return null;
		return AggregationFunctionEnum.valueOf(value.toUpperCase());
	}

	public String aplicarMascara(String toReplace) {
		return String.format(" %s(%s) ", this, toReplace);
	}
}
