package org.opentripplanner.csvexporter;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.TimeZone;

import com.vividsolutions.jts.geom.LineString;

import org.opentripplanner.routing.edgetype.PlainStreetEdge;
import org.opentripplanner.routing.graph.Edge;
import org.opentripplanner.routing.graph.Graph;
import org.opentripplanner.routing.graph.Vertex;
import org.opentripplanner.routing.impl.GraphServiceFileImpl;
import org.opentripplanner.routing.services.GraphService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.csvreader.CsvWriter;

public class CsvExporter {
		
	private String DEFAULT_EXPORTER_DIRECTORY = null;
	private String dateTime = null;
	private File timestampDirectory = null;
	private final GraphService graphService;
	private final Graph graph;
	
	private static final Logger LOG = LoggerFactory
			.getLogger(GraphServiceFileImpl.class);

	public CsvExporter(GraphService graphService) {
		this.graphService = graphService;
		this.graph = graphService.getGraph();
		//get the name time stamp directory
		this.dateTime = getDateTime();
		this.DEFAULT_EXPORTER_DIRECTORY = System.getProperty("user.dir").toString()+"/exporters";		
		this.timestampDirectory = new File(DEFAULT_EXPORTER_DIRECTORY+"/"+dateTime+"/");		
	}

	public void run() {
		LOG.info("Run CsvExporter");
		this.addVertextoCsv(this.graph.getVertices());
		this.addEdgestoCsv(this.graph.getEdges());
		this.addExternalandOtpidToCsv(this.graph.getEdges());
	}

	public void addVertextoCsv(Collection<Vertex> vertices) {
		LOG.info("adding vertices....");
		//check if the directory exists();
		if (!this.timestampDirectory.exists()){			
			this.timestampDirectory.mkdirs();			
		}
		
		for (Iterator<Vertex> iterator = vertices.iterator(); iterator
				.hasNext();) {
			Vertex vertex = (Vertex) iterator.next();
			int hashCode = vertex.hashCode();
			write(hashCode,vertex.getX(), vertex.getY());						
		}
		LOG.info("Done adding vertices");
	
	}

	public void addEdgestoCsv(Collection<Edge> edges) {
		LOG.info("adding edges....");
		
		//check if the directory exists();
		if (!this.timestampDirectory.exists()){			
			this.timestampDirectory.mkdirs();			
		}
		
		for (Iterator<Edge> iterator = edges.iterator(); iterator.hasNext();) {			
			Edge edge = (Edge) iterator.next();							
			Vertex vertexFrom = edge.getFromVertex();
			Vertex vertexTo = edge.getToVertex();			
			int vertexFromhashCode = vertexFrom.hashCode();
			int vertexTohashCode = vertexTo.hashCode();	
			int edgeHashcode = generateEdgeexternalId(edge);
			
			//check is the edge is a street edge
			//add only street edge
			if(edge instanceof PlainStreetEdge){	
				// get shape of the edge			
				LineString l = edge.getGeometry();							
				String shape = l.toString();											
				write(edgeHashcode,vertexFromhashCode,vertexTohashCode,shape);
				checkPropertytype c = new checkPropertytype(edge);
				
		}
		}
		LOG.info("Done adding edges");
	}
	
	public void addExternalandOtpidToCsv(Collection<Edge> edges) {
		LOG.info("adding external ids....");
		
		//check if the directory exists();
		if (!this.timestampDirectory.exists()){			
			this.timestampDirectory.mkdirs();			
		}
		
		for (Iterator<Edge> iterator = edges.iterator(); iterator.hasNext();) {
			Edge edge = (Edge) iterator.next();
			int internal_id = edge.getId();
			int external_id = generateEdgeexternalId(edge);
			write(external_id, internal_id);
		}
		LOG.info("Done adding external ids");
	}
	
	/** write method is overloaded to create and write CSV files
	 * @param longitude : longitude of the vertex 
	 * @param latitude	: latitude of the vertex
	 * @param hashCode : hash code of longitude and latitude
	 * this function writes a vertex.csv file containing hashCode(Id), latitude and longitude
	 */
	//TODO: move the write functions to csvWritejava
	private void write(int vertexHashcode, double longitude, double latitude) {
		
		String csv = this.timestampDirectory + "/"+this.dateTime + "_"+ "vertices.csv";					
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
			csvOutput.write(Integer.toString(vertexHashcode));
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
	private void write(int edgeHashcode, int vertexFromhashCode, int vertexTohashCode, String shape) {
		//check if the directory exists();
		if (!this.timestampDirectory.exists()){			
			this.timestampDirectory.mkdir();			
		}
				
		String csv = this.timestampDirectory + "/" + this.dateTime + "_"+ "edges.csv";

		// before we open the file check to see if it already exists
		boolean exists = new File(csv).exists();
		//LOG.info("to file: ", csv);

		try {
			CsvWriter csvOutput = new CsvWriter(new FileWriter(csv, true), ',');
			if (!exists) {
				csvOutput.write("external_id");
				csvOutput.write("from_node_id");
				csvOutput.write("to_node_id");
				csvOutput.write("shape");
				csvOutput.endRecord();
				//LOG.info("header added");
			}
			// else if doesn't add the header
			// but adds records
			
			csvOutput.write(Integer.toString(edgeHashcode));
			csvOutput.write(Integer.toString(vertexFromhashCode));
			csvOutput.write(Integer.toString(vertexTohashCode));
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
	private void write(int external_id, int internal_id) {
		//check if the directory exists();
		if (!this.timestampDirectory.exists()){			
			this.timestampDirectory.mkdir();			
		}
					
		String csv = this.timestampDirectory + "/" + this.dateTime + "_" + "externalids.csv";

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
			csvOutput.write(Integer.toString(external_id));
			csvOutput.write(Integer.toString(internal_id));
			csvOutput.endRecord();
			csvOutput.close();

		} catch (IOException e) {
			LOG.error("Error while writing to CSV file: {}", e.getMessage());

		}

	}

	/**generates external id based on the from vertex, to vertex and shape of the edge
	 * @param edge
	 * @return external id 
	 */
	private int generateEdgeexternalId(Edge edge) {
		Vertex vertexFrom = edge.getFromVertex();
		Vertex vertexTo = edge.getToVertex();
		int vertexFromhashCode = vertexFrom.hashCode();
		int vertexTohashCode = vertexTo.hashCode();		
		//shape not known
		//let shape be string shape
		
		String shape = null;
		String edgeRowstring = Integer.toString(vertexFromhashCode)+Integer.toString(vertexTohashCode)+shape;  					
		int edgeHashcode = hashCode(edgeRowstring);
		return edgeHashcode;
	}
	
	public int hashCode(String s){
				
		return s.hashCode();		
	}
	
	/** gets Date and Time in string format
	 * @return : yyyyMMdd_hhmmss in string format
	 */
	private final static String getDateTime() {
		DateFormat df = new SimpleDateFormat("hhmmss");		
		df.setTimeZone(TimeZone.getDefault());			
		return df.format(new Date())+"_"+Long.toString(System.currentTimeMillis());
	}

}
