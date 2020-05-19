package pt.ipsantarem.esgts.covid19tracker.server;

import pt.ipsantarem.esgts.covid19tracker.server.models.nodes.*;
import pt.ipsantarem.esgts.covid19tracker.server.trees.AVLVirusStatsTree;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.text.ParseException;
import java.util.*;

import static pt.ipsantarem.esgts.covid19tracker.server.utils.DateUtils.parseStringToDate;

/**
 * Class that parses the CSVs.
 */
public class CSVParser implements Parser<String> {

    /**
     * CSV constants. These are the index of the records from the COVID-19 stats CSV file we downloaded from the
     * World in Data website.
     */
    private static final int LOCATION_IDX = 1;
    private static final int DATE_IDX = 2;
    private static final int TOTAL_CASES_IDX = 3;
    private static final int NEW_CASES_IDX = 4;
    private static final int TOTAL_DEATHS_IDX = 5;
    private static final int NEW_DEATHS_IDX = 6;

    /**
     * Converts a CSV string into a map of virus stat trees
     *
     * @param csv The CSV
     * @return The map with the trees mapped to their respective countries
     */
    @SuppressWarnings("ConstantConditions")
    public Map<String, List<AVLVirusStatsTree<?, ?>>> parse(String csv) {
        // define the csv limiter
        String cvsSplitBy = ",";

        // current line that the bufferedreader is reading
        String line;

        // list of trees mapped by each individual country
        Map<String, List<AVLVirusStatsTree<?, ?>>> treesByCountry = new LinkedHashMap<>();

        // create a new reader to read the csv
        try (BufferedReader br = new BufferedReader(new StringReader(csv))) {
            // a flag to indicate if its the first loop or not
            boolean firstLoop = true;

            // the location the csv is currently iterating on
            String currentLocation = "";

            // initialize the trees
            AVLVirusStatsTree<Integer, NewCasesNode> newCasesTree = null;
            AVLVirusStatsTree<Integer, TotalCasesNode> totalCasesTree = null;
            AVLVirusStatsTree<Integer, NewDeathsNode> newDeathsTree = null;
            AVLVirusStatsTree<Integer, TotalDeathsNode> totalDeathsTree = null;

            // start reading the csv
            while ((line = br.readLine()) != null) {
                // skip the csv header
                if (firstLoop) {
                    firstLoop = false;
                    continue;
                }

                // split the record, delimited by a comma, into a string array
                String[] record = line.split(cvsSplitBy);

                String locationLowerCase = record[LOCATION_IDX].toLowerCase();

                // create the nodes
                NewCasesNode newCasesNode = createNewNode(NewCasesNode.class,
                        record[DATE_IDX], record[LOCATION_IDX], record[NEW_CASES_IDX]);
                TotalCasesNode totalCasesNode = createNewNode(TotalCasesNode.class,
                        record[DATE_IDX], record[LOCATION_IDX], record[TOTAL_CASES_IDX]);
                NewDeathsNode newDeathsNode = createNewNode(NewDeathsNode.class,
                        record[DATE_IDX], record[LOCATION_IDX], record[NEW_DEATHS_IDX]);
                TotalDeathsNode totalDeathsNode = createNewNode(TotalDeathsNode.class,
                        record[DATE_IDX], record[LOCATION_IDX], record[TOTAL_DEATHS_IDX]);

                // if the location record read in the csv is different than the current location,
                // then that means we are reading the records of the next country in the csv. save the trees of the
                // other country in the map, reassign the variables and start adding again from scratch.
                if (!locationLowerCase.equals(currentLocation)) {
                    if (!currentLocation.isEmpty()) {
                        treesByCountry.put(currentLocation,
                                Arrays.asList(newCasesTree, totalCasesTree, newDeathsTree, totalDeathsTree));
                    }

                    currentLocation = locationLowerCase;

                    newCasesTree = new AVLVirusStatsTree<>(newCasesNode);
                    totalCasesTree = new AVLVirusStatsTree<>(totalCasesNode);
                    newDeathsTree = new AVLVirusStatsTree<>(newDeathsNode);
                    totalDeathsTree = new AVLVirusStatsTree<>(totalDeathsNode);

                    continue;
                }

                // add the nodes into their respective trees
                newCasesTree.add(newCasesNode);
                totalCasesTree.add(totalCasesNode);
                newDeathsTree.add(newDeathsNode);
                totalDeathsTree.add(totalDeathsNode);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return treesByCountry;
    }

    /**
     * Small utility function to create a new node
     *
     * @param nodeClazz The {@link java.lang.Class} of the argument
     * @param date      The date
     * @param location  The location
     * @param stat      The stat
     * @param <T>       A node that extends from {@link VirusStatsNode}
     * @return A new node
     */
    private <T extends VirusStatsNode<?>> T createNewNode(Class<T> nodeClazz, String date,
                                                          String location, String stat) {
        try {
            // get the constructor of the node reflectively, using the Class types from the passed arguments
            Constructor<T> nodeConstructor = nodeClazz.getConstructor(Date.class, String.class, int.class);

            // using the obtained constructor, create the node
            return nodeConstructor.newInstance((parseStringToDate("yyyy-MM-dd", date)), location, Integer.parseInt(stat));
        } catch (ParseException | NoSuchMethodException | InstantiationException | IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }
}
