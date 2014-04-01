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
import java.util.LinkedList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.csvreader.CsvReader;

final class Holiday {
    private static final Logger LOG = LoggerFactory.getLogger(Holiday.class);

    final String type;
    final String holidayRegion;
    final String comment;
    final String yearFromUtc;
    final String monthFromUtc;
    final String monthdayFromUtc;
    final String hourFromUtc;
    final String minuteFromUtc;
    final String yearToUtc;
    final String monthToUtc;
    final String monthdayToUtc;
    final String hourToUtc;
    final String minuteToUtc;

    private Holiday(CsvReader csvReader) throws IOException {
        type = csvReader.get(0);
        holidayRegion = csvReader.get(1);
        comment = csvReader.get(2);
        yearFromUtc = csvReader.get(3);
        monthFromUtc = csvReader.get(4);
        monthdayFromUtc = csvReader.get(5);
        hourFromUtc = csvReader.get(6);
        minuteFromUtc = csvReader.get(7);
        yearToUtc = csvReader.get(8);
        monthToUtc = csvReader.get(9);
        monthdayToUtc = csvReader.get(10);
        hourToUtc = csvReader.get(11);
        minuteToUtc = csvReader.get(12);
    }

    static Holiday[] readHolidays(Reader reader) {
        Holiday[] holidays;
        CsvReader csvReader = new CsvReader(reader);

        try {
            LinkedList<Holiday> list = new LinkedList<Holiday>();

            while (csvReader.readRecord()) {
                list.add(new Holiday(csvReader));
            }

            if (list.size() < 1) {
                LOG.error("Holidays file doesn't contain any holidays.");
                return null;
            }

            holidays = list.toArray(new Holiday[list.size()]);
        } catch (IOException e) {
            LOG.error("IO error while reading holidays.", e);
            return null;
        } finally {
            csvReader.close();
        }

        return holidays;
    }
}
