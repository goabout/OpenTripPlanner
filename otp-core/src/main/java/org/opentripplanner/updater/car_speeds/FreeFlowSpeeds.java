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

import static org.opentripplanner.updater.car_speeds.CarSpeeds.KM_H;

class FreeFlowSpeeds extends SpeedsMatrix {
    private final float speeds[];

    FreeFlowSpeeds(Segment[] segments) {
        int highest = 0;

        for (Segment segment : segments) {
            int id = Integer.parseInt(segment.id);
            if (id > highest) highest = id;
        }

        speeds = new float[highest];

        for (Segment segment : segments) {
            int id = Integer.parseInt(segment.id);
            speeds[id - 1] = Float.parseFloat(segment.value) * KM_H;
        }
    }

    float getCarSpeed(long timestamp, int segment, int day, int hour, int minute) {
        if (segment < 1 || segment > speeds.length) return Float.NaN;
        return speeds[segment - 1];
    }
}
