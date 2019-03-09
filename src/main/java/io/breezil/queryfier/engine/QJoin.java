package io.breezil.queryfier.engine;

import io.breezil.queryfier.engine.annotations.QField;
import io.breezil.queryfier.engine.enums.JoinType;

public class QJoin extends QSection {
	JoinType joinType;

	public QJoin(String item, String alias) {
		super(item, alias);
		this.joinType = JoinType.INNER_JOIN;
	}
	
	public QJoin(String table, String alias, JoinType joinType, boolean needParentAlias) {
		this(table, alias);
		this.joinType = joinType;
		this.hasJoinAlias(needParentAlias);
	}
	
	public QJoin(QField qField, String alias) {
		super(qField.name(), alias);
		this.joinType = qField.join();
	}


	public String toString(String alias) {
		String parentAlias = configureAlias(alias);
        return String.format(" %s %s%s AS %s", this.joinType.toString(), parentAlias, this.item, this.alias);
    }
}
