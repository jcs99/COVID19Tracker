package pt.ipsantarem.esgts.covid19tracker.server;

import io.javalin.Javalin;
import org.mapdb.DB;
import org.mapdb.DBMaker;
import org.mapdb.Serializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pt.ipsantarem.esgts.covid19tracker.server.callbacks.UpdateAvailableListener;
import pt.ipsantarem.esgts.covid19tracker.server.exceptions.NonExistentCountryException;
import pt.ipsantarem.esgts.covid19tracker.server.models.VirusStatistic;
import pt.ipsantarem.esgts.covid19tracker.server.scraping.WorldInDataDocumentUpdateHandler;
import pt.ipsantarem.esgts.covid19tracker.server.trees.AVLVirusStatsTreesManager;

import java.time.LocalTime;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import static pt.ipsantarem.esgts.covid19tracker.server.utils.VirusPredictionUtils.newStatCasesPredict;
import static pt.ipsantarem.esgts.covid19tracker.server.utils.VirusPredictionUtils.totalStatCasesPredict;

/**
 * The main class where the server runs.
 */
public class ServerMain implements UpdateAvailableListener {
    private static final Logger LOGGER = LoggerFactory.getLogger(ServerMain.class);

    // a scheduled executor service, for scheduling update checks
    private ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();

    // a reference to the db file with the csv string.
    private DB db = DBMaker.fileDB("csv.db").fileMmapEnable().make();

    // the serialized csv record.
    private Map<String, String> storedCsv = db
            .hashMap("csv", Serializer.STRING, Serializer.STRING)
            .createOrOpen();

    // the tree manager.
    private AVLVirusStatsTreesManager treeManager;

    // the parser to be used when parsing the csv.
    private Parser<String> parser = new CSVParser();

    // is there a constant update check already scheduled.
    private boolean isConstantUpdateCheckScheduled = false;

    public void init() {
        Javalin app = Javalin.create(config -> {
            config.defaultContentType = "application/json";
            config.contextPath = "/api";
        }).start(7000);

        // --------------------------------------- API ENDPOINTS --------------------------------------- //

        app.before("/:country/*", ctx -> treeManager.setCountry(ctx.pathParam("country")));
        app.get("/:country/cases/new", ctx -> ctx.json(treeManager.getNewCasesStats()));
        app.get("/:country/cases/total", ctx -> ctx.json(treeManager.getTotalCasesStats()));
        app.get("/:country/deaths/new", ctx -> ctx.json(treeManager.getNewDeathsStats()));
        app.get("/:country/deaths/total", ctx -> ctx.json(treeManager.getTotalDeathsStats()));
        app.get("/:country/cases/new/predict", ctx -> ctx.json(newStatCasesPredict(treeManager.getTotalCasesStats())));
        app.get("/:country/cases/total/predict", ctx -> ctx.json(totalStatCasesPredict(treeManager.getTotalCasesStats())));
        app.get("/:country/deaths/new/predict", ctx -> ctx.json(newStatCasesPredict(treeManager.getTotalDeathsStats())));
        app.get("/:country/deaths/total/predict", ctx -> ctx.json(totalStatCasesPredict(treeManager.getTotalDeathsStats())));

        app.get("/:country/cases/new/:date", ctx -> {
            VirusStatistic<Integer> stat = treeManager.getNewCasesInDate(Long.parseLong(ctx.pathParam("date")));
            ctx.json(Objects.requireNonNullElse(stat, ""));
        });
        app.get("/:country/cases/total/:date", ctx -> {
            VirusStatistic<Integer> stat = treeManager.getTotalCasesInDate(Long.parseLong(ctx.pathParam("date")));
            ctx.json(Objects.requireNonNullElse(stat, ""));
        });
        app.get("/:country/deaths/new/:date", ctx -> {
            VirusStatistic<Integer> stat = treeManager.getNewDeathsInDate(Long.parseLong(ctx.pathParam("date")));
            ctx.json(Objects.requireNonNullElse(stat, ""));
        });
        app.get("/:country/deaths/total/:date", ctx -> {
            VirusStatistic<Integer> stat = treeManager.getTotalDeathsInDate(Long.parseLong(ctx.pathParam("date")));
            ctx.json(Objects.requireNonNullElse(stat, ""));
        });

        app.exception(NonExistentCountryException.class, (ex, ctx) ->
                ctx.json("The country " + ex.getMessage() + " doesn't exist. Please introduce a correct name"));

        // --------------------------------------- API ENDPOINTS --------------------------------------- //
    }

    /**
     * This function gets called when the {@link WorldInDataDocumentUpdateHandler} finds a new update.
     */
    @Override
    public void onUpdateAvailable(String data) {
        updateCsv(data);

        // terminate the scheduled constant update check on the executor service, since its already been updated
        executorService.shutdownNow();
    }

    /**
     * Function that gets invoked by the preinit() function if there are yet no updates to the page, yet the current hours
     * are the ones where the page usually gets updated.
     */
    public void scheduleConstantChecks() {
        LOGGER.info("Scheduling constant checks for updates!");
        if (!isConstantUpdateCheckScheduled) {
            isConstantUpdateCheckScheduled = true;
            try {
                executorService.schedule(new WorldInDataDocumentUpdateHandler(this), 5, TimeUnit.MINUTES).get();
            } catch (InterruptedException | ExecutionException e) {
                throw new RuntimeException(e);
            }
        }
    }

    /**
     * Some pre initialization operations to be ran when the server starts.
     */
    private void preinit() throws ExecutionException, InterruptedException {
        // check if there are updates first
        String csv = executorService.submit(new WorldInDataDocumentUpdateHandler()).get();

        // update flag
        boolean updates = false;

        // if there are csv updates, we have two options to save the updated csv now:
        if (!csv.isEmpty()) {
            updates = true;
            // if there isn't a previous serialized csv record, create it.
            if (storedCsv.get("csv") == null) {
                storedCsv.put("csv", csv);
                // if there is, replace it.
            } else {
                storedCsv.replace("csv", csv);
            }
            // if there aren't updates, get the stored serialized csv record.
        } else {
            csv = storedCsv.get("csv");
        }

        // create the tree and set it to inorder the results before we get any records from it.
        treeManager = new AVLVirusStatsTreesManager(parser.parse(csv));
        treeManager.setInordered(true);

        // add a shutdown hook to the JVM that closes the database.
        Runtime.getRuntime().addShutdownHook(new Thread(() -> db.close()));

        // then, finally initialize the server.
        init();

        // check the current time. if the time is between 10 AM and 13 AM and there are still no updates, schedule
        // constant checks, there will be one soon
        LocalTime currentTime = LocalTime.now();
        if (!updates && (currentTime.getHour() >= 10 && currentTime.getHour() <= 13)) {
            scheduleConstantChecks();
        }
    }

    // update the csv and the TreesManager if new updates are found.
    private void updateCsv(String csv) {
        storedCsv.replace("csv", csv);
        treeManager = new AVLVirusStatsTreesManager(parser.parse(csv));
        treeManager.setInordered(true);
    }

    public static void main(String[] args) throws ExecutionException, InterruptedException {
        new ServerMain().preinit();
    }
}
