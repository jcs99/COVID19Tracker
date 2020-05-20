package pt.ipsantarem.esgts.covid19tracker.server.callbacks;

import pt.ipsantarem.esgts.covid19tracker.server.trees.AVLVirusStatsTree;

import java.util.List;
import java.util.Map;

/**
 * Interface that's used as a callback when a COVID 19 stats page receives new records.
 */
public interface UpdateAvailableListener {
    void onUpdateAvailable(Map<String, List<AVLVirusStatsTree<?, ?>>> records);
}
