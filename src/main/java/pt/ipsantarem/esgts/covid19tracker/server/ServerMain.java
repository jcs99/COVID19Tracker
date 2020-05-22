package pt.ipsantarem.esgts.covid19tracker.server;

import io.javalin.Javalin;
import io.javalin.plugin.openapi.OpenApiOptions;
import io.javalin.plugin.openapi.OpenApiPlugin;
import io.javalin.plugin.openapi.ui.SwaggerOptions;
import io.javalin.websocket.WsContext;
import io.swagger.v3.oas.models.info.Info;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pt.ipsantarem.esgts.covid19tracker.server.listeners.UpdateAvailableListener;
import pt.ipsantarem.esgts.covid19tracker.server.scraping.COVID19StatsPageDocumentUpdateHandler;
import pt.ipsantarem.esgts.covid19tracker.server.scraping.pages.COVID19StatsPage;
import pt.ipsantarem.esgts.covid19tracker.server.scraping.pages.WorldInDataPage;
import pt.ipsantarem.esgts.covid19tracker.server.trees.AVLVirusStatsTree;
import pt.ipsantarem.esgts.covid19tracker.server.trees.AVLVirusStatsTreesManager;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import static io.javalin.apibuilder.ApiBuilder.get;
import static io.javalin.apibuilder.ApiBuilder.path;
import static pt.ipsantarem.esgts.covid19tracker.server.utils.ObjectPersistenceUtils.*;

/**
 * The main class where the server runs.
 */
public class ServerMain implements UpdateAvailableListener {
    private static final Logger LOGGER = LoggerFactory.getLogger(ServerMain.class);

    // a scheduled executor service, for scheduling update checks
    private ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();

    // the covid 19 stats page instance to use
    private COVID19StatsPage page = new WorldInDataPage();

    // the websocket context.
    private WsContext wsContext;

    // the number of users connected to the websocket endpoint.
    private int users = 0;

    private void init() {
        Javalin app = Javalin.create(config -> {
            config.defaultContentType = "application/json";
            config.contextPath = "/api";
            config.registerPlugin(new OpenApiPlugin(getOpenApiOptions()));
        }).start(7000);

        // --------------------------------------- API ENDPOINTS --------------------------------------- //

        app.routes(() ->
                path("/:country", () -> {
                            get("/cases/new", COVID19StatsController::getNewCases);
                            get("/cases/new/predict", COVID19StatsController::getNewCasesPredict);
                            get("/cases/new/:date", COVID19StatsController::getNewCasesInDate);
                            get("/cases/total", COVID19StatsController::getTotalCases);
                            get("/cases/total/predict", COVID19StatsController::getTotalCasesPredict);
                            get("/cases/total/:date", COVID19StatsController::getTotalCasesInDate);
                            get("/deaths/new", COVID19StatsController::getNewDeaths);
                            get("/deaths/new/predict", COVID19StatsController::getNewDeathsPredict);
                            get("/deaths/new/:date", COVID19StatsController::getNewDeathsInDate);
                            get("/deaths/total", COVID19StatsController::getTotalDeaths);
                            get("/deaths/total/predict", COVID19StatsController::getTotalDeathsPredict);
                            get("/deaths/total/:date", COVID19StatsController::getTotalDeathsInDate);
                        }
                ));

        // --------------------------------------- API ENDPOINTS --------------------------------------- //

        // ------------------------------ WEBSOCKET CLIENT FOR LIVE UPDATES ------------------------------ //

        app.ws("/recordsUpdate", ws -> {
            ws.onConnect(ctx -> {
                wsContext = ctx;
                users++;
            });
            ws.onClose(ctx -> {
                users--;
                if (users == 0) {
                    wsContext = null;
                }
            });
        });

        // ------------------------------ WEBSOCKET CLIENT FOR LIVE UPDATES ------------------------------ //
    }

    /**
     * This function gets called when the {@link COVID19StatsPageDocumentUpdateHandler} finds a new update.
     */
    @Override
    public void onUpdateAvailable(Map<String, List<AVLVirusStatsTree<?, ?>>> records) {
        // update the records file.
        updateRecordsFile(records);

        // notify the websocket clients that records are available, if any are connected.
        if (wsContext != null) {
            wsContext.send(records);
        }

        // terminate the scheduled constant update check on the executor service, since its already been updated
        executorService.shutdownNow();
    }

    /**
     * Some pre initialization operations to be ran when the server starts.
     */
    private void preinit() throws ExecutionException, InterruptedException {
        // check if there are updates first
        Map<String, List<AVLVirusStatsTree<?, ?>>> records =
                executorService.submit(new COVID19StatsPageDocumentUpdateHandler(page)).get();

        // update flag
        boolean updates = false;

        // if there are remote updates, then use them and write them to the records map file. if not, then use the locally
        // stored file as the current records.
        if (!records.isEmpty()) {
            updates = true;
            writeRecordsMap(records);
        } else {
            LOGGER.info("Using the saved COVID-19 records map!");
            records = readRecordsMap();
        }

        // create the tree and set it to inorder the results before we get any records from it.
        COVID19StatsController.treeManager = new AVLVirusStatsTreesManager(records);
        COVID19StatsController.treeManager.setInordered(true);

        // then, finally initialize the server.
        init();

        // if the page has an usual update timeframe that is explicit on the webpage, then check if there were no updates
        // but we are within an update timeframe and the last download day is lesser than the current day.
        // if the conditions are true, then schedule a background thread constantly checking for changes.
        if (page.getUsualUpdateTimeframe().length > 0) {
            LocalTime currentTime = LocalTime.now();
            LocalDate lastDownloadDate = readLastDownloadDate();
            if (!updates && lastDownloadDate.getDayOfMonth() < LocalDate.now().getDayOfMonth()
                    && (currentTime.getHour() >= page.getUsualUpdateTimeframe()[0]
                    && currentTime.getHour() < page.getUsualUpdateTimeframe()[1])) {
                scheduleConstantChecks();
            }
        }
    }

    /**
     * Function that gets invoked by the preinit() function if there are yet no updates to the page, yet the current hours
     * are the ones where the page usually gets updated.
     */
    private void scheduleConstantChecks() {
        LOGGER.info("Scheduling constant checks for updates!");
        try {
            executorService.scheduleAtFixedRate(() -> {
                LOGGER.info("Running a update check!");
                COVID19StatsPageDocumentUpdateHandler updateHandler = new COVID19StatsPageDocumentUpdateHandler(page, this);
                updateHandler.call();
            }, 5, 5, TimeUnit.MINUTES).get();
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
        }
    }

    // update the records file and the TreesManager if new updates are found.
    private void updateRecordsFile(Map<String, List<AVLVirusStatsTree<?, ?>>> records) {
        writeRecordsMap(records);
        COVID19StatsController.treeManager = new AVLVirusStatsTreesManager(records);
        COVID19StatsController.treeManager.setInordered(true);
    }

    // defines the OpenAPI settings.
    private OpenApiOptions getOpenApiOptions() {
        Info applicationInfo = new Info()
                .version("1.0-SNAPSHOT")
                .description("COVID 19 tracker");

        return new OpenApiOptions(applicationInfo)
                .path("/swagger-json")
                .activateAnnotationScanningFor("pt.ipsantarem.esgts.covid19tracker.server")
                .swagger(new SwaggerOptions("/swagger")
                        .title("COVID-19 stats API documentation"));
    }

    public static void main(String[] args) throws ExecutionException, InterruptedException {
        new ServerMain().preinit();
    }
}
