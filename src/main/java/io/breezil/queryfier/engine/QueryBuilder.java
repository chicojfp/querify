package io.breezil.queryfier.engine;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

import io.breezil.queryfier.engine.annotations.QEntity;
import io.breezil.queryfier.engine.annotations.QField;

public class QueryBuilder {
	
    public QQuery parseQuery(QBase toParse) throws IllegalAccessException {
		if (toParse == null) {
            return  null;
        }
		
		QQuery q = new QQuery();
		Class<? extends Object> classToParse = toParse.getClass();
		configureAlias(q, classToParse);
		
        Map<String, String> projections = new HashMap<>();
		for(Field f : classToParse.getDeclaredFields()) {
			configureProjectionAndSelections(toParse, q, f);
            projections.put(f.getName(), getColumnName(f));
		}
        
		for (String columnsAlias : toParse.getColumns()) {
			String actualColumns = projections.get(columnsAlias);
            q.addProjection(new QProjection(actualColumns, columnsAlias));
		}
		
		return q;
	}

    private void configureProjectionAndSelections(QBase toParse, QQuery q, Field f) throws IllegalAccessException {
        f.setAccessible(true);
        Object fieldValue = f.get(toParse);
        if (fieldValue != null) {
            QSelection selection = createSelection(q, f);
        	q.addSelection(selection);
        	q.addParameter(f.getName(), fieldValue);
        }
    }

    private QSelection createSelection(QQuery q, Field f) {
        String columnName = getColumnName(f);
        String operator = getComparator(f);
        return new QSelection(columnName, f.getName(), operator);
    }

    private void configureAlias(QQuery q, Class<? extends Object> classToParse) {
        if (classToParse.isAnnotationPresent(QEntity.class)) {
			QEntity n = classToParse.getAnnotation(QEntity.class);
            q.setAlias(getAlias(n));
            q.setEntity(n.name());
		}
    }

    private String getColumnName(Field f) {
        QField q = f.getAnnotation(QField.class);
        if (q == null) {
            return f.getName();
        }
        return q.name();
    }
    
    private String getComparator(Field f) {
        QField q = f.getAnnotation(QField.class);
        if (q == null) {
            return "=";
        }
        return q.comparator();
    }
    
	private String getAlias(QEntity n) {
        return n.alias().equals("") ? n.name().getName() : n.alias();
	}

}
