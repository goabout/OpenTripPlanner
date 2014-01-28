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

import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;

import com.google.common.collect.Lists;

/**
 * Holds information to be included in the REST Response for debugging and profiling purposes. This
 * class contains information pertaining to a single search instance, whereas @link{DebugOutput}
 * contains information about all related searches.
 */
@XmlRootElement
public class DebugOutputInstance {
    /* Only public fields are serialized by JAX-RS */
    long startedCalculating;
    long finishedPrecalculating;
    List<Long> foundPaths = Lists.newArrayList();
    long finishedCalculating;

    /* Results, public to cause JAX-RS serialization */
    public boolean timedOut;
    public long precalculationTime;
    public long pathCalculationTime;
    public List<Long> pathTimes = Lists.newArrayList();

    /**
     * Record the time when we first began calculating a path for this search (before any heuristic
     * pre-calculation). finishedPrecalculating is also set because some heuristics will not mark
     * any precalculation step, and path times are measured from when precalculation ends.
     */
    void startedCalculating() {
        startedCalculating = finishedPrecalculating = System.currentTimeMillis();
    }

    /** Record the time when we finished heuristic pre-calculation. */
    void finishedPrecalculating() {
        finishedPrecalculating = System.currentTimeMillis();
    }

    /** Record the time when a path was found. */
    void foundPath() {
        foundPaths.add(System.currentTimeMillis());
    }

    /** Record the time when we finished calculating paths for this request. */
    void finishedCalculating() {
        finishedCalculating = System.currentTimeMillis();
    }

    /** Summarize and calculate elapsed times. */
    void computeSummary() {
        precalculationTime = finishedPrecalculating - startedCalculating;
        pathCalculationTime = finishedCalculating - finishedPrecalculating;
        long last_t = finishedPrecalculating;
        for (long t : foundPaths) {
            pathTimes.add(t - last_t);
            last_t = t;
        }
    }
}
