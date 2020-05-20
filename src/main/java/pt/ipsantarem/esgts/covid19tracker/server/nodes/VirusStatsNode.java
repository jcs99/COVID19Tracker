package pt.ipsantarem.esgts.covid19tracker.server.nodes;

import java.util.Date;

/**
 * A node that can represent a certain statistic about the coronavirus outbreak. This class must be subclassed in order
 * to specify the type of statistic to be represented. The base node contains the common information referent to every
 * virus statistic.
 *
 * @param <T> The type of data that the statistic represents.
 */
public abstract class VirusStatsNode<T> extends VirusNode<Date, T> {

    /**
     * The date of the information present in this node
     */
    private Date date;

    /**
     * The country of the information present in this node
     */
    private String country;


    public VirusStatsNode(Date date, String country) {
        this.date = date;
        this.country = country;
    }

    public VirusStatsNode(Date date, String country, VirusStatsNode<T> left) {
        this.date = date;
        this.country = country;
        this.left = left;
    }

    public VirusStatsNode(Date date, String country, VirusStatsNode<T> left, VirusStatsNode<T> right) {
        this.date = date;
        this.country = country;
        this.left = left;
        this.right = right;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    @Override
    public Date getKey() {
        return getDate();
    }

    @Override
    public void setKey(Date key) {
        setDate(key);
    }

    /**
     * @return The type of virus related stat this node will represent
     */
    public abstract String typeOfVirusStat();

    @Override
    public int hashCode() {
        int result = country != null ? country.hashCode() : 0;
        result = 31 * result + (date != null ? date.hashCode() : 0);
        result = 31 * result + (left != null ? left.hashCode() : 0);
        result = 31 * result + (right != null ? right.hashCode() : 0);
        return result;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        VirusStatsNode<?> that = (VirusStatsNode<?>) o;

        if (country != null ? !country.equals(that.country) : that.country != null) return false;
        if (date != null ? !date.equals(that.date) : that.date != null) return false;
        if (left != null ? !left.equals(that.left) : that.left != null) return false;
        return right != null ? right.equals(that.right) : that.right == null;
    }

    @Override
    public String toString() {
        return "VirusStatsNode{" +
                "country='" + country + '\'' +
                ", date=" + date +
                ", left=" + left +
                ", right=" + right +
                '}';
    }
}
