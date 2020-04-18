package io.breezil.queryfier.service;

import java.util.List;

import io.breezil.queryfier.dao.GeneralDao;
import io.breezil.queryfier.engine.QBaseClass;
import io.breezil.queryfier.patch.JSonPatchOp;
import io.breezil.queryfier.util.PatchJsonHelper;
import io.breezil.queryfier.util.ReflectionUtil;

public class GeneralService<T> {

	public GeneralDao getDao() {
		return null;
	}
	
	public PatchJsonHelper getPatchParser() {
		return null;
	}

	public <D> List<D> searchDTOs(QBaseClass<T, D> filtro) {
		return this.getDao().searchDTOs(filtro);
	}

	public <D> List<T> searchEntities(QBaseClass<T, D> filtro) {
		return this.getDao().searchEntities(filtro);
	}

	public <D> D updateModel(QBaseClass<T, D> filtro, List<JSonPatchOp> ops) {
		ops = this.getPatchParser().mapFilterFields2ActualEntityFields(filtro, ops);
		ops = preUpdatePatch(ops);
		T entity = this.getDao().searchEntity(filtro);
		entity = preFilledEntity(entity);
		entity = this.getPatchParser().applyPatch(entity, ops);
		entity = preUpdateEntity(entity);
		this.getDao().update(entity);
		postUpdateEntity(entity);
		return this.getDao().searchDTO(filtro);
	}

	public <D> D updateFullModel(QBaseClass<T, D> filtro, D dados) {
		List<JSonPatchOp> ops = this.getPatchParser().convertFieldValues2PatchJsonOp(dados);
		ops = preUpdatePatch(ops);
		T entity = this.getDao().searchEntity(filtro);
		entity = preFilledEntity(entity);
		udpateEntityFields(filtro, ops, entity);
		entity = preUpdateEntity(entity);
		this.getDao().persistOrUpdate(entity);
		postUpdateEntity(entity);
		return this.getDao().searchDTO(filtro);
	}

	public <D> D createModel(QBaseClass<T, D> filter, D data) {
		List<JSonPatchOp> ops = this.getPatchParser().convertFieldValues2PatchJsonOp(data);
		ops = preUpdatePatch(ops);
		T entity = ReflectionUtil.createNewInstanceFromEntity(filter);
		entity = preFilledEntity(entity);
		udpateEntityFields(filter, ops, entity);
		entity = prePersistEntity(entity);
		this.getDao().persist(entity);
		postPersistEntity(entity);
		return this.getDao().searchDTO(filter);
	}

	public <D> void deleteModel(QBaseClass<T, D> filtro) {
		T entity = this.getDao().searchEntity(filtro);
		entity = preDeleteEntity(entity);
		this.getDao().delete(entity);
	}

	public <D> void udpateEntityFields(QBaseClass<T, D> filtro, List<JSonPatchOp> ops, T entity) {
		List<JSonPatchOp> mapped = this.getPatchParser().mapFilterFields2ActualEntityFields(filtro, ops);
		entity = this.getPatchParser().applyPatch(entity, mapped);
	}
	
	public void postUpdateEntity(T entity) {

	}

	public void postPersistEntity(T entity) {

	}

	public T preUpdateEntity(T entity) {
		return entity;
	}
	
	public T prePersistEntity(T entity) {
		return entity;
	}

	public T preFilledEntity(T entity) {
		return entity;
	}
	
	public T preDeleteEntity(T entity) {
		return entity;
	}

	public List<JSonPatchOp> preUpdatePatch(List<JSonPatchOp> ops) {
		return ops;
	}
}
