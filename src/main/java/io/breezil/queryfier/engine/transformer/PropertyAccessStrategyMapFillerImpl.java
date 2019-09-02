/*
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package io.breezil.queryfier.engine.transformer;

import org.hibernate.mapping.Map;
import org.hibernate.property.access.spi.PropertyAccess;
import org.hibernate.property.access.spi.PropertyAccessStrategy;

import io.breezil.queryfier.engine.QStatisticsMap;

public class PropertyAccessStrategyMapFillerImpl implements PropertyAccessStrategy {
	/**
	 * Singleton access
	 */
	public static final PropertyAccessStrategyMapFillerImpl INSTANCE = new PropertyAccessStrategyMapFillerImpl();

	@Override
	public PropertyAccess buildPropertyAccess(Class containerJavaType, String propertyName) {
		
		// Sometimes containerJavaType is null, but if it isn't, make sure it's a Map.
		if (containerJavaType != null && !QStatisticsMap.class.isAssignableFrom(containerJavaType)) {
			throw new IllegalArgumentException(
				String.format(
					"Expecting class: [%1$s], but containerJavaType is of type: [%2$s] for propertyName: [%3$s]",
					QStatisticsMap.class.getName(),
					containerJavaType.getName(),
					propertyName
				)
			);
		}

		return new PropertyAccessMapFillerImpl( this, propertyName );
	}
}
