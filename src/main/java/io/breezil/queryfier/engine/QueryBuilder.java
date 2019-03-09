package io.breezil.queryfier.engine;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import io.breezil.queryfier.engine.annotations.QEntity;
import io.breezil.queryfier.engine.annotations.QField;

public class QueryBuilder {

	private Map<String, String> joinMaps;

	public QQuery parseQuery(QBase toParse) throws IllegalAccessException {
		if (toParse == null) {
			return null;
		}
		Map<String, String> allAlias2Cols = new HashMap<>();

		QQuery q = new QQuery();
		Class<? extends Object> classToParse = toParse.getClass();
		configureAlias(q, classToParse);

		List<QProjection> allProjections = new ArrayList<>();
		for (Field f : classToParse.getDeclaredFields()) {
			configureProjectionAndSelections(toParse, q, f);
			allProjections.add(new QProjection(getColumnName(f), f.getName()));
			allAlias2Cols.put(f.getName(), getColumnName(f));
		}
		
		this.joinMaps = mapAlias2Joins(allAlias2Cols);

		configureProjections(toParse, q, allProjections);

		configureSortedColumns(toParse, allAlias2Cols, q);
		
		configureJoins(q);

		return q;
	}

	private void configureJoins(QQuery q) {
		Set<String> usedJoins = new HashSet<>();
		
		List<String> alias = this.joinMaps.keySet().stream()
				.sorted(Comparator.comparingLong(String::length).reversed())
				.collect(Collectors.toList());
		
		q.getProjections().forEach(p -> {
			String match = alias.stream().filter(a -> p.getColumn().contains(a)).findFirst().orElse(null);
			if (match != null) {
				String joinAlias = this.joinMaps.get(match);
				String newName = p.getColumn().replace(match, joinAlias);
				p.setColumn(newName);
				p.hasJoinAlias(true);
				usedJoins.add(match);
			}
		});
		
		q.getSelections().forEach(p -> {
			String match = alias.stream().filter(a -> p.getColumn().contains(a)).findFirst().orElse(null);
			if (match != null) {
				String joinAlias = this.joinMaps.get(match);
				String newName = p.getColumn().replace(match, joinAlias);
				p.setColumn(newName);
				p.hasJoinAlias(true);
				usedJoins.add(match);
			}
		});
		
		usedJoins.forEach(table -> {
			q.addJoin(new QJoin(table, this.joinMaps.get(table)));
		});
		
		System.out.println(usedJoins);
		
	}

	private Map<String, String> mapAlias2Joins(Map<String, String> allAlias2Cols) {
		Map<String, String> joinAlias = new HashMap<>();
		
		allAlias2Cols.forEach((alias, column) -> {
			if (column.contains(".")) {
				int dotIndex = column.indexOf(".");
				while (dotIndex > 0) {
					String columnName = column.substring(0, dotIndex);
					joinAlias.putIfAbsent(columnName, columnName.replaceAll("\\.", ""));
					dotIndex = column.indexOf(".", dotIndex+1);
				}
			}
		});
		
		return joinAlias;
	}

	private void configureSortedColumns(QBase toParse, Map<String, String> columnAlias, QQuery q) {
		if (toParse instanceof QSortableQuery) {
			List<String> sortedAlias = ((QSortableQuery) toParse).getSortedColumns();
			sortedAlias = mapAlias2ActualNames(columnAlias, sortedAlias);
			
			List<QSort> sortedCols = this.mapToActualColumns(sortedAlias);
			q.addSortColumns(sortedCols);
		}
	}

	private List<String> mapAlias2ActualNames(Map<String, String> columnAlias, List<String> colsToParse) {
		colsToParse = colsToParse.stream().map(s -> {
			String ac = columnAlias.get(s.replace("!", ""));
//			String ac = columnAlias.stream()
//					.map(p -> p.getAlias()).filter(p -> p.equals(s.replace("!", ""))).findFirst().get();
			return (s.startsWith("!") ? "!" : "") + ac;
		}).collect(Collectors.toList());
		return colsToParse;
	}
	
	public List<QSort> mapToActualColumns(List<String> sortedColumns) {
		return sortedColumns.stream().map(column -> {
			String order = "ASC";
			if (column.startsWith("!")) {
				order = "DESC";
				column = column.substring(1);
			}
			return new QSort(column, order);
		}).collect(Collectors.toList());
	}

	private void configureProjections(QBase toParse, QQuery q, List<QProjection> projections) {
		if (toParse.getColumns().isEmpty()) {
			projections.forEach(p -> q.addProjection(p));
		} else {
			projections.stream().filter(p -> {
				return toParse.getColumns().contains(p.getAlias());
			}).forEach(p -> q.addProjection(p));
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
