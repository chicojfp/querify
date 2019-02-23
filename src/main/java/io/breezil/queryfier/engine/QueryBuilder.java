package io.breezil.queryfier.engine;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

import io.breezil.queryfier.engine.annotations.QEntity;
import io.breezil.queryfier.engine.annotations.QField;

public class QueryBuilder {
	
	public QQuery parseQuery(QBase toParse) throws IllegalArgumentException, IllegalAccessException {
		if (toParse == null) {
            return  null;
        }
		
		QQuery q = new QQuery();
		Class<? extends Object> classToParse = toParse.getClass();
		configureAlias(q, classToParse);
		
		Map<String, String> projections = new HashMap<String, String>();
		for(Field f : classToParse.getDeclaredFields()) {
			f.setAccessible(true);
			Object fieldValue = f.get(toParse);
			if (fieldValue != null) {
                String selection = createSelection(q, f);
				q.addSelection(selection);
				q.addParameter(f.getName(), fieldValue);
			}
            projections.put(f.getName(), getFieldName(f));
		}
		
		for (String columnsAlias : toParse.getColumns()) {
            System.out.println(columnsAlias);
			String actualColumns = projections.get(columnsAlias);
            q.addProjection(new QSelection(actualColumns, columnsAlias));
		}
		
		return q;
	}

    private String createSelection(QQuery q, Field f) {
        String equality = getExpression(f);
        String fieldName = getFieldName(f);
        String selection = String.format(" AND %s.%s %s :%s", q.getAlias(), fieldName, equality, f.getName());
        return selection;
    }

    private void configureAlias(QQuery q, Class<? extends Object> classToParse) {
        if (classToParse.isAnnotationPresent(QEntity.class)) {
			QEntity n = classToParse.getAnnotation(QEntity.class);
            q.setAlias(getAlias(n));
            q.setEntity(n.name());
		}
    }

	private String getFieldName(Field f) {
        QField q = f.getAnnotation(QField.class);
        if (q == null) {
            return f.getName();
        }
        return q.name();
    }
    
    private String getExpression(Field f) {
        QField q = f.getAnnotation(QField.class);
        if (q == null) {
            return " = ";
        }
        return " " + q.comparator() + " ";
    }

	private String getAlias(QEntity n) {
		return n.alias().equals("") ? n.name() : n.alias();
	}

}
