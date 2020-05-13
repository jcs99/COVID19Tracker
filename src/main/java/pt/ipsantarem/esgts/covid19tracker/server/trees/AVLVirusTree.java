package pt.ipsantarem.esgts.covid19tracker.server.trees;

import pt.ipsantarem.esgts.covid19tracker.server.interfaces.Tree;
import pt.ipsantarem.esgts.covid19tracker.server.models.VirusStatistic;
import pt.ipsantarem.esgts.covid19tracker.server.models.abstracts.VirusStatsNode;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

/**
 * A self balancing tree implementation of the interface {@link Tree}
 * All credits go to eric-martin, code adapted from his AVL tree implementation.
 *
 * @param <E> The stat that the virus statistic node represents
 * @param <T> The virus statistic node itself
 * @see "https://github.com/eugenp/tutorials/blob/master/data-structures/src/main/java/com/baeldung/avltree/AVLTree.java"
 */
@SuppressWarnings("unchecked")
public class AVLVirusTree<E, T extends VirusStatsNode<E>> implements Tree<Date, E, T> {
    private T root;

    public AVLVirusTree(T root) {
        this.root = root;
    }

    @Override
    public T getRoot() {
        return root;
    }

    @Override
    public T get(Date key) {
        T current = root;

        while (current != null) {
            if (current.getKey().equals(key)) {
                break;
            }

            current = (T) (current.getKey().compareTo(key) < 0 ? current.getRight() : current.getLeft());
        }

        return current;
    }

    @Override
    public void add(T node) {
        checkNodeInstanceIsSameAsRoot(node.getClass());
        checkNodeCountryIsSameAsRoot(node.getCountry());
        root = insert(root, node);
    }

    @Override
    public void delete(Date key) {
        root = delete(root, key);
    }

    @Override
    public List<VirusStatistic<E>> preorder() {
        if (root == null) return Collections.emptyList();
        return preorder(root);
    }

    @Override
    public List<VirusStatistic<E>> inorder() {
        if (root == null) return Collections.emptyList();
        return inorder(root);
    }

    @Override
    public List<VirusStatistic<E>> postorder() {
        if (root == null) return Collections.emptyList();
        return postorder(root);
    }

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

    private T delete(T current, Date key) {
        if (key == null) {
            return null;
        } else if (current.getKey().compareTo(key) > 0) {
            current.setLeft(delete((T) current.getLeft(), key));
        } else if (current.getKey().compareTo(key) < 0) {
            current.setRight(delete((T) current.getRight(), key));
        } else {
            if (current.getLeft() == null || current.getRight() == null) {
                current = (T) ((current.getLeft() == null) ? current.getRight() : current.getLeft());
            } else {
                T mostLeftChild = mostLeftChild((T) current.getRight());
                current.setKey(mostLeftChild.getKey());
                current.setRight(delete((T) current.getRight(), current.getKey()));
            }
        }

        if (current != null) {
            current = rebalance(current);
        }

        return current;
    }

    private T mostLeftChild(T node) {
        T current = node;

        while (current.getRight() != null) {
            current = (T) current.getLeft();
        }

        return current;
    }

    private T rebalance(T z) {
        updateHeight(z);
        int balance = getBalance(z);

        if (balance > 1) {
            if (height((T) z.getRight().getRight()) > height((T) z.getRight().getLeft())) {
                z = rotateLeft(z);
            } else {
                z.setRight(rotateRight((T) z.getRight()));
                z = rotateLeft(z);
            }
        } else if (balance < -1) {
            if (height((T) z.getLeft().getLeft()) > height((T) z.getLeft().getRight())) {
                z = rotateRight(z);
            } else {
                z.setLeft(rotateLeft((T) z.getLeft()));
                z = rotateRight(z);
            }
        }

        return z;
    }

    private T rotateRight(T y) {
        T x = (T) y.getLeft();
        T z = (T) y.getRight();
        x.setRight(y);
        y.setLeft(z);
        updateHeight(y);
        updateHeight(x);
        return x;
    }

    private T rotateLeft(T y) {
        T x = (T) y.getRight();
        T z = (T) x.getLeft();
        x.setLeft(y);
        y.setRight(z);
        updateHeight(y);
        updateHeight(x);
        return x;
    }

    private void updateHeight(T n) {
        n.setHeight(1 + Math.max(height((T) n.getLeft()), height((T) n.getRight())));
    }

    private int height(T n) {
        return n == null ? -1 : n.getHeight();
    }

    private int getBalance(T n) {
        return (n == null) ? 0 : height((T) n.getRight()) - height((T) n.getLeft());
    }

    private List<VirusStatistic<E>> preorder(T node) {
        if (node == null) return Collections.emptyList();

        List<VirusStatistic<E>> preorder = new ArrayList<>();

        preorder.add(new VirusStatistic<E>(node.getDate(), node.getCountry(), node.getVirusStat()) {
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
        inorder.add(new VirusStatistic<E>(node.getDate(), node.getCountry(), node.getVirusStat()) {
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
        postorder.add(new VirusStatistic<E>(node.getDate(), node.getCountry(), node.getVirusStat()) {
            @Override
            public String statType() {
                return root.typeOfVirusStat();
            }
        });

        return postorder;
    }

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
}
