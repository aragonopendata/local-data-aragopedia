package com.localidata;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;

import org.apache.log4j.Logger;

public class DataBean {
	
	private final static Logger log = Logger.getLogger(DataBean.class);

	private String name;

	private String nameNormalized;

	private String normalizacion;
	private String dimensionMesureEntry;
	private String dimensionMesure;
	private String dimensionMesureProperty;
	private String dimensionMesureSDMX;

	private String type;

	private HashMap<String, SkosBean> mapSkos = new HashMap<String, SkosBean>();
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		if(name.equals("null")){
			this.name=null;
			this.nameNormalized = null;
		}else{
			this.name = name;
			this.nameNormalized = Utils.urlify(name);
		}
	}
	public String getNormalizacion() {
		return normalizacion;
	}
	public void setNormalizacion(String normalizacion) {
		if(normalizacion.equals("null"))
			this.normalizacion=null;
		else
			this.normalizacion = normalizacion;
	}
	public String getDimensionMesure() {
		return dimensionMesure;
	}
	public void setDimensionMesure(String dimensionMesure) {
		this.dimensionMesureEntry=dimensionMesure;
		if(dimensionMesure.equals("dim")){
			this.dimensionMesure = Constants.dimension;
			this.dimensionMesureProperty = Constants.dimensionProperty;
			this.dimensionMesureSDMX = Constants.dimensionSDMX;
		}else if(dimensionMesure.equals("null")) {
			this.dimensionMesure = null;
			this.dimensionMesureProperty = null;
			this.dimensionMesureSDMX = null;
		}else {
			this.dimensionMesure = Constants.mesure;
			this.dimensionMesureProperty = Constants.mesureProperty;
			this.dimensionMesureSDMX = Constants.mesureSDMX;
		}
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		if(type.equals("null"))
			this.type=null;
		else
			this.type = type;
	}
	public HashMap<String, SkosBean> getMapSkos() {
		return mapSkos;
	}
	public void setMapSkos(HashMap<String, SkosBean> mapSkos) {
		this.mapSkos = mapSkos;
	}
	public String getDimensionMesureProperty() {
		return dimensionMesureProperty;
	}
	public void setDimensionMesureProperty(String dimensionMesureProperty) {
		this.dimensionMesureProperty = dimensionMesureProperty;
	}	
	public String getDimensionMesureSDMX() {
		return dimensionMesureSDMX;
	}
	public void setDimensionMesureSDMX(String dimensionMesureSDMX) {
		this.dimensionMesureSDMX = dimensionMesureSDMX;
	}
	public String getDimensionMesureEntry() {
		return dimensionMesureEntry;
	}
	@Override
	public String toString() {
		final int maxLen = 20;
		return "DataBean [dimension="
				+ Constants.dimension
				+ ", mesure="
				+ Constants.mesure
				+ ", dimensionProperty="
				+ Constants.dimensionProperty
				+ ", mesureProperty="
				+ Constants.mesureProperty
				+ ", name="
				+ name
				+ ", normalizacion="
				+ normalizacion
				+ ", DimensionMesure="
				+ dimensionMesure
				+ ", DimensionMesureProperty="
				+ dimensionMesureProperty
				+ ", type="
				+ type
				+ ", mapSkos="
				+ (mapSkos != null ? toString(mapSkos.entrySet(), maxLen)
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
	
	public String generateSkosMapping(){
		String nameFile ="mapping-"+Utils.urlify(getName());
		return nameFile+".xlsx";
	}
	
}
