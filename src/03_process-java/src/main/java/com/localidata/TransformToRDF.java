package com.localidata;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;


public class TransformToRDF {

	private final static Logger log = Logger.getLogger(GenerateData.class);

	private List<String> csvLines = null;

	private String rdfFinal = "";

	private String rdfProperties = "";

	protected static String errorsReport = "";
	
	private static HashSet<String> errorsSet = new HashSet<String>();
	
	protected static StringBuffer errorsContent = new StringBuffer();

	private ArrayList<String> cleanHeader = new ArrayList<String>();

	private ArrayList<String> normalizedHeader = new ArrayList<String>();

	private File outputDirectoryFile;

	private File propertiesFile;
	
	private File dsdFile;

	protected static File errorReportFile;
	
	private ConfigBean configBean;
	
	private HashSet<String> dsdSet = new HashSet<String>();
	
	
	private HashSet<String> propertiesSet = new HashSet<String>();
	
	protected static StringBuffer propertiesContent = new StringBuffer();
	
	String cubo="";
	

	public TransformToRDF(List<String> csvLines, File outputDirectoryFile,
			File propertiesFile, File dsdFile, File errorReportFile, ConfigBean configBean) {
		this.csvLines = csvLines;
		this.outputDirectoryFile = outputDirectoryFile;
		this.propertiesFile = propertiesFile;
		this.dsdFile = dsdFile;
		this.errorReportFile = errorReportFile;
		this.configBean = configBean;
	}


