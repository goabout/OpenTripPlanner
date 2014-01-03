package org.opentripplanner.csvexporter;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;

public class csvFiles implements csvExporterInterface{
		
	public File getLatestcsv(String dir){
		
		// dir is the path to the directory from DEFAULT_EXPORTER_DIRECTORY
		// dir = null returns the latest csv from the default folder
		// DEFAULT_EXPORTER_DIRECTORY

		File latestFile = null;
		File folder = new File(DEFAULT_EXPORTER_DIRECTORY);
		
		if(dir!= null)
				folder = new File(DEFAULT_EXPORTER_DIRECTORY+"/"+dir);
			
		String[] subFolders = folder.list();
		ArrayList<String> notUploadedcsvfiles = new ArrayList<String>();
				
		for(String s:subFolders){
			if(!s.contains("uploaded") && !s.contains("adapticon")){				
				notUploadedcsvfiles.add(s);					
			}
		}
		if (notUploadedcsvfiles.size() > 0){
			Collections.sort(notUploadedcsvfiles);			
			String name =notUploadedcsvfiles.get(notUploadedcsvfiles.size()-1); 
			try{
				latestFile = new File(DEFAULT_EXPORTER_DIRECTORY+"/"+name+"/");
			}
			catch(Exception ex){
				
			}
		}		
		return latestFile;
	}

	public File getLatestcsv(){
		return this.getLatestcsv(null);
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
