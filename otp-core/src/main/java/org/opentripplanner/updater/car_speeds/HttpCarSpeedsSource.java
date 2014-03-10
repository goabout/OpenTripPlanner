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

import java.io.CharArrayReader;
import java.io.InputStream;
import java.util.TimeZone;
import java.util.prefs.Preferences;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.opentripplanner.routing.graph.Graph;
import org.opentripplanner.util.HttpUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** Reads the dynamic car speeds from an HTTP URL. */
class HttpCarSpeedsSource extends CarSpeedsSource {
    private static final Logger LOG = LoggerFactory.getLogger(HttpCarSpeedsSource.class);

    private TimeZone timeZone;
    private String url;

    @Override
    public void configure(Graph graph, Preferences preferences) throws Exception {
        timeZone = graph.getTimeZone();
        url = preferences.get("url", null);

        if (url == null) {
            throw new IllegalArgumentException("Missing mandatory 'url' parameter");
        }
    }

    @Override
    public String toString() {
        return "HttpCarSpeedsSource(" + url + ")[" + timeZone.getID() +']';
    }

    @Override
    public CarSpeeds getCarSpeeds() {
        InputStream inputStream = null;
        ZipInputStream zipInputStream = null;

        try {
            final char array[][] = new char[6][];

            inputStream = HttpUtils.getData(url);
            zipInputStream = new ZipInputStream(inputStream);

            for (int i = 0; i < array.length; i++) {
                ZipEntry zipEntry = zipInputStream.getNextEntry();
                array[i] = new char[(int) zipEntry.getSize()];
                for (int j = 0; j < array[i].length; j++) {
                    array[i][j] = (char) zipInputStream.read();
                }
            }

            return new CarSpeeds(new CharArrayReader(array[2]), new CharArrayReader(array[1]),
                    new CharArrayReader(array[0]), new CharArrayReader(array[5]),
                    new CharArrayReader(array[4]), new CharArrayReader(array[3]), timeZone);
        } catch (Exception e) {
            LOG.warn("Failed to parse dynamic car speed data:", e);
            return null;
        } finally {
            // We try to close all streams. If this should fail, there's nothing we can do about it.
            try {
                zipInputStream.close();
            } catch (Exception e) {}
            try {
                inputStream.close();
            } catch (Exception e) {}
        }
    }
}
