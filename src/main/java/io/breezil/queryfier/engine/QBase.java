package io.breezil.queryfier.engine;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Set;

public interface QBase<E, D> {
    public Set<String> getColumns();
    
    public void addColumn(String columnName);
    
    default Class<E> recuperarTipoEntidade() {
        Type genericSuperclass = getClass().getGenericSuperclass();
        ParameterizedType parameterizedType = ParameterizedType.class.cast(genericSuperclass);
        return (Class<E>) parameterizedType.getActualTypeArguments()[0];
    }
    
    default Class<D> recuperarTipoDTO() {
        Type genericSuperclass = getClass().getGenericSuperclass();
        ParameterizedType parameterizedType = ParameterizedType.class.cast(genericSuperclass);
        return (Class<D>) parameterizedType.getActualTypeArguments()[1];
    }

}
