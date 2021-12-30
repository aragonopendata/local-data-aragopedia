package com.localidata.generic;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.apache.log4j.Logger;

/**
 * 
 * @author Localidata
 *
 */
public class Prop {

	private final static Logger log = Logger.getLogger(Prop.class);
	public static String domainPermission = "";
	public static String p12File = "";
	public static String acountId = "";
	public static String APPLICATION_NAME = "";
	public static boolean publishDrive = true;

	public static boolean downloadDrive = true;
	
	public static boolean createIssue = true;
	public static String emailUserFile = "";
	public static String idParentFolder = "";
	public static String kosName = "";
	public static String defName = "";
	public static String host = "";
	public static String eldaName = "";
	public static String datasetName = "";
	public static boolean addDataConstant;
	public static String formatConfig = "";
	public static String fileHashCSV = "";
	public static String fileErrorGeneric = "";
	public static String fileErrorBig = "";
	public static String fileErrorNotFound = "";
	public static String fileConfElda = "";
	
	public static int bom = 3;
	public static String emailUser = "";
	public static String emailPassword = "";
	public static String emailDestination = "";
	public static String apiKeyAragopedia = "";
	public static String githubToken = "";
	public static String githubURLIssues = "";
	public static String urlBiAragon = "";
	public static String initialDataCube = "";
	public static String nqUserBiAragon = "";
	public static String nqPasswordBiAragon = "";
	public static String sawUiAragonBiAragon = "";
	public static String oraBipsLbinfoBiAragon = "";
	public static String oraBipsNqidBiAragon = "";
	public static String utmaBiAragon = "";
	public static String utmcBiAragon = "";
	public static String utmzBiAragon = "";
	public static String urlQueryTodosGrafos = "";
	public static String urlGraph = "";
	public static String urlGraphCommonData = "";
	public static boolean limitCubeSizeActive = false;
	public static int limitCubeSize = 100000;
	public static int defaultTimeOut = 5000;
	public static int readTimeOut = 30000;
	
	
	public static boolean loadConf() {
		boolean conf = false;

		Properties prop = new Properties();

		try {
			InputStream input = new FileInputStream("system.properties");
			prop.load(input);

			domainPermission = prop.getProperty("domainPermission");
			p12File = prop.getProperty("p12File");
			acountId = prop.getProperty("acountId");
			APPLICATION_NAME = prop.getProperty("APPLICATION_NAME");
			
			createIssue = Boolean.valueOf(prop.getProperty("createIssue"));
			downloadDrive = Boolean.valueOf(prop.getProperty("downloadDrive"));
			publishDrive = Boolean.valueOf(prop.getProperty("publicDrive"));
			emailUserFile = prop.getProperty("emailUserFile");
			idParentFolder = prop.getProperty("idParentFolder");
			kosName = prop.getProperty("kosName");
			defName = prop.getProperty("defName");
			host = prop.getProperty("host");
			eldaName = prop.getProperty("eldaName");
			datasetName = prop.getProperty("datasetName");
			addDataConstant = Boolean.valueOf(prop.getProperty("addDataConstant"));
			formatConfig = prop.getProperty("formatConfig");
			fileHashCSV = prop.getProperty("fileHashCSV");
			fileErrorGeneric = prop.getProperty("fileErrorGeneric");
			fileErrorBig = prop.getProperty("fileErrorBig");
			fileErrorNotFound = prop.getProperty("fileErrorNotFound");
			fileConfElda = prop.getProperty("fileConfElda");
			bom = Integer.valueOf(prop.getProperty("bom"));
			
			emailUser = prop.getProperty("emailUser");
			emailPassword = prop.getProperty("emailPassword");
			emailDestination = prop.getProperty("emailDestination");

			apiKeyAragopedia = prop.getProperty("apiKeyAragopedia");
			
			githubToken = prop.getProperty("githubToken");
			githubURLIssues = prop.getProperty("githubURLIssues");

			urlBiAragon = prop.getProperty("urlBiAragon");
			initialDataCube = prop.getProperty("initialDataCube");
			nqUserBiAragon = prop.getProperty("nqUserBiAragon");
			nqPasswordBiAragon = prop.getProperty("nqPasswordBiAragon");
			sawUiAragonBiAragon = prop.getProperty("sawUiAragonBiAragon");
			oraBipsLbinfoBiAragon = prop.getProperty("oraBipsLbinfoBiAragon");
			oraBipsNqidBiAragon = prop.getProperty("oraBipsNqidBiAragon");
			utmaBiAragon = prop.getProperty("utmaBiAragon");
			utmcBiAragon = prop.getProperty("utmcBiAragon");
			utmzBiAragon = prop.getProperty("utmzBiAragon");
			
			urlQueryTodosGrafos = prop.getProperty("urlQueryTodosGrafos");
			urlGraph = prop.getProperty("urlGraph");
			urlGraphCommonData = prop.getProperty("urlGraphCommonData");
			
			
			limitCubeSizeActive = Boolean.valueOf(prop.getProperty("limitCubeSizeActive"));
			limitCubeSize = Integer.parseInt(prop.getProperty("limitCubeSize"));
			defaultTimeOut = Integer.parseInt(prop.getProperty("defaultTimeOut"));
			readTimeOut = Integer.parseInt(prop.getProperty("readTimeOut"));

			conf = true;
		} catch (IOException io) {
			log.error("Error loading configuration", io);
		}
		return conf;
	}
}
