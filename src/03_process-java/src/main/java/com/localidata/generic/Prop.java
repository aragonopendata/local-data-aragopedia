package com.localidata.generic;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.apache.log4j.Logger;

public class Prop {
	
	private final static Logger log = Logger.getLogger(Prop.class);
	public static String domainPermission = "";
	public static String p12File = "";
	public static String acountId = "";
	public static String APPLICATION_NAME = "";
	public static boolean publishDrive = true;
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
	
	public static String emailUser = "";
	
	public static String emailPassword = "";
	
	public static String emailDestination = "";
	
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
			
			emailUser = prop.getProperty("emailUser");
			emailPassword = prop.getProperty("emailPassword");
			emailDestination = prop.getProperty("emailDestination");
			
			conf = true;
		} catch (IOException io) {
			log.error("Error loading configuration", io);
		}
		return conf;
	}
}
