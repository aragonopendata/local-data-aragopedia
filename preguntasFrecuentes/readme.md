##### ¿Se paginan los valores de las codelist si accedo con este tipo de query http://opendata.aragon.es/kos/iaest/ano-de-construccion?

> No se pagina el resultado ya que los codelist solo va a devolver un resultado y a través de hasTopConcept se puede obtener sus posibles valores.

##### ¿Es siempre las entidades territoriales refArea en el dsd?

>  Si, las entidades territoriales son siempre refArea en el dsd
	
##### ¿Puedo considerar refPeriod (si aparece) como serie temporal? ¿Variará para ese mismo informe o no hay series temporales?

> Si, refPeriod es una serie temporal, pero en los datos casi nunca aparece y se está agregando manualmente, podrá variar cuando se introduzca en los datos originales.

##### ¿Como puedo obtener las entidades territoriales que cumplen más de un criterios?

> Con una query en el punto sparql, en http://opendata.aragon.es:8890/sparql

##### En una query sparql filtrando por una dimension del cubo de datos 01-010002TC me devuelve 6 resultados, cuando esperaba solo uno ¿Es normal?

> Si es normal, para el csv que comentas, es un cubo con las siguientes dimensiones:
> 
> * Comarca nombre	
> * REGTENEN ORDEN	
> * Régimen de tenencia (agregado)	
> * Régimen de tenencia (detalle)	
> 
> Y la siguiente medida:
>
> * Número hogares
> 
> Para obtener solo un resultado tendrías que filtrar por más dimensiones. Por ejemplo filtrar por Régimen de tenencia (agregado) y Régimen de tenencia (detalle).
> 
> Para saber los posibles valores de esas dimensiones entrarías en las siguientes URL:
> 
> http://opendata.aragon.es/kos/iaest/regimen-de-tenencia-agregado
> http://opendata.aragon.es/kos/iaest/regimen-de-tenencia-detalle

##### ¿en todos los cubos tengo que filtrar por todas las dimensiones que tenga?

> Simplificando si. Pero es algo más complicado que eso. Intento explicarlo con un ejemplo más sencillo.
> 
> El siguiente cubo de datos:
> 
> genero	edad	profesión	provincia de residencia	Total personas
> 
> hombre	30	informático	Madrid	100
> mujer	40	fontanero	León	50
> hombre	40	informático	Sevilla	800
> mujer	30	doctora	Zaragoza	60
> 
> Si filtras solo por provincia, te daría solo un resultado o si filtras por informático dos, pero es por como son los datos del cubo.  Lo más habitual es que tengas que filtrar por cada una de las dimensiones para obtener un valor, pero depende de como sean los datos.
	
	