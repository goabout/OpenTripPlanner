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

import java.util.ArrayList;

class HolidayData {
    private final HolidayRegion midden;
    private final HolidayRegion noord;
    private final HolidayRegion zuid;

    HolidayData(Holiday[] holidays) {
        ArrayList<Holiday> middenList = new ArrayList<Holiday>(holidays.length);
        ArrayList<Holiday> noordList = new ArrayList<Holiday>(holidays.length);
        ArrayList<Holiday> zuidList = new ArrayList<Holiday>(holidays.length);

        for (Holiday holiday : holidays) {
            if (isMidden(holiday.holidayRegion)) middenList.add(holiday);
            if (isNoord(holiday.holidayRegion)) noordList.add(holiday);
            if (isZuid(holiday.holidayRegion)) zuidList.add(holiday);
        }

        midden = new HolidayRegion(middenList.toArray(new Holiday[middenList.size()]));
        noord = new HolidayRegion(noordList.toArray(new Holiday[noordList.size()]));
        zuid = new HolidayRegion(zuidList.toArray(new Holiday[zuidList.size()]));
    }

    boolean isHoliday(Region region, long timestamp) {
        if (region != null) switch (region) {
            case MIDDEN:
                return midden.isHoliday(timestamp);
            case NOORD:
                return noord.isHoliday(timestamp);
            case ZUID:
                return zuid.isHoliday(timestamp);
        }
        return false;
    }

    private static final boolean isMidden(String string) {
        return "All".equals(string) || "Midden".equals(string);
    }

    private static final boolean isNoord(String string) {
        return "All".equals(string) || "Noord".equals(string);
    }

    private static final boolean isZuid(String string) {
        return "All".equals(string) || "Zuid".equals(string);
    }
}
