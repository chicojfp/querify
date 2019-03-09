package io.breezil.queryfier.engine.enums;

public enum JoinType {
	LEFT_JOIN("LEFT JOIN"),
	INNER_JOIN("INNER JOIN"),
	RIGHT_JOIN("RIGHT JOIN"),
	FULL_JOIN("FULL JOIN"),
	
	;
	
	String name;
	private JoinType(String name) {
		this.name = name;
	}
	
	@Override
	public String toString() {
		return this.name;
	}

}
