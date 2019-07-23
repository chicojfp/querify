package io.breezil.queryfier.engine;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class QBaseClass<E, D> implements QBase<E, D>, QSortableQuery {
    private final Set<String> columns;
    private final List<String> sortedColumns;
    private final List<QProjection> groupedColumns;
    
    public QBaseClass() {
        this.columns = new HashSet<>();
        this.sortedColumns = new ArrayList<>();
        this.groupedColumns = new ArrayList<>();
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

	@Override
    public List<String> getSortedColumns() {
		return this.sortedColumns;
	}
	
	@Override
    public void removeSortedColumn(String columnName) {
		String found = this.sortedColumns.stream().filter(p -> {
			return p.contains(columnName) || columnName.contains(p);
		}).findFirst().get();
		this.sortedColumns.remove(found);
	}
	
	@Override
    public void addSortedColumns(String... columnNames) {
		for (String name : columnNames) {
			this.sortedColumns.add(name);
		}
	}
	
    public boolean hasGroupedColumns(String name) {
		return this.groupedColumns.stream().anyMatch(p -> p.item.equals(name));
	}
    
    public void addGroupedColumns(String... columnNames) {
    	for (String name : columnNames) {
    		if (name.contains("!")) {
    			String[] groupInfo = name.split("!");
    			GrouppingType grouping = GrouppingType.criar(groupInfo[0]);
    			String item = groupInfo[1];
    			String alias = name.replace("!", "_");
    			
    			if (grouping != null) {
    				this.groupedColumns.add(new QProjection(item, alias, grouping));
    			}
    		}
    	}
    }
    
}
