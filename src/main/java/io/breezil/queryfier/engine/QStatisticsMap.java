package io.breezil.queryfier.engine;

import java.io.Serializable;
import java.util.Map;

import io.breezil.queryfier.engine.enums.AggregationFunctionEnum;
import io.breezil.queryfier.engine.transformer.QueryfierTransformer;

/**
 * Enable the use of Hibernate aggregation functions when user combined with {@link QueryfierTransformer} transformer.
 * All aggregations will be placed in a Map retrieved by {@link QStatisticsMap#getStats()}.
 * 
 * Use:
 * * Create a class based on class {@link QBase};
 * * Add a aggregated column using the pattern: <pre>AGG_FUNCTION@COLUMN_NAME</pre> {@code base.addColumn("count@name");}
 * * The Builder will generate the resulting JPQl
 * 
 * 
 * The types of enabled aggregation functions is defined in class {@link AggregationFunctionEnum}
 * 
 * @author chicojfp
 *
 */
public interface QStatisticsMap extends Serializable {
	
	public Map<String, Object> getStats();
	
}
