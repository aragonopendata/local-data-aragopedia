# local-data-aragopedia

Repositorio en el que se incluye el código fuente y los datos correspondientes al proyecto de extracción y transformación de informes y macro-datos del Instituto Aragonés de Estadística (IAEST) para el proyecto Open Data Aragón.

En la última actualización, principalmente se ha adaptado el código a la integración con el nuevo servicio desarrollado por el Instituto Aragonés de Estadística (IAEST).

## Pasos a realizar para el procesamiento de datos:

* Cada día a las 22:00 en el servidor de front, se ejecuta el script UpdateRDFandLoad-FRONT.sh que se encarga de recuperar y procesar los datos del IAEST. 
* Recupera el listado de los cubos de datos disponibles y lo vuelca sobre el fichero InformesEstadisticaLocal-URLs.csv.
* Recorre esta lista descargando cada cubo desde el servicio de datos del IAEST y genera un hash por cada archivo descargado. Compara el hash con el del día anterior para detectar si ha habido cambios:
	* Si se trata de un cubo nuevo, genera una propuesta de fichero de configuración (csv) y lo indica en el issue del día, que publica en este repositorio, para proceder a su revisión y consolidación como configuración del nuevo cubo de datos.
	* Si es un cubo existente y ha habido cambios, revisa si su estructura corresponde a la configuración que tiene definida (fichero xlsx correspondiente alojado en /data/metadata). Si es así, procede a la generación de un fichero ttl que servirá para realizar su carga en el paso final y si no, genera un registro en el issue del día con la revisión a realizar, que luego publica en este repositorio.
* Actualiza los hash (archivos hashcode.xlsx y hashcode.csv) y sube los datos que se han procesado en el día actual a este repositorio en GitHub. 
* A las 7:00 del día siguiente, se ejecuta en el servidor de back el script UpdateRDFandLoad-BACK.sh, que recupera los cubos procesados por el servidor de front y los carga en Virtuoso.

## URIs a tener en cuenta:

* Grafo de un cubo: http://opendata.aragon.es/graph/datacube/{idCubo}
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