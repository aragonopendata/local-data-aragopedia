Como actualizar la configuración de los cubos de datos
======================================================

#### Introducción
En el enlace proporcionado tenemos una hoja de excel por cada cubo de datos agrupados por área. Es decir para el cubo de datos 01-010001A, 01-010001TC, 01-010001TM, 01-010001TP tendremos un solo excel de nombre Informe-01-010001-A-TC-TM-TP para configurar los cuatro cubos de datos.

Cada columna del excel se corresponde con una columna de los cubos de datos.

 - La primera fila es el nombre del csv original.
 - La segunda fila es el nombre normalizado, es decir en minúsculas, sin espacios y como único símbolo el carácter '-'
 - La tercera fila es la URI que va a normalizar la columna
 - La cuarta fila indica si es una dimensión (dim) o una medida (medida)
 - La quita fila indica si es un kos si es un área o el tipo, si es entero, doble o cadena de texto
 - La sexta fila en caso de que sea un kos se especifica el nombre del fichero donde contienen los valores del kos
 - La séptima fila se pondrá la cadena de texto 'constante' en caso de querer agregar un dato fijo al cubo de datos. Como por ejemplo el año
 - La octava fila se pondrá el valor constante a agregar. 
 - La novena fila se utiliza para relacionar dos kos, se podrá el nombre de la columna con la que se relaciona.
 - La décima fila se utiliza para relacionar dos kos, se podrá la cadena de texto que se utilizará en el kos.

##### Como añadir un valor constante a un cubo de datos

En una columna vacía de la configuración añadiremos la cadena de texto constante. Y el resto de filas se rellenarán como se explica en la introducción. Por ejemplo:

1ª fila -> Año
2ª fila -> año
3ª fila -> sdmx-dimension:refPeriod
4ª fila -> dim
5ª fila -> xsd:uri
6ª fila ->
7ª fila -> constante
8ª fila -> <http://reference.data.gov.uk/id/year/2016>

##### Como relacionar dos kos

En la novena fila del kos padre podremos el nombre de la columna del kos hijo. En la décima fila pondremos en ambos kos el nuevo nombre que tendrán ambos kos.

Se puede encontrar un ejemplo en la configuración Informe-01-010001-A-TC-TM-TP
