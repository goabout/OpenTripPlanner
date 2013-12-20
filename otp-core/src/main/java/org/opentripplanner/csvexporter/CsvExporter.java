package org.opentripplanner.csvexporter;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Collection;
import java.util.Iterator;

import org.opentripplanner.routing.graph.Edge;
import org.opentripplanner.routing.graph.Graph;
import org.opentripplanner.routing.graph.Vertex;
import org.opentripplanner.routing.impl.GraphServiceFileImpl;
import org.opentripplanner.routing.services.GraphService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.csvreader.CsvWriter;

public class CsvExporter {
	private static final String DEFAULT_GRAPH_DIRECTORY  = "//Users//Jasper//Desktop//";
	
	private final GraphService graphService;
    private final Graph graph;
    
    private static final Logger LOG = LoggerFactory.getLogger(GraphServiceFileImpl.class);

    public CsvExporter(GraphService graphService){
        this.graphService = graphService;
        this.graph = graphService.getGraph();
    }

    public void run () {
        LOG.info("Run CsvExporter");
        this.addVertextoCsv ( this.graph.getVertices() );
    }
    
    public void addVertextoCsv(Collection<Vertex> vertices){        
        
        LOG.info("adding vertices....");
        for (Iterator<Vertex> iterator = vertices.iterator(); iterator.hasNext();) {
            Vertex vertex = (Vertex) iterator.next();
            int hashCode = vertex.hashCode();
            write(vertex.getX(),vertex.getY(),hashCode);             
        }
    }	
	
	public void addEdgestoCsv(Collection<Edge> edges){				
		for (Iterator<Edge> iterator = edges.iterator(); iterator.hasNext();) {
            Edge edge = (Edge) iterator.next();
        }
	}
	
	
	public void write(double longitude, double latitude,int hashCode){
        
        /*write function with longitude and latitude as arguments
        write node csv file
        csv file path of node is DEFAULT_GRAPH_DIRECTORY
        */
        String csv = DEFAULT_GRAPH_DIRECTORY + "vertex.csv";
        
        // before we open the file check to see if it already exists
        boolean exists = new File(csv).exists();
        LOG.info("to file: ",csv);
                
        try{
            CsvWriter csvOutput = new CsvWriter(new FileWriter(csv, true), ',');
            if(!exists){
                csvOutput.write("Id");
                csvOutput.write("latitude");
                csvOutput.write("longitude");        
                LOG.info("header added");
            }
            //else if doesn't add the header
            // but adds records
            
            /*int externalId =  (Double.toString(latitude)+Double.toString(longitude)).hashCode(); //generate hash id
*/            csvOutput.write(Integer.toString(hashCode)); 
            csvOutput.write(Double.toString(latitude));
            csvOutput.write(Double.toString(longitude));
            csvOutput.endRecord();                    
            csvOutput.close();
        }
        catch(IOException e){
            LOG.error("Error while writing to CSV file: {}", e.getMessage());
            
        }
        
    }

}
