package com.localidata.process;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;

import com.localidata.bean.ConfigBean;
import com.localidata.bean.DataBean;
import com.localidata.generic.Constants;
import com.localidata.generic.Prop;
import com.localidata.util.Utils;

/**
 * 
 * @author Localidata
 *
 */
public class TransformToRDF {

	private final static Logger log = Logger.getLogger(TransformToRDF.class);
	private List<String> csvLines = null;
	private String rdfFinal = "";
	private String rdfProperties = "";
	protected static String errorsReport = "";
	private static ArrayList<String> errorsList = new ArrayList<String>();
	private ArrayList<String> cleanHeader = new ArrayList<String>();
	private ArrayList<String> normalizedHeader = new ArrayList<String>();
	private File outputDirectoryFile;
	private static File propertiesFile;
	private static File dsdFile;
	protected static File errorReportFile;
	private ConfigBean configBean;
	private static ArrayList<String> dsdList = new ArrayList<String>();
	private static ArrayList<String> propertiesList = new ArrayList<String>();
	protected static StringBuffer propertiesContent = new StringBuffer();
	String cubo = "";
	private static String specsTtlFileString;
	private static String decription;
	private static ArrayList<String> viewsSpecsTtl = new ArrayList<>();

	public TransformToRDF(List<String> csvLines, File outputDirectoryFile, File propertiesFile, File dsdFile, File errorReportFile, ConfigBean configBean, String specsTtl) {
		this.csvLines = csvLines;
		this.outputDirectoryFile = outputDirectoryFile;
		TransformToRDF.propertiesFile = propertiesFile;
		TransformToRDF.dsdFile = dsdFile;
		TransformToRDF.errorReportFile = errorReportFile;
		this.configBean = configBean;
		TransformToRDF.specsTtlFileString = specsTtl;

	}

	public TransformToRDF(File propertiesFile, File dsdFile, File errorReportFile, String specsTtl) {
		TransformToRDF.propertiesFile = propertiesFile;
		TransformToRDF.dsdFile = dsdFile;
		TransformToRDF.errorReportFile = errorReportFile;
		TransformToRDF.specsTtlFileString = specsTtl;

	}

	public void initTransformation(String fileName, int numfile, String id, ArrayList<String> dsdList, ArrayList<String> propertiesList, String decription) {
		log.debug("Init initTransformation");
		if (this.csvLines != null && this.csvLines.size() >= 2) {
			log.debug("Start file " + fileName);
			TransformToRDF.dsdList = dsdList;
			TransformToRDF.propertiesList = propertiesList;
			TransformToRDF.decription = decription;
			StringBuffer lineAux = new StringBuffer();
			lineAux.append(addPrefix());
			log.debug("Insert prefix");
			boolean cabecera = true;
			int numLine = 1;
			String dsd = "";

			for (int h = 0; h < csvLines.size(); h++) {
				if (h == (int) (csvLines.size() * 0.75)) {
					log.info("\tProcesed 75%");
				} else if (h == (int) (csvLines.size() * 0.5)) {
					log.info("\tProcesed 50%");
				} else if (h == (int) (csvLines.size() * 0.25))
					log.info("\tProcesed 25%");
				String dirtyLine = csvLines.get(h);
				String line = Utils.weakClean(dirtyLine);
				if (!Utils.v(line))
					continue;
				if (cabecera) {
					dsd = Prop.host + "/" + Prop.eldaName + "/" + Prop.datasetName + "/dsd/" + id;
					if(csvLines.size()<=2){
						addHeader(line,csvLines.get(h + 1), id, numfile, configBean.getLetters());
					}else{
						addHeader(line, csvLines.get(h + 2), id, numfile, configBean.getLetters());
					}
					
					
					log.debug("Insert header");
					lineAux.append("#Observations\n");
					lineAux.append(addCubeLink(fileName, dsd));
					cabecera = false;
				} else {
					lineAux.setLength(0);
					lineAux.append(addObservation(line, fileName));
					log.debug("Insert line " + numLine);
				}
				Utils.stringToFileAppend(lineAux.toString(), outputDirectoryFile);
				numLine++;
			}
			log.debug("Insert observations");

		}
		log.debug("End initTransformation");
	}

