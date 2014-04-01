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

package org.opentripplanner.api.ws;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import lombok.Setter;

import org.opentripplanner.common.geometry.DistanceLibrary;
import org.opentripplanner.common.geometry.SphericalDistanceLibrary;
import org.opentripplanner.routing.edgetype.PlainStreetEdge;
import org.opentripplanner.routing.graph.Graph;
import org.opentripplanner.routing.graph.Vertex;
import org.opentripplanner.routing.services.GraphService;
import org.opentripplanner.updater.car_speeds.CarSpeeds;

import com.sun.jersey.api.core.InjectParam;

@Path("/car")
public class DynamicCarSpeeds {
    @Setter @InjectParam
    private GraphService graphService;

    private DistanceLibrary distanceLibrary = SphericalDistanceLibrary.getInstance();

    @GET
    @Produces(MediaType.TEXT_HTML)
    public String car() {
        if (graphService == null) return null;
        Graph graph = graphService.getGraph();
        if (graph == null) return null;
        CarSpeeds snapshot = graph.getCarSpeeds();

        double minX = Double.MAX_VALUE, maxX = Double.MIN_VALUE, minY = minX, maxY = maxX;
        for (PlainStreetEdge plainStreetEdge : graph.getDynamicCarSpeeds()) {
            Vertex from = plainStreetEdge.getFromVertex();
            Vertex to = plainStreetEdge.getToVertex();
            if (from.getX() < minX) minX = plainStreetEdge.getFromVertex().getX();
            if (from.getX() > maxX) maxX = plainStreetEdge.getFromVertex().getX();
            if (to.getX() < minX) minX = plainStreetEdge.getToVertex().getX();
            if (to.getX() > maxX) maxX = plainStreetEdge.getToVertex().getX();
            if (from.getY() < minY) minY = plainStreetEdge.getFromVertex().getY();
            if (from.getY() > maxY) maxY = plainStreetEdge.getFromVertex().getY();
            if (to.getY() < minY) minY = plainStreetEdge.getToVertex().getY();
            if (to.getY() > maxY) maxY = plainStreetEdge.getToVertex().getY();
        }
        double medX = (minX + maxX) / 2,  medY = (minY + maxY) / 2;
        double height = distanceLibrary.distance(minY, medX, maxY, medX);
        double width = distanceLibrary.distance(medY, minX, medY, maxX);
        double scaleX = Math.min(1, (width / height)) / (maxX - minX);
        double scaleY = Math.min(1, (height / width)) / (maxY - minY);

        StringBuilder response = new StringBuilder();
        long now = System.currentTimeMillis();

        response.append("<!DOCTYPE html>\n");
        response.append("<HTML>\n");
        response.append("<HEAD>\n");
        response.append("<META CHARSET=\"UTF-8\">\n");
        response.append("<TITLE>Dynamic Car Speeds</TITLE>\n");
        response.append("</HEAD>\n");
        response.append("<BODY>\n");
        response.append("<SVG>\n");

        for (PlainStreetEdge plainStreetEdge : graph.getDynamicCarSpeeds()) {
            Vertex from = plainStreetEdge.getFromVertex();
            Vertex to = plainStreetEdge.getToVertex();
            float normal = plainStreetEdge.getDynamicCarSpeed(snapshot, now, 2);
            float current = plainStreetEdge.getDynamicCarSpeed(snapshot, now, 3);
            if (normal >= 0 && current >= 0) response.append(String.format(
                    "<LINE X1=\"%f\" Y1=\"%f\" X2=\"%f\" Y2=\"%f\" STROKE=\"%s\"/>\n",
                    (from.getX() - minX) * 888 * scaleX,
                    (maxY - from.getY()) * 888 * scaleY,
                    (to.getX() - minX) * 888 * scaleX,
                    (maxY - to.getY()) * 888 * scaleY,
                    current > 0 ? current != normal ? current > normal ?
                            "GREEN" : "RED" : "BLUE" : "BLACK"));
        }

        response.append("</SVG>\n");
        response.append("</BODY>\n");
        response.append("</HTML>\n");
        response.trimToSize();

        return response.toString();
    }
}
