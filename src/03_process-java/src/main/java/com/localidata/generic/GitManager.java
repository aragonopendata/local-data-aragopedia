package com.localidata.generic;

import java.io.InputStreamReader;

import org.apache.log4j.Logger;
/**
 * @author Localidata Teams
 *
 */
public class GitManager {
	
	private final static Logger log = Logger.getLogger(GitManager.class);
	
	public GitManager(){
		
	}
	
	public void init(){
		
	}
	
	public void config(){
		executeCommand("git config --global user.name \"hlafuente\"");
		executeCommand("git config --global user.email \"hlafuente@localidata.com\"");
	}
	
	public void add(){
		executeCommand("git add .");
	}
	
	public void commit(String descriptionCommit){
		executeCommand("git commit -m "+descriptionCommit);
	}
	
	public void push(String branch){
		executeCommand("git push "+branch);
	}
	
	private void executeCommand(String command){
		java.lang.Runtime rt = java.lang.Runtime.getRuntime();
		try {
			java.lang.Process p=rt.exec(command);
			log.info("executeCommand "+command);
			java.io.InputStream is = p.getInputStream();
			java.io.BufferedReader reader = new java.io.BufferedReader(new InputStreamReader(is));

			String s = null;
			while ((s = reader.readLine()) != null) {
				log.info(s);
			}
			is.close();
			p.waitFor();      
			log.info("End of executeCommand "+command);
		} catch (Exception e) {
			log.error("Error executing a command "+command,e);				
		}
	}
	
	public static void main(String[] args) {
		

	}

}
