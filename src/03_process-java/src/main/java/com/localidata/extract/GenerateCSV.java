package com.localidata.extract;

import java.io.File;
import java.io.IOException;
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
 * Class to download data cubes
 * 
 * @author Localidata
 */
public class GenerateCSV {
	/**
	 * Log class
	 */
	private final static Logger log = Logger.getLogger(GenerateCSV.class);
	/**
	 * String urls File
	 */
	private String urlsFileString = "";
	/**
	 * String output directory
	 */
	private String outputFilesDirectoryString = "";
	/**
	 * HashMap<String, String> Map to save hashCode old
	 */
	private HashMap<String, String> hashCodeOld = new HashMap<>();
	/**
	 * HashMap<String, String> Map to save hashCode new
	 */
	private HashMap<String, String> hashCodeNew = new HashMap<>();
	/**
	 * HashMap<String, String> Map to save data cube description
	 */
	private HashMap<String, String> idDescription = new HashMap<>();
	/**
	 * List<String> List to save name files with changes
	 */
	private List<String> changes = new ArrayList<String>();
	/**
	 * List<String> to save name files news
	 */
	private List<String> news = new ArrayList<String>();
	/**
	 * Class to management drive api
	 */
	private GoogleDriveAPI drive = null;

	public GenerateCSV(String urls, String outputFiles) {
		urlsFileString = urls;
		outputFilesDirectoryString = outputFiles;
		drive = new GoogleDriveAPI();
		drive.init();
		log.info("Generando el fichero InformesEstadisticaLocal-URLs.csv");
		try {
			Jdbcconnection.main(null);
		} catch (Exception e) {
			log.error("Error generando informe bbdd iaest",e);
		}

		log.info("fin de la generación del fichero InformesEstadisticaLocal-URLs.csv");
	}

	/**
	 * Method to extract data cube files
	 */
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

			try {
				for (int h = 1; h < csvLines.size(); h++) {
					String line = csvLines.get(h);
					valores = line.split(",");
					valores[0] = valores[0].replaceAll("\"", "");
					valores[1] = valores[1].replaceAll("\"", "");
					valores[2] = valores[2].replaceAll("\"", "");
					idDescription.put(valores[1], valores[2]);
					content = Utils.processURLGet(Prop.urlBiAragon + valores[0] + "&Action=Download&Options=df&NQUser=" + Prop.nqUserBiAragon + "&NQPassword=" + Prop.nqPasswordBiAragon , "", headers, cookies, "ISO-8859-1");
					if (Utils.v(content)) {
						log.debug("content "+content.substring(0, 100));
						content = cleanAndTransform(content);
						log.debug("content "+content.substring(0, 100));
						String hash = Utils.generateHash(content);
						processContentFile(all, numErrorFiles, errorFiles, valores, content, hash);
					}
				}
			} catch (IOException e2) {
				numErrorFiles.put(valores, new Integer(0));
				errorFiles.put(valores, content);
				e2.printStackTrace();
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
								processContentFile(all, numErrorFiles, errorFiles, valores, content, hash);
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

			for (String[] val : errorFiles.keySet()) {
				String cont = errorFiles.get(valores);
				informeErrores(val[1], cont);
			}

		} catch (Exception e) {
			log.error("Error desconocido", e);
		}
		log.info("end extractFilesWithChanges");
	}

	/**
	 * Method for classifying exchange rate or type of error
	 * 
	 * @param all List with changes and news
	 * @param numErrorFiles Map of error files
	 * @param errorFiles Map of content error files
	 * @param valores Array of strings of characteristics file (name url, name file, description data cube)
	 * @param content String content file
	 * @param hash String hashcode file
	 * @throws Exception
	 */
	private void processContentFile(List<String> all, HashMap<String[], Integer> numErrorFiles, HashMap<String[], String> errorFiles, String[] valores, String content, String hash) throws Exception {
		boolean safeFile = false;
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
			if (!content.contains(Constants.errorDoctypeHtml1) && !content.contains(Constants.errorHtml) && !content.contains(Constants.errorDoctypeHtml2) && !content.contains(Constants.errorDiv)) {
				Utils.stringToFile(content, new File(outputFilesDirectoryString + File.separator + valores[1] + ".csv"));
				hashCodeNew.put(valores[1], hash);
				log.info("Descargado csv " + valores[1]);
			} else if (!content.contains(Constants.errorExcedidoN) && !content.contains(Constants.errorRutaNoEncontrada)) {
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

	/**
	 * Clean content file and transform to UTF8
	 * 
	 * @param content String content file
	 * @return String Content file cleaned and transformed
	 */
	private String cleanAndTransform(String content) {
		content = content.replace(new String(Character.toChars(0)), "");
		content = content.replace("ÿþ", "");
		return content;
	}

	/**
	 * Method to generate hashcode file
	 * 
	 * @param result List with entities with new hashcode
	 * @param list
	 */
	public void generateHashCode(List<String> result, List<String> list) {

		File file = new File(Prop.fileHashCSV + "." + Constants.CSV);
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
			Utils.stringToFile(hashCodeFile, file);
			drive.updateFile(Prop.fileHashCSV, file, "text/csv");
		} catch (Exception e) {
			log.error("Error generando fichero hashcode", e);
		}
	}

	/**
	 * Method to add error to propiate file
	 * 
	 * @param id String with id data cube
	 * @param content String with error response
	 */
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

	/**
	 * Method to download hashcode file
	 */
	protected void extractHashCode() {

		drive.downloadFile("", Prop.fileHashCSV, Constants.CSV);
		File file = new File("" + Prop.fileHashCSV + "." + Constants.CSV);
		try {
			List<String> hashLines = FileUtils.readLines(file, "UTF-8");
			for (String line : hashLines) {
				String[] valores = line.split(",");
				hashCodeOld.put(valores[0], valores[1]);
			}
		} catch (IOException e) {
			log.error("Error leyendo fichero hashcode", e);
		}
	}

	/**
	 * Method to do backup of mean files
	 */
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
		if (args.length == 3) {
			log.info("Start process");
			Prop.loadConf();
			GenerateCSV app = new GenerateCSV(args[1], args[2]);
			app.backup();
			if (args[0].equals("update")) {
				app.extractFiles();
			}
			log.info("Finish process");
		} else {
			log.info("Se deben de pasar dos parámetros: ");

		}

	}

}
