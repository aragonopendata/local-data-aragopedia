##Ejemplos de consultas sobre cubos de datos

A continuación se presentan algunos ejemplos de consultas sobre cubos de datos, utilizando como referencia el conjunto de datos 03-030005TM, que se corresponde con la pirámide de población en grandes grupos de edad, por municipios 

Por ejemplo, en esta [consulta](http://opendata.aragon.es/sparql?default-graph-uri=&query=select+distinct+%3Fx+%3Fperiod+%3Farea+%3Fedad+%3Fsexo+%3Fpersonas%0D%0Awhere+%0D%0A%7B%0D%0A++%3Fx+a+qb%3AObservation+.+%0D%0A++%3Fx+qb%3AdataSet+%3Chttp%3A%2F%2Fopendata.aragon.es%2Frecurso%2Fiaest%2Fdataset%2F03-030005TM%3E+.+%23Pir%C3%A1mide+Poblaci%C3%B3n+Grandes+Grupos%0D%0A++%3Fx+%3Chttp%3A%2F%2Fpurl.org%2Flinked-data%2Fsdmx%2F2009%2Fdimension%23refPeriod%3E+%3Fperiod+.%0D%0A++%3Fx+%3Chttp%3A%2F%2Fpurl.org%2Flinked-data%2Fsdmx%2F2009%2Fdimension%23refArea%3E+%3Farea+.%0D%0A++%3Fx+%3Chttp%3A%2F%2Fopendata.aragon.es%2Fdef%2Fiaest%2Fdimension%23edad-grandes-grupos%3E+%3Fedad+.%0D%0A++%3Fx+%3Chttp%3A%2F%2Fopendata.aragon.es%2Fdef%2Fiaest%2Fdimension%23sexo%3E+%3Fsexo+.%0D%0A++%3Fx+%3Chttp%3A%2F%2Fopendata.aragon.es%2Fdef%2Fiaest%2Fmedida%23personas%3E+%3Fpersonas+.%0D%0A%7D+%0D%0A+LIMIT+100&format=text%2Fhtml&timeout=0&debug=on) se piden 100 de las observaciones relacionadas con este conjunto de datos  
```
select distinct ?x ?period ?area ?edad ?sexo ?personas
where 
{
  ?x a qb:Observation . 
  ?x qb:dataSet <http://opendata.aragon.es/recurso/iaest/dataset/03-030005TM> . #Pirámide Población Grandes Grupos
  ?x <http://purl.org/linked-data/sdmx/2009/dimension#refPeriod> ?period .
  ?x <http://purl.org/linked-data/sdmx/2009/dimension#refArea> ?area .
  ?x <http://opendata.aragon.es/def/iaest/dimension#edad-grandes-grupos> ?edad .
  ?x <http://opendata.aragon.es/def/iaest/dimension#sexo> ?sexo .
  ?x <http://opendata.aragon.es/def/iaest/medida#personas> ?personas .
} 
 LIMIT 100
```


