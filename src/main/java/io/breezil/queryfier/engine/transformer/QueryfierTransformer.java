package io.breezil.queryfier.engine.transformer;

import java.util.Arrays;

import org.hibernate.HibernateException;
import org.hibernate.property.access.internal.PropertyAccessStrategyBasicImpl;
import org.hibernate.property.access.internal.PropertyAccessStrategyChainedImpl;
import org.hibernate.property.access.internal.PropertyAccessStrategyFieldImpl;
import org.hibernate.property.access.spi.Setter;
import org.hibernate.transform.AliasToBeanResultTransformer;

public class QueryfierTransformer extends AliasToBeanResultTransformer {
	
	private static final long serialVersionUID = -2083508832052748644L;
	
	private final Class resultClass;
	private boolean isInitialized;
	private String[] aliases;
	private Setter[] setters;
	
	public QueryfierTransformer(Class resultClass) {
		super(resultClass);
		this.resultClass = resultClass;
	}

	@Override
	public Object transformTuple(Object[] tuple, String[] aliases) {
		Object result;

		try {
			if ( ! isInitialized ) {
				initialize( aliases );
			}
			else {
				check( aliases );
			}
			
			result = resultClass.newInstance();

			for ( int i = 0; i < aliases.length; i++ ) {
				if ( setters[i] != null ) {
					setters[i].set( result, tuple[i], null );
				}
			}
		}
		catch ( InstantiationException e ) {
			throw new HibernateException( "Could not instantiate resultclass: " + resultClass.getName() );
		}
		catch ( IllegalAccessException e ) {
			throw new HibernateException( "Could not instantiate resultclass: " + resultClass.getName() );
		}

		return result;
	}
	
	private void initialize(String[] aliases) {
		PropertyAccessStrategyChainedImpl propertyAccessStrategy = new PropertyAccessStrategyChainedImpl(
				PropertyAccessStrategyBasicImpl.INSTANCE,
				PropertyAccessStrategyFieldImpl.INSTANCE,
				PropertyAccessStrategyMapFillerImpl.INSTANCE
		);
		this.aliases = new String[ aliases.length ];
		setters = new Setter[ aliases.length ];
		for ( int i = 0; i < aliases.length; i++ ) {
			String alias = aliases[ i ];
			if ( alias != null ) {
				this.aliases[ i ] = alias;
				setters[ i ] = propertyAccessStrategy.buildPropertyAccess( resultClass, alias ).getSetter();
			}
		}
		isInitialized = true;
	}

	private void check(String[] aliases) {
		if ( ! Arrays.equals( aliases, this.aliases ) ) {
			throw new IllegalStateException(
					"aliases are different from what is cached; aliases=" + Arrays.asList( aliases ) +
							" cached=" + Arrays.asList( this.aliases ) );
		}
	}

}
