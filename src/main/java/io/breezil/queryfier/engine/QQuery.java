package io.breezil.queryfier.engine;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import org.hibernate.transform.Transformers;

public class QQuery {
	private String from;
	private final List<QProjection> projections;
    private final List<QSelection> selections;
	private final Map<String, Object> parameters;
    private String entity;
    private String alias;

	public QQuery() {
		this.projections = new ArrayList<QProjection>();
        this.selections = new ArrayList<QSelection>();
		this.parameters = new HashMap<String, Object>();
	}
    
    public String getEntity() {
        return this.entity;
    }
    
    public void setEntity(String entity) {
        this.entity = entity;
    }
    
    public String getAlias() {
        return this.alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }
    
    public String getFrom() {
        return this.from;
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
        for (QSelection s : this.selections) {
            b.append(" AND ");
            b.append(s.getColumn());
            b.append(" = ");
            b.append(s.getAlias());
			b.append("\n");
		}
		b.append("\n");
		b.append("Parameters");
		b.append("\n");
		for (String key : this.parameters.keySet()) {
			b.append(String.format("%s -> %s", key, this.parameters.get(key)));
			b.append("\n");
		}
		b.append("\n");
		b.append("Projections");
		b.append("\n");
		for (QProjection p : this.projections) {
			b.append(p.toString());
			b.append("\n");
		}
		
		return b.toString();
	}

	public void addParameter(String name, Object value) {
		this.parameters.put(name, value);
	}

	public void addProjection(QProjection proj) {
		this.projections.add(proj);
	}
    
    public Query mapToQuery(EntityManager em) {
        
        Query q = em.createQuery(toString());
        mapParameters(q);
        try {
            q.unwrap(org.hibernate.Query.class)
                    .setResultTransformer(Transformers.aliasToBean(Class.forName(getEntity())));
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return q;
    }

    private void mapParameters(Query q) {
        // this.parameters.forEach(p -> q.setFirstResult(p.));
    }
    
}
