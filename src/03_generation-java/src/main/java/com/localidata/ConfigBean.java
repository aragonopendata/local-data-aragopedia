package com.localidata;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;

import org.apache.log4j.Logger;

public class ConfigBean {
	

	private final static Logger log = Logger.getLogger(ConfigBean.class);

	private String nameFile;

	private String id;

	private ArrayList<String> letters = new ArrayList<String>();

	private HashMap<String, DataBean> mapData = new HashMap<String, DataBean>();
	
	public String getNameFile() {
		return nameFile;
	}
	public void setNameFile(String nameFile) {
		this.nameFile = nameFile;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}	
	public HashMap<String, DataBean> getMapData() {
		return mapData;
	}
	public void setMapData(HashMap<String, DataBean> mapData) {
		this.mapData = mapData;
	}
	public ArrayList<String> getLetters() {
		return letters;
	}
	public void setLetters(ArrayList<String> letters) {
		this.letters = letters;
	}
	@Override
	public String toString() {
		final int maxLen = 20;
		return "ConfigBean [nameFile="
				+ nameFile
				+ ", id="
				+ id
				+ ", mapData="
				+ (mapData != null ? toString(mapData.entrySet(), maxLen)
						: null) + "]";
	}
	private String toString(Collection<?> collection, int maxLen) {
		StringBuilder builder = new StringBuilder();
		builder.append("[");
		int i = 0;
		for (Iterator<?> iterator = collection.iterator(); iterator.hasNext()
				&& i < maxLen; i++) {
			if (i > 0)
				builder.append(", ");
			builder.append(iterator.next());
		}
		builder.append("]");
		return builder.toString();
	}
	
	public String toCSV(){
		String csvSepartor=",";
		String fieldSeparator= "\"";
		String content = "";
		DataBean[] dataArray = new DataBean[getMapData().keySet().size()];
		int cont=0;
		for (String key : getMapData().keySet()) {
			DataBean data = getMapData().get(key);
			dataArray[cont++]=data;
		}
		for (DataBean dataBean : dataArray) {
			content=content+fieldSeparator+dataBean.getName()+fieldSeparator+csvSepartor;
		}
		content=content.substring(0, content.length()-1)+System.getProperty("line.separator");
		for (DataBean dataBean : dataArray) {
			content=content+fieldSeparator+dataBean.getNormalizacion()+fieldSeparator+csvSepartor;
		}
		content=content.substring(0, content.length()-1)+System.getProperty("line.separator");
		for (DataBean dataBean : dataArray) {
			content=content+fieldSeparator+dataBean.getDimensionMesureEntry()+fieldSeparator+csvSepartor;
		}
		content=content.substring(0, content.length()-1)+System.getProperty("line.separator");
		for (DataBean dataBean : dataArray) {
			content=content+fieldSeparator+dataBean.getType()+fieldSeparator+csvSepartor;
		}
		content=content.substring(0, content.length()-1)+System.getProperty("line.separator");
		for (DataBean dataBean : dataArray) {
			DataBean dataBeanAux = ConfigAdmin.skosExtrated.get(dataBean.getName());
			if(dataBeanAux!=null && dataBeanAux.getMapSkos()!=null && dataBeanAux.getMapSkos().size()>0){
				String nameFile = dataBeanAux.generateSkosMapping();
				content=content+fieldSeparator+nameFile+fieldSeparator+csvSepartor;
				
			}else{
				content=content+fieldSeparator+fieldSeparator+csvSepartor;
			}
		}
		content=content.substring(0, content.length()-1)+System.getProperty("line.separator");
		
		String nameFile=ConfigAdmin.configDirectoryString+File.separator+getNameFile();
		File file = new File(nameFile);
		try {
			Utils.stringToFile(content, file);
		} catch (Exception e) {
			log.error("Error to generate config file "+getNameFile(), e);
		}
		if(Constants.publicDrive){
			GoogleDriveAPI api = new GoogleDriveAPI();
			api.init();
			api.createSpreadsheetFromFile(Constants.idParentFolder,
					Constants.emailUserFile, "csv", getNameFile().substring(0, getNameFile().length()-4), file,
			"text/csv");
		}
		return content;
	}
	
	public String toXLSX(){
		return "";
	}
}
