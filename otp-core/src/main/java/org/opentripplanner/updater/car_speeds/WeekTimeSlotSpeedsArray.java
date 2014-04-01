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

import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.csvreader.CsvReader;

final class WeekTimeSlotSpeedsArray {
    private static final Logger LOG = LoggerFactory.getLogger(WeekTimeSlotSpeedsArray.class);

    final String timeindexWeek;
    final String weekdayLocaltime;
    final String hourLocaltime;
    final String minuteLocaltime;
    final String speedsKmh[];

    private WeekTimeSlotSpeedsArray(CsvReader csvReader) throws IOException {
        final int columnCount = csvReader.getColumnCount();
        if (columnCount < 5) {
            throw new IOException("Time slot speeds file doesn't contain any segments.");
        }

        timeindexWeek = csvReader.get(0);
        weekdayLocaltime = csvReader.get(1);
        hourLocaltime = csvReader.get(2);
        minuteLocaltime = csvReader.get(3);
        speedsKmh = new String[columnCount - 4];

        for (int i = 4; i < columnCount; i++) {
            speedsKmh[i - 4] = csvReader.get(i);
        }
    }

    static WeekTimeSlotSpeedsArray[] readWeekTimeSlotSpeedsArrays(Reader reader) {
        WeekTimeSlotSpeedsArray[] weekTimeSlotSpeedsArrays;
        CsvReader csvReader = new CsvReader(reader);

        try {
            ArrayList<WeekTimeSlotSpeedsArray> list = new ArrayList<WeekTimeSlotSpeedsArray>(2016);

            while (csvReader.readRecord()) {
                list.add(new WeekTimeSlotSpeedsArray(csvReader));
            }

            if (list.size() < 1) {
                LOG.error("Time slot speeds file doesn't contain any time slots.");
                return null;
            }

            weekTimeSlotSpeedsArrays = list.toArray(new WeekTimeSlotSpeedsArray[list.size()]);
        } catch (IOException e) {
            LOG.error("IO error while reading time slot speed arrays.", e);
            return null;
        } finally {
            csvReader.close();
        }

        int segmentCount = weekTimeSlotSpeedsArrays[0].speedsKmh.length;
        for (WeekTimeSlotSpeedsArray weekTimeSlotSpeedsArray : weekTimeSlotSpeedsArrays) {
            if (weekTimeSlotSpeedsArray.speedsKmh.length != segmentCount) {
                LOG.error("Time slot speeds file contains an inconsistent number of segments.");
                return null;
            }
        }

        return weekTimeSlotSpeedsArrays;
    }
}
