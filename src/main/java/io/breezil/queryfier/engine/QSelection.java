package io.breezil.queryfier.engine;

public class QSelection extends QSection {
    private final String comparator;
	private boolean list;
    
    public QSelection() {
    	super(null, null);
        this.comparator = " = ";
    }
    
    public QSelection(String item) {
        this();
        this.item = item;
    }
    
    public QSelection(String item, String alias, String comparator) {
    	super(item, alias);
        this.comparator = comparator;
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
        String compExpression = this.getComparator();
        if (compExpression.contains("%s")) {
        	compExpression = String.format(compExpression, ":"+getAlias());
        } else {
        	compExpression = String.format(" %s :%s ", compExpression, getAlias());
        }
        return String.format(" AND %s%s %s", parentAlias, getItem(), compExpression);
    }

	public void setParameterList(boolean isList) {
		this.list = isList;
	}
	
	public boolean isParameterList() {
		return this.list;
	}

}
