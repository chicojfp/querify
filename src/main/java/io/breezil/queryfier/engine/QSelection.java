package io.breezil.queryfier.engine;

import io.breezil.queryfier.engine.enums.CompType;

public class QSelection extends QSection {
    private final CompType comparator;
    
    public QSelection() {
    	super(null, null);
        this.comparator = CompType.EQUALS;
    }
    
    public QSelection(String item) {
        this();
        this.item = item;
    }
    
    public QSelection(String item, String alias, CompType comparator) {
    	super(item, alias);
        this.comparator = comparator;
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
        return String.format(" AND %s%s "+getComparator(), parentAlias, getItem(), ":"+getAlias());
    }

}
