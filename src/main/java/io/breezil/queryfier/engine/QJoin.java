package io.breezil.queryfier.engine;

import io.breezil.queryfier.engine.annotations.QField;
import io.breezil.queryfier.engine.enums.JoinType;

public class QJoin {
	String table;
	String alias;
	JoinType joinType;
	private boolean needParentAlias;

	public QJoin(String table, String alias) {
		this.table = table;
		this.alias = alias;
		this.joinType = JoinType.INNER_JOIN;
	}
	
	public QJoin(String table, String alias, JoinType joinType, boolean needParentAlias) {
		this(table, alias);
		this.joinType = joinType;
		this.needParentAlias = needParentAlias;
	}
	
	public QJoin(QField qField, String alias) {
		this.table = qField.name();
		this.alias = alias;
		this.joinType = qField.join();
	}

	public String getTable() {
		return table;
	}
	public void setTable(String table) {
		this.table = table;
	}
	public String getAlias() {
		return alias;
	}
	public void setAlias(String alias) {
		this.alias = alias;
	}

	public String toString(String alias) {
		String parentAlias = configureAlias(alias);
        return String.format(" %s %s%s AS %s", this.joinType.toString(), parentAlias, this.table, this.alias);
    }

    private String configureAlias(String parentAlias) {
        if (parentAlias == null || !needParentAlias) {
            parentAlias = "";
        }
        if (parentAlias.trim().length() > 0) {
            parentAlias += ".";
        }
        return parentAlias;
    }
}
