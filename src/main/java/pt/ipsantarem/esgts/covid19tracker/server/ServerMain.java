package pt.ipsantarem.esgts.covid19tracker.server;

import pt.ipsantarem.esgts.covid19tracker.server.models.nodes.VirusStatsNode;
import pt.ipsantarem.esgts.covid19tracker.server.scraping.WorldInDataDocumentUpdateHandler;
import pt.ipsantarem.esgts.covid19tracker.server.trees.AVLVirusTree;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

public class ServerMain {
    private static ScheduledExecutorService scheduledExecutorService = Executors.newScheduledThreadPool(4);

    public static void main(String[] args) throws ExecutionException, InterruptedException {
        String csv = scheduledExecutorService.submit(new WorldInDataDocumentUpdateHandler()).get();
        if (!csv.isEmpty()) {
            Map<String, List<AVLVirusTree<Integer, ? extends VirusStatsNode<Integer>>>> records =
                    CSVParser.parseCsvIntoVirusTrees(csv);
        }
    }
}
