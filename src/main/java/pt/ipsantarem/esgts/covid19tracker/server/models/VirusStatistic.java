package pt.ipsantarem.esgts.covid19tracker.server.models;

import java.util.Date;

public abstract class VirusStatistic<T> {
    private final Date date;
    private final String country;
    private final T stat;

    public VirusStatistic(Date date, String country, T stat) {
        this.date = date;
        this.country = country;
        this.stat = stat;
    }

    public Date getDate() {
        return date;
    }

    public String getCountry() {
        return country;
    }

    public T getStat() {
        return stat;
    }

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
