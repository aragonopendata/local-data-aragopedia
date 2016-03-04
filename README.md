# local-data-aragopedia

Repositorio en el que se incluye el código fuente y los datos correspondientes al proyecto de extracción y transformación de informes y macro-datos del Instituto Aragonés de Estadística (IAEst) para el proyecto Open Data Aragón.

Para un correcto uso del código se necesita tener una credencial del https://console.developers.google.com

## Pasos a realizar para la descarga masiva de datos:

* Se deben utilizar los scripts que se encuentra en la carpeta src/01_extraction-scripts, en el orden que se indica. Ojo, que hay que tener cuidado con las cookies, que deben ser actualizadas cada vez que se haga un proceso de extracción masiva.

## Pasos a realizar para el procesamiento de datos:

* Primero se debe de configurar el archivo system.properties espeficicando los parámetros que pide del Google Drive. Una vez hecho esto, en Constants se pondrá la variable publicDrive a true.
* Se ejecuta la clase GenerateConfig.
* Si se ha configurado que se publique en Google Drive, se debe descargar la configuración en formato Excel a una nueva carpeta.
* Se ejecuta la clase GenerateData.
* Se suben los archivos procesados a la base de datos de tripletas
* Se configura el linked data API

## URLs de la API:

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