	public void initTransformation(String fileName, int numfile, String id, HashSet<String> dsdSet, HashSet<String> propertiesSet) {
		log.debug("Init initTransformation");
		if (this.csvLines != null) {
			log.debug("Start file " + fileName);
			this.dsdSet=dsdSet;
			this.propertiesSet=propertiesSet;
			StringBuffer lineAux = new StringBuffer();
			lineAux.append(addPrefix());
			log.debug("Insert prefix");
			boolean cabecera = true;
			int numLine = 1;
			String dsd="";
			
			for (int h = 0; h < csvLines.size(); h++) {
				if (h == (int) (csvLines.size() * 0.75)) {
					log.info("\tProcesed 75%");
				} else if (h == (int) (csvLines.size() * 0.5)) {
					log.info("\tProcesed 50%");
				} else if (h == (int) (csvLines.size() * 0.25))
					log.info("\tProcesed 25%");
				String dirtyLine = csvLines.get(h);
				String line = Utils.weakClean(dirtyLine);
				if(!Utils.v(line))
					continue;
				if (cabecera) {
					dsd=addHeader(line, csvLines.get(h + 2),
							id, numfile, configBean.getLetters());
					log.debug("Insert header");
					lineAux.append("#Observations\n");
					lineAux.append(addCubeLink(fileName,dsd));
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

		result.append("@prefix rdfs: <http://www.w3.org/2000/01/rdf-schema#> ."
				+ "\n");
		result.append("@prefix foaf: <http://xmlns.com/foaf/0.1/> ." + "\n");
		result.append("@prefix xsd: <http://www.w3.org/2001/XMLSchema#> ."
				+ "\n");
		result.append("@prefix owl: <http://www.w3.org/2002/07/owl#> ."
				+ "\n");
				result.append("@prefix rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> ."
				+ "\n");
		result.append("@prefix qb: <http://purl.org/linked-data/cube#> ."
				+ "\n");
		result.append("@prefix skos: <http://www.w3.org/2004/02/skos/core#> ."
				+ "\n");
		result.append("@prefix sdmx-concept:    <http://purl.org/linked-data/sdmx/2009/concept#> ."
				+ "\n");
		result.append("@prefix sdmx-dimension:  <http://purl.org/linked-data/sdmx/2009/dimension#> ."
				+ "\n");
		result.append("@prefix sdmx-attribute:  <http://purl.org/linked-data/sdmx/2009/attribute#> ."
				+ "\n");
		result.append("@prefix sdmx-measure:    <http://purl.org/linked-data/sdmx/2009/measure#> ."
				+ "\n");
		result.append("@prefix sdmx-metadata:   <http://purl.org/linked-data/sdmx/2009/metadata#> ."
				+ "\n");
		result.append("@prefix sdmx-code:       <http://purl.org/linked-data/sdmx/2009/code#> ."
				+ "\n");
		result.append("@prefix sdmx-subject:    <http://purl.org/linked-data/sdmx/2009/subject#> ."
				+ "\n");
		result.append("@prefix "+Prop.datasetName+"-dimension: <"+Prop.host+"/def/"+Prop.datasetName+"/dimension#> ."
				+ "\n");
		result.append("@prefix "+Prop.datasetName+"-measure:    <"+Prop.host+"/def/"+Prop.datasetName+"/medida#> ."
				+ "\n");
		result.append("@prefix dc: <http://purl.org/dc/elements/1.1/> .\n");
		
		result.append("\n");
		log.debug("End addPrefix");
		return result;
	}
	
	private  StringBuffer addCubeLink(String fileName, String dsd) {
		log.debug("Init addCubeLink");
		StringBuffer result = new StringBuffer();
		String url = Prop.host+"/"+Prop.eldaName+"/"+Prop.datasetName;
		cubo=url+"/dataset/"+fileName;
		result.append("<"+cubo+"> a qb:DataSet;\n");
		result.append("\tqb:structure <"+dsd+">;\n");
		result.append("\trdfs:label \"Cubo de datos para "+fileName+"\"@es ;\n");
		result.append("\trdfs:comment \"Cubo de datos para "+fileName+"\"@es ;\n");
		SimpleDateFormat formatFullDate = new SimpleDateFormat("yyyy-MM-dd");
		String fecha = formatFullDate.format(new Date());
		result.append("\tdc:modified \""+fecha+"\"^^xsd:date.\n");
		log.debug("End addCubeLink");
		return result;
	}


	private String addHeader(String headerLine, String nextLine,
			String fileName, int numfile, ArrayList<String> lettersList) {
		log.debug("Init addHeader "+headerLine+" "+nextLine+" "+fileName+" "+numfile);
		String resultado = "";
		boolean year = false;
		String aux = "";
		
		resultado = Prop.host+"/"+Prop.eldaName+"/"+Prop.datasetName+"/dsd/"+fileName;
		aux = "<"+ resultado + "> a qb:DataStructureDefinition ;" + "\n";
		aux = aux+"\trdfs:label \"Estructura de los cubos de datos que se corresponden con los informes "
				+ fileName + "\"@es ;" + "\n";
		String notation = "\"DSD-" + fileName+ "\"";
		aux = aux+"\tskos:notation "+notation+" ;" + "\n";
		String letters="";
		for (String letter : lettersList) {
			letters+=letter+" ";
		}
		aux = aux+"\trdfs:comment \"Esta estructura afecta a las areas: "+letters+"\" ." + "\n";
		aux = aux+"\n";
		insertDsd(aux, resultado+" "+notation);

		headerLine = Utils.weakClean(headerLine);
		String[] cells = headerLine.split("\t");

		int col = 1;
		int numCell = 1;

		for (int h = 0; h < cells.length; h++) {
			String cell = cells[h];
			String cleanCell = Utils.weakClean(cell);
			String normalizedCell = Utils.urlify(cell);

			DataBean dataBean = configBean.getMapData().get(normalizedCell);
			cleanHeader.add(cleanCell);
			normalizedHeader.add(normalizedCell);
			
			if(dataBean!=null){
				if(dataBean.getNormalizacion()!=null){
					
					boolean noRepetido = true;
					if(!propertiesSet.contains(dataBean.getNormalizacion())){
						propertiesSet.add(dataBean.getNormalizacion());
					}else{
						noRepetido=false;
					}

					if (!dataBean.getName().toLowerCase().contains("año")) {
						aux = "<"+ resultado + "> qb:component _:node" + numfile + "egmfx" + col + " ." + "\n";
						if(!dsdSet.contains(resultado+" "+dataBean.getDimensionMesure()+" "+dataBean.getNormalizacion())){
							Utils.stringToFileAppend(aux, dsdFile);
						}

						if(!dataBean.getType().contains(Constants.URIType)){
							aux = "_:node" + numfile + "egmfx"
									+ col + " "+dataBean.getDimensionMesure()+" "+dataBean.getNormalizacion() + " ." + "\n";
							aux = aux + "\n";
							insertDsd(aux, resultado+" "+dataBean.getDimensionMesure()+" "+dataBean.getNormalizacion());
							
							if(noRepetido){
								String coded = dataBean.getDimensionMesure().equals(Constants.mesure) ? "" : ", qb:CodedProperty ";
								propertiesContent.append(dataBean.getNormalizacion()
										+ " a "+dataBean.getDimensionMesureProperty()+" , rdf:Property"+coded+";"
										+ "\n");
								propertiesContent.append("\trdfs:label \"" + cleanCell
										+ "\"@es ;" + "\n");
								propertiesContent.append("\trdfs:comment \"" + cleanCell
										+ "\"@es ;" + "\n");
								propertiesContent.append("\trdfs:range "+dataBean.getType());
								if(dataBean.getType().equals(Constants.skosType)){
									propertiesContent.append(" ;" + "\n");
									if(dataBean.getMapSkos().keySet().size()>0){
										String key = dataBean.getMapSkos().keySet().iterator().next();
										String codeList = dataBean.getMapSkos().get(key).getURI();
										codeList = codeList.substring(0,codeList.lastIndexOf("/"));
										propertiesContent.append("\tqb:codeList <"+ codeList + "> ." + "\n");
									}else{
										if(cleanCell.equals("")){
											TransformToRDF.insertError(fileName + ". ERROR. CELL EMPTY "
													+ ". SKOS FOR THIS COLUMN NOT FOUND ");
											log.error(fileName + ". ERROR. CELL EMPTY "
													+ ". SKOS FOR THIS COLUMN NOT FOUND ");
										}
										TransformToRDF.insertError(fileName + ". ERROR. Column "
												+ cleanCell + ". SKOS FOR THIS COLUMN NOT FOUND ");
										log.error(fileName + ". ERROR. Column "
												+ cleanCell + ". SKOS FOR THIS COLUMN NOT FOUND ");
									}
								}else{
									propertiesContent.append(" ." + "\n");
								}
								propertiesContent.append("" + "\n");
								Utils.stringToFileAppend(propertiesContent.toString(), propertiesFile);
								propertiesContent.setLength(0);
							}
							
						}else{
							aux = "_:node" + numfile + "egmfx" + col
									+ " "+dataBean.getDimensionMesure()+" "+dataBean.getDimensionMesureSDMX()+":refArea ." + "\n";
							aux = aux + "\n";
							insertDsd(aux, resultado+" "+dataBean.getDimensionMesure()+" "+dataBean.getDimensionMesureSDMX()+":refArea");
						}
					}else{
						year = true;
						aux = "<"+resultado + "> qb:component _:node" + numfile
								+ "egmfx" + col + " ." + "\n";
						aux = aux + "_:node" + numfile + "egmfx" + col
								+ " "+dataBean.getDimensionMesure()+" "+dataBean.getDimensionMesureSDMX()+":refPeriod ." + "\n";
						aux = aux + "\n";
						insertDsd(aux, resultado+" "+dataBean.getDimensionMesure()+" "+dataBean.getDimensionMesureSDMX()+":refPeriod");
					}
					col++;
				}
			}else{
				if(normalizedCell.equals("")){
					TransformToRDF.insertError(fileName + ". ERROR. CELL EMPTY "
							+ ". CONFIGURATION FOR THIS COLUMN NOT FOUND ");
					log.error(fileName + ". ERROR. CELL EMPTY "
							+ ". CONFIGURATION FOR THIS COLUMN NOT FOUND ");
				}
				TransformToRDF.insertError(fileName + ". ERROR. Column "
						+ normalizedCell + ". CONFIGURATION FOR THIS COLUMN NOT FOUND ");
				log.error(fileName + ". ERROR. Column "
						+ normalizedCell + ". CONFIGURATION FOR THIS COLUMN NOT FOUND ");
			}
		}

		if (!year && cells.length > 1) {
			aux = "<"+resultado + "> qb:component _:node" + numfile + "egmfx"
					+ col + " ." + "\n";
			aux = aux + "_:node" + numfile + "egmfx" + col
					+ " qb:dimension sdmx-dimension:refPeriod ." + "\n";
			aux = aux + "\n";
			insertDsd(aux, resultado+" qb:dimension sdmx-dimension:refPeriod");

		}
		if(configBean.getListDataConstant().size()>0){
			for (DataBean data : configBean.getListDataConstant()) {
				aux = "<"+resultado + "> qb:component _:node" + numfile + "egmfx"
						+ col + " ." + "\n";
				aux = aux + "_:node" + numfile + "egmfx" + col
						+ " "+data.getDimensionMesure()+" "+data.getNormalizacion()+" ." + "\n";
				aux = aux + "\n";
				insertDsd(aux, resultado+" "+data.getDimensionMesure()+" "+data.getNormalizacion());

			}
		}
		
		log.debug("End addHeader");
		return resultado;
	}

	/**
	 * Method to insert observation RDF
	 * 
	 * @param line
	 *            String with header line
	 * @param fileName
	 *            String file name
	 * @return String with observation RDF
	 */
	private StringBuffer addObservation(String line, String fileName) {
		log.debug("Init addObservation "+line+" "+fileName);
		StringBuffer result = new StringBuffer();
		String endResult = "";
		boolean year=false;
		String cleanLine = Utils.weakClean(line);
		if (cleanLine.equals("")) {
			log.debug("End addObservation");
			return result;
		}
		String id = Utils.genUUIDHash(cleanLine);
		result.append("<"+Prop.host+"/"+Prop.eldaName+"/"+Prop.datasetName+"/observacion/"
				+ fileName + "/" + id + "> a qb:Observation ;" + "\n");
		result.append("\tqb:DataSet <"+cubo+ ">; \n");

		String[] cells = cleanLine.split("\t");
		int col = 1;
		for (String cell : cells) {
			String normalizedCell = Utils.urlify(cell);
			String header = normalizedHeader.get(col - 1);
			String headerclean = cleanHeader.get(col - 1);
			DataBean dataBean = configBean.getMapData().get(header);
			try{
				if(dataBean!=null){
					if (dataBean.getNormalizacion()!=null){
						if (normalizedCell.equals("")) {
							TransformToRDF.insertError(fileName + ". ERROR. Column "
									+ header + ". NO VALUE ");
						} else {
							
							if(!dataBean.getType().contains(Constants.URIType)){
								if (!dataBean.getType().equals("xsd:date")){
									if(dataBean.getType().equals(Constants.skosType)){
										if(dataBean.getMapSkos().get(normalizedCell)==null){
											TransformToRDF.insertError(fileName + ". ERROR. Column "
													+ header + ". NO SKOS VALID BY "+normalizedCell);
											log.error(fileName + ". ERROR. Column "
													+ header + ". NO SKOS VALID BY "+normalizedCell);
										}else{
											result.append("\t"+dataBean.getNormalizacion()
													+ " <"+ dataBean.getMapSkos().get(normalizedCell).getURI() +"> ;"
													+ "\n");
											dataBean.getMapSkos().get(normalizedCell).setLabel(Utils.weakClean(cell));
										}
									}else{
										result.append("\t"+dataBean.getNormalizacion()
												+ " \""+normalizedCell+"\"^^"+dataBean.getType()+";"
												+ "\n");
									}
								}else{
									if(dataBean.getType().equals("xsd:date")){
										year=true;
										result.append("\t"+dataBean.getNormalizacion()+" <http://reference.data.gov.uk/id/year/"
												+ normalizedCell + "> ;" + "\n");
									}
								}
							}else{
								String pattern = "([0-9]+)(-)(.*)";
								Pattern r = Pattern.compile(pattern);
								Matcher m = r.matcher(cell);
								if (m.find()) {
									TransformToRDF.insertError(fileName
											+ ". WARNING. Column " + header
											+ ". MIXED CODE AND VALUE");
									cell = cell.substring(cell.indexOf("-") + 1,
											cell.length());
								}
								String urlRefArea = Utils.getUrlRefArea(header, cell,
										fileName);
								result.append("\t"+dataBean.getDimensionMesureSDMX()+":refArea "
										+ urlRefArea + " ;" + "\n");
							}	
							
						}
					}
				}else{
					if(header.equals("")){
						TransformToRDF.insertError(fileName + ". ERROR. HEADER COLUMN EMPTY "
								+ ". CONFIGURATION FOR THIS COLUMN NOT FOUND ");
						log.error(fileName + ". ERROR. HEADER COLUMN EMPTY "
								+ ". CONFIGURATION FOR THIS COLUMN NOT FOUND ");
					}
					TransformToRDF.insertError(fileName + ". ERROR. Column "
							+ header + ". CONFIGURATION FOR THIS COLUMN NOT FOUND ");
					log.error(fileName + ". ERROR. Column "
							+ header + ". CONFIGURATION FOR THIS COLUMN NOT FOUND ");
				}
				col++;
			}catch(Exception e){
				log.error("Error al añadir una observacion en "+configBean.getNameFile(), e);
			}
		}
		if(!year){
			 result.append("\tsdmx-dimension:refPeriod <http://reference.data.gov.uk/id/year/2011> ."
				+ "\n");
		}
		if(configBean.getListDataConstant().size()>0){
			for (DataBean data : configBean.getListDataConstant()) {
				result.append("\t"+data.getNormalizacion()+" "+data.getConstant()+" ."
						+ "\n");
			}
		}
		endResult = (result.toString()).substring(0, result.length()-2);
		endResult = endResult+"." + "\n";
		result = new StringBuffer(endResult);
		log.debug("End addObservation");
		return result;
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

	protected static void insertError(String error) {
		log.debug("Init insertError "+error);
		if(!errorsSet.contains(error)){
			errorsSet.add(error);
			Utils.stringToFileAppend(error+"\n", errorReportFile);
		}
		log.debug("End insertError");
	}
	
	protected void insertDsd(String aux, String search) {
		log.debug("Init insertDsd "+aux+" "+search);
		if(!dsdSet.contains(search)){
			dsdSet.add(search);
			Utils.stringToFileAppend(aux, dsdFile);
		}
		log.debug("End insertDsd");
	}
}
