package io.breezil.queryfier.checker;

public class CheckException extends Exception {

	private static final long serialVersionUID = 1L;
	private CheckErrorType type;
	
	public CheckException(CheckErrorType type, String msg) {
		super(msg);
		this.type = type;
	}

	public CheckErrorType getType() {
		return type;
	}

	public void setType(CheckErrorType type) {
		this.type = type;
	}
	
	

}
