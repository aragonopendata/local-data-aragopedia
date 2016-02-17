package com.localidata;


public class SkosBean {
	

	private String id;

	private String URI;
	
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
	
	@Override
	public String toString() {
		return "SkosBean [id=" + id + ", URI=" + URI + "]";
	}
		
}
