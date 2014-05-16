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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.TimeZone;
import java.util.prefs.Preferences;

import org.junit.Before;
import org.junit.Test;
import org.opentripplanner.common.geometry.PackedCoordinateSequence;
import org.opentripplanner.routing.core.RoutingContext;
import org.opentripplanner.routing.core.RoutingRequest;
import org.opentripplanner.routing.core.State;
import org.opentripplanner.routing.core.TraverseMode;
import org.opentripplanner.routing.edgetype.PlainStreetEdge;
import org.opentripplanner.routing.edgetype.StreetTraversalPermission;
import org.opentripplanner.routing.graph.Edge;
import org.opentripplanner.routing.graph.Graph;
import org.opentripplanner.routing.vertextype.IntersectionVertex;
import org.opentripplanner.updater.GraphUpdaterManager;

import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.LineString;

public class CarSpeedsTest {
    @Before
    public final void setUp() {
        Edge.reset();
    }

    @Test
    public final void testSpeedComposition() {
        State arrive, depart;
        RoutingRequest routingRequest = new RoutingRequest(TraverseMode.CAR);
        GeometryFactory geometryFactory = new GeometryFactory();
        PackedCoordinateSequence coordinates = new PackedCoordinateSequence.Double(
                new double[]{0, 0, 180, 90}, 2);
        LineString lineString = new LineString(coordinates, geometryFactory);

        Graph graph = mock(Graph.class);
        IntersectionVertex v0 = mock(IntersectionVertex.class);
        IntersectionVertex v1 = mock(IntersectionVertex.class);
        CarSpeeds carSpeeds = mock(CarSpeeds.class);

        when(graph.getTimeZone()).thenReturn(TimeZone.getTimeZone("UTC"));
        when(graph.getCarSpeeds()).thenReturn(carSpeeds);
        when(carSpeeds.getCarSpeed(0L, 1, Integer.MAX_VALUE)).thenReturn(1.0F);
        when(carSpeeds.getCarSpeed(300000L, 1, Integer.MAX_VALUE)).thenReturn(2.0F);
        when(carSpeeds.getCarSpeed(600000L, 1, Integer.MAX_VALUE)).thenReturn(3.0F);
        when(carSpeeds.getCarSpeed(900000L, 1, Integer.MAX_VALUE)).thenReturn(4.0F);
        when(carSpeeds.getCarSpeed(1200000L, 1, Integer.MAX_VALUE)).thenReturn(5.0F);

        PlainStreetEdge edge = new PlainStreetEdge(
                v0, v1, lineString, "Edge", 4321.0, StreetTraversalPermission.ALL, false, 1);

        edge.setSegmentId(1);

        routingRequest.arriveBy = false;
        routingRequest.dateTime = 0L;
        routingRequest.rctx = new RoutingContext(routingRequest, graph, v0, v1);
        depart = new State(routingRequest);
        arrive = edge.traverse(depart);
        assertEquals(1465000L, arrive.getTimeInMillis());
    }

    @Test
    public final void testBackwardToForward() {
        State arrive, depart;
        RoutingRequest routingRequest = new RoutingRequest(TraverseMode.CAR);
        GeometryFactory geometryFactory = new GeometryFactory();
        PackedCoordinateSequence coordinates = new PackedCoordinateSequence.Double(
                new double[]{0, 0, 180, 90}, 2);
        LineString lineString = new LineString(coordinates, geometryFactory);

        Graph graph = mock(Graph.class);
        IntersectionVertex v0 = mock(IntersectionVertex.class);
        IntersectionVertex v1 = mock(IntersectionVertex.class);
        CarSpeeds carSpeeds = mock(CarSpeeds.class);

        when(graph.getTimeZone()).thenReturn(TimeZone.getTimeZone("UTC"));
        when(graph.getCarSpeeds()).thenReturn(carSpeeds);
        when(carSpeeds.getCarSpeed(0L, 1, Integer.MAX_VALUE)).thenReturn(2.0F);
        when(carSpeeds.getCarSpeed(299000L, 1, Integer.MAX_VALUE)).thenReturn(2.0F);
        when(carSpeeds.getCarSpeed(300000L, 1, Integer.MAX_VALUE)).thenReturn(1.0F);
        when(carSpeeds.getCarSpeed(301000L, 1, Integer.MAX_VALUE)).thenReturn(1.0F);

        PlainStreetEdge edge = new PlainStreetEdge(
                v0, v1, lineString, "Edge", 2.0, StreetTraversalPermission.ALL, false, 1);

        edge.setSegmentId(1);

        routingRequest.arriveBy = true;
        routingRequest.dateTime = 301L;
        routingRequest.rctx = new RoutingContext(routingRequest, graph, v0, v1);
        depart = new State(routingRequest);
        arrive = edge.traverse(depart);
        assertEquals(299000L, arrive.getTimeInMillis());

        routingRequest.arriveBy = false;
        routingRequest.dateTime = 299L;
        routingRequest.rctx = new RoutingContext(routingRequest, graph, v0, v1);
        depart = new State(routingRequest);
        arrive = edge.traverse(depart);
        assertEquals(300000L, arrive.getTimeInMillis());
    }

