package org.opentripplanner.csvexporter;

import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.TimeZone;

import com.vividsolutions.jts.geom.LineString;

import org.opentripplanner.routing.edgetype.PlainStreetEdge;
import org.opentripplanner.routing.graph.Edge;
import org.opentripplanner.routing.graph.Graph;
import org.opentripplanner.routing.graph.Vertex;
import org.opentripplanner.routing.services.GraphService;
import org.apache.commons.codec.digest.DigestUtils;

public class CsvExporter implements csvExporterInterface  {
	
	private String dateTime = null;
	private File timestampDirectory = null;	
	private final Graph graph;
	private csvWriter writer = null;

	public CsvExporter(GraphService graphService) {
		this.graph = graphService.getGraph();
		//get the name time stamp directory
		this.dateTime = getDateTime();		
		this.timestampDirectory = new File(DEFAULT_EXPORTER_DIRECTORY+"/"+dateTime+"/");
		this.writer = new csvWriter(this.timestampDirectory);		
	}

	public void run() {
		LOG.info("Run CsvExporter");				
		this.addVertexToCSV(this.graph.getVertices());
		this.addEdgesToCSV(this.graph.getEdges());
		this.addExternalandOTPIdsToCSV(this.graph.getEdges());
		
		// after writing the new CSV file we compute the percentage of
		// difference between latest uploaded and the new CSV files		
		csvDiff csvdiff =new csvDiff();
		int[] result = csvdiff.computeEdgeDiff();

		if(result != null){					
			LOG.info("Number of edges in the old graph and not in new graph: "+ result[0]);
			LOG.info("Number of edges in the new graph and not in old graph : "+ result[1]);		
			LOG.info("Number of edges in common (both new and old grpahs): " + result[2]);					
		}
		adapticonCsv adapticon = new adapticonCsv();
		adapticon.createAdapticoncsv();
	}

	public void addVertexToCSV(Collection<Vertex> vertices) {
		LOG.info("adding vertices....");
		//check if the directory exists();
		if (!this.timestampDirectory.exists()){			
			this.timestampDirectory.mkdirs();			
		}		
		for (Iterator<Vertex> iterator = vertices.iterator(); iterator
				.hasNext();) {
			Vertex vertex = (Vertex) iterator.next();

			// check if the vertex is real time capable
			if(isVertexRealTimeCapable(vertex)){						
				String hashCode = generateVertExternalId(vertex);
				this.writer.write(hashCode,vertex.getX(), vertex.getY());
			}
		}		
		LOG.info("Done adding vertices to CSV");	
	}

	public void addEdgesToCSV(Collection<Edge> edges) {
		LOG.info("adding edges....");

		//check if the directory exists();
		if (!this.timestampDirectory.exists()){			
			this.timestampDirectory.mkdirs();			
		}

		for (Iterator<Edge> iterator = edges.iterator(); iterator.hasNext();) {			
			Edge edge = (Edge) iterator.next();		
			//check is the edge is a street edge
			//add only street edge
			if(edge instanceof PlainStreetEdge){
				if (!edge.isRealtimeCapable()) {
					continue;
				}
				Vertex vertexFrom = edge.getFromVertex();
				Vertex vertexTo = edge.getToVertex();			
				String vertexFromhashCode = generateVertExternalId(vertexFrom);
				String vertexTohashCode = generateVertExternalId(vertexTo);							
				LineString l = edge.getGeometry();												
				String shape = l.toString();				
				String edgeHashcode = generateEdgeExternalId(edge,shape);
				this.writer.write(edgeHashcode,vertexFromhashCode,vertexTohashCode,shape);				
			}
		}
		LOG.info("Done adding edges");
	}

	public void addExternalandOTPIdsToCSV(Collection<Edge> edges) {
		LOG.info("adding external ids....");

		//check if the directory exists();
		if (!this.timestampDirectory.exists()){			
			this.timestampDirectory.mkdirs();			
		}

		for (Iterator<Edge> iterator = edges.iterator(); iterator.hasNext();) {
			Edge edge = (Edge) iterator.next();
			if(edge instanceof PlainStreetEdge){	
				if (!edge.isRealtimeCapable()) {
					continue;
				}
				int internal_id = edge.getId();
				LineString l = edge.getGeometry();												
				String shape = l.toString();				
				String external_id = generateEdgeExternalId(edge,shape);
				this.writer.write(external_id, Integer.toString(internal_id));
			}
		}
		LOG.info("Done adding external ids");
	}

	public boolean isVertexRealTimeCapable(Vertex vertex){
		//check for real time vertices
		if(vertex.getIncoming()!=null || vertex.getOutgoing()!=null){
			Collection<Edge> incomingEdges = vertex.getIncoming();
			Collection<Edge> outgoingEdges = vertex.getOutgoing();
			boolean incomingEdgeCheck = false;
			boolean outgoingEdgeCheck = false;

			//at least one incoming edge isRealtimeCapable or at least one outgoing edge isRealtimeCapable
			for (Iterator<Edge> incomingEdgeIter = incomingEdges.iterator(); incomingEdgeIter.hasNext();) {			
				Edge inEdge = (Edge) incomingEdgeIter.next();
				if(inEdge instanceof PlainStreetEdge){
					if (inEdge.isRealtimeCapable()) {						
						incomingEdgeCheck =true;					
					}			
				}	
			}
			for (Iterator<Edge> outgoingEdgeIter = outgoingEdges.iterator(); outgoingEdgeIter.hasNext();) {			
				Edge outEdge = (Edge) outgoingEdgeIter.next();
				if(outEdge instanceof PlainStreetEdge){
					if (outEdge.isRealtimeCapable()) {						
						outgoingEdgeCheck =true;					
					}			
				}	
			}
			if(outgoingEdgeCheck || incomingEdgeCheck)
				return true;
			else
				return false;
		}		
		else{
			return false;
		}
	}
	
	public String generateVertExternalId(Vertex v){
		String row = Double.toString(v.getX())+Double.toString(v.getY());		
		return hashCode(row);		
	}
	
	/**generates external id based on the from vertex and to vertex 
	 * @param edge
	 * @return external id 
	 */
	public String generateEdgeExternalId(Edge edge, String shape) {
		Vertex vertexFrom = edge.getFromVertex();
		Vertex vertexTo = edge.getToVertex();
		String vertexFromhashCode = generateVertExternalId(vertexFrom);
		String vertexTohashCode = generateVertExternalId(vertexTo);		

		//String edgeRowstring = Integer.toString(vertexFromhashCode)+Integer.toString(vertexTohashCode)+shape;
		String edgeRowstring = vertexFromhashCode + vertexTohashCode;
		String edgeHashcode = hashCode(edgeRowstring);
		return edgeHashcode;
	}

	public String hashCode(String s){		
		DigestUtils.md5Hex(s);						
		return DigestUtils.md5Hex(s);	
	}

	/** gets Date and Time in string format
	 * @return : yyyyMMdd_hhmmss in string format
	 */
	public final static String getDateTime() {
		DateFormat df = new SimpleDateFormat("hhmmss");		
		df.setTimeZone(TimeZone.getDefault());			
		//return df.format(new Date())+"_"+Long.toString(System.currentTimeMillis());
		return Long.toString(System.currentTimeMillis());
	}

}
