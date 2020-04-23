package io.breezil.queryfier.patch;

import java.lang.reflect.Method;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.sun.xml.txw2.output.StreamSerializer;

import io.breezil.queryfier.dao.GeneralDao;
import io.breezil.queryfier.engine.QBaseClass;
import io.breezil.queryfier.engine.util.QReflectionUtil;
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

	private  <T> void applyPatchOp(T entity, JSonPatchOp opp) {
		String from = opp.getFrom().substring(1);
		Method method = QReflectionUtil.getSetterMethod(entity, from);
		// FIXME Não deveria precisar desta verificação, detectar a causa.
		if (method != null) {
			Object value = mapPatchValue2Type(opp, method);
			QReflectionUtil.callMethodWithParameter(entity, method, value);
		}
	}

	private  Object mapPatchValue2Type(JSonPatchOp opp, Method method) {
		if (isAddPatch(opp)) {
			return mapPatchValue2ActualType(opp, QReflectionUtil.getSetterValueType(method));
		}
		// FIXME: Operação "remove" as demais precisam ser implementadas
		return null;
	}

	private  boolean isAddPatch(JSonPatchOp opp) {
		return opp.getOp().contentEquals("add");
	}

	private Object mapPatchValue2ActualType(JSonPatchOp opp, Class<? extends Object> clazz) {
		String paramValue = opp.getPatch().toString();
		if (isFieldFromOtherEntity(opp)) {
			paramValue = buildJson(opp.getFrom().split("\\."), paramValue, 1);
		}
		return this.getDeserializer().deserialize(clazz, paramValue);
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

	public  <D, T> List<JSonPatchOp> mapFilterFields2ActualEntityFields(QBaseClass<T, D> filter,
			List<JSonPatchOp> ops) {
		Map<String, String> fieldMaps = QReflectionUtil.mapField2QFieldName(filter.getClass());
		return ops.stream().filter(op -> fieldMaps.get(op.getFrom().substring(1)) != null).map(op -> {
			return new JSonPatchOp(op.getOp(), JSON_PATCH_PREFIX + fieldMaps.get(op.getFrom().substring(1)),
					op.getPatch());
		}).collect(Collectors.toList());
	}

	private String buildJson(String[] split, String value, int i) {
		if (i < split.length) {
			String json = buildJson(split, value, i + 1);
			return String.format("{\"%s\": %s }", split[i], json);
		}
		return "\"" + value + "\"";
	}

}
