package com.localidata;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;


public class GenerateData {

	private final static Logger log = Logger.getLogger(GenerateData.class);

	String inputDirectoryString = "";

	String outputDirectoryString = "";

	String configDirectoryString = "";

	static String propertiesFileString = "";

	String[] extensions = new String[] { "csv" };

	String[] extensionsConfig = new String[] { "xlsx", "csv" };
	HashMap<String, ConfigBean> mapconfig = new HashMap<String, ConfigBean>();
	private String formatConfig = "xlsx";
	private HashSet<String> dsdSet = new HashSet<String>();
	private HashSet<String> propertiesSet = new HashSet<String>();


	public GenerateData(String input, String output, String config) {
		this.inputDirectoryString = input;
		this.outputDirectoryString = output;
		this.configDirectoryString = config;
	}


	public static void main(String[] args) {
		if ((log == null) || (log.getLevel() == null))
			PropertyConfigurator.configure("log4j.properties");
		if (args.length == 3) {
			log.info("Start process");
			Prop.loadConf();
			GenerateData app = new GenerateData(args[0], args[1], args[2]);
			app.backup();
			app.extractConfig();
			app.extractInformation();
			log.info("Finish process");
		} 

	}

	private void extractConfig() {
		log.debug("Init extractConfig");
		log.info("Comienza a extraerse la configuración");
		File configDirectoryFile = new File(configDirectoryString);
		File areasReportFile = new File(outputDirectoryString + File.separator
				+ "areas.txt");
		Collection<File> listCSV = FileUtils.listFiles(configDirectoryFile,
				extensionsConfig, true);
		int cont = 0;
		int size = listCSV.size();
		for (File file : listCSV) {
			log.info("Se extrae el fichero " + file.getName() + " " + (++cont)
					+ " " + size);
			if (!file.getName().startsWith("mapping")) {
				ConfigBean configBean = new ConfigBean();
				configBean.setNameFile(file.getName());
				String id = file.getName().substring(8);
				String areas = "";
				id = id.replace(".csv", "");
				id = id.replace(".xlsx", "");
				if (id.contains("TC")) {
					id = id.replace("TC", "");
					configBean.getLetters().add("TC");
					areas += "TC ";
				}
				if (id.contains("TM")) {
					id = id.replace("TM", "");
					configBean.getLetters().add("TM");
					areas += "TM ";
				}
				if (id.contains("TP")) {
					id = id.replace("TP", "");
					configBean.getLetters().add("TP");
					areas += "TP ";
				}
				if (id.contains("A")) {
					id = id.replace("A", "");
					configBean.getLetters().add("A");
					areas += "A ";
				}
				while (id.charAt(id.length() - 1) == '-') {
					id = id.substring(0, id.length() - 1);
				}

				configBean.setId(id);
				if (formatConfig.equals("csv")) {
					readCsv(file, configBean);
				} else {
					readXlsx(file, configBean);
				}
				mapconfig.put(id, configBean);
				Utils.stringToFileAppend(id + " " + areas + "\n",
						areasReportFile);
			}
		}
		log.info("Finaliza de extraerse la configuración");
		log.debug("End extractConfig");
	}

	private void readCsv(File file, ConfigBean configBean) {
		log.debug("Init readCsv");
		List<String> csvLines;
		try {
			csvLines = FileUtils.readLines(file, "UTF-8");

			String[] cellsName = csvLines.get(0).split(",");
			String[] cellsNormalization = csvLines.get(1).split(",");
			String[] cellsDimMesure = csvLines.get(2).split(",");
			String[] cellsType = csvLines.get(3).split(",");
			String[] cellsSkosfile = csvLines.get(4).split(",");

			boolean cont = true;
			int columnReaded = 0;
			while (cont && columnReaded < cellsName.length) {
				DataBean dataBean = new DataBean();
				if (cellsName[columnReaded] == null) {
					cont = false;
				} else {
					dataBean.setName(removeStartEndCaracter(cellsName[columnReaded]));
					dataBean.setNormalizacion(removeStartEndCaracter(cellsNormalization[columnReaded]));
					dataBean.setDimensionMesure(removeStartEndCaracter(cellsDimMesure[columnReaded]));
					dataBean.setType(removeStartEndCaracter(cellsType[columnReaded]));
					if (Utils
							.v(removeStartEndCaracter(cellsSkosfile[columnReaded]))) {
						HashMap<String, SkosBean> mapSkos = processSkos(removeStartEndCaracter(cellsSkosfile[columnReaded]));
						dataBean.setMapSkos(mapSkos);
					}
					configBean.getMapData().put(
							Utils.urlify(dataBean.getName()), dataBean);
					columnReaded++;
				}
			}
		} catch (IOException e) {
			log.error("Error read csv ", e);
		}
		log.debug("End readCsv");
	}

