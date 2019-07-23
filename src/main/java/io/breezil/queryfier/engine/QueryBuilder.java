package io.breezil.queryfier.engine;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import io.breezil.queryfier.engine.annotations.QEntity;
import io.breezil.queryfier.engine.annotations.QField;
import io.breezil.queryfier.engine.annotations.QFieldQuery;
import io.breezil.queryfier.engine.enums.CompType;
import io.breezil.queryfier.engine.helper.ReflectionHelper;

public class QueryBuilder {

	private static final String DESC = "DESC";
	private static final String ASC = "ASC";
	private static final String NOT = "!";
	private Map<String, QJoin> joinMaps;

	public QueryBuilder() {
		this.joinMaps = new HashMap<>();
	}

	public QQuery parseQuery(QBase<?, ?> toParse) throws IllegalAccessException {
		if (toParse == null) {
			return null;
		}
		Map<String, QField> allAlias2Cols = new HashMap<>();

		QQuery q = new QQuery();
		Class<? extends QBase> classToParse = toParse.getClass();
		configureAlias(q, classToParse);
		
//		buildProjections(toParse);

		List<QProjection> allProjections = new ArrayList<>();
		for (Field field : classToParse.getDeclaredFields()) {
			QField qField = ReflectionHelper.getQField(field);
			if (!qField.ignore()) {
				configureSelectionAndParameter(toParse, q, field);
				allProjections.add(new QProjection(qField.name(), field.getName()));
				allAlias2Cols.put(field.getName(), qField);
			}
		}

		this.joinMaps = mapAlias2Joins(allAlias2Cols);

		configureProjections(toParse, q, allProjections);

		configureSortedColumns(toParse, allAlias2Cols, q);

		configureJoins(q);

		return q;
	}

	

	private void buildProjections(Class<? extends QBase> classToParse) {
		Arrays.asList(classToParse.getDeclaredFields()).forEach(field -> {
			QField qField = ReflectionHelper.getQField(field);
//			QProjection p = new QProjection();
		});
		
	}

	private void configureJoins(QQuery q) {
		Set<String> usedJoins = new HashSet<>();

		List<String> alias = this.joinMaps.keySet().stream().sorted(Comparator.comparingLong(String::length).reversed())
				.collect(Collectors.toList());

		q.getProjections().stream().map(s -> (QSection) s).forEach(p -> {
			usedJoins.addAll(remapJoinedAlias(alias, p));
		});

		q.getSelections().stream().map(s -> (QSection) s).forEach(s -> {
			usedJoins.addAll(remapJoinedAlias(alias, s));
		});

		q.getSortColumns().stream().map(s -> (QSection) s).forEach(s -> {
			usedJoins.addAll(remapJoinedAlias(alias, s));
		});

		usedJoins.forEach(table -> {
			q.addJoin(this.joinMaps.get(table));
		});

		System.out.println(usedJoins);

	}

	private Set<String> remapJoinedAlias(List<String> alias, QSection s) {
		Set<String> usedJoins = new HashSet<>();
		alias.stream().filter(a -> s.getItem().contains(a)).forEach(match -> {
			QJoin joinAlias = this.joinMaps.get(match);
			String newName = s.getItem().replace(match, joinAlias.getAlias());
			s.setItem(newName);
			s.hasJoinAlias(true);
			usedJoins.add(match);
		});

		return usedJoins;
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
					join = new QJoin(tableName, tableName.replaceAll("\\.", ""), table.join(), join != null);
					joinAlias.putIfAbsent(tableName, join);
					dotIndex = column.indexOf(".", dotIndex + 1);
				}
			}
		});

		return joinAlias;
	}

	private void configureSortedColumns(QBase toParse, Map<String, QField> columnAlias, QQuery q) {
		if (toParse instanceof QSortableQuery) {
			List<String> sortedAlias = ((QSortableQuery) toParse).getSortedColumns();
			sortedAlias = mapAlias2ActualNames(columnAlias, sortedAlias);

			List<QSort> sortedCols = mapToActualColumns(sortedAlias);
			q.addSortColumns(sortedCols);
		}
	}

	private List<String> mapAlias2ActualNames(Map<String, QField> columnAlias, List<String> colsToParse) {
		colsToParse = colsToParse.stream().map(s -> {
			String ac = columnAlias.get(s.replace(NOT, "")).name();
			return (s.startsWith(NOT) ? NOT : "") + ac;
		}).collect(Collectors.toList());
		return colsToParse;
	}

	public List<QSort> mapToActualColumns(List<String> sortedColumns) {
		return sortedColumns.stream().map(column -> {
			String order = ASC;
			if (column.startsWith(NOT)) {
				order = DESC;
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
		if (isValidValue(fieldValue) && !hasGroupingColumn(toParse, f)) {
			QSelection selection = createSelection(toParse, q, f);
			q.addSelection(selection);
			selection.addParameters(q);
		}
	}
	
	private boolean hasGroupingColumn(QBase toParse, Field f) {
		String columnName = getColumnName(f);
		return ((QBaseClass)toParse).hasGroupedColumns(columnName);
	}

	private boolean isValidValue(Object fieldValue) {
		return (fieldValue != null) && isNonEmptyList(fieldValue);
	}

	private boolean isNonEmptyList(Object fieldValue) {
		return (!(fieldValue instanceof Collection<?>))
				|| ((fieldValue instanceof Collection<?>) && !((Collection<?>) fieldValue).isEmpty());
	}

	private QSelection createSelection(QBase toParse, QQuery q, Field f) throws IllegalAccessException {
		String columnName = getColumnName(f);
		QQuery query = createSubquery(toParse, f);
		return new QSelection(columnName, f.getName(), getComparator(f), f.get(toParse), query);
	}

	private QQuery createSubquery(QBase toParse, Field f) throws IllegalAccessException {
		QFieldQuery q = f.getAnnotation(QFieldQuery.class);
		if (q != null) {
			return new QueryBuilder().parseQuery((QBase) f.get(toParse));
		}
		return null;
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

	private CompType getComparator(Field f) {
		QField q = f.getAnnotation(QField.class);
		if (q == null) {
			return CompType.EQUALS;
		}
		return q.comparator();
	}

	private String getAlias(QEntity n) {
		return n.alias().equals("") ? n.name().getName() : n.alias();
	}

}
