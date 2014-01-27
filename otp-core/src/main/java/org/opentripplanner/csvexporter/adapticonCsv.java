package org.opentripplanner.csvexporter;

import java.io.File;
import java.io.IOException;
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
		File adapticon =new File(DEFAULT_EXPORTER_DIRECTORY+"/adapticon");	
		String[] subFolders = adapticon.list();
		ArrayList<String> adapticonfiles = new ArrayList<String>();	
		if(!adapticon.exists()){
			LOG.error("Adapticon folder doesnt exist. \n " +
					"Please add the adapticon folder to "+ DEFAULT_EXPORTER_DIRECTORY + 
					"directory \n and make sure it contains _adapticon.csv file(s)");
		}
		else{
			return null;
		}
		
		for(String s:subFolders){
			if(s.contains("adapticon")){	
				// check if the files are CSV files 
				File f = new File(DEFAULT_EXPORTER_DIRECTORY+"/adapticon/"+s);				
				if(Files.getFileExtension(f.getAbsolutePath()).equals("csv")){
					adapticonfiles.add(s);
				}}}				
		
		//return the latest adapticon file
		File latestAdapticon=new File(DEFAULT_EXPORTER_DIRECTORY+"/adapticon/"+adapticonfiles.get(0));
		long time1 = latestAdapticon.lastModified();
		for(String s:adapticonfiles){
			File f = new File(DEFAULT_EXPORTER_DIRECTORY+"/adapticon/"+s);
			long time2= f.lastModified();
			if(time2 > time1){
				latestAdapticon = new File(DEFAULT_EXPORTER_DIRECTORY+"/adapticon/"+s);
				time1= time2;}
			
		}
		
		return latestAdapticon;
	}
	
	public void createAdapticoncsv(){
		
		//read latest uploaded externalids csv
		csvFiles csvfile = new csvFiles();
		File latestUpcsv = csvfile.getLastUplodedcsv();
		if(latestUpcsv== null){
			LOG.error("Adapticon folder doesnt exist. \n " +
					"Please add the adapticon folder to "+ DEFAULT_EXPORTER_DIRECTORY + 
					" directory \n and make sure it contains _adapticon.csv file(s)");						
		}		
		
		else{					
							
			try {
				File lastestExternalidCsvFile = new File(latestUpcsv.getAbsolutePath()+"/"+latestUpcsv.getName().split("_")[0].toString()+"_externalids.csv");
				csvReader csvread = new csvReader();				
				ArrayList<ArrayList<String>> externalIdlist = csvread.csvTostringArray(lastestExternalidCsvFile);
						
				//read latest adapticon segment csv file
				File adapticonCsvfile = adapticonSegementFile();
				ArrayList<ArrayList<String>> adapticonList = csvread.csvTostringArray(adapticonCsvfile);			
				
				
				//link externalId list to the adapticon list using the external_id 
				ArrayList<String> extId = new ArrayList<String>();
				for(ArrayList<String> column: externalIdlist){
					if(column.get(0).equals("external_id")){
						extId.addAll(column);
					}						
				}		
				
				ArrayList<String> internalId = new ArrayList<String>();
				for(ArrayList<String> column: externalIdlist){
					if(column.get(0).equals("otp_id")){
						internalId.addAll(column);
					}						
				}
				
				ArrayList<String> extIdadapticon = new ArrayList<String>();
				for(ArrayList<String> column: adapticonList){
					if(column.get(0).equals("external_id")){
						extIdadapticon.addAll(column);
					}						
				}
				
				ArrayList<String> intIdadapticon = new ArrayList<String>();
				for(ArrayList<String> column: adapticonList){
					if(column.get(0).equals("adapticon_segment_id")){
						intIdadapticon.addAll(column);
					}						
				}
				
				File adapticonOutput = new File(DEFAULT_EXPORTER_DIRECTORY+"/adapticon/output/"+CsvExporter.getDateTime());
				csvWriter csvwrite = new csvWriter(adapticonOutput);			
												
				for(String item: intIdadapticon){			
					int index = intIdadapticon.indexOf(item);
					if (index != 0){
						try {
							if(extId.indexOf(extIdadapticon.get(index))!=-1)
								csvwrite.write(item,internalId.get(extId.indexOf(extIdadapticon.get(index))));
						} catch (Exception e) {
							LOG.error("error writing the adapticon output csv file");
						}
					}						
				}
			} catch (Exception e) {
				LOG.error(e.toString());
			}		
		}
			
	}
}
