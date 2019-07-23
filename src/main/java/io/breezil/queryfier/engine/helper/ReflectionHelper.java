package io.breezil.queryfier.engine.helper;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import io.breezil.queryfier.engine.annotations.QField;
import io.breezil.queryfier.engine.enums.CompType;
import io.breezil.queryfier.engine.enums.JoinType;

public class ReflectionHelper {
	
	public static QField getQField(Class<? extends Object> classToParse, String fieldName) throws NoSuchFieldException, SecurityException {
		Field field = classToParse.getDeclaredField(fieldName);
		return getQField(field);
	}
	
	public static Class<? extends Object> getGenericDeclaredType(Field fieldToCheck) {
		ParameterizedType stringListType = (ParameterizedType) fieldToCheck.getGenericType();
        return (Class<?>) stringListType.getActualTypeArguments()[0];
	}
	
	public static List<Field> getDeclaredFields(Class<? extends Object> clazz) {
		return Arrays.asList(clazz.getDeclaredFields());
	}
	
	public static QField getQField(Field f) {
		QField q = f.getAnnotation(QField.class);
		if (q == null) {
			q = new QField() {

				@Override
				public Class<? extends Annotation> annotationType() {
					return null;
				}

				@Override
				public String valueWrapper() {
					return null;
				}

				@Override
				public String name() {
					return f.getName();
				}

				@Override
				public JoinType join() {
					return JoinType.INNER_JOIN;
				}

				@Override
				public boolean ignore() {
					return false;
				}

				@Override
				public CompType comparator() {
					return CompType.EQUALS;
				}
			};
		}
		return q;

	}

}
