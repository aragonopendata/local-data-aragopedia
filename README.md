# local-data-aragopedia

 

Repositorio en el que se incluye el código fuente y los datos correspondientes al proyecto de extracción y transformación de informes y macro-datos del Instituto Aragonés de Estadística (IAEst) para el proyecto Open Data Aragón.     

 

## Pasos a realizar para el procesamiento de datos:

 

* Cada día a las 22:00 en el servidor de front, se ejecuta el script UpdateRDFandLoad-FRONT_new.sh que se encarga de recuperar y procesar los datos del IAEST. Compara los hash de los datacubes del día anterior (alojados en Github) con los del día actual, que los calcula en el proceso. 
* Descarga y procesa los que tengan cambios.
* Actualiza los hash (archivos hashcode.xlsx y hashcode.csv) y sube los datos que se han procesado en el dia actual a este repositorio en GitHub. 
* A las 7:00 del día siguiente, se ejecuta en el servidor de back el script UpdateRDFandLoad-BACK.sh, que recupera los cubos procesados por el servidor de front, los carga en Virtuoso y actualiza el buscador Solr.

 

## URLs resultantes:

 

* Todas las observaciones: http://opendata.aragon.es/recurso/iaest/observacion
* Observación específica: http://opendata.aragon.es/recurso/iaest/observacion/{idCubo}/{idObservation}
* Todos los dsd (Data Structure Definitions): http://opendata.aragon.es/recurso/iaest/dsd
* Estructura de un cubo específico: http://opendata.aragon.es/recurso/iaest/dsd/{idCubo}
* Todas los propiedades: http://opendata.aragon.es/recurso/iaest/property
* Dimension específica: http://opendata.aragon.es/recurso/iaest/dimension/{idDimension}
* Medida específica: http://opendata.aragon.es/recurso/iaest/medida/{idMedida}
* Codelist: http://opendata.aragon.es/kos/iaest/{idCodelist}
* Concepto específico de una codelist: http://opendata.aragon.es/kos/iaest/{idCodelist}/{valor}
* Todos los cubos: http://opendata.aragon.es/recurso/iaest/dataset
* Todas las observaciones del cubo: http://opendata.aragon.es/recurso/iaest/dataset/{idCubo}
* Cubos que tienen un valor dado para una dimensión dada: http://opendata.aragon.es/recurso/iaest/cubosdimension/{dimension}/{valor}
* Cubos que tienen un valor dado para una medida dada: http://opendata.aragon.es/recurso/iaest/cubosmedida/{medida}/{valor}
* Cubos que tienen un valor dado para alguna dimensión: http://opendata.aragon.es/recurso/iaest/cubosdimensionvalor/{valor}
* Cubos que tienen un valor dado para alguna medida: http://opendata.aragon.es/recurso/iaest/cubosmedidavalor/{valor}
* Cubos que tienen una dimensión dada: http://opendata.aragon.es/recurso/iaest/cubosdimensionpropiedad/{dimension}
* Cubos que tienen una medida dada: http://opendata.aragon.es/recurso/iaest/cubosmedidapropiedad/{medida}
* Cubos que tienen una comcarca para una dimensión específica http://opendata.aragon.es/recurso/iaest/cubosComarca/{valor}
* Cubos que tienen un municipio para una dimensión específica http://opendata.aragon.es/recurso/iaest/cubosMunicipio/{valor}
* Cubos que tienen una provincia para una dimensión específica http://opendata.aragon.es/recurso/iaest/cubosProvincia/{valor}
* Cubos que tienen una comunidad autónoma para una dimensión específica http://opendata.aragon.es/recurso/iaest/cubosComunidadAutonoma/{valor}

 

## Consultas a realizar en el SPARQL endpoint
En [Consultas](consultas.md) se pueden analizar estas consultas