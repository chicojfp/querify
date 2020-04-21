package io.breezil.queryfier.service;

import java.util.List;

import io.breezil.queryfier.dao.GeneralDao;
import io.breezil.queryfier.engine.QBaseClass;
import io.breezil.queryfier.engine.util.QReflectionUtil;
import io.breezil.queryfier.patch.JSonPatchOp;
import io.breezil.queryfier.patch.PatchJsonHelper;

public class GeneralService<T> {

	public GeneralDao getDao() {
		return null;
	}
	
	public PatchJsonHelper getPatchParser() {
		return null;
	}

	public <D> List<D> searchDTOs(QBaseClass<T, D> filter) {
		return this.getDao().searchDTOs(filter);
	}

	public <D> List<T> searchEntities(QBaseClass<T, D> filter) {
		return this.getDao().searchEntities(filter);
	}

	public <D> D updateModel(QBaseClass<T, D> filter, List<JSonPatchOp> ops) {
		ops = this.getPatchParser().mapFilterFields2ActualEntityFields(filter, ops);
		ops = filter.preUpdatePatch(ops);
		T entity = this.getDao().searchEntity(filter);
		entity = filter.preFilledEntity(entity);
		entity = this.getPatchParser().applyPatch(entity, ops);
		entity = filter.preUpdateEntity(entity);
		this.getDao().update(entity);
		filter.postUpdateEntity(entity);
		return this.getDao().searchDTO(filter);
	}

	public <D> D updateFullModel(QBaseClass<T, D> filter, D data) {
		List<JSonPatchOp> ops = this.getPatchParser().convertFieldValues2PatchJsonOp(data);
		ops = filter.preUpdatePatch(ops);
		T entity = this.getDao().searchEntity(filter);
		entity = filter.preFilledEntity(entity);
		udpateEntityFields(filter, ops, entity);
		entity = filter.preUpdateEntity(entity);
		this.getDao().persistOrUpdate(entity);
		filter.postUpdateEntity(entity);
		return this.getDao().searchDTO(filter);
	}

	public <D> D createModel(QBaseClass<T, D> filter, D data) {
		List<JSonPatchOp> ops = this.getPatchParser().convertFieldValues2PatchJsonOp(data);
		ops = filter.preUpdatePatch(ops);
		T entity = QReflectionUtil.createNewInstanceFromEntity(filter);
		entity = filter.preFilledEntity(entity);
		udpateEntityFields(filter, ops, entity);
		entity = filter.prePersistEntity(entity);
		this.getDao().persist(entity);
		filter.postPersistEntity(entity);
		return this.getDao().searchDTO(filter);
	}

	public <D> void deleteModel(QBaseClass<T, D> filter) {
		T entity = this.getDao().searchEntity(filter);
		entity = filter.preDeleteEntity(entity);
		this.getDao().delete(entity);
	}

	public <D> void udpateEntityFields(QBaseClass<T, D> filtro, List<JSonPatchOp> ops, T entity) {
		List<JSonPatchOp> mapped = this.getPatchParser().mapFilterFields2ActualEntityFields(filtro, ops);
		entity = this.getPatchParser().applyPatch(entity, mapped);
	}
	
}
