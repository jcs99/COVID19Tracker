package pt.ipsantarem.esgts.covid19tracker.server.interfaces;

import pt.ipsantarem.esgts.covid19tracker.server.models.VirusStatistic;

import java.util.Collection;

/**
 * An interface that describes a virus tree data structure.
 */
public interface Tree<K extends Comparable<K>, E, T extends Node<K, E>> {

    /**
     * @return The root of the tree
     */
    T getRoot();

    /**
     * @return The obtained element by its key
     */
    T get(K key);

    /**
     * Adds an element to the tree.
     */
    void add(T node);

    /**
     * Deletes an element from the tree.
     */
    void delete(K key);

    /**
     * @return A collection of preordered virus stats.
     */
    Collection<VirusStatistic<E>> preorder();

    /**
     * @return A collection of inordered virus stats.
     */
    Collection<VirusStatistic<E>> inorder();

    /**
     * @return A collection of postordered virus stats.
     */
    Collection<VirusStatistic<E>> postorder();
}
