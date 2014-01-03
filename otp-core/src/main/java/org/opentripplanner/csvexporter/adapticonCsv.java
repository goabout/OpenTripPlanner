package org.opentripplanner.csvexporter;

import java.io.File;
import java.util.ArrayList;

import com.google.common.io.Files;

public class adapticonCsv implements csvExporterInterface{
	
	
	
	
	/**
	 * adapticonSegementFile returns the adapticon segment file existing in
	 * exporter folder
	 * 
	 * @return File adapticon segment file
	 */
	public File adapticonSegementFile(){		
		
		// check if Adapticon file is there
		File adapticon =new File(DEFAULT_EXPORTER_DIRECTORY);	
		String[] subFolders = adapticon.list();
		ArrayList<String> adapticonfiles = new ArrayList<String>();	
		
		for(String s:subFolders){
			if(s.contains("adapticon")){				
				adapticonfiles.add(s);}}	
		
		// check if the files are CSV files if not remove the files from the
		// list		
		for(String s:adapticonfiles){
			File f = new File(s);
			if(!(Files.getFileExtension(f.getAbsolutePath()).equals("csv"))){
				adapticonfiles.remove(s);}}
		
		//return the latest adapticon file
		File latestAdapticon=new File(adapticonfiles.get(0));
		long time1 = latestAdapticon.lastModified();
		for(String s:adapticonfiles){
			File f = new File(s);
			long time2= f.lastModified();
			if(time2 > time1)
				latestAdapticon = new File(s);			
		}
		
		return latestAdapticon;
	}
	
	//read adapticon and latest uploaded 
	
	//creat csv
	
	//save it to new csv
	

}
