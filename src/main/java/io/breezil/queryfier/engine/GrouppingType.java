package io.breezil.queryfier.engine;

public enum GrouppingType {
	MAX,
	MIN,
	AVG;

	public static GrouppingType criar(String valor) {
		return GrouppingType.valueOf(valor.toUpperCase());
	}

}
