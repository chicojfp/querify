package io.breezil.queryfier.engine;

public class QProjection {
	private String column;
	private String alias;
	private boolean hasJoinAlias;
	
	public QProjection(String column, String alias) {
		this.column = column;
		this.alias = alias;
	}
	
	public String getColumn() {
		return this.column;
	}
	public void setColumn(String column) {
		this.column = column;
	}
	public String getAlias() {
		return this.alias;
	}
	public void setAlias(String alias) {
		this.alias = alias;
	}
	
	@Override
    public String toString() {
        return toString("");
    }
    
    public String toString(String parentAlias) {
        parentAlias = configureAlias(parentAlias);
        return String.format(" %s%s AS %s", parentAlias, this.column, this.alias);
    }

    private String configureAlias(String parentAlias) {
        if (parentAlias == null || this.hasJoinAlias) {
            parentAlias = "";
        }
        if (parentAlias.trim().length() > 0) {
            parentAlias += ".";
        }
        return parentAlias;
    }

	public void hasJoinAlias(boolean hasJoinAlias) {
		this.hasJoinAlias = hasJoinAlias;
	}

}
