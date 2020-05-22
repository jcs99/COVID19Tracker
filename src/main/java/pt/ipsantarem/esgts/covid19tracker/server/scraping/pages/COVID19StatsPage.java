package pt.ipsantarem.esgts.covid19tracker.server.scraping.pages;

import org.jsoup.nodes.Document;
import pt.ipsantarem.esgts.covid19tracker.server.parsers.Parser;
import pt.ipsantarem.esgts.covid19tracker.server.trees.AVLVirusStatsTree;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import static pt.ipsantarem.esgts.covid19tracker.server.utils.InputStreamUtils.readAllBytes;

/**
 * Represents a webpage that has downloadable analyzable coronavirus statistics.
 */
public interface COVID19StatsPage {

    /**
     * @return The page instance itself.
     */
    Document getPageInstance();

    /**
     * @return The URL of this page.
     */
    URL getPageURL();

    /**
     * @return The download URL.
     */
    URL getDownloadURL();

    /**
     * @return The last time the stats were updated on the page.
     */
    LocalDate getLastTimeUpdated();

    /**
     * @return An array with the time interval where the page gets updated (for example, it should return an array
     * with 10 and 13 if the page gets updated between 10 and 13 AM).
     */
    int[] getUsualUpdateTimeframe();

    /**
     * @return The parser to be used when parsing the downloaded COVID 19 stats.
     */
    Parser getParser();

    /**
     * @return The COVID 19 stats in a byte array form.
     */
    default byte[] downloadCovid19Stats() throws IOException {
        // open a url connection to the stats file
        HttpURLConnection conn = (HttpURLConnection) getDownloadURL().openConnection();

        // get the input stream of the url connection
        try (InputStream data = new BufferedInputStream(conn.getInputStream())) {
            return readAllBytes(data);
        }
    }

    /**
     * @return The downloaded and parsed COVID 19 stats.
     * @throws IOException If there was a IO problem downloading and parsing the stats
     */
    default Map<String, List<AVLVirusStatsTree<?, ?>>> downloadAndParseCovid19Stats() throws IOException {
        return getParser().parse(downloadCovid19Stats());
    }
}
