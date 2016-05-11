package com.localidata.extract;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Properties;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.apache.log4j.Logger;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;

import com.siebel.analytics.web.soap.v5.ItemInfo;
import com.siebel.analytics.web.soap.v5.QueryResults;
import com.siebel.analytics.web.soap.v5.ReportParams;
import com.siebel.analytics.web.soap.v5.ReportRef;
import com.siebel.analytics.web.soap.v5.SAWSessionServiceLocator;
import com.siebel.analytics.web.soap.v5.SAWSessionServiceSoap;
import com.siebel.analytics.web.soap.v5.WebCatalogServiceLocator;
import com.siebel.analytics.web.soap.v5.WebCatalogServiceSoap;
import com.siebel.analytics.web.soap.v5.XMLQueryExecutionOptions;
import com.siebel.analytics.web.soap.v5.XMLQueryOutputFormat;
import com.siebel.analytics.web.soap.v5.XmlViewServiceLocator;
import com.siebel.analytics.web.soap.v5.XmlViewServiceSoap;

/**
 * 
 * @author Idearium Consultores S.L.
 * @author Miquel Quetglas
 *
 */
public class OBIAragon {
	protected static Properties properties;  // archivo de configuración del servicio
	public static Logger logger = Logger.getLogger("IAESTdescarga"); //fichero para escribir logs
	public static ArrayList<String> fallidos = new ArrayList<String>();
	/**
	 * carga el fichero de propiedades
	 * @param propertiesPath ruta completa del fichero de propiedades
	 */
	static public void initProperties(String propertiesPath){

		properties = new Properties();
		try{
			properties.load(new FileInputStream(propertiesPath));
		}
		catch(Exception ex){
			logger.error("Error al cargar el fichero de propiedades "+propertiesPath, ex);
		}
	}