	private String removeStartEndCaracter(String csvLine) {
		log.debug("Init removeStartEndCaracter");
		String line = csvLine;
		if (csvLine != null) {
			if (csvLine.startsWith("\""))
				line = csvLine.substring(1, csvLine.length());
			if (csvLine.endsWith("\""))
				line = line.substring(0, line.length() - 1);
		}
		log.debug("End removeStartEndCaracter");
		return line;
	}

	private void readXlsx(File file, ConfigBean configBean) {
		log.debug("Init readXlsx");
		InputStream inp = null;
		Workbook wb = null;
		try {
			inp = new FileInputStream(file);
			wb = WorkbookFactory.create(inp);
		} catch (FileNotFoundException e) {
			log.error(e.getMessage());
		} catch (InvalidFormatException e) {
			log.error(e.getMessage());
		} catch (IOException e) {
			log.error(e.getMessage());
		}

		Sheet sheet = wb.getSheetAt(0);
		Row rowName = sheet.getRow(0);
		Row rowNormalization = sheet.getRow(1);
		Row rowDimMesure = sheet.getRow(2);
		Row rowType = sheet.getRow(3);
		Row rowSkosfile = sheet.getRow(4);
		Row rowConstant = sheet.getRow(5);
		Row rowConstantValue = sheet.getRow(6);
		boolean cont = true;
		int columnReaded = 0;
		while (cont) {
			Cell cellName = rowName.getCell(columnReaded);
			Cell cellNormalization = rowNormalization.getCell(columnReaded);
			Cell cellDimMesure = rowDimMesure.getCell(columnReaded);
			Cell cellType = rowType.getCell(columnReaded);
			Cell cellSkosfile = null;
			if (rowSkosfile != null)
				cellSkosfile = rowSkosfile.getCell(columnReaded);
			Cell cellConstant = null;
			if (rowConstant != null)
				cellConstant = rowConstant.getCell(columnReaded);
			Cell cellConstantValue = null;
			if (rowConstantValue != null)
				cellConstantValue = rowConstantValue.getCell(columnReaded);
				
			DataBean dataBean = new DataBean();
			if (cellName == null) {
				cont = false;
			} else {
				dataBean.setName(cellName.getStringCellValue());
				dataBean.setNormalizacion(cellNormalization
						.getStringCellValue());
				dataBean.setDimensionMesure(cellDimMesure.getStringCellValue());
				String type="";
				if(cellType!=null){
					type=cellType.getStringCellValue();
				}else{
					type = "xsd:string";
				}
				dataBean.setType(type);
				if (cellSkosfile != null) {
					HashMap<String, SkosBean> mapSkos = processSkos(cellSkosfile
							.getStringCellValue());
					dataBean.setMapSkos(mapSkos);
					configBean.getMapData().put(
							Utils.urlify(dataBean.getName()), dataBean);
				} else {
					configBean.getMapData().put(
							Utils.urlify(dataBean.getName()), dataBean);
				}
				if (Prop.addDataConstant && cellConstant != null && cellConstant.getStringCellValue().equals(Constants.constante)){
					if(cellConstantValue!=null){
						dataBean.setConstant(cellConstantValue.getStringCellValue() + "");
						configBean.getListDataConstant().add(dataBean);
					}
				}

				columnReaded++;
			}
		}
		log.debug("End readXlsx");
	}

