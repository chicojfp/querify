package io.breezil.queryfier.engine;

public class QJoin {
	String table;
	String alias;

	public QJoin(String table, String alias) {
		this.table = table;
		this.alias = alias;
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
        return String.format(" INNER JOIN %s%s AS %s", parentAlias, this.table, this.alias);
    }

    private String configureAlias(String parentAlias) {
        if (parentAlias == null) {
            parentAlias = "";
        }
        if (parentAlias.trim().length() > 0) {
            parentAlias += ".";
        }
        return parentAlias;
    }
}