	/**
	 * Obtiene los del informe indicado del servicio OBI del IAEST
	 * @param report informe (e.g.: 03/030001TM)
	 * @param enXML si es true se devuelve directamente el XML obtenido del OBI, en caso contrario se devuelve el contenido del mismo en RDF
	 * @param schemaAndData si es true devuelve schema y datos en el xml, si es false solo datos.
	 * @return contenido XML del informe ofrecido por el servicio OBI
	 * @throws Exception
	 */
	public static String getReportData(String report, boolean enXML, boolean schemaAndData) throws Exception{
		
		try {
			SAWSessionServiceLocator awsessionservicelocator = new SAWSessionServiceLocator();
			XmlViewServiceLocator  xmlViewServiceLocator = new XmlViewServiceLocator();

			String url_obi = properties.getProperty("OBI_URL");
			SAWSessionServiceSoap m_Session = awsessionservicelocator.getSAWSessionServiceSoap(
					new URL("http://"+url_obi+"/analytics/saw.dll?SoapImpl=nQSessionService"));
			
			// Servicio de consulta del OBI
			XmlViewServiceSoap xmlService = xmlViewServiceLocator.getXmlViewServiceSoap(
					new URL("http://"+url_obi+"/analytics/saw.dll?SoapImpl=xmlViewService"));
	
			// Inicio de sesión en el OBI
			String m_sessionID = m_Session.logon(properties.getProperty("OBI_USER"), properties.getProperty("OBI_PWD"));
	
			// Configuración del informe solicitado
			ReportRef rRef = new ReportRef();
			rRef.setReportPath(properties.getProperty("REPORTS_PATH")+report);
			ReportParams rParams = new ReportParams();
		
			// Obtener el informe
			XMLQueryExecutionOptions xqeo = new XMLQueryExecutionOptions();
			xqeo.setRefresh(false);
			xqeo.setPresentationInfo(true);
			xqeo.setAsync(false);
			
			String ou_format = "SAWRowsetData";
			if(schemaAndData){ ou_format = "SAWRowsetSchemaAndData";}
			else{ ou_format = "SAWRowsetData";}
			
			QueryResults results = xmlService.executeXMLQuery(rRef, XMLQueryOutputFormat.fromString(ou_format), xqeo, rParams, m_sessionID);
			
		
			String xmlResult = results.getRowset();
			report = report.replace("/", "_").substring(1);
			PrintWriter writer = new PrintWriter(properties.getProperty("DOWNLOAD_FILES")+ report + ".xml", "UTF-8");
			writer.println(xmlResult);
			writer.close();
			
			// salir de sesión
			m_Session.logoff(m_sessionID);
			
		
			if (enXML){
				
				return  xmlResult;
			}
			else{
				// convertir el XML en RDF
				return xml2rdf(report,xmlResult);
				//return "todo OK";
			}
			
		
		} catch (Exception e) {
			
			logger.error("No se pudieron obtener los datos del informe "+report,e);
			fallidos.add(report);
			return "No se pudieron obtener los datos del informe "+ report + " - " + e;
			//throw e;
		}
	}

	
	
	
	/**
	 * Convierte el contenido del informe en XML a RDF, mediante la hoja de estilo correspondiente al informe solicitado
	 * @param report informe
	 * @param xml contenido xml del informe
	 * @return contenido del informe en RDF
	 * @throws Exception
	 */
	protected static String xml2rdf(String report, String xml) throws Exception{
		try{
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder builder = dbFactory.newDocumentBuilder();
			Document xmlDoc = builder.parse(new InputSource(new ByteArrayInputStream(xml.getBytes("UTF-8"))));
			File stylesheet = new File(properties.getProperty("XSL_PATH")+report.replaceAll("\\W", "_")+".xsl");
		
			TransformerFactory factory = TransformerFactory.newInstance();
			Source xslt = new StreamSource(stylesheet);
			Transformer transformer = factory.newTransformer(xslt);

			StringWriter outWriter = new StringWriter();
			StreamResult result = new StreamResult( outWriter );
			transformer.transform(new DOMSource(xmlDoc), result);
					
			return outWriter.getBuffer().toString();

		}
		catch(IOException ex){
			logger.error("No se ha encontrado la hoja de estilo "+properties.getProperty("XSL_PATH")+report.replaceAll("\\W", "_")+".xsl",ex);
			throw ex;
		}
		catch(Exception ex){
			logger.error("No se ha podido transformar el XML a RDF ("+report+")",ex);
			throw ex;
		}

	}
	
	
	/**
	 * Crea un XLS y un CSV con la lista de TODOS los informes que hay en la carpeta /shared/IAEST-PUBLICA/Estadistica Loca del servidor OBI
	 * @param ninguno
	 * @return String de confirmación
	 * @throws Exception
	 */
	public static String getReportsList() throws Exception{
		
		try{
			Logger logger = Logger.getLogger("IAESTdescarga");
			
			SAWSessionServiceLocator awsessionservicelocator = new SAWSessionServiceLocator();
			XmlViewServiceLocator  xmlViewServiceLocator = new XmlViewServiceLocator();
			WebCatalogServiceLocator webCatalogServiceLocator = new WebCatalogServiceLocator();
			
			String url_obi = properties.getProperty("OBI_URL");

			
			// Servicio de inicio de sesión del OBI
			SAWSessionServiceSoap m_Session = awsessionservicelocator.getSAWSessionServiceSoap(
					new URL("http://"+url_obi+"/analytics/saw.dll?SoapImpl=nQSessionService"));
			
			//Servicio Web Catalog del OBi
			WebCatalogServiceSoap webCatalogService = webCatalogServiceLocator.getWebCatalogServiceSoap(
					new URL("http://"+url_obi+"/analytics/saw.dll?SoapImpl=webCatalogService"));
			
			// Inicio de sesión en el OBI
			String m_sessionID = m_Session.logon(properties.getProperty("OBI_USER"), properties.getProperty("OBI_PWD"));
	
			//Formato de Fecha
			SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
			
			//Archivo donde vamos a escribir
			String rutaListaInformesOBI = properties.getProperty("LIST_FILE");
			PrintWriter writer = new PrintWriter(rutaListaInformesOBI, "UTF-8");
			//writer.println("COMIENZO");
		
			String rutaListaInformesOBIExcel = properties.getProperty("EXCEL_FILE");
			// create a new Excel file
			FileOutputStream out = new FileOutputStream(properties.getProperty("EXCEL_FILE"));
			// create a new workbook
			Workbook wb = new HSSFWorkbook();
			// create a new sheet
			Sheet s = wb.createSheet();
			// declare a row object reference
			Row r = null;
			// declare a cell object reference
			Cell c = null;
			 
			int rownum = 0;
			int cellnum = 0;
			
			String c_path = properties.getProperty("REPORTS_PATH");
			
			writer.println("CodigoEstadisticaNormal,CodigoEstadistica");
			
			r = s.createRow(rownum);
			rownum++;
			c = r.createCell(0);
			c.setCellValue("CodigoEstadisticaNormal");
			c = r.createCell(1);
			c.setCellValue("CodigoEstadistica");
			try{
				ItemInfo[] arrChilds = webCatalogService.getSubItems(c_path,"*",true,null,m_sessionID);
				
				for(int i = 0; i < arrChilds.length; i++){
					String ct = arrChilds[i].getPath().toString().replace(c_path+"/", "");
					String ct1 = ct.substring(ct.length() - 1);
					String ct2 = ct.substring(ct.length() - 2);
					
					//if(arrChilds[i].getType().toString().equals("Object")){
					if(arrChilds[i].getType().toString().equals("Object") && (ct2.equals("TC") || ct2.equals("TM") || ct2.equals("TP") || ct1.equals("A")) && !(ct.contains("old") || ct.contains("mal") || ct.contains("prueba") || ct.contains("anual") || ct.substring(0,2).equals("06"))){
						writer.println(arrChilds[i].getPath().toString().replace(c_path+"/", "") + "," + arrChilds[i].getPath().toString().replace(c_path+"/", "").replace("/","-"));
						r = s.createRow(rownum);
						rownum++;
						c = r.createCell(0);
						c.setCellValue(arrChilds[i].getPath().toString().replace(c_path+"/", ""));
						c = r.createCell(1);
						c.setCellValue(arrChilds[i].getPath().toString().replace(c_path+"/", "").replace("/","-"));

					}
					
					try{
						ItemInfo[] arrChilds2 = webCatalogService.getSubItems(arrChilds[i].getPath().toString(),"*",true,null,m_sessionID);
						if(arrChilds2 != null){
							for(int x = 0; x < arrChilds2.length; x++){
								ct = arrChilds2[x].getPath().toString().replace(c_path+"/", "");
								ct1 = ct.substring(ct.length() - 1);
								ct2 = ct.substring(ct.length() - 2);
								
								//if(arrChilds2[x].getType().toString().equals("Object")){
								if(arrChilds2[x].getType().toString().equals("Object") && (ct2.equals("TC") || ct2.equals("TM") || ct2.equals("TP") || ct1.equals("A")) && !(ct.contains("old") || ct.contains("mal") || ct.contains("prueba") || ct.contains("anual")  || ct.substring(0,2).equals("06"))){	
									writer.println(arrChilds2[x].getPath().toString().replace(c_path+"/", "") + "," + arrChilds2[x].getPath().toString().replace(c_path+"/", "").replace("/","-"));
									r = s.createRow(rownum);
									rownum++;
									c = r.createCell(0);
									c.setCellValue(arrChilds2[x].getPath().toString().replace(c_path+"/", ""));
									c = r.createCell(1);
									c.setCellValue(arrChilds2[x].getPath().toString().replace(c_path+"/", "").replace("/","-"));
								
								}
								try{
									ItemInfo[] arrChilds3 = webCatalogService.getSubItems(arrChilds2[x].getPath().toString(),"*",true,null,m_sessionID);
									if(arrChilds3 != null){
										for(int z = 0; z < arrChilds3.length; z++){
											ct = arrChilds3[z].getPath().toString().replace(c_path+"/", "");
											ct1 = ct.substring(ct.length() - 1);
											ct2 = ct.substring(ct.length() - 2);
											
											//if(arrChilds3[z].getType().toString().equals("Object")){
											if(arrChilds3[z].getType().toString().equals("Object") && (ct2.equals("TC") || ct2.equals("TM") || ct2.equals("TP") || ct1.equals("A")) && !(ct.contains("old") || ct.contains("mal") || ct.contains("prueba") || ct.contains("anual")|| ct.substring(0,2).equals("06"))){
												writer.println(arrChilds3[z].getPath().toString().replace(c_path+"/", "")+ "," + arrChilds3[z].getPath().toString().replace(c_path+"/", "").replace("/","-"));
												r = s.createRow(rownum);
												rownum++;
												c = r.createCell(0);
												c.setCellValue(arrChilds3[z].getPath().toString().replace(c_path+"/", ""));
												c = r.createCell(1);
												c.setCellValue(arrChilds3[z].getPath().toString().replace(c_path+"/", "").replace("/","-"));
												
											}
											
											
											try{
												ItemInfo[] arrChilds4 = webCatalogService.getSubItems(arrChilds3[z].getPath().toString(),"*",true,null,m_sessionID);
												if(arrChilds4 != null){
													for(int y = 0; y < arrChilds4.length; y++){
														ct = arrChilds4[y].getPath().toString().replace(c_path+"/", "");
														ct1 = ct.substring(ct.length() - 1);
														ct2 = ct.substring(ct.length() - 2);

														//if(arrChilds4[y].getType().toString().equals("Object")){
														if(arrChilds4[y].getType().toString().equals("Object") && (ct2.equals("TC") || ct2.equals("TM") || ct2.equals("TP") || ct1.equals("A")) && !(ct.contains("old") || ct.contains("mal") || ct.contains("prueba") || ct.contains("anual")|| ct.substring(0,2).equals("06"))){
															writer.println(arrChilds4[y].getPath().toString().replace(c_path+"/", "") + "," + arrChilds4[y].getPath().toString().replace(c_path+"/", "").replace("/","-"));
															r = s.createRow(rownum);
															rownum++;
															c = r.createCell(0);
															c.setCellValue(arrChilds4[y].getPath().toString().replace(c_path+"/", ""));
															c = r.createCell(1);
															c.setCellValue(arrChilds4[y].getPath().toString().replace(c_path+"/", "").replace("/","-"));
														
														}
													}	
												}
											}catch(Exception e){
												//logger.error("Error en 4er nivel. "+ e);
											}
											
											
										}	
									}
								}catch(Exception e){
									//logger.error("Error en 3er nivel. "+ e);
								}
							}
						
						}
					}catch(Exception e){
						//logger.error("Error en 2o nivel. "+ e);
					}
					
				}
			}
			catch(Exception e){
				logger.error("Error en 1er nivel. "+ e);
			}
			writer.close();
			
			wb.write(out);
			out.close();
			return "Fichero creado en : " + rutaListaInformesOBI + "<br />Fichero creado en : " +  rutaListaInformesOBIExcel;
			
			
		}
		catch (Exception e){
		logger.error("No se pudo recuperar la lista. "+ e);
		throw e;
		}
	
	}

