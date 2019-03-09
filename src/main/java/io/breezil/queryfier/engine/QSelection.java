package io.breezil.queryfier.engine;

public class QSelection extends QSection {
    private final String comparator;
    
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
        return String.format(" AND %s%s %s :%s", parentAlias, getItem(), getComparator(), getAlias());
    }

}
