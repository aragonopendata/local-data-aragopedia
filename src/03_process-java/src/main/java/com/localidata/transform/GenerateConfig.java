package com.localidata.transform;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import org.apache.commons.collections.ListUtils;
import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

import com.localidata.bean.ConfigBean;
import com.localidata.bean.DataBean;
import com.localidata.bean.SkosBean;
import com.localidata.generic.Constants;
import com.localidata.generic.GithubApi;
import com.localidata.generic.GoogleDriveAPI;
import com.localidata.generic.Prop;
import com.localidata.util.Utils;

/**
 * 
 * @author Localidata
 */
public class GenerateConfig {

	private final static Logger log = Logger.getLogger(GenerateConfig.class);
	protected String inputDirectoryString = "D:\\trabajo\\gitOpenDataAragon2\\doc\\iaest\\DatosPrueba2";
	public static String configDirectoryString = "";
	protected String dimensionDirectoryString = "D:\\trabajo\\gitOpenDataAragon2\\doc\\iaest\\DimensionesFinales\\PosiblesDimensionesMenos20Valores";
	protected String[] extensions = new String[] { "csv", "txt" };
	public static HashMap<String, DataBean> skosExtrated = new HashMap<String, DataBean>();
	public static final String errorFileString = "errores.txt";
	protected ArrayList<DataBean> listConstants = new ArrayList<DataBean>();
	private HashMap<String, ConfigBean> configMap = new HashMap<>();
	private HashMap<File, ArrayList<File>> mappingGenerated = new HashMap<>();
	private HashSet<String> filesNotRDF = new HashSet<>();

	public GenerateConfig(String input, String dimension, String config) {
		inputDirectoryString = input;
		dimensionDirectoryString = dimension;
		configDirectoryString = config;
	}


