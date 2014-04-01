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

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.TimeZone;

abstract class SpeedsMatrix {
    private final TimeZone timeZone;

    abstract float getCarSpeed(long timestamp, int segment, int day, int hour, int minute);

    SpeedsMatrix() {
        // If no time zone was specified (the default), fall back (and don't spring forward) to UTC.
        this(TimeZone.getTimeZone("UTC"));
    }

    SpeedsMatrix(TimeZone timeZone) {
        this.timeZone = timeZone;
    }

    float getCarSpeed(long timestamp, int segment) {
        Calendar calendar = new GregorianCalendar(timeZone);
        calendar.setTimeInMillis(timestamp);

        int minute = calendar.get(Calendar.MINUTE);
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int day = convertDayOfWeek(calendar.get(Calendar.DAY_OF_WEEK));

        return getCarSpeed(timestamp, segment, day, hour, minute);
    }

    private int convertDayOfWeek(int calendarDayOfWeek) {
        switch (calendarDayOfWeek) {
            case Calendar.MONDAY:
                return 0;
            case Calendar.TUESDAY:
                return 1;
            case Calendar.WEDNESDAY:
                return 2;
            case Calendar.THURSDAY:
                return 3;
            case Calendar.FRIDAY:
                return 4;
            case Calendar.SATURDAY:
                return 5;
            case Calendar.SUNDAY:
                return 6;
        }

        return -1; // This will yield NaN down the line, which is okay as it is consistent behavior.
    }
}
