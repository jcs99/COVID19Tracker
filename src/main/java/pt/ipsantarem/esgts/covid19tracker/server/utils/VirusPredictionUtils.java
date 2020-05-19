package pt.ipsantarem.esgts.covid19tracker.server.utils;

import org.apache.commons.math3.stat.regression.SimpleRegression;
import pt.ipsantarem.esgts.covid19tracker.server.models.VirusStatistic;

import java.util.List;

/**
 * A utility class that can be used for predicting various indicators about the virus.
 */
public class VirusPredictionUtils {

    /**
     * Make a new prediction for total cases of a certain stat for the next day,
     * based on the trend of growth of the latest 15 days.
     *
     * @param stats A list of virus statistic records. These can be, for example, a list of total cases or a
     *              list of total deaths.
     * @return The prediction of total stat cases for the next day.
     */
    public static int totalStatCasesPredict(List<VirusStatistic<Integer>> stats) {
        SimpleRegression regression = new SimpleRegression();
        List<VirusStatistic<Integer>> stats15days = stats.subList(stats.size() - 15, stats.size());

        int day = 1;
        for (VirusStatistic<Integer> stat : stats15days) {
            double y;

            if (stat.getStat() == 0) {
                y = 0.0;
            } else {
                y = Math.log10(stat.getStat());
            }

            regression.addData(day, y);
            day++;
        }

        return stats15days.get(stats15days.size() - 1).getStat() +
                ((int) Math.pow(10, regression.predict(day)) - (int) Math.pow(10, regression.predict(day - 1)));
    }

    /**
     * Make a new prediction for the new cases of a certain stat for the next day,
     * based on the trend of growth of the latest 15 days.
     *
     * @param stats A list of virus statistic records. These can be, for example, a list of new cases or a
     *              list of new total deaths.
     * @return The prediction of new stat cases for the next day.
     */
    public static int newStatCasesPredict(List<VirusStatistic<Integer>> stats) {
        return totalStatCasesPredict(stats) - stats.get(stats.size() - 1).getStat();
    }
}
