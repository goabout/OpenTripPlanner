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

import java.io.Reader;
import java.util.TimeZone;

public class CarSpeeds {
    private static final float KM = 1000F;
    private static final float H = 3600F;
    static final float KM_H = KM / H;

    private static final int MILLISECONDS_PER_SECOND = 1000;
    private static final int SECONDS_PER_MINUTE = 60;
    static final int MINUTES_PER_HOUR = 60;
    static final int HOURS_PER_DAY = 24;
    private static final int DAYS_PER_WEEK = 7;

    static final int SLOT = 5;

    static final int TIME_SLOTS_PER_WEEK = DAYS_PER_WEEK * HOURS_PER_DAY * MINUTES_PER_HOUR / SLOT;

    public static final long TIME_SLOT = SLOT * SECONDS_PER_MINUTE * MILLISECONDS_PER_SECOND;

    private final HolidayData holidayData;
    private final HolidayRegionMappings holidayRegionMappings;
    private final SpeedsMatrix freeFlowSpeedsMatrix;
    private final SpeedsMatrix normalSpeedsMatrix;
    private final SpeedsMatrix holidaySpeedsMatrix;
    private final SpeedsMatrix shortTermSpeedsMatrix;

    CarSpeeds(Reader holidayData, Reader holidayRegionMappings, Reader freeFlowSpeedsMatrix,
            Reader normalSpeedsMatrix, Reader holidaySpeedsMatrix, Reader shortTermSpeedsMatrix,
            TimeZone timeZone) {
        try {
            this.holidayData = new HolidayData(Holiday
                    .readHolidays(holidayData));
            this.holidayRegionMappings = new HolidayRegionMappings(Segment
                    .readSegments(holidayRegionMappings));
            this.freeFlowSpeedsMatrix = new FreeFlowSpeeds(Segment
                    .readSegments(freeFlowSpeedsMatrix));
            this.normalSpeedsMatrix = new WeekSpeedsMatrix(WeekTimeSlotSpeedsArray
                    .readWeekTimeSlotSpeedsArrays(normalSpeedsMatrix), timeZone);
            this.holidaySpeedsMatrix = new WeekSpeedsMatrix(WeekTimeSlotSpeedsArray
                    .readWeekTimeSlotSpeedsArrays(holidaySpeedsMatrix), timeZone);
            this.shortTermSpeedsMatrix = new ShortTermSpeedsMatrix(ShortTermTimeSlotSpeedsArray
                    .readShortTermTimeSlotSpeedsArrays(shortTermSpeedsMatrix));
        } finally {
            // We try to close all readers. If this should fail, there's nothing we can do about it.
            try {
                shortTermSpeedsMatrix.close();
            } catch (Exception e) {}
            try {
                holidaySpeedsMatrix.close();
            } catch (Exception e) {}
            try {
                normalSpeedsMatrix.close();
            } catch (Exception e) {}
            try {
                freeFlowSpeedsMatrix.close();
            } catch (Exception e) {}
            try {
                holidayRegionMappings.close();
            } catch (Exception e) {}
            try {
                holidayData.close();
            } catch (Exception e) {}
        }
    }

    public float getCarSpeed(long timestamp, int segment, int type) {
        float speed = type > 2 ? shortTermSpeedsMatrix.getCarSpeed(timestamp, segment) : Float.NaN;

        if (Float.isNaN(speed) || speed == -1) {        // -1 means missing data
            Region region = type > 1 ? holidayRegionMappings.getRegion(segment) : null;
            if (region != null) {
                speed = holidayData.isHoliday(region, timestamp) ?
                        holidaySpeedsMatrix.getCarSpeed(timestamp, segment) :
                        normalSpeedsMatrix.getCarSpeed(timestamp, segment);
            }
            if (Float.isNaN(speed) || speed == -1) {    // -1 means missing data
                speed = type > 0 ? freeFlowSpeedsMatrix.getCarSpeed(timestamp, segment) : Float.NaN;
            }
        }

        return speed;
    }
}
