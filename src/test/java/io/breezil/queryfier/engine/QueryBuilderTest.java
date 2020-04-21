package io.breezil.queryfier.engine;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import io.breezil.queryfier.engine.QQuery;
import io.breezil.queryfier.engine.StateFilterTest;

public class QueryBuilderTest {
	private StateFilterTest sf;
	private int NUMBER_OF_TOTAL_PROJECTIONS = 5;
	private int NUMBER_OF_QUERIED_PROJECTIONS = 1;
	private int ONE_SORTED_COLUNM = 1;
	private int TWO_SORTED_COLUNM = 2;
	private String ASC_SORTING = "ASC";
	private String DESC_SORTING = "DESC";
	
	private String COUNTRY_COLUMN_ALIAS = "country";
	private String COUNTRY_COLUMN_FULL_NAME = "country.name";
	private String NAME_COLUMN_ALIAS = "name";
	private String NAME_COLUMN_FULL_NAME = "name";
	private String STATE_FIELD_NAME = "name";
	private String BRASIL = "Brasil";
	
	private int NO_PROJECTIONS = 0;
	private int ONE_PROJECTION = 1;
	private int TWO_PROJECTION = 2;
	
	
	
	@Before
	public void configura() {
		sf = new StateFilterTest();
		sf.setCountry(BRASIL);
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
		sf.addSortedColumns("governor");
		QQuery q = convertDTO2Query(sf);
		Assert.assertEquals("A consulta deveria retornar apenas 1 projeção.",
				ONE_SORTED_COLUNM, q.getSortColumns().size());
		
		Assert.assertEquals("A ordenação não está ordenando ascendente por padrão.",
				ASC_SORTING, q.getSortColumns().get(0).getOrder());
	}
	
	@Test
	public void deveOrdenarDescendentemente() {
		sf.addSortedColumns("!name");
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
				"main", q.getSortColumns().get(0).getItem());
		Assert.assertEquals("A ordenação não está ordenando ascendente por padrão.",
				"governor", q.getSortColumns().get(1).getItem());
	}

	private QQuery addSortedColumns() {
		sf.addSortedColumns("!main");
		sf.addSortedColumns("governor");
		QQuery q = convertDTO2Query(sf);
		Assert.assertEquals("A consulta deveria retornar duas projeções.",
				TWO_SORTED_COLUNM, q.getSortColumns().size());
		return q;
	}
	
	@Test
	public void deveFiltrarPorColunasNaoNulas() {
		StateFilterTest sf = new StateFilterTest();
		QQuery q = convertDTO2Query(sf);
		
		Assert.assertEquals("Não deve haver projeções com valores nulos.",
				NO_PROJECTIONS, q.getSelections().size());
		
		sf.setCountry(BRASIL);
		q = convertDTO2Query(sf);
		Assert.assertEquals("Deve haver uma projeção para o campo não nulo",
				ONE_PROJECTION, q.getSelections().size());
		Assert.assertEquals("Deve haver uma projeção para o campo não nulo",
				COUNTRY_COLUMN_ALIAS, q.getSelections().get(0).getAlias());
		Assert.assertEquals("Deve haver um mapeamento para o nome real da coluna",
				COUNTRY_COLUMN_FULL_NAME, q.getSelections().get(0).getItem());
		
		sf.setName("PE");
		q = convertDTO2Query(sf);
		Assert.assertEquals("Deve haver uma projeção para o campo não nulo",
				TWO_PROJECTION, q.getSelections().size());
		Assert.assertEquals("Deve haver uma projeção para o campo não nulo",
				NAME_COLUMN_ALIAS, q.getSelections().get(0).getAlias());
		Assert.assertEquals("Deve haver uma projeção para o campo não nulo",
				NAME_COLUMN_FULL_NAME, q.getSelections().get(0).getItem());
	}
	
	@Test
	public void deveSuportarConsultasComDistinctEmColunaEspecifica() {
		StateFilterTest sf = new StateFilterTest();
		sf.setCountry(BRASIL);
		sf.addColumn(STATE_FIELD_NAME);
		sf.setDistinct(true);
		
		QQuery q = convertDTO2Query(sf);
		
		System.out.println(q.toDTOQuery());
		
		Assert.assertEquals("Deve haver apenas um agrupamento conforme definido",
				ONE_PROJECTION, q.getGroups().size());
	}
	
	
	@Test
	public void deveAgruparDemaisColunasSemAgregacao() {
		StateFilterTest sf = new StateFilterTest();
		sf.setCountry(BRASIL);
		sf.addColumns("count@main", "sum@population");
		sf.addColumns(COUNTRY_COLUMN_ALIAS, STATE_FIELD_NAME);
		
		QQuery q = convertDTO2Query(sf);
		
		System.out.println(q.toDTOQuery());
		
		Assert.assertEquals("Deve haver apenas um agrupamento conforme definido",
				TWO_PROJECTION, q.getGroups().size());
	}
	
	@Test
	public void deveAgruparColunasSemAgregacao() {
		StateFilterTest sf = new StateFilterTest();
		sf.setCountry(BRASIL);
		sf.addColumn("count@name");
		sf.addColumns(STATE_FIELD_NAME);
		
		QQuery q = convertDTO2Query(sf);
		
		System.out.println(q.toDTOQuery());
		
		Assert.assertEquals("Deve haver apenas um agrupamento conforme definido",
				ONE_PROJECTION, q.getGroups().size());
	}

	
	@Test
	public void deveSuportarConsultasComDistinct() {
		StateFilterTest sf = new StateFilterTest();
		sf.setCountry(BRASIL);
		sf.addColumns(STATE_FIELD_NAME, "main");
		sf.setDistinct(true);
		
		QQuery q = convertDTO2Query(sf);
		
		Assert.assertEquals("Deve haver apenas dois agrupamentos conforme definido",
				TWO_PROJECTION, q.getGroups().size());
	}
	
	@Test
	public void testarAParada() {
		StateFilterTest sf = new StateFilterTest();
		sf.setCountry(BRASIL);
		sf.setGovernor("Manoel Bandeira");
		sf.setMain("Louca da Espanha");
		sf.setName("Passárgada");
        
        sf.addColumn(COUNTRY_COLUMN_ALIAS);
        sf.addColumn("main");
        
        sf.addSortedColumns(COUNTRY_COLUMN_ALIAS);
        sf.addSortedColumns("!main");
        
		QQuery q = convertDTO2Query(sf);
		System.out.println(q);
	}

	private QQuery convertDTO2Query(StateFilterTest sf) {
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
		CityFilterTest sf = new CityFilterTest();
		sf.setCountry(BRASIL);
		sf.setState("PE");
        sf.setMajor("Lima Barreto");
        sf.setName("Bruzudanga");
        
        
        sf.addSortedColumns("major");
        sf.addSortedColumns("!name");
        
		
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