	private HashMap<String, SkosBean> processSkos(String skosPath) {
		log.debug("Init processSkos");
		HashMap<String, SkosBean> mapSkos = new HashMap<String, SkosBean>();
		
		File skosMappingg = new File(configDirectoryString + File.separator
				+ skosPath);
		InputStream inp = null;
		Workbook wb = null;
		try {
			inp = new FileInputStream(skosMappingg);
			wb = WorkbookFactory.create(inp);
		} catch (FileNotFoundException e) {
			log.error(e.getMessage());
		} catch (InvalidFormatException e) {
			log.error(e.getMessage());
		} catch (IOException e) {
			log.error(e.getMessage());
		}
		if (wb != null) {
			Sheet sheet = wb.getSheetAt(0);
			for (int i = 0; i < sheet.getPhysicalNumberOfRows(); i++) {
				Row row = sheet.getRow(i);
				Cell cellId = row.getCell(0);
				Cell cellUri = row.getCell(1);
				SkosBean skosBean = new SkosBean();
				SkosBean skosBeanExtra = new SkosBean();
				String idCell = "";
				if (cellId.getCellType() == 0) {
					Double d = new Double(cellId.getNumericCellValue());
					idCell = d.intValue() + "";
				} else {
					idCell = cellId.getStringCellValue();
				}
				skosBean.setLabel(idCell);
				idCell = Utils.urlify(idCell);
				skosBean.setId(idCell);
				String uriCell = "";
				if (cellUri.getCellType() == 0) {
					Double d = new Double(cellUri.getNumericCellValue());
					uriCell = d.intValue() + "";
				} else {
					uriCell = cellUri.getStringCellValue();
					String id = uriCell.substring(uriCell.lastIndexOf("/")+1, uriCell.length()); 
					if(!idCell.equals(id)){
						skosBeanExtra.setId(id);
						skosBeanExtra.setLabel(id);
						skosBeanExtra.setURI(uriCell);
						mapSkos.put(id, skosBeanExtra);
					}
				}
				skosBean.setURI(uriCell);
				mapSkos.put(idCell, skosBean);
			}
		}
		log.debug("End processSkos");
		return mapSkos;
	}

	private void createSkos() {
		log.debug("Init createSkos");
		log.info("Init to create skos");
		File kosFile = new File(outputDirectoryString + File.separator
				+ "DatosTTL" + File.separator + "codelists" + File.separator
				+ "kos.ttl");
		StringBuffer resultIni = new StringBuffer();
		StringBuffer resultFin = new StringBuffer();
		HashSet<String> kosCreated = new HashSet<String>();
		resultIni.append(TransformToRDF.addPrefix());
		for (Iterator<String> iterator = mapconfig.keySet().iterator(); iterator
				.hasNext();) {
			String keyConfig = iterator.next();
			ConfigBean configBean = mapconfig.get(keyConfig);
			if (configBean != null) {
				for (Iterator<String> iterator2 = configBean.getMapData().keySet()
						.iterator(); iterator2.hasNext();) {
					String keyData = iterator2.next();
					DataBean dataBean = configBean.getMapData().get(keyData);
					if (dataBean != null
							&& !kosCreated.contains(dataBean.getName())
							&& dataBean.getMapSkos().size() > 0) {
						String suject = Prop.host + "/" + Prop.kosName + "/"
								+ Prop.datasetName + "/"
								+ Utils.urlify(dataBean.getName());
						resultIni.append("<" + suject + "> "
								+ "a skos:ConceptScheme.\n");
						resultIni.append("<" + suject + "> skos:notation \""
								+ Utils.urlify(dataBean.getName()) + "\".\n");
						resultIni.append("<" + suject + "> rdfs:label \""
								+ dataBean.getName() + "\".\n");

						for (Iterator<String> iterator3 = dataBean.getMapSkos()
								.keySet().iterator(); iterator3.hasNext();) {
							// resultIni.append(resultFin);
							String keySkos = iterator3.next();
							SkosBean skosBean = dataBean.getMapSkos().get(
									keySkos);
							if (skosBean != null) {
								resultIni.append("<" + suject
										+ "> skos:hasTopConcept <"
										+ skosBean.getURI() + ">.\n");
								resultFin.append("<" + skosBean.getURI()
										+ "> a skos:Concept.\n");
								resultFin
										.append("<" + skosBean.getURI()
												+ "> skos:inScheme <" + suject
												+ ">.\n");
								String label = skosBean.getId();
								if (skosBean.getLabel() != null
										&& !skosBean.getLabel().equals(""))
									label = skosBean.getLabel();
								resultFin.append("<" + skosBean.getURI()
										+ "> skos:notation \""
										+ skosBean.getId() + "\".\n");
								resultFin.append("<" + skosBean.getURI()
										+ "> skos:prefLabel \"" + Utils.prefLabelClean(label)
										+ "\".\n");

								resultFin.append("\n");
							}
						}
						resultIni.append("\n");
						resultIni.append(resultFin);
						kosCreated.add(dataBean.getName());
					}
					// escribir en fichero
					Utils.stringToFileAppend(resultIni.toString(), kosFile);
					resultIni.setLength(0);
					resultFin.setLength(0);
				}
			}
		}

		log.info("end to create skos");
		log.debug("End createSkos");
	}

