package io.breezil.queryfier.engine;

import java.util.Set;

public interface QBase {
    public Set<String> getColumns();
    
    public void addColumn(String columnName);

}
