# Queryfier!

Este Framework visa simplificar a criação de consultas ao banco de dados por meio de uso de parâmetros simples anotados através de um DTO ou POJO.

Ele constrói consultas HQL com base em anotações em propriedades anotadas de classes Java. Assim, uma única classe mapeia um conjunto (universo) de possíveis filtros (seleções) e retornos (projeções) sobre determinada entidade.

Este Framework visa possibilitar a construção dinâmica de consultas que retornem quaisquer subconjunto de possíveis projeções e seleções realizando as junções necessárias para realizá-las.

## Exemplo prático

[Veja detalhes no projeto de exemplo](https://github.com/chicojfp/queryfier-sample)

Existem as classes:
City (Name, Major, State)
State (Name, Governor, Capital, Country)
Country (Name, Presidente, Capital)

É definida o DTO de filtro CityFilter anota como:
```java
@QEntity(name = City.class, alias = "c")
public class CityFilter extends QBaseClass {

	@QueryParam(value="name")
	String name;

	@QueryParam(value="major")
	@QField(name="major.name")
	String major;

	@QueryParam(value="country")
	@QField(name="state.country.name", join=JoinType.LEFT_JOIN)
	String country;

	@QueryParam(value="state")
	@QField(name="state.name")
	String state;

	///(...)
}
````

A parametrização abaixo:

```java
CityFilter filter = new CityFilter();
filter.addColumns("name");
filter.addSortedColumns("name", "!state", "!country");
new Dao().recuperarLista(filter);
```

Resulta na seguinte consulta (HQL/JPQL):

```sql
SELECT
c.name AS name
FROM io.breezil.queryfiersamples.entities.City AS c
RIGHT JOIN c.state AS state
RIGHT JOIN state.country AS statecountry
WHERE 1=1
ORDER BY c.name ASC,
state.name DESC,
statecountry.name DESC
```

Que resulta na consulta SQL seguinte:

```sql
SELECT
city0_.name AS col_0_0_
FROM
city city0_
LEFT OUTER JOIN state state1_ ON city0_.state_id = state1_.id
LEFT OUTER JOIN country country2_ ON state1_.country_id = country2_.id
WHERE 1 = 1
ORDER  BY
city0_.name ASC,
state1_.name DESC,
country2_.name DESC
```
Removendo-se a ordenação por nome de estado e país:

```java
CityFilter filter = new CityFilter();
filter.addColumns("name");
filter.addSortedColumns("name");
new Dao().recuperarLista(filter);
```

Não há necessidade de JOIN com tais tabelas (visto que nenhum dado dela é necessário).

```sql
SELECT city0_.name AS col_0_0_
, city0_.population as col_1_0_
FROM city city0_
WHERE 1 = 1
ORDER BY city0_.name ASC
```

## Como Funciona

O Framework define um conjunto de anotações que indicam qual entidades devem ser mapeadas na consulta à base de dados.
Estas anotações indicam:
- Qual a entidade principal da consulta [ ```@QEntity(name = City.class, alias = "c")``` ];
- Os campos destas entidades que serão filtrados recuperados [```@QField(name="state.country.name", join=JoinType.LEFT_JOIN)``` ].
- Os tipos de junções entre entidades (caso existam) que precisam ser analisadas;
- Por padrão, não é necessário qualquer anotação, será utilizada a propriedade homônima da entidade definida em ``@QEntity``.

Além disso, é definida uma classe básica que possui informações que aplicam restrições sobre o conjunto de dados, tais como:
- Restringir colunas que serão recuperadas [`` <? extends QBaseClass>.addColumns(...) ``];
- Colunas pelas quais a consulta será ordenada [`` <? extends QBaseClass>.addSortedColumns(...) ``];
- Colunas que serão utilizadas para filtro [Qualquer propriedade com valor diferente de null]
- Quantidade de registros a serem recuperados [`` Em breve ``];
- Janela de dados que podem ser recuperados [`` Em breve ``].



## Dependências / Restrições

 - Entidades mapeadas com JPA/Hibernate

### DTO e POJO para HQL/JPQL queries instantâneas
