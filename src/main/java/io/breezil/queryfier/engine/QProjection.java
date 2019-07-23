package io.breezil.queryfier.engine;

public class QProjection extends QSection {
	private GrouppingType grouping;
	
	public QProjection(String item, String alias, GrouppingType grouping) {
		super(item, alias);
		this.grouping = grouping;
	}
	
	public QProjection(String item, String alias) {
		super(item, alias);
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
		return String.format(" %s%s AS %s", parentAlias, this.item, this.alias);
	}

	public GrouppingType getGrouping() {
		return grouping;
	}

	public void setGrouping(GrouppingType grouping) {
		this.grouping = grouping;
	}

}
