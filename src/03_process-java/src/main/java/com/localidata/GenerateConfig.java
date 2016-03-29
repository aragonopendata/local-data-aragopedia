package com.localidata;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

public class GenerateConfig {


	private final static Logger log = Logger.getLogger(GenerateConfig.class);

	protected String inputDirectoryString = "D:\\trabajo\\gitOpenDataAragon2\\doc\\iaest\\DatosPrueba2";

	protected static String configDirectoryString = "";

	protected String dimensionDirectoryString = "D:\\trabajo\\gitOpenDataAragon2\\doc\\iaest\\DimensionesFinales\\PosiblesDimensionesMenos20Valores";

	protected String[] extensions = new String[] { "csv", "txt" };

	protected static HashMap<String, DataBean> skosExtrated = new HashMap<String, DataBean>();

	public static final String errorFileString = "errores.txt";

	protected ArrayList<DataBean> listConstants = new ArrayList<DataBean>();


	public GenerateConfig(String input, String dimension, String config) {
		inputDirectoryString = input;
		dimensionDirectoryString = dimension;
		configDirectoryString = config;
	}


	public void generateAllConfig() {

		ArrayList<String> dimension = extractDimensions(dimensionDirectoryString);
		HashMap<String, ConfigBean> configExtrated = new HashMap<String, ConfigBean>();
		File inputDirectoryFile = new File(inputDirectoryString);
		Collection<File> listCSV = FileUtils.listFiles(inputDirectoryFile,
				extensions, true);
		int cont = 0;
		int size = listCSV.size();
		for (File file : listCSV) {

			ArrayList<DataBean> skosData = new ArrayList<DataBean>();
			String id = "";
			String letters = "";
			if (file.getName().endsWith("A.csv")) {
				id = file.getName().substring(0, file.getName().length() - 5);
				letters = file.getName().substring(file.getName().length() - 5,
						file.getName().length() - 4);
			} else {
				id = file.getName().substring(0, file.getName().length() - 6);
				letters = file.getName().substring(file.getName().length() - 6,
						file.getName().length() - 4);
			}
			log.info("Comienza tratamiento para " + id + letters + " "
					+ (++cont) + "/" + size);
			ConfigBean configBean = null;
			if (configExtrated.get(id) != null) {
				configBean = configExtrated.get(id);
			} else {
				configBean = new ConfigBean();
				configBean.setId(id);
			}

			configBean.getLetters().add(letters);
			try {
				List<String> csvLines = FileUtils.readLines(file, "UTF-8");
				String headerLine = Utils.weakClean(csvLines.get(0));
				String fisrtLine = Utils.weakClean(csvLines.get(2));
				String[] cells = headerLine.split("\t");
				String[] cellsFisrtLine = fisrtLine.split("\t");
				for (int h = 0; h < cells.length; h++) {
					String name = cells[h];
					DataBean dataBean = null;
					if (configBean.getMapData().get(name) != null) {
						dataBean = configBean.getMapData().get(name);
					} else {
						dataBean = new DataBean();
						dataBean.setName(name.trim());
					}

					if (name.toLowerCase().contains("código")
							|| name.toLowerCase().contains("codigo")) {
						dataBean.setNormalizacion("null");
						dataBean.setDimensionMesure("null");
						dataBean.setType("null");
					} else {

						if (contains(dimension, name + ".txt")) {

							dataBean.setDimensionMesure("dim");
							if (dataBean.getName().toLowerCase()
									.contains("comarca")) {
								dataBean.setNormalizacion("sdmx-dimension:refArea");
								dataBean.setType("URI-comarca");
							} else if (dataBean.getName().toLowerCase()
									.contains("municipio")) {
								dataBean.setNormalizacion("sdmx-dimension:refArea");
								dataBean.setType("URI-Municipio");
							} else if (dataBean.getName().toLowerCase()
									.contains("provincia")) {
								dataBean.setNormalizacion("sdmx-dimension:refArea");
								dataBean.setType("URI-Provincia");
							} else if (dataBean.getName().toLowerCase()
									.contains("comunidad")
									|| dataBean.getName().toLowerCase()
											.contains("aragón")
									|| dataBean.getName().toLowerCase()
											.contains("ccaa")) {
								dataBean.setNormalizacion("sdmx-dimension:refArea");
								dataBean.setType("URI-Comunidad");
							} else {
								if (name.toLowerCase().contains("año")) {
									String type = "";
									for (int j = 1; j < csvLines.size(); j++) {
										String line = Utils.weakClean(csvLines.get(j));
										if (Utils.v(line)) {
											String[] cellsLine = line.split("\t");
											if (cellsLine.length > 0
													&& cellsLine.length > h) {
												String cell = cellsLine[h];
									
												if (Utils.isDate(cell) && !type.equals("xsd:int")) {
													type="xsd:date";
												}else if (Utils.isInteger(cell)){
													type="xsd:int";
												}else{
													log.info("La celda '"+cell+"' de la columna '"+name+"' no es un año");
													break;
												}
											}
										}
									}
									if(type.equals("xsd:date")){
										dataBean.setNormalizacion("sdmx-dimension:refPeriod");
										dataBean.setType("xsd:date");
									}else if(type.equals("xsd:int")){
										dataBean.setDimensionMesure("medida");
										dataBean.setNormalizacion(Prop.datasetName
												+ "-measure:" + Utils.urlify(name));
										dataBean.setType("xsd:int");
									}else{
										dataBean.setNormalizacion(Prop.datasetName
												+ "-dimension:"
												+ Utils.urlify(name));
										dataBean.setType("xsd:string");
									}
								} else{
									dataBean.setNormalizacion(Prop.datasetName
											+ "-dimension:"
											+ Utils.urlify(name));
									dataBean.setType("skos:Concept");
									skosData.add(dataBean);										
								}
							}
						} else {
							dataBean.setDimensionMesure("medida");
							dataBean.setNormalizacion(Prop.datasetName
									+ "-measure:" + Utils.urlify(name));
							String type = "";
							for (int j = 1; j < csvLines.size(); j++) {
								String line = Utils.weakClean(csvLines.get(j));
								if (Utils.v(line)) {
									String[] cellsLine = line.split("\t");
									if (cellsLine.length > 0
											&& cellsLine.length > h) {
										String cell = cellsLine[h];
										if (Utils.isInteger(cell)) {
											if (type.equals(""))
												type = "xsd:int";
										} else if (Utils.isDouble(cell)) {
											if (!type.equals("xsd:string"))
												type = "xsd:double";
										} else if (Utils.v(cell)) {
											type = "xsd:string";
											break;
										}
									} else {
										type = "xsd:string";
									}
								}
							}
							if(type.equals(""))
								type = "xsd:string";
							dataBean.setType(type);
							
						}
					}
					configBean.getMapData().put(dataBean.getName(), dataBean);
				}

				if (skosData.size() > 0)
					extractSkosConcept(csvLines, skosData);

			} catch (IOException e) {
				log.error("Error to read lines", e);
			}
			configExtrated.put(configBean.getId(), configBean);
			log.info("Finaliza tratamiento para " + id + letters);
		}

		cont = 0;
		size = configExtrated.keySet().size();
		for (String key : configExtrated.keySet()) {

			ConfigBean configBean = configExtrated.get(key);
			String letters = "-";
			for (String letter : configBean.getLetters()) {
				letters = letters + letter + "-";
			}
			letters = letters.substring(0, letters.length() - 1);
			configBean.setNameFile("Informe-" + configBean.getId() + letters
					+ ".csv");
			log.info("Comienza a escribirse el archivo " + "Informe-"
					+ configBean.getId() + letters + ".csv " + (++cont) + "/"
					+ size);
			configBean.toCSV();
			log.info("Finaliza de escribirse el archivo " + "Informe-"
					+ configBean.getId() + letters + ".csv");
		}
		generateSkosMapping();
	}