	public static StringBuffer addPrefix() {
		log.debug("Init addPrefix");
		StringBuffer result = new StringBuffer();

		result.append("@prefix rdfs: <http://www.w3.org/2000/01/rdf-schema#> ." + "\n");
		result.append("@prefix foaf: <http://xmlns.com/foaf/0.1/> ." + "\n");
		result.append("@prefix xsd: <http://www.w3.org/2001/XMLSchema#> ." + "\n");
		result.append("@prefix owl: <http://www.w3.org/2002/07/owl#> ." + "\n");
		result.append("@prefix rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> ." + "\n");
		result.append("@prefix qb: <http://purl.org/linked-data/cube#> ." + "\n");
		result.append("@prefix skos: <http://www.w3.org/2004/02/skos/core#> ." + "\n");
		result.append("@prefix sdmx-concept:    <http://purl.org/linked-data/sdmx/2009/concept#> ." + "\n");
		result.append("@prefix sdmx-dimension:  <http://purl.org/linked-data/sdmx/2009/dimension#> ." + "\n");
		result.append("@prefix sdmx-attribute:  <http://purl.org/linked-data/sdmx/2009/attribute#> ." + "\n");
		result.append("@prefix sdmx-measure:    <http://purl.org/linked-data/sdmx/2009/measure#> ." + "\n");
		result.append("@prefix sdmx-metadata:   <http://purl.org/linked-data/sdmx/2009/metadata#> ." + "\n");
		result.append("@prefix sdmx-code:       <http://purl.org/linked-data/sdmx/2009/code#> ." + "\n");
		result.append("@prefix sdmx-subject:    <http://purl.org/linked-data/sdmx/2009/subject#> ." + "\n");
		result.append("@prefix " + Prop.datasetName + "-dimension: <" + Prop.host + "/def/" + Prop.datasetName + "/dimension#> ." + "\n");
		result.append("@prefix " + Prop.datasetName + "-measure:    <" + Prop.host + "/def/" + Prop.datasetName + "/medida#> ." + "\n");
		result.append("@prefix dc: <http://purl.org/dc/elements/1.1/> .\n");
		result.append("@prefix dct: <http://purl.org/dc/terms/> .\n");

		result.append("\n");
		log.debug("End addPrefix");
		return result;
	}

	private StringBuffer addCubeLink(String fileName, String dsd) {
		log.debug("Init addCubeLink");
		StringBuffer result = new StringBuffer();
		String url = Prop.host + "/" + Prop.eldaName + "/" + Prop.datasetName;
		cubo = url + "/dataset/" + fileName;
		result.append("<" + cubo + "> a qb:dataSet;\n");
		result.append("\tqb:structure <" + dsd + ">;\n");
		result.append("\trdfs:label \"" + decription + "\"@es ;\n");
		result.append("\tdct:identifier  \"" + fileName + "\" ;\n");
		result.append("\trdfs:comment \"Cubo de datos para " + fileName + "\"@es ;\n");
		SimpleDateFormat formatFullDate = new SimpleDateFormat("yyyy-MM-dd");
		String fecha = formatFullDate.format(new Date());
		result.append("\tdc:modified \"" + fecha + "\"^^xsd:date.\n");
		log.debug("End addCubeLink");
		return result;
	}

	private void addHeader(String headerLine, String nextLine, String fileName, int numfile, ArrayList<String> lettersList) {
		log.debug("Init addHeader " + headerLine + " " + nextLine + " " + fileName + " " + numfile);
		headerLine = Utils.weakClean(headerLine);
		String[] cells = headerLine.split("\t");

		for (int h = 0; h < cells.length; h++) {
			String cell = cells[h];
			String cleanCell = Utils.weakClean(cell);
			String normalizedCell = Utils.urlify(cell);

			cleanHeader.add(cleanCell);
			normalizedHeader.add(normalizedCell);

		}
		log.debug("End addHeader");
	}

