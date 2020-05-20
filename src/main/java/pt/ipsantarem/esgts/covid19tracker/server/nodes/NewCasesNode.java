package pt.ipsantarem.esgts.covid19tracker.server.nodes;

import java.util.Date;

/**
 * A node that represents the new cases in a day.
 */
public class NewCasesNode extends VirusStatsNode<Integer> {
    private int newCases;

    public NewCasesNode(Date date, String country, int newCases) {
        super(date, country);
        this.newCases = newCases;
    }

    public NewCasesNode(Date date, String country, VirusStatsNode<Integer> left, int newCases) {
        super(date, country, left);
        this.newCases = newCases;
    }

    public NewCasesNode(Date date, String country, VirusStatsNode<Integer> left, VirusStatsNode<Integer> right, int newCases) {
        super(date, country, left, right);
        this.newCases = newCases;
    }

    public int getNewCases() {
        return newCases;
    }

    public void setNewCases(int newCases) {
        this.newCases = newCases;
    }

    @Override
    public String typeOfVirusStat() {
        return "newCases";
    }

    @Override
    public Integer getNodeInformation() {
        return newCases;
    }
}
