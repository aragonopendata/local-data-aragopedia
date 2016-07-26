/**
 * 
 * @author Miquel Quetglas
 * @author AMS
 *
 */
package com.localidata.util;

public class Valor {
	int id;
	

	String linea_clave;
    String padre;
    String descripcion;
    String dirweb;
    String operacion;
	
    public String getLinea_clave() {
		return linea_clave;
	}
	
    public void setLinea_clave(String linea_clave) {
		this.linea_clave = linea_clave;
	}
	
    public String getPadre() {
		return padre;
	}
	
    public void setPadre(String padre) {
		this.padre = padre;
	}
	
    public String getDescripcion() {
		return descripcion;
	}
	
    public void setDescripcion(String descripcion) {
		this.descripcion = descripcion;
	}
	
    public String getDirweb() {
		return dirweb;
	}
	
    public void setDirweb(String dirweb) {
		this.dirweb = dirweb;
	}
    
    public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}
	
	public String getOperacion() {
		return operacion;
	}
	
    public void setOperacion(String operacion) {
		this.operacion = operacion;
	}
	@Override
	public String toString() {
		return "Valor [linea_clave=" + linea_clave + ", padre=" + padre + ", descripcion=" + descripcion + ", dirweb="
				+ dirweb + "]";
	}
    
    
    
}
