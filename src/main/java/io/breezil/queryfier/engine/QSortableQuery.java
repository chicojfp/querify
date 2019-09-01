package io.breezil.queryfier.engine;

import java.util.List;

/**
 * Enable ability to query sorting by any columns defined in filter ( {@link QBase} subclasses ).
 * 
 * Use:
 * * COLUMN_NAME as string for ascending sort order;
 * * !COLUMN_NAME as string for descending sort order.
 * 
 * 
 * @author chicojfp
 *
 */
public interface QSortableQuery {
	
	public List<String> getSortedColumns();
	
	public void removeSortedColumn(String columnName);
	
	public void addSortedColumns(String... columnName);

}
