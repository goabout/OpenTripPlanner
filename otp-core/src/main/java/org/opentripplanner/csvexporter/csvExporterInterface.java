package org.opentripplanner.csvexporter;

import org.opentripplanner.routing.impl.GraphServiceFileImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public interface csvExporterInterface {
	
	public String DEFAULT_EXPORTER_DIRECTORY = System.getProperty("user.dir").toString()+"/exporters";
	public static final Logger LOG = LoggerFactory.getLogger(GraphServiceFileImpl.class);
	
}
