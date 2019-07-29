package io.breezil.queryfier.engine;

import io.breezil.queryfier.engine.enums.CompType;
import io.breezil.queryfier.engine.sql.HQLFormatter;

public class QSelection extends QSection {
	private final CompType comparator;
	private QQuery innerQuery;
	private Object value;

	public QSelection() {
		super(null, null);
		this.comparator = CompType.EQUALS;
	}

	public QSelection(String item) {
		this();
		this.item = item;
	}

	public QSelection(String item, String alias, CompType comparator, Object value, QQuery innerQuery) {
		super(item, alias);
		this.comparator = comparator;
		this.innerQuery = innerQuery;
		this.value = value;
	}

	public CompType getComparator() {
		return this.comparator;
	}

	@Override
	public String toString() {
		return toString("");
	}

	public String toString(String parentAlias) {
		parentAlias = configureAlias(parentAlias);
		String paramOrExpression = computeFilterOrExpression();
		return String.format(" AND %s%s " + getComparator(), parentAlias, getItem(), paramOrExpression);
	}

	private String computeFilterOrExpression() {
		if (this.innerQuery != null) {
			return new HQLFormatter(this.innerQuery).toHql();
		}

		return ":" + getAlias();
	}

	public void addParameters(QQuery q) {
		if (this.innerQuery != null) {
			((QQuery) this.innerQuery).getParameters().forEach((nome, valor) -> {
				q.addParameter(nome, valor);
			});
		} else {
			q.addParameter(alias, value);
		}
	}

}
