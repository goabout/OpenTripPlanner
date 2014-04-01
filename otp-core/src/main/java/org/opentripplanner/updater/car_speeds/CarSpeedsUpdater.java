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

package org.opentripplanner.updater.car_speeds;

import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.prefs.Preferences;

import org.opentripplanner.routing.edgetype.PlainStreetEdge;
import org.opentripplanner.routing.graph.Edge;
import org.opentripplanner.routing.graph.Graph;
import org.opentripplanner.updater.GraphUpdaterManager;
import org.opentripplanner.updater.GraphWriterRunnable;
import org.opentripplanner.updater.PollingGraphUpdater;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Update OTP car speeds matrix from an external source
 *
 * Usage example ('car' name is an example) in file 'Graph.properties':
 *
 * <pre>
 * car.type = car-speeds-updater
 * car.frequencySec = 60
 * car.sourceType = car-speeds-http
 * car.url = http://host.tld/path
 * car.segmentMapping = SegmentMapping.csv
 * </pre>
 *
 */
public class CarSpeedsUpdater extends PollingGraphUpdater {
    private static final Logger LOG = LoggerFactory.getLogger(CarSpeedsUpdater.class);

    /**
     * Parent update manager. Is used to execute graph writer runnables.
     */
    private GraphUpdaterManager updaterManager;

    /**
     * Update streamer
     */
    private CarSpeedsSource carSpeedsSource;

    @Override
    public void setGraphUpdaterManager(GraphUpdaterManager updaterManager) {
        this.updaterManager = updaterManager;
    }

    @Override
    public void configurePolling(Graph graph, Preferences preferences) throws Exception {
        // Apply segment mapping to graph
        String segmentMapping = preferences.get("segmentMapping", null);
        if (segmentMapping != null) {
            File file = new File(segmentMapping);
            Segment[] array = Segment.readSegments(new FileReader(file));
            ArrayList<PlainStreetEdge> dynamicCarSpeeds = new ArrayList<PlainStreetEdge>();

            for (Segment segment : array) {
                String segmentId = segment.id;
                String edgeId = segment.value;
                Edge edge = graph.getEdgeById(Integer.parseInt(edgeId));

                if (!(edge instanceof PlainStreetEdge)) {
                    LOG.error("Segment id {} cannot be mapped onto edge id {}.", segmentId, edgeId);
                    continue;
                }

                PlainStreetEdge plainStreetEdge = (PlainStreetEdge) edge;
                plainStreetEdge.setSegmentId(Integer.parseInt(segmentId));
                dynamicCarSpeeds.add(plainStreetEdge);
            }

            dynamicCarSpeeds.trimToSize();
            graph.setDynamicCarSpeeds(dynamicCarSpeeds);
        }

        // Create update streamer from preferences
        String sourceType = preferences.get("sourceType", "(not specified)");
        if (sourceType.equals("car-speeds-http")) {
            carSpeedsSource = new HttpCarSpeedsSource();
        } else if (sourceType.equals("car-speeds-file")) {
            carSpeedsSource = new FileCarSpeedsSource();
        } else {
            throw new IllegalArgumentException("Unknown car speeds source type: " + sourceType);
        }

        // Configure update source
        carSpeedsSource.configure(graph, preferences);

        LOG.info("Creating car speeds updater running every {} seconds : {}",
                getFrequencySec(), carSpeedsSource);
    }

    @Override
    public void setup() {
    }

    /**
     * Repeatedly makes blocking calls to an UpdateStreamer to retrieve new car speed updates, and
     * applies those updates to the graph.
     */
    @Override
    public void runPolling() {
        // Get new car speeds from update source
        final CarSpeeds carSpeeds = carSpeedsSource.getCarSpeeds();

        if (carSpeeds != null) {
            // Handle car speed updates via graph writer runnable
            updaterManager.execute(new GraphWriterRunnable() {
                @Override
                public void run(Graph graph) {
                    graph.setCarSpeeds(carSpeeds);
                }
            });
        }
    }

    @Override
    public void teardown() {
    }

    public String toString() {
        String s = (carSpeedsSource == null) ? "NONE" : carSpeedsSource.toString();
        return "Streaming car speeds updater with update source = " + s;
    }
}