	private void backup() {
		log.debug("Init backup");
		log.info("Comienza a hacerse el backup");
		File outputDirectoryFile = new File(outputDirectoryString);
		if (outputDirectoryFile.exists()) {
			SimpleDateFormat formatFullDate = new SimpleDateFormat("yyyyMMdd");
			String copy = outputDirectoryString + "_"
					+ formatFullDate.format(new Date());
			File copyDirectoryFile = new File(copy);
			int aux = 1;
			while (copyDirectoryFile.exists()) {
				copyDirectoryFile = new File(copy + "_" + aux++);
			}

			try {
				FileUtils.moveDirectoryToDirectory(outputDirectoryFile,
						copyDirectoryFile, true);
			} catch (IOException e) {
				log.error("Error haciendo backup", e);
			}
		}
		log.info("Finaliza de hacerse el backup");
		log.debug("End backup");
	}


	private void extractInformation() {
		log.debug("Init extractInformation");
		// Getting input files
		File inputDirectoryFile = new File(inputDirectoryString);
		File propertiesFile = new File(outputDirectoryString + File.separator
				+ "DatosTTL" + File.separator + "codelists" + File.separator
				+ "properties.ttl");
		File dsdFile = new File(outputDirectoryString + File.separator
				+ "DatosTTL" + File.separator + "dataStructures"
				+ File.separator + "dsd.ttl");
		File errorReportFile = new File(outputDirectoryString + File.separator
				+ "errorReport.txt");

		TransformToRDF.propertiesContent.append(TransformToRDF.addPrefix());
		Utils.stringToFileAppend(TransformToRDF.addPrefix().toString(), dsdFile);

		Collection<File> listCSV = FileUtils.listFiles(inputDirectoryFile,
				extensions, true);
		int numfile = 1;
		for (File file : listCSV) {
			try {
				String fileName = "";
				String fileLetter = "";
				if (file.getName().endsWith("A.csv")) {
					fileName = file.getName().substring(0,
							file.getName().length() - 5);
					fileLetter = file.getName().substring(
							file.getName().length() - 5,
							file.getName().length() - 4);
				} else {
					fileName = file.getName().substring(0,
							file.getName().length() - 6);
					fileLetter = file.getName().substring(
							file.getName().length() - 6,
							file.getName().length() - 4);
				}
				ConfigBean configBean = mapconfig.get(fileName);
				if(configBean!=null){
					File outputDirectoryFile = new File(outputDirectoryString
							+ File.separator + "DatosTTL" + File.separator
							+ "informes" + File.separator + fileName + fileLetter
							+ ".ttl");
					log.info("Init file " + fileName + fileLetter + ". Size "
							+ FileUtils.sizeOf(file) + " " + numfile + "/"
							+ listCSV.size());
					List<String> csvLines = FileUtils.readLines(file, "UTF-8");
					TransformToRDF transformToRDF = new TransformToRDF(csvLines,
							outputDirectoryFile, propertiesFile, dsdFile,
							errorReportFile, configBean);
					transformToRDF.initTransformation(fileName + fileLetter,
							numfile, fileName, dsdSet, propertiesSet);
					log.info("End file " + outputDirectoryFile.getName() + " "
							+ numfile + "/" + listCSV.size());
				}else{
					log.error("Error al extraer la configuración de "+fileName);
				}
				numfile++;
			} catch (Exception e) {
				log.error("Error al extraer la información ", e);
			}
		}

		createSkos();
		log.debug("End extractInformation");
	}
}
