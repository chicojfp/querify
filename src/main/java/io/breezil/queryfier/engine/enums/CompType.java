package io.breezil.queryfier.engine.enums;

public enum CompType {
	EQUALS(" = %s "),
	GT(" > %s "),
	GE(" >= %s "),
	MD(" < %s "),
	ME(" <= %s "),
	IN(" IN (%s) "),
    ILIKE(" ILIKE %s "),
	
	;
	
	String expression;
	private CompType(String expression) {
		this.expression = expression;
	}
	
	@Override
	public String toString() {
		return this.expression;
	}

}
