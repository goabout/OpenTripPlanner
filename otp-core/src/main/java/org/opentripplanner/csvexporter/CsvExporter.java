package org.opentripplanner.csvexporter;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Collection;
import java.util.Iterator;


import org.opentripplanner.routing.core.State;
import org.opentripplanner.routing.graph.Edge;
import org.opentripplanner.routing.graph.Graph;
import org.opentripplanner.routing.graph.Vertex;
import org.opentripplanner.routing.impl.GraphServiceFileImpl;
import org.opentripplanner.routing.services.GraphService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.csvreader.CsvWriter;

public class CsvExporter {
	private static final String DEFAULT_GRAPH_DIRECTORY = "//Users//Jasper//Desktop//";

	private final GraphService graphService;
	private final Graph graph;

	private static final Logger LOG = LoggerFactory
			.getLogger(GraphServiceFileImpl.class);

	public CsvExporter(GraphService graphService) {
		this.graphService = graphService;
		this.graph = graphService.getGraph();
	}

	public void run() {
		LOG.info("Run CsvExporter");
		this.addVertextoCsv(this.graph.getVertices());
		this.addEdgestoCsv(this.graph.getEdges());
		this.addExternalandOtpidToCsv(this.graph.getEdges());
	}

	public void addVertextoCsv(Collection<Vertex> vertices) {
		LOG.info("adding vertices....");
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
		for (Iterator<Edge> iterator = edges.iterator(); iterator.hasNext();) {
			Edge edge = (Edge) iterator.next();
			int externalId = generateEdgeexternalId(edge);
			Vertex vertexFrom = edge.getFromVertex();
			Vertex vertexTo = edge.getToVertex();
			int vertexFromhashCode = vertexFrom.hashCode();
			int vertexTohashCode = vertexTo.hashCode();	
			int edgeHashcode = generateEdgeexternalId(edge);
			String shape =null;
			write(edgeHashcode,vertexFromhashCode,vertexTohashCode,shape);
		}
		LOG.info("Done adding edges");
	}
	
	
	public void addExternalandOtpidToCsv(Collection<Edge> edges) {
		LOG.info("adding external ids....");
		for (Iterator<Edge> iterator = edges.iterator(); iterator.hasNext();) {
			Edge edge = (Edge) iterator.next();
			int internal_id = edge.hashCode();
			int external_id = generateEdgeexternalId(edge);
			write(external_id, internal_id);
		}
		LOG.info("Done adding external ids");
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
		
		char[] cArray = s.toCharArray();
		int hashCode =0;		
		return s.hashCode();		
	}
	
	/** write method is overloaded to create and write CSV files
	 * @param longitude : longitude of the vertex 
	 * @param latitude	: latitude of the vertex
	 * @param hashCode : hash code of longitude and latitude
	 * this function writes a vertex.csv file containing hashCode(Id), latitude and longitude
	 */
	public void write(int vertexHashcode, double longitude, double latitude) {

		/*
		 * write function with longitude and latitude as arguments write node
		 * csv file csv file path of node is DEFAULT_GRAPH_DIRECTORY
		 */
		String csv = DEFAULT_GRAPH_DIRECTORY + "vertices.csv";

		// before we open the file check to see if it already exists
		boolean exists = new File(csv).exists();
		//LOG.info("to file: ", csv);

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
			
			csvOutput.write(Double.toString(vertexHashcode));
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
	public void write(int edgeHashcode, int vertexFromhashCode, int vertexTohashCode, String shape) {
		/*
		 * write function with longitude and latitude as arguments write node
		 * csv file csv file path of node is DEFAULT_GRAPH_DIRECTORY
		 */
		String csv = DEFAULT_GRAPH_DIRECTORY + "edges.csv";

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
			
			csvOutput.write(Double.toString(edgeHashcode));
			csvOutput.write(Double.toString(vertexFromhashCode));
			csvOutput.write(Double.toString(vertexTohashCode));
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
	public void write(int external_id, int internal_id) {
			
		String csv = DEFAULT_GRAPH_DIRECTORY + "externalids.csv";

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
			csvOutput.write(Double.toString(external_id));
			csvOutput.write(Double.toString(internal_id));
			csvOutput.endRecord();
			csvOutput.close();

		} catch (IOException e) {
			LOG.error("Error while writing to CSV file: {}", e.getMessage());

		}

	}


}
