package io.breezil.queryfier.serializer;

import java.time.LocalDate;

public interface ISerializer {
	
	default public <T> T serialize(Class<T> clazz, String value) {
		if (clazz.isAssignableFrom(LocalDate.class)) {
			return (T) LocalDate.parse(value);
		}
		return (T) value;
	}

}
