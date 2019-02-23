package io.breezil.queryfier.engine;

import org.junit.Before;
import org.junit.Test;

import io.breezil.queryfier.dto.StateFilter;
import io.breezil.queryfier.entities.City;
import io.breezil.queryfier.entities.Country;
import io.breezil.queryfier.entities.State;

public class QueryBuilderTest {
	
	@Before
	public void configura() {
		Country c = new Country();
		State s = new State();
		City t = new City();
		s.setMain(t);
		t.setName("Recife");
		s.setName("PE");
		t = new City();
		t.setName("Surubim");
		
	}
	
	@Test
	public void testarAParada() {
		StateFilter sf = new StateFilter();
		sf.setCountry("Brasil");
		sf.setGovernor("Paulo Câmara");
		sf.setMain("Recife");
		sf.setName("PE");
		
		QQuery q = null;
		try {
			q = new QueryBuilder().parseQuery(sf);
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println(q);
	}

}
