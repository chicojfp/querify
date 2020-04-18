package io.breezil.queryfier.engine;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.ArrayList;
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
import io.breezil.queryfier.engine.enums.JoinType;
import io.breezil.queryfier.engine.util.QReflectionUtil;

public class QueryBuilder {

	private static final String DESC = "DESC";
	private static final String ASC = "ASC";
	private static final String NOT = "!";
	private Map<String, QJoin> joinMaps;

	public QueryBuilder() {
		this.joinMaps = new HashMap<>();
	}

	@SuppressWarnings("rawtypes")
	public QQuery parseQuery(QBase<? extends Object, ? extends Object> toParse) throws IllegalAccessException {
		if (toParse == null) {
			return null;
		}
		QQuery q = new QQuery();
		Map<String, QField> allAlias2Cols = new HashMap<>();
		
		Class<? extends QBase> classToParse = toParse.getClass();
		configureAlias(q, classToParse, toParse);
		
		boolean groupBy = ((QBaseClass) toParse).isDistinct();
		q.setDistinct(groupBy);

		List<QProjection> allProjections = new ArrayList<>();
		for (Field field : classToParse.getDeclaredFields()) {
			QField qField = getQField(field);
			if (!qField.ignore()) {
				configureSelectionAndParameter(toParse, q, field);
				allProjections.add(new QProjection(qField.name(), field.getName()));
				allAlias2Cols.put(field.getName(), qField);
			}
		}

		this.joinMaps = mapAlias2Joins(allAlias2Cols);

		configureProjections(toParse, q, allProjections, allAlias2Cols);
		
		configureGrouping(q.getProjections(), q);

		configureSortedColumns(toParse, allAlias2Cols, q);

		configureJoins(q);

		return q;
	}

	private void configureGrouping(List<QProjection> projections, QQuery q) {
		boolean isDistinct = q.hasGroupping();
		if (isDistinct) {
			projections.stream().filter(p -> p.getGroupFunction() == null).forEach(p -> q.addGroup(p));
		}
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
				public boolean ignore() {
					return false;
				}

				@Override
				public CompType comparator() {
					return CompType.EQUALS;
				}
			};
		}
		return q;

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

//		System.out.println(usedJoins);

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

	private void configureSortedColumns(QBase<? extends Object, ? extends Object> toParse, Map<String, QField> columnAlias, QQuery q) {
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

	private void configureProjections(QBase<? extends Object, ? extends Object> toParse, QQuery q, List<QProjection> projections, Map<String, QField> allAlias2Cols) {
		if (toParse.getColumns().isEmpty()) {
			projections.forEach(p -> q.addProjection(p));
		} else {
			addColumnsWithProjections(toParse, q, projections);
			addColumnsWithAggregationFunctions(toParse, q, allAlias2Cols);
		}
	}

	private void addColumnsWithAggregationFunctions(QBase<? extends Object, ? extends Object> toParse, QQuery q, Map<String, QField> allAlias2Cols) {
		toParse.getColumns().stream()
			.filter(col -> col.contains(QProjection.SPLITTER))
			.forEach(col -> {
				q.addProjection(new QProjection(col, allAlias2Cols.get(col.split(QProjection.SPLITTER)[1]).name())); 
		});
	}

	private void addColumnsWithProjections(QBase<? extends Object, ? extends Object> toParse, QQuery q, List<QProjection> projections) {
		projections.stream().filter(p -> {
			return toParse.getColumns().contains(p.getAlias());
		}).forEach(p -> q.addProjection(p));
	}

	private void configureSelectionAndParameter(QBase<? extends Object, ? extends Object> toParse, QQuery q, Field f) throws IllegalAccessException {
		f.setAccessible(true);
		Object fieldValue = f.get(toParse);
		if ((fieldValue != null) && QReflectionUtil.isNonEmptyList(fieldValue)) {
			QSelection selection = createSelection(toParse, q, f);
			q.addSelection(selection);
			selection.addParameters(q);
		}
	}

	private QSelection createSelection(QBase<? extends Object, ? extends Object> toParse, QQuery q, Field f) throws IllegalAccessException {
		String columnName = getColumnName(f);
		QQuery query = createSubquery(toParse, f);
		return new QSelection(columnName, f.getName(), getComparator(f), f.get(toParse), query);
	}

	private QQuery createSubquery(QBase<? extends Object, ? extends Object> toParse, Field f) throws IllegalAccessException {
		QFieldQuery q = f.getAnnotation(QFieldQuery.class);
		if (q != null) {
			return new QueryBuilder().parseQuery((QBase) f.get(toParse));
		}
		return null;
	}

	private void configureAlias(QQuery q, Class<? extends QBase> classToParse, QBase<? extends Object, ? extends Object> toParse) {
		if (classToParse.isAnnotationPresent(QEntity.class)) {
			QEntity n = classToParse.getAnnotation(QEntity.class);
			q.setAlias(getAlias(n));
			q.setEntity(n.name());
		} else {
			q.setEntity(toParse.recuperarTipoEntidade());
			q.setAlias(toParse.recuperarTipoEntidade().getSimpleName().substring(0,1));
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
