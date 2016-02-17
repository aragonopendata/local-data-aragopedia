# local-data-aragopedia

Repositorio en el que se incluye el código fuente y los datos correspondientes al proyecto de extracción y transformación de informes y macro-datos del Instituto Aragonés de Estadística (IAEst) para el proyecto Open Data Aragón.

Para un correcto uso del código se necesita tener una credencial del https://console.developers.google.com

## Procesamiento de datos:

* Primero se debe de configurar el archivo system.properties espeficicando los parámetros que pide del google drive, una vez hecho en Constants podremos la variable 
publicDrive a true.

* Se ejecuta la clase GenerateConfig.

* Si hemos configurado que se publique en google drive, debemos de descargar la configuración en formato excel a una nueva carpeta.

* Se ejecuta la clase GenerateData.

* Subimos los archivos procesados a la base de datos orientada a grafos

* Configuramos el linked data API

## URL del api:

* Todas las observaciones

http://opendata.aragon.es/recurso/iaest/observacion

* Observacion por Id

http://opendata.aragon.es/recurso/iaest/observacion/{idCubo}/{idObservation}

* Todos los dsd

http://opendata.aragon.es/recurso/iaest/dsd

* Estructura de un cubo dado

http://opendata.aragon.es/recurso/iaest/dsd/{idCubo}

* Todas los property

http://opendata.aragon.es/recurso/iaest/property

* Dimension por Id

http://opendata.aragon.es/recurso/iaest/dimension/{idCubo}

* Medida por Id

http://opendata.aragon.es/recurso/iaest/medida/{idCubo}

* Codelist

http://opendata.aragon.es/kos/iaest/{idCodelist}

* Codelist por valor

http://opendata.aragon.es/kos/iaest/{idCodelist}/{valor}

* Todos los cubos

http://opendata.aragon.es/recurso/iaest/cubo

* Todas las observaciones del cubo

http://opendata.aragon.es/recurso/iaest/cubo/{idCubo}

* Cubos que tienen un valor dado para una dimensión dada

http://opendata.aragon.es/recurso/iaest/cubosdimension/{dimension}/{valor}

* Cubos que tienen un valor dado para una medida dada

http://opendata.aragon.es/recurso/iaest/cubosmedida/{medida}/{valor}

* Cubos que tienen un valor dado para alguna dimensión

http://opendata.aragon.es/recurso/iaest/cubosdimensionvalor/{valor}

* Cubos que tienen un valor dado para alguna medida

http://opendata.aragon.es/recurso/iaest/cubosmedidavalor/{valor}

* Cubos que tienen una dimensión dada

http://opendata.aragon.es/recurso/iaest/cubosdimensionpropiedad/{dimension}

* Cubos que tienen una medida dada

http://opendata.aragon.es/recurso/iaest/cubosmedidapropiedad/{medida}


