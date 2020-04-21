package io.breezil.queryfier.engine;

import java.util.List;

import io.breezil.queryfier.patch.JSonPatchOp;

public interface QDataController<E, D> {
	
	public void postUpdateEntity(E entity);

	public void postPersistEntity(E entity);

	public E preUpdateEntity(E entity);
	
	public E prePersistEntity(E entity);

	public E preFilledEntity(E entity);
	
	public E preDeleteEntity(E entity);

	public List<JSonPatchOp> preUpdatePatch(List<JSonPatchOp> ops);

}
