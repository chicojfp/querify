package io.breezil.queryfier.engine;

public class QProjection {
	private String name;
	private String alias;
	
	public QProjection(String name, String alias) {
		this.name = name;
		this.alias = alias;
	}
	
	public String getName() {
		return this.name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getAlias() {
		return this.alias;
	}
	public void setAlias(String alias) {
		this.alias = alias;
	}
	
	@Override
    public String toString() {
        return toString("");
    }
    
    public String toString(String parentAlias) {
        parentAlias = configureAlias(parentAlias);
        return String.format(" %s%s AS %s", parentAlias, this.name, this.alias);
    }

    private String configureAlias(String parentAlias) {
        if (parentAlias == null) {
            parentAlias = "";
        }
        if (parentAlias.trim().length() > 0) {
            parentAlias += ".";
        }
        return parentAlias;
    }

}