	public void generateNewConfig(List<String> news) {
		log.info("Init generateNewConfig");
		HashMap<String, ConfigBean> configExtrated = new HashMap<String, ConfigBean>();

		int cont = 0;
		boolean update = false;
		for (String nuevo : news) {
			File file = new File(inputDirectoryString + File.separator + nuevo + ".csv");
			ArrayList<String> dimension = extractDimensionsLocal(file);

			ArrayList<DataBean> skosData = new ArrayList<DataBean>();
			String id = "";
			String letters = "";
			if (file.getName().endsWith("A.csv")) {
				id = file.getName().substring(0, file.getName().length() - 5);
				letters = file.getName().substring(file.getName().length() - 5, file.getName().length() - 4);
			} else {
				id = file.getName().substring(0, file.getName().length() - 6);
				letters = file.getName().substring(file.getName().length() - 6, file.getName().length() - 4);
			}

			log.info("Comienza tratamiento para " + id + letters);
			ConfigBean configBean = null;
			if (configExtrated.get(id) != null) {
				configBean = configExtrated.get(id);
			} else {
				configBean = new ConfigBean();
				configBean.setId(id);
			}
			configBean.getLetters().add(letters);
			configBean.setUpdated(update);
			try {
				List<String> csvLines = FileUtils.readLines(file, "UTF-8");
				String headerLine = Utils.weakClean(csvLines.get(0));
				String[] cells = headerLine.split("\t");
				for (int h = 0; h < cells.length; h++) {
					String name = cells[h];
					DataBean dataBean = null;
					if (configBean.getMapData().get(name) != null) {
						dataBean = configBean.getMapData().get(name);
					} else {
						dataBean = new DataBean();
						dataBean.setName(name.trim());
					}

					if (name.toLowerCase().contains("código") || name.toLowerCase().contains("codigo")) {
						dataBean.setNormalizacion("null");
						dataBean.setDimensionMesure("null");
						dataBean.setType("null");
					} else {

						if (contains(dimension, name + ".txt")) {

							dataBean.setDimensionMesure("dim");
							if (dataBean.getName().toLowerCase().contains("comarca")) {
								dataBean.setNormalizacion("sdmx-dimension:refArea");
								dataBean.setType("URI-comarca");
							} else if (dataBean.getName().toLowerCase().contains("municipio")) {
								dataBean.setNormalizacion("sdmx-dimension:refArea");
								dataBean.setType("URI-Municipio");
							} else if (dataBean.getName().toLowerCase().contains("provincia")) {
								dataBean.setNormalizacion("sdmx-dimension:refArea");
								dataBean.setType("URI-Provincia");
							} else if (dataBean.getName().toLowerCase().contains("comunidad") || dataBean.getName().toLowerCase().contains("aragón") || dataBean.getName().toLowerCase().contains("ccaa")) {
								dataBean.setNormalizacion("sdmx-dimension:refArea");
								dataBean.setType("URI-Comunidad");
							} else {
								if (name.toLowerCase().contains("año")) {
									String type = "";
									for (int j = 1; j < csvLines.size(); j++) {
										String line = Utils.weakClean(csvLines.get(j));
										if (Utils.v(line)) {
											String[] cellsLine = line.split("\t");
											if (cellsLine.length > 0 && cellsLine.length > h) {
												String cell = cellsLine[h];

												if (Utils.isDate(cell) && !type.equals("xsd:int")) {
													type = "xsd:date";
												} else if (Utils.isInteger(cell)) {
													type = "xsd:int";
												} else {
													log.info("La celda '" + cell + "' de la columna '" + name + "' no es un año");
													break;
												}
											}
										}
									}
									if (type.equals("xsd:date")) {
										dataBean.setNormalizacion("sdmx-dimension:refPeriod");
										dataBean.setType("xsd:date");
									} else if (type.equals("xsd:int")) {
										dataBean.setDimensionMesure("medida");
										dataBean.setNormalizacion(Prop.datasetName + "-measure:" + Utils.urlify(name));
										dataBean.setType("xsd:int");
									} else {
										dataBean.setNormalizacion(Prop.datasetName + "-dimension:" + Utils.urlify(name));
										dataBean.setType("xsd:string");
									}
								} else {
									dataBean.setNormalizacion(Prop.datasetName + "-dimension:" + Utils.urlify(name));
									dataBean.setType("skos:Concept");
									skosData.add(dataBean);

									String nameFile = "mapping-" + Utils.urlify(dataBean.getName());
									String pathFile = configDirectoryString + File.separator + nameFile + ".csv";
									ArrayList<File> listFiles = null;
									if (mappingGenerated.get(new File(pathFile)) == null) {
										listFiles = new ArrayList<>();
										listFiles.add(file);
										mappingGenerated.put(new File(pathFile), listFiles);
									} else {
										listFiles = mappingGenerated.get(new File(pathFile));
										listFiles.add(file);
										mappingGenerated.put(new File(pathFile), listFiles);
									}
								}
							}
						} else {
							dataBean.setDimensionMesure("medida");
							dataBean.setNormalizacion(Prop.datasetName + "-measure:" + Utils.urlify(name));
							String type = "";
							for (int j = 1; j < csvLines.size(); j++) {
								String line = Utils.weakClean(csvLines.get(j));
								if (Utils.v(line)) {
									String[] cellsLine = line.split("\t");
									if (cellsLine.length > 0 && cellsLine.length > h) {
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
							if (type.equals(""))
								type = "xsd:string";
							dataBean.setType(type);

						}
					}
					configBean.getMapData().put(dataBean.getName(), dataBean);
				}

				if (skosData.size() > 0)
					extractSkosConcept(csvLines, skosData, null);

			} catch (IOException e) {
				log.error("Error to read lines", e);
			}
			configExtrated.put(configBean.getId(), configBean);
			log.info("Finaliza tratamiento para " + id + letters);
		}

		cont = 0;
		for (String key : configExtrated.keySet()) {

			ConfigBean configBean = configExtrated.get(key);
			String letters = "-";
			for (String letter : configBean.getLetters()) {
				letters = letters + letter + "-";
			}
			letters = letters.substring(0, letters.length() - 1);
			configBean.setNameFile("Informe-" + configBean.getId() + letters + ".csv");
			log.info("Comienza a escribirse el archivo " + "Informe-" + configBean.getId() + letters + ".csv " + (++cont));
			if (configBean.isUpdated())
				configMap.put(configBean.getId(), configBean);
			configBean.toCSV();
			log.info("Finaliza de escribirse el archivo " + "Informe-" + configBean.getId() + letters + ".csv");
		}
		if (update) {
			generateSkosMapping(true);
		} else {
			generateSkosMapping(false);
		}
		log.info("End generateNewConfig");
	}

	public HashMap<String, ConfigBean> generateUpdateConfig() {
		log.info("Init generateUpdateConfig");
		HashMap<String, ConfigBean> configExtrated = new HashMap<String, ConfigBean>();
		File inputDirectoryFile = new File(inputDirectoryString);
		Collection<File> listCSV = FileUtils.listFiles(inputDirectoryFile, extensions, true);

		boolean update = false;
		for (File file : listCSV) {
			ArrayList<DataBean> skosData = new ArrayList<DataBean>();
			String id = "";
			String letters = "";
			if (file.getName().endsWith("A.csv")) {
				id = file.getName().substring(0, file.getName().length() - 5);
				letters = file.getName().substring(file.getName().length() - 5, file.getName().length() - 4);
			} else {
				id = file.getName().substring(0, file.getName().length() - 6);
				letters = file.getName().substring(file.getName().length() - 6, file.getName().length() - 4);
			}

			log.info("Comienza tratamiento para " + id + letters);
			ConfigBean configBean = null;
			if (configExtrated.get(id) != null) {
				configBean = configExtrated.get(id);
			} else {
				configBean = new ConfigBean();
				configBean.setId(id);
			}
			configBean.getLetters().add(letters);
			configBean.setUpdated(update);
			try {
				List<String> csvLines = FileUtils.readLines(file, "UTF-8");
				String headerLine = Utils.weakClean(csvLines.get(0));
				String[] cells = headerLine.split("\t");
				for (int h = 0; h < cells.length; h++) {
					String name = cells[h];
					DataBean dataBean = null;
					if (configBean.getMapData().get(name) != null) {
						dataBean = configBean.getMapData().get(name);
					} else {
						dataBean = new DataBean();
						dataBean.setName(name.trim());
						dataBean.setNameNormalized(Utils.urlify(name.trim()));
					}

					if (name.toLowerCase().contains("código") || name.toLowerCase().contains("codigo")) {
						dataBean.setNormalizacion("null");
						dataBean.setDimensionMesure("null");
						dataBean.setType("null");
					} else {
						configBean.getMapData().put(dataBean.getNameNormalized(), dataBean);
						skosData.add(dataBean);
					}

				}

				extractSkosConcept(csvLines, skosData, configBean);
				skosExtrated = new HashMap<String, DataBean>();

			} catch (IOException e) {
				log.error("Error to read lines", e);
			}
			configExtrated.put(id + letters, configBean);
			log.info("Finaliza tratamiento para " + id + letters);
		}

		log.info("End generateUpdateConfig");
		return configExtrated;
	}

	private void extractSkosConcept(List<String> csvLines, ArrayList<DataBean> skosData, ConfigBean configBean) {
		log.debug("Init extractSkosConcept");
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
						if (cells.length > posColumn[i]) {
							String cell = cells[posColumn[i]];
							SkosBean skosBean = new SkosBean();
							String skosUrified = Utils.urlify(cell);
							skosBean.setId(skosUrified);
							skosBean.setLabel(Utils.weakClean(cell));
							skosBean.setURI(Prop.host + "/kos/" + Prop.datasetName + "/" + Utils.urlify(skosData.get(i).getName()) + "/" + skosUrified);
							DataBean dataBean = null;
							if (skosExtrated.get(skosData.get(i).getName()) != null) {
								dataBean = skosExtrated.get(skosData.get(i).getName());
							} else {
								dataBean = skosData.get(i);
							}
							if (dataBean.getMapSkos().get(skosBean.getId()) == null) {
								dataBean.getMapSkos().put(skosBean.getId(), skosBean);
								skosExtrated.put(dataBean.getName(), dataBean);
								if (configBean != null)
									configBean.getMapData().put(dataBean.getName(), dataBean);
							}
						}
					} catch (ArrayIndexOutOfBoundsException e) {
						log.error("ERROR al extraer los skos debido a incoherencia de columnas", e);
						DataBean dataBean = null;
						if (skosExtrated.get(skosData.get(i).getName()) != null) {
							dataBean = skosExtrated.get(skosData.get(i).getName());
						} else {
							dataBean = skosData.get(i);
						}
						skosExtrated.put(dataBean.getName(), dataBean);
					}
				}
			}
		}
		log.debug("End extractSkosConcept");
	}


	private ArrayList<String> extractDimensionsLocal(File file) {
		log.debug("Init extractDimensionsLocal");
		ArrayList<String> result = new ArrayList<>();
		ArrayList<HashSet<String>> countValues = new ArrayList<>();
		List<String> csvLines = null;
		try {
			csvLines = FileUtils.readLines(file, "UTF-8");
		} catch (IOException e) {
			log.error("Error al leer el fichero " + file.getName());
		}
		String headerLine = Utils.weakClean(csvLines.get(0));
		String[] headerCells = Utils.split(headerLine, "\t");
		for (int h = 1; h < csvLines.size(); h++) {
			String line = csvLines.get(h);
			String[] cells = Utils.split(line, "\t");
			for (int u = 0; u < cells.length; u++) {
				if (u == countValues.size())
					countValues.add(new HashSet<String>());
				countValues.get(u).add(cells[u]);
			}
		}
		for (int g = 1; g < countValues.size(); g++) {
			HashSet<String> column = countValues.get(g);
			if (column.size() <= 20) {
				result.add(Utils.dimensionStrongClean(headerCells[g]) + ".txt");
			}

		}
		for (int o = 1; o < headerCells.length; o++) {
			if (headerCells[o].toLowerCase().contains("comarca") || headerCells[o].toLowerCase().contains("municipio") || headerCells[o].toLowerCase().contains("provincia") || headerCells[o].toLowerCase().contains("comunidad") || headerCells[o].toLowerCase().contains("aragón")
					|| headerCells[o].toLowerCase().contains("ccaa")) {
				result.add(Utils.dimensionStrongClean(headerCells[o]) + ".txt");
			}
		}
		log.debug("End extractDimensionsLocal");
		return result;
	}

	private boolean contains(ArrayList<String> set, String busqueda) {
		boolean result = false;
		for (String setString : set) {
			if (setString.equalsIgnoreCase(Utils.dimensionStrongClean(busqueda))) {
				result = true;
				break;
			}
		}
		return result;
	}

	public void generateSkosMapping(boolean update) {
		log.debug("Init generateSkosMapping");
		String filedSeparator = "\"";
		String csvSeparator = ",";

		for (String key : GenerateConfig.skosExtrated.keySet()) {
			StringBuffer content = new StringBuffer();
			DataBean data = GenerateConfig.skosExtrated.get(key);

			for (String skosName : data.getMapSkos().keySet()) {
				SkosBean skosBean = data.getMapSkos().get(skosName);
				if (Utils.v(skosBean.getId()))
					content.append(filedSeparator + skosBean.getLabel() + filedSeparator + csvSeparator + filedSeparator + skosBean.getURI() + filedSeparator + System.getProperty("line.separator"));
			}
			String nameFile = "mapping-" + Utils.urlify(data.getName());
			String pathFile = configDirectoryString + File.separator + nameFile + ".csv";
			log.info("comienza a escribirse el archivo " + nameFile + ".csv");
			File file = new File(pathFile);
			try {
				Utils.stringToFile(content.toString(), file);

				if (Prop.publishDrive) {
					if (!update) {
						GoogleDriveAPI api = new GoogleDriveAPI();
						api.init();
						com.google.api.services.drive.model.File f = api.searchFile(nameFile);
						if (f == null)
							api.createSpreadsheetFromFile(Prop.idParentFolder, Prop.emailUserFile, "csv", nameFile, file, "text/csv");
					}
				}
				log.info("finaliza de escribirse el archivo " + nameFile + ".csv");
			} catch (Exception e) {
				log.error("Error to generate skos mapping " + pathFile, e);
			}

		}
		log.debug("End generateSkosMapping");
	}

	public void updateConfig(List<String> changes, List<String> news, HashMap<String, ConfigBean> configMap) {
		log.info("init updateConfig");
		HashMap<String, ConfigBean> configExtrated = generateUpdateConfig();
		this.configMap = configMap;
		GoogleDriveAPI drive = new GoogleDriveAPI();
		drive.init();

		String mensaje = "";
		String mensajeAux = "";
		String mensajesNuevos = "";
		String mensajesCambiosNuevaConf = "";
		String mensajeCambiosConf = "";
		String mensajeRegenerar = "";

		generateNewConfig(news);
		for (String nuevo : news) {
			String id = "";
			String letters = "";
			if (nuevo.endsWith("A")) {
				id = nuevo.substring(0, nuevo.length() - 1);
				letters = nuevo.substring(nuevo.length() - 1, nuevo.length());
			} else {
				id = nuevo.substring(0, nuevo.length() - 2);
				letters = nuevo.substring(nuevo.length() - 2, nuevo.length());
			}
			filesNotRDF.add(id + letters);
			com.google.api.services.drive.model.File f = drive.searchFile(id);
			mensajesNuevos = mensajesNuevos + "Se ha detectado un nuevo cubo de datos " + nuevo + ", revise la nueva configuración propuesta y sus mapping " + f.getDefaultOpenWithLink() + "\n\n";
		}

		for (String change : changes) {
			log.info("Detectando si hay cambios en " + change);
			String id = "";
			String letters = "";
			boolean detectadoCambio = false;
			mensajeAux = "";
			
			if (change.endsWith("A")) {
				id = change.substring(0, change.length() - 1);
				letters = change.substring(change.length() - 1, change.length());
			} else {
				id = change.substring(0, change.length() - 2);
				letters = change.substring(change.length() - 2, change.length());
			}

			try {
				com.google.api.services.drive.model.File f = drive.searchFile(id);
				File fileDrive = drive.downloadFile("config", f, Constants.CSV);
				if (fileDrive == null)
					continue;
				List<String> csvLinesDrive = FileUtils.readLines(fileDrive, "UTF-8");
				List cellsDriveList = new ArrayList();
				String lineClean = csvLinesDrive.get(0);
				log.debug("csvLinesDrive "+lineClean);
				lineClean = lineClean.replace("'", "");
				int charac = 0;
				int first = -1;
				int last = -1;
				while (lineClean.contains("\"")) {
					if (lineClean.charAt(charac) == '"' && first == -1) {
						first = charac + 1;
					} else if (lineClean.charAt(charac) == '"' && last == -1) {
						last = charac;
						cellsDriveList.add(lineClean.substring(first, last));
						String aux = lineClean.substring(0, first - 1);
						String aux2 = lineClean.substring(last + 1, lineClean.length());
						lineClean = aux + aux2;
						first = -1;
						last = -1;
						charac = 0;
					}
					charac++;
				}
				Object[] cellsDriveArray = Utils.split(lineClean, ",");
				cellsDriveList.addAll(Arrays.asList(cellsDriveArray));
				ConfigBean config = configMap.get(id);

				File fileLocal = new File(inputDirectoryString + File.separator + change + ".csv");
				List<String> csvLinesLocal = FileUtils.readLines(fileLocal, "UTF-8");
				lineClean = csvLinesLocal.get(0);
				log.debug("csvLinesLocal "+lineClean);
				lineClean = lineClean.replace("'", "");
				lineClean = lineClean.replace("", "");
				Object[] cellsLocalArray = Utils.split(lineClean, "\t");
				List cellsLocalList = Arrays.asList(cellsLocalArray);
				ConfigBean configLocal = configExtrated.get(id + letters);

				if (ListUtils.subtract(cellsLocalList, cellsDriveList).size() > 0) {
					List list = ListUtils.subtract(cellsLocalList, cellsDriveList);
					mensajeAux = mensajeAux + "Se han añadido las columnas ";
					for (Object object : list) {
						if (cellsLocalList.contains(object) && cellsDriveList.contains(object))
							continue;
						mensajeAux = mensajeAux + "'" + object + "', ";
						detectadoCambio = true;
					}
					if (detectadoCambio) {
						mensajeAux = mensajeAux + " al cubo " + change + ", por favor actualice la configuración " + f.getDefaultOpenWithLink() + "\n\n";
						if(!mensajesCambiosNuevaConf.contains(mensajeAux))
							mensajesCambiosNuevaConf = mensajesCambiosNuevaConf + mensajeAux;
						List<String> lettersList = config.getLetters();
						for (String letter : lettersList) {
							filesNotRDF.add(config.getId() + letter);
						}
					}
				}
				if (config != null) {
					for (String key : config.getMapData().keySet()) {
						String provisionalMensaje = "Se han añadido los valores ";
						boolean sendEmail = false;
						DataBean data = config.getMapData().get(key);
						if (data.getType() != null && data.getType().equals(Constants.skosType)) {
							DataBean dataLocal = configLocal.getMapData().get(key);
							if (dataLocal != null)
								for (String key2 : dataLocal.getMapSkos().keySet()) {
									if (data.getMapSkos().get(key2) == null) {
										SkosBean skos = dataLocal.getMapSkos().get(key2);
										if (Utils.v(skos.getLabel())) {
											provisionalMensaje = provisionalMensaje + "'" + skos.getLabel() + "' ";
											sendEmail = true;
											detectadoCambio = true;
										}
									}
								}

						}
						if (sendEmail) {
							String fileName = fileLocal.getName().substring(0, fileLocal.getName().length() - 4);
							f = drive.searchFile(data.getNameNormalized());
							provisionalMensaje = provisionalMensaje + "al codelist " + fileName + ", por favor actualice la configuración " + f.getDefaultOpenWithLink() + "\n\n";
							if(!mensajeCambiosConf.contains(provisionalMensaje))
								mensajeCambiosConf = mensajeCambiosConf + provisionalMensaje;
							List<String> lettersList = config.getLetters();
							for (String letter : lettersList) {
								filesNotRDF.add(config.getId() + letter);
							}
						}
					}
				}
			} catch (IOException e) {
				log.error("Error chequeando las cambios ", e);
			}
			if (!detectadoCambio) {
				mensajeRegenerar = mensajeRegenerar + "En el cubo " + change + ", se han detectado nuevos registros y se va a regenerar el cubo de datos.\n\n";
			}
		}

		if (Utils.v(mensajesNuevos) || Utils.v(mensajesCambiosNuevaConf) || Utils.v(mensajeCambiosConf) || Utils.v(mensajeRegenerar)) {
			if (true) {
				mensaje = mensajesNuevos + mensajesCambiosNuevaConf + mensajeCambiosConf + mensajeRegenerar;
				String titulo = "Cambios en los datos del IAEsT " + Utils.getDate();
				GithubApi.createIssue(titulo, mensaje);
			}
		}
		log.info("end updateConfig");
	}

	public HashSet<String> getFilesNotRDF() {
		return filesNotRDF;
	}

	public void setFilesNotRDF(HashSet<String> filesNotRDF) {
		this.filesNotRDF = filesNotRDF;
	}

	public static void main(String[] args) {

		if ((log == null) || (log.getLevel() == null))
			PropertyConfigurator.configure("log4j.properties");
		if (args.length == 4) {
			log.info("Start process");
			Prop.loadConf();
			GenerateConfig config = null;
			if (args[0].equals("update")) {
				config = new GenerateConfig(args[2], args[3], args[4]);
			}

			log.info("Finish process");
		} 
	}

}
