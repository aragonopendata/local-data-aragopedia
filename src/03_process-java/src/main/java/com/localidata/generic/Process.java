package com.localidata.generic;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

import com.localidata.extract.GenerateCSV;
import com.localidata.process.GenerateRDF;
import com.localidata.process.TransformToRDF;
import com.localidata.transform.GenerateConfig;

/**
 * 
 * @author Localidata
 *
 */
public class Process {

	public static void main(String[] args) {
		
		Logger log = Logger.getLogger(Process.class);
		PropertyConfigurator.configure("log4j.properties");
		
		if (args[0].equals("config")) {
			GenerateConfig.main(args);
		} else if (args[0].equals("data")) {
			GenerateRDF.main(args);
		} else if (args[0].equals("csv") || args[0].equals("generateHash")) {
			GenerateCSV.main(args);			
		} else if (args[0].equals("createIssue")) {	
			GithubApi.main(args);	
			log.info("Prueba creando una issue en GitHub superada");
		} else if (args[0].equals("generateHash")) {
			GenerateCSV app = new GenerateCSV(args[1], args[2]);
			app.generateHashCodeFromBI();
		} else if (args[0].equals("commonData")) {
			GenerateRDF rdf = new GenerateRDF(args[2], args[4], args[3], args[1], args[5]);
			rdf.readConfig(null);
			rdf.delete();
			rdf.generateCommonData();
			rdf.writeSkosTTL();
			rdf.zipFiles();
		} else if (args[0].equals("configTest")) {
			Prop.loadConf();
			log.info("Init configTest");
			
			GenerateConfig config = new GenerateConfig(args[2], "", args[3], args[1]);
			config.updateConfig(new ArrayList(), new ArrayList(), new HashMap());
			
			log.info("End configTest");
			
		} else if (args[0].equals("update")) {
			PropertyConfigurator.configure("log4j.properties");
			log.info("Start process");
			Prop.loadConf();
			GenerateCSV csv = new GenerateCSV(args[1], args[2]);
			csv.extractFiles();

			if (csv.getChanges().size() > 0 || csv.getNews().size() > 0) {
				log.info("Cantidad de cubos nuevos: " + csv.getNews().size());
				log.info("El procese de actualización ha detectado "+csv.getChanges().size()+" cambios y "+csv.getNews().size()+" nuevos");
				
				GenerateRDF rdf = new GenerateRDF(args[2], args[4], args[3], args[1], args[5]);
				rdf.readConfig(csv.getIdDescription());
				
				GenerateConfig config = new GenerateConfig(args[2], "", args[3], args[1]);
				config.updateConfig(csv.getChanges(), csv.getNews(), rdf.getMapconfig());

				if (csv.getNews().size() == 0 && config.getFilesNotRDF().size() == csv.getChanges().size()) {
					log.info("Todos los cambios requieren cambio de configuración y no se generan nuevos ttl");
					System.exit(0);
				}

				rdf.setFilesNotRDF(config.getFilesNotRDF());
				rdf.delete();
				List<String> result = rdf.writeInformationTTL();
				rdf.writeSkosTTL();
				rdf.zipFiles();
				csv.generateHashCode(result,csv.getNews());

			} else {
				log.info("No hay cambios");
			}
			log.info("end update");
		}
	}

}
