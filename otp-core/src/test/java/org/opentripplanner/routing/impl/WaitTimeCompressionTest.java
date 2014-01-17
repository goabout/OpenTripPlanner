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

/* this is in api.common so it can set package-private fields */

package org.opentripplanner.routing.impl;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.opentripplanner.api.model.Itinerary;
import org.opentripplanner.api.model.TripPlan;
import org.opentripplanner.api.ws.PlanGenerator;
import org.opentripplanner.graph_builder.impl.GtfsGraphBuilderImpl;
import org.opentripplanner.graph_builder.model.GtfsBundle;
import org.opentripplanner.routing.algorithm.GenericAStar;
import org.opentripplanner.routing.core.RoutingRequest;
import org.opentripplanner.routing.core.ServiceDay;
import org.opentripplanner.routing.graph.Graph;
import org.opentripplanner.routing.impl.TrivialPathServiceImpl;

import junit.framework.TestCase;

public class WaitTimeCompressionTest extends TestCase {
    public void testWaitTimeCompression() {
        final Itinerary itineraries[] = new Itinerary[3];
        final TripPlan tripPlans[] = new TripPlan[3];
        PlanGenerator planGenerator = new PlanGenerator();
        TrivialPathServiceImpl trivialPathServiceImpl = new TrivialPathServiceImpl();
        GenericAStar genericAStar = new GenericAStar();
        Graph graph = new Graph();
        GtfsBundle gtfsBundle = new GtfsBundle(new File("src/test/resources/WAITTIME.ZIP"));
        List<GtfsBundle> gtfsBundleList = Collections.singletonList(gtfsBundle);
        GtfsGraphBuilderImpl gtfsGraphBuilderImpl = new GtfsGraphBuilderImpl(gtfsBundleList);
        RoutingRequest routingRequest = new RoutingRequest();
        ArrayList<ServiceDay> serviceDayList = new ArrayList<ServiceDay>(1);

        planGenerator.pathService = trivialPathServiceImpl;
        trivialPathServiceImpl.sptService = genericAStar;
        gtfsGraphBuilderImpl.buildGraph(graph, null);
        serviceDayList.add(new ServiceDay(graph, 0, graph.getCalendarService(), "Agency"));

        routingRequest.setArriveBy(false);
        routingRequest.dateTime = 0L;
        routingRequest.showIntermediateStops = true;
        routingRequest.setRoutingContext(graph, "Agency_A", "Agency_G");
        routingRequest.rctx.serviceDays = serviceDayList;
        tripPlans[0] = planGenerator.generate(routingRequest);
        itineraries[0] = tripPlans[0].itinerary.get(0);

        routingRequest.setArriveBy(true);
        routingRequest.dateTime = 65536L;
        routingRequest.showIntermediateStops = true;
        routingRequest.setRoutingContext(graph, "Agency_A", "Agency_G");
        routingRequest.rctx.serviceDays = serviceDayList;
        tripPlans[1] = planGenerator.generate(routingRequest);
        itineraries[1] = tripPlans[1].itinerary.get(0);

        routingRequest.setArriveBy(false);
        routingRequest.dateTime = 24576L;
        routingRequest.showIntermediateStops = true;
        routingRequest.setRoutingContext(graph, "Agency_A", "Agency_G");
        routingRequest.rctx.serviceDays = serviceDayList;
        tripPlans[2] = planGenerator.generate(routingRequest);
        itineraries[2] = tripPlans[2].itinerary.get(0);

        assertEquals(1, tripPlans[2].itinerary.size());
        assertEquals("D", itineraries[2].legs.get(1).stop.get(0).name);
        assertEquals(24576000L, itineraries[2].startTime.getTimeInMillis());
        assertEquals(65536000L, itineraries[2].endTime.getTimeInMillis());
        assertEquals(40960000L, itineraries[2].duration);
        assertEquals(24576L, itineraries[2].transitTime);
        assertEquals(16384L, itineraries[2].waitingTime);
        assertEquals(0L, itineraries[2].walkTime);

        assertEquals(1, tripPlans[1].itinerary.size());
        assertEquals("D", itineraries[1].legs.get(1).stop.get(0).name);
        assertEquals(24576000L, itineraries[1].startTime.getTimeInMillis());
        assertEquals(65536000L, itineraries[1].endTime.getTimeInMillis());
        assertEquals(40960000L, itineraries[1].duration);
        assertEquals(24576L, itineraries[1].transitTime);
        assertEquals(16384L, itineraries[1].waitingTime);
        assertEquals(0L, itineraries[1].walkTime);

        assertEquals(1, tripPlans[0].itinerary.size());
        assertEquals("D", itineraries[0].legs.get(1).stop.get(0).name);
        assertEquals(24576000L, itineraries[0].startTime.getTimeInMillis());
        assertEquals(65536000L, itineraries[0].endTime.getTimeInMillis());
        assertEquals(40960000L, itineraries[0].duration);
        assertEquals(24576L, itineraries[0].transitTime);
        assertEquals(16384L, itineraries[0].waitingTime);
        assertEquals(0L, itineraries[0].walkTime);
    }
}
