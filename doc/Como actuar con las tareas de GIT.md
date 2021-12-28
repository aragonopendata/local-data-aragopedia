# Cómo actuar cuando se reciben issues a través de Github

### Se indica que se ha regenerado un cubo de datos.

Líneas informativas que no requieren ninguna acción.

Ejemplo:

En el cubo 05-050204TP, se han detectado nuevos registros y se va a regenerar el cubo de datos.

### Se indica que se ha detectado un nuevo cubo de datos.

El proceso ha generado una configuración por defecto y generado con extensión csv. Habrá que repasar todo, pero principalmente si las dimensiones y las medidas están bien elegidas. Una vez hecho esto, se debe generar el fichero .xlsx correspondiente en el directorio data/metadata de este repositorio.

Ejemplo:

Se ha detectado un nuevo cubo de datos 915-150017TM, revise la nueva configuración propuesta y sus mapping 915-150017.

### Se indica que se ha detectado un nueva columna en un cubo de datos.

El proceso ha detectado una nueva columna y habrá que añadir la configuración correspondiente alojada en el directorio de este repositorio data/metadata. Para saber cómo hacerlo habrá que seguir las intrucciones de la guía 'guía de la configuración'  https://github.com/aragonopendata/local-data-aragopedia/blob/master/doc/guia%20de%20la%20configuraci%C3%B3n.md

Ejemplo:

Se han añadido las columnas 'CCAA nombre', al cubo 05-050302A, por favor actualice la configuración Infocsv

### Se indica que hay nuevos valores para una codelist

El proceso ha detectado que hay nuevos valores para una dimensión y hay que añadirlos en el enlace que se adjunta. Son dos columnas: una con el valor tal cual viene en el csv y otra con su URI.

Ejemplo:

Se han añadido los valores '44142' al codelist 05-050104TM, por favor actualice la configuración Infocsv

### Se indica que un cubo de datos ha sido borrado

Líneas informativas que no requieren ninguna acción.

Ejemplo:

El cubo de datos 05-050204TP ya no se encuentra en la base de datos.
