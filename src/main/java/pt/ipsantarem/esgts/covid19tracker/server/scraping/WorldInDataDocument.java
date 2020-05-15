package pt.ipsantarem.esgts.covid19tracker.server.scraping;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class WorldInDataDocument {
    private static final Logger LOGGER = LoggerFactory.getLogger(WorldInDataDocument.class);

    private static Document document;

    public static Document getInstance() {
        if (document == null) {
            createInstance();
        }

        return document;
    }

    private static void createInstance() {
        Document doc;

        try {
            doc = Jsoup.connect("https://ourworldindata.org/coronavirus-source-data").get();
        } catch (IOException ioe) {
            LOGGER.error("Could not access the World in Data page, virus stats will not be available until a connection" +
                    "is successfully established!", ioe);
            doc = null;
        }

        document = doc;
    }
}
