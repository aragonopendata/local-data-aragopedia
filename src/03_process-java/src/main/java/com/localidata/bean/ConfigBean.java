package com.localidata.bean;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;

import org.apache.log4j.Logger;

import com.localidata.generic.Prop;
import com.localidata.transform.GenerateConfig;
import com.localidata.util.Utils;

/**
 * 
 * @author Localidata
 *
 */
public class ConfigBean {

	private final static Logger log = Logger.getLogger(ConfigBean.class);
	private String nameFile;
	private String id;
	private ArrayList<String> letters = new ArrayList<String>();
	private HashMap<String, DataBean> mapData = new HashMap<String, DataBean>();
	private ArrayList<DataBean> listDataConstant = new ArrayList<DataBean>();
	private boolean updated;

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

	public ArrayList<DataBean> getListDataConstant() {
		return listDataConstant;
	}

	public void setListDataConstant(ArrayList<DataBean> listDataConstant) {
		this.listDataConstant = listDataConstant;
	}

	public ArrayList<String> getLetters() {
		return letters;
	}

	public void setLetters(ArrayList<String> letters) {
		this.letters = letters;
	}

	public boolean isUpdated() {
		return updated;
	}

	public void setUpdated(boolean updated) {
		this.updated = updated;
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

	public String toCSV() {
		String csvSepartor = ",";
		String fieldSeparator = "\"";
		String content = "";
		DataBean[] dataArray = new DataBean[getMapData().keySet().size()];
		int cont = 0;
		for (String key : getMapData().keySet()) {
			DataBean data = getMapData().get(key);
			dataArray[cont++] = data;
		}
		byte b;
		int i;
		DataBean[] arrayOfDataBean1;
		for (i = (arrayOfDataBean1 = dataArray).length, b = 0; b < i; ) {
			DataBean dataBean = arrayOfDataBean1[b];
			content = String.valueOf(content) + fieldSeparator + dataBean.getName() + fieldSeparator + csvSepartor;
			b++;
		}

		for (DataBean dataBean : this.listDataConstant)
			content = String.valueOf(content) + fieldSeparator + dataBean.getName() + fieldSeparator + csvSepartor;
		
		content = String.valueOf(content.substring(0, content.length() - 1)) + System.getProperty("line.separator");
		
		for (i = (arrayOfDataBean1 = dataArray).length, b = 0; b < i; ) {
			DataBean dataBean = arrayOfDataBean1[b];
			content = String.valueOf(content) + fieldSeparator + dataBean.getNameNormalized() + fieldSeparator + csvSepartor;
			b++;
		}

		for (DataBean dataBean : this.listDataConstant)
			content = String.valueOf(content) + fieldSeparator + dataBean.getNameNormalized() + fieldSeparator + csvSepartor; 
		
		content = String.valueOf(content.substring(0, content.length() - 1)) + System.getProperty("line.separator");

		for (i = (arrayOfDataBean1 = dataArray).length, b = 0; b < i; ) {
			DataBean dataBean = arrayOfDataBean1[b];
			content = String.valueOf(content) + fieldSeparator + dataBean.getNormalizacion() + fieldSeparator + csvSepartor;
			b++;
		}
		
		for (DataBean dataBean : this.listDataConstant)
			content = String.valueOf(content) + fieldSeparator + dataBean.getNormalizacion() + fieldSeparator + csvSepartor; 
		
		content = String.valueOf(content.substring(0, content.length() - 1)) + System.getProperty("line.separator");
		
		for (i = (arrayOfDataBean1 = dataArray).length, b = 0; b < i; ) {
			DataBean dataBean = arrayOfDataBean1[b];
			content = String.valueOf(content) + fieldSeparator + dataBean.getDimensionMesureEntry() + fieldSeparator + csvSepartor;
			b++;
		}

		for (DataBean dataBean : this.listDataConstant)
			content = String.valueOf(content) + fieldSeparator + dataBean.getDimensionMesureEntry() + fieldSeparator + csvSepartor; 
		
		content = String.valueOf(content.substring(0, content.length() - 1)) + System.getProperty("line.separator");

		for (i = (arrayOfDataBean1 = dataArray).length, b = 0; b < i; ) {
			DataBean dataBean = arrayOfDataBean1[b];
			content = String.valueOf(content) + fieldSeparator + dataBean.getType() + fieldSeparator + csvSepartor;
			b++;
		}
		
		for (DataBean dataBean : this.listDataConstant)
			content = String.valueOf(content) + fieldSeparator + dataBean.getType() + fieldSeparator + csvSepartor; 
		
		content = String.valueOf(content.substring(0, content.length() - 1)) + System.getProperty("line.separator");

		for (i = (arrayOfDataBean1 = dataArray).length, b = 0; b < i; ) {
			DataBean dataBean = arrayOfDataBean1[b];
			DataBean dataBeanAux = (DataBean)GenerateConfig.skosExtrated.get(dataBean.getName());
			if (dataBeanAux != null && dataBeanAux.getMapSkos() != null && dataBeanAux.getMapSkos().size() > 0) {
				String str = dataBeanAux.generateSkosMapping();
				content = String.valueOf(content) + fieldSeparator + str + fieldSeparator + csvSepartor;
			} else {
				content = String.valueOf(content) + fieldSeparator + fieldSeparator + csvSepartor;
			}
			b++;
		}
		
		for (DataBean dataBean : this.listDataConstant)
			content = String.valueOf(content) + fieldSeparator + dataBean.getConstant() + fieldSeparator + csvSepartor; 
		
		content = String.valueOf(content.substring(0, content.length() - 1)) + System.getProperty("line.separator");
		String nameFile = String.valueOf(GenerateConfig.configDirectoryString) + File.separator + getNameFile();
		File file = new File(nameFile);
		
		try {
			Utils.stringToFile(content, file);
		} catch (Exception e) {
			log.error("Error to generate config file " + getNameFile(), e);
		}
		return content;
	}

}
