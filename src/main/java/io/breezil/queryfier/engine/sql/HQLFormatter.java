package io.breezil.queryfier.engine.sql;

import java.util.stream.Collectors;

import io.breezil.queryfier.engine.QQuery;

public class HQLFormatter {
	private String query;
	
	public HQLFormatter(QQuery query) {
		StringBuilder b = new StringBuilder();
        b.append(mapProjections(query));
        b.append("\n");
        b.append(getFrom(query));
        b.append(getJoins(query));
        b.append("\n");
        b.append("WHERE 1=1 ");
        b.append("\n");
        b.append(mapSelections(query));
        b.append("\n");
        b.append(createOrderBy(query));

        this.query = b.toString();
	}
	
	private String getJoins(QQuery query) {
		return query.getJoins().stream()
	        	.map(p -> p.toString(query.getAlias()))
	        	.collect(Collectors.joining("  \n"));
	}

	public String toHql() {
        return query;
    }
	
	public String mapProjections(QQuery query) {
        return getSelectStatement(query) + query.getProjections().stream()
        	.map(p -> p.toString(query.getAlias()))
        	.collect(Collectors.joining(",\n"));
    }

    private String getSelectStatement(QQuery query) {
        String select = "";
        if (!query.getProjections().isEmpty()) {
            select = "SELECT \n";
        }
        return select;
    }
    
    public String mapSelections(QQuery query) {
        return query.getSelections().stream()
        		.map(p -> p.toString(query.getAlias()))
        		.collect(Collectors.joining("\n"));
    }
    
    public String getFrom(QQuery query) {
        StringBuilder b = new StringBuilder();
        b.append(" FROM ");
        b.append(query.getEntity().getName());
        b.append(" AS ");
        b.append(query.getAlias());
        
        return b.toString();
    }
    
    public String createOrderBy (QQuery query) {
    	StringBuilder b = new StringBuilder();
    	if (!query.getSortColumns().isEmpty()) {
			b.append(" ORDER BY ");
			b.append(query.getSortColumns().stream()
				.map(sort -> sort.getName() + " " + sort.getOrder())
				.collect(Collectors.joining(", "))
			);
		}
    	return b.toString();
    }
    
    @Override
    public String toString() {
    	return this.query;
    }

}
