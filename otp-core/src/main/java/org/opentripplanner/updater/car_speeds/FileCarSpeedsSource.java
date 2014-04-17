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
import java.util.TimeZone;
import java.util.prefs.Preferences;

import org.opentripplanner.routing.graph.Graph;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** Reads the dynamic car speeds from a local file. */
class FileCarSpeedsSource extends CarSpeedsSource {
    private static final Logger LOG = LoggerFactory.getLogger(FileCarSpeedsSource.class);

    private TimeZone timeZone;
    private File holidayData;
    private File holidayRegionMappings;
    private File freeFlowSpeedsMatrix;
    private File normalSpeedsMatrix;
    private File holidaySpeedsMatrix;
    private File shortTermSpeedsMatrix;

    @Override
    public void configure(Graph graph, Preferences preferences) throws Exception {
        timeZone = graph.getTimeZone();
        holidayData = new File(preferences.get("holidayData", ""));
        holidayRegionMappings = new File(preferences.get("holidayRegionMappings", ""));
        freeFlowSpeedsMatrix = new File(preferences.get("freeFlowSpeedsMatrix", ""));
        normalSpeedsMatrix = new File(preferences.get("normalSpeedsMatrix", ""));
        holidaySpeedsMatrix = new File(preferences.get("holidaySpeedsMatrix", ""));
        shortTermSpeedsMatrix = new File(preferences.get("shortTermSpeedsMatrix", ""));
    }

    @Override
    public String toString() {
        return "FileCarSpeedsSource(" + holidayData + ',' + holidayRegionMappings + ',' +
                freeFlowSpeedsMatrix + ',' + normalSpeedsMatrix + ',' + holidaySpeedsMatrix + ',' +
                shortTermSpeedsMatrix + ")[" + timeZone.getID() +']';
    }

    @Override
    public CarSpeeds getCarSpeeds() {
        try {
            return new CarSpeeds(new FileReader(holidayData), new FileReader(holidayRegionMappings),
                    new FileReader(freeFlowSpeedsMatrix), new FileReader(normalSpeedsMatrix),
                    new FileReader(holidaySpeedsMatrix), new FileReader(shortTermSpeedsMatrix),
                    timeZone);
        } catch (Exception e) {
            LOG.warn("Failed to parse dynamic car speed data:", e);
            return null;
        }
    }
}
