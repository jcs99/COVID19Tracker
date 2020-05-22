package pt.ipsantarem.esgts.covid19tracker.server.scraping;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pt.ipsantarem.esgts.covid19tracker.server.listeners.UpdateAvailableListener;
import pt.ipsantarem.esgts.covid19tracker.server.scraping.pages.COVID19StatsPage;
import pt.ipsantarem.esgts.covid19tracker.server.trees.AVLVirusStatsTree;

import java.io.IOException;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;

import static pt.ipsantarem.esgts.covid19tracker.server.utils.ObjectPersistenceUtils.readLastDownloadDate;
import static pt.ipsantarem.esgts.covid19tracker.server.utils.ObjectPersistenceUtils.writeLastDownloadDate;

/**
 * Handles the update of the a COVID 19 stat page update (in short, when a new batch of COVID-19 related data is inserted). This
 * class can be called by a ExecutorService (which submits a series of tasks handled by one or more threads), since it
 * implements the {@link Callable} interface.
 *
 * @author José Simões
 */
public class COVID19StatsPageDocumentUpdateHandler implements Callable<Map<String, List<AVLVirusStatsTree<?, ?>>>> {
    private static final Logger LOGGER = LoggerFactory.getLogger(COVID19StatsPageDocumentUpdateHandler.class);

    private COVID19StatsPage covid19StatsPage;
    private UpdateAvailableListener updateAvailableListener;

    /**
     * Initializes a new document update handler instance.
     *
     * @param covid19StatsPage The page to handle updates for.
     */
    public COVID19StatsPageDocumentUpdateHandler(COVID19StatsPage covid19StatsPage) {
        this.covid19StatsPage = covid19StatsPage;
    }

    /**
     * Initializes a new document update handler instance with an attached listener that gets notified if new updates
     * are available.
     *
     * @param covid19StatsPage        The page to handle updates for.
     * @param updateAvailableListener The update listener instance.
     */
    public COVID19StatsPageDocumentUpdateHandler(COVID19StatsPage covid19StatsPage,
                                                 UpdateAvailableListener updateAvailableListener) {
        this.covid19StatsPage = covid19StatsPage;
        this.updateAvailableListener = updateAvailableListener;
    }

    /**
     * Check for changes on the specified COVID 19 stat page and compare the date of those changes to the last time where we checked
     * for changes locally.
     *
     * @return A empty records map if the COVID 19 stat page document instance is null of if there are no updates
     * to the page since we last checked, or a records map that should contain the newly added records
     * if the page was changed since we last checked it.
     */
    @Override
    public Map<String, List<AVLVirusStatsTree<?, ?>>> call() {
        // if the document is null, just return empty data and try again later. it means that no connection could be
        // established.
        if (covid19StatsPage.getPageInstance() == null) {
            LOGGER.warn("The stats page document is null, skipping the change check!");
            return Collections.emptyMap();
        }

        // if there is a connection, we open the locally stored database file that contains the last time we downloaded
        LocalDate lastTimeDownloadedRecords = readLastDownloadDate();

        // if the last time we downloaded the updates is null, that means we never downloaded them in the first place.
        // add the current date as the time we checked for updates and proceed to download the records.
        if (lastTimeDownloadedRecords == null) {
            LOGGER.info("First run of the server, downloading the records!");
            writeLastDownloadDate(LocalDate.now());
            return downloadRecords();
        }

        // get the time where the page was last updated.
        LocalDate lastTimeUpdatedInPage = covid19StatsPage.getLastTimeUpdated();

        // if the time where the records were updated on the page is bigger than the time where we last downloaded
        // the updates, it means there are new records. update the last time we downloaded new records and then download
        // it.
        if (lastTimeUpdatedInPage.compareTo(lastTimeDownloadedRecords) > 0) {
            LOGGER.info("New updates are available, downloading them!");

            writeLastDownloadDate(LocalDate.now());
            Map<String, List<AVLVirusStatsTree<?, ?>>> records = downloadRecords();

            if (updateAvailableListener != null) {
                updateAvailableListener.onUpdateAvailable(records);
            }

            return records;
        }

        LOGGER.info("No updates found!");

        // no updates, return empty map
        return Collections.emptyMap();
    }

    /**
     * Download and parse the records
     *
     * @return A map containing the downloaded records.
     */
    private Map<String, List<AVLVirusStatsTree<?, ?>>> downloadRecords() {
        try {
            return covid19StatsPage.downloadAndParseCovid19Stats();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
