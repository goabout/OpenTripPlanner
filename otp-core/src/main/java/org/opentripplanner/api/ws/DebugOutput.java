/* This program is free software: you can redistribute it and/or
 modify it under the terms of the GNU Lesser General Public License
 as published by the Free Software Foundation, either version 3 of
 the License, or (props, at your option) any later version.

 This program is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 GNU General Public License for more details.

 You should have received a copy of the GNU General Public License
 along with this program.  If not, see <http://www.gnu.org/licenses/>. */

package org.opentripplanner.api.ws;

import java.util.LinkedList;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Holds information to be included in the REST Response for debugging and profiling purposes.
 *
 * startedCalculating is called in the routingContext constructor.
 * finishedCalculating and finishedRendering are all called in PlanGenerator.generate().
 * finishedPrecalculating and foundPaths are called in the SPTService implementations.
 */
@XmlRootElement
public class DebugOutput {
    private long finishedRendering;

    /* Results, public to cause JAX-RS serialization */
    public LinkedList<DebugOutputInstance> instances = new LinkedList<DebugOutputInstance>();
    public long renderingTime;
    public long totalTime;

    /**
     * Record the time when we first began calculating a path for this request
     * (before any heuristic pre-calculation). Note that timings will not
     * include network and server request queue overhead, which is what we want.
     * finishedPrecalculating is also set because some heuristics will not mark any precalculation
     * step, and path times are measured from when precalculation ends.
     */
    public void startedCalculating() {
        instances.add(new DebugOutputInstance());
        instances.getLast().startedCalculating();
    }

    /** Record the time when we finished heuristic pre-calculation. */
    public void finishedPrecalculating() {
        instances.getLast().finishedPrecalculating();
    }

    /** Record the time when a path was found. */
    public void foundPath() {
        instances.getLast().foundPath();
    }

    /** Record the time when we finished calculating paths for this request. */
    public void finishedCalculating() {
        instances.getLast().finishedCalculating();
    }

    /** Record the time when we finished converting paths into itineraries. */
    public void finishedRendering() {
        finishedRendering = System.currentTimeMillis();
        computeSummary();
    }

    public void timedOut() {
        instances.getLast().timedOut = true;
    }

    /** Summarize and calculate elapsed times. */
    private void computeSummary() {
        LinkedList<String> summaries = new LinkedList<String>();
        for (DebugOutputInstance debugOutputInstance : instances) {
            debugOutputInstance.computeSummary();
            summaries.add(debugOutputInstance.pathTimes.toString());
        }
        System.out.printf("%s\n", summaries);
        renderingTime = finishedRendering - instances.getLast().finishedCalculating;
        totalTime = finishedRendering - instances.getFirst().startedCalculating;
    }
}
