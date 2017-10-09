/**
 * 
 * @author Miquel Quetglas
 * @author AMS
 *
 */
package com.localidata.util;

import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Properties;



public class Jdbcconnection {

	
	public static void main (String args []) throws Exception
	{	
		
		//Creamos el fichero properties
	    Properties props = new Properties();
	    /*InputStream is = Jdbcconnection.class.getResourceAsStream("config.properties");
	    if( is != null )
	    {
	    	props.load(is);
	    }*/
	    InputStream input = new FileInputStream("config.properties");
		props.load(input);
	    
	    //Query que devuelve las carpetas estructuradas.
		String query_full = "select linea_clave, padre, descripcion, dirweb, operacion "
				+ "from"
				+ "  (select linea_clave, descripcion, padre, orden, operacion, level"
				+ "   from IAES.menu_linea"
				+ "   where linea_clave not like '900%'"
				+ "   start with padre = 0"
				+ "   connect by prior linea_clave = padre"
				+ "  ) linea,"
				+ "  ( select menu_linea, dirweb"
				+ "    from IAES.menu_link"
				+ "    where dirweb is not null"
				+ "      and (substr(dirweb, length(dirweb)-1) in ('TP','TC','TM')"
				+ "           or substr(dirweb, length(dirweb)) = 'A' )"
				+ "  ) enlace"
				+ "  where enlace.menu_linea (+) = linea.linea_clave";
		
		
		//Archivo en el que vamos a escribir.
		FileWriter writer = new FileWriter(props.getProperty("WRITE_FILE"));
		FileWriter writerToTest = new FileWriter("writerToTest.txt");
		FileWriter writer2 = new FileWriter(props.getProperty("WRITE_FILE2"));
		
		//Creamos la conexión
		Class.forName ("oracle.jdbc.OracleDriver");
		
		String dbURL = props.getProperty("DB_IAEST");
		String strUserID = props.getProperty("DB_USER");
		String strPassword = props.getProperty("DB_PASS");
		
        
        Connection conn=DriverManager.getConnection(dbURL,strUserID,strPassword);
        Statement statement = conn.createStatement();
        
        //Creamos la consulta
        ResultSet rs = statement.executeQuery(query_full);
       
        
        ArrayList<Valor> valores = new ArrayList<Valor>();

        //Insertamos los resultados en objetos de tipo Valor y los metemos en un ArrayList
        while(rs.next()) {
        	 Valor v = new Valor();      
	      	   v.setLinea_clave(rs.getString("linea_clave"));
	      	   v.setPadre(rs.getString("padre"));
	      	   v.setDescripcion(rs.getString("descripcion"));
	      	   v.setDirweb(rs.getString("dirweb"));
	      	   v.setOperacion(rs.getString("operacion"));
	      	   valores.add(v);
        	} 
       
       
        int lenS="Estadistica Local/".length();
        
        //Escribimos cabeceras en el archivo
        writer.append("\"Descripcion\"");
        writer.append(",");
        writer.append("\"DescripcionConJerarquia\"");
        writer.append(",");
        writer.append("\"DescripcionMejorada\"");
        writer.append(",");
        writer.append("\"Ruta\"");
        writer.append(",");
        writer.append("\"RutaSinTipo\"");
        writer.append(",");
        writer.append("\"Tipo\"");
        writer.append(",");
        writer.append("\"Operacion\"");
   	 	writer.append("\n");
   	 	
   	 	
   	 	//Cabeceras Fichero2  CodigoEstadisticaNormal,CodigoEstadistica
   	 	writer2.append("\"CodigoEstadisticaNormal\"");
   	 	writer2.append(",");
   	 	writer2.append("\"CodigoEstadistica\"");
   	 	writer2.append(",");
   	 	writer2.append("\"Descripcion\"");
   	 	writer2.append("\n");
   	 	
        for (Valor valor : valores) {
        	
        	
        	//Excluimos los informes de la carpeta /06
        	if(valor.getDirweb() != null && !valor.getDirweb().substring(lenS,lenS+2).equals("06")){
	        	writer.append("\"" + valor.getDescripcion() + "\"");
	        	writer.append(",");
	        	writer.append("\"" + valor.getDescripcion() + " # ");
	        	
	        	String descripcionMejorada = valor.getDescripcion();
	        	//writerToTest.write(valor.getDescripcion().substring(valor.getDescripcion().length() - 4).toLowerCase()+"\n");
	        	//Vamos recorriendo cada carpeta y sus padres anidando las descripciones. 
	        	for (Valor valor1 : valores) {
	        		 if(valor1.getLinea_clave().equals(valor.getPadre())){
	        			
	        			//1.- Los informes que la descripción empieze por "según", concaternar el padre. (Excluyendo los de la carpeta 05)
	     	        	//2.- Los informes que la descripción empiecen por "Por", concaternar el padre.
	        			//3.- (En carpeta 05)Si la última palabra de la descripción es "sexo", concatenar el padre.
	     	        	if(valor.getDescripcion().length()>=4){
		     	        	if((valor.getDescripcion().substring(0, 4).toLowerCase()).equals("segú")
		     	        			|| valor.getDescripcion().substring(0, 3).toLowerCase().equals("por")
		     	        			|| (valor.getDescripcion().substring(valor.getDescripcion().length() - 4).toLowerCase().equals("sexo")) && valor.getDirweb().substring(lenS,lenS+2).equals("05") 
		     	        			|| valor1.getDescripcion().toLowerCase().contains("accidentes")){
		     	        		
		     	        		descripcionMejorada =  valor1.getDescripcion()  + " " + descripcionMejorada;
		     	        	} 
	     	        	}
	        			 
	        			 writer.append(valor1.getDescripcion() + " # ");
	        			 for (Valor valor2 : valores) {
	                		 if(valor2.getLinea_clave().equals(valor1.getPadre())){
	                			 
	                			 writerToTest.write(Boolean.toString(descripcionMejorada.contains("Características personales")) + "\n");
		        				 if(descripcionMejorada.contains("Características personales") || descripcionMejorada.contains("Características del contrato")){
		        					 descripcionMejorada =  valor2.getDescripcion()  + " " + descripcionMejorada.replace("Características personales","").replace("Características del contrato", "");
		        				 }
	                			 
	                			 writer.append(valor2.getDescripcion() + " # ");
	                			 for (Valor valor3 : valores) {
	                        		 if(valor3.getLinea_clave().equals(valor2.getPadre())){
	                        			 writer.append(valor3.getDescripcion() + " # ");
	                        			 for (Valor valor4 : valores) {
	                                		 if(valor4.getLinea_clave().equals(valor3.getPadre())){
	                                			 writer.append(valor4.getDescripcion() + " # ");
	                                			 for (Valor valor5 : valores) {
	                                        		 if(valor5.getLinea_clave().equals(valor4.getPadre())){
	                                        			 writer.append(valor5.getDescripcion() + " # ");
	                                        			 for (Valor valor6 : valores) {
	                                                		 if(valor6.getLinea_clave().equals(valor5.getPadre())){
	                                                			 writer.append(valor6.getDescripcion() + " # ");
	                                                		 }
	                                                     }
	                                        		 }
	                                             }
	                                		 }
	                                     }
	                        		 }
	                             }
	                		 }
	                     }
	        		 }
	             }
	        	 writer.append("\"");
	        	 
	        	 
	        	 
	        	 /*
	        	  * Nuevo campo, DescripcionMejorada
	        	  */
	        	 writer.append(",");
	        	 writer.append("\"" +descripcionMejorada+ "\"");
	        	 
	        	 
	        	 writer.append(",");
	        	 //Insertamos Ruta
	        	 writer.append("\"" +valor.getDirweb()+ "\"");
	        	 writer.append(",");
	        	 
	        	 String dirweb_sin_code  = "";
	        	 String code = "";
	        	 String ct1 = valor.getDirweb().substring(valor.getDirweb().length() - 1);
	        	 if(ct1.equals("A")){
	        		 dirweb_sin_code = valor.getDirweb().substring(0,valor.getDirweb().length()-1);
	        		 code = valor.getDirweb().substring(valor.getDirweb().length() - 1);
	        	 }else{
	        		 dirweb_sin_code = valor.getDirweb().substring(0,valor.getDirweb().length()-2);
	        		 code = valor.getDirweb().substring(valor.getDirweb().length() - 2);
	        	 }
	        	 
	        	 //Insertamos Ruta sin el coódigo del final (A,TC,TM,TA)
	        	 writer.append("\"" + dirweb_sin_code + "\"");
	        	 writer.append(",");
	        	 //Insertamos código
	        	 writer.append("\"" + code + "\"");
	        	 writer.append(",");
	        	 //Insertamos operacion
	        	 writer.append("\"" + valor.getOperacion() + "\"");
	        	 
	        	 writer.append("\n");
	        	 
	        	 int len = "Estadistica Local".length() + 1;
	        	 String codigoEstadistica = valor.getDirweb().substring(len); 
	        	//Escribimos en writer2
	         	writer2.append("\""+ codigoEstadistica +"\"");
	        	writer2.append(",");
	        	writer2.append("\""+ codigoEstadistica.replace("/", "-") +"\"");
	        	writer2.append(",");
	        	writer2.append("\""+ descripcionMejorada +"\"");
	        	writer2.append("\n");
	
	        }	
        }
        
        writer.close();
        writer2.close();
        writerToTest.close();
	}
		
}
