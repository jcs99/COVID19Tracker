package pt.ipsantarem.esgts.covid19tracker.server.parsers;

public interface CSVParser extends Parser {

    /**
     * @return The delimiter used by this CSV. Generally, it's a comma.
     */
    String getDelimiter();

    // ---------------------- INDEXES OF THE STATS SPLITTED BY THE DELIMITER ---------------------- //

    int getLocationIndex();

    int getDateIndex();

    int getNewCasesIndex();

    int getTotalCasesIndex();

    int getNewDeathsIndex();

    int getTotalDeathsIndex();

    // ---------------------- INDEXES OF THE STATS SPLITTED BY THE DELIMITER ---------------------- /
}
