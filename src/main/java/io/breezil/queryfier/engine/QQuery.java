package io.breezil.queryfier.engine;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class QQuery {
    private String from;
    private final List<QProjection> projections;
    private final List<QSelection> selections;
    private final Map<String, Object> parameters;
    private Class<? extends Object> entity;
    private String alias;
    
    public QQuery() {
        this.projections = new ArrayList<>();
        this.selections = new ArrayList<>();
        this.parameters = new HashMap<>();
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
        return getSelectStatement() + this.projections.stream().map(p -> p.toString(getAlias())).collect(Collectors.joining(",\n"));
    }

    private String getSelectStatement() {
        String select = "";
        if (!this.projections.isEmpty()) {
            select = "select \n";
        }
        return select;
    }
    
    public String mapSelections() {
        return this.selections.stream().map(p -> p.toString(getAlias())).collect(Collectors.joining("\n"));
    }
    
    public String getFrom() {
        StringBuilder b = new StringBuilder();
        b.append(" from ");
        b.append(getEntity().getName());
        b.append(" ");
        b.append(this.alias);
        
        return b.toString();
    }
    
    public void setFrom(String from) {
        this.from = from;
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
