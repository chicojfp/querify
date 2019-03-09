package io.breezil.queryfier.engine;

import java.lang.annotation.Annotation;
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
import io.breezil.queryfier.engine.enums.JoinType;

public class QueryBuilder {

	private Map<String, QJoin> joinMaps;
	
	public QueryBuilder() {
		this.joinMaps = new HashMap<>();
	}

	public QQuery parseQuery(QBase toParse) throws IllegalAccessException {
		if (toParse == null) {
			return null;
		}
		Map<String, QField> allAlias2Cols = new HashMap<>();

		QQuery q = new QQuery();
		Class<? extends Object> classToParse = toParse.getClass();
		configureAlias(q, classToParse);

		List<QProjection> allProjections = new ArrayList<>();
		for (Field field : classToParse.getDeclaredFields()) {
			QField qField = getQField(field);
			configureSelectionAndParameter(toParse, q, field);
			allProjections.add(new QProjection(qField.name(), field.getName()));
			allAlias2Cols.put(field.getName(), qField);
		}
		
		this.joinMaps = mapAlias2Joins(allAlias2Cols);

		configureProjections(toParse, q, allProjections);

		configureSortedColumns(toParse, allAlias2Cols, q);
		
		configureJoins(q);

		return q;
	}

	private QField getQField(Field f) {
		QField q = f.getAnnotation(QField.class);
		if (q == null) {
			q = new QField() {
				
				@Override
				public Class<? extends Annotation> annotationType() {
					return null;
				}
				
				@Override
				public String valueWrapper() {
					return null;
				}
				
				@Override
				public String name() {
					return f.getName();
				}
				
				@Override
				public JoinType join() {
					return JoinType.INNER_JOIN;
				}
				
				@Override
				public String comparator() {
					return "=";
				}
			};
		}
		return q;
		
	}

	private void configureJoins(QQuery q) {
		Set<String> usedJoins = new HashSet<>();
		
		List<String> alias = this.joinMaps.keySet().stream()
				.sorted(Comparator.comparingLong(String::length).reversed())
				.collect(Collectors.toList());
		
		q.getProjections().forEach(p -> {
			String match = alias.stream()
					.filter(a -> p.getColumn().contains(a))
					.findFirst().orElse(null);
			
			if (match != null) {
				QJoin joinAlias = this.joinMaps.get(match);
				String newName = p.getColumn().replace(match, joinAlias.getAlias());
				p.setColumn(newName);
				p.hasJoinAlias(true);
				usedJoins.add(match);
			}
		});
		
		q.getSelections().forEach(s -> {
			String match = alias.stream()
					.filter(a -> s.getColumn().contains(a))
					.findFirst().orElse(null);
			
			if (match != null) {
				QJoin joinAlias = this.joinMaps.get(match);
				String newName = s.getColumn().replace(match, joinAlias.getAlias());
				s.setColumn(newName);
				s.hasJoinAlias(true);
				usedJoins.add(match);
			}
		});
		
		q.getSortColumns().forEach(s -> {
			String match = alias.stream()
					.filter(a -> s.getName().contains(a))
					.findFirst().orElse(null);
			
			if (match != null) {
				QJoin joinAlias = this.joinMaps.get(match);
				String newName = s.getName().replace(match, joinAlias.getAlias());
				s.setName(newName);
				s.hasJoinAlias(true);
				usedJoins.add(match);
			}
		});
		
		usedJoins.forEach(table -> {
			q.addJoin(this.joinMaps.get(table));
		});
		
		System.out.println(usedJoins);
		
	}

	private Map<String, QJoin> mapAlias2Joins(Map<String, QField> allAlias2Cols) {
		Map<String, QJoin> joinAlias = new HashMap<>();
		
		allAlias2Cols.forEach((alias, table) -> {
			String column = table.name();
			if (column.contains(".")) {
				int dotIndex = column.indexOf(".");
				QJoin join = null;
				while (dotIndex > 0) {
					String tableName = column.substring(0, dotIndex);
					join = new QJoin(tableName, tableName.replaceAll("\\.", ""), table.join(), join == null);
					joinAlias.putIfAbsent(tableName, join);
					dotIndex = column.indexOf(".", dotIndex+1);
				}
			}
		});
		
		return joinAlias;
	}

	private void configureSortedColumns(QBase toParse, Map<String, QField> columnAlias, QQuery q) {
		if (toParse instanceof QSortableQuery) {
			List<String> sortedAlias = ((QSortableQuery) toParse).getSortedColumns();
			sortedAlias = mapAlias2ActualNames(columnAlias, sortedAlias);
			
			List<QSort> sortedCols = this.mapToActualColumns(sortedAlias);
			q.addSortColumns(sortedCols);
		}
	}

	private List<String> mapAlias2ActualNames(Map<String, QField> columnAlias, List<String> colsToParse) {
		colsToParse = colsToParse.stream().map(s -> {
			String ac = columnAlias.get(s.replace("!", "")).name();
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

	private void configureSelectionAndParameter(QBase toParse, QQuery q, Field f) throws IllegalAccessException {
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
