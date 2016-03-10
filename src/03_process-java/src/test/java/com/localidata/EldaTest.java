package com.localidata;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;



public class EldaTest
{
	
	/**
	 * Log class
	 */
	private final static Logger log = Logger.getLogger(EldaTest.class);
	
	String searched ="http://opendata.aragon.es/";
	String replacement="http://alzir.dia.fi.upm.es/";
	
    public EldaTest(  )
    {
    	dsdAll();
    	propertyAll();
    	cubosAll();
    	dsdId();
    	cubosId();
    	observacionAll();
    }
    
    public void observacionAll(){
    	log.info("Start test observacionAll");
    	boolean result=true;
    	String URI="http://alzir.dia.fi.upm.es/recurso/iaest/observacion.json";
    	result=testJSONURI(URI);
		if(result){
			log.info("Test observacionAll OK");
		}else{
			log.error("Test observacionAll KO");
		}
		log.info("End test observacionAll");
    }
    
    public void ObservacionId(){
    	log.info("Start test ObservacionId");
    	log.info("End test ObservacionId");
    }
    
    public void dsdAll(){
    	log.info("Start test dsdAll");
    	boolean result=true;
    	String URI="http://alzir.dia.fi.upm.es/recurso/iaest/dsd.json";
    	result=testJSONURI(URI);
		if(result){
			log.info("Test dsdAll OK");
		}else{
			log.error("Test dsdAll KO");
		}
		log.info("End test dsdAll");
    }
    
    public void dsdId(){
    	log.info("Start test dsdId");
    	String URI="http://alzir.dia.fi.upm.es/recurso/iaest/dsd.json";
    	ArrayList<String> listDsd = getDataJSONURI(URI,"_about",null);
    	boolean globalResult=true;
    	for (String uriDsd : listDsd) {
    		boolean result=true;
        	result=testJSONURI(uriDsd);
    		if(!result){
    			globalResult=false;
    			log.error("Test dsdId "+uriDsd+" KO");
    		}
		}
    	if(globalResult){
			log.info("Test dsdId OK");
		}else{
			log.error("Test dsdId KO");
		}
    	log.info("End test dsdId");
    }
    
    public void propertyAll(){
    	log.info("Start test propertyAll");
    	boolean result=true;
    	String URI="http://alzir.dia.fi.upm.es/recurso/iaest/property.json";
    	result=testJSONURI(URI);
		if(result){
			log.info("Test propertyAll OK");
		}else{
			log.error("Test propertyAll KO");
		}
		log.info("End test propertyAll");
    }
    
    public void dimensionId(){
    	log.info("Start test dimensionId");
    	log.info("End test dimensionId");
    }
    
    public void medidaId(){
    	log.info("Start test medidaId");
    	log.info("End test medidaId");
    }
    
    public void cubosAll(){
    	log.info("Start test cubosAll");
    	boolean result=true;
    	String URI="http://alzir.dia.fi.upm.es/recurso/iaest/dataset.json";
    	result=testJSONURI(URI);
		if(result){
			log.info("Test cubosAll OK");
		}else{
			log.error("Test cubosAll KO");
		}
		log.info("End test cubosAll");
    }
    
    public void cubosId(){
    	log.info("Start test cubosId");
    	String URI="http://alzir.dia.fi.upm.es/recurso/iaest/dataset.json";
    	ArrayList<String> listCubos = getDataJSONURI(URI,"_about",null);
    	boolean globalResult=true;
    	for (String uriCubo : listCubos) {
    		boolean result=true;
        	result=testJSONURI(uriCubo);
    		if(!result){
    			globalResult=false;
    			log.error("Test dsdId "+uriCubo+" KO");
    		}
		}
    	if(globalResult){
			log.info("Test cubosId OK");
		}else{
			log.error("Test cubosId KO");
		}
    	log.info("End test cubosId");
    }
    
    public void cubosDimension(){
    	log.info("Start test cubosDimension");
    	log.info("End test cubosDimension");
    }
    
    public void cubosMedida(){
    	log.info("Start test cubosMedida");
    	log.info("End test cubosMedida");
    }
    
    public void cubosDimensionValor(){
    	log.info("Start test cubosDimensionValor");
    	log.info("End test cubosDimensionValor");
    }
    
    public void cubosMedidaValor(){
    	log.info("Start test cubosMedidaValor");
    	log.info("End test cubosMedidaValor");
    }
    
    public void cubosDimensionPropiedad(){
    	log.info("Start test cubosDimensionPropiedad");
    	log.info("End test cubosDimensionPropiedad");
    }
    
    public void cubosMedidaPropiedad(){
    	log.info("Start test cubosMedidaPropiedad");
    	log.info("End test cubosMedidaPropiedad");
    }
    
    public void codelistId(){
    	log.info("Start test codelistId");
    	log.info("End test codelistId");
    }
    
    public void codelistIdValue(){
    	log.info("Start test codelistIdValue");
    	log.info("End test codelistIdValue");
    }
    
    
    private boolean testJSONURI(String URI) {
        String content=null;
        String next=null;
        JSONParser parser=new JSONParser();
        try {
            content = Utils.processURLGet(URI);
        } catch (IOException e) {
            log.error("Error in URL "+URI,e);
            return false;
        }        
        
        try {
            parser.parse(content);
            
            JSONObject json=(JSONObject) parser.parse(content);
        	JSONObject result = (JSONObject)json.get("result");
//        	JSONArray items = (JSONArray) result.get("items");
//        	for(int h=0;h<items.size();h++){
//        		JSONObject item=(JSONObject) items.get(h);
//				String about=(String)item.get(search);
//				if(Utils.validValue(about)){
//					listResult.add(replaceURI(about)+".json");
//				}
//			}
        	next = (String) result.get("next");
        	if(next!=null){
        		testJSONURI(next);
        	}
        } catch (ParseException e) {    
            log.error("Error in URI "+next,e);
            return false;
        }
        
        return true;
    }
    //	http://alzir.dia.fi.upm.es/recurso/iaest/dsd.json
    private ArrayList<String> getDataJSONURI(String URI, String search, ArrayList<String> listResult) {
    	if(listResult==null){
    		listResult = new ArrayList<String>();
    	}
    	String content=null;
    	String next=null;
        JSONParser parser=new JSONParser();
        try {
            content = Utils.processURLGet(URI);
        } catch (IOException e) {
            log.error("Error process URL "+URI,e);
            return null;
        }        
        
        try {
        	JSONObject json=(JSONObject) parser.parse(content);
        	JSONObject result = (JSONObject)json.get("result");
        	JSONArray items = (JSONArray) result.get("items");
        	for(int h=0;h<items.size();h++){
        		JSONObject item=(JSONObject) items.get(h);
				String about=(String)item.get(search);
				if(Utils.validValue(about)){
					listResult.add(replaceURI(about)+".json");
				}
			}
        	next = (String) result.get("next");
        	if(next!=null){
        		getDataJSONURI(next,search,listResult);
        	}
        	
        	
        } catch (ParseException e) {    
            log.error("Error in data "+next,e);
            return null;
        }
    	return listResult;
    }
    
    private String replaceURI(String source){
    	if(source.contains(searched)){
    		return source.replace(searched, replacement);
    	}
    	return source;
    }
    
    /**
	 * Main's class
	 */
	public static void main(String[] args) {
		if ((log == null) || (log.getLevel() == null))
			PropertyConfigurator.configure("log4j.properties");
		EldaTest test = new EldaTest();
	}
}
