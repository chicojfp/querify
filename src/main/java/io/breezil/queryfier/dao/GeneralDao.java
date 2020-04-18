package io.breezil.queryfier.dao;

import java.util.List;

import io.breezil.queryfier.engine.QBaseClass;

public interface GeneralDao {
    
    public <E, T> List<T> searchDTOs(QBaseClass<E, T> filter);

    public <T, E> List<E> searchEntities(QBaseClass<E, T> filter);
    
    public <T, E> E searchEntity(QBaseClass<E, T> filter);
    
    public <T, E> T searchDTO(QBaseClass<E, T> filter);

    public <T> T getReference(Class<? extends T> clazz, Object primaryKey);

	public <T> void update(T entity);

	public <T> void persistOrUpdate(T entity);

	public <T> void persist(T entity);

	public <T> void delete(T entity);
    
	public <T> void merge(T entity);
}
