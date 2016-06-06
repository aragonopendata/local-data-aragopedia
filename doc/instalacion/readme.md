# Manual de instalación

## Instalación del proceso de generación de cubo de datos y carga en virtuoso

1. Comprobaremos que tenemos java 8 instalado ejecutando el comando java -version
2. Comprobaremos que tenemos git instalado ejecutando el comando git -version
3. Descomprimiremos el zip doc/instalacion/datacube.zip en el directorio home del usuario
4. A los archivos .sh les daremos permisos con el comando: sudo chmod 755 *.sh
5. Ejecutaremos el siguiente comando teniendo en cuanta que hay que sustituir <usuarioGit> por el usuario de git que va a crear las issues: curl -i -u <usuarioGit> -d '{"scopes": ["repo", "user"], "note": "getting-started"}' https://api.github.com/authorizations
5.1. En la respuesta nos quedaremos con el código que hay al lado de "token": para ponerlo en la configuración.
6. Editaremos el archivo datacube/app/KBManager/props/3cixty.properties En user pondremos el usuario de virtuoso en password la contraseña de virtuoso, en virtuoso_path donde está instalado virtuoso y en httpHost la dirección del punto de sparql
7. Editaremos el archivo datacube/system.properties en githubToken podremos el token del usuario que va a crear las issues y en githubURLIssues la URL del repositorio de GitHub
8. Comprobaremos que podemos descargar documentos de google drive ejecutando el comando: java -Dfile.encoding=UTF8 -jar DataCube.jar googleDrive
9. Comprobaremos que podemos crear issues en GitHub ejecutando el comando: java -Dfile.encoding=UTF8 -jar DataCube.jar createIssue
10. Descargaremos el proyecto del git
10.1. En el directorio datacube ejecutamos el comando git clone  https://github.com/aragonopendata/local-data-aragopedia.git
10.2. Se descargará un directorio local-data-aragopedia, entramos en el y editamos el fichero readme.md
10.3. Añadiremos un espacio en blanco al final de la primera línea
10.4. Ejecutaremos los siguientes comandos en el directorio local-data-aragopedia:
10.4.1. Para este paso vamos a necesitar un usuario de github que pueda hacer commit en el repositorio http://github.com/aragonopendata/local-data-aragopedia.git
10.4.2. git config credential.helper store
10.4.3. git add .
10.4.4. git commit -m "Actualización  automática $(date)"
10.4.5. git push origin master
10.4.6. Insertamos el usuario de github
10.4.7. Insertamos la contraseña github
11. Ejecutaremos el script home/datacube/GenerateAllRDFandLoad.sh
12. Se revisará el log para ver que no hay nada excepciones que paren el proceso.
13. Ejecutaremos el script home/datacube/UpdateRDFandLoad.sh
14. Se revisará el log para ver que no hay nada excepciones que paren el proceso.


## Instalación del proceso de actualización de datos y carga en virtuoso

1. Ejecutamos el comando crontab -e
2. Insertamos en la ultima línea lo siguiente:
00 22 * * * ~/datacube/UpdateRDFandLoad.sh
3. Comprobaremos que a las 22:00 de cada día se ejecuta el script viendo el log datacube/datacube.log
4. Se revisará que se ha creado un issue en github con los cambios realizados.
 
## Como desplegar los linked data api en nuestro servidor

 1. Una vez accedemos a la máquina, desplegamos en el tomcat los war doc.war, eldakos.war y kos.war, en ese orden. Para desplegar los war se puede hacer o desde el tomcat manager vía navegador web o parando el tomcat, copiando los .war en la carpeta webapps (por ejemplo /var/lib/tomcat7/webapps) y arrancando en tomcat (sudo service tomcat7 restart)
 2. Una vez desplegados los war visualizamos que en los logs no hay errores (/var/log/tomcat7)
 3. En la aplicación actual del linkded data api, normalmente llamadas standalone o elda, deberemos de agregar el fichero iaest.ttl en la carpeta var/lib/tomcat7/webapps/standalone/specs/iaest.ttl y después deberemos de editar el fichero /var/lib/tomcat7/webapps/standalone/WEB-INF/web.xml añadiendo la siguiente línea ', iaest::specs/iaest.ttl' después del actual contenido de la etiqueta <param-value>
 4. Si el host del servidor es diferente de http://opendata.aragon.es habrá que editar el fichero /var/lib/tomcat7/webapps/kos/WEB-INF/localidata.properties cambiando la variable urlRecurso por el host del actual servidor y si existe seguridad 3scale habrá que poner la variable seguridad3Scale a true y en localidataKey3Scale la key identificativa
 5. Editar los ficheros /var/lib/tomcat7/webapps/doc/api.json y /var/lib/tomcat7/webapps/doc/index.html y en donde aparezca alzir.dia.fi.upm.es sustituirlo por el host del servidor 
 
Si el despliegue se ha hecho correctamente las siguientes urls deberían de cargar bien:

http://opendata.aragon.es/recurso/iaest/dsd
http://opendata.aragon.es/recurso/iaest/observacion.json
http://opendata.aragon.es/kos/iaest/regimen-de-tenencia-agregado.json
http://opendata.aragon.es/recurso/iaest/cubosdimension/clase-vivienda-agregado/colectiva.json
http://opendata.aragon.es/doc
