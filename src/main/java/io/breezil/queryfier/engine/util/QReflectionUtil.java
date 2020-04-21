package io.breezil.queryfier.engine.util;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

import io.breezil.queryfier.engine.QBaseClass;
import io.breezil.queryfier.engine.annotations.QField;
import io.breezil.queryfier.engine.enums.CompType;
import io.breezil.queryfier.engine.enums.JoinType;

public class QReflectionUtil {
	
	public static Map<String, String> mapField2QFieldName(Class<? extends Object> object) {
		Map<String, String> fieldNames = new HashMap<>();
		Stream.of(object.getDeclaredFields()).
				forEach(field -> fieldNames.put(field.getName(), getQField(field).name()));
				//map(field -> getQField(field)).collect(Collectors.toList());
		return fieldNames;
	}
	
	
	public static Map<String, Object> mapField2Value(Object instance) {
		Map<String, Object> fieldValues = new HashMap<>();
		Stream.of(instance.getClass().getDeclaredFields()).
				forEach(field -> {
					Object value = getDeclaredValue(instance, field);
					if (value != null) {
						fieldValues.put(field.getName(), value);
					}
				});
				//map(field -> getQField(field)).collect(Collectors.toList());
		return fieldValues;
	}
	
	public static boolean isNonEmptyList(Object fieldValue) {
		return (!(fieldValue instanceof Collection<?>))
				|| ((fieldValue instanceof Collection<?>) && !((Collection<?>) fieldValue).isEmpty());
	}
	
	private static Object getDeclaredValue(Object instance, Field field) {
		try {
			field.setAccessible(true);
			return field.get(instance);
		} catch (IllegalArgumentException | IllegalAccessException e) {
			e.printStackTrace();
		}
		return null;
	}


	private static QField getQField(Field f) {
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
	
	public static <T> Method getSetterMethod(T entidade, String from) {
		String methodName = "set" + from.substring(0, 1).toUpperCase() + from.split("\\.")[0].substring(1);
		Method method = null;
		for (Method m : entidade.getClass().getMethods()) {
			if (m.getName().equals(methodName)) {
				method = m;
				break;
			}
		}
		return method;
	}
	
	public static <T> Class<? extends Object> getPropertyType(T entity, String jsonPatchFrom) {
		return getSetterValueType(getSetterMethod(entity, jsonPatchFrom));
	}
	
	public static Class<? extends Object> getSetterValueType(Method method) {
		Class<? extends Object> clazz =  method.getParameterTypes()[0];
		return clazz;
	}
	
	public static void callSetterWithParameter(Object entidade, String methodName, Object value) {
		Method method = getSetterMethod(entidade, methodName);
		callMethodWithParameter(entidade, method, value);
	}

	public static <T> void callMethodWithParameter(T entidade, Method method, Object value) {
		try {
			method.invoke(entidade, value);
		} catch (SecurityException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
			e.printStackTrace();
		}
	}

	public static <D, T> T createNewInstanceFromEntity(QBaseClass<T, D> filtro) {
		T entity = null;
		try {
			Constructor<T> entityConst;
			entityConst = filtro.getSourceType().getConstructor();
			entity = entityConst.newInstance();
		} catch (NoSuchMethodException | SecurityException | InstantiationException | IllegalAccessException
				| IllegalArgumentException | InvocationTargetException e) {
		}
		return entity;
	}
	
	public static Object createNewInstance(Class<? extends Object> clazz) {
		try {
			return clazz.newInstance();
		} catch (SecurityException | InstantiationException | IllegalAccessException
				| IllegalArgumentException e) {
		}
		return null;
	}

}
