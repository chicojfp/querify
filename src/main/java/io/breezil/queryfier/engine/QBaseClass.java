package io.breezil.queryfier.engine;

import java.util.HashSet;
import java.util.Set;

public class QBaseClass implements QBase {
    private final Set<String> columns;
    
    public QBaseClass() {
        this.columns = new HashSet<>();
    }
    
    public void addColumns(String columnNames) {
        // for (String cName : columnNames) {
        this.columns.add(columnNames);
        // }
    }
    
    @Override
    public Set<String> getColumns() {
		return this.columns;
	}
    
    @Override
    public void addColumn(String columnNames) {
        this.columns.add(columnNames);
    }
    
}
