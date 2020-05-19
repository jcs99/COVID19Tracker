package pt.ipsantarem.esgts.covid19tracker.server.trees;

import pt.ipsantarem.esgts.covid19tracker.server.exceptions.NonExistentCountryException;
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
    private String selectedCountry; // currently selected country
    private boolean shouldBeInordered = false; // should we inorder the contents before returning them?

    public AVLVirusStatsTreesManager(Map<String, List<AVLVirusStatsTree<?, ?>>> records) {
        this.records = records;
    }

    /**
     * Set the country to get virus stats for. This method can be called whenever we want to change the currently selected
     * country.
     *
     * @param country The country to select.
     * @throws NonExistentCountryException If the country doesn't exist.
     */
    public void setCountry(String country) {
        String lowerCaseCountry = country.toLowerCase();
        if (records.get(lowerCaseCountry) == null)
            throw new NonExistentCountryException(country);
        selectedCountry = lowerCaseCountry;
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

    public VirusStatistic<Integer> getNewCasesInDate(long date) {
        return (VirusStatistic<Integer>) getVirusTree(NEW_CASES_TREE_IDX).get(new Date(date));
    }

    public VirusStatistic<Integer> getTotalCasesInDate(long date) {
        return (VirusStatistic<Integer>) getVirusTree(TOTAL_CASES_TREE_IDX).get(new Date(date));
    }

    public VirusStatistic<Integer> getNewDeathsInDate(long date) {
        return (VirusStatistic<Integer>) getVirusTree(NEW_DEATHS_TREE_IDX).get(new Date(date));
    }

    public VirusStatistic<Integer> getTotalDeathsInDate(long date) {
        return (VirusStatistic<Integer>) getVirusTree(TOTAL_DEATHS_TREE_IDX).get(new Date(date));
    }

    public List<VirusStatistic<Integer>> getNewCasesStats() {
        return (List<VirusStatistic<Integer>>) getVirusStats(NEW_CASES_TREE_IDX);
    }

    public List<VirusStatistic<Integer>> getTotalCasesStats() {
        return (List<VirusStatistic<Integer>>) getVirusStats(TOTAL_CASES_TREE_IDX);
    }

    public List<VirusStatistic<Integer>> getNewDeathsStats() {
        return (List<VirusStatistic<Integer>>) getVirusStats(NEW_DEATHS_TREE_IDX);
    }

    public List<VirusStatistic<Integer>> getTotalDeathsStats() {
        return (List<VirusStatistic<Integer>>) getVirusStats(TOTAL_DEATHS_TREE_IDX);
    }

    // --------------------------------------- TREE OPERATIONS --------------------------------------- //

    /**
     * Get a list of virus stats by a tree index (the information of that tree summarized in a {@link VirusStatistic}
     * object).
     *
     * @param treeIdx The index of the tree we want to get a list of virus stats.
     * @return The list of virus stats.
     */
    private List<? extends VirusStatistic<?>> getVirusStats(int treeIdx) {
        AVLVirusStatsTree<?, ?> tree = getVirusTree(treeIdx);
        if (!shouldBeInordered) return tree.preorder();
        return tree.inorder();
    }

    /**
     * Get a certain type of tree by a tree index.
     *
     * @param treeIdx The index of the type of the tree.
     * @return The tree itself.
     * @throws IllegalStateException If no country is currently selected.
     */
    private AVLVirusStatsTree<?, ?> getVirusTree(int treeIdx) {
        if (selectedCountry == null) throw new IllegalStateException("There is not a currently selected country!");
        return records.get(selectedCountry).get(treeIdx);
    }
}
