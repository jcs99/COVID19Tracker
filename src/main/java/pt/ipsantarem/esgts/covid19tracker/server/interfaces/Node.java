package pt.ipsantarem.esgts.covid19tracker.server.interfaces;

public abstract class Node<K extends Comparable<K>, V> {
    private int height;

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public abstract K getKey();

    public abstract void setKey(K key);
}
