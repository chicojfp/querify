package io.breezil.queryfier.engine;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

import io.breezil.queryfier.engine.annotations.QEntity;
import io.breezil.queryfier.engine.annotations.QField;

public class QueryBuilder {
	
	public QQuery parseQuery(QBase toParse) throws IllegalArgumentException, IllegalAccessException {
		if (toParse == null)
			return  null;
		
		QQuery q = new QQuery();
		String alias = "";
		Class<? extends Object> c = toParse.getClass();
		if (c.isAnnotationPresent(QEntity.class)) {
			QEntity n = (QEntity) c.getAnnotation(QEntity.class);
			alias = getAlias(n);
			q.setFrom(n.name() + " " + alias );
		}
		
		Map<String, String> projections = new HashMap<String, String>();
		for(Field f : c.getDeclaredFields()) {
			f.setAccessible(true);
			System.out.println();
			Object fValue = f.get(toParse); 
			if (fValue != null) {
				String equality = getFieldName(f);
				String selection = String.format(" AND %s.%s :%s", alias, equality, f.getName());
				q.addSelection(selection);
				q.addParameter(f.getName(), fValue);
			}
			projections.put(getFieldName(f), alias);
//			q.addProjection(new QSelection(getFieldName(f), alias));
		}
		
		for (String p : toParse.getColumns()) {
			String a = projections.get(p);
			q.addProjection(new QSelection(p, a));
		}
		
		return q;
	}

	private String getFieldName(Field f) {
		QField q = (QField) f.getAnnotation(QField.class);
		if (q == null) return f.getName() + " = ";
		return q.name() + " " + q.comparator() + " ";
	}

	private String getAlias(QEntity n) {
		return n.alias().equals("") ? n.name() : n.alias();
	}

}
