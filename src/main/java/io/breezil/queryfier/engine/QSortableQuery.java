package io.breezil.queryfier.engine;

import java.util.Iterator;

public interface QSortableQuery {
	
	public Iterator<String> getSortedColumns();
	
	public void removeSortedColumn(String columnName);
	
	public void addSortedColumn(String columnName);

}
