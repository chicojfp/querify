package io.breezil.queryfier.engine;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class QueryBuilderTest {
	private StateFilter sf;
	private int NUMBER_OF_TOTAL_PROJECTIONS = 4;
	private int NUMBER_OF_QUERIED_PROJECTIONS = 1;
	private int ONE_SORTED_COLUNM = 1;
	private int TWO_SORTED_COLUNM = 2;
	private String ASC_SORTING = "ASC";
	private String DESC_SORTING = "DESC";
	
	private String COUNTRY_COLUMN_ALIAS = "country";
	private String COUNTRY_COLUMN_FULL_NAME = "country.name";
	private int TWO_PROJECTION = 2;
	private String NAME_COLUMN_ALIAS = "name";
	private String NAME_COLUMN_FULL_NAME = "name";
	
	private int NO_PROJECTIONS = 0;
	private int ONE_PROJECTION = 1;
	
	
	@Before
	public void configura() {
		sf = new StateFilter();
		sf.setCountry("Brasil");
		sf.setGovernor("Manoel Bandeira");
		sf.setMain("Louca da Espanha");
		sf.setName("Passárgada");
	}
	
	@Test
	public void recuperaTodasAsColunasSeNenhumaForInformada() {
		QQuery q = convertDTO2Query(sf);
		Assert.assertEquals("A consulta deveria não retornou todos os elementos.",
				NUMBER_OF_TOTAL_PROJECTIONS, q.getProjections().size());
	}
	
	@Test
	public void recuperaApenasColunasInformadas() {
		sf.addColumn("main");
		QQuery q = convertDTO2Query(sf);
		Assert.assertEquals("A consulta deveria retornar apenas 1 projeção.",
				NUMBER_OF_QUERIED_PROJECTIONS, q.getProjections().size());
	}
	
	@Test
	public void deveOrdenarAscendentemente() {
		sf.addSortedColumn("governor");
		QQuery q = convertDTO2Query(sf);
		Assert.assertEquals("A consulta deveria retornar apenas 1 projeção.",
				ONE_SORTED_COLUNM, q.getSortColumns().size());
		
		Assert.assertEquals("A ordenação não está ordenando ascendente por padrão.",
				ASC_SORTING, q.getSortColumns().get(0).getOrder());
	}
	
	@Test
	public void deveOrdenarDescendentemente() {
		sf.addSortedColumn("!name");
		QQuery q = convertDTO2Query(sf);
		Assert.assertEquals("A consulta deveria retornar apenas 1 projeção.",
				ONE_SORTED_COLUNM, q.getSortColumns().size());
		
		Assert.assertEquals("A ordenação não está excendente descendentemente quando usa o padrão !nome_coluna.",
				DESC_SORTING, q.getSortColumns().get(0).getOrder());
	}
	
	@Test
	public void deveOrdernarPorMaisDeUmCampo() {
		QQuery q = addSortedColumns();
		
		Assert.assertEquals("A ordenação não está ordenando descendentemente quando usa o padrão !nome_coluna.",
				DESC_SORTING, q.getSortColumns().get(0).getOrder());
		Assert.assertEquals("A ordenação não está ordenando ascendente por padrão.",
				ASC_SORTING, q.getSortColumns().get(1).getOrder());
	}
	
	@Test
	public void deveManterAPrecedenciaDeOrdenacao() {
		QQuery q = addSortedColumns();
		
		Assert.assertEquals("A ordenação não está ordenando descendentemente quando usa o padrão !nome_coluna.",
				"main", q.getSortColumns().get(0).getName());
		Assert.assertEquals("A ordenação não está ordenando ascendente por padrão.",
				"governor", q.getSortColumns().get(1).getName());
	}

	private QQuery addSortedColumns() {
		sf.addSortedColumn("!main");
		sf.addSortedColumn("governor");
		QQuery q = convertDTO2Query(sf);
		Assert.assertEquals("A consulta deveria retornar duas projeções.",
				TWO_SORTED_COLUNM, q.getSortColumns().size());
		return q;
	}
	
	@Test
	public void deveFiltrarPorColunasNaoNulas() {
		StateFilter sf = new StateFilter();
		QQuery q = convertDTO2Query(sf);
		
		Assert.assertEquals("Não deve haver projeções com valores nulos.",
				NO_PROJECTIONS, q.getSelections().size());
		
		sf.setCountry("Brasil");
		q = convertDTO2Query(sf);
		Assert.assertEquals("Deve haver uma projeção para o campo não nulo",
				ONE_PROJECTION, q.getSelections().size());
		Assert.assertEquals("Deve haver uma projeção para o campo não nulo",
				COUNTRY_COLUMN_ALIAS, q.getSelections().get(0).getAlias());
		Assert.assertEquals("Deve haver um mapeamento para o nome real da coluna",
				COUNTRY_COLUMN_FULL_NAME, q.getSelections().get(0).getColumn());
		
		sf.setName("PE");
		q = convertDTO2Query(sf);
		Assert.assertEquals("Deve haver uma projeção para o campo não nulo",
				TWO_PROJECTION, q.getSelections().size());
		Assert.assertEquals("Deve haver uma projeção para o campo não nulo",
				NAME_COLUMN_ALIAS, q.getSelections().get(0).getAlias());
		Assert.assertEquals("Deve haver uma projeção para o campo não nulo",
				NAME_COLUMN_FULL_NAME, q.getSelections().get(0).getColumn());
	} 
	
	@Test
	public void testarAParada() {
		StateFilter sf = new StateFilter();
		sf.setCountry("Brasil");
		sf.setGovernor("Manoel Bandeira");
		sf.setMain("Louca da Espanha");
		sf.setName("Passárgada");
        
        sf.addColumn("country");
        sf.addColumn("main");
        
        sf.addSortedColumn("country");
        sf.addSortedColumn("!main");
        
		QQuery q = convertDTO2Query(sf);
		System.out.println(q);
	}

	private QQuery convertDTO2Query(StateFilter sf) {
		QQuery q = null;
		try {
			q = new QueryBuilder().parseQuery(sf);
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
		return q;
	}
	
	@Test
	public void consultarEntidadesPais() {
		CityFilter sf = new CityFilter();
		sf.setCountry("Brasil");
		sf.setState("PE");
        sf.setMajor("Lima Barreto");
        sf.setName("Bruzudanga");
        
        
        sf.addSortedColumn("major");
        sf.addSortedColumn("!name");
        
		
		QQuery q = null;
		try {
			q = new QueryBuilder().parseQuery(sf);
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println(q);
	}

}
