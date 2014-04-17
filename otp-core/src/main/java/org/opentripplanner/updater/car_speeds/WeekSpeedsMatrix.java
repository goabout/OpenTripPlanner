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

import static org.opentripplanner.updater.car_speeds.CarSpeeds.HOURS_PER_DAY;
import static org.opentripplanner.updater.car_speeds.CarSpeeds.KM_H;
import static org.opentripplanner.updater.car_speeds.CarSpeeds.MINUTES_PER_HOUR;
import static org.opentripplanner.updater.car_speeds.CarSpeeds.SLOT;
import static org.opentripplanner.updater.car_speeds.CarSpeeds.TIME_SLOTS_PER_WEEK;

import java.util.TimeZone;

class WeekSpeedsMatrix extends SpeedsMatrix {
    private final float carSpeeds[][];

    WeekSpeedsMatrix(WeekTimeSlotSpeedsArray[] weekTimeSlotSpeedsArrays, TimeZone timeZone) {
        super(timeZone);

        if (weekTimeSlotSpeedsArrays.length != TIME_SLOTS_PER_WEEK) {
            throw new RuntimeException("A week has " + TIME_SLOTS_PER_WEEK + " time slots, but an "
                    + "array was supplied with " + weekTimeSlotSpeedsArrays.length + " elements.");
        }

        carSpeeds = new float[TIME_SLOTS_PER_WEEK][weekTimeSlotSpeedsArrays[0].speedsKmh.length];

        for (int i = 0; i < carSpeeds.length; i++) {
            for (int j = 0; j < carSpeeds[i].length; j++) {
                carSpeeds[i][j] = Float.parseFloat(weekTimeSlotSpeedsArrays[i].speedsKmh[j]) * KM_H;
            }
        }
    }

    float getCarSpeed(long timestamp, int segment, int day, int hour, int minute) {
        final int index = ((day * HOURS_PER_DAY + hour) * MINUTES_PER_HOUR + minute) / SLOT;
        if (index < 0 || index >= TIME_SLOTS_PER_WEEK) return Float.NaN;
        if (segment < 1 || segment > carSpeeds[index].length) return Float.NaN;
        return carSpeeds[index][segment - 1];
    }
}
