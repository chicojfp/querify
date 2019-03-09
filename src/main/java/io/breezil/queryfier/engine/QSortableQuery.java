package io.breezil.queryfier.engine;

import java.util.List;

public interface QSortableQuery {
	
	public List<String> getSortedColumns();
	
	public void removeSortedColumn(String columnName);
	
	public void addSortedColumns(String... columnName);

}
