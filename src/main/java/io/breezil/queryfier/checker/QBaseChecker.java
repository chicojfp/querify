package io.breezil.queryfier.checker;

import java.lang.reflect.Field;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import io.breezil.queryfier.engine.QBaseClass;
import io.breezil.queryfier.engine.annotations.QField;
import io.breezil.queryfier.engine.helper.ReflectionHelper;

public class QBaseChecker {
	private QBaseClass<? extends Object, ? extends Object> obj;
	
	public boolean check(QBaseClass<? extends Object, ? extends Object> obj) throws CheckException {
		this.obj = obj;
		List<Field> fields = ReflectionHelper.getDeclaredFields(obj.getClass());
		for (Field field : fields) {
			this.checkColumn(field);
		}
		
		Set<String> declaredColumn = fields.stream().map(Field::getName).collect(Collectors.toSet());

		for (String c : this.obj.getColumns()) {
			if (!declaredColumn.contains(c)) {
				throw new CheckException(CheckErrorType.INVALID_TYPE, String.format("A coluna '%s' não existe no objeto '%s'", c, this.obj.getClass()));
			}
		}
		
		return true;
	}

	private void checkColumn(Field fieldFilter) throws CheckException {
		Field field = extractField(this.obj.getClass(), fieldFilter);
		reportNotExistingColumn(field, fieldFilter.getName(), this.obj.getClass());
		
		Class<? extends Object> tipoDTO = this.obj.recuperarTipoDTO();
		Field fieldDto = extractField(tipoDTO, fieldFilter);
		reportNotExistingColumn(fieldDto, fieldFilter.getName(), tipoDTO);
		
		checkEntitiesColumns(field, fieldDto);
		
		
	}

	private Class<? extends Object> checkEntitiesColumns(Field field, Field fieldDto) throws CheckException {
		QField qfield = ReflectionHelper.getQField(field);
		String targetFieldName = qfield.name();
		targetFieldName = targetFieldName == null ? field.getName() : targetFieldName;
		
		String[] columnNames = targetFieldName.split("\\.");
		
		Class<? extends Object> entityType = this.obj.recuperarTipoEntidade();
		Field entityField = null;
		for (String cName : columnNames) { 
			entityField = extractField(entityType, cName);
			reportNotExistingColumn(entityField, cName, entityType);
			entityType = unwrapContainerEntity(entityField, entityField.getType());
//			entityType = unwrapContainerEntity();
		}
		
		if (!entityType.isAssignableFrom(fieldDto.getType())) {
			throw new CheckException(CheckErrorType.INVALID_COLUMN, String.format("O tipo da coluna '%s' (%s) não é equivalente ao tipo '%s'", targetFieldName, entityField, fieldDto));
		}
		
		return entityType;
		
	}

	private Class<? extends Object> unwrapContainerEntity(Field entityField, Class<?> fieldType) {
		if (Collection.class.isAssignableFrom(entityField.getType())) {
			return ReflectionHelper.getGenericDeclaredType(entityField);
		}
		return fieldType;
	}

	private void reportNotExistingColumn(Field field, String cName, Class<? extends Object> objToCheck) throws CheckException {
		if (field == null) {
			throw new CheckException(CheckErrorType.INVALID_COLUMN, String.format("A coluna '%s' informada não existe no objeto '%s'", cName, objToCheck.getName()));
		}
	}

	private Field extractField(Class<? extends Object> type, Field field) {
		return this.extractField(type, field.getName());
	}
	
	private Field extractField(Class<? extends Object> type, String cName) {
		Field field = null;
		try {
			field = type.getDeclaredField(cName);
		} catch (NoSuchFieldException | SecurityException e) {
		}
		return field;
	}

}
