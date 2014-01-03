package org.opentripplanner.csvexporter;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;

public class csvDiff {
	
	private File latestCsv = null;
	private File lastUploadedcsv = null;
	
	
	/**csvDiff computed the difference between 
	 * the latest csv and last uploaded csv file
	 * displays the percentage of difference between the vertices, edges. 
	 */
	
	public csvDiff(){
		csvFiles csvfile = new csvFiles();
		latestCsv = csvfile.getLatestcsv();
		lastUploadedcsv = csvfile.getLastUplodedcsv();
		computeVertexdiffPercentage();		
	}
	
	private Collection<String> computeVertexdiffPercentage(){
			
		ArrayList<String> diff = new ArrayList<String>();
		
		ArrayList<ArrayList<String>> dataLatestcsv = new ArrayList<ArrayList<String>>();
		ArrayList<ArrayList<String>> dataLatestuploadedCsv = new ArrayList<ArrayList<String>>();
		
		{File vertexFile = new File(latestCsv.getAbsolutePath()+"/"+latestCsv.getName()+"_vertices.csv");
		csvReader csvread = new csvReader();		
		dataLatestcsv = csvread.csvTostringArray(vertexFile);}
		
		{File vertexFile = new File(lastUploadedcsv.getAbsolutePath()+"/"+lastUploadedcsv.getName()+"_vertices.csv");
		csvReader csvread = new csvReader();		
		dataLatestuploadedCsv = csvread.csvTostringArray(vertexFile);}
				
		for(int i=0;i<dataLatestcsv.size();i++){
			Collection<String> dataList = dataLatestcsv.get(i);
			Collection<String> dataUplist = dataLatestuploadedCsv.get(i);
			Collection<String> duplicatedataUplist = dataUplist;			
			dataUplist.removeAll(dataList);
			dataList.removeAll(duplicatedataUplist);			
			diff.addAll(dataUplist);
			diff.addAll(dataList);			
		}
		
		return diff;					
	}
	
	
	
	

}
