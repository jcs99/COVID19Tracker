package pt.ipsantarem.esgts.covid19tracker.server.parsers;

import pt.ipsantarem.esgts.covid19tracker.server.trees.AVLVirusStatsTree;

import java.util.List;
import java.util.Map;

/**
 * Parses a data structure with virus records to a map of virus trees mapped by individual countries.
 */
public interface Parser {

    /**
     * Parse an array of bytes to a map of virus trees mapped by individual countries.
     *
     * @param data The byte array to parse
     * @return A map of virus trees mapped by individual countries.
     */
    Map<String, List<AVLVirusStatsTree<?, ?>>> parse(byte[] data);
}
