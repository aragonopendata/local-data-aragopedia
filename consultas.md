##Ejemplos de consultas sobre cubos de datos

A continuación se presentan algunos ejemplos de consultas sobre cubos de datos, utilizando como referencia el conjunto de datos 03-030005TM, que se corresponde con la pirámide de población en grandes grupos de edad, por municipios 

Por ejemplo, en esta [consulta](http://opendata.aragon.es/sparql?default-graph-uri=&query=select+distinct+%3Fx+%3Fperiod+%3Farea+%3Fedad+%3Fsexo+%3Fpersonas%0D%0Awhere+%0D%0A%7B%0D%0A++%3Fx+a+qb%3AObservation+.+%0D%0A++%3Fx+qb%3AdataSet+%3Chttp%3A%2F%2Fopendata.aragon.es%2Frecurso%2Fiaest%2Fdataset%2F03-030005TM%3E+.+%23Pir%C3%A1mide+Poblaci%C3%B3n+Grandes+Grupos%0D%0A++%3Fx+%3Chttp%3A%2F%2Fpurl.org%2Flinked-data%2Fsdmx%2F2009%2Fdimension%23refPeriod%3E+%3Fperiod+.%0D%0A++%3Fx+%3Chttp%3A%2F%2Fpurl.org%2Flinked-data%2Fsdmx%2F2009%2Fdimension%23refArea%3E+%3Farea+.%0D%0A++%3Fx+%3Chttp%3A%2F%2Fopendata.aragon.es%2Fdef%2Fiaest%2Fdimension%23edad-grandes-grupos%3E+%3Fedad+.%0D%0A++%3Fx+%3Chttp%3A%2F%2Fopendata.aragon.es%2Fdef%2Fiaest%2Fdimension%23sexo%3E+%3Fsexo+.%0D%0A++%3Fx+%3Chttp%3A%2F%2Fopendata.aragon.es%2Fdef%2Fiaest%2Fmedida%23personas%3E+%3Fpersonas+.%0D%0A%7D+%0D%0A+LIMIT+100&format=text%2Fhtml&timeout=0&debug=on) se piden 100 de las observaciones relacionadas con este conjunto de datos  
```
select distinct ?x ?period ?area ?edad ?sexo ?personas
where 
{
  ?x a qb:Observation . 
  ?x qb:dataSet <http://opendata.aragon.es/recurso/iaest/dataset/03-030005TM> .
  ?x <http://purl.org/linked-data/sdmx/2009/dimension#refPeriod> ?period .
  ?x <http://purl.org/linked-data/sdmx/2009/dimension#refArea> ?area .
  ?x <http://opendata.aragon.es/def/iaest/dimension#edad-grandes-grupos> ?edad .
  ?x <http://opendata.aragon.es/def/iaest/dimension#sexo> ?sexo .
  ?x <http://opendata.aragon.es/def/iaest/medida#personas> ?personas .
} 
 LIMIT 100
```

En la siguiente [consulta](http://opendata.aragon.es/sparql?default-graph-uri=&query=select+distinct+%3Farea+%3Fperiod+%28AVG%28%3Fpersonas%29+AS+%3FmediaPersonas%29%0D%0Awhere+%0D%0A%7B%0D%0A++%3Fx+a+qb%3AObservation+.+%0D%0A++%3Fx+qb%3AdataSet+%3Chttp%3A%2F%2Fopendata.aragon.es%2Frecurso%2Fiaest%2Fdataset%2F03-030005TM%3E+.+%23Pir%C3%A1mide+Poblaci%C3%B3n+Grandes+Grupos%0D%0A++%3Fx+%3Chttp%3A%2F%2Fpurl.org%2Flinked-data%2Fsdmx%2F2009%2Fdimension%23refPeriod%3E+%3Fperiod+.%0D%0A++%3Fx+%3Chttp%3A%2F%2Fpurl.org%2Flinked-data%2Fsdmx%2F2009%2Fdimension%23refArea%3E+%3Farea+.%0D%0A++%3Fx+%3Chttp%3A%2F%2Fopendata.aragon.es%2Fdef%2Fiaest%2Fdimension%23edad-grandes-grupos%3E+%3Chttp%3A%2F%2Fopendata.aragon.es%2Fkos%2Fiaest%2Fedad-grandes-grupos%2F0-a-15%3E+.%0D%0A++%3Fx+%3Chttp%3A%2F%2Fopendata.aragon.es%2Fdef%2Fiaest%2Fdimension%23sexo%3E+%3Fsexo+.%0D%0A++%3Fx+%3Chttp%3A%2F%2Fopendata.aragon.es%2Fdef%2Fiaest%2Fmedida%23personas%3E+%3Fpersonas+.%0D%0A%7D+GROUP+BY+%3Farea+%3Fperiod+ORDER+BY+%3Farea+%3Fperiod%0D%0A&format=text%2Fhtml&timeout=0&debug=on) se agrupan por año y municipio las personas del grupo de edad de 0 a 15 años, independientemente del sexo, utilizando la media como medida de agregación.
```
select distinct ?area ?period (AVG(?personas) AS ?mediaPersonas)
where 
{
  ?x a qb:Observation . 
  ?x qb:dataSet <http://opendata.aragon.es/recurso/iaest/dataset/03-030005TM> . #Pirámide Población Grandes Grupos
  ?x <http://purl.org/linked-data/sdmx/2009/dimension#refPeriod> ?period .
  ?x <http://purl.org/linked-data/sdmx/2009/dimension#refArea> ?area .
  ?x <http://opendata.aragon.es/def/iaest/dimension#edad-grandes-grupos> <http://opendata.aragon.es/kos/iaest/edad-grandes-grupos/0-a-15> .
  ?x <http://opendata.aragon.es/def/iaest/dimension#sexo> ?sexo .
  ?x <http://opendata.aragon.es/def/iaest/medida#personas> ?personas .
} GROUP BY ?area ?period ORDER BY ?area ?period
```

La URI [http://opendata.aragon.es/recurso/iaest/observacion/03-030005TM/50a0a5b9-cb76-37f4-961c-8abdf1c458e3](http://opendata.aragon.es/recurso/iaest/observacion/03-030005TM/50a0a5b9-cb76-37f4-961c-8abdf1c458e3) se puede utilizar para hacer referencia al siguiente texto: En el año 1998 en Zaragoza había 42916 mujeres en el rango de edad de 0 a 15 años

Y esta [consulta](http://opendata.aragon.es/sparql?default-graph-uri=&query=select+distinct+%3Fyear+%3Fpersonas%0D%0Awhere+%0D%0A%7B%0D%0A++%3Fx+a+qb%3AObservation+.+%0D%0A++%3Fx+qb%3AdataSet+%3Chttp%3A%2F%2Fopendata.aragon.es%2Frecurso%2Fiaest%2Fdataset%2F03-030005TM%3E+.+%23Pir%C3%A1mide+Poblaci%C3%B3n+Grandes+Grupos%0D%0A++%3Fx+%3Chttp%3A%2F%2Fpurl.org%2Flinked-data%2Fsdmx%2F2009%2Fdimension%23refPeriod%3E+%3Fyear+.%0D%0A++%3Fx+%3Chttp%3A%2F%2Fpurl.org%2Flinked-data%2Fsdmx%2F2009%2Fdimension%23refArea%3E+%3Chttp%3A%2F%2Fopendata.aragon.es%2Frecurso%2Fterritorio%2FMunicipio%2FZaragoza%3E.%0D%0A++%3Fx+%3Chttp%3A%2F%2Fopendata.aragon.es%2Fdef%2Fiaest%2Fdimension%23edad-grandes-grupos%3E+%3Chttp%3A%2F%2Fopendata.aragon.es%2Fkos%2Fiaest%2Fedad-grandes-grupos%2F0-a-15%3E+.%0D%0A++%3Fx+%3Chttp%3A%2F%2Fopendata.aragon.es%2Fdef%2Fiaest%2Fdimension%23sexo%3E+%3Chttp%3A%2F%2Fopendata.aragon.es%2Fkos%2Fiaest%2Fsexo%2Fmujeres%3E.%0D%0A++%3Fx+%3Chttp%3A%2F%2Fopendata.aragon.es%2Fdef%2Fiaest%2Fmedida%23personas%3E+%3Fpersonas+.%0D%0A%7D+ORDER+BY+%3Fyear%0D%0A&format=text%2Fhtml&timeout=0&debug=on) se puede utilizar como referencia para el siguiente texto: La tendencia de población de 0 a 15 años en mujeres en Zaragoza creció hasta el 2013 y luego ha ido descendiendo levemente
```
select distinct ?year ?personas
where 
{
  ?x a qb:Observation . 
  ?x qb:dataSet <http://opendata.aragon.es/recurso/iaest/dataset/03-030005TM> . #Pirámide Población Grandes Grupos
  ?x <http://purl.org/linked-data/sdmx/2009/dimension#refPeriod> ?year .
  ?x <http://purl.org/linked-data/sdmx/2009/dimension#refArea> <http://opendata.aragon.es/recurso/territorio/Municipio/Zaragoza>.
  ?x <http://opendata.aragon.es/def/iaest/dimension#edad-grandes-grupos> <http://opendata.aragon.es/kos/iaest/edad-grandes-grupos/0-a-15> .
  ?x <http://opendata.aragon.es/def/iaest/dimension#sexo> <http://opendata.aragon.es/kos/iaest/sexo/mujeres>.
  ?x <http://opendata.aragon.es/def/iaest/medida#personas> ?personas .
} ORDER BY ?year
```




