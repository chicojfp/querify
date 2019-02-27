package io.breezil.queryfier.engine;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public class QBaseClass implements QBase, QSortableQuery {
    private final Set<String> columns;
    private final Set<String> sortedColumns;
    
    public QBaseClass() {
        this.columns = new HashSet<>();
        this.sortedColumns = new HashSet<>();
    }
    
    public void addColumns(String... columnNames) {
         for (String cName : columnNames) {
        	 this.columns.add(cName);
         }
    }
    
    @Override
    public Set<String> getColumns() {
		return this.columns;
	}
    
    @Override
    public void addColumn(String columnNames) {
        this.columns.add(columnNames);
    }

	public Iterator<String> getSortedColumns() {
		return sortedColumns.iterator();
	}
	
	public void removeSortedColumn(String columnName) {
		String found = this.sortedColumns.stream().filter(p -> {
			return p.contains(columnName) || columnName.contains(p);
		}).findFirst().get();
		this.sortedColumns.remove(found);
	}
	
	public void addSortedColumn(String columnName) {
		this.sortedColumns.add(columnName);
	}
    
}
