package pt.ipsantarem.esgts.covid19tracker.server.models;

import pt.ipsantarem.esgts.covid19tracker.server.models.abstracts.VirusStatsNode;

import java.util.Date;

/**
 * A node that represents the total deaths in each day.
 */
public class TotalDeathsNode extends VirusStatsNode<Integer> {
    private int totalDeaths;

    public TotalDeathsNode(Date date, String country, int totalDeaths) {
        super(date, country);
        this.totalDeaths = totalDeaths;
    }

    public TotalDeathsNode(Date date, String country, VirusStatsNode<Integer> left, int totalDeaths) {
        super(date, country, left);
        this.totalDeaths = totalDeaths;
    }

    public TotalDeathsNode(Date date, String country, VirusStatsNode<Integer> left, VirusStatsNode<Integer> right, int totalDeaths) {
        super(date, country, left, right);
        this.totalDeaths = totalDeaths;
    }

    public int getTotalDeaths() {
        return totalDeaths;
    }

    public void setTotalDeaths(int totalDeaths) {
        this.totalDeaths = totalDeaths;
    }

    @Override
    public String typeOfVirusStat() {
        return "totalDeaths";
    }

    @Override
    public Integer getVirusStat() {
        return totalDeaths;
    }
}