	private void extractSkosConcept(List<String> csvLines,
			ArrayList<DataBean> skosData) {
		String headerLine = Utils.weakClean(csvLines.get(0));
		String[] cells = headerLine.split("\t");
		int[] posColumn = new int[skosData.size()];
		for (int h = 0; h < skosData.size(); h++) {
			String name = skosData.get(h).getName();
			for (int i = 0; i < cells.length; i++) {
				if (cells[i].equalsIgnoreCase(name)) {
					boolean incluido = false;
					for (int j = 0; j < posColumn.length; j++) {
						if (posColumn[j] == i)
							incluido = true;
					}
					if (!incluido) {
						posColumn[h] = i;
						break;
					}
				}
			}
		}
		for (int h = 1; h < csvLines.size(); h++) {
			String line = Utils.weakClean(csvLines.get(h));
			if (Utils.v(line)) {
				cells = line.split("\t");
				for (int i = 0; i < skosData.size(); i++) {
					try {
						String cell = cells[posColumn[i]];
						SkosBean skosBean = new SkosBean();
						String skosUrified = Utils.urlify(cell);
						skosBean.setId(skosUrified);
						skosBean.setLabel(Utils.weakClean(cell));
						skosBean.setURI(Prop.host + "/kos/" + Prop.datasetName
								+ "/" + Utils.urlify(skosData.get(i).getName())
								+ "/" + skosUrified);
						DataBean dataBean = null;
						if (skosExtrated.get(skosData.get(i).getName()) != null) {
							dataBean = skosExtrated.get(skosData.get(i)
									.getName());
						} else {
							dataBean = skosData.get(i);
						}
						if(dataBean.getMapSkos().get(skosBean.getId())==null){
							dataBean.getMapSkos().put(skosBean.getId(), skosBean);
							skosExtrated.put(dataBean.getName(), dataBean);
						}
					} catch (ArrayIndexOutOfBoundsException e) {
						log.error(
								"ERROR al extraer los skos debido a incoherencia de columnas",
								e);
						DataBean dataBean = null;
						if (skosExtrated.get(skosData.get(i).getName()) != null) {
							dataBean = skosExtrated.get(skosData.get(i)
									.getName());
						} else {
							dataBean = skosData.get(i);
						}
						skosExtrated.put(dataBean.getName(), dataBean);
					}
				}
			}
		}
	}


