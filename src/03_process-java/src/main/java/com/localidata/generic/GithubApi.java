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
		headers.put("Content-Type", "application/json");

		String body = "{" + "\"title\": \"" + titulo + "\"," + "\"body\": \"" + cuerpo + "\"" + "}";
		body = body.replace("\n", "\\n");
		try {
			Utils.processURLPost(Prop.githubURLIssues, "", headers, body);
		} catch (Exception e) {
			log.error("Error creando una incidencia en github",e);
		}
		log.info("end createIssue");
	}

	public static void main(String[] args) {

		if ((log == null) || (log.getLevel() == null))
			PropertyConfigurator.configure("log4j.properties");
		Prop.loadConf();

		 GithubApi.createIssue("Prueba de creacción de issue desde api 23051213","Esto es una prueba de creaccion de una isssue desde la api de github realizada por hlafuente");

	}

}
