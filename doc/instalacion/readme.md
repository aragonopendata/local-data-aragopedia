# Manual de instalación

## Instalación del proceso de generacion de cubo de datos y carga en virtuoso

1. Comprobaremos que tenemos java 8 instalado ejecutando el comando java -version
2. Comprobaremos que tenemos git instalado ejecutando el comando git -version
3. Descomprimiremos el zip doc/instalacion/datacube.zip en el directorio home del usuario
4. A los archivos .sh les daremos permisos con el comando: sudo chmod 755 *.sh
5. Editaremos el archivo datacube/app/KBManager/props/3cixty.properties En user pondremos el usuario de virtuoso en password la contraseña de virtuoso y en virtuoso_path donde está instalado virtuoso.
6. Editaremos el archivo datacube/system.properties en emailDestination pondremos los correos electrónicos separados por comas de quien se va a encargar del mantenimiento de los datos y de su configuración
7. Descargaremos el proyecto del git
7.1. En el directorio datacube ejecutamos el comando git clone  http://github.com/aragonopendata/local-data-aragopedia.git
7.2. Se descargará un directorio local-data-aragopedia, entramos en el y editamos el fichero readme.md
7.3. Añadiremos un espacio en blanco al final de la primera linea
7.4. Ejecutaremos los siguientes comandos en el directorio local-data-aragopedia:
7.4.1. Para este paso vamos a necesitar un usuario de github que pueda hacer commit en el repositorio http://github.com/aragonopendata/local-data-aragopedia.git
7.4.1. git config --global user.name "<user github>"
7.4.2. git config --global user.email "<email user github>"
7.4.3. git add .
7.4.4. git commit -m "Actualización  automatica $(date)"
7.4.5. git push origin master
7.4.6. insert user github
7.4.7. insert password github
8. Editaremos los archivos GenerateAllRDFandLoad.sh y UpdateRDFandLoad.sh poniendo el mismo usuario y correo de github que en los pasos 7.4
9. Ejecutaremos el script home/datacube/GenerateAllRDFandLoad.sh
10. Para comprobar que la carga se ha hecho correctamente se lanzará en el punto sparql, por ejemplo http://opendata.aragon.es:8890/sparql, la siguiente query:

   select distinct ?graph where {

   GRAPH ?graph {
   ?x ?y ?z
   }

   }
   
   Y en el resultado deberán de haber grafos que empiecen por "http://opendata.aragon.es/graph/datacube"


## Instalación del proceso de actualización de datos y carga en virtuoso

1. Ejecutamos el comando crontab -e
2. Insertamos en la ultima linea lo siguiente:
00 00 * * * ~/datacube/UpdateRDFandLoad.sh
3. Comprobaremos que a las 00:00 de cada día se ejecuta el script viendo el log datacube/datacube.log
4. Para comprobar que la carga se ha hecho correctamente se lanzará en el punto sparql, por ejemplo http://opendata.aragon.es:8890/sparql, la siguiente query:

   select distinct ?graph where {

   GRAPH ?graph {
   ?x ?y ?z
   }

   }
   
   Y en el resultado deberán de haber grafos que empiecen por "http://opendata.aragon.es/graph/datacube"
 
## Como desplegar los linked data api en nuesto servidor

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

Si el despliegue se ha hecho correctamente las siguientes urls deberian de cargar bien:

http://preopendata.aragon.es/recurso/iaest/dsd
http://preopendata.aragon.es/recurso/iaest/observacion.json
http://preopendata.aragon.es/kos/iaest/regimen-de-tenencia-agregado.json
http://preopendata.aragon.es/recurso/iaest/cubosdimension/clase-vivienda-agregado/colectiva.json
http://preopendata.aragon.es/doc