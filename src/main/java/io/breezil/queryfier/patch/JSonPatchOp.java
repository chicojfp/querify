package io.breezil.queryfier.patch;

import java.io.Serializable;

public class JSonPatchOp implements Serializable {
	private static final long serialVersionUID = 4657131278146624803L;
	
	private String op;
	private String from;
	private Object patch;

	public JSonPatchOp(String from, Object patch) {
		this.op = "add";
		this.from = from;
		this.patch = patch;
	}
	
	public JSonPatchOp() {
		
	}

	public JSonPatchOp(String op, String from, Object patch) {
		this(from, patch);
		this.op = op;
	}

	public String getOp() {
		return op;
	}

	public void setOp(String op) {
		this.op = op;
	}

	public String getFrom() {
		return from;
	}

	public void setFrom(String from) {
		this.from = from;
	}

	public Object getPatch() {
		return patch;
	}

	public void setPatch(Object patch) {
		this.patch = patch;
	}
	
	@Override
	public String toString() {
		return String.format("[op: %s | from: %s | patch: %s]", getOp(), getFrom(), getPatch());
	}

}
