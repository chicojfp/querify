package io.breezil.queryfier.checker;

import org.junit.Test;

import io.breezil.queryfier.engine.StateFilter;
import io.breezil.queryfier.engine.StateFilterError;

public class QBaseCheckerTest {
	
	@Test(expected = CheckException.class)
	public void deveValidarExistenciaDoCampos() throws CheckException {
		StateFilter sf = new StateFilter();
		sf.addColumn("countryyyy");
		
		QBaseChecker checker = new QBaseChecker();
		try {
			checker.check(sf);
		} catch (Exception e) {
			System.err.println(e.getMessage());
			throw e;
		}
	}
	
	@Test
	public void deveListasComoSendoEncapsuladoresDeEntidades() throws CheckException {
		StateFilter sf = new StateFilter();
		sf.addColumn("city");
		
		QBaseChecker checker = new QBaseChecker();
		checker.check(sf);
	}
	
	@Test(expected = CheckException.class)
	public void deveVerificarOsTiposDosFiltrosEDosAtributosDasEntidades() throws CheckException {
		StateFilterError sf = new StateFilterError();
		sf.addColumn("name");
		
		QBaseChecker checker = new QBaseChecker();
		checker.check(sf);
	}

}




















