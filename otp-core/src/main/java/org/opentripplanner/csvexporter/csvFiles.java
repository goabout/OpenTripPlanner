package org.opentripplanner.csvexporter;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;

public class csvFiles implements csvExporterInterface{
		
	public File getLatestcsv(){
		File latestFile = null;
		File folder = new File(DEFAULT_EXPORTER_DIRECTORY);
		String[] subFolders = folder.list();
		ArrayList<String> notUploadedcsvfiles = new ArrayList<String>();
				
		for(String s:subFolders){
			if(!s.contains("uploaded")){				
				notUploadedcsvfiles.add(s);					
			}
		}
		if (notUploadedcsvfiles.size() > 0){
			Collections.sort(notUploadedcsvfiles);			
			String name =notUploadedcsvfiles.get(notUploadedcsvfiles.size()-1); 
			latestFile = new File(DEFAULT_EXPORTER_DIRECTORY+"/"+name+"/");			
		}
		
		return latestFile;
	}
	
	public File getLastUplodedcsv(){
		File lastUploadedfile = null;
		File folder = new File(DEFAULT_EXPORTER_DIRECTORY);
		String[] subFolders = folder.list();
		ArrayList<String> uploadedCsvfiles = new ArrayList<String>();		
		
		for(String s:subFolders){
			if(s.contains("uploaded")){
				uploadedCsvfiles.add(s);
			}
		}
		if (uploadedCsvfiles.size() > 0){
			Collections.sort(uploadedCsvfiles);			
			lastUploadedfile = new File(DEFAULT_EXPORTER_DIRECTORY+"/"+uploadedCsvfiles.get(uploadedCsvfiles.size()-1));			
		}						
		return lastUploadedfile;		
	}
}
