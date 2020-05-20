package pt.ipsantarem.esgts.covid19tracker.server.nodes;

import java.io.Serializable;

/**
 * The base node for all the nodes that contain COVID-19 related information
 *
 * @param <K> The type of the key
 * @param <V> The type of the information that this node has
 */
public abstract class VirusNode<K extends Comparable<K>, V> implements Serializable {

    /**
     * The height of this node relative to its parent node.
     */
    protected int height;

    /**
     * The left node
     */
    protected VirusNode<K, V> left;

    /**
     * The right node
     */
    protected VirusNode<K, V> right;

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public VirusNode<K, V> getLeft() {
        return left;
    }

    public void setLeft(VirusNode<K, V> left) {
        this.left = left;
    }

    public VirusNode<K, V> getRight() {
        return right;
    }

    public void setRight(VirusNode<K, V> right) {
        this.right = right;
    }

    /**
     * @return The key of this node (should be something that implements the {@link java.lang.Comparable} interface
     * and be unique, as well)
     */
    public abstract K getKey();

    /**
     * Reassign the key of this node
     *
     * @param key The new key
     */
    public abstract void setKey(K key);

    /**
     * @return The information that this node represents
     */
    public abstract V getNodeInformation();
}
