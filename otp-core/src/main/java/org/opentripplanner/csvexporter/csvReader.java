package org.opentripplanner.csvexporter;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

import com.google.common.io.Files;

public class csvReader implements csvExporterInterface{

	public ArrayList<ArrayList<String>> csvTostringArray(File csv){
			
		ArrayList<ArrayList<String>> data = new ArrayList<ArrayList<String>>();
		//check if the file exists and it is .csv file
		try{
			if(Files.getFileExtension(csv.getAbsolutePath()).equals("csv")){
				LOG.info("reading csv file : "+ csv.getAbsolutePath());						
				BufferedReader reader = new BufferedReader(new FileReader(csv.getAbsolutePath()));				
				String line = "";
				
				while((line=reader.readLine())!=null){						
					String[] row = line.trim().split(",");						
					ArrayList<String> rowData = new ArrayList<String>();
					for(String e: row){								
						rowData.add(e);
					}
						if(data.size()==0){								
							for(int i=0;i<rowData.size();i++){								
								ArrayList<String> element = new ArrayList<String>();
								element.add(rowData.get(i));
								data.add(element);
							}
						}
						else{
							for(int i=0;i<rowData.size();i++){									
								data.get(i).add(rowData.get(i));
							}							
						}
					}
					
			}
			else{
				LOG.error("not a csv file: "+csv.getAbsolutePath());
			}
			
		}
		catch(IOException ioe){
			LOG.error("file not found: "+csv.getAbsolutePath());
		}
		return data;		
		
	}
}
