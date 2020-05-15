package pt.ipsantarem.esgts.covid19tracker.server.models.nodes;

import java.util.Date;

/**
 * A node that represents the recovered cases in each day.
 */
public class RecoveredCasesNode extends VirusStatsNode<Integer> {
    private int recoveredCases;

    public RecoveredCasesNode(Date date, String country, int recoveredCases) {
        super(date, country);
        this.recoveredCases = recoveredCases;
    }

    public RecoveredCasesNode(Date date, String country, VirusStatsNode<Integer> left, int recoveredCases) {
        super(date, country, left);
        this.recoveredCases = recoveredCases;
    }

    public RecoveredCasesNode(Date date, String country, VirusStatsNode<Integer> left, VirusStatsNode<Integer> right, int recoveredCases) {
        super(date, country, left, right);
        this.recoveredCases = recoveredCases;
    }

    public int getRecoveredCases() {
        return recoveredCases;
    }

    public void setRecoveredCases(int recoveredCases) {
        this.recoveredCases = recoveredCases;
    }

    @Override
    public String typeOfVirusStat() {
        return "recoveredCases";
    }

    @Override
    public Integer getNodeInformation() {
        return recoveredCases;
    }
}