	/**
	 * Ejecuta getReportData de todas las rutas indicadas en el archivo EXCEL "$DOWNLOAD_FILES", 
	 * descargando todos los reports en formato XMl
	 * @param ninguno
	 * @return String de confirmación
	 * @throws Exception
	 */
	public static String getAllReports() throws Exception{ 
		try{
			logger.info("-------- COMIENZA getAllReports : ");
			String rutaFicheros = properties.getProperty("DOWNLOAD_FILES");
			
			String file = properties.getProperty("EXCEL_FILE");
			
			POIFSFileSystem fs = new POIFSFileSystem(new FileInputStream(file));
		    HSSFWorkbook wb = new HSSFWorkbook(fs);
		    HSSFSheet sheet = wb.getSheetAt(0);
		    HSSFRow row;
		    HSSFCell cell;
		    int rows; // No of rows
		    rows = sheet.getPhysicalNumberOfRows();

		    int cols = 1; // No of columns
		    int tmp = 0;

		    
		    for(int r = 0; r < rows; r++) {
		        row = sheet.getRow(r);
		        if(row != null) {
		            for(int c = 0; c < cols; c++) {
		                cell = row.getCell(c);
		                if(cell != null) {
		                	
		                	/*
		                	 * Vamos a limitar a descargar los que acaben en TC, TM y TP
		                	 * y no contengan "old", "mal", "prueba", "test" en el nombre.
		                	 * */
		               	 	String ct = cell.toString();
		                	//String[] si = {"TC","TM","TP"};
		               	 	//if((ct.contains("TC") || ct.contains("TM") || ct.contains("TP")) && !(ct.contains("old") || ct.contains("mal") || ct.contains("prueba") || ct.contains("anual"))){
		               	 		//logger.info("Creando : " + cell );
		               	 		getReportData("/" + ct, true, false);
		               	 	//}
		                }
		            }
		        }
		    }
		    repiteFallidos(fallidos);
			return "Ficheros creado en " +  rutaFicheros;
		}
		catch(Exception e){
			logger.error("Se ha producido un error en getAllReports : " + e);
			throw e;
		}
		
	}
	
	/**
	 * Hace una pasada por los reports que han fallado al descargar, por error 503 por ejemplo
	 * @param fallidos : array con el nombre de los reports que han fallado.*
	 * @return : nada
	 */
	public static void repiteFallidos(ArrayList<String>  fallidos) throws Exception{
		logger.info("---- Comienza repiteFallidos ----");
		for( String nombreReport : fallidos ){
			try{
				logger.info("-- Repite fallido: " + nombreReport);
				getReportData(nombreReport, true, false);
			}
			catch(Exception e){
				logger.error("Ha fallado repiteFallidos: " + nombreReport + "  :  "+ e);
			}
		}
	}
	
	/**
	 * Testeo de la clase 
	 * @param args no se usa
	 */
	public static void main(String[] args) {
		initProperties("");
		try{
			//System.out.println(getReportData("03/030001TM", false,false).substring(0, 5000));		

		} catch (Exception e) {
			e.printStackTrace();
		}


	}

}
