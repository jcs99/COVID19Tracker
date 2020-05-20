package pt.ipsantarem.esgts.covid19tracker.server.parsers;

import pt.ipsantarem.esgts.covid19tracker.server.nodes.*;
import pt.ipsantarem.esgts.covid19tracker.server.trees.AVLVirusStatsTree;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.util.*;

import static pt.ipsantarem.esgts.covid19tracker.server.utils.DateUtils.parseStringToDate;

/**
 * Class that parses the CSVs.
 */
public class WorldInDataCSVParser implements CSVParser {

    @Override
    public String getDelimiter() {
        return ",";
    }

    @Override
    public int getLocationIndex() {
        return 1;
    }

    @Override
    public int getDateIndex() {
        return 2;
    }

    @Override
    public int getNewCasesIndex() {
        return 4;
    }

    @Override
    public int getTotalCasesIndex() {
        return 3;
    }

    @Override
    public int getNewDeathsIndex() {
        return 6;
    }

    @Override
    public int getTotalDeathsIndex() {
        return 5;
    }

    /**
     * Converts a CSV string into a map of virus stat trees
     *
     * @param csv The CSV
     * @return The map with the trees mapped to their respective countries
     */
    @Override
    @SuppressWarnings("ConstantConditions")
    public Map<String, List<AVLVirusStatsTree<?, ?>>> parse(byte[] csv) {
        // current line that the bufferedreader is reading
        String line;

        // list of trees mapped by each individual country
        Map<String, List<AVLVirusStatsTree<?, ?>>> treesByCountry = new LinkedHashMap<>();

        // create a new reader to read the csv
        try (BufferedReader br = new BufferedReader(new StringReader(new String(csv, StandardCharsets.UTF_8)))) {
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
                String[] record = line.split(getDelimiter());

                String locationLowerCase = record[getLocationIndex()].toLowerCase();

                // create the nodes
                NewCasesNode newCasesNode = createNewNode(NewCasesNode.class,
                        record[getDateIndex()], record[getLocationIndex()], record[getNewCasesIndex()]);
                TotalCasesNode totalCasesNode = createNewNode(TotalCasesNode.class,
                        record[getDateIndex()], record[getLocationIndex()], record[getTotalCasesIndex()]);
                NewDeathsNode newDeathsNode = createNewNode(NewDeathsNode.class,
                        record[getDateIndex()], record[getLocationIndex()], record[getNewDeathsIndex()]);
                TotalDeathsNode totalDeathsNode = createNewNode(TotalDeathsNode.class,
                        record[getDateIndex()], record[getLocationIndex()], record[getTotalDeathsIndex()]);

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
