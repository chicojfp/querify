package io.breezil.queryfier.engine;

public class QSelection {
    private String column;
    private String value;
    private String operation;
    
    public String getColumn() {
        return this.column;
    }
    
    public void setColumn(String column) {
        this.column = column;
    }
    
    public String getOperation() {
        return this.operation;
    }
    
    public void setOperation(String operation) {
        this.operation = operation;
    }
    
}
