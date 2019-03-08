package io.breezil.queryfier.engine;

public class QSelection {
    private String column;
    private String alias;
    private final String comparator;
	private boolean hasJoinAlias;
    
    public QSelection() {
        this.comparator = " = ";
    }
    
    public QSelection(String column) {
        this();
        this.column = column;
    }
    
    public QSelection(String column, String alias) {
        this();
        this.column = column;
        this.alias = alias;
    }
    
    public QSelection(String column, String alias, String comparator) {
        this.column = column;
        this.alias = alias;
        this.comparator = comparator;
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
    
    public String getComparator() {
        return this.comparator;
    }
    
    @Override
    public String toString() {
        return toString("");
    }
    
    public String toString(String parentAlias) {
        parentAlias = configureAlias(parentAlias);
        return String.format(" AND %s%s %s :%s", parentAlias, getColumn(), getComparator(), getAlias());
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
