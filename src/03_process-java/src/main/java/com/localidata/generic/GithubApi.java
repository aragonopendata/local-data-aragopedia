package com.localidata.generic;

import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

import com.localidata.util.Utils;

/**
 * 
 * @author Localidata
 */
public class GithubApi {

	private final static Logger log = Logger.getLogger(GithubApi.class);

	public static void createIssue(String titulo, String cuerpo) {
		
		log.info("init createIssue");
		Map<String, String> headers = new HashMap<>();
		headers.put("Authorization", "token " + Prop.githubToken);
		headers.put("Content-Type", "application/json; charset=utf-8");
		
		titulo = titulo.replace("\"", "");
		cuerpo = Utils.weakClean(cuerpo);
//		cuerpo = Utils.nameDataBeanClean(cuerpo);
		
		String body = "{" + "\"title\": \"" + titulo + "\"," + "\"body\": \"" + cuerpo + "\"" + "}";
		body = body.replace("\n", "\\n");
		log.info("Body create issue "+body);
		
		try {
			Utils.processURLPost(Prop.githubURLIssues, "", headers, body, "ISO-8859-1");
		} catch (Exception e) {
			log.error("Error creando una incidencia en github",e);
		}
		log.info("end createIssue");
	}

	public static void main(String[] args) {

		if ((log == null) || (log.getLevel() == null))
			PropertyConfigurator.configure("log4j.properties");
		Prop.loadConf();

		 GithubApi.createIssue("Prueba de creación de issue desde API","Esto es una prueba de creación de una isssue desde la API de GitHub realizada por @aragopedia");

	}

}
