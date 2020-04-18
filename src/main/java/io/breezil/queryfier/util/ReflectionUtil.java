package io.breezil.queryfier.util;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import io.breezil.queryfier.engine.QBaseClass;

public class ReflectionUtil {

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
			entityConst = filtro.recuperarTipoEntidade().getConstructor();
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
