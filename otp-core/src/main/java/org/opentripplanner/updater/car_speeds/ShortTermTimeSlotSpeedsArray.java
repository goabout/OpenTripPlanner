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

final class ShortTermTimeSlotSpeedsArray {
    private static final Logger LOG = LoggerFactory.getLogger(ShortTermTimeSlotSpeedsArray.class);

    final String measuredPredicted;
    final String yearUtc;
    final String monthUtc;
    final String monthdayUtc;
    final String hourUtc;
    final String minuteUtc;
    final String speedsKmh[];

    private ShortTermTimeSlotSpeedsArray(CsvReader csvReader) throws IOException {
        final int columnCount = csvReader.getColumnCount();
        if (columnCount < 7) {
            throw new IOException("Time slot speeds file doesn't contain any segments.");
        }

        measuredPredicted = csvReader.get(0);
        yearUtc = csvReader.get(1);
        monthUtc = csvReader.get(2);
        monthdayUtc = csvReader.get(3);
        hourUtc = csvReader.get(4);
        minuteUtc = csvReader.get(5);
        speedsKmh = new String[columnCount - 6];

        for (int i = 6; i < columnCount; i++) {
            speedsKmh[i - 6] = csvReader.get(i);
        }
    }

    static ShortTermTimeSlotSpeedsArray[] readShortTermTimeSlotSpeedsArrays(Reader reader) {
        ShortTermTimeSlotSpeedsArray[] shortTermTimeSlotSpeedsArrays;
        CsvReader csvReader = new CsvReader(reader);

        try {
            ArrayList<ShortTermTimeSlotSpeedsArray> list =
                    new ArrayList<ShortTermTimeSlotSpeedsArray>();

            while (csvReader.readRecord()) {
                list.add(new ShortTermTimeSlotSpeedsArray(csvReader));
            }

            if (list.size() < 1) {
                LOG.error("Time slot speeds file doesn't contain any time slots.");
                return null;
            }

            shortTermTimeSlotSpeedsArrays =
                    list.toArray(new ShortTermTimeSlotSpeedsArray[list.size()]);
        } catch (IOException e) {
            LOG.error("IO error while reading time slot speed arrays.", e);
            return null;
        } finally {
            csvReader.close();
        }

        int segmentCount = shortTermTimeSlotSpeedsArrays[0].speedsKmh.length;
        for (ShortTermTimeSlotSpeedsArray shortTermTimeSlotSpeedsArray :
            shortTermTimeSlotSpeedsArrays) {
            if (shortTermTimeSlotSpeedsArray.speedsKmh.length != segmentCount) {
                LOG.error("Time slot speeds file contains an inconsistent number of segments.");
                return null;
            }
        }

        return shortTermTimeSlotSpeedsArrays;
    }
}
