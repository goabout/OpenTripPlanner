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

final class Segment {
    private static final Logger LOG = LoggerFactory.getLogger(Segment.class);

    final String id;
    final String value;

    private Segment(CsvReader csvReader) throws IOException {
        id = csvReader.get(0);
        value = csvReader.get(1);
    }

    static Segment[] readSegments(Reader reader) {
        Segment[] segments;
        CsvReader csvReader = new CsvReader(reader);

        try {
            LinkedList<Segment> list = new LinkedList<Segment>();

            while (csvReader.readRecord()) {
                list.add(new Segment(csvReader));
            }

            if (list.size() < 1) {
                LOG.error("Segments file doesn't contain any segments.");
                return null;
            }

            segments = list.toArray(new Segment[list.size()]);
        } catch (IOException e) {
            LOG.error("IO error while reading segments.", e);
            return null;
        } finally {
            csvReader.close();
        }

        return segments;
    }
}
