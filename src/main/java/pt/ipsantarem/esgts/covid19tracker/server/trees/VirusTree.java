package pt.ipsantarem.esgts.covid19tracker.server.trees;

import pt.ipsantarem.esgts.covid19tracker.server.models.VirusStatistic;
import pt.ipsantarem.esgts.covid19tracker.server.nodes.VirusNode;

import java.io.Serializable;
import java.util.Collection;

/**
 * An interface that describes a virus tree data structure.
 */
public interface VirusTree<K extends Comparable<K>, E, T extends VirusNode<K, E>> extends Serializable {

    /**
     * @return The root of the tree
     */
    T getRoot();

    /**
     * @return The obtained element by its key.
     */
    VirusStatistic<E> get(K key);

    /**
     * Adds a node to the tree.
     */
    void add(T node);

    /**
     * Deletes an element from the tree. This method is not supported in the {@link AVLVirusStatsTree} class since
     * the stats are final unless the external source changes (and in that case, it's not our responsibility).
     */
    void delete(K key);

    /**
     * Preorder traversal of the tree.
     * If the tree has the following structure:
     * 1
     * /\
     * 2 3
     * /\
     * 4 5
     * Then the returned preordered elements will be: 1-2-4-5-3
     *
     * @return A list of the virus stats after the preorder operation.
     */
    Collection<VirusStatistic<E>> preorder();

    /**
     * Inorder traversal of the tree.
     * If the tree has the following structure:
     * 1
     * /\
     * 2 3
     * /\
     * 4 5
     * Then the returned inordered elements will be: 4-2-5-1-3
     * The inorder algorithm will be of particular importance in the {@link AVLVirusStatsTree}
     * class since it will allow us to get a list of virus stats ordered since the date of the first cases.
     * @return A list of the virus stats after the inorder operation.
     */
    Collection<VirusStatistic<E>> inorder();

    /**
     * Postorder traversal of the tree.
     * If the tree has the following structure:
     * 1
     * /\
     * 2 3
     * /\
     * 4 5
     * Then the returned postordered elements will be: 4-5-2-3-1
     * @return A list of the virus stats after the postorder operation.
     */
    Collection<VirusStatistic<E>> postorder();
}