	private StringBuffer addObservation(String line, String fileName) {
		log.debug("Init addObservation " + line + " " + fileName);
		StringBuffer result = new StringBuffer();
		String endResult = "";
		boolean year = false;
		boolean month = false;
		String cleanLine = Utils.weakClean(line);
		if (cleanLine.equals("")) {
			log.debug("End addObservation");
			return result;
		}
		String id = Utils.genUUIDHash(cleanLine);
		result.append("<" + Prop.host + "/" + Prop.eldaName + "/" + Prop.datasetName + "/observacion/" + fileName + "/" + id + "> a qb:Observation ;" + "\n");
		result.append("\tqb:dataSet <" + cubo + ">; \n");

		String[] cells = cleanLine.split("\t");
		int col = 1;
		for (String cell : cells) {
			String normalizedCell = Utils.urlify(cell);
			if (normalizedHeader.size() <= col - 1) {
				TransformToRDF.insertError(fileName + ". ERROR. " + "COLUMN NAME MISSING  ");
				log.error(fileName + ". ERROR. " + "COLUMN NAME MISSING  ");
				continue;
			}
			String header = normalizedHeader.get(col - 1);
			cleanHeader.get(col - 1);
			DataBean dataBean = configBean.getMapData().get(header);
			try {
				if (dataBean != null) {
					if (dataBean.getNormalizacion() != null) {
						if (normalizedCell.equals("")) {
							TransformToRDF.insertError(fileName + ". ERROR. Column " + header + ". NO VALUE ");
						} else {

							if (!dataBean.getType().contains(Constants.URIType)) {
								if (!dataBean.getNormalizacion().equals("sdmx-dimension:refPeriod")) {
									if (dataBean.getType().equals(Constants.skosType)) {
										if (dataBean.getMapSkos().get(normalizedCell) == null) {
											TransformToRDF.insertError(fileName + ". ERROR. Column " + header + ". NO SKOS VALID BY " + normalizedCell);
											log.error(fileName + ". ERROR. Column " + header + ". NO SKOS VALID BY " + normalizedCell);
										} else {
											result.append("\t" + dataBean.getNormalizacion() + " <" + dataBean.getMapSkos().get(normalizedCell).getURI() + "> ;" + "\n");
											dataBean.getMapSkos().get(normalizedCell).setLabel(Utils.weakClean(cell));
										}
									} else {
										if (dataBean.getType().equals(Constants.doubleType)) {
											cell = cell.replace(",", ".");
										}
										result.append("\t" + dataBean.getNormalizacion() + " \"" + Utils.weakClean(cell) + "\"^^" + dataBean.getType() + ";" + "\n");
									}
								} else {
									if (dataBean.getNormalizacion().equals("sdmx-dimension:refPeriod")) {
										try{
											if (dataBean.getNameNormalized().equals("ano")){
												year = true;
												result.append("\t" + dataBean.getNormalizacion() + " <http://reference.data.gov.uk/id/year/" + normalizedCell + "> ;" + "\n");
											} else if (dataBean.getNameNormalized().equals("mes-codigo")){
												month = true;
												result.append("\t" + dataBean.getNormalizacion() + " <http://reference.data.gov.uk/id/month/" + normalizedCell.substring(0, 4) + "-" + normalizedCell.substring(4, 6) + "> ;" + "\n");
											} else {
												result.append("\t" + dataBean.getNormalizacion() + " <http://reference.data.gov.uk/id/year/2011> ;" + "\n");
											}
										}catch (Exception e) {
											log.error("[M] Error formateando refPedriod: " + e);
										}
									}
								}
							} else {
								String pattern = "([0-9]+)(-)(.*)";
								Pattern r = Pattern.compile(pattern);
								Matcher m = r.matcher(cell);
								if (m.find()) {
									TransformToRDF.insertError(fileName + ". WARNING. Column " + header + ". MIXED CODE AND VALUE");
									cell = cell.substring(cell.indexOf("-") + 1, cell.length());
								}
								if(Utils.v(cell)){
									String urlRefArea = Utils.getUrlRefArea(header, cell, fileName);
									if(Utils.v(urlRefArea))
										result.append("\t" + dataBean.getDimensionMesureSDMX() + ":refArea " + urlRefArea + " ;" + "\n");
								}
							}

						}
					}
				} else {
					if (header.equals("")) {
						TransformToRDF.insertError(fileName + ". ERROR. HEADER COLUMN EMPTY " + ". CONFIGURATION FOR THIS COLUMN NOT FOUND ");
						log.error(fileName + ". ERROR. HEADER COLUMN EMPTY " + ". CONFIGURATION FOR THIS COLUMN NOT FOUND ");
					}
					TransformToRDF.insertError(fileName + ". ERROR. Column " + header + ". CONFIGURATION FOR THIS COLUMN NOT FOUND ");
					log.error(fileName + ". ERROR. Column " + header + ". CONFIGURATION FOR THIS COLUMN NOT FOUND ");
				}
				col++;
			} catch (Exception e) {
				log.error("Error al añadir una observacion en " + configBean.getNameFile(), e);
			}
		}
		if (configBean.getListDataConstant().size() > 0) {
			for (DataBean data : configBean.getListDataConstant()) {
				result.append("\t" + data.getNormalizacion() + " " + data.getConstant() );
				if (Utils.v(data.getType()))
					result.append("^^" + data.getType() + " ." + "\n");
				else
					result.append(" ." + "\n");
				if (data.getNormalizacion().equals("sdmx-dimension:refPeriod")) {
					year = true;
				}
			}
		}
		if (!year && !month) {
			result.append("\tsdmx-dimension:refPeriod <http://reference.data.gov.uk/id/year/2011> ." + "\n");
		}

		endResult = (result.toString()).substring(0, result.length() - 2);
		endResult = endResult + "." + "\n";
		result = new StringBuffer(endResult);
		log.debug("End addObservation");
		return result;
	}

