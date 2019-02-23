package io.breezil.queryfier.engine;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class QQuery {
	private String from;
	private List<QSelection> projections;
	private List<String> selections;
	private Map<String, Object> parameters;

	public QQuery() {
		this.projections = new ArrayList<QSelection>();
		this.selections = new ArrayList<String>();
		this.parameters = new HashMap<String, Object>();
	}

	public void setFrom(String from) {
		this.from = from;
	}

	public void addSelection(String selection) {
		this.selections.add(selection);
	}
	
	@Override
	public String toString() {
		StringBuilder b = new StringBuilder();
		b.append(this.from);
		b.append("\n");
		b.append("WHERE 1=1 ");
		b.append("\n");
		for (String s : selections) {
			b.append(s);
			b.append("\n");
		}
		b.append("\n");
		b.append("Parameters");
		b.append("\n");
		for (String key : parameters.keySet()) {
			b.append(String.format("%s -> %s", key, parameters.get(key)));
			b.append("\n");
		}
		b.append("\n");
		b.append("Projections");
		b.append("\n");
		for (QSelection p : projections) {
			b.append(p.toString());
			b.append("\n");
		}
		
		return b.toString();
	}

	public void addParameter(String name, Object value) {
		this.parameters.put(name, value);
	}

	public void addProjection(QSelection proj) {
		this.projections.add(proj);		
	}

}
