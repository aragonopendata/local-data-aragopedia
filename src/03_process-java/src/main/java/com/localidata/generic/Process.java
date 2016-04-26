package com.localidata.generic;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

import com.localidata.extract.GenerateCSV;
import com.localidata.process.GenerateRDF;
import com.localidata.transform.GenerateConfig;

public class Process {

	public static void main(String[] args) {

		if (args[0].equals("config")) {
			GenerateConfig.main(args);
		} else if (args[0].equals("data")) {
			GenerateRDF.main(args);
		} else if (args[0].equals("csv")) {
			GenerateCSV.main(args);
		} else if (args[0].equals("update")) {
			Logger log = Logger.getLogger(Process.class);
			PropertyConfigurator.configure("log4j.properties");
			Prop.loadConf();
			log.info("Init update");
			GenerateCSV csv = new GenerateCSV(args[1], args[2], args[3], args[6]);
			csv.extractFilesWithChanges();
			
			if(csv.getChanges().size()>0 || csv.getNews().size()>0){
				GenerateConfig config = new GenerateConfig(args[2], args[3],
						args[4]);
				config.updateConfig(csv.getChanges(),csv.getNews());
				
				if(csv.getNews().size()==0 && config.getFilesNotRDF().size()==csv.getChanges().size()){
					log.info("Todos los cambios requieren cambio de configuración y no se generan nuevos ttl");
					System.exit(0);
				}
				
				GenerateRDF rdf = new GenerateRDF(args[2], args[5], args[4]);
				rdf.setGenerateHeaderRDF(csv.getNews().size()>0 || config.getFilesNotRDF().size()==0);
				rdf.setFilesNotRDF(config.getFilesNotRDF());
				rdf.backup();
				rdf.readConfig();
				rdf.writeInformationTTL(csv.getNews().size()==0); 
				if(csv.getNews().size()>0)
					rdf.writeSkosTTL();
				rdf.zipFiles();
				csv.generateHashCode();
			}else{
				log.info("No hay cambios");
			}
			log.info("end update");
		}
	}

}
