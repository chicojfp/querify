package io.breezil.queryfier.engine;

import java.util.ArrayList;
import java.util.List;

public class QBaseClass implements QBase {
	public List<String> columns;
	
	public QBaseClass() {
		this.columns = new ArrayList<String>();
	}

	public List<String> getColumns() {
		return this.columns;
	}

}
