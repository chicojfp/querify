package io.breezil.queryfier.engine;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

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
		
        List<QProjection> projections = new ArrayList<>();
		for(Field f : classToParse.getDeclaredFields()) {
			configureProjectionAndSelections(toParse, q, f);
            projections.add(new QProjection(getColumnName(f), f.getName()));
		}
        
		configureProjections(toParse, q, projections);
		
		return q;
	}

    private void configureProjections(QBase toParse, QQuery q, List<QProjection> projections) {
        if (toParse.getColumns().isEmpty()) {
            projections.forEach(p -> q.addProjection(p));
        } else {
            projections.stream().filter(p -> {
                // System.out.println(toParse.getColumns());
                // System.out.println(" Contém ");
                // System.out.println(p.getAlias());
                // System.out.println(toParse.getColumns().contains(p.getAlias()));
                return toParse.getColumns().contains(p.getAlias());
            }).forEach(p -> q.addProjection(p));
//            for (String columnsAlias : toParse.getColumns()) {
//                String actualColumns = projections.get(columnsAlias);
//                q.addProjection(new QProjection(actualColumns, columnsAlias));
//            }
        }
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
