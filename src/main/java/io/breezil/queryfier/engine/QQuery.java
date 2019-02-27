package io.breezil.queryfier.engine;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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
    
    public String mapProjections() {
        return getSelectStatement() + this.projections.stream()
        	.map(p -> p.toString(getAlias()))
        	.collect(Collectors.joining(",\n"));
    }

    private String getSelectStatement() {
        String select = "";
        if (!this.projections.isEmpty()) {
            select = "SELECT \n";
        }
        return select;
    }
    
    public String mapSelections() {
        return this.selections.stream().map(p -> p.toString(getAlias())).collect(Collectors.joining("\n"));
    }
    
    public String getFrom() {
        StringBuilder b = new StringBuilder();
        b.append(" FROM ");
        b.append(getEntity().getName());
        b.append(" ");
        b.append(this.alias);
        
        return b.toString();
    }
    
    public void addSelection(String selection) {
        this.selections.add(new QSelection(selection));
    }
    
    public void addSelection(QSelection selection) {
        this.selections.add(selection);
    }
    
    @Override
    public String toString() {
        StringBuilder b = new StringBuilder();
        b.append(toHql());
        b.append("\n\n\n\n");
        printParameters(b);
        b.append("\n");
        
        return b.toString();
    }

    public String toHql() {
        StringBuilder b = new StringBuilder();
        b.append(mapProjections());
        b.append("\n");
        b.append(getFrom());
        b.append("\n");
        b.append("WHERE 1=1 ");
        b.append("\n");
        b.append(mapSelections());
        b.append("\n");
        b.append(createOrderBy());
        return b.toString();
    }
    
    public String createOrderBy () {
    	StringBuilder b = new StringBuilder();
    	if (!this.sortColumns.isEmpty()) {
			b.append(" ORDER BY ");
			b.append( this.sortColumns.stream()
				.map(sort -> sort.getName() + " " + sort.getOrder())
				.collect(Collectors.joining(", "))
			);
		}
    	return b.toString();
    }

    private void printParameters(StringBuilder b) {
        b.append("Parameters");
        b.append("\n");
        for (String key : this.parameters.keySet()) {
            b.append(String.format("%s -> %s", key, this.parameters.get(key)));
            b.append("\n");
        }
    }
    
    public void addParameter(String name, Object value) {
        this.parameters.put(name, value);
    }
    
    public void addProjection(QProjection proj) {
        this.projections.add(proj);
    }

	public void addSortColumns(List<String> sortedColumns) {
		sortedColumns.forEach(column -> {
			String order = "ASC";
			if (column.startsWith("!")) {
				order = "DESC";
				column = column.substring(1);
			}
			this.sortColumns.add(new QSort(column, order));
		});
	}
    
    // public Query mapToQuery(EntityManager em) {
    //
    // Query q = em.createQuery(toString());
    // mapParameters(q);
    // q.unwrap(org.hibernate.Query.class).setResultTransformer(Transformers.aliasToBean(getEntity()));
    // return q;
    // }
    //
    // private void mapParameters(Query q) {
    // // this.parameters.forEach(p -> q.setFirstResult(p.));
    // }
    
}