    @Test
    public final void testForwardToBackward() {
        State arrive, depart;
        RoutingRequest routingRequest = new RoutingRequest(TraverseMode.CAR);
        GeometryFactory geometryFactory = new GeometryFactory();
        PackedCoordinateSequence coordinates = new PackedCoordinateSequence.Double(
                new double[]{0, 0, 180, 90}, 2);
        LineString lineString = new LineString(coordinates, geometryFactory);

        Graph graph = mock(Graph.class);
        IntersectionVertex v0 = mock(IntersectionVertex.class);
        IntersectionVertex v1 = mock(IntersectionVertex.class);
        CarSpeeds carSpeeds = mock(CarSpeeds.class);

        when(graph.getTimeZone()).thenReturn(TimeZone.getTimeZone("UTC"));
        when(graph.getCarSpeeds()).thenReturn(carSpeeds);
        when(carSpeeds.getCarSpeed(299000L, 1, Integer.MAX_VALUE)).thenReturn(1.0F);
        when(carSpeeds.getCarSpeed(300000L, 1, Integer.MAX_VALUE)).thenReturn(2.0F);
        when(carSpeeds.getCarSpeed(301000L, 1, Integer.MAX_VALUE)).thenReturn(2.0F);

        PlainStreetEdge edge = new PlainStreetEdge(
                v0, v1, lineString, "Edge", 2.0, StreetTraversalPermission.ALL, false, 1);

        edge.setSegmentId(1);

        routingRequest.arriveBy = false;
        routingRequest.dateTime = 299L;
        routingRequest.rctx = new RoutingContext(routingRequest, graph, v0, v1);
        depart = new State(routingRequest);
        arrive = edge.traverse(depart);
        assertEquals(301000L, arrive.getTimeInMillis());

        routingRequest.arriveBy = true;
        routingRequest.dateTime = 301L;
        routingRequest.rctx = new RoutingContext(routingRequest, graph, v0, v1);
        depart = new State(routingRequest);
        arrive = edge.traverse(depart);
        assertEquals(300000L, arrive.getTimeInMillis());
    }

