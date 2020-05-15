package pt.ipsantarem.esgts.covid19tracker.server.models.nodes;

import java.util.Date;

/**
 * A node that represents the new deaths in a day.
 */
public class NewDeathsNode extends VirusStatsNode<Integer> {
    private int newDeaths;

    public NewDeathsNode(Date date, String country, int newDeaths) {
        super(date, country);
        this.newDeaths = newDeaths;
    }

    public NewDeathsNode(Date date, String country, VirusStatsNode<Integer> left, int newDeaths) {
        super(date, country, left);
        this.newDeaths = newDeaths;
    }

    public NewDeathsNode(Date date, String country, VirusStatsNode<Integer> left, VirusStatsNode<Integer> right, int newDeaths) {
        super(date, country, left, right);
        this.newDeaths = newDeaths;
    }

    public int getNewDeaths() {
        return newDeaths;
    }

    public void setNewDeaths(int newDeaths) {
        this.newDeaths = newDeaths;
    }

    @Override
    public String typeOfVirusStat() {
        return "newDeaths";
    }

    @Override
    public Integer getNodeInformation() {
        return newDeaths;
    }
}
