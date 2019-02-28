package io.breezil.queryfier.engine;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.breezil.queryfier.engine.sql.HQLFormatter;
import io.breezil.queryfier.engine.sql.ParamFormatter;

public class QQuery {
    private final List<QProjection> projections;
    private final List<QSelection> selections;
    private final List<QSort> sortColumns;
    private final Map<String, Object> parameters;
    private Class<? extends Object> entity;
    private String alias;
    
    public QQuery() {
    	this.parameters = new HashMap<>();
        this.projections = new ArrayList<>();
        this.selections = new ArrayList<>();
        this.sortColumns = new ArrayList<>();
    }
    
    public Class<? extends Object> getEntity() {
        return this.entity;
    }
    
    public Map<String, Object> getParameters() {
        return this.parameters;
    }
    
    public void setEntity(Class<? extends Object> entity) {
        this.entity = entity;
    }
    
    public String getAlias() {
        return this.alias;
    }
    
    public void setAlias(String alias) {
        this.alias = alias;
    }
    
    public void addSelection(String selection) {
        this.selections.add(new QSelection(selection));
    }
    
    public void addSelection(QSelection selection) {
        this.selections.add(selection);
    }
    
	public String toHql() {
        return new HQLFormatter(this).toString();
    }
    
    @Override
    public String toString() {
        StringBuilder b = new StringBuilder();
        b.append(toHql());
        b.append("\n\n\n\n");
        b.append(new ParamFormatter(this).toString());
        b.append("\n");
        return b.toString();
    }
    
    public void addParameter(String name, Object value) {
        this.parameters.put(name, value);
    }
    
    public void addProjection(QProjection proj) {
        this.projections.add(proj);
    }

	public void addSortColumns(List<QSort> sortedColumns) {
		this.sortColumns.addAll(sortedColumns);
	}

	public List<QProjection> getProjections() {
		return projections;
	}

	public List<QSelection> getSelections() {
		return selections;
	}

	public List<QSort> getSortColumns() {
		return sortColumns;
	}

}
