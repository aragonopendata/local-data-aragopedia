package com.localidata.process;

public class Process {

	public static void main(String[] args) {
		
		if(args[0].equals("config")){
			GenerateConfig.main(args);
		}else if(args[0].equals("data")){
			GenerateData.main(args);
		}

	}

}
