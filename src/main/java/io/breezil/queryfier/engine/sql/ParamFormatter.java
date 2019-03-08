package io.breezil.queryfier.engine.sql;

import io.breezil.queryfier.engine.QQuery;

public class ParamFormatter {
	
	private String text;

	public ParamFormatter(QQuery query) {
		this.text = this.printParameters(query);
	}
	
    private String printParameters(QQuery query) {
    	StringBuilder b = new StringBuilder();
        b.append("Parameters");
        b.append("\n");
        for (String key : query.getParameters().keySet()) {
            b.append(String.format("%s -> %s", key, query.getParameters().get(key)));
            b.append("\n");
        }
        
        return b.toString();
    }
    
    @Override
    public String toString() {
    	return this.text;
    }

}
