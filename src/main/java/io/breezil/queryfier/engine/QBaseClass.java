package io.breezil.queryfier.engine;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import io.breezil.queryfier.patch.JSonPatchOp;

public class QBaseClass<E, D> implements QBase<E, D>, QSortableQuery, QDataController<E, D> {
    private final Set<String> columns;
    private final List<String> sortedColumns;
    private boolean distinct;
    
    public QBaseClass() {
        this.columns = new HashSet<>();
        this.sortedColumns = new ArrayList<>();
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
    
    
    public void setColumns(List<String> columns) {
    	this.columns.clear();
    	this.columns.addAll(columns);
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
    public void addSortedColumns(String... columnName) {
		for (String name : columnName) {
			this.sortedColumns.add(name);
		}
	}

	public boolean isDistinct() {
		return distinct;
	}

	public void setDistinct(boolean distinct) {
		this.distinct = distinct;
	}
	
	public void postUpdateEntity(E entity) {

	}

	public void postPersistEntity(E entity) {

	}

	public E preUpdateEntity(E entity) {
		return entity;
	}
	
	public E prePersistEntity(E entity) {
		return entity;
	}

	public E preFilledEntity(E entity) {
		return entity;
	}
	
	public E preDeleteEntity(E entity) {
		return entity;
	}

	public List<JSonPatchOp> preUpdatePatch(List<JSonPatchOp> ops) {
		return ops;
	}

}
