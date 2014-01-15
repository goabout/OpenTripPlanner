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

class HolidayRegionMappings {
    private final Region regions[];

    HolidayRegionMappings(Segment[] segments) {
        int highest = 0;

        for (Segment segment : segments) {
            int id = Integer.parseInt(segment.id);
            if (id > highest) highest = id;
        }

        regions = new Region[highest];

        for (Segment segment : segments) {
            int id = Integer.parseInt(segment.id);
            String value = segment.value;
            if ("Midden".equals(value)) {
                regions[id - 1] = Region.MIDDEN;
            } else if ("Noord".equals(value)) {
                regions[id - 1] = Region.NOORD;
            } else if ("Zuid".equals(value)) {
                regions[id - 1] = Region.ZUID;
            }
        }
    }

    Region getRegion(int segment) {
        if (segment < 1 || segment > regions.length) return null;
        return regions[segment - 1];
    }
}
