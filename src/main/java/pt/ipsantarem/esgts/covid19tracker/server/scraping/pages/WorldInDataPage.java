package pt.ipsantarem.esgts.covid19tracker.server.scraping.pages;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pt.ipsantarem.esgts.covid19tracker.server.parsers.Parser;
import pt.ipsantarem.esgts.covid19tracker.server.parsers.WorldInDataCSVParser;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.ParseException;
import java.time.LocalDate;

import static pt.ipsantarem.esgts.covid19tracker.server.utils.DateUtils.parseStringToLocalDate;

public class WorldInDataPage implements COVID19StatsPage {
    private static final Logger LOGGER = LoggerFactory.getLogger(WorldInDataPage.class);

    // the document instance
    private static Document document;

    /**
     * {@inheritDoc}
     */
    @Override
    public Document getPageInstance() {
        if (document == null) {
            createInstance();
        }

        return document;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public URL getPageURL() {
        URL url = null;

        try {
            url = new URL("https://ourworldindata.org/coronavirus-source-data");
        } catch (MalformedURLException ignored) { /* will never be malformed */ }

        return url;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public URL getDownloadURL() {
        URL url = null;

        try {
            url = new URL("https://covid.ourworldindata.org/data/owid-covid-data.csv");
        } catch (MalformedURLException ignored) { /* will never be malformed */ }

        return url;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public LocalDate getLastTimeUpdated() {
        String pageDate = getPageInstance().getElementsByClass("last-updated")
                .get(0)
                .child(0)
                .child(0)
                .text();

        try {
            // parse and convert the string date to a LocalDate
            return parseStringToLocalDate(pageDate);
        } catch (ParseException pe) {
            throw new RuntimeException(pe);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int[] getUsualUpdateTimeframe() {
        return new int[]{10, 13};
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Parser getParser() {
        return new WorldInDataCSVParser();
    }

    /**
     * Creates an instance of the World in Data webpage document. If no connection could be established to the webpage,
     * then that instance will be null.
     */
    private void createInstance() {
        Document doc;

        try {
            doc = Jsoup.connect(getPageURL().toString()).get();
        } catch (IOException ioe) {
            LOGGER.error("Could not access the World in Data page, virus stats will not be available until a connection" +
                    "is successfully established!", ioe);
            doc = null;
        }

        document = doc;
    }
}
