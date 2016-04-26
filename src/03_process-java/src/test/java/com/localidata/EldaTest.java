package com.localidata;

import java.io.IOException;
import java.util.ArrayList;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import com.localidata.util.Utils;



public class EldaTest
{
	
	private final static Logger log = Logger.getLogger(EldaTest.class);
	
	String searched ="http://opendata.aragon.es/";
	String replacement="http://alzir.dia.fi.upm.es/";
	
	
    
    public void run(){
    	dsdAll();
    	propertyAll();
    	dimensionAll();
    	medidaAll();
    	cubosAll();
    	dsdId();
    	codelistId();
    }
    
    public void observacionAll(){
    	log.info("Start test observacionAll");
    	boolean result=true;
    	String URI=replacement+"recurso/iaest/observacion.json";
    	result=testJSONURI(URI,"observacionAll");
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
    	String URI=replacement+"recurso/iaest/dsd.json";
    	result=testJSONURI(URI,"dsdAll");
		if(result){
			log.info("Test dsdAll OK");
		}else{
			log.error("Test dsdAll KO");
		}
		log.info("End test dsdAll");
    }
    
    public void dsdId(){
    	log.info("Start test dsdId");
    	String URI=replacement+"recurso/iaest/dsd.json?_view=all";
    	ArrayList<String> listDsd = getDataJSONURI(URI,"_about",null);
    	boolean globalResult=true;
    	for (String uriDsd : listDsd) {
    		boolean result=true;
        	result=testJSONURI(uriDsd,"dsdId");
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
    	String URI=replacement+"recurso/iaest/property.json";
    	result=testJSONURI(URI,"propertyAll");
		if(result){
			log.info("Test propertyAll OK");
		}else{
			log.error("Test propertyAll KO");
		}
		log.info("End test propertyAll");
    }
    
    public void dimensionAll(){
    	log.info("Start test dimensionAll");
    	boolean result=true;
    	String URI=replacement+"recurso/iaest/dimension.json";
    	result=testJSONURI(URI,"dimensionAll");
		if(result){
			log.info("Test dimensionAll OK");
		}else{
			log.error("Test dimensionAll KO");
		}
		log.info("End test dimensionAll");
    }
    
    public void medidaAll(){
    	log.info("Start test medidaAll");
    	boolean result=true;
    	String URI=replacement+"recurso/iaest/medida.json";
    	result=testJSONURI(URI,"medidaAll");
		if(result){
			log.info("Test medidaAll OK");
		}else{
			log.error("Test medidaAll KO");
		}
    	log.info("End test medidaAll");
    }
    
    public void cubosAll(){
    	log.info("Start test cubosAll");
    	boolean result=true;
    	String URI=replacement+"recurso/iaest/dataset.json";
    	result=testJSONURI(URI,"cubosAll");
		if(result){
			log.info("Test cubosAll OK");
		}else{
			log.error("Test cubosAll KO");
		}
		log.info("End test cubosAll");
    }
    
    public void cubosId(){
    	log.info("Start test cubosId");
    	String URI=replacement+"recurso/iaest/dataset.json";
    	ArrayList<String> listCubos = getDataJSONURI(URI,"_about",null);
    	boolean globalResult=true;
    	for (String uriCubo : listCubos) {
    		boolean result=true;
        	result=testJSONURI(uriCubo,"cubosId");
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
    	String URI=replacement+"recurso/iaest/dimension.json";
    	ArrayList<String> listCubos = getDataJSONURI(URI,"qb:codeList",null);
    	boolean globalResult=true;
    	for (String uriCubo : listCubos) {
    		boolean result=true;
        	result=testJSONURI(uriCubo,"codelistId");
    		if(!result){
    			globalResult=false;
    			log.error("Test codelistId "+uriCubo+" KO");
    		}
		}
    	if(globalResult){
			log.info("Test codelistId OK");
		}else{
			log.error("Test codelistId KO");
		}
    	log.info("End test codelistId");
    }
    
    public void codelistIdValue(){
    	log.info("Start test codelistIdValue");
    	log.info("End test codelistIdValue");
    }
    
    
    private boolean testJSONURI(String URI, String test) {
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
            if(!validateJSON(json,test)){
            	log.error("Error in URI "+URI);
                return false;
            }
        	JSONObject result = (JSONObject)json.get("result");
        	next = (String) result.get("next");
        	if(next!=null){
        		testJSONURI(replaceURI(next),test);
        	}
        } catch (ParseException e) {    
            log.error("Error in URI "+next,e);
            return false;
        }
        
        return true;
    }
    

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
				if(Utils.v(about)){
					listResult.add(replaceURI(about)+".json");
				}
			}
        	next = (String) result.get("next");
        	if(next!=null){
        		getDataJSONURI(replaceURI(next),search,listResult);
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
    
    private boolean validateJSON(JSONObject json, String test) {
		
		return true;
		
	}
    
	public static void main(String[] args) {
		if ((log == null) || (log.getLevel() == null))
			PropertyConfigurator.configure("log4j.properties");
		EldaTest test = new EldaTest();
		test.run();
	}
}
