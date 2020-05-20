package pt.ipsantarem.esgts.covid19tracker.server.nodes;

import java.util.Date;

/**
 * A node that represents the total cases in each day.
 */
public class TotalCasesNode extends VirusStatsNode<Integer> {
    private int totalCases;

    public TotalCasesNode(Date date, String country, int totalCases) {
        super(date, country);
        this.totalCases = totalCases;
    }

    public TotalCasesNode(Date date, String country, VirusStatsNode<Integer> left, int totalCases) {
        super(date, country, left);
        this.totalCases = totalCases;
    }

    public TotalCasesNode(Date date, String country, VirusStatsNode<Integer> left, VirusStatsNode<Integer> right, int totalCases) {
        super(date, country, left, right);
        this.totalCases = totalCases;
    }

    public int getTotalCases() {
        return totalCases;
    }

    public void setTotalCases(int totalCases) {
        this.totalCases = totalCases;
    }

    @Override
    public String typeOfVirusStat() {
        return "totalCases";
    }

    @Override
    public Integer getNodeInformation() {
        return totalCases;
    }
}