    @Test
    public final void testCarSpeeds() throws Exception {
        State arrive, depart;
        Graph graph = new Graph();
        GraphUpdaterManager graphUpdaterManager = new GraphUpdaterManager(graph);
        CarSpeedsUpdater carSpeedsUpdater = new CarSpeedsUpdater();
        IntersectionVertex v0 = new IntersectionVertex(graph, "Vertex 0", 0, 0);
        IntersectionVertex v1 = new IntersectionVertex(graph, "Vertex 1", 180, 90);
        GeometryFactory geometryFactory = new GeometryFactory();
        PackedCoordinateSequence coordinates = new PackedCoordinateSequence.Double(
                new double[]{0, 0, 180, 90}, 2);
        LineString lineString = new LineString(coordinates, geometryFactory);
        PlainStreetEdge edge = new PlainStreetEdge(
                v0, v1, lineString, "Edge", 1.0, StreetTraversalPermission.ALL, false, 1);
        RoutingRequest routingRequest = new RoutingRequest(TraverseMode.CAR);

        Preferences preferences = mock(Preferences.class);

        when(preferences.getInt(eq("frequencySec"), anyInt())).thenReturn(60);
        when(preferences.get(eq("sourceType"), anyString())).thenReturn("car-speeds-file");
        when(preferences.get(eq("segmentMapping"), anyString())).thenReturn(
                "src/test/resources/SegmentMapping.csv");
        when(preferences.get(eq("holidayData"), anyString())).thenReturn(
                "src/test/resources/Holidays.csv");
        when(preferences.get(eq("holidayRegionMappings"), anyString())).thenReturn(
                "src/test/resources/HolidayRegionMappings.csv");
        when(preferences.get(eq("freeFlowSpeedsMatrix"), anyString())).thenReturn(
                "src/test/resources/FreeFlowSpeeds.csv");
        when(preferences.get(eq("normalSpeedsMatrix"), anyString())).thenReturn(
                "src/test/resources/WeekTimeSlotSpeedsArraysNormal.csv");
        when(preferences.get(eq("holidaySpeedsMatrix"), anyString())).thenReturn(
                "src/test/resources/WeekTimeSlotSpeedsArraysHoliday.csv");
        when(preferences.get(eq("shortTermSpeedsMatrix"), anyString())).thenReturn(
                "src/test/resources/ShortTermTimeSlotSpeedsArrays.csv");

        routingRequest.rctx = new RoutingContext(routingRequest, graph, v0, v1);
        graph.rebuildVertexAndEdgeIndices();
        carSpeedsUpdater.configure(graph, preferences);
        carSpeedsUpdater.setGraphUpdaterManager(graphUpdaterManager);
        graphUpdaterManager.addUpdater(carSpeedsUpdater);

        assertNull(routingRequest.rctx.carSpeedsSnapshot);
        while (graph.getCarSpeeds() == null);
        assertNull(routingRequest.rctx.carSpeedsSnapshot);
        routingRequest.rctx = new RoutingContext(routingRequest, graph, v0, v1);
        assertNotNull(routingRequest.rctx.carSpeedsSnapshot);

        routingRequest.dateTime = 0L;
        depart = new State(routingRequest);
        arrive = edge.traverse(depart);
        assertEquals(3000L, arrive.getTimeInMillis());

        routingRequest.dateTime = 297L;
        depart = new State(routingRequest);
        arrive = edge.traverse(depart);
        assertEquals(300000L, arrive.getTimeInMillis());

        routingRequest.dateTime = 300L;
        depart = new State(routingRequest);
        arrive = edge.traverse(depart);
        assertEquals(304000L, arrive.getTimeInMillis());

        routingRequest.dateTime = 596L;
        depart = new State(routingRequest);
        arrive = edge.traverse(depart);
        assertEquals(600000L, arrive.getTimeInMillis());

        routingRequest.dateTime = 600L;
        depart = new State(routingRequest);
        arrive = edge.traverse(depart);
        assertEquals(603000L, arrive.getTimeInMillis());

        routingRequest.dateTime = 897L;
        depart = new State(routingRequest);
        arrive = edge.traverse(depart);
        assertEquals(900000L, arrive.getTimeInMillis());

        routingRequest.dateTime = 900L;
        depart = new State(routingRequest);
        arrive = edge.traverse(depart);
        assertEquals(905000L, arrive.getTimeInMillis());

        routingRequest.dateTime = 1195L;
        depart = new State(routingRequest);
        arrive = edge.traverse(depart);
        assertEquals(1200000L, arrive.getTimeInMillis());

        routingRequest.dateTime = 1200L;
        depart = new State(routingRequest);
        arrive = edge.traverse(depart);
        assertEquals(1205000L, arrive.getTimeInMillis());

        routingRequest.dateTime = 1495L;
        depart = new State(routingRequest);
        arrive = edge.traverse(depart);
        assertEquals(1500000L, arrive.getTimeInMillis());

        routingRequest.dateTime = 1500L;
        depart = new State(routingRequest);
        arrive = edge.traverse(depart);
        assertEquals(1506000L, arrive.getTimeInMillis());

        routingRequest.dateTime = 1794L;
        depart = new State(routingRequest);
        arrive = edge.traverse(depart);
        assertEquals(1800000L, arrive.getTimeInMillis());

        routingRequest.dateTime = 1800L;
        depart = new State(routingRequest);
        arrive = edge.traverse(depart);
        assertEquals(1803000L, arrive.getTimeInMillis());

        routingRequest.dateTime = 2097L;
        depart = new State(routingRequest);
        arrive = edge.traverse(depart);
        assertEquals(2100000L, arrive.getTimeInMillis());

        routingRequest.ignoreRealtimeUpdates = true;
        assertNotNull(routingRequest.rctx.carSpeedsSnapshot);
        routingRequest.rctx = new RoutingContext(routingRequest, graph, v0, v1);
        assertNull(routingRequest.rctx.carSpeedsSnapshot);

        routingRequest.dateTime = 0L;
        depart = new State(routingRequest);
        arrive = edge.traverse(depart);
        assertEquals(1000L, arrive.getTimeInMillis());

        graphUpdaterManager.stop();
        routingRequest.ignoreRealtimeUpdates = false;
        assertNull(routingRequest.rctx.carSpeedsSnapshot);

        routingRequest.rctx = new RoutingContext(routingRequest, graph, v0, v1);
        graph.setCarSpeeds(null);
        assertNotNull(routingRequest.rctx.carSpeedsSnapshot);

        routingRequest.rctx = new RoutingContext(routingRequest, graph, v0, v1);
        assertNull(routingRequest.rctx.carSpeedsSnapshot);
        assertNull(graph.getCarSpeeds());
    }
}
