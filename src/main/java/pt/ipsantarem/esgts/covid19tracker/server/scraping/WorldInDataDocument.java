package pt.ipsantarem.esgts.covid19tracker.server.scraping;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

/**
 * The class that holds an instance of the World in Data webpage document.
 */
public class WorldInDataDocument {
    private static final Logger LOGGER = LoggerFactory.getLogger(WorldInDataDocument.class);

    // the document instance
    private static Document document;

    /**
     * Get an instance of the World in Data webpage document. If it's null, it tries to create one, and if it's not null,
     * it simply returns its existence.
     *
     * @return The World in Data webpage document.
     */
    public static Document getInstance() {
        if (document == null) {
            createInstance();
        }

        return document;
    }

    /**
     * Creates an instance of the World in Data webpage document. If no connection could be established to the webpage,
     * then that instance will be null.
     */
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
