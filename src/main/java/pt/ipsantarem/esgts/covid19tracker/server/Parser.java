package pt.ipsantarem.esgts.covid19tracker.server;

import pt.ipsantarem.esgts.covid19tracker.server.trees.AVLVirusStatsTree;

import java.util.List;
import java.util.Map;

/**
 * Parses a data structure with virus records to a map of virus trees mapped by individual countries.
 */
public interface Parser<T> {

    /**
     * Parse the data structure.
     *
     * @param t The data structure to parse
     * @return A map of virus trees mapped by individual countries.
     */
    Map<String, List<AVLVirusStatsTree<?, ?>>> parse(T t);
}
