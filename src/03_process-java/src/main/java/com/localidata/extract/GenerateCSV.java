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
	private String urlsFileString = "";
	private String outputFilesDirectoryString = "";
	private HashMap<String, String> hashCodeOld = new HashMap<>();
	private HashMap<String, String> hashCodeNew = new HashMap<>();
	private HashMap<String, String> idDescription = new HashMap<>();
	private List<String> changes = new ArrayList<String>();
	private List<String> news = new ArrayList<String>();

	public GenerateCSV(String urls, String outputFiles) {
		urlsFileString = urls;
		outputFilesDirectoryString = outputFiles;
		log.info("Generando el fichero InformesEstadisticaLocal-URLs.csv");
		try {
			Jdbcconnection.main(null);
		} catch (Exception e) {
			log.error("Error generando informe bbdd iaest",e);
		}

		log.info("fin de la generación del fichero InformesEstadisticaLocal-URLs.csv");
	}

	public void extractFiles() {
		log.info("init extractFilesWithChanges");
		extractHashCode();
		List<String> all = new ArrayList<>();
		all.add("cabecera");
		Cookies cookies = new Cookies();
		File urlsFile = new File(urlsFileString);
		HashMap<String[], Integer> numErrorFiles = new HashMap<>();
		HashMap<String[], String> errorFiles = new HashMap<>();
		String[] valores = null;
		String content = null;
		try {
			Map<String, String> headers = new HashMap<String, String>();
			headers.put("User-Agent", "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_10_2) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/46.0.2490.86 Safari/537.36");
			headers.put("Cookie", "sawU=" + Prop.sawUiAragonBiAragon + "; ORA_BIPS_LBINFO=" + Prop.oraBipsLbinfoBiAragon + "; ORA_BIPS_NQID=" + Prop.oraBipsNqidBiAragon + "; __utma=" + Prop.utmaBiAragon + "; __utmc=" + Prop.utmcBiAragon + "; __utmz=" + Prop.utmzBiAragon);
			headers.put("content-type", "text/csv; charset=ISO-8859-1");
			Utils.processURLGet(Prop.urlBiAragon + Prop.initialDataCube + "&Action=Download&Options=df&NQUser=" + Prop.nqUserBiAragon + "&NQPassword=" + Prop.nqPasswordBiAragon , "", headers, cookies, "ISO-8859-1");
			List<String> csvLines = FileUtils.readLines(urlsFile, "UTF-8");

			
			for (int h = 1; h < csvLines.size(); h++) {
				try {
					String line = csvLines.get(h);
					valores = line.split(",");
					valores[0] = valores[0].replaceAll("\"", "");
					valores[1] = valores[1].replaceAll("\"", "");
					valores[2] = valores[2].replaceAll("\"", "");
					idDescription.put(valores[1], valores[2]);
					content = Utils.processURLGet(Prop.urlBiAragon + valores[0] + "&Action=Download&Options=df&NQUser=" + Prop.nqUserBiAragon + "&NQPassword=" + Prop.nqPasswordBiAragon , "", headers, cookies, "ISO-8859-1");
					if (Utils.v(content)) {
						content = cleanAndTransform(content);
							String hash = Utils.generateHash(content);
							processContentFile(all, numErrorFiles, errorFiles, valores, content, hash);
					}
				} catch (Exception e2) {
					numErrorFiles.put(valores, new Integer(0));
					errorFiles.put(valores, content);
					String valor = valores.length>0 ? valores[1] : "";
					log.error("Error al descargar " + valor, e2);
				}
			}

			int j = 0;
			int totalElements = numErrorFiles.size();
			log.info("Elementos totales " + totalElements);

			try {
				Iterator<String[]> iterator = numErrorFiles.keySet().iterator();
				while (j < totalElements) {
					valores = iterator.next();
					Integer numErrors = numErrorFiles.get(valores);
					boolean sucess = false;
					while (numErrors < 5 && numErrors != -1 && sucess ) {
						log.info("Intento "+numErrors+" del csv "+valores[0]);
						content = Utils.processURLGet(Prop.urlBiAragon + valores[0] + "&Action=Download&Options=df&NQUser=" + Prop.nqUserBiAragon + "&NQPassword=" + Prop.nqPasswordBiAragon , "", headers, cookies, "ISO-8859-1");
						if (Utils.v(content)) {
							content = cleanAndTransform(content);
							if (!content.contains(Constants.errorDoctypeHtml1) && !content.contains(Constants.errorHtml) && !content.contains(Constants.errorDoctypeHtml2) && !content.contains(Constants.errorDiv) && !content.contains(Constants.errorNingunaFila)) {
								Utils.stringToFile(content, new File(outputFilesDirectoryString + File.separator + valores[1] + ".csv"));
								String hash = Utils.generateHash(content);
								processContentFile(all, numErrorFiles, errorFiles, valores, content, hash);
								numErrorFiles.put(valores, new Integer(-1));
								errorFiles.remove(valores);
								sucess=true;
							} else if (!content.contains(Constants.errorExcedidoN) && !content.contains(Constants.errorRutaNoEncontrada) && !content.contains(Constants.errorNingunaFila)) {
								log.error("Informe " + valores[1] + " imposible de descargar intento " + (numErrors + 1));
								numErrorFiles.put(valores, new Integer(-1));
							} else {
								log.error("Informe " + valores[1] + " imposible de descargar intento " + (numErrors + 1));
								numErrorFiles.put(valores, ++numErrors);
							}
						}
						Thread.sleep(1000);
					}
					if (!iterator.hasNext()) {
						iterator = numErrorFiles.keySet().iterator();
						j++;
					}
				}
			} catch (Exception e2) {
				log.error("Error al descargar " + valores[1], e2);
			}

			for (String[] val : errorFiles.keySet()) {
				String cont = errorFiles.get(valores);
				informeErrores(val[1], cont);
			}

		} catch (Exception e) {
			log.error("Error desconocido", e);
		}
		log.info("end extractFilesWithChanges");
	}

	private void processContentFile(List<String> all, HashMap<String[], Integer> numErrorFiles, HashMap<String[], String> errorFiles, String[] valores, String content, String hash) throws Exception {
		boolean safeFile = false;
		log.debug("processContentFile hash "+hash);
		if (hashCodeOld.get(valores[1]) == null) {
			news.add(valores[1]);
			all.add(valores[0] + "," + valores[1]);
			safeFile = true;
			log.info("Se ha encontrado un nuevo cubo de datos: " + valores[1]);
		} else if (!hashCodeOld.get(valores[1]).equals(hash)) {
			changes.add(valores[1]);
			all.add(valores[0] + "," + valores[1]);
			safeFile = true;
			log.info("Se han encontrado cambios en el cubo de datos " + valores[1]);
		} else {
			log.info("No hay cambios en el cubo de datos " + valores[1]);
		}
		if (safeFile) {
			if (!content.contains(Constants.errorDoctypeHtml1) && !content.contains(Constants.errorHtml) && !content.contains(Constants.errorDoctypeHtml2) && !content.contains(Constants.errorDiv) && !content.contains(Constants.errorNingunaFila)) {
				Utils.stringToFile(content, new File(outputFilesDirectoryString + File.separator + valores[1] + ".csv"));
				hashCodeNew.put(valores[1], hash);
				log.info("Descargado csv " + valores[1]);
			} else if (!content.contains(Constants.errorExcedidoN) && !content.contains(Constants.errorRutaNoEncontrada) && !content.contains(Constants.errorNingunaFila)) {
				numErrorFiles.put(valores, new Integer(0));
				errorFiles.put(valores, content);
				news.remove(valores[1]);
				changes.remove(valores[1]);
				log.info("El csv " + valores[1] + " no se pudo descargar");
			} else {
				informeErrores(valores[1], content);
				news.remove(valores[1]);
				changes.remove(valores[1]);
				log.info("El csv " + valores[1] + " no se pudo descargar");
			}
		}
	}

	private String cleanAndTransform(String content) {
		content = content.replace(new String(Character.toChars(0)), "");
		content = content.replace("ÿþ", "");
		return content;
	}

	public void generateHashCode(List<String> result, List<String> list) {

		File fileCSV = new File(Prop.fileHashCSV + "." + Constants.CSV);
		File fileXlsx = new File(Prop.fileHashCSV + "." + Constants.XLSX);
		String hashCodeFile = "";

		for (String key : hashCodeOld.keySet()) {
			String hash = "";
			if (result.contains(key))
				hash = hashCodeNew.get(key);
			else {
				hash = hashCodeOld.get(key);
			}
			hashCodeFile += key + "," + hash + "\n";

		}
		for (String key : hashCodeNew.keySet()) {
			String hash = "";
			if (!hashCodeFile.contains(key)) {
				hash = hashCodeNew.get(key);
				hashCodeFile += key + ",nuevo\n";
			}
		}
		try {
			Utils.stringToFile(hashCodeFile, fileCSV);
			Utils.csvToXLSX(fileCSV, fileXlsx);
			log.info("Hashcode generado correctamente");
		} catch (Exception e) {
			log.error("Error generando fichero hashcode", e);
		}
	}
	
	public void generateHashCodeFromBI() {
		
		File urlsFile = new File(urlsFileString);
		File hashFile = new File(Prop.fileHashCSV + "." + Constants.CSV);
		Cookies cookies = new Cookies();
		String[] valores = null;
		String content = null;
		StringBuffer result = new StringBuffer();
		HashMap<String[], Integer> numErrorFiles = new HashMap<>();
		HashMap<String[], String> errorFiles = new HashMap<>();
		try{
			Map<String, String> headers = new HashMap<String, String>();
			headers.put("User-Agent", "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_10_2) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/46.0.2490.86 Safari/537.36");
			headers.put("Cookie", "sawU=" + Prop.sawUiAragonBiAragon + "; ORA_BIPS_LBINFO=" + Prop.oraBipsLbinfoBiAragon + "; ORA_BIPS_NQID=" + Prop.oraBipsNqidBiAragon + "; __utma=" + Prop.utmaBiAragon + "; __utmc=" + Prop.utmcBiAragon + "; __utmz=" + Prop.utmzBiAragon);
			headers.put("content-type", "text/csv; charset=ISO-8859-1");
			Utils.processURLGet(Prop.urlBiAragon + Prop.initialDataCube + "&Action=Download&Options=df&NQUser=" + Prop.nqUserBiAragon + "&NQPassword=" + Prop.nqPasswordBiAragon , "", headers, cookies, "ISO-8859-1");
			List<String> csvLines = FileUtils.readLines(urlsFile, "UTF-8");
	
			try {
				for (int h = 1; h < csvLines.size(); h++) {
					String line = csvLines.get(h);
					valores = line.split(",");
					valores[0] = valores[0].replaceAll("\"", "");
					valores[1] = valores[1].replaceAll("\"", "");
					valores[2] = valores[2].replaceAll("\"", "");
					content = Utils.processURLGet(Prop.urlBiAragon + valores[0] + "&Action=Download&Options=df&NQUser=" + Prop.nqUserBiAragon + "&NQPassword=" + Prop.nqPasswordBiAragon , "", headers, cookies, "ISO-8859-1");
					if (Utils.v(content)) {
						content = cleanAndTransform(content);
						String hash = Utils.generateHash(content);
						result.append(valores[1]+","+hash+"\n");
						log.info(valores[1]+","+hash);
					}else{
						numErrorFiles.put(valores, new Integer(0));
						errorFiles.put(valores, content);
					}
				}
			} catch (IOException e2) {
				numErrorFiles.put(valores, new Integer(0));
				errorFiles.put(valores, content);
				log.error("Error al descargar " + valores[1], e2);
			}
			
			int j = 0;
			int totalElements = numErrorFiles.size();
			
			try {
				Iterator<String[]> iterator = numErrorFiles.keySet().iterator();
				while (j < totalElements) {
					valores = iterator.next();
					Integer numErrors = numErrorFiles.get(valores);
					if (numErrors < 5 && numErrors != -1) {
						content = Utils.processURLGet(Prop.urlBiAragon + valores[0] + "&Action=Download&Options=df&NQUser=" + Prop.nqUserBiAragon + "&NQPassword=" + Prop.nqPasswordBiAragon , "", headers, cookies, "ISO-8859-1");
						if (Utils.v(content)) {
							content = cleanAndTransform(content);
							if (!content.contains(Constants.errorDoctypeHtml1) && !content.contains(Constants.errorHtml) && !content.contains(Constants.errorDoctypeHtml2) && !content.contains(Constants.errorDiv)) {
								Utils.stringToFile(content, new File(outputFilesDirectoryString + File.separator + valores[1] + ".csv"));
								String hash = Utils.generateHash(content);
								result.append(valores[1]+","+hash+"\n");
								numErrorFiles.put(valores, new Integer(-1));
								errorFiles.remove(valores);
							} else if (!content.contains(Constants.errorExcedidoN) && !content.contains(Constants.errorRutaNoEncontrada)) {
								numErrorFiles.put(valores, new Integer(-1));
							} else {
								log.error("Informe " + valores[1] + " imposible de descargar intento " + (numErrors + 1));
								numErrorFiles.put(valores, ++numErrors);
							}
						}
						Thread.sleep(1000);
					}
					if (!iterator.hasNext()) {
						iterator = numErrorFiles.keySet().iterator();
						j++;
					}
				}
			} catch (IOException e2) {
				log.error("Error al descargar " + valores[1], e2);
			}
			
			try {
				Utils.stringToFile(result.toString(), hashFile);
				log.info("Hashcode generado correctamente");
			} catch (Exception e) {
				log.error("Error generando fichero hashcode", e);
			}
			
		} catch (Exception e) {
			log.error("Error desconocido", e);
		}
	}

	protected void informeErrores(String id, String content) {
		if (content != null && content.contains("Se ha excedido el n")) {
			File file = new File(Prop.fileErrorBig);
			Utils.stringToFileAppend(id + ".csv" + System.lineSeparator(), file);
		} else if (content != null && content.contains("Ruta de acceso no encontrada")) {
			File file = new File(Prop.fileErrorNotFound);
			Utils.stringToFileAppend(id + ".csv" + System.lineSeparator(), file);
		} else {
			File file = new File(Prop.fileErrorGeneric);
			Utils.stringToFileAppend(id + ".csv" + System.lineSeparator(), file);
		}
	}

	protected void extractHashCode() {

		File fileXlsx = new File("" + Prop.fileHashCSV + "." + Constants.XLSX);
		File fileCSV = new File("" + Prop.fileHashCSV + "." + Constants.CSV);
		try {
			Utils.XLSXToCsv(fileXlsx, fileCSV);
		} catch (Exception e1) {
			log.error("Error al transformar el fichero Xlsx a Csv");
		}
		try {
			List<String> hashLines = FileUtils.readLines(fileCSV, "UTF-8");
			for (String line : hashLines) {
				String[] valores = line.split(",");
				hashCodeOld.put(valores[0], valores[1]);
			}
		} catch (IOException e) {
			log.error("Error leyendo fichero hashcode", e);
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
		File copyDirectoryFile = null;
		File copyHashCSVFile = null;
		File copyErrorBigFile = null;
		File copyErrorNotFoundFile = null;
		File copyErrorGenericFile = null;
		if (outputDirectoryFile.exists()) {

			String copy = outputFilesDirectoryString + "_" + formatFullDate.format(new Date());
			copyDirectoryFile = new File(copy);
			int aux = 1;
			while (copyDirectoryFile.exists()) {
				copyDirectoryFile = new File(copy + "_" + aux++);
			}
		}
		if (hashCSVFile.exists()) {
			String s2 = Prop.fileHashCSV + "_" + formatFullDate.format(new Date());
			copyHashCSVFile = new File(s2);
			int aux = 1;
			while (copyHashCSVFile.exists()) {
				copyHashCSVFile = new File(s2 + "_" + aux++);
			}
		}
		if (errorBigFile.exists()) {
			String s3 = Prop.fileErrorBig + "_" + formatFullDate.format(new Date());
			copyErrorBigFile = new File(s3);
			int aux = 1;
			while (copyErrorBigFile.exists()) {
				copyErrorBigFile = new File(s3 + "_" + aux++);
			}
		}
		if (errorNotFoundFile.exists()) {
			String s4 = Prop.fileErrorNotFound + "_" + formatFullDate.format(new Date());
			copyErrorNotFoundFile = new File(s4);
			int aux = 1;
			while (copyErrorNotFoundFile.exists()) {
				copyErrorNotFoundFile = new File(s4 + "_" + aux++);
			}
		}
		if (errorGenericFile.exists()) {
			String s5 = Prop.fileErrorGeneric + "_" + formatFullDate.format(new Date());
			copyErrorGenericFile = new File(s5);
			int aux = 1;
			while (copyErrorGenericFile.exists()) {
				copyErrorGenericFile = new File(s5 + "_" + aux++);
			}
		}
		try {
			if (copyDirectoryFile != null)
				FileUtils.moveDirectoryToDirectory(outputDirectoryFile, copyDirectoryFile, true);
			if (copyHashCSVFile != null)
				FileUtils.copyFile(hashCSVFile, copyHashCSVFile);
			if (copyErrorBigFile != null)
				FileUtils.copyFile(errorBigFile, copyErrorBigFile);
			if (copyErrorNotFoundFile != null)
				FileUtils.copyFile(errorNotFoundFile, copyErrorNotFoundFile);
			if (copyErrorGenericFile != null)
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
			log.info("Start process");
			Prop.loadConf();
			GenerateCSV app = new GenerateCSV(args[1], args[2]);
//			app.backup();
			if (args[0].equals("update")) {
				app.extractFiles();
			}else if(args[0].equals("generateHash")){
				app.generateHashCodeFromBI();
			}
			log.info("Finish process");
		}

}
