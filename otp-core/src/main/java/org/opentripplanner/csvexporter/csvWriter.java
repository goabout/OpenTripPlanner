package org.opentripplanner.csvexporter;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import org.opentripplanner.standalone.OTPConfigurator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.csvreader.CsvWriter;

public class csvWriter{
	
	private static final Logger LOG = LoggerFactory.getLogger(OTPConfigurator.class);
	private File dir = null;
	private String dateTime = null;
	
	public csvWriter(File directory){
		dir = directory;	
		this.dateTime = dir.getName();		
	}
	
	
	/** write method is overloaded to create and write CSV files
	 * @param longitude : longitude of the vertex 
	 * @param latitude	: latitude of the vertex
	 * @param hashCode : hash code of longitude and latitude
	 * this function writes a vertex.csv file containing hashCode(Id), latitude and longitude
	 */
	
	public void write(String vertexHashcode, double longitude, double latitude) {
		
		String csv = this.dir + "/"+this.dateTime + "_"+ "vertices.csv";					
		//check to see if the file already exists
		boolean exists = new File(csv).exists();
		
		try {
			CsvWriter csvOutput = new CsvWriter(new FileWriter(csv, true), ',');
			if (!exists) {
				csvOutput.write("external_id");
				csvOutput.write("latitude");
				csvOutput.write("longitude");
				csvOutput.endRecord();
				//LOG.info("header added");
			}
			// else if doesn't add the header
			// but adds records			
			csvOutput.write(vertexHashcode);
			csvOutput.write(Double.toString(latitude));
			csvOutput.write(Double.toString(longitude));
			csvOutput.endRecord();
			csvOutput.close();
		} catch (IOException e) {
			LOG.error("Error while writing to CSV file: {}", e.getMessage());

		}

	}
	
	/** write method is overloaded to create and write CSV files
	 * @param vertexFromhashCode : hash code of from vertex of the edge
	 * @param vertexTohashCode : hash code of to vertex of the edge
	 * @param edgeHashcode : hash code of the edge row
	 */
	public void write(String edgeHashcode, String vertexFromhashCode, String vertexTohashCode, String shape) {
		//check if the directory exists();
		if (!this.dir.exists()){			
			this.dir.mkdir();			
		}
				
		String csv = this.dir + "/" + this.dateTime + "_"+ "edges.csv";

		// before we open the file check to see if it already exists
		boolean exists = new File(csv).exists();
		//LOG.info("to file: ", csv);

		try {
			CsvWriter csvOutput = new CsvWriter(new FileWriter(csv, true), ',');
			if (!exists) {
				csvOutput.write("external_id");
				csvOutput.write("from_node_exid");
				csvOutput.write("to_node_exid");
				csvOutput.write("shape");
				csvOutput.endRecord();
				//LOG.info("header added");
			}
			// else if doesn't add the header
			// but adds records
			
			csvOutput.write(edgeHashcode);
			csvOutput.write((vertexFromhashCode));
			csvOutput.write((vertexTohashCode));
			csvOutput.write(shape);
			csvOutput.endRecord();
			csvOutput.close();
		} catch (IOException e) {
			LOG.error("Error while writing to CSV file: {}", e.getMessage());

		}
	}
	
	/**write method is overloaded to create and write CSV files
	 * @param external_id : hash code generated using edge 
	 * @param internal_id : OTP id
	 * this function writes a Externalid.csv file containing external id and OTP id
	 */
	public void write(String external_id, String internal_id) {
		//check if the directory exists();
		if (!this.dir.exists()){			
			this.dir.mkdir();			
		}
					
		String csv = this.dir + "/" + this.dateTime + "_" + "externalids.csv";

		// before we open the file check to see if it already exists
		boolean exists = new File(csv).exists();
		//LOG.info("to file: ", csv);

		try {
			CsvWriter csvOutput = new CsvWriter(new FileWriter(csv, true), ',');
			if (!exists) {
				csvOutput.write("external_id");
				csvOutput.write("otp_id");
				csvOutput.endRecord();
				//LOG.info("header added");
			}
			// else if doesn't add the header
			// but adds records
			csvOutput.write(external_id);
			csvOutput.write(internal_id);
			csvOutput.endRecord();
			csvOutput.close();

		} catch (IOException e) {
			LOG.error("Error while writing to CSV file: {}", e.getMessage());

		}

	}
	

}