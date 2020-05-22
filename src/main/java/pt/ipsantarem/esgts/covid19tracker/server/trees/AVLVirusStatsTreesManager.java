package pt.ipsantarem.esgts.covid19tracker.server.trees;

import pt.ipsantarem.esgts.covid19tracker.server.models.VirusStatistic;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Manages a list of self balanced trees mapped by individual countries.
 *
 * @author José Simões
 */
@SuppressWarnings("unchecked")
public class AVLVirusStatsTreesManager {

    /**
     * The indexes of the lists with certain virus stats.
     */
    private static final int NEW_CASES_TREE_IDX = 0;
    private static final int TOTAL_CASES_TREE_IDX = 1;
    private static final int NEW_DEATHS_TREE_IDX = 2;
    private static final int TOTAL_DEATHS_TREE_IDX = 3;

    private Map<String, List<AVLVirusStatsTree<?, ?>>> records; // the list of trees mapped by a individual country.
    private boolean shouldBeInordered = false; // should we inorder the contents before returning them?

    public AVLVirusStatsTreesManager(Map<String, List<AVLVirusStatsTree<?, ?>>> records) {
        this.records = records;
    }

    /**
     * Set if we should inorder the results before returning them. If not, we return them preordered.
     *
     * @param inordered Inorder before returning the results.
     */
    public void setInordered(boolean inordered) {
        shouldBeInordered = inordered;
    }

    // --------------------------------------- TREE OPERATIONS --------------------------------------- //

    public VirusStatistic<Integer> getNewCasesInDate(String country, long date) {
        return (VirusStatistic<Integer>) getVirusTree(country, NEW_CASES_TREE_IDX).get(new Date(date));
    }

    public VirusStatistic<Integer> getTotalCasesInDate(String country, long date) {
        return (VirusStatistic<Integer>) getVirusTree(country, TOTAL_CASES_TREE_IDX).get(new Date(date));
    }

    public VirusStatistic<Integer> getNewDeathsInDate(String country, long date) {
        return (VirusStatistic<Integer>) getVirusTree(country, NEW_DEATHS_TREE_IDX).get(new Date(date));
    }

    public VirusStatistic<Integer> getTotalDeathsInDate(String country, long date) {
        return (VirusStatistic<Integer>) getVirusTree(country, TOTAL_DEATHS_TREE_IDX).get(new Date(date));
    }

    public List<VirusStatistic<Integer>> getNewCasesStats(String country) {
        return (List<VirusStatistic<Integer>>) getVirusStats(country, NEW_CASES_TREE_IDX);
    }

    public List<VirusStatistic<Integer>> getTotalCasesStats(String country) {
        return (List<VirusStatistic<Integer>>) getVirusStats(country, TOTAL_CASES_TREE_IDX);
    }

    public List<VirusStatistic<Integer>> getNewDeathsStats(String country) {
        return (List<VirusStatistic<Integer>>) getVirusStats(country, NEW_DEATHS_TREE_IDX);
    }

    public List<VirusStatistic<Integer>> getTotalDeathsStats(String country) {
        return (List<VirusStatistic<Integer>>) getVirusStats(country, TOTAL_DEATHS_TREE_IDX);
    }

    public List<VirusStatistic<Integer>> getNewCasesStatsBetweenDates(String country, long firstDate, long secondDate) {
        return (List<VirusStatistic<Integer>>) getVirusStatsBetweenDates(country, NEW_CASES_TREE_IDX, firstDate, secondDate);
    }

    public List<VirusStatistic<Integer>> getTotalCasesStatsBetweenDates(String country, long firstDate, long secondDate) {
        return (List<VirusStatistic<Integer>>) getVirusStatsBetweenDates(country, TOTAL_CASES_TREE_IDX, firstDate, secondDate);
    }

    public List<VirusStatistic<Integer>> getNewDeathsStatsBetweenDates(String country, long firstDate, long secondDate) {
        return (List<VirusStatistic<Integer>>) getVirusStatsBetweenDates(country, NEW_DEATHS_TREE_IDX, firstDate, secondDate);
    }

    public List<VirusStatistic<Integer>> getTotalDeathsStatsBetweenDates(String country, long firstDate, long secondDate) {
        return (List<VirusStatistic<Integer>>) getVirusStatsBetweenDates(country, TOTAL_DEATHS_TREE_IDX, firstDate, secondDate);
    }

    // --------------------------------------- TREE OPERATIONS --------------------------------------- //

    /**
     * Get a list of virus stats by a tree index (the information of that tree summarized in a {@link VirusStatistic}
     * object).
     *
     * @param treeIdx The index of the tree we want to get a list of virus stats for.
     * @return The list of virus stats.
     */
    private List<? extends VirusStatistic<?>> getVirusStats(String country, int treeIdx) {
        AVLVirusStatsTree<?, ?> tree = getVirusTree(country, treeIdx);
        if (!shouldBeInordered) return tree.preorder();
        return tree.inorder();
    }

    /**
     * Get a list of virus stats between two dates by a tree index.
     *
     * @param treeIdx    The index of the tree we want to get a list of virus stats for.
     * @param firstDate  The first date in the interval.
     * @param secondDate The second date in the interval.
     * @return The list of virus stats.
     */
    private List<? extends VirusStatistic<?>> getVirusStatsBetweenDates(String country,
                                                                        int treeIdx, long firstDate, long secondDate) {
        return getVirusTree(country, treeIdx).getBetweenDates(new Date(firstDate), new Date(secondDate));
    }

    /**
     * Get a certain type of tree by a tree index.
     *
     * @param treeIdx The index of the type of the tree.
     * @return The tree itself.
     * @throws IllegalStateException If no country is currently selected.
     */
    private AVLVirusStatsTree<?, ?> getVirusTree(String country, int treeIdx) {
        return records.get(country.toLowerCase()).get(treeIdx);
    }
}
