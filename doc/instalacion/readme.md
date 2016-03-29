#Manual de instalación

##Como carga en Virtuoso de los datos

1. Crearemos una carpeta virtuoso_BulkLoad en el home por ejemplo /home/localidata/virtuoso_BulkLoad en caso de que no lo tengamos ya creado.
2. Añadir el directorio recien creado /home/localidata/virtuoso_BulkLoad en la línea de DirsAllowed del fichero virtuoso.ini Normalmente se encuentra en  /etc/virtuoso-opensource-6.1/virtuoso.ini o en /usr/local/virtuoso-opensource/var/lib/virtuoso/db/virtuoso.iniencuentra en  /etc/virtuoso-opensource-6.1/virtuoso.ini o en /usr/local/virtuoso-opensource/var/lib/virtuoso/db/virtuoso.ini
3. Se reiniciará virtuoso
4. En el directorio recien creado crear un fichero llamado global.graph en el cual contendrá el grafo donde se cargarán los datos.
5. Colocaremos en la carpeta virtuoso_BulkLoad comentada en el punto anterior, todos los ttl de las subcarpetas de la siguiente URL https://github.com/localidata/local-data-aragopedia/tree/master/data/dump/DatosTTL
6. Entraremos en el conductor de virtuoso, por ejemplo http://localhost:8890/conductor/main_tabs.vspx
7. Hacemos login y hacemos click en el enlace "Interactive SQL (ISQL)"
8. Se nos despliega una nueva ventana en la cual copiaremos los siguientes comandos:
 
   ld_dir_all('/home/localidata/virtuoso_BulkLoad','*.ttl','http://opendata.aragon.es:8890/datacube');
   rdf_loader_run();
   select * from DB.DBA.load_list;	
   
	DB.DBA.RDF_OBJ_FT_RULE_ADD (null, null, 'All');
	DB.DBA.VT_INC_INDEX_DB_DBA_RDF_OBJ ();
	DB.DBA.VT_INDEX_DB_DBA_RDF_OBJ ();

   Teniendo en cuenta que se debe sustituir el path '/home/localidata/virtuoso_BulkLoad' por el path creado en los primeros pasos.

9. Una vez esté listo el comando con nuestros datos, pulsaremos el botón Execute
10. Para comprobar que la carga se ha hecho correctamente se lanzará en el punto sparql, por ejemplo http://opendata.aragon.es:8890/sparql, la siguiente query:

   select distinct ?graph where {

   GRAPH ?graph {
   ?x ?y ?z
   }

   }
   
   Y en el resultado deberá de estar el grafo que esté en el archivo global.graph

##Como actualizar los datos en virtuoso

1. Primero debemos borrar los datos viejos, para ello entraremos en el conductor de virtuoso por ejemplo http://localhost:8890/conductor/main_tabs.vspx
2. Haremos click en el menu linked data y despues en Graphs
3. En la página recien cargada volvemos a pulsar en Graphs y una vez cargados todos los grafos pulsamos en delete de nuestro grafo http://opendata.aragon.es:8890/datacube
4. Copiamos los nuevos datos a cargar en nuestro directorio virtuoso_BulkLoad, pero debemos de cambiar el nombre de la subcarpeta. Si antes se llamaba datacube1 ahora lo renombraremos a datacube2.
5. El resto de pasos son iguales a la carga de datos a partir del punto 6.
 
##Como desplegar los linked data api en nuesto servidor

* Supuesto 1, En el servidor ya hay un linked data API 

 1. Una vez accedemos a la máquina, deplegamos en el tomcat los war doc.war, eldakos.war y kos.war, en ese orden. Para desplegar los war se peude hacer o desde el tomcat manager via navegador web o parando el tomcat, copiando los .war en la carpeta webapps (por ejemplo /var/lib/tomcat7/webapps) y arrancando en tomcat (sudo service tomcat7 restart)
 2. Una vez desplegados los war visualizamos que en los logs no hay errores (/var/log/tomcat7)
 3. En la aplicación actual del linkded data api, normalmente llamadas standalone o elda, deberemos de agregar el fichero iaest.ttl en la carpeta var/lib/tomcat7/webapps/standalone/specs/iaest.ttl y despues deberemos de editar el fichero /var/lib/tomcat7/webapps/standalone/WEB-INF/web.xml añadiendo la siguiente linea ', iaest::specs/iaest.ttl' despues del actual contenido de la etiqueta <param-value>
 4. Si el host del servidor es diferente de http://opendata.aragon.es habrá que editar el fichero /var/lib/tomcat7/webapps/kos/WEB-INF/localidata.properties cambiando la variable urlRecurso por el host del actual servidor y si existe seguridad 3scale habrá que poner la variable seguridad3Scale a true y en localidataKey3Scale la key identificativa
 5. Editar los ficheros /var/lib/tomcat7/webapps/doc/api.json y /var/lib/tomcat7/webapps/doc/index.html y en donde aparezca alzir.dia.fi.upm.es sustituirlo por el host del servidor 
 
* Supuesto 2, En el servidor NO hay un linked data API
 
 1. Una vez accedemos a la máquina, deplegamos en el tomcat los war doc.war, eldakos.war, standalone.war, estaticos.war, recurso.war y kos.war, en ese orden.  Para desplegar los war se peude hacer o desde el tomcat manager via navegador web o copiando los .war en la carpeta webapps (por ejemplo /var/lib/tomcat7/webapps) y reiniciando en tomcat (sudo service tomcat7 restart)  
 2. Una vez desplegados los war visualizamos que en los logs no hay errores (/var/log/tomcat7)
 3. Si el host del servidor es diferente de http://opendata.aragon.es habrá que editar el fichero /var/lib/tomcat7/webapps/kos/WEB-INF/localidata.properties y /var/lib/tomcat7/webapps/recurso/WEB-INF/localidata.properties cambiando la variable urlRecurso por el host del actual servidor y si existe seguridad 3scale habrá que poner la variable seguridad3Scale a true y en localidataKey3Scale la key identificativa
 4. Editar los ficheros /var/lib/tomcat7/webapps/doc/api.json y /var/lib/tomcat7/webapps/doc/index.html y en donde aparezca alzir.dia.fi.upm.es sustituirlo por el host del servidor 
  