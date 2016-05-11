package com.localidata.extract;

import java.io.File;
import java.io.IOException;
import java.net.SocketTimeoutException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

import com.localidata.generic.Constants;
import com.localidata.generic.GoogleDriveAPI;
import com.localidata.generic.Prop;
import com.localidata.util.Cookies;
import com.localidata.util.Jdbcconnection;
import com.localidata.util.Utils;

/**
 * 
 * @author Localidata
 */
public class GenerateCSV {
	private final static Logger log = Logger.getLogger(GenerateCSV.class);
	
	private String urlsDirectoryString = "";
	
	private String outputFilesDirectoryString = "";
	private HashMap<String, String> hashCode = new HashMap<>();
	
	private HashMap<String, String> hashCodeNew = new HashMap<>();
	
	private HashMap<String, String> idDescription = new HashMap<>();
	
	
	private List<String> changes = new ArrayList<String>();
	
	private List<String> news = new ArrayList<String>();
	
	private GoogleDriveAPI drive = null;
	

		urlsDirectoryString = urls;
		outputFilesDirectoryString = outputFiles;
		drive = new GoogleDriveAPI();
		drive.init();
		log.info("Generando el fichero InformesEstadisticaLocal-URLs.csv");
		/*OBIAragon.initProperties("service.properties");
		try {
			OBIAragon.getReportsList();
		} catch (Exception e) {
			e.printStackTrace();
		}*/
		/*try {
			Jdbcconnection.main(null);
		} catch (Exception e) {
			e.printStackTrace();
		}*/
		
