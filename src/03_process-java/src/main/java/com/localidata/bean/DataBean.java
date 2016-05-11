package com.localidata.bean;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;

import org.apache.log4j.Logger;

import com.localidata.generic.Constants;
import com.localidata.util.Utils;

/**
 * Bean with information's column csv
 * 
 * @author Localidata
 *
 */
public class DataBean {

	/**
	 * Log class
	 */
	private final static Logger log = Logger.getLogger(DataBean.class);
	/**
	 * name column csv
	 */
	private String name;
	/**
	 * normalized name column csv
	 */
	private String nameNormalized;
	/**
	 * URL by normalize column csv
	 */
	private String normalizacion;
	/**
	 * Third line config. ['dim' | 'medida' | null ]
	 */
	private String dimensionMesureEntry;
	/**
	 * Dsd normalization ['qb:dimension' | 'qb:measure' | null]
	 */
	private String dimensionMesure;
	/**
	 * Properties normalization ['qb:DimensionProperty' | 'qb:MeasureProperty' |
	 * null]
	 */
	private String dimensionMesureProperty;
	/**
	 * Prefix normalization refArea or RefPeriod ['sdmx-dimension' |
	 * 'sdmx-measure' | null]
	 */
	private String dimensionMesureSDMX;
	/**
	 * Type data column
	 */
	private String type;
	/**
	 * Value constant of databean. If databean isn't constant will be null
	 */
	private String constant;
	/**
	 * Id config of databean
	 */
	private String idConfig;
	/**
	 * boolean by know if change skos name
	 */
	private boolean writeSkos;
	/**
	 * String normalization skos
	 */
	private String kosName;
	/**
	 * Hashmap (id skos -> skosbean) with skos related
	 */
	private HashMap<String, SkosBean> mapSkos;

	private String relationKos;

	public DataBean() {
		writeSkos = true;
		mapSkos = new HashMap<String, SkosBean>();
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		if (name.equals("null")) {
			this.name = null;
			this.nameNormalized = null;
		} else {
			this.name = name;
			this.nameNormalized = Utils.urlify(name);
		}
	}

	public String getNormalizacion() {
		return normalizacion;
	}

	public void setNormalizacion(String normalizacion) {
		if (normalizacion.equals("null"))
			this.normalizacion = null;
		else
			this.normalizacion = normalizacion;
	}

	public String getDimensionMesure() {
		return dimensionMesure;
	}

	public void setDimensionMesure(String dimensionMesure) {
		this.dimensionMesureEntry = dimensionMesure;
		if (dimensionMesure.equals("dim")) {
			this.dimensionMesure = Constants.dimension;
			this.dimensionMesureProperty = Constants.dimensionProperty;
			this.dimensionMesureSDMX = Constants.dimensionSDMX;
		} else if (dimensionMesure.equals("null")) {
			this.dimensionMesure = null;
			this.dimensionMesureProperty = null;
			this.dimensionMesureSDMX = null;
		} else {
			this.dimensionMesure = Constants.mesure;
			this.dimensionMesureProperty = Constants.mesureProperty;
			this.dimensionMesureSDMX = Constants.mesureSDMX;
		}
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		if (type.equals("null"))
			this.type = null;
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

	public String getNameNormalized() {
		return nameNormalized;
	}

	public void setNameNormalized(String nameNormalized) {
		this.nameNormalized = nameNormalized;
	}

	public String getConstant() {
		return constant;
	}

	public void setConstant(String constant) {
		this.constant = constant;
	}

	public String getIdConfig() {
		return idConfig;
	}

	public void setIdConfig(String idConfig) {
		this.idConfig = idConfig;
	}

	public boolean isWriteSkos() {
		return writeSkos;
	}

	public void setWriteSkos(boolean writeSkos) {
		this.writeSkos = writeSkos;
	}

	public String getKosName() {
		return kosName;
	}

	public void setKosName(String kosName) {
		this.kosName = kosName;
	}	

	public String getRelationKos() {
		return relationKos;
	}

	public void setRelationKos(String relationKos) {
		this.relationKos = relationKos;
	}

	@Override
	public String toString() {
		final int maxLen = 20;
		return "DataBean [name="
				+ name
				+ ", normalizacion="
				+ normalizacion
				+ ", dimensionMesureEntry="
				+ dimensionMesureEntry
				+ ", type="
				+ type
				+ ", constant="
				+ constant
				+ ", idConfig="
				+ idConfig
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

	public String generateSkosMapping() {
		String nameFile = "mapping-" + Utils.urlify(getName());
		return nameFile + ".xlsx";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((idConfig == null) ? 0 : idConfig.hashCode());
		result = prime * result
				+ ((nameNormalized == null) ? 0 : nameNormalized.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		DataBean other = (DataBean) obj;
		if (idConfig == null) {
			if (other.idConfig != null)
				return false;
		} else if (!idConfig.equals(other.idConfig))
			return false;
		if (nameNormalized == null) {
			if (other.nameNormalized != null)
				return false;
		} else if (!nameNormalized.equals(other.nameNormalized))
			return false;
		return true;
	}

	@SuppressWarnings("unchecked")
	public HashMap<String, SkosBean> mergeSkos(DataBean data) {
		log.debug("init mergeSkos " + data);
		HashMap<String, SkosBean> mapSkos = null;
		mapSkos = (HashMap<String, SkosBean>) this.mapSkos.clone();
		if (this.equals(data)) {
			return null;
		}
		for (Iterator<SkosBean> itMapSkosSource = this.mapSkos.values()
				.iterator(); itMapSkosSource.hasNext();) {
			SkosBean skosSource = itMapSkosSource.next();
			boolean continua = false;

			for (Iterator<SkosBean> itMapSkosTarget = data.getMapSkos()
					.values().iterator(); itMapSkosTarget.hasNext();) {
				SkosBean skosTarget = itMapSkosTarget.next();
				if (!this.mapSkos.values().contains(skosTarget)) {
					if (skosTarget.getId().equals(skosSource.getId())) {
						continua = true;
						continue;
					} else if (skosTarget.getId()
							.startsWith(skosSource.getId())) {
						skosTarget.setParent(skosSource);
						skosSource.getSons().add(skosTarget);
						mapSkos.put(skosTarget.getId(), skosTarget);
						continua = true;
						continue;
					}
				}
			}
			if (!continua)
				return null;
		}
		log.debug("end mergeSkos " + data);
		return mapSkos;
	}
}
