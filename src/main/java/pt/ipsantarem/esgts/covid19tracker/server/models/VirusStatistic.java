package pt.ipsantarem.esgts.covid19tracker.server.models;

import java.io.Serializable;

/**
 * Summarized information about a certain virus statistic. Primary purpose, for now, is to be sent over to
 * clients/consumers over a JSON format.
 *
 * @param <T> The type of statistic this class represents.
 */
public abstract class VirusStatistic<T> implements Serializable {
    private final String date;
    private final String country;
    private final T stat;

    public VirusStatistic(String date, String country, T stat) {
        this.date = date;
        this.country = country;
        this.stat = stat;
    }

    public String getDate() {
        return date;
    }

    public String getCountry() {
        return country;
    }

    public T getStat() {
        return stat;
    }

    /**
     * @return The type of stat we are representing.
     */
    public abstract String statType();

    @Override
    public String toString() {
        return "VirusStatistic{" +
                "date=" + date +
                ", country='" + country + '\'' +
                ", " + statType() + "=" + stat +
                '}';
    }
}