		log.info("fin de la generación del fichero InformesEstadisticaLocal-URLs.csv");
	}
	
	public void extractFiles(List<String> csvLines){
		
		log.info("init extractFiles");
		Cookies cookies = new Cookies();
		File urlsDirectoryFile = new File(urlsDirectoryString);
		HashMap<String[], Integer> numErrorFiles = new HashMap<>();
		HashMap<String[], String> errorFiles = new HashMap<>();
		String[] valores = null;
		String content = null;
		try {
			Map<String, String> headers = new HashMap<String, String>();
			headers.put("User-Agent", "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_10_2) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/46.0.2490.86 Safari/537.36");
			headers.put("Cookie","sawU=granpublico; ORA_BIPS_LBINFO=153c4c924b8; ORA_BIPS_NQID=k8vgekohfuquhdg71on5hjvqbcorcupbmh4h3lu25iepaq5izOr07UFe9WiFvM3; __utma=263932892.849551431.1443517596.1457200753.1458759706.17; __utmc=263932892; __utmz=263932892.1456825145.15.6.utmcsr=alzir.dia.fi.upm.es|utmccn=(referral)|utmcmd=referral|utmcct=/kos/iaest/clase-vivienda-agregado");
			headers.put("content-type","text/csv; charset=utf-8");
			Utils.processURLGet("http://bi.aragon.es/analytics/saw.dll?Go&path=/shared/IAEST-PUBLICA/Estadistica%20Local/03/030018TC&Action=Download&Options=df&NQUser=granpublico&NQPassword=granpublico","",headers,cookies,"ISO-8859-1");
			if(csvLines==null)
				csvLines = FileUtils.readLines(urlsDirectoryFile, "UTF-8");
			
			try{
				for (int h = 1; h < csvLines.size(); h++) {
					String line=csvLines.get(h);
					valores = line.split(",");
					log.info("Descargando csv "+valores[1]+" "+(h+1)+" de "+csvLines.size());
					content = Utils.processURLGet("http://bi.aragon.es/analytics/saw.dll?Go&path=/shared/IAEST-PUBLICA/Estadistica%20Local/"+valores[0]+"&Action=Download&Options=df&NQUser=granpublico&NQPassword=granpublico","",headers,cookies,"ISO-8859-1");
					if(Utils.v(content)){
						content=content.replace(new String(Character.toChars(0)), "");
						content=content.replace("ÿþ", "");
						content = Utils.ISO88591toUTF8(content);
						if(!content.contains("<!DOCTYPE HTML") && !content.contains("<HTML>") && !content.contains("<DOCTYPE HTML>") && !content.contains("<HTML>") && !content.contains("<div>") && !content.contains("<HTML>")){
							Utils.stringToFile(content,new File(outputFilesDirectoryString+File.separator+valores[1]+".csv"));
							String hash = Utils.generateHash(content);
							hashCode.put(valores[1], hash);
						}else if(!content.contains("Se ha excedido el n") && !content.contains("Ruta de acceso no encontrada")){
							numErrorFiles.put(valores, new Integer(0));
							errorFiles.put(valores,content);
						}else{
							informeErrores(valores[1],content);
						}
					}else{
						numErrorFiles.put(valores, new Integer(0));
						errorFiles.put(valores,content);
					}
					
					Thread.sleep(1000);
				}
			}catch(SocketTimeoutException e2){
				numErrorFiles.put(valores, new Integer(0));
				errorFiles.put(valores,content);
				e2.printStackTrace();
				log.error("Error al descargar "+valores[1],e2);
			}
			
			int j=0;
			int totalElements=numErrorFiles.size();
			
			try{
				Iterator<String[]> iterator = numErrorFiles.keySet().iterator();
				while(j<totalElements){
					valores = iterator.next();
					Integer numErrors = numErrorFiles.get(valores);
					if(numErrors<5 && numErrors!=-1){
						content = Utils.processURLGet("http://bi.aragon.es/analytics/saw.dll?Go&path=/shared/IAEST-PUBLICA/Estadistica%20Local/"+valores[0]+"&Action=Download&Options=df&NQUser=granpublico&NQPassword=granpublico","",headers,cookies,"ISO-8859-1");
						if(Utils.v(content)){
							content=content.replace(new String(Character.toChars(0)), "");
							content=content.replace("ÿþ", "");
							content = Utils.ISO88591toUTF8(content);
							if(!content.contains("<!DOCTYPE HTML") && !content.contains("<HTML>") && !content.contains("<DOCTYPE HTML>") && !content.contains("<HTML>") && !content.contains("<div>") && !content.contains("<HTML>")){
								log.info("content "+content);
								log.info("------------------------------------------------------");
								Utils.stringToFile(content,new File(outputFilesDirectoryString+File.separator+valores[1]+".csv"));
								String hash = Utils.generateHash(content);
								hashCode.put(valores[1], hash);
								numErrorFiles.put(valores,new Integer(-1));
								errorFiles.remove(valores);
							}else if(!content.contains("Se ha excedido el n") && !content.contains("Ruta de acceso no encontrada")){
								numErrorFiles.put(valores,new Integer(-1));
							}else {
								log.error("Informe "+valores[1]+" imposible de descargar intento "+(numErrors+1));
								numErrorFiles.put(valores,++numErrors);
							}
						}
						Thread.sleep(1000);
					}
					if(!iterator.hasNext()){
						iterator = numErrorFiles.keySet().iterator();
						j++;
					}
				}
			}catch(SocketTimeoutException e2){
				log.error("Error al descargar "+valores[1],e2);
			}
			
		for (String[] val : errorFiles.keySet()) {
			String cont = errorFiles.get(valores);
			informeErrores(val[1],cont);
		}
		
			
		} catch (IOException e) {
			e.printStackTrace();
			log.error("Error desconocido",e);
		} catch (Exception e) {
			e.printStackTrace();
			log.error("Error desconocido",e);
		}
		log.info("end extractFiles");
	}
	
	public void extractFilesWithChanges(){
		log.info("init extractFilesWithChanges");
		extractHashCode();
		List<String> all = new ArrayList<>();
		all.add("cabecera");
		Cookies cookies = new Cookies();
		File urlsDirectoryFile = new File(urlsDirectoryString);
		HashMap<String[], Integer> numErrorFiles = new HashMap<>();
		HashMap<String[], String> errorFiles = new HashMap<>();
		String[] valores = null;
		String content = null;
		try {
			Map<String, String> headers = new HashMap<String, String>();
			headers.put("User-Agent", "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_10_2) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/46.0.2490.86 Safari/537.36");
			headers.put("Cookie","sawU=granpublico; ORA_BIPS_LBINFO=153c4c924b8; ORA_BIPS_NQID=k8vgekohfuquhdg71on5hjvqbcorcupbmh4h3lu25iepaq5izOr07UFe9WiFvM3; __utma=263932892.849551431.1443517596.1457200753.1458759706.17; __utmc=263932892; __utmz=263932892.1456825145.15.6.utmcsr=alzir.dia.fi.upm.es|utmccn=(referral)|utmcmd=referral|utmcct=/kos/iaest/clase-vivienda-agregado");
			headers.put("content-type","text/csv; charset=utf-8");
			Utils.processURLGet("http://bi.aragon.es/analytics/saw.dll?Go&path=/shared/IAEST-PUBLICA/Estadistica%20Local/03/030018TC&Action=Download&Options=df&NQUser=granpublico&NQPassword=granpublico","",headers,cookies,"ISO-8859-1");
			List<String> csvLines = FileUtils.readLines(urlsDirectoryFile, "UTF-8");
			
			try{
				for (int h = 1; h < csvLines.size(); h++) {
					String line=csvLines.get(h);
					valores = line.split(",");
					valores[0]=valores[0].replaceAll("\"", "");
					valores[1]=valores[1].replaceAll("\"", "");
					valores[2]=valores[2].replaceAll("\"", "");
					idDescription.put(valores[1],valores[2]);
					content = Utils.processURLGet("http://bi.aragon.es/analytics/saw.dll?Go&path=/shared/IAEST-PUBLICA/Estadistica%20Local/"+valores[0]+"&Action=Download&Options=df&NQUser=granpublico&NQPassword=granpublico","",headers,cookies,"ISO-8859-1");
					if(Utils.v(content)){
						content=content.replace(new String(Character.toChars(0)), "");
						content=content.replace("ÿþ", "");
						content = Utils.ISO88591toUTF8(content);
						String hash = Utils.generateHash(content);
						boolean safeFile=false;
						if(hashCode.get(valores[1])==null){
							news.add(valores[1]);
							all.add(valores[0]+","+valores[1]);
							log.info("Se ha encontrado un nuevo cubo de datos: "+valores[1]+" "+(h+1)+" de "+csvLines.size());
							safeFile=true;
						}else if(!hashCode.get(valores[1]).equals(hash)){
							changes.add(valores[1]);
							all.add(valores[0]+","+valores[1]);
							log.info("Se han encontrado cambios en el cubo de datos "+valores[1]+" "+(h+1)+" de "+csvLines.size());
							safeFile=true;
						}else{
							log.info("No hay cambios en el cubo de datos "+valores[1]+" "+(h+1)+" de "+csvLines.size());
						}
						if(safeFile){
							if(!content.contains("<!DOCTYPE HTML") && !content.contains("<HTML>") && !content.contains("<DOCTYPE HTML>") && !content.contains("<HTML>") && !content.contains("<div>") && !content.contains("<HTML>")){
								Utils.stringToFile(content,new File(outputFilesDirectoryString+File.separator+valores[1]+".csv"));
								hashCodeNew.put(valores[1], hash);
								log.info("Descargado csv "+valores[1]);
							}else if(!content.contains("Se ha excedido el n") && !content.contains("Ruta de acceso no encontrada")){
								numErrorFiles.put(valores, new Integer(0));
								errorFiles.put(valores,content);
								news.remove(valores[1]);
								changes.remove(valores[1]);
								log.info("El csv "+valores[1]+" no se pudo descargar");
							}else{
								informeErrores(valores[1],content);
								news.remove(valores[1]);
								changes.remove(valores[1]);
								log.info("El csv "+valores[1]+" no se pudo descargar");
							}
						}
					}
				}
			}catch(IOException e2){
				numErrorFiles.put(valores, new Integer(0));
				errorFiles.put(valores,content);
				e2.printStackTrace();
				log.error("Error al descargar "+valores[1],e2);
			}
			
			int j=0;
			int totalElements=numErrorFiles.size();
			
			try{
				Iterator<String[]> iterator = numErrorFiles.keySet().iterator();
				while(j<totalElements){
					valores = iterator.next();
					Integer numErrors = numErrorFiles.get(valores);
					if(numErrors<5 && numErrors!=-1){
						content = Utils.processURLGet("http://bi.aragon.es/analytics/saw.dll?Go&path=/shared/IAEST-PUBLICA/Estadistica%20Local/"+valores[0]+"&Action=Download&Options=df&NQUser=granpublico&NQPassword=granpublico","",headers,cookies,"ISO-8859-1");
						if(Utils.v(content)){
							content=content.replace(new String(Character.toChars(0)), "");
							content=content.replace("ÿþ", "");
							content = Utils.ISO88591toUTF8(content);
							if(!content.contains("<!DOCTYPE HTML") && !content.contains("<HTML>") && !content.contains("<DOCTYPE HTML>") && !content.contains("<HTML>") && !content.contains("<div>") && !content.contains("<HTML>")){
								log.info("content "+content);
								log.info("------------------------------------------------------");
								Utils.stringToFile(content,new File(outputFilesDirectoryString+File.separator+valores[1]+".csv"));
								String hash = Utils.generateHash(content);
								boolean safeFile=false;
								if(hashCode.get(valores[1])==null){
									news.add(valores[1]);
									all.add(valores[0]+","+valores[1]);
									safeFile=true;
									log.info("Se ha encontrado un nuevo cubo de datos: "+valores[1]);
								}else if(!hashCode.get(valores[1]).equals(hash)){
									changes.add(valores[1]);
									all.add(valores[0]+","+valores[1]);
									safeFile=true;
									log.info("Se han encontrado cambios en el cubo de datos "+valores[1]);
								}else{
									log.info("No hay cambios en el cubo de datos "+valores[1]);
								}
								if(safeFile){
									if(!content.contains("<!DOCTYPE HTML") && !content.contains("<HTML>") && !content.contains("<DOCTYPE HTML>") && !content.contains("<HTML>") && !content.contains("<div>") && !content.contains("<HTML>")){
										Utils.stringToFile(content,new File(outputFilesDirectoryString+File.separator+valores[1]+".csv"));
										hashCodeNew.put(valores[1], hash);
										log.info("Descargado csv "+valores[1]);
									}else if(!content.contains("Se ha excedido el n") && !content.contains("Ruta de acceso no encontrada")){
										numErrorFiles.put(valores, new Integer(0));
										errorFiles.put(valores,content);
										news.remove(valores[1]);
										changes.remove(valores[1]);
										log.info("El csv "+valores[1]+" no se pudo descargar");
									}else{
										informeErrores(valores[1],content);
										news.remove(valores[1]);
										changes.remove(valores[1]);
										log.info("El csv "+valores[1]+" no se pudo descargar");
									}
								}
								numErrorFiles.put(valores,new Integer(-1));
								errorFiles.remove(valores);
							}else if(!content.contains("Se ha excedido el n") && !content.contains("Ruta de acceso no encontrada")){
								numErrorFiles.put(valores,new Integer(-1));
							}else {
								log.error("Informe "+valores[1]+" imposible de descargar intento "+(numErrors+1));
								numErrorFiles.put(valores,++numErrors);
							}
						}
						Thread.sleep(1000);
					}
					if(!iterator.hasNext()){
						iterator = numErrorFiles.keySet().iterator();
						j++;
					}
				}
			}catch(IOException e2){
				log.error("Error al descargar "+valores[1],e2);
			}
			
		for (String[] val : errorFiles.keySet()) {
			String cont = errorFiles.get(valores);
			informeErrores(val[1],cont);
		}
		
		} catch (Exception e) {
			e.printStackTrace();
			log.error("Error desconocido",e);
		}
		log.info("end extractFilesWithChanges");
	}
	/*
	public void updateDimensionsMesures(){
		log.info("init updateDimensionsMesures");
		//Lectura de la dimension o medida curada
		////Se genera HashMap<nombre dimension/medida, lista valores>
		readDimensionsMesure();
		
		//Comprobación de si está el cada valor está en los valores
		////Se genera una lista con los valores del CSV
		File outputFilesDirectoryFile = new File(outputFilesDirectoryString);
		Collection<File> listOutput = FileUtils.listFiles(outputFilesDirectoryFile,
				extensions, true);
		for (File file : listOutput) {
			HashMap<String,List<String>> mapColumns = extractColumns(file);
			//En caso de no estar se incluirán los nuevos valores
			////Con la clase ListUtils() comprobamos si hay valores diferentes
			////En caso de haberlos los agregamos al fichero de curadas
			
			for (String header : mapColumns.keySet()) {
				
				List<String> curada = dimMesCurada.get(Utils.dimensionStrongClean(header+".txt"));
				List<String> column = mapColumns.get(header);
				List<String> subtract = new ArrayList<>();
					
				if(ListUtils.subtract(column, curada).size()>0){
					subtract=ListUtils.subtract(column, curada);
					subtract.addAll(curada);
					File dim = new File(dimensionsDirectoryString+File.separator+header+".txt");
					File mes = new File(mesuresDirectoryString+File.separator+header+".txt");
					try {
						if(dim.exists())
							FileUtils.writeLines(dim, subtract);
						else if(mes.exists())
							FileUtils.writeLines(mes, subtract);
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		}
		log.info("end updateDimensionsMesures");
	}*/
	/*
	private HashMap<String,List<String>> extractColumns(File file) {
		HashMap<String,List<String>> result = new HashMap<>();
		try {
			List<String> lines = FileUtils.readLines(file, "UTF-8");
			Object[] header = null;
			Object[] cells = null;
			for (int row = 0; row < lines.size(); row++) {
				String line = lines.get(row);
				if(row==0)
					header = Utils.split(line, "\t");
				else{
					cells = Utils.split(line, "\t");
					for (int column = 0; column < cells.length; column++) {
						String cell = (String)cells[column];
						List<String> list = result.get(header[column]);
						if(list==null){
							list = new ArrayList<String>();
						}
						if(!list.contains(cell)){
							Utils.dimensionStrongClean(cell.trim());
							list.add(Utils.cleanSpaceAndCaracters(cell));
							result.put((String)header[column],list);
						}
					}
				}
			}

		} catch (IOException e) {
			e.printStackTrace();
		}
		return result;
	}
	*/
	/*
	private void readDimensionsMesure(){
		File dimensionsDirectoryFile = new File(dimensionsDirectoryString);
		Collection<File> listdimensions = FileUtils.listFiles(dimensionsDirectoryFile,
				extensions, true);
		for (File file : listdimensions) {
			try {
				List<String> lines = FileUtils.readLines(file, "UTF-8");
				dimMesCurada.put(Utils.dimensionWeakClean(file.getName()),lines);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		File mesuresDirectoryFile = new File(mesuresDirectoryString);
		Collection<File> listMesures = FileUtils.listFiles(mesuresDirectoryFile,
				extensions, true);
		for (File file : listMesures) {
			List<String> lines;
			try {
				lines = FileUtils.readLines(file, "UTF-8");
				dimMesCurada.put(Utils.dimensionWeakClean(file.getName()),lines);
			} catch (IOException e) {
				e.printStackTrace();
			}
			
		}
	}*/
	
	/*
	public void generateDimensiones() {
		
		File urlsDirectoryFile = new File(outputFilesDirectoryString);
		Collection<File> listCSV = FileUtils.listFiles(urlsDirectoryFile,
				extensions, true);
		HashMap<String,HashSet<String>> dimensions = new HashMap<>();
		String[] heads = null;
		for (File file : listCSV) {
			try {
				List<String> csvLines = FileUtils.readLines(file, "UTF-8");
				String firstLine = csvLines.get(0);
				heads = firstLine.split("\t");
				
				for (int h = 1; h < csvLines.size(); h++) {
					String line = csvLines.get(h);
//					String[] cells = line.split("\t");
					Object[] cells = Utils.split(line,"\t");
					for (int i = 0; i < heads.length; i++) {
						String value = (String) cells[i];
						if(dimensions.get(heads[i])!=null){
							HashSet<String> values = dimensions.get(heads[i]);
							values.add(value);
						}else{
							HashSet<String> values = new HashSet<>();
							values.add(value);
							dimensions.put(heads[i], values);
						}
					}
					
				}
				int column=0;
				
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		for (String key : dimensions.keySet()) {
			try {
				File file = null;
				if(dimensions.get(key).size()<20){
					log.info(key+" PosiblesDimensionesMenos20Valores"); 
					file = new File(dimensionsDirectoryString+File.separator+"PosiblesDimensionesMenos20Valores"+File.separator+key);
					FileUtils.writeStringToFile(file, "");
				}else if(dimensions.get(key).size()<50){
					log.info(key+" Undecided"); 
					file = new File(dimensionsDirectoryString+File.separator+"Undecided"+File.separator+key);
					FileUtils.writeStringToFile(file, "");
				}else{
					log.info(key+" PosiblesMeasures"); 
					file = new File(dimensionsDirectoryString+File.separator+"PosiblesMeasures"+File.separator+key);
					FileUtils.writeStringToFile(file, "");
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}*/
	
	public void extractFilesPrueba(){
		
		File file = new File(""+Prop.fileHashCSV+"."+Constants.CSV);
		try {
			List<String> hashLines = FileUtils.readLines(file, "UTF-8");
			for (String line : hashLines) {
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	
	public void generateHashCode(List<String> result){
		
		File file = new File(Prop.fileHashCSV+"."+Constants.CSV);
		String hashCodeFile="";

		for (String key : hashCode.keySet()) {
			String hash="";
			if(result.contains(key))
				hash=hashCodeNew.get(key);
			else{
				hash=hashCode.get(key);
			}
			hashCodeFile += key + "," + hash + "\n";
			
		}
		try {
			Utils.stringToFile(hashCodeFile, file);
			drive.updateFile(Prop.fileHashCSV,file,"text/csv");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	protected void informeErrores(String id, String content){
		if(content!=null && content.contains("Se ha excedido el n")){
			File file = new File(Prop.fileErrorBig);
			Utils.stringToFileAppend(id+".csv"+System.lineSeparator(), file);
		}else if(content!=null && content.contains("Ruta de acceso no encontrada")){
			File file = new File(Prop.fileErrorNotFound);
			Utils.stringToFileAppend(id+".csv"+System.lineSeparator(), file);
		}else{
			File file = new File(Prop.fileErrorGeneric);
			Utils.stringToFileAppend(id+".csv"+System.lineSeparator(), file);
		}
	}
	
	protected void extractHashCode(){
		
		drive.downloadFile("", Prop.fileHashCSV, Constants.CSV);
		File file = new File(""+Prop.fileHashCSV+"."+Constants.CSV);
		try {
			List<String> hashLines = FileUtils.readLines(file, "UTF-8");
			for (String line : hashLines) {
				String[] valores = line.split(",");
				hashCode.put(valores[0], valores[1]);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private void backup() {
		log.debug("Init backup");
		log.info("Comienza a hacerse el backup");
		File outputDirectoryFile = new File(outputFilesDirectoryString);
		File hashCSVFile = new File(Prop.fileHashCSV);
		File errorBigFile = new File(Prop.fileErrorBig);
		File errorNotFoundFile = new File(Prop.fileErrorNotFound);
		File errorGenericFile = new File(Prop.fileErrorGeneric);
		SimpleDateFormat formatFullDate = new SimpleDateFormat("yyyyMMdd");
		File copyDirectoryFile =  null;
		File copyHashCSVFile =  null;
		File copyErrorBigFile =  null;
		File copyErrorNotFoundFile =  null;
		File copyErrorGenericFile  =  null;
		if (outputDirectoryFile.exists()) {
			
			String copy = outputFilesDirectoryString + "_"
					+ formatFullDate.format(new Date());
			copyDirectoryFile = new File(copy);
			int aux = 1;
			while (copyDirectoryFile.exists()) {
				copyDirectoryFile = new File(copy + "_" + aux++);
			}
		}
		if (hashCSVFile.exists()) {
			String s2 = Prop.fileHashCSV+ "_"
					+ formatFullDate.format(new Date());
			copyHashCSVFile = new File(s2);
			int aux = 1;
			while (copyHashCSVFile.exists()) {
				copyHashCSVFile = new File(s2 + "_" + aux++);
			}
		}
		if (errorBigFile.exists()) {
			String s3 = Prop.fileErrorBig+ "_"
					+ formatFullDate.format(new Date());
			copyErrorBigFile = new File(s3);
			int aux = 1;
			while (copyErrorBigFile.exists()) {
				copyErrorBigFile = new File(s3 + "_" + aux++);
			}
		}
		if (errorNotFoundFile.exists()) {
			String s4 = Prop.fileErrorNotFound+ "_"
					+ formatFullDate.format(new Date());
			copyErrorNotFoundFile = new File(s4);
			int aux = 1;
			while (copyErrorNotFoundFile.exists()) {
				copyErrorNotFoundFile = new File(s4 + "_" + aux++);
			}
		}
		if (errorGenericFile.exists()) {
			String s5 = Prop.fileErrorGeneric+ "_"
					+ formatFullDate.format(new Date());
			copyErrorGenericFile = new File(s5);
			int aux = 1;
			while (copyErrorGenericFile.exists()) {
				copyErrorGenericFile = new File(s5 + "_" + aux++);
			}
		}
			try {
				if(copyDirectoryFile!=null)
					FileUtils.moveDirectoryToDirectory(outputDirectoryFile,
						copyDirectoryFile, true);
				if(copyHashCSVFile!=null)
					FileUtils.copyFile(hashCSVFile, copyHashCSVFile);
				if(copyErrorBigFile!=null)
					FileUtils.copyFile(errorBigFile, copyErrorBigFile);
				if(copyErrorNotFoundFile!=null)
					FileUtils.copyFile(errorNotFoundFile, copyErrorNotFoundFile);
				if(copyErrorGenericFile!=null)
					FileUtils.copyFile(errorGenericFile, copyErrorGenericFile);

			} catch (IOException e) {
				log.error("Error haciendo backup", e);
			}
		
		log.info("Finaliza de hacerse el backup");
		log.debug("End backup");
	}
	
	
	
	public List<String> getChanges() {
		return changes;
	}

	public void setChanges(List<String> changes) {
		this.changes = changes;
	}

	public List<String> getNews() {
		return news;
	}

	public void setNews(List<String> news) {
		this.news = news;
	}

	public HashMap<String, String> getIdDescription() {
		return idDescription;
	}

	public void setIdDescription(HashMap<String, String> idDescription) {
		this.idDescription = idDescription;
	}

	public static void main(String[] args) {
		if ((log == null) || (log.getLevel() == null))
			PropertyConfigurator.configure("log4j.properties");
		if (args.length == 3) {
			log.info("Start process");
			Prop.loadConf();
			GenerateCSV app = new GenerateCSV(args[1],args[2]/*,args[3],args[4]*/);
			app.backup();
			if(args[0].equals("update")){
				app.extractFilesWithChanges();
			}else{
				app.extractFiles(null);
			}
			log.info("Finish process");
		} else {
			log.info("Se deben de pasar dos parámetros: ");
			
		}

	}

	

}
