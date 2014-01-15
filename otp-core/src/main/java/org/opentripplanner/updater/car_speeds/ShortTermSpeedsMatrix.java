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
import static org.opentripplanner.updater.car_speeds.CarSpeeds.TIME_SLOT;

import java.util.Date;
import java.util.TimeZone;

import org.opentripplanner.util.DateUtils;

class ShortTermSpeedsMatrix extends SpeedsMatrix {
    private final long startTime;
    private final float carSpeeds[][];

    ShortTermSpeedsMatrix(ShortTermTimeSlotSpeedsArray[] shortTermTimeSlotSpeedsArrays) {
        final String dateString = shortTermTimeSlotSpeedsArrays[0].yearUtc + '.' +
                shortTermTimeSlotSpeedsArrays[0].monthUtc + '.' +
                shortTermTimeSlotSpeedsArrays[0].monthdayUtc;
        final String timeString = shortTermTimeSlotSpeedsArrays[0].hourUtc + ':' +
                shortTermTimeSlotSpeedsArrays[0].minuteUtc;

        final Date date = DateUtils.toDate(dateString, timeString, TimeZone.getTimeZone("UTC"));
        if (date == null) {
            throw new RuntimeException("A date/time specification could not be parsed correctly.");
        }

        startTime = date.getTime();
        carSpeeds = new float[shortTermTimeSlotSpeedsArrays.length]
                [shortTermTimeSlotSpeedsArrays[0].speedsKmh.length];

        for (int i = 0; i < carSpeeds.length; i++) {
            for (int j = 0; j < carSpeeds[i].length; j++) {
                carSpeeds[i][j] =
                        Float.parseFloat(shortTermTimeSlotSpeedsArrays[i].speedsKmh[j]) * KM_H;
            }
        }
    }

    float getCarSpeed(long timestamp, int segment, int day, int hour, int minute) {
        final int index = floorDiv((timestamp - startTime), TIME_SLOT);
        if (index < 0 || index >= carSpeeds.length) return Float.NaN;
        if (segment < 1 || segment > carSpeeds[index].length) return Float.NaN;
        return carSpeeds[index][segment - 1];
    }

    /**
     * Divide two integers, rounding towards negative infinity. A similar method will show up in JDK
     * 1.8, but in the meantime we will (unfortunately) just have to use this special case solution.
     * @param dividend The dividend, which can be any long.
     * @param divisor The divisor, which must be positive at all times.
     * @return The quotient converted to an int. Preventing overflow is the caller's responsibility.
     */
    private static int floorDiv(long dividend, long divisor) {
        if (divisor < 1) throw new ArithmeticException("All floorDiv() calls require divisor > 0.");

        if (dividend < 0) {
            dividend -= divisor - 1;
        }

        return (int) (dividend / divisor);
    }
}
