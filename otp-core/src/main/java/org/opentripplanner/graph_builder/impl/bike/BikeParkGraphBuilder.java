/* This program is free software: you can redistribute it and/or
 modify it under the terms of the GNU Lesser General Public License
 as published by the Free Software Foundation, either version 3 of
 the License, or (at your option) any later version.

 This program is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 GNU General Public License for more details.

 You should have received a copy of the GNU General Public License
 along with this program.  If not, see <http://www.gnu.org/licenses/>. */

package org.opentripplanner.graph_builder.impl.bike;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

import lombok.Setter;

import org.opentripplanner.gbannotation.BikeParkUnlinked;
import org.opentripplanner.graph_builder.services.GraphBuilder;
import org.opentripplanner.routing.bike_park.BikePark;
import org.opentripplanner.routing.bike_rental.BikeRentalStationService;
import org.opentripplanner.routing.edgetype.BikeParkEdge;
import org.opentripplanner.routing.edgetype.loader.NetworkLinkerLibrary;
import org.opentripplanner.routing.graph.Graph;
import org.opentripplanner.routing.vertextype.BikeParkVertex;
import org.opentripplanner.updater.bike_park.BikeParkDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BikeParkGraphBuilder implements GraphBuilder {

    private static Logger LOG = LoggerFactory.getLogger(BikeParkGraphBuilder.class);

    @Setter
    private BikeParkDataSource dataSource;
    
    @Setter
    private String namePrefix;

    @Override
    public void buildGraph(Graph graph, HashMap<Class<?>, Object> extra) {

        LOG.info("Building bike parks from static source...");
        NetworkLinkerLibrary networkLinkerLibrary = new NetworkLinkerLibrary(graph, extra);
        BikeRentalStationService service = graph.getService(BikeRentalStationService.class, true);
        if (!dataSource.update()) {
            LOG.warn("No bike parks found from the data source.");
            return;
        }
        Collection<BikePark> bikeParks = dataSource.getBikeParks();

        for (BikePark bikePark : bikeParks) {
            if (namePrefix != null)
                bikePark.name = namePrefix + bikePark.name;
            service.addBikePark(bikePark);
            BikeParkVertex bikeParkVertex = new BikeParkVertex(graph, bikePark);
            new BikeParkEdge(bikeParkVertex);
            if (!networkLinkerLibrary.connectVertexToStreets(bikeParkVertex).getResult()) {
                LOG.warn(graph.addBuilderAnnotation(new BikeParkUnlinked(bikeParkVertex)));
            }
        }
        LOG.info("Created " + bikeParks.size() + " bike parks.");
    }

    @Override
    public List<String> provides() {
        return Arrays.asList("bike_parks");
    }

    @Override
    public List<String> getPrerequisites() {
        return Arrays.asList("streets");
    }

    @Override
    public void checkInputs() {
    }
}
