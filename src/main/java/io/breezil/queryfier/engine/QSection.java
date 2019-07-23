package io.breezil.queryfier.engine;

public class QSection {
	protected String item;
	protected String alias;
	private boolean hasJoinAlias;
	
	
	public QSection(String item, String alias) {
		this.item = item;
		this.alias = alias;
	}
	
	public String getAlias() {
		return alias;
	}
	public void setAlias(String alias) {
		this.alias = alias;
	}

	public String getItem() {
		return item;
	}

	public void setItem(String item) {
		this.item = item;
	}
	
	public String toString(String parentAlias) {
        return "";
    }
    
    protected String configureAlias(String parentAlias) {
        if (parentAlias == null || this.hasJoinAlias) {
            parentAlias = "";
        }
        if (parentAlias.trim().length() > 0) {
            parentAlias += ".";
        }
        return parentAlias;
    }
    
	public void hasJoinAlias(boolean hasJoinAlias) {
		this.hasJoinAlias = hasJoinAlias;
	}
	
}