	public void generateCommonData(HashMap<String, ConfigBean> mapconfig, HashMap<String, String> idDescription) {
		int numfile = 1;

		File specsTtlFile = new File(specsTtlFileString);
		try {
			List<String> specsLines = FileUtils.readLines(specsTtlFile, "UTF-8");
			for (String line : specsLines) {
				if (line.contains(" api:label ")) {
					String[] column = line.split(" ");
					viewsSpecsTtl.add(column[0]);
				}
			}
		} catch (Exception e) {
			log.error("Error generando los datos comunes (dsd, properties, kos)", e);
		}

		for (String keyConfig : mapconfig.keySet()) {
			ConfigBean config = mapconfig.get(keyConfig);

			boolean year = false;
			String resultado = "";
			String aux = "";

			resultado = Prop.host + "/" + Prop.eldaName + "/" + Prop.datasetName + "/dsd/" + config.getId();
			aux = "<" + resultado + "> a qb:DataStructureDefinition ;" + "\n";
			String description = "";
			if(config!=null && config.getId()!=null && config.getLetters()!=null && config.getLetters().size()>0)
				description = idDescription.get(config.getId() + config.getLetters().get(0)) != null ? idDescription.get(config.getId() + config.getLetters().get(0)) : "";
			aux = aux + "\trdfs:label \"Estructura de los cubos de datos que se corresponden con los informes " + config.getId() + ", " + description + "\"@es ;" + "\n";
			String notation = "\"DSD-" + config.getId() + "\"";
			aux = aux + "\tskos:notation " + notation + " ;" + "\n";
			String letters = "";
			for (String letter : config.getLetters()) {
				letters += letter + " ";
			}
			aux = aux + "\trdfs:comment \"Esta estructura afecta a las areas: " + letters + "\"^^xsd:string ." + "\n";
			aux = aux + "\n";
			insertDsd(aux, resultado + " " + notation);

			int col = 1;
			for (String keyData : config.getMapData().keySet()) {
				DataBean data = config.getMapData().get(keyData);

				boolean noRepetido = true;
				if (Utils.v(data.getNormalizacion())) {
					if (!propertiesList.contains(data.getNormalizacion())) {
						propertiesList.add(data.getNormalizacion());
						if (!viewsSpecsTtl.contains(data.getNormalizacion())) {
							insertViewTTL(data.getNormalizacion());
						}

					} else {
						noRepetido = false;
					}
					if (!data.getNormalizacion().contains("sdmx-dimension:refPeriod")) {
						aux = "<" + resultado + "> qb:component _:node" + numfile + "egmfx" + col + " ." + "\n";
						if (!dsdList.contains(resultado + " " + data.getDimensionMesure() + " " + data.getNormalizacion())) {
							Utils.stringToFileAppend(aux, dsdFile);
						}
						if (!data.getType().contains(Constants.URIType)) {
							aux = "_:node" + numfile + "egmfx" + col + " " + data.getDimensionMesure() + " " + data.getNormalizacion() + " ." + "\n";
							aux = aux + "\n";
							insertDsd(aux, resultado + " " + data.getDimensionMesure() + " " + data.getNormalizacion());
							if (noRepetido && data.isWriteSkos()) {
								String coded = data.getDimensionMesure().equals(Constants.mesure) ? "" : ", qb:CodedProperty ";
								propertiesContent.append(data.getNormalizacion() + " a " + data.getDimensionMesureProperty() + " , rdf:Property" + coded + ";" + "\n");
								propertiesContent.append("\trdfs:label \"" + Utils.weakClean(data.getKosName()) + "\"@es ;" + "\n");
								propertiesContent.append("\trdfs:comment \"" + Utils.weakClean(data.getKosName()) + "\"@es ;" + "\n");
								propertiesContent.append("\trdfs:range " + data.getType());
								if (data.getType().equals(Constants.skosType)) {
									if (data.getMapSkos().keySet().size() > 0) {
										String key = data.getMapSkos().keySet().iterator().next();
										if (Utils.v(key)) {
											String codeList = data.getMapSkos().get(key).getURI();
											if (Utils.v(codeList)) {
												propertiesContent.append(" ;" + "\n");
												codeList = codeList.substring(0, codeList.lastIndexOf("/"));
												propertiesContent.append("\tqb:codeList <" + codeList + "> ." + "\n");
											}else{
												propertiesContent.append(".\n");
											}
										}else{
											propertiesContent.append(".\n");
										}
									} else {
										propertiesContent.append(" ." + "\n");
										if (Utils.weakClean(data.getName()).equals("")) {
											TransformToRDF.insertError(config.getId() + ". ERROR. CELL EMPTY " + ". SKOS FOR THIS COLUMN NOT FOUND ");
											log.error(config.getId() + ". ERROR. CELL EMPTY " + ". SKOS FOR THIS COLUMN NOT FOUND ");
										}
										TransformToRDF.insertError(config.getId() + ". ERROR. Column " + Utils.weakClean(data.getName()) + ". SKOS FOR THIS COLUMN NOT FOUND ");
										log.error(config.getId() + ". ERROR. Column " + Utils.weakClean(data.getName()) + ". SKOS FOR THIS COLUMN NOT FOUND ");
									}
								} else {
									propertiesContent.append(" ." + "\n");
								}
								propertiesContent.append("" + "\n");
								Utils.stringToFileAppend(propertiesContent.toString(), propertiesFile);
								propertiesContent.setLength(0);
							}

						} else {
							aux = "_:node" + numfile + "egmfx" + col + " " + data.getDimensionMesure() + " " + data.getDimensionMesureSDMX() + ":refArea ." + "\n";
							aux = aux + "\n";
							insertDsd(aux, resultado + " " + data.getDimensionMesure() + " " + data.getDimensionMesureSDMX() + ":refArea");
						}
					} else {
						year = true;
						aux = "<" + resultado + "> qb:component _:node" + numfile + "egmfx" + col + " ." + "\n";
						aux = aux + "_:node" + numfile + "egmfx" + col + " " + data.getDimensionMesure() + " " + data.getDimensionMesureSDMX() + ":refPeriod ." + "\n";
						aux = aux + "\n";
						insertDsd(aux, resultado + " " + data.getDimensionMesure() + " " + data.getDimensionMesureSDMX() + ":refPeriod");
					}
					col++;
				}
			}
			if (!year) {
				aux = "<" + resultado + "> qb:component _:node" + numfile + "egmfx" + col + " ." + "\n";
				aux = aux + "_:node" + numfile + "egmfx" + col + " qb:dimension sdmx-dimension:refPeriod ." + "\n";
				aux = aux + "\n";
				insertDsd(aux, resultado + " qb:dimension sdmx-dimension:refPeriod");
			}
			numfile++;

		}
	}

	private static void insertViewTTL(String view) {

		File specsTtl = new File(specsTtlFileString);
		String line = view + " api:label \"" + view.replace(":", "_") + "\".\n";
		Utils.stringToFileAppend(line, specsTtl);

	}

	public String getRdfFinal() {
		return rdfFinal;
	}

	public void setRdfFinal(String rdfFinal) {
		this.rdfFinal = rdfFinal;
	}

	public String getRdfProperties() {
		return rdfProperties;
	}

	public void setRdfProperties(String rdfProperties) {
		this.rdfProperties = rdfProperties;
	}

	public static void insertError(String error) {
		log.debug("Init insertError " + error);
		if (!errorsList.contains(error)) {
			errorsList.add(error);
			Utils.stringToFileAppend(error + "\n", errorReportFile);
		}
		log.debug("End insertError");
	}

	protected static void insertDsd(String aux, String search) {
		log.debug("Init insertDsd " + aux + " " + search);
		if (!dsdList.contains(search)) {
			dsdList.add(search);
			Utils.stringToFileAppend(aux, dsdFile);
		}
		log.debug("End insertDsd");
	}
}
