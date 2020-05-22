package pt.ipsantarem.esgts.covid19tracker.server.trees;

import pt.ipsantarem.esgts.covid19tracker.server.models.VirusStatistic;
import pt.ipsantarem.esgts.covid19tracker.server.nodes.VirusStatsNode;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static pt.ipsantarem.esgts.covid19tracker.server.utils.DateUtils.localDateToString;

/**
 * A self balancing tree implementation of the interface {@link VirusTree}
 * All credits go to eric-martin, code adapted from his AVL tree implementation.
 *
 * @param <E> The stat that the virus statistic node represents
 * @param <T> The virus statistic node itself
 * @see "https://github.com/eugenp/tutorials/blob/master/data-structures/src/main/java/com/baeldung/avltree/AVLTree.java"
 */
@SuppressWarnings("unchecked")
public class AVLVirusStatsTree<E, T extends VirusStatsNode<E>> implements VirusTree<Date, E, T> {
    private T root;

    /**
     * Constructs a new virus stat tree with a root element.
     *
     * @param root The element that will be used to initialize this tree.
     */
    public AVLVirusStatsTree(T root) {
        this.root = root;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public T getRoot() {
        return root;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public VirusStatistic<E> get(Date key) {
        T current = root;

        while (current != null) {
            if (current.getKey().equals(key)) {
                break;
            }

            current = (T) (current.getKey().compareTo(key) < 0 ? current.getRight() : current.getLeft());
        }

        return current != null ? new VirusStatistic<E>(localDateToString(current.getDate()), current.getCountry(),
                current.getNodeInformation()) {
            @Override
            public String statType() {
                return root.typeOfVirusStat();
            }
        } : null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void add(T node) {
        checkNodeInstanceIsSameAsRoot(node.getClass());
        checkNodeCountryIsSameAsRoot(node.getCountry());
        root = insert(root, node);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void delete(Date key) {
        throw new UnsupportedOperationException("Delete operation not supported on AVL virus stats tree!");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<VirusStatistic<E>> preorder() {
        if (root == null) return Collections.emptyList();
        return preorder(root);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<VirusStatistic<E>> inorder() {
        if (root == null) return Collections.emptyList();
        return inorder(root);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<VirusStatistic<E>> postorder() {
        if (root == null) return Collections.emptyList();
        return postorder(root);
    }

    /**
     * Gets all the virus statistics in a certain date interval.
     *
     * @param firstDate  The first date interval.
     * @param secondDate The second date interval.
     * @return A list of virus statistics
     */
    public List<VirusStatistic<E>> getBetweenDates(Date firstDate, Date secondDate) {
        long timeDiff = secondDate.getTime() - firstDate.getTime();
        long diff = TimeUnit.DAYS.convert(timeDiff, TimeUnit.MILLISECONDS);
        Date[] dates = new Date[(int) diff + 1];

        for (int i = 0; i <= diff; i++) {
            dates[i] = new Date(firstDate.getTime() + TimeUnit.DAYS.toMillis(i));
        }

        List<VirusStatistic<E>> recordsByDate = new ArrayList<>();

        for (Date date : dates) {
            VirusStatistic<E> stat = get(date);
            if (stat != null) recordsByDate.add(stat);
        }

        return recordsByDate;
    }

    /**
     * Insert a new node to the current ({@param current}) node. The algorithm specifies that if the key of the current
     * node being analyzed is bigger than the node to be inserted, then it takes the left route, and if it's smaller, then
     * it takes the right route. This process wil repeat until it eventually reaches the end of the tree. At the end of it,
     * since this is a self-balancing tree, it rebalances the node in the tree.
     *
     * @param current The node to add the {@param node} to.
     * @param node    The new node.
     * @return The inserted node.
     * @throws IllegalArgumentException If the key is duplicate.
     */
    private T insert(T current, T node) {
        if (current == null) {
            return node;
        } else if (current.getKey().compareTo(node.getKey()) > 0) {
            current.setLeft(insert((T) current.getLeft(), node));
        } else if (current.getKey().compareTo(node.getKey()) < 0) {
            current.setRight(insert((T) current.getRight(), node));
        } else {
            throw new IllegalArgumentException("Duplicate key!");
        }

        return rebalance(current);
    }

    /**
     * Rebalances the node so that the tree stays balanced (one branch cant be substantially deeper than the other
     * branch).
     *
     * @param node The node to be rebalanced.
     * @return The rebalanced node.
     */
    private T rebalance(T node) {
        // update the height of the node
        updateHeight(node);

        // get the balance of the node
        int balance = getBalance(node);

        // if the balance is bigger than 1
        if (balance > 1) {
            // if the right node of the right node relative to the node being passed is bigger than the left node of the
            // right node relative to the node being passed, then rotate the said node to the left.
            if (height((T) node.getRight().getRight()) > height((T) node.getRight().getLeft())) {
                node = rotateLeft(node);
                // if the opposite condition holds true instead, set the right node of the passed node to its rotated to
                // right variation, and then rotate the passed node to the left.
            } else {
                node.setRight(rotateRight((T) node.getRight()));
                node = rotateLeft(node);
            }
            // if its less than -1 instead, do the opposite of what we are doing in case of the balance being bigger than 1.
        } else if (balance < -1) {
            if (height((T) node.getLeft().getLeft()) > height((T) node.getLeft().getRight())) {
                node = rotateRight(node);
            } else {
                node.setLeft(rotateLeft((T) node.getLeft()));
                node = rotateRight(node);
            }
        }

        return node;
    }

    /**
     * Rotates the node to the right.
     *
     * @param node The node to be rotated to the right.
     * @return The rotated node.
     */
    private T rotateRight(T node) {
        // get the left and right nodes of the passed node
        T left = (T) node.getLeft();
        T right = (T) node.getRight();

        // set the left node right node to the passed node
        left.setRight(node);

        // set the passed node left node to the previous right node.
        node.setLeft(right);

        // update the heights.
        updateHeight(node);
        updateHeight(left);

        // return the node
        return left;
    }

    /**
     * Rotates the node to the left.
     *
     * @param node The node to be rotated to the left side.
     * @return The rotated node.
     */
    private T rotateLeft(T node) {
        // get the left and right nodes of the passed node.
        T right = (T) node.getRight();
        T left = (T) right.getLeft();

        // set the right node left node to the passed node.
        right.setLeft(node);

        // set the passed node right node to the previous left node.
        node.setRight(left);

        // update the heights.
        updateHeight(node);
        updateHeight(right);

        // return the node.
        return right;
    }

    /**
     * Sets the height of the {@param node} to the highest height in the subset of the left and right nodes.
     *
     * @param node The node to update the height of.
     */
    private void updateHeight(T node) {
        node.setHeight(1 + Math.max(height((T) node.getLeft()), height((T) node.getRight())));
    }

    /**
     * Gets the height of a {@param node}.
     *
     * @param node The node to get the height for.
     * @return the height of the node.
     */
    private int height(T node) {
        return node == null ? -1 : node.getHeight();
    }

    /**
     * Gets the balance of a {@param node} by subtracting the height of its left and right nodes.
     *
     * @param node The node to get balance for
     * @return The balance of the node
     */
    private int getBalance(T node) {
        return (node == null) ? 0 : height((T) node.getRight()) - height((T) node.getLeft());
    }

    private List<VirusStatistic<E>> preorder(T node) {
        if (node == null) return Collections.emptyList();

        List<VirusStatistic<E>> preorder = new ArrayList<>();

        preorder.add(new VirusStatistic<E>(localDateToString(node.getDate()), node.getCountry(), node.getNodeInformation()) {
            @Override
            public String statType() {
                return root.typeOfVirusStat();
            }
        });
        preorder.addAll(preorder((T) node.getLeft()));
        preorder.addAll(preorder((T) node.getRight()));

        return preorder;
    }

    private List<VirusStatistic<E>> inorder(T node) {
        if (node == null) return Collections.emptyList();

        List<VirusStatistic<E>> inorder = new ArrayList<>(inorder((T) node.getLeft()));
        inorder.add(new VirusStatistic<E>(localDateToString(node.getDate()), node.getCountry(), node.getNodeInformation()) {
            @Override
            public String statType() {
                return root.typeOfVirusStat();
            }
        });
        inorder.addAll(inorder((T) node.getRight()));

        return inorder;
    }

    private List<VirusStatistic<E>> postorder(T node) {
        if (node == null) return Collections.emptyList();

        List<VirusStatistic<E>> postorder = new ArrayList<>();

        postorder.addAll(postorder((T) node.getLeft()));
        postorder.addAll(postorder((T) node.getRight()));
        postorder.add(new VirusStatistic<E>(localDateToString(node.getDate()), node.getCountry(), node.getNodeInformation()) {
            @Override
            public String statType() {
                return root.typeOfVirusStat();
            }
        });

        return postorder;
    }

    // ------------------------------------------ PRECONDITIONS ------------------------------------------ //

    private void checkNodeInstanceIsSameAsRoot(Class<?> nodeClazz) {
        if (!root.getClass().isAssignableFrom(nodeClazz)) {
            throw new IllegalArgumentException("The node being inserted is from the " + nodeClazz.getSimpleName()
                    + " type, while the root node is from the type " + root.getClass().getSimpleName() + "! All the nodes"
                    + " in this tree must share the same instance type.");
        }
    }

    private void checkNodeCountryIsSameAsRoot(String nodeCountry) {
        if (!root.getCountry().equals(nodeCountry)) {
            throw new IllegalArgumentException("The node being inserted is from the " + nodeCountry
                    + " country, while the root node is from the country " + root.getCountry() + "! All the nodes"
                    + " in this tree must share the same country.");
        }
    }

    // ------------------------------------------ PRECONDITIONS ------------------------------------------ //
}
