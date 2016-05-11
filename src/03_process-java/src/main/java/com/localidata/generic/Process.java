package com.localidata.generic;

import java.util.ArrayList;
import java.util.List;

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
			GenerateCSV csv = new GenerateCSV(args[1], args[2]);
			csv.extractFilesWithChanges();
			
			if(csv.getChanges().size()>0 || csv.getNews().size()>0){
				
				GenerateRDF rdf = new GenerateRDF(args[2], args[4], args[3], args[1], args[5]);
				rdf.readConfig(csv.getIdDescription());
				
				GenerateConfig config = new GenerateConfig(args[2], "",
						args[3]);
				config.updateConfig(csv.getChanges(),csv.getNews(),rdf.getMapconfig());
				
				if(csv.getNews().size()==0 && config.getFilesNotRDF().size()==csv.getChanges().size()){
					log.info("Todos los cambios requieren cambio de configuración y no se generan nuevos ttl");
					System.exit(0);
				}
				
				rdf.setFilesNotRDF(config.getFilesNotRDF());
				rdf.delete();
				List result =rdf.writeInformationTTL(); 
				rdf.writeSkosTTL();
				rdf.zipFiles();
				csv.generateHashCode(result);

			}else{
				log.info("No hay cambios");
			}
			log.info("end update");
		}
	}

}
