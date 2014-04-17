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

import java.util.Date;
import java.util.TimeZone;

import org.opentripplanner.util.DateUtils;

class HolidayRegion {
    private final static TimeZone UTC = TimeZone.getTimeZone("UTC");

    private final long timestamps[][];

    HolidayRegion(Holiday[] holidays) {
        timestamps = new long[holidays.length][2];

        for (int i = 0; i < holidays.length; i++) {
            String fromDateString = holidays[i].yearFromUtc + '-'
                    + holidays[i].monthFromUtc + '-' + holidays[i].monthdayFromUtc;
            String fromTimeString = holidays[i].hourFromUtc + ':' + holidays[i].minuteFromUtc;
            String toTimeString = holidays[i].hourToUtc + ':' + holidays[i].minuteToUtc;
            String toDateString = holidays[i].yearToUtc + '-'
                    + holidays[i].monthToUtc + '-' + holidays[i].monthdayToUtc;

            Date fromDate = DateUtils.toDate(fromDateString, fromTimeString, UTC);
            Date toDate = DateUtils.toDate(toDateString, toTimeString, UTC);

            if (fromDate == null || toDate == null) {
                throw new RuntimeException("Unable to parse date of holiday.");
            }

            timestamps[i][0] = fromDate.getTime();
            timestamps[i][1] = toDate.getTime();
        }
    }

    boolean isHoliday(long timestamp) {
        for (int i = 0; i < timestamps.length; i++) {
            if (timestamps[i][0] <= timestamp && timestamp < timestamps[i][1]) return true;
        }
        return false;
    }
}
