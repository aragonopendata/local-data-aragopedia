package com.localidata.bean;

import java.util.HashSet;

/**
 * 
 * @author Localidata
 *
 */
public class SkosBean {

	private String id;
	private SkosBean parent;
	private HashSet<SkosBean> sons;
	private String URI;
	private String label;

	public SkosBean() {
		sons = new HashSet<SkosBean>();
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getURI() {
		return URI;
	}

	public void setURI(String uRI) {
		URI = uRI;
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public SkosBean getParent() {
		return parent;
	}

	public void setParent(SkosBean parent) {
		this.parent = parent;
	}

	public HashSet<SkosBean> getSons() {
		return sons;
	}

	public void setSons(HashSet<SkosBean> sons) {
		this.sons = sons;
	}

	@Override
	public String toString() {
		return "SkosBean [id=" + id + ", URI=" + URI + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((URI == null) ? 0 : URI.hashCode());
		result = prime * result + ((id == null) ? 0 : id.hashCode());
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
		SkosBean other = (SkosBean) obj;
		if (URI == null) {
			if (other.URI != null)
				return false;
		} else if (!URI.equals(other.URI))
			return false;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}

}