	private ArrayList<String> extractDimensions(String directoryString) {
		ArrayList<String> result = new ArrayList<String>();
		File dimensionDirectoryFile = new File(directoryString);
		Collection<File> listCSV = FileUtils.listFiles(dimensionDirectoryFile,
				extensions, true);
		for (File file : listCSV) {
			result.add(Utils.dimensionWeakClean(file.getName()));
		}
		return result;
	}


	private boolean contains(ArrayList<String> set, String busqueda) {
		boolean result = false;
		for (String setString : set) {
			if (setString
					.equalsIgnoreCase(Utils.dimensionStrongClean(busqueda))) {
				result = true;
				break;
			}
		}
		return result;
	}


	public void generateSkosMapping() {
		String filedSeparator = "\"";
		String csvSeparator = ",";

		for (String key : GenerateConfig.skosExtrated.keySet()) {
			StringBuffer content = new StringBuffer();
			DataBean data = GenerateConfig.skosExtrated.get(key);

			for (String skosName : data.getMapSkos().keySet()) {
				SkosBean skosBean = data.getMapSkos().get(skosName);
				if (Utils.v(skosBean.getId()))
					content.append(filedSeparator + skosBean.getLabel()
							+ filedSeparator + csvSeparator + filedSeparator
							+ skosBean.getURI() + filedSeparator
							+ System.getProperty("line.separator"));
			}
			String nameFile = "mapping-" + Utils.urlify(data.getName());
			String pathFile = configDirectoryString + File.separator + nameFile
					+ ".csv";
			log.info("comienza a escribirse el archivo " + nameFile + ".csv");
			File file = new File(pathFile);
			try {
				Utils.stringToFile(content.toString(), file);
				if (Prop.publicDrive) {
					GoogleDriveAPI api = new GoogleDriveAPI();
					api.init();
					api.createSpreadsheetFromFile(Prop.idParentFolder,
							Prop.emailUserFile, "csv", nameFile, file,
							"text/csv");
				}
				log.info("finaliza de escribirse el archivo " + nameFile
						+ ".csv");
			} catch (Exception e) {
				log.error("Error to generate skos mapping " + pathFile, e);
			}

		}

	}


	public static void main(String[] args) {

		if ((log == null) || (log.getLevel() == null))
			PropertyConfigurator.configure("log4j.properties");
		if (args.length == 3) {
			log.info("Start process");
			Prop.loadConf();
			GenerateConfig config = new GenerateConfig(args[0], args[1],
					args[2]);
			config.generateAllConfig();
			log.info("Finish process");
		} else {
			log.info("Se deben de pasar dos parámetros: ");
			log.info("\tEl directorio donde están los archivos de entrada");
			log.info("\tEl directorio donde están las dimensiones");
			log.info("\tEl directorio donde se va a escribir la configuración resultante");
		}

	}

}
