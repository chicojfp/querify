package io.breezil.queryfier.util;

import java.lang.reflect.Method;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import io.breezil.queryfier.dao.GeneralDao;
import io.breezil.queryfier.engine.QBaseClass;
import io.breezil.queryfier.engine.util.QReflectionUtil;
import io.breezil.queryfier.patch.JSonPatchOp;
import io.breezil.queryfier.serializer.ISerializer;

public class PatchJsonHelper {
	private static final String PROPERTY_SPLITTER = ".";
	private static final String PROPERTY_SPLITTER_REGEX = "\\.";
	private static final String JSON_PATCH_PREFIX = "/";
	
	private GeneralDao dao;
	private ISerializer deserializer;

	public PatchJsonHelper(GeneralDao dao, ISerializer deserializer) {
		this.dao = dao;
		this.deserializer = deserializer;
	}
	
	public GeneralDao getDao() {
		return this.dao;
	}
	
	private ISerializer getDeserializer() {
		return this.deserializer;
	}

	public  <T> T applyPatch(T entity, List<JSonPatchOp> ops) {
		for (JSonPatchOp opp : ops) {
			applyPatchOp(entity, opp);
		}
		return entity;
	}

	private  <T> void applyPatchOp(T entidade, JSonPatchOp opp) {
		String from = opp.getFrom().substring(1);
		Method method = ReflectionUtil.getSetterMethod(entidade, from);
		// FIXME Não deveria precisar desta verificação, detectar a causa.
		if (method != null) {
			Object value = mapPatchValue2Type(opp, method);
			ReflectionUtil.callMethodWithParameter(entidade, method, value);
		}
	}

	private  Object mapPatchValue2Type(JSonPatchOp opp, Method method) {
		if (isAddPatch(opp)) {
			return mapPatchValue2ActualType(opp, ReflectionUtil.getSetterValueType(method));
		}
		// FIXME: Operação "remove" as demais precisam ser implementadas
		return null;
	}

	private  boolean isAddPatch(JSonPatchOp opp) {
		return opp.getOp().contentEquals("add");
	}

	private  Object mapPatchValue2ActualType(JSonPatchOp opp, Class<? extends Object> clazz) {
		Object valueToParse = opp.getPatch();
		if (clazz.isAssignableFrom(LocalDate.class)) {
			return this.getDeserializer().serialize(clazz, opp.getPatch().toString());
		} else if (!clazz.getPackage().getName().startsWith("java")) {
			Object value = ReflectionUtil.createNewInstance(clazz);
			return processEntityProperty(opp, value);
		}
		return valueToParse;
	}

	private  Object processEntityProperty(JSonPatchOp opp, Object value) {
		if (isFieldFromOtherEntity(opp)) {
			String propertyName = getEntityFieldName(opp);
			Method innerMethod = ReflectionUtil.getSetterMethod(value, propertyName);
			Object innerValue = mapPatchValue2ActualType(opp, ReflectionUtil.getSetterValueType(innerMethod));
			ReflectionUtil.callMethodWithParameter(value, innerMethod, innerValue);
			return this.getDao().getReference(value.getClass(), innerValue);
		}
		return value;
	}

	private  String getEntityFieldName(JSonPatchOp opp) {
		return opp.getFrom().split(PROPERTY_SPLITTER_REGEX)[1];
	}

	private  boolean isFieldFromOtherEntity(JSonPatchOp opp) {
		return opp.getFrom().contains(PROPERTY_SPLITTER);
	}

	public  <D> List<JSonPatchOp> convertFieldValues2PatchJsonOp(D instance) {
		Map<String, Object> values = QReflectionUtil.mapField2Value(instance);
		List<JSonPatchOp> ops = new ArrayList<JSonPatchOp>();
		values.forEach((propertyName, value2Patch) -> {
			ops.add(new JSonPatchOp(JSON_PATCH_PREFIX + propertyName, value2Patch));
		});
		return ops;
	}

	public  <D, T> List<JSonPatchOp> mapFilterFields2ActualEntityFields(QBaseClass<T, D> filtro,
			List<JSonPatchOp> ops) {
		Map<String, String> fieldMaps = QReflectionUtil.mapField2QFieldName(filtro.getClass());
		return ops.stream().filter(op -> fieldMaps.get(op.getFrom().substring(1)) != null).map(op -> {
			return new JSonPatchOp(op.getOp(), JSON_PATCH_PREFIX + fieldMaps.get(op.getFrom().substring(1)),
					op.getPatch());
		}).collect(Collectors.toList());
	}

}
